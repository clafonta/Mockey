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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class HomeServlet extends HttpServlet {

	private static final long serialVersionUID = -5485332140449853235L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	private static Logger logger = Logger.getLogger(HomeServlet.class);
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String action = req.getParameter("action");
		if(action!=null && "start".equals(action)) {
			// Why this check? There's a weird issue with the first start up
			// of Jetty and Mockey. The page loads but without CSS and Javascript initilizing.
			// Workaround: get the browser to Redirect
			String contextRoot = req.getContextPath();
			resp.sendRedirect(Url.getContextAwarePath("/home", contextRoot));
			return;
		}
		else if (action != null && "init".equals(action)) {

			// Flush - clean slate.
			IMockeyStorage store = StorageRegistry.MockeyStorage;
			store.deleteEverything();

			// Load with local file.
			String fileName = req.getParameter("file");
			try {
				File f = new File(fileName);
				if (f.exists()) {
					
					// Slurp it up and initialize definitions.
					FileInputStream fstream = new FileInputStream(f);
					BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Charset.forName(HTTP.UTF_8)));
					StringBuffer inputString = new StringBuffer();
					// Read File Line By Line
					String strLine = null;
					while ((strLine = br.readLine()) != null) {
						// Print the content on the console
						inputString.append(new String(strLine.getBytes(HTTP.UTF_8)));
					}
					ConfigurationReader reader = new ConfigurationReader();
					reader.loadConfiguration(inputString.toString().getBytes(HTTP.UTF_8));
					logger.info("Loaded definitions from " + fileName);
				}
			} catch (Exception e) {
				logger.debug("Unable to load service definitions with name: '" + fileName + "'", e);
			}
			String contextRoot = req.getContextPath();
			resp.sendRedirect(Url.getContextAwarePath("/home", contextRoot));
			return;

		} else if (action != null && "deleteAllServices".equals(action)) {
			// Flush - clean slate.
			IMockeyStorage store = StorageRegistry.MockeyStorage;
			store.deleteEverything();
			String contextRoot = req.getContextPath();
			resp.sendRedirect(Url.getContextAwarePath("/home", contextRoot));
			return;
		} else {

			req.setAttribute("services", Util.orderAlphabeticallyByServiceName(store.getServices()));
			req.setAttribute("plans", Util.orderAlphabeticallyByServicePlanName(store.getServicePlans()));
		}

		RequestDispatcher dispatch = req.getRequestDispatcher("home.jsp");

		dispatch.forward(req, resp);
	}

}
