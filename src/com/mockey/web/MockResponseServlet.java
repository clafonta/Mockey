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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

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
		String requestIp = req.getRemoteAddr();
		String urlPath = req.getRequestURI();
		MockServiceBean ms = store.getMockServiceByUrl(urlPath);
        ms.setHttpMethod(req.getMethod());

        logger.info("Examining Headers");
        Enumeration headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = (String) headerNames.nextElement();
            logger.info("Header: "+name+" = "+req.getHeader(name));
        }

		// There are several options to look at:
		// 1. Respond with real service response
		// 2. Respond with scenario response independent of request message.
		// 3. Respond with scenario response dependent on matching request
		// message.
		//
		StringBuffer requestMsg = new StringBuffer();
		String thisLine;

		// NOTE: Get reader only retrieves the BODY of the message
		// but no "params".
		BufferedReader br = new BufferedReader(req.getReader());
		StringBuffer responseMsg = new StringBuffer();
		resp.setContentType(ms.getHttpHeaderDefinition());

		while ((thisLine = br.readLine()) != null) {
			requestMsg.append(thisLine);
		}

		// CHECK to see if the request is GET/POST with PARAMS instead
		// of a BODY message
		if (requestMsg.toString().trim().length() == 0) {
			// OK..let's build the request message from Params.
			// Is this a HACK? I dunno yet.
			logger.debug("Request message is EMPTY; building request message out of Parameters. ");
			// FIRST, let's informe the end user of what we are doing.
			requestMsg.append("NOTE: Incoming request BODY is EMPTY; Building message request body out of PARAMS. ");
			Enumeration parameterNameEnum = req.getParameterNames();
			while (parameterNameEnum.hasMoreElements()) {
				String paramName = (String) parameterNameEnum.nextElement();
				String[] paramValues = req.getParameterValues(paramName);
				// IF foo has multiple VALUES, then create a message
				// like "&foo=x&foo=y" to capture someone matching
				// key value pair. Still, this is a hack; can't predict
				// what people are going to try to match to.
				for (int i = 0; i < paramValues.length; i++) {
                    requestMsg.append("&").append(paramName).append("=").append(paramValues[i]);
				}
			}

		}

		// If no scenarios, then proxy is automatically on.
		if (ms.getScenarios().size() == 0) {
			ms.setProxyOn(true);
		}

		// If proxy on, then
		// 1) Capture request message.
		// 2) Set up a connection to the real service URL
		// 3) Forward the request message to the real service URL
		// 4) Read the reply from the real service URL.
		// 5) Save request + response as a historical scenario.
		ResponseMessage proxyResponse = null;
		if (ms.isProxyOn()) {

			// There are 2 proxy things going on here:
			// 1. Using Mockey as a 'proxy' to a real service.
			// 2. The proxy server between Mockey and the real service.
			//
			// For the proxy server between Mockey and the real service,
			// we do the following:
			ProxyServer proxyInfo = store.getProxyInfo();
			if (proxyInfo.isProxyEnabled()) {
				ClientExecuteProxy proxyServer = new ClientExecuteProxy();
				try {
                    logger.debug("Initiating request through proxy");
					proxyResponse = proxyServer.execute(proxyInfo, ms, String.valueOf(requestMsg));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {

				proxyResponse = getResponseMsg(ms.getRealServiceUrl(), requestMsg.toString(), ms
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
			if (ms.isReplyWithMatchingRequest()) {
				List scenarios = ms.getScenarios();
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
				MockServiceScenarioBean scenario = ms.getScenario(ms.getDefaultScenarioId());

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
		mssb.setServiceId(ms.getId());
		store.addHistoricalScenario(mssb);

		//       
		// String responseType = ms.getResponseType();
		// logger.debug("POST request from : '" + requestIp +
		// "'. Response will be:" + responseType);

		try {
			// Wait for a minute.
			logger.debug("Waiting..." + ms.getHangTime() + " miliseconds ");

			long future = System.currentTimeMillis() + ms.getHangTime();

			while (true) {
				if (System.currentTimeMillis() > future) {
					break;
				}
			}

			logger.debug("Done Waiting");
		} catch (Exception e) {
			// Catch interrupt exception
		}

		PrintStream out = null;
		if (ms.isProxyOn() && !proxyResponse.isValid()) {
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
