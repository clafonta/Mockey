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
