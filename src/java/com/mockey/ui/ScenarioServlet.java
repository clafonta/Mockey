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
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.ScenarioValidator;
import com.mockey.model.Scenario;
import com.mockey.model.ScenarioRef;
import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class ScenarioServlet extends HttpServlet {

	private static final long serialVersionUID = -5920793024759540668L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	private Logger logger = Logger.getLogger(ScenarioServlet.class);

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// Ensure ENCODING is set. This is important. 
		// The request body will be fully processed only whenever the first call on a getParameterXXX() method is made.
		// We set encoding to ensure we handle special characters. 
		req.setCharacterEncoding(HTTP.UTF_8);
		
		// A Service is needed to associate the
		// scenario to.
		Long serviceId = new Long(req.getParameter("serviceId"));
		Long scenarioId = null;
		try {
			scenarioId = new Long(req.getParameter("scenarioId"));
		} catch (Exception e) {
			// Do nothing. If the value doesn't exist,
			// then we'll create a new Scenario
			// for this service.
		}

		// Get the service.
		Service service = store.getServiceById(serviceId);

		// DELETE scenario logic
		if (req.getParameter("deleteScenario") != null && serviceId != null
				&& scenarioId != null) {
			try {

				service.deleteScenario(scenarioId);
				store.saveOrUpdateService(service);
			} catch (Exception e) {
				// Just in case an invalid service ID
				// or scenario ID were past in.
			}
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			JSONObject result = new JSONObject();
			JSONObject message = new JSONObject();
			try {
				result.put("result", message);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			out.println(result.toString());
			out.flush();
			out.close();
			return;
		}

		Scenario scenario = null;

		// ************************************************
		// HACK A: if renaming an existing Scenario Name, then
		// we need to update this Service's Scenario Name in a
		// Service Plan. NOTE:
		// * we can't ask the Service to update the Plan because it doesn't
		// know about it.
		// * we can't ask the Store to update the Plan because it doesn't
		// know about the Scenario's 'old' name, only this Servlet does!
		// Hence, we do it here via 'newName' and 'oldName'.
		// ************************************************
		String oldName = null;
		String newName = null;
		try {
			scenario = service.getScenario(new Long(req
					.getParameter("scenarioId")));
			// ***************** HACK A ****************
			oldName = scenario.getScenarioName();
			// *****************************************
		} catch (Exception e) {
			//
		}

		// CREATE OR UPDATE OF SCENARIO
		// If scenario is null, that means we're creating,
		// not updating
		if (scenario == null) {
			scenario = new Scenario();
		}

		String scenarioName = req.getParameter("scenarioName");
		if (scenarioName == null || scenarioName.trim().length() == 0) {
			// Let's be nice and make up a name.
			scenarioName = "Scenario for " + service.getServiceName()
					+ "(name auto-generated)";
		}
		// ***************** HACK A ****************
		newName = scenarioName;
		// *****************************************

		scenario.setScenarioName(scenarioName);

		if (req.getParameter("tag") != null) {
			scenario.setTag(req.getParameter("tag"));
		}
		
		if (req.getParameter("hangTime") != null) {
			try{
				String v = req.getParameter("hangTime");
				int hangtime = Integer.parseInt(v);
				scenario.setHangTime(hangtime);
			}catch(Exception e)
			{
				scenario.setHangTime(0);
			}
			
		}

		if (req.getParameter("httpResponseStatusCode") != null) {
			try {
				String v = req.getParameter("httpResponseStatusCode");
				int statusCodeVal = Integer.parseInt(v);
				scenario.setHttpResponseStatusCode(statusCodeVal);
			} catch (Exception e) {

			}
		}
		
		if (req.getParameter("httpMethodType") != null) {
			try {
				String v = req.getParameter("httpMethodType");
				scenario.setHttpMethodType(v);
			} catch (Exception e) {

			}
		}

		if (req.getParameter("responseHeader") != null) {
			String responseHeader = req.getParameter("responseHeader");
			if (responseHeader != null) {
				scenario.setResponseHeader(responseHeader);
			}

		}
		
		
		String respMessage = req.getParameter("responseMessage");
		if (respMessage != null) {
			scenario.setResponseMessage(respMessage);
		}
		if (req.getParameter("matchStringArg") != null) {
			scenario.setMatchStringArg(req.getParameter("matchStringArg"));
		}

		String matchArgAsRegexBoolVal = req
				.getParameter("matchStringArgEvaluationRulesFlag");
		if (matchArgAsRegexBoolVal != null) {
			try {
				scenario.setMatchStringArgEvaluationRulesFlag(Boolean
						.parseBoolean(matchArgAsRegexBoolVal));
			} catch (Exception t) {
				logger.error(
						"Unable to parse the Scenario match-to-be-used-as-a-regex flag, which should be 'true' or 'false' but was  "
								+ matchArgAsRegexBoolVal, t);
			}
		}

		// VALIDATION
		Map<String, String> errorMap = ScenarioValidator.validate(scenario);

		if ((errorMap != null) && (errorMap.size() == 0)) {

			// If creating a Scenario, then the returned scenario
			// will now have an id. If updating scenario, then
			// scenario ID remains the same.
			scenario = service.saveOrUpdateScenario(scenario);

			// Make this the default 'error response' scenario
			// for the service
			String v = req.getParameter("errorScenario");
			if (v != null && "true".equalsIgnoreCase(v.trim())) {
				service.setErrorScenarioId(scenario.getId());
			} else if (service.getErrorScenarioId() == scenario.getId()) {
				service.setErrorScenarioId(null);
			}

			// Make this the default universal 'error response',
			// for all services defined in Mockey.
			v = req.getParameter("universalErrorScenario");
			if (v != null && "true".equalsIgnoreCase(v.trim())) {
				ScenarioRef scenarioRef = new ScenarioRef(scenario.getId(),
						scenario.getServiceId());
				store.setUniversalErrorScenarioRef(scenarioRef);

			} else if (store.getUniversalErrorScenario() != null) {
				store.setUniversalErrorScenarioRef(null);
			}

			store.saveOrUpdateService(service);

			// ***************** HACK A ****************
			if (newName != null && oldName != null
					&& !oldName.trim().equals(newName.trim())) {
				// OK, we had an existing Service Scenario with a name change.
				// Let's update the appropriate Service Plan.
				store.updateServicePlansWithNewScenarioName(serviceId, oldName, newName);
				
			}
			// *****************************************
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();

			JSONObject object = new JSONObject();
			JSONObject resultObject = new JSONObject();
			try {

				object.put("success", "Scenario updated");
				object.put("scenarioId", scenario.getId().toString());
				object.put("serviceId", service.getId().toString());
				resultObject.put("result", object);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			out.println(resultObject);
			out.flush();
			out.close();
			return;

		} else {
			// ERROR STATE
			// Something is wrong with the input values.
			// Scenario is not created or updated.
			// Coaching messages are available in the
			// error dictionary/map.
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			String resultingJSON = Util.getJSON(errorMap);
			out.println(resultingJSON);
			out.flush();
			out.close();
			return;
		}
	}
}
