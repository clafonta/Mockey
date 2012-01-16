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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.model.ProxyServerModel;
import com.mockey.model.TwistInfo;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class ConfigurationInfoAJAXServlet extends HttpServlet {

	private static final long serialVersionUID = 5503460488900643184L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	private static Logger logger = Logger.getLogger(ConfigurationInfoAJAXServlet.class);

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
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ProxyServerModel proxyInfo = store.getProxy();
		String transientState = req.getParameter("transient_state");

		try {
			if (transientState != null) {
				store.setReadOnlyMode(new Boolean(transientState));
			}
		} catch (Exception e) {
			logger.error("Unable to set transient state with value: " + transientState, e);
		}

		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		try {
			JSONObject responseObject = new JSONObject();
			JSONObject messageObject = new JSONObject();
			messageObject.put("proxy_enabled", Boolean.toString(proxyInfo.isProxyEnabled()));
			messageObject.put("transient_state", store.getReadOnlyMode());
			Long twistInfoId = store.getUniversalTwistInfoId();
			TwistInfo twistInfo = store.getTwistInfoById(twistInfoId);
			if (twistInfo != null) {
				messageObject.put("twist_enabled", true);
				messageObject.put("twist-id", twistInfo.getId());
				messageObject.put("twist-name", twistInfo.getName());

			} else {
				messageObject.put("twist_enabled", false);
			}
			String filter = (String)req.getSession().getAttribute(TagHelperServlet.FILTER_TAG);
			if(filter!=null && filter.trim().length()>0){
				messageObject.put("filter_view_arg", filter.trim());
				messageObject.put("filter_view_status", "on");
			}else {
				messageObject.put("filter_view_arg", "");
				messageObject.put("filter_view_status", "off");
			}
			
			resp.setContentType("application/json;");
			responseObject.put("result", messageObject);

			out.println(responseObject.toString());
		} catch (JSONException e) {
			throw new ServletException(e);
		}
		out.flush();
		out.close();
		return;
	}
}
