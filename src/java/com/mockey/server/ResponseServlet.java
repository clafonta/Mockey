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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.RequestFromClient;
import com.mockey.model.ResponseFromService;
import com.mockey.model.Service;
import com.mockey.model.TwistInfo;
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

		String originalHttpReqURI = originalHttpReqFromClient.getRequestURI();
		String targetHttpReqURI = null;
		String contextRoot = originalHttpReqFromClient.getContextPath();
		if (originalHttpReqURI.startsWith(contextRoot)) {
			originalHttpReqURI = originalHttpReqURI.substring(contextRoot.length(), originalHttpReqURI.length());
		}

		// ************************************************************************
		// STEP #1) Is URL TWISTING ON?
		// ************************************************************************
		targetHttpReqURI = originalHttpReqURI;
		if (store.getUniversalTwistInfoId() != null) {
			TwistInfo twistInfo = store.getTwistInfoById(store.getUniversalTwistInfoId());
			if (twistInfo != null) {
				logger.debug("URL twisting is enabled.");
				targetHttpReqURI = twistInfo.getTwistedValue(originalHttpReqURI);
			}
		}

		Url serviceUrl = new Url(targetHttpReqURI);
		Service service = store.getServiceByUrl(serviceUrl.getFullUrl());
		// ************************************************************************
		// STEP #2) Process your original request.
		// ************************************************************************
		RequestFromClient request = new RequestFromClient(originalHttpReqFromClient);

		// ************************************************************************
		// STEP #3) JAVA and JSON implemented Inspectors
		// ************************************************************************

		PluginStore pluginStore = PluginStore.getInstance();
		RequestInspectionResult inspectionMessage = pluginStore.processRequestInspectors(service, request);

		// ************************************************************************
		// STEP #4) Get the Response (static,dynamic, or proxy).
		// ************************************************************************
		service.setHttpMethod(originalHttpReqFromClient.getMethod());
		ResponseFromService response = service.execute(request, serviceUrl);

		// ************************************************************************
		// STEP #5) If twisting was enabled, let's be sure to set the original
		// URL
		// ************************************************************************
		if (!originalHttpReqURI.equalsIgnoreCase(targetHttpReqURI)) {
			response.setOriginalRequestUrlBeforeTwisting(new Url(originalHttpReqURI));
		}
		logRequestAsFulfilled(service, request, response, originalHttpReqFromClient.getRemoteAddr(), inspectionMessage);

		try {
			int hangTime = service.getHangTime();
			if(response.getServiceScenarioHangTime() >0){
				//Override the Service's value for sleep
				hangTime = response.getServiceScenarioHangTime();
			}
			// Wait for a X hang time seconds.
			logger.debug("Waiting..." + hangTime + " miliseconds ");
			Thread.currentThread().sleep(hangTime);
			logger.debug("Done Waiting");
		} catch (Exception e) {
			// Catch interrupt exception.
			// Or not.
		}

		if (!(service.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_PROXY)) {
			if (response.getHeaders() != null) {
				for (Header h : response.getHeaders()) {
					resp.setHeader(h.getName(), h.getValue());
					if (h.getValue() != null) {
						int indexOfUTF = h.getValue().toLowerCase().indexOf("utf-8");
						if (indexOfUTF >= 0) {

							// Why do we do this?
							// * We don't want to setup utf-8 for all things,
							// hence we look for the User's scenario with
							// explicit setting for header information, ex:
							// 'Content-Type: text/json; charset=utf-8'
							//
							// * Setting headers should be sufficient, but
							// apparently not as seen debugging with Jetty. I
							// did not have this issue w/ Tomcat.
							resp.setCharacterEncoding(HTTP.UTF_8);
						}
					}

				}

			}

			try {
				resp.setStatus(response.getHttpResponseStatusCode());
			} catch (java.lang.IllegalArgumentException iae) {
				logger.debug("Unable to set the response status to '" + response.getHttpResponseStatusCode() + "'",
						iae);
			}

			PrintWriter out = resp.getWriter();
			out.println(response.getBody());
			out.flush();
			out.close();
		} else {
			// RULE_FOR_HEADERS
			try {
				resp.setStatus(response.getHttpResponseStatusCode());
			} catch (java.lang.IllegalArgumentException iae) {
				logger.debug("Unable to set the response status to '" + response.getHttpResponseStatusCode() + "'",
						iae);
			}
			response.writeToOutput(resp);
		}
	}

	private void logRequestAsFulfilled(Service service, RequestFromClient request, ResponseFromService response,
			String ip, RequestInspectionResult inspectionResult) throws UnsupportedEncodingException {
		FulfilledClientRequest fcr = new FulfilledClientRequest();
		//fcr.setRawRequest((response.getRequestUrl() != null) ? response.getRequestUrl().getFullUrl() : "");
		fcr.setRawRequest(request.getRequestURL());
		fcr.setRequestorIP(ip);
		fcr.setServiceId(service.getId());
		fcr.setServiceName(service.getServiceName());
		fcr.setClientRequestBody(request.getBodyInfo());
		fcr.setClientRequestHeaders(request.getHeaderInfo());
		fcr.setClientRequestParameters(request.getParameterInfo());
		fcr.setResponseMessage(response);
		fcr.setScenarioName(response.getScenarioName());
		fcr.setClientRequestCookies(request.getCookieInfoAsString());// response.getRequestCookies());
		fcr.setClientResponseCookies(response.getResponseCookiesAsString());

		fcr.setServiceResponseType(service.getServiceResponseType());
		if (response.getOriginalRequestUrlBeforeTwisting() != null) {
			fcr.setOriginalUrlBeforeTwisting(response.getOriginalRequestUrlBeforeTwisting().toString());
		}
		fcr.setRequestInspectionResult(inspectionResult);
		fcr.setServiceTagsAsString(service.getTag());
		fcr.setScenarioTagsAsString(response.getScenarioTagsAsString());
		store.saveOrUpdateFulfilledClientRequest(fcr);
	}
}
