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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.http.Header;

import com.mockey.ClientExecuteProxy;
import com.mockey.MockServiceBean;
import com.mockey.MockServiceScenarioBean;
import com.mockey.MockServiceStore;
import com.mockey.MockServiceStoreImpl;
import com.mockey.ProxyServer;

public class MockResponseServlet extends HttpServlet {

	private static final long serialVersionUID = 8401356766354139506L;
	private static MockServiceStore store = MockServiceStoreImpl.getInstance();
	private static Logger logger = Logger.getLogger(MockResponseServlet.class);

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
        logger.info(request.dumpHeaders());
        
		String requestIp = req.getRemoteAddr();
		String urlPath = req.getRequestURI();
		MockServiceBean serviceBean = store.getMockServiceByUrl(urlPath);
        serviceBean.setHttpMethod(req.getMethod());

        // There are several options to look at:
		// 1. Respond with real service response
		// 2. Respond with scenario response independent of request message.
		// 3. Respond with scenario response dependent on matching request
		// message.
		//
        
        StringBuffer responseMsg = new StringBuffer();


        String requestMsg = request.getParametersAsString();

		// If no scenarios, then proxy is automatically on.
		if (serviceBean.getScenarios().size() == 0) {
			serviceBean.setProxyOn(true);
		}

		// If proxy on, then
		// 1) Capture request message.
		// 2) Set up a connection to the real service URL
		// 3) Forward the request message to the real service URL
		// 4) Read the reply from the real service URL.
		// 5) Save request + response as a historical scenario.
		ResponseMessage proxyResponse = null;
		if (serviceBean.isProxyOn()) {

			// There are 2 proxy things going on here:
			// 1. Using Mockey as a 'proxy' to a real service.
			// 2. The proxy server between Mockey and the real service.
			//
			// For the proxy server between Mockey and the real service,
			// we do the following:
			ProxyServer proxyServer = store.getProxyInfo();
			if (proxyServer.isProxyEnabled()) {
				ClientExecuteProxy clientExecuteProxy = new ClientExecuteProxy();
				try {
                    logger.debug("Initiating request through proxy");
					proxyResponse = clientExecuteProxy.execute(proxyServer, serviceBean, request);
                    proxyResponse.writeToOutput(resp);
                    return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {

				proxyResponse = getResponseMsg(serviceBean.getRealServiceUrl(), requestMsg.toString(), serviceBean
						.getHttpHeaderDefinition());

			}
			responseMsg.append(proxyResponse.getResponseMsg());
		}
		// Proxy is NOT on. Therefore we use a scenario to figure out a reply.
		// Either:
		// 1) Based on matching the request message to one of the scenarios
		// or
		// 2) Based on scenario selected.
		//
		else {
			if (serviceBean.isReplyWithMatchingRequest()) {
				List scenarios = serviceBean.getScenarios();
				Iterator iter = scenarios.iterator();
				String messageMatchFound = null;
				while (iter.hasNext()) {
					MockServiceScenarioBean scenario = (MockServiceScenarioBean) iter.next();
					logger.debug("Checking: '" + scenario.getMatchStringArg() + "' in Scenario message: \n"
							+ requestMsg.toString());

					int indexValue = requestMsg.toString().indexOf(scenario.getMatchStringArg());
					if (requestMsg.toString() != null && (indexValue > -1)) {
						logger.debug("FOUND - matching '" + scenario.getMatchStringArg() + "' in message: \n"
								+ requestMsg.toString());

						messageMatchFound = scenario.getResponseMessage();

						break;
					}
				}
				if (messageMatchFound == null) {
					messageMatchFound = "Big fat ERROR:[Be sure to view source to see more...] \n"
							+ "Your setting is 'match scenario' but there is no matching scenario to incoming message: \n"
							+ requestMsg.toString();
				}
				responseMsg.append(messageMatchFound);

			} else {
				MockServiceScenarioBean scenario = serviceBean.getScenario(serviceBean.getDefaultScenarioId());

				if (scenario != null) {

					responseMsg.append(scenario.getResponseMessage());
				} else {
					responseMsg.append("NO SCENARIO SELECTED");
				}

			}
		}

		MockServiceScenarioBean mssb = new MockServiceScenarioBean();
		mssb.setScenarioName((new Date()) + " Remote address:" + requestIp);
		mssb.setRequestMessage(requestMsg.toString());
		mssb.setResponseMessage(responseMsg.toString());
		mssb.setConsumerId(requestIp);
		mssb.setServiceId(serviceBean.getId());
		store.addHistoricalScenario(mssb);

		//       
		// String responseType = ms.getResponseType();
		// logger.debug("POST request from : '" + requestIp +
		// "'. Response will be:" + responseType);

		try {
			// Wait for a minute.
			logger.debug("Waiting..." + serviceBean.getHangTime() + " miliseconds ");

			long future = System.currentTimeMillis() + serviceBean.getHangTime();

			while (true) {
				if (System.currentTimeMillis() > future) {
					break;
				}
			}

			logger.debug("Done Waiting");
		} catch (Exception e) {
			// Catch interrupt exception
		}
        resp.setContentType(serviceBean.getHttpHeaderDefinition());
		PrintStream out = null;
		if (serviceBean.isProxyOn() && !proxyResponse.isValid()) {
			resp.setContentType("text/plain");
			out = new PrintStream(resp.getOutputStream());
			out.println(proxyResponse.getErrorMsg());
		} else {
			out = new PrintStream(resp.getOutputStream());
			out.println(responseMsg);
		}

		return;
	}

	/**
	 * Returns the output message (if any) from the request to URL with the
	 * request message.
	 * 
	 * @param urlString
	 * @param requestMsg
	 * @return
	 */
	private ResponseMessage getResponseMsg(String urlString, String requestMsg, String contentType) {
		ResponseMessage responseMessage = new ResponseMessage();
		StringBuffer responseMsg = new StringBuffer();
		BufferedWriter bWriter = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-type", contentType);
			connection.setDoOutput(true);

			bWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			bWriter.write(requestMsg);
			bWriter.flush();

			if (logger.isDebugEnabled()) {
				StringBuffer debugStatement = new StringBuffer();
				debugStatement.append("Set-Cookie:" + connection.getHeaderField("Set-Cookie"));
				debugStatement.append("Cookie:" + connection.getHeaderField("Cookie"));
				debugStatement.append("Headers:");
				Map headers = connection.getHeaderFields();
				for (Iterator it = headers.entrySet().iterator(); it.hasNext();) {
					Map.Entry me = (Map.Entry) it.next();
					debugStatement.append("\t" + me.getKey() + " = " + me.getValue());
				}
				logger.debug(debugStatement.toString());
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String response;

			while ((response = in.readLine()) != null) {
				responseMsg.append(response);
			}
			responseMessage.setValid(true);
			bWriter.close();
			in.close();
		} catch (MalformedURLException ex) {
			responseMessage.setValid(false);
			responseMessage.setErrorMsg("Error! Most likely, you have a malformed setting for your real service URL '"
					+ urlString + "' " + "or have not selected a scenario.");
		} catch (IOException ex) {
			responseMessage.setValid(false);
			responseMessage.setErrorMsg("Error! There was a IOException when trying to connect to URL '" + urlString
					+ "'");

		} finally {
			if (bWriter != null) {
				try {
					bWriter.close();
				} catch (Exception ex) {
				}
			}

			if (connection != null) {
				try {
					connection.disconnect();
				} catch (Exception ex) {
				}
			}
		}

		responseMessage.setResponseMsg(responseMsg.toString());
		return responseMessage;
	}

}
