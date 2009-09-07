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
import java.util.Date;
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
     * 
     * @param req
     *            basic request
     * @param resp
     *            basic resp
     * @throws ServletException
     *             basic
     * @throws IOException
     *             basic
     */
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestFromClient request = new RequestFromClient(req);
        logger.info(request.getHeaderInfo());
        logger.info(request.getParameterInfo());
        boolean replied = false;
        String requestIp = req.getRemoteAddr();

        // on macs sometimes localhost resolves to the IPV6 format IP
        if (requestIp.equals("0:0:0:0:0:0:0:1%0")) {
            requestIp = "127.0.0.1";
        }

        String urlPath = req.getRequestURI();

        String contextRoot = req.getContextPath();
        if (urlPath.startsWith(contextRoot)) {
            urlPath = urlPath.substring(contextRoot.length(), urlPath.length());
        }
        Url url = new Url(urlPath);
        Service service = store.getServiceByUrl(url.getFullUrl());
        service.setHttpMethod(req.getMethod());

        // There are several options to look at:
        // 1. Respond with real service response
        // 2. Respond with scenario response independent of request message.
        // 3. Respond with scenario response dependent on matching request
        // message.
        String rawRequestData = new String();
        if (!request.hasPostBody()) {
            // OK..let's build the request message from Params.
            // Is this a HACK? I dunno yet.
            logger.debug("Request message is EMPTY; building request message out of Parameters. ");
            rawRequestData = request.buildParameterRequest();
        } else {
            rawRequestData = request.getBodyInfo();
        }

        // If proxy on, then
        // 1) Capture request message.
        // 2) Set up a connection to the real service URL
        // 3) Forward the request message to the real service URL
        // 4) Read the reply from the real service URL.
        // 5) Save request + response as a historical scenario.
        ResponseFromService response = null;

        if (service.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_PROXY) {

            // There are 2 proxy things going on here:
            // 1. Using Mockey as a 'proxy' to a real service.
            // 2. The proxy server between Mockey and the real service.
            //
            // For the proxy server between Mockey and the real service,
            // we do the following:
            ProxyServerModel proxyServer = store.getProxy();
            ClientExecuteProxy clientExecuteProxy = new ClientExecuteProxy();

            try {
                logger.debug("Initiating request through proxy");
                response = clientExecuteProxy.execute(proxyServer, service, request);
                response.writeToOutput(resp);
                replied = true;
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

        }
        // Proxy is NOT on. Therefore we use a scenario to figure out a reply.
        // Either:
        // 1) Based on matching the request message to one of the scenarios
        // or
        // 2) Based on scenario selected.
        //
        else if (service.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_DYNAMIC_SCENARIO) {
            response = new ResponseFromService();
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

        } else if (service.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_STATIC_SCENARIO) {
            Scenario scenario = service.getScenario(service.getDefaultScenarioId());
            response = new ResponseFromService();

            if (scenario != null) {
                response.setBody(scenario.getResponseMessage());
            } else {
                response.setBody("NO SCENARIO SELECTED");
            }

        }

        // **********************
        // History
        // **********************
        FulfilledClientRequest fulfilledClientRequest = new FulfilledClientRequest();
        fulfilledClientRequest.setRequestorIP(requestIp);
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

        try {
        	
            // Wait for a minute.
            logger.debug("Waiting..." + service.getHangTime() + " miliseconds ");
            
            Thread.currentThread().sleep(service.getHangTime());
        
            logger.debug("Done Waiting");
   
        } catch (Exception e) {
            // Catch interrupt exception
        }
        if (!replied) {
            resp.setContentType(service.getHttpContentType());
            new PrintStream(resp.getOutputStream()).println(response.getBody());
        }
    }

}
