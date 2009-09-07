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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.model.ProxyServerModel;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.XmlMockeyStorage;

public class ProxyInfoServlet extends HttpServlet {

	
	private static final long serialVersionUID = 5503460488900643184L;
	private static IMockeyStorage store = XmlMockeyStorage.getInstance();
	
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
		
		ProxyServerModel proxyInfo = store.getProxyInfo();
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
		try{			
			proxyEnabled = Boolean.parseBoolean(enabled);
		}catch(Exception e){
			e.printStackTrace();
		}
		proxyInfo.setProxyEnabled(proxyEnabled);
		Util.saveSuccessMessage("Proxy settings set", req);
		store.setProxyInfo(proxyInfo);
		proxyInfo = store.getProxyInfo();
		req.setAttribute("proxyInfo", proxyInfo);
		RequestDispatcher dispatch = req.getRequestDispatcher("/proxy_setup.jsp");
		dispatch.forward(req, resp);
	}
}
