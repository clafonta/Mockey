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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.model.ProxyServerModel;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class ProxyInfoServlet extends HttpServlet {

	private static final long serialVersionUID = 5503460488900643184L;
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
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ProxyServerModel proxyInfo = store.getProxy();
		req.setAttribute("proxyInfo", proxyInfo);

		RequestDispatcher dispatch = req.getRequestDispatcher("/proxy_setup.jsp");
		dispatch.forward(req, resp);
	}

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
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ProxyServerModel proxyInfo = new ProxyServerModel();

		proxyInfo.setProxyPassword(req.getParameter("proxyPassword"));
		proxyInfo.setProxyUsername(req.getParameter("proxyUsername"));
		proxyInfo.setProxyUrl(req.getParameter("proxyUrl"));
		String enabled = req.getParameter("proxyEnabled");
		boolean proxyEnabled = false;
		try {
			proxyEnabled = Boolean.parseBoolean(enabled);
		} catch (Exception e) {
			e.printStackTrace();
		}
		proxyInfo.setProxyEnabled(proxyEnabled);
		store.setProxy(proxyInfo);
		JSONObject responseObject = new JSONObject();
		JSONObject successMessage = new JSONObject();

		try {
			successMessage.put("success", "Proxy settings updated.");
			responseObject.put("result", successMessage);

		} catch (JSONException e) {

		}
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		out.println(responseObject.toString());
		out.flush();
		out.close();
		return;
	}
}
