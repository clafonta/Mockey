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
package com.mockey.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
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

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

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
			PrintWriter out = resp.getWriter();
			Map<String, String> successMap = new HashMap<String, String>();
			String resultingJSON = Util.getJSON(successMap);
			out.println(resultingJSON);
			out.flush();
			out.close();
			return;
		}

		Scenario scenario = null;
		try {
			scenario = service.getScenario(new Long(req
					.getParameter("scenarioId")));
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
		scenario.setScenarioName(scenarioName);

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
			if (req.getParameter("errorScenario") != null) {
				service.setErrorScenarioId(scenario.getId());
			} else if (service.getErrorScenarioId() == scenario.getId()) {
				service.setErrorScenarioId(null);
			}

			// Make this the default universal 'error response',
			// for all services defined in Mockey.
			if (req.getParameter("universalErrorScenario") != null) {
				store.setUniversalErrorScenarioId(scenario.getId());
				store.setUniversalErrorServiceId(serviceId);

			} else if (store.getUniversalErrorScenario() != null
					&& store.getUniversalErrorScenario().getId() == scenario
							.getId()) {
				store.setUniversalErrorScenarioId(null);
				store.setUniversalErrorServiceId(null);
			}

			store.saveOrUpdateService(service);
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
			PrintWriter out = resp.getWriter();
			String resultingJSON = Util.getJSON(errorMap);
			out.println(resultingJSON);
			out.flush();
			out.close();
			return;
		}
	}
}
