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
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mockey.ClientExecuteProxy;
import com.mockey.model.ProxyServerModel;
import com.mockey.model.RequestFromClient;
import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.ResponseFromService;
import com.mockey.model.Service;
import com.mockey.model.Scenario;
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

        ResponseFromService response = null;
        boolean isAMoxie = false;
        if (service.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_PROXY) {
        		response = proxyTheRequest(service, request);
            isAMoxie = true;
        } else if (service.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_DYNAMIC_SCENARIO) {
        		response = executeDynamicScenario(service, request);
        		isAMoxie = false;
        } else if (service.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_STATIC_SCENARIO) {
        		response = executeStaticScenario(service);
        		isAMoxie = false;
        }

        logRequest(service, request, response, originalHttpReqFromClient.getRemoteAddr());

        try {
            // Wait for a minute.
            logger.debug("Waiting..." + service.getHangTime() + " miliseconds ");
            Thread.currentThread().sleep(service.getHangTime());
            logger.debug("Done Waiting");
        } catch (Exception e) {
            // Catch interrupt exception.
        		// Or not.
        }
        
        if (!isAMoxie) {
            resp.setContentType(service.getHttpContentType());
            new PrintStream(resp.getOutputStream()).println(response.getBody());
        } else {
        		response.writeToOutput(resp);
        }
    }
    
    private ResponseFromService proxyTheRequest(Service service, RequestFromClient request) {

    		logger.debug("proxying a moxie.");
        // If proxy on, then
        // 1) Capture request message.
        // 2) Set up a connection to the real service URL
        // 3) Forward the request message to the real service URL
        // 4) Read the reply from the real service URL.
        // 5) Save request + response as a historical scenario.
    	
        // There are 2 proxy things going on here:
        // 1. Using Mockey as a 'proxy' to a real service.
        // 2. The proxy server between Mockey and the real service.
        //
        // For the proxy server between Mockey and the real service,
        // we do the following:
        ProxyServerModel proxyServer = store.getProxy();
        ClientExecuteProxy clientExecuteProxy = new ClientExecuteProxy();
        ResponseFromService response = null;
        try {
            logger.debug("Initiating request through proxy");
            response = clientExecuteProxy.execute(proxyServer, service, request);
        } catch (Exception e) {
            // We're here for various reasons.
            // 1) timeout from calling real service.
            // 2) unable to parse real response.
            // 3) magic!
            // Before we throw an exception, check:
            // (A) does this mock service have a default error response. If
            // no, then
            // (B) see if Mockey has a universal error response
            // If neither, then throw the exception.
            response = new ResponseFromService();
            
            Scenario error = service.getErrorScenario();
            if (error != null) {
                response.setBody(error.getResponseMessage());
            } else {
            	response.setBody("No scenario defined. Also, we encountered this error: " + e.getClass() + ": "
                            + e.getMessage());
            }   
        }
        return response;
    }
    
    private ResponseFromService executeStaticScenario(Service service) {
    	
    		logger.debug("mockeying a static scenario");
    		
        // Proxy is NOT on. Therefore we use a scenario to figure out a reply.
        // Either:
        // 1) Based on matching the request message to one of the scenarios
        // or
        // 2) Based on scenario selected.
        //
        Scenario scenario = service.getScenario(service.getDefaultScenarioId());
        ResponseFromService response = new ResponseFromService();

        if (scenario != null) {
            response.setBody(scenario.getResponseMessage());
        } else {
            response.setBody("NO SCENARIO SELECTED");
        }
        return response;
    }
    
    private ResponseFromService executeDynamicScenario(Service service, RequestFromClient request) {
    	
    		logger.debug("mockeying a dynamic scenario.");
    		String rawRequestData = "";
    		try {
            rawRequestData = new String();
            if (!request.hasPostBody()) {
                // OK..let's build the request message from Params.
                // Is this a HACK? I dunno yet.
                logger.debug("Request message is EMPTY; building request message out of Parameters. ");
                rawRequestData = request.buildParameterRequest();
            } else {
                rawRequestData = request.getBodyInfo();
            }
    		} catch (UnsupportedEncodingException e) {
    			// uhm.
    		}
            
        ResponseFromService response = new ResponseFromService();
        List<Scenario> scenarios = service.getScenarios();
        Iterator<Scenario> iter = scenarios.iterator();
        String messageMatchFound = null;
        while (iter.hasNext()) {
            Scenario scenario = iter.next();
            logger
                    .debug("Checking: '" + scenario.getMatchStringArg() + "' in Scenario message: \n"
                            + rawRequestData);
            int indexValue = -1;
            if (request.hasPostBody()) {
                indexValue = request.getBodyInfo().indexOf(scenario.getMatchStringArg());
            } else {
                indexValue = rawRequestData.indexOf(scenario.getMatchStringArg());
            }

            if ((indexValue > -1)) {
                logger.debug("FOUND - matching '" + scenario.getMatchStringArg() + "' ");
                messageMatchFound = scenario.getResponseMessage();
                break;
            }
        }
        if (messageMatchFound == null) {
            messageMatchFound = "Big fat ERROR:[Be sure to view source to see more...] \n"
                    + "Your setting is 'match scenario' but there is no matching scenario to incoming message: \n"
                    + rawRequestData;
        }
        response.setBody(messageMatchFound);
        return response;
    }
    
    private void logRequest(Service service, RequestFromClient request, ResponseFromService response, String ip) {
        // **********************
        // History
        // **********************
        FulfilledClientRequest fulfilledClientRequest = new FulfilledClientRequest();
        fulfilledClientRequest.setRequestorIP(ip);
        fulfilledClientRequest.setServiceId(service.getId());

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
