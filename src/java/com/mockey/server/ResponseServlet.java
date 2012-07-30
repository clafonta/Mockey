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
import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
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
import com.mockey.storage.file.FileSystemManager;

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
	public void service(HttpServletRequest originalHttpReqFromClient,
			HttpServletResponse resp) throws ServletException, IOException {

		String originalHttpReqURI = originalHttpReqFromClient.getRequestURI();

		String contextRoot = originalHttpReqFromClient.getContextPath();
		if (originalHttpReqURI.startsWith(contextRoot)) {
			originalHttpReqURI = originalHttpReqURI.substring(
					contextRoot.length(), originalHttpReqURI.length());
		}

		Url serviceUrl = new Url(originalHttpReqURI);
		Service service = store.getServiceByUrl(serviceUrl.getFullUrl());

		// ************************************************************************
		// STEP #1) Inspectors must be done BEFORE you process the original
		// ************************************************************************
		// request, otherwise, POST body data will be lost if being retrieved
		// via 'getParameter'.

		// BEGIN - REQUEST INSPECTORS
		// Check for Global
		PluginStore pluginStore = PluginStore.getInstance();
		RequestInspectionResult inspectionMessage = pluginStore
				.processRequestInspectors(service, originalHttpReqFromClient);

		// END INSPECTORS
		// ************************************************************************
		// STEP #2) Process your original request.
		// ************************************************************************
		RequestFromClient request = new RequestFromClient(
				originalHttpReqFromClient);
		Url urlToExecute = service.getDefaultRealUrl();
		service.setHttpMethod(originalHttpReqFromClient.getMethod());
		ResponseFromService response = service.execute(request, urlToExecute);
		logRequestAsFulfilled(service, request, response,
				originalHttpReqFromClient.getRemoteAddr(), inspectionMessage);

		try {
			// Wait for a X hang time seconds.
			logger.debug("Waiting..." + service.getHangTime() + " miliseconds ");
			Thread.currentThread().sleep(service.getHangTime());
			logger.debug("Done Waiting");
		} catch (Exception e) {
			// Catch interrupt exception.
			// Or not.
		}

		if (!(service.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_PROXY)) {
			if (response.getHeaders() != null) {
				for (Header h : response.getHeaders()) {
					resp.setHeader(h.getName(), h.getValue());
				}
			}
			byte[] myCharSetBytes = null;
			String body = response.getBody();
			
			// TODO: Post processing starts
			// Have to improve the logic by adding support response processing
			// That approach will have a plugin like system where every response
			// can be processed prior to sending back to the client. For now, I
			// just added a hack which looks for 'Mockey-Post-Method' in the
			// headers and if found look for the value which corresponds to the
			// post processing method. And based on the method do special
			// processing. In this specific case, it just look for
			// 'loadImageFile' and if found then it used the body of the msg as
			// the file name of the image which should be passed as binary data.
			// NOTE: I have to add this quick hack sinse we need this feature
			// really urgently.
			// NOTE: This code will drastically change after I implement the
			// full post processing mechanism
			
			// Usage: To use this hack. Just create a mockey scenario and add a
			// header 'Mockey-Post-Method'. The value will be the post processor
			// method name to be called. As of now, it only support
			// 'loadImageFile'. This method expects file name to be the content
			// of the body. Example:
			// Mockey-Post-Method: loadImageFile
			//
			//
			// imageFileName.png

			Header mockeyPostHeader = response.getHeader("Mockey-Post-Method");

			if (mockeyPostHeader != null) {
				String postValue = mockeyPostHeader.getValue();
				logger.debug("Found the post processing header with : " + postValue);
				logger.debug("Post process body: " + response.getBody());

				// TODO Need to support user files which are not image files like zip, tar etc.
				if (postValue.equalsIgnoreCase("loadImageFile")) {
					String fileName = response.getBody();
					FileSystemManager fsm = new FileSystemManager();
					File imageFile = fsm.getImageFile(fileName);
					logger.debug("Creaing image");
					myCharSetBytes = fsm.getBytesFromFile(imageFile);
				}
			} else {
				myCharSetBytes = response.getBody().getBytes();
			}

			new PrintStream(resp.getOutputStream()).write(myCharSetBytes);
			resp.getOutputStream().flush();
			// TODO: Post processing ends
		} else {
			// HEADERS
			resp.setStatus(response.getHttpResponseStatusCode());
			response.writeToOutput(resp);
		}
	}

	private void logRequestAsFulfilled(Service service,
			RequestFromClient request, ResponseFromService response, String ip,
			RequestInspectionResult inspectionResult)
			throws UnsupportedEncodingException {
		FulfilledClientRequest fcr = new FulfilledClientRequest();
		fcr.setRawRequest((response.getRequestUrl() != null) ? response
				.getRequestUrl().toString() : "");
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
			fcr.setOriginalUrlBeforeTwisting(response
					.getOriginalRequestUrlBeforeTwisting().toString());
		}
		fcr.setRequestInspectionResult(inspectionResult);
		store.saveOrUpdateFulfilledClientRequest(fcr);
	}
}
