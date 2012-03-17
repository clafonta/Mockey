/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2012  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
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
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.plugin.PluginStore;

/**
 * Returns JSON of the fulfilled request, designed to be consumed by an AJAX
 * call.
 * 
 */
public class RequestInspectorAjaxServlet extends HttpServlet {

	private static final long serialVersionUID = 4178219038104708097L;
	private static Logger logger = Logger.getLogger(RequestInspectorAjaxServlet.class);

	/**
     * 
     */
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Long fulfilledRequestId = null;
		JSONObject jsonObject = new JSONObject();
		JSONArray array = new JSONArray();
		try {
			for (Class<?> item : PluginStore.getInstance().getRequestInspectorImplClassList()) {
				array.put(item.getName());
			}
			jsonObject.putOpt("request_inspectors", array);

		} catch (Exception e) {
			try {
				jsonObject.put("error", "" + "Sorry, history for this conversation (fulfilledRequestId="
						+ fulfilledRequestId + ") is not available.");
			} catch (JSONException e1) {
				logger.error("Unable to create JSON", e1);
			}
		}

		resp.setContentType("application/json");

		PrintStream out = new PrintStream(resp.getOutputStream());

		out.println(jsonObject.toString());
	}

}
