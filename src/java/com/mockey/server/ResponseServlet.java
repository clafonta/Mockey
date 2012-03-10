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
import java.nio.charset.Charset;

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
import com.mockey.plugin.PluginStore;
import com.mockey.plugin.RequestInspectionResult;
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
	 * determines the appropriate mock service for the definition of the
	 * response. If the mock service type includes a request inspector, then it
	 * will be processed.
	 * 
	 * @see com.mockey.model.Service#getRequestInspectorName()
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

		// ************************************
		// BEGIN - REQUEST INSPECTORS
		// Check for Global
		PluginStore pluginStore = PluginStore.getInstance();
		RequestInspectionResult inspectionMessage = pluginStore.processRequestInspectors(service,
				originalHttpReqFromClient);

		// END INSPECTORS
		// ************************************
		Url urlToExecute = service.getDefaultRealUrl();

		service.setHttpMethod(originalHttpReqFromClient.getMethod());

		ResponseFromService response = service.execute(request, urlToExecute);
		logRequestAsFulfilled(service, request, response, originalHttpReqFromClient.getRemoteAddr(), inspectionMessage);

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
		final String charSet = getServiceCharSet(service);

		resp.setCharacterEncoding(charSet); // "UTF-8");
		resp.setContentType(service.getHttpContentType());
		if (!(service.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_PROXY)) {
			resp.setContentType(service.getHttpContentType());
			byte[] myCharSetBytes = response.getBody().getBytes(charSet);
			new PrintStream(resp.getOutputStream()).write(myCharSetBytes);
			resp.getOutputStream().flush();
		} else {
			if (response.getStatusLine() != null) {
				resp.setStatus(response.getStatusLine().getStatusCode());
			}
			response.writeToOutput(resp);
		}
	}

	/**
	 * Determines the proper CharSet for the given service and returns it. If
	 * there is an issue or no charset is specified for the service, then the
	 * default charset ISO-8859-1 is returned.
	 * 
	 * @param service
	 * @return the charset for this service.
	 */
	private String getServiceCharSet(Service service) {
		String charSet = HTTP.ISO_8859_1;

		try {
			// check content type for charset and try to use that if its
			// there...
			// example: "application/json;charset=utf-8" should use the charset
			// "utf-8"
			if (service.getHttpContentType() != null) {
				final String contentTypeLower = service.getHttpContentType().toLowerCase();
				int charsetIndex = contentTypeLower.indexOf("charset=");

				if (charsetIndex >= 0) {
					charSet = contentTypeLower.substring(charsetIndex + "charset=".length());

					// content-type can have multiple attributes so we make sure
					// that if there
					// are multiple we trim off any that follow the charset
					int trailingSemiColonIdx = charSet.indexOf(';');
					if (trailingSemiColonIdx > 0) {
						charSet = charSet.substring(0, trailingSemiColonIdx);
					}

					// kill trailing white space if any
					charSet = charSet.trim();

					// make sure it is a valid charset before returning it (note
					// charSet
					// names are case insensitive so using the lowercase version
					// is OK)
					Charset.forName(charSet);
				}
			}
		} catch (Exception e) {
			// malformed content types or unsupported charsets will end up here
			// not much we can do other than default to the regular charset
			charSet = HTTP.ISO_8859_1;
			logger.info("Unable to use charset for service \"" + service.getServiceName() + "\" from content-type \""
					+ service.getHttpContentType() + "\". Defaulting to ISO-8859-1.", e);
		}

		return charSet;
	}

	private void logRequestAsFulfilled(Service service, RequestFromClient request, ResponseFromService response,
			String ip, RequestInspectionResult inspectionResult) throws UnsupportedEncodingException {
		FulfilledClientRequest fcr = new FulfilledClientRequest();
		fcr.setRawRequest((response.getRequestUrl() != null) ? response.getRequestUrl().toString() : "");
		fcr.setRequestorIP(ip);
		fcr.setServiceId(service.getId());
		fcr.setServiceName(service.getServiceName());
		fcr.setClientRequestBody(request.getBodyInfo());
		fcr.setClientRequestHeaders(request.getHeaderInfo());
		fcr.setClientRequestParameters(request.getParameterInfo());
		fcr.setResponseMessage(response);
		fcr.setClientRequestCookies(request.getCookieInfoAsString());// response.getRequestCookies());
		fcr.setClientResponseCookies(response.getResponseCookiesAsString());

		fcr.setServiceResponseType(service.getServiceResponseType());
		if (response.getOriginalRequestUrlBeforeTwisting() != null) {
			fcr.setOriginalUrlBeforeTwisting(response.getOriginalRequestUrlBeforeTwisting().toString());
		}
		fcr.setRequestInspectionResult(inspectionResult);
		store.saveOrUpdateFulfilledClientRequest(fcr);
	}
}
