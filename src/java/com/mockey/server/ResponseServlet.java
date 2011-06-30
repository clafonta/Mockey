/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2010  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
 * neil.cronin (neil AT rackle DOT com) 
 * lorin.kobashigawa (lkb AT kgawa DOT com)
 * rob.meyer (rob AT bigdis DOT com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.mockey.server;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.RequestFromClient;
import com.mockey.model.ResponseFromService;
import com.mockey.model.Service;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Responsible for serving mock responses. Based on configuration, returns
 * desired content either from a source (Mockey being used as a proxy) or from a
 * defined scenario.
 * 
 * @author chad.lafontaine
 * 
 */
public class ResponseServlet extends HttpServlet {

	private static final long serialVersionUID = 8401356766354139506L;
	private IMockeyStorage store = StorageRegistry.MockeyStorage;
	private Logger logger = Logger.getLogger(ResponseServlet.class);

	/**
	 * Parses the caller's remote address, parses the URL, (the URI) then
	 * determines the appropriate mockservice for the definition of the response
	 * type.
	 */
	@SuppressWarnings("static-access")
	public void service(HttpServletRequest originalHttpReqFromClient, HttpServletResponse resp)
			throws ServletException, IOException {

		RequestFromClient request = new RequestFromClient(originalHttpReqFromClient);

		logger.info(request.getHeaderInfo());
		logger.info(request.getParameterInfo());
		logger.info(request.getCookieInfoAsString());

		String originalHttpReqURI = originalHttpReqFromClient.getRequestURI();

		String contextRoot = originalHttpReqFromClient.getContextPath();
		if (originalHttpReqURI.startsWith(contextRoot)) {
			originalHttpReqURI = originalHttpReqURI.substring(contextRoot.length(), originalHttpReqURI.length());
		}

		Url serviceUrl = new Url(originalHttpReqURI);
		Service service = store.getServiceByUrl(serviceUrl.getFullUrl());
		Url urlToExecute = service.getDefaultRealUrl();

		service.setHttpMethod(originalHttpReqFromClient.getMethod());

		ResponseFromService response = service.execute(request, urlToExecute);
		logRequestAsFulfilled(service, request, response, originalHttpReqFromClient.getRemoteAddr());

		try {
			// Wait for a X hang time seconds.
			logger.debug("Waiting..." + service.getHangTime() + " miliseconds ");
			Thread.currentThread().sleep(service.getHangTime());
			logger.debug("Done Waiting");
		} catch (Exception e) {
			// Catch interrupt exception.
			// Or not.
		}

		// TODO:
		// return all headers and cookies to allow setup of
		// services and/or scenarios copied/created from History.
		resp.setCharacterEncoding(HTTP.ISO_8859_1); // "UTF-8");
		resp.setContentType(service.getHttpContentType());
		if (!(service.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_PROXY)) {
			resp.setContentType(service.getHttpContentType());
			byte[] myISO88591asBytes = response.getBody().getBytes("ISO-8859-1");
			new PrintStream(resp.getOutputStream()).write(myISO88591asBytes);
			resp.getOutputStream().flush();
		} else {
			resp.setStatus(response.getStatusLine().getStatusCode());
			response.writeToOutput(resp);
		}
	}

	private void logRequestAsFulfilled(Service service, RequestFromClient request, ResponseFromService response,
			String ip) throws UnsupportedEncodingException {
		FulfilledClientRequest fulfilledClientRequest = new FulfilledClientRequest();
		fulfilledClientRequest.setRawRequest((response.getRequestUrl() != null) ? response.getRequestUrl().toString()
				: "");
		fulfilledClientRequest.setRequestorIP(ip);
		fulfilledClientRequest.setServiceId(service.getId());
		fulfilledClientRequest.setServiceName(service.getServiceName());
		fulfilledClientRequest.setClientRequestBody(request.getBodyInfo());
		fulfilledClientRequest.setClientRequestHeaders(request.getHeaderInfo());
		fulfilledClientRequest.setClientRequestParameters(request.getParameterInfo());
		fulfilledClientRequest.setResponseMessage(response);
		fulfilledClientRequest.setClientRequestCookies(request.getCookieInfoAsString());// response.getRequestCookies());
		fulfilledClientRequest.setClientResponseCookies(response.getResponseCookiesAsString());

		fulfilledClientRequest.setServiceResponseType(service.getServiceResponseType());
		if (response.getOriginalRequestUrlBeforeTwisting() != null) {
			fulfilledClientRequest.setOriginalUrlBeforeTwisting(response.getOriginalRequestUrlBeforeTwisting()
					.toString());
		}
		store.saveOrUpdateFulfilledClientRequest(fulfilledClientRequest);
	}
}
