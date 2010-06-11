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
package com.mockey.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.model.Service;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class InjectRealUrlPerServiceServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8696328817749243557L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	/**
	 * 
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
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		RequestDispatcher dispatch = req
				.getRequestDispatcher("/inject_realurl.jsp");
		dispatch.forward(req, resp);
	}

	/**
	 * Injects real URLs per service. For example, if real url is
	 * 
	 * <pre>
	 * http://qa1.google.com/search
	 * </pre>
	 * 
	 * and match is
	 * 
	 * <pre>
	 * http://qa3.google.com/
	 * </pre>
	 * 
	 * then this method builds URL as
	 * 
	 * <pre>
	 * http://qa3.google.com/search
	 * </pre>
	 * 
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
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String matchPattern = req.getParameter("match");
		String[] replacementArray = req.getParameterValues("replacement[]");

		Map<String, String> statusMessage = new HashMap<String, String>();
		if (matchPattern != null && replacementArray != null) {

			for (Long serviceId : store.getServiceIds()) {
				Service service = store.getServiceById(serviceId);
				List<Url> newUrlList = new ArrayList<Url>();
				// Build a list of real Url objects.
				for (Url realUrl : service.getRealServiceUrls()) {
					for (String replacement : replacementArray) {
						// We don't want to inject empty string match
						if (replacement.trim().length() > 0) {
							Url newUrl = new Url(realUrl.getFullUrl()
									.replaceAll(matchPattern, replacement));
							if (!service.hasRealServiceUrl(newUrl)) {
								newUrlList.add(newUrl);
								// Note: you should not save or update
								// the realServiceUrl or service while
								// iterating through the list itself, or you'll
								// get
								// a java.util.ConcurrentModificationException
								// Wait until 'after'
							}
						}
					}
				}
				// Save/update this new Url object list.
				for (Url newUrl : newUrlList) {
					service.saveOrUpdateRealServiceUrl(newUrl);
				}

				// Now update the service.
				store.saveOrUpdateService(service);
			}

			statusMessage.put("success", "URL injecting complete.");
		} else {
			statusMessage.put("fail",
					"You didn't pass any match or inject URL arguments.  ");
		}

		String resultingJSON = Util.getJSON(statusMessage);
		PrintWriter out = resp.getWriter();
		out.println(resultingJSON);
		out.flush();
		out.close();
		return;
	}
}
