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
package com.mockey.web;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.log4j.Logger;

import com.mockey.ClientExecuteProxy;
import com.mockey.MockServiceBean;
import com.mockey.MockServiceScenarioBean;
import com.mockey.MockServiceStore;
import com.mockey.MockServiceStoreImpl;
import com.mockey.ProxyServer;

/**
 * Responsible for serving mock responses. Based on configuration, returns
 * desired content either from a source (Mockey being used as a proxy) or from a
 * defined scenario.
 * 
 * @author chad.lafontaine
 * 
 */
public class MockResponseServlet extends HttpServlet {

    private static final long serialVersionUID = 8401356766354139506L;
    private MockServiceStore store = MockServiceStoreImpl.getInstance();
    private Logger logger = Logger.getLogger(MockResponseServlet.class);

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
        RequestFromClient mockeyRequestFromClient = new RequestFromClient(req);
        logger.info(mockeyRequestFromClient.getHeaderInfo());
        logger.info(mockeyRequestFromClient.getParameterInfo());
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

        MockServiceBean realService = store.getMockServiceByUrl(urlPath);
        realService.setHttpMethod(req.getMethod());

        // There are several options to look at:
        // 1. Respond with real service response
        // 2. Respond with scenario response independent of request message.
        // 3. Respond with scenario response dependent on matching request
        // message.
        String clientRequest = new String();
        if (!mockeyRequestFromClient.hasPostBody()) {
            // OK..let's build the request message from Params.
            // Is this a HACK? I dunno yet.
            logger.debug("Request message is EMPTY; building request message out of Parameters. ");
            clientRequest = mockeyRequestFromClient.buildParameterRequest();
        }else {
            clientRequest = mockeyRequestFromClient.getBodyInfo();
        }
        

        // If no scenarios, then proxy is automatically on.
        if (realService.getScenarios().size() == 0) {
            realService.setServiceResponseType(MockServiceBean.SERVICE_RESPONSE_TYPE_PROXY);
        }

        // If proxy on, then
        // 1) Capture request message.
        // 2) Set up a connection to the real service URL
        // 3) Forward the request message to the real service URL
        // 4) Read the reply from the real service URL.
        // 5) Save request + response as a historical scenario.
        ResponseMessage mockeyResponseMessage = null;
        
        if (realService.getServiceResponseType() == MockServiceBean.SERVICE_RESPONSE_TYPE_PROXY) {

            // There are 2 proxy things going on here:
            // 1. Using Mockey as a 'proxy' to a real service.
            // 2. The proxy server between Mockey and the real service.
            //
            // For the proxy server between Mockey and the real service,
            // we do the following:
            ProxyServer proxyServer = store.getProxyInfo();
            ClientExecuteProxy clientExecuteProxy = new ClientExecuteProxy();

            try {
                logger.debug("Initiating request through proxy");
                mockeyResponseMessage = clientExecuteProxy.execute(proxyServer, realService, mockeyRequestFromClient);
                mockeyResponseMessage.writeToOutput(resp);
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
                mockeyResponseMessage = new ResponseMessage();
                boolean serviceErrorDefined = false;
                // FIND SERVICE ERROR, IF EXIST.
                Iterator iter = realService.getScenarios().iterator();
                while (iter.hasNext()) {
                    MockServiceScenarioBean scenario = (MockServiceScenarioBean) iter.next();
                    if (scenario.getId() == realService.getErrorScenarioId()) {
                        mockeyResponseMessage.setBody(scenario.getResponseMessage());
                        serviceErrorDefined = true;
                        break;
                    }
                }
                // No service error defined, therefore, let's use the universal
                // error.
                if (!serviceErrorDefined) {
                    MockServiceScenarioBean universalError = store.getUniversalErrorResponse();
                    if (universalError != null) {
                        mockeyResponseMessage.setBody(universalError.getResponseMessage());
                    } else {
                        mockeyResponseMessage.setBody(e.getMessage());
                    }
                }

                resp.setContentType(realService.getHttpHeaderDefinition());
                PrintStream out = new PrintStream(resp.getOutputStream());
                out.println(mockeyResponseMessage.getBody());

            }

        }
        // Proxy is NOT on. Therefore we use a scenario to figure out a reply.
        // Either:
        // 1) Based on matching the request message to one of the scenarios
        // or
        // 2) Based on scenario selected.
        //
        else if (realService.getServiceResponseType() == MockServiceBean.SERVICE_RESPONSE_TYPE_DYNAMIC_SCENARIO) {
            mockeyResponseMessage = new ResponseMessage();
            List<MockServiceScenarioBean> scenarios = realService.getScenarios();
            Iterator iter = scenarios.iterator();
            String messageMatchFound = null;
            while (iter.hasNext()) {
                MockServiceScenarioBean scenario = (MockServiceScenarioBean) iter.next();
                logger.debug("Checking: '" + scenario.getMatchStringArg() + "' in Scenario message: \n" + clientRequest);
                int indexValue = -1;
                if(mockeyRequestFromClient.hasPostBody()){
                    indexValue =  mockeyRequestFromClient.getBodyInfo().indexOf(scenario.getMatchStringArg());
                }else {
                    indexValue = clientRequest.indexOf(scenario.getMatchStringArg());
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
                        + clientRequest;
            }
            mockeyResponseMessage.setBody(messageMatchFound);
            

        } else if (realService.getServiceResponseType() == MockServiceBean.SERVICE_RESPONSE_TYPE_STATIC_SCENARIO) {
            MockServiceScenarioBean scenario = realService.getScenario(realService.getDefaultScenarioId());
            mockeyResponseMessage = new ResponseMessage();
            
            if (scenario != null) {
                mockeyResponseMessage.setBody(scenario.getResponseMessage());
                
            } else {
                mockeyResponseMessage.setBody("NO SCENARIO SELECTED");
                
            }

        }
        // **********************
        // History
        // **********************
        RequestResponseTransaction reqRespX = new RequestResponseTransaction();
        MockServiceScenarioBean historyRequestResponse = new MockServiceScenarioBean();        
        historyRequestResponse.setScenarioName((new Date()) + " Remote address:" + requestIp);
        historyRequestResponse.setConsumerId(requestIp);
        historyRequestResponse.setServiceId(realService.getId());
        reqRespX.setServiceInfo(historyRequestResponse);
        
        if(mockeyRequestFromClient.hasPostBody()){
            reqRespX.setClientRequestBody("[No post body provided by client]");
        }else {
            reqRespX.setClientRequestBody(mockeyRequestFromClient.getBodyInfo());
        }
        reqRespX.setClientRequestHeaders(mockeyRequestFromClient.getHeaderInfo());
        reqRespX.setClientRequestParameters(mockeyRequestFromClient.getParameterInfo());
        reqRespX.setResponseMessage(mockeyResponseMessage);        
        store.addHistoricalScenario(reqRespX);

        try {
            // Wait for a minute.
            logger.debug("Waiting..." + realService.getHangTime() + " miliseconds ");

            long future = System.currentTimeMillis() + realService.getHangTime();

            while (true) {
                if (System.currentTimeMillis() > future) {
                    break;
                }
            }

            logger.debug("Done Waiting");
        } catch (Exception e) {
            // Catch interrupt exception
        }
        if (!replied) {
            resp.setContentType(realService.getHttpHeaderDefinition());
            PrintStream out;
            out = new PrintStream(resp.getOutputStream());
            out.println(mockeyResponseMessage.getBody());
        }
    }
   
}
