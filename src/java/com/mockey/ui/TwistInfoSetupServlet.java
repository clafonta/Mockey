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

import com.mockey.model.TwistInfo;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class TwistInfoSetupServlet extends HttpServlet implements TwistInfoConfigurationAPI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2685147086312954142L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	

	/**
	 * Handles the following activities for <code>TwistInfo</code>
	 * 
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		List<TwistInfo> twistInfoList = store.getTwistInfoList();
		String responseType = req.getParameter("response-type");
		// If type is JSON, then respond with JSON
		// Otherwise, direct to JSP

		if (PARAMETER_KEY_RESPONSE_TYPE_VALUE_JSON.equalsIgnoreCase(responseType)) {
			// ***********************
			// BEGIN - JSON response
			// ***********************
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			try {
				JSONObject jsonResponseObject = new JSONObject();
				JSONObject jsonObject = null; // new JSONObject();
				for (TwistInfo twistInfo : twistInfoList) {
					jsonObject = new JSONObject();
					jsonObject.put("id", "" + twistInfo.getId());
					jsonObject.put("name", twistInfo.getName());
					for (PatternPair patternPair : twistInfo.getPatternPairList()) {
						JSONObject ppObj = new JSONObject();
						ppObj.put("origination", patternPair.getOrigination());
						ppObj.put("destination", patternPair.getDestination());
						jsonObject.append("pattern-pair-list", ppObj);
					}
				}

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
			req.setAttribute("twistInfoList", twistInfoList);
			req.setAttribute("twistInfoIdEnabled", store.getUniversalTwistInfoId());
			RequestDispatcher dispatch = req.getRequestDispatcher("/twistinfo_setup.jsp");
			dispatch.forward(req, resp);
			return;
		}

	}

	/**
	 * Handles the following activities for <code>TwistInfo</code>
	 * 
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// ***********************
		// 1. If type is JSON, then respond with JSON
		// 2. Otherwise, dispatch to JSP
		// 3. Create/delete/update TwistInfo
		// ***********************

		// ***********************
		// INITIALIZAION
		// ***********************

		Long twistInfoId = null;
		TwistInfo twistInfo = null;
		try {
			twistInfoId = new Long(req.getParameter(PARAMETER_KEY_TWIST_ID));
			twistInfo = store.getTwistInfoById(twistInfoId);
		} catch (Exception e) {
			// Do nothing. If the value doesn't exist,
			// then we'll create a new TwistInfo
		}

		if (twistInfo == null) {
			twistInfo = new TwistInfo();
		}
		// ***********************
		// DATA HANDLING
		// ***********************
		String name = req.getParameter(PARAMETER_KEY_TWIST_NAME);
		
		if (name == null || name.trim().length() == 0) {
			name = "TwistInfo (auto-generated)";
		}
		String[] originationArguments = req.getParameterValues(PARAMETER_KEY_TWIST_ORIGINATION_LIST);
		String[] destinationArguments = req.getParameterValues(PARAMETER_KEY_TWIST_DESTINATION_LIST);
		// Remove any existing TwistInfo patterns.
		twistInfo.setPatternPairList(new ArrayList<PatternPair>());
		if ((originationArguments != null && destinationArguments != null)
				&& (originationArguments.length == destinationArguments.length)) {
			for (int i = 0; i < originationArguments.length; i++) {
				twistInfo.addPatternPair(new PatternPair(originationArguments[i], destinationArguments[i]));
			}
		}

		twistInfo.setName(name);
		twistInfo = store.saveOrUpdateTwistInfo(twistInfo);
		
		// ***********************
		// RESPONSE - in JSON or JSP
		// ***********************
		String responseType = req.getParameter(PARAMETER_KEY_RESPONSE_TYPE);
		if (PARAMETER_KEY_RESPONSE_TYPE_VALUE_JSON.equalsIgnoreCase(responseType)) {
			// BUILD JSON response
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			try {
				JSONObject jsonResponseObject = new JSONObject();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("success", "Twist updated");
				jsonObject.put("id", ""+twistInfo.getId());
				jsonObject.put("name", twistInfo.getName());

				jsonResponseObject.put("result", jsonObject);
				out.println(jsonResponseObject.toString());

			} catch (JSONException jsonException) {
				throw new ServletException(jsonException);
			}

			out.flush();
			out.close();
			return;
		} else {
			List<TwistInfo> twistInfoList = store.getTwistInfoList();
			req.setAttribute("twistInfoList", twistInfoList);
			req.setAttribute("twistInfoIdEnabled", store.getUniversalTwistInfoId());

			RequestDispatcher dispatch = req.getRequestDispatcher("/twistinfo_setup.jsp");
			dispatch.forward(req, resp);
			return;
		}

	}
}
