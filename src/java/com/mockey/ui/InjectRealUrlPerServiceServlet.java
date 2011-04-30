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
package com.mockey.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

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
		JSONObject successOrFail = new JSONObject();
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

			try {
				successOrFail.put("success", "URL injecting complete.");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				successOrFail.put("fail",
						"You didn't pass any match or inject URL arguments.  ");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		JSONObject responseObject = new JSONObject();
		
		try {
			responseObject.put("result", successOrFail);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter out = resp.getWriter();
		out.println(responseObject.toString());
		out.flush();
		out.close();
		return;
	}
}
