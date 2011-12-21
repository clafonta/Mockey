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

import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.ScenarioValidator;
import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class ScenarioServlet extends HttpServlet {

	private static final long serialVersionUID = -5920793024759540668L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

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
		if (req.getParameter("deleteScenario") != null && serviceId != null && scenarioId != null) {
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
		try {
			scenario = service.getScenario(new Long(req.getParameter("scenarioId")));
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
			scenarioName = "Scenario for " + service.getServiceName() + "(name auto-generated)";
		}
		scenario.setScenarioName(scenarioName);

		if (req.getParameter("tag") != null) {
			scenario.setTag(req.getParameter("tag"));
		}
		
		if (req.getParameter("responseMessage") != null) {
			scenario.setResponseMessage(req.getParameter("responseMessage"));
		}
		if (req.getParameter("matchStringArg") != null) {
			scenario.setMatchStringArg(req.getParameter("matchStringArg"));
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
			if ( v != null && "true".equalsIgnoreCase(v.trim())) {
				service.setErrorScenarioId(scenario.getId());
			} else if (service.getErrorScenarioId() == scenario.getId()) {
				service.setErrorScenarioId(null);
			}

			// Make this the default universal 'error response',
			// for all services defined in Mockey.
			v = req.getParameter("universalErrorScenario");
			if (v != null && "true".equalsIgnoreCase(v.trim())) {
				store.setUniversalErrorScenarioId(scenario.getId());
				store.setUniversalErrorServiceId(serviceId);

			} else if (store.getUniversalErrorScenarioId() != null
					&& store.getUniversalErrorScenarioId() == scenario.getId()) {
				store.setUniversalErrorScenarioId(null);
				store.setUniversalErrorServiceId(null);
			}

			store.saveOrUpdateService(service);
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
