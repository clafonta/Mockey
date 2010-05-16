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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Responsible building a request and then calling for a server response.
 * 
 * @author chad.lafontaine
 * 
 */
public class RedoRequestServlet extends HttpServlet {

	private static final long serialVersionUID = 7709751584129936447L;
	private IMockeyStorage store = StorageRegistry.MockeyStorage;

	// private Logger logger = Logger.getLogger(RedoRequestServlet.class);
	/**
     * 
     */
	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long fulfilledClientRequestId = new Long(req
				.getParameter("fulfilledClientRequestId"));
		FulfilledClientRequest pastFulfilledClientRequest = store
				.getFulfilledClientRequestsById(fulfilledClientRequestId);
		String pipeDelimitedHeaderInfo = req.getParameter("requestHeader");
		String parameters = req.getParameter("requestParameters");
		String body = req.getParameter("requestBody");

		Url serviceUrl = new Url(pastFulfilledClientRequest.getRawRequest());
		StringBuffer redoUrl = new StringBuffer();

		// 1. Cut out the parameters
		String fullUrl = serviceUrl.getFullUrl();
		int indexOfParam = fullUrl.indexOf("?");
		if (indexOfParam > 0) {
			fullUrl = fullUrl.substring(0, indexOfParam);
		}
		redoUrl.append("/service/" + fullUrl);

		RedoRequestWrapper requestWrapper = new RedoRequestWrapper(req);
		// 2. Set the URI
		String contextRoot = req.getContextPath();
		if (!contextRoot.endsWith("/")) {
			contextRoot = contextRoot + "/";
		}
		String requestURI = contextRoot + "service/" + fullUrl;
		requestWrapper.setRequestURI(requestURI);
		// 3. Replace with new parameters.

		List<NameValuePair> newParameters = buildNameValue(parameters);
		for (int i = 0; i < newParameters.size(); i++) {
			requestWrapper.addParameter(newParameters.get(i).getName(),
					newParameters.get(i).getValue());
		}
		// 4. New Body
		requestWrapper.setBody(body);

		// 5. Build headers
		List<NameValuePair> headers = buildNameValue(pipeDelimitedHeaderInfo);
		if (headers != null) {
			for (int i = 0; i < headers.size(); i++) {
				requestWrapper.addHeader(headers.get(i).getName(), headers.get(
						i).getValue());
			}
		}
		// String servletPath = requestWrapper.getServletPath();
		// String requestUrl = requestWrapper.getRequestURL().toString();
		// String requestUri = requestWrapper.getRequestURI();
		// String queryString = requestWrapper.getQueryString();

		String newPath = redoUrl.toString();
		RequestDispatcher requestDispatcher = requestWrapper
				.getRequestDispatcher(newPath); //
		requestDispatcher.forward(requestWrapper, resp);

	}

	private List<NameValuePair> buildNameValue(String pipeDelimitedHeaders) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		StringTokenizer st = new StringTokenizer(pipeDelimitedHeaders, "|");
		while (st.hasMoreTokens()) {
			String nameValuePair = st.nextToken();
			int equalsIndex = nameValuePair.indexOf("=");
			if (equalsIndex > -1) {
				String name = nameValuePair.substring(0, equalsIndex);
				String value = nameValuePair.substring(equalsIndex + 1);
				NameValuePair pair = new NameValuePair(name, value);
				list.add(pair);
			}
		}
		return list;
	}

	private class NameValuePair {
		private String name;
		private String value;

		public NameValuePair(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public String getName() {
			return name;
		}
	}

	public static void main(String[] args) {
		String pipeDelimitedHeaders = "z=y|x=y||";
		RedoRequestServlet fakeRequestServlet = new RedoRequestServlet();
		List<NameValuePair> pairs = fakeRequestServlet
				.buildNameValue(pipeDelimitedHeaders);
		if (pairs != null) {
			for (int i = 0; i < pairs.size(); i++) {
				System.out.println(pairs.get(i).getName() + "="
						+ pairs.get(i).getValue());
			}
		}
	}

}
