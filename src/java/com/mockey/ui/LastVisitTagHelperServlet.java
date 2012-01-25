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
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * This is here to help manage last visit tags on Services, Scenarios, and
 * Service Plans
 * 
 * @author chad.lafontaine
 * 
 */
public class LastVisitTagHelperServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6008311413723842054L;
	/**
	 * 
	 */
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	private static Logger logger = Logger
			.getLogger(LastVisitTagHelperServlet.class);
	public static final String FILTER_TAG = "FILTER_SESSION_TAG";

	/**
	 * Service does a few things, which includes:
	 * 
	 */
	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String serviceId = req.getParameter("serviceId");
		String servicePlanId = req.getParameter("servicePlanId");
		String scenarioId = req.getParameter("scenarioId");
		String action = req.getParameter("action");

		JSONObject jsonObject = new JSONObject();
		try {
			if ("clear_last_visit".equalsIgnoreCase(action)) {
				if (serviceId != null && scenarioId != null) {
					Service service = store.getServiceById(new Long(serviceId));
					Scenario scenario = service
							.getScenario(new Long(scenarioId));
					scenario.setLastVisit(null);
					service.saveOrUpdateScenario(scenario);
					store.saveOrUpdateService(service);

				} else if (serviceId != null) {
					Service service = store.getServiceById(new Long(serviceId));
					service.setLastVisit(null);
					store.saveOrUpdateService(service);
				} else if (servicePlanId != null) {
					ServicePlan servicePlan = store
							.getServicePlanById(new Long(servicePlanId));
					servicePlan.setLastVisit(null);
					store.saveOrUpdateServicePlan(servicePlan);
				}

				jsonObject.put("success", "Last visit was cleared.");
			}else {
				jsonObject.put("info", "Hmm...you seem to be missing some things. ");
			}

		} catch (Exception e) {
			logger.debug("Unable to clear last visit time with action '"
					+ action + "' :" + e.getMessage());
			try {
				jsonObject.put("error", "" + "Sorry, not available.");
			} catch (JSONException e1) {
				logger.debug("What happended?" + e1.getMessage());
			}
		}

		resp.setContentType("application/json");

		PrintStream out = new PrintStream(resp.getOutputStream());

		out.println(jsonObject.toString());

		return;
	}
}
