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

import com.mockey.*;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MockResponseServlet extends HttpServlet {

    private static final long serialVersionUID = 8401356766354139506L;
    private static MockServiceStore store = MockServiceStoreImpl.getInstance();
    private static Logger logger = Logger.getLogger(MockResponseServlet.class);

    /**
     * Parses the caller's remote address, parses the URL, (the URI) then
     * determines the appropriate mockservice for the definition of the response
     * type.
     *
     * @param req  basic request
     * @param resp basic resp
     * @throws ServletException basic
     * @throws IOException      basic
     */
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestFromClient request = new RequestFromClient(req);
        logger.info(request.dumpHeaders());

        String requestIp = req.getRemoteAddr();

        // on macs sometimes localhost resolves to the IPV6 format IP
        if (requestIp.equals("0:0:0:0:0:0:0:1%0")) {
            requestIp = "127.0.0.1";
        }

        String urlPath = req.getRequestURI();
        MockServiceBean realService = store.getMockServiceByUrl(urlPath);
        realService.setHttpMethod(req.getMethod());

        // There are several options to look at:
        // 1. Respond with real service response
        // 2. Respond with scenario response independent of request message.
        // 3. Respond with scenario response dependent on matching request
        // message.
        //

        StringBuffer responseMsg = new StringBuffer();


        String requestMsg = request.getParametersAsString();

        // If no scenarios, then proxy is automatically on.
        if (realService.getScenarios().size() == 0) {
            realService.setProxyOn(true);
        }

        // If proxy on, then
        // 1) Capture request message.
        // 2) Set up a connection to the real service URL
        // 3) Forward the request message to the real service URL
        // 4) Read the reply from the real service URL.
        // 5) Save request + response as a historical scenario.
        ResponseMessage proxyResponse;
        if (realService.isProxyOn()) {

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
                proxyResponse = clientExecuteProxy.execute(proxyServer, realService, request);
                proxyResponse.writeToOutput(resp);

                MockServiceScenarioBean mssb = new MockServiceScenarioBean();
                mssb.setScenarioName((new Date()) + " Remote address:" + requestIp);
                mssb.setRequestMessage(request.toString());
                mssb.setResponseMessage(proxyResponse.getBody());
                mssb.setConsumerId(requestIp);
                mssb.setServiceId(realService.getId());
                store.addHistoricalScenario(mssb);

                return;
            } catch (Exception e) {
                throw new ServletException(e);
            }

        }
        // Proxy is NOT on. Therefore we use a scenario to figure out a reply.
        // Either:
        // 1) Based on matching the request message to one of the scenarios
        // or
        // 2) Based on scenario selected.
        //
        else {
            if (realService.isReplyWithMatchingRequest()) {
                List scenarios = realService.getScenarios();
                Iterator iter = scenarios.iterator();
                String messageMatchFound = null;
                while (iter.hasNext()) {
                    MockServiceScenarioBean scenario = (MockServiceScenarioBean) iter.next();
                    logger.debug("Checking: '" + scenario.getMatchStringArg() + "' in Scenario message: \n"
                            + requestMsg);

                    int indexValue = requestMsg.indexOf(scenario.getMatchStringArg());
                    if ((indexValue > -1)) {
                        logger.debug("FOUND - matching '" + scenario.getMatchStringArg() + "' in message: \n"
                                + requestMsg);

                        messageMatchFound = scenario.getResponseMessage();

                        break;
                    }
                }
                if (messageMatchFound == null) {
                    messageMatchFound = "Big fat ERROR:[Be sure to view source to see more...] \n"
                            + "Your setting is 'match scenario' but there is no matching scenario to incoming message: \n"
                            + requestMsg;
                }
                responseMsg.append(messageMatchFound);

            } else {
                MockServiceScenarioBean scenario = realService.getScenario(realService.getDefaultScenarioId());

                if (scenario != null) {

                    responseMsg.append(scenario.getResponseMessage());
                } else {
                    responseMsg.append("NO SCENARIO SELECTED");
                }

            }
        }

        MockServiceScenarioBean mssb = new MockServiceScenarioBean();
        mssb.setScenarioName((new Date()) + " Remote address:" + requestIp);
        mssb.setRequestMessage(requestMsg);
        mssb.setResponseMessage(responseMsg.toString());
        mssb.setConsumerId(requestIp);
        mssb.setServiceId(realService.getId());
        store.addHistoricalScenario(mssb);

        //
        // String responseType = ms.getResponseType();
        // logger.debug("POST request from : '" + requestIp +
        // "'. Response will be:" + responseType);

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
        resp.setContentType(realService.getHttpHeaderDefinition());
        PrintStream out;
        out = new PrintStream(resp.getOutputStream());
        out.println(responseMsg);
    }
}
