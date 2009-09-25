/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mockey.server;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    public void service(HttpServletRequest originalHttpReqFromClient, HttpServletResponse resp) throws ServletException, IOException {
        
    	RequestFromClient request = new RequestFromClient(originalHttpReqFromClient);
        
        logger.info(request.getHeaderInfo());
        logger.info(request.getParameterInfo());

        String requestedUrl = originalHttpReqFromClient.getRequestURI();
        String contextRoot = originalHttpReqFromClient.getContextPath();
        if (requestedUrl.startsWith(contextRoot)) {
            requestedUrl = requestedUrl.substring(contextRoot.length(), requestedUrl.length());
        }
        Url serviceUrl = new Url(requestedUrl);
        
        Service service = store.getServiceByUrl(serviceUrl.getFullUrl());
        service.setHttpMethod(originalHttpReqFromClient.getMethod());

        ResponseFromService response = service.Execute(request);
        logRequestAsFulfilled(service, request, response, originalHttpReqFromClient.getRemoteAddr());

        try {
            // Wait for a minute.
            logger.debug("Waiting..." + service.getHangTime() + " miliseconds ");
            Thread.currentThread().sleep(service.getHangTime());
            logger.debug("Done Waiting");
        } catch (Exception e) {
            // Catch interrupt exception.
        	// Or not.
        }
        
        if (!(service.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_PROXY)) {
            resp.setContentType(service.getHttpContentType());
            new PrintStream(resp.getOutputStream()).println(response.getBody());
        } else {
        	response.writeToOutput(resp);
        }
    }
    
    private void logRequestAsFulfilled(Service service, RequestFromClient request, ResponseFromService response, String ip) {
        FulfilledClientRequest fulfilledClientRequest = new FulfilledClientRequest();
        fulfilledClientRequest.setRequestorIP(ip);
        fulfilledClientRequest.setServiceId(service.getId());
        fulfilledClientRequest.setServiceName(service.getServiceName());

        if (!request.hasPostBody()) {
            fulfilledClientRequest.setClientRequestBody("[No post body provided by client]");
        } else {
            fulfilledClientRequest.setClientRequestBody(request.getBodyInfo());
        }
        fulfilledClientRequest.setClientRequestHeaders(request.getHeaderInfo());
        fulfilledClientRequest.setClientRequestParameters(request.getParameterInfo());
        fulfilledClientRequest.setResponseMessage(response);
        store.logClientRequest(fulfilledClientRequest);
    }
}
