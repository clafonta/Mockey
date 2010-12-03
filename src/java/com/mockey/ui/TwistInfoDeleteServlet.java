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
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.model.TwistInfo;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class TwistInfoDeleteServlet extends HttpServlet implements TwistInfoConfigurationAPI{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1116768564448626948L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	
	/**
	 * Handles the following activities for <code>TwistInfo</code>
	 * 
	 */
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String responseType = req.getParameter("response-type");
		// If type is JSON, then respond with JSON
		// Otherwise, direct to JSP

		Long twistInfoId = null;
		TwistInfo twistInfo = null;
		try {
			twistInfoId = new Long(req.getParameter(PARAMETER_KEY_TWIST_ID));
			twistInfo = store.getTwistInfoById(twistInfoId);
			store.deleteTwistInfo(twistInfo);
		} catch (Exception e) {
			// Do nothing. If the value doesn't exist, oh well. 
		}

		if (PARAMETER_KEY_RESPONSE_TYPE_VALUE_JSON.equalsIgnoreCase(responseType)) {
			// ***********************
			// BEGIN - JSON response
			// ***********************
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			try {
				JSONObject jsonResponseObject = new JSONObject();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("success", "Deleted");
				jsonResponseObject.put("result", jsonObject);
				out.println(jsonResponseObject.toString());

			} catch (JSONException jsonException) {
				throw new ServletException(jsonException);
			}

			out.flush();
			out.close();
			return;
			// ***********************
			// END - JSON response
			// ***********************

		} else {
			List<TwistInfo> twistInfoList = store.getTwistInfoList();
			Util.saveSuccessMessage("Deleted", req);
			req.setAttribute("twistInfoList", twistInfoList);
			req.setAttribute("twistInfoIdEnabled", store.getUniversalTwistInfoId());

			RequestDispatcher dispatch = req.getRequestDispatcher("/twistinfo_setup.jsp");
			dispatch.forward(req, resp);
			return;
		}

	}

}
