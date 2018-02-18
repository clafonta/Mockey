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
 * This is here to help manage Tags on Services.
 * 
 * @author chad.lafontaine
 * 
 */
public class TagHelperServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2146692704092245457L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	private static Logger logger = Logger.getLogger(TagHelperServlet.class);
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
		String tag = req.getParameter("tag");

		JSONObject jsonObject = new JSONObject();
		try {

			// PERFORM ACTION (OPTIONAL)
			if ("filter_tag_on".equals(action)) {
				// Redirect to Home and SET as session FILTER, your tags.
				String currentTagJumble = store.getGlobalStateSystemFilterTag();
				StringBuilder updatedTagJumble = new StringBuilder();
				if(currentTagJumble!=null) {
					boolean foundTag = false;
					String[] tagList = currentTagJumble.split("\\s+");
					for(String tagItem : tagList){
						if(tagItem!=null && tagItem.trim().equalsIgnoreCase(tag)){
							foundTag = true;
						}else {
							updatedTagJumble.append(tagItem + " ");
						}
					}
					if(!foundTag){
						updatedTagJumble.append(tag + " ");
					}
					store.setGlobalStateSystemFilterTag(updatedTagJumble.toString());
					
				}else {
					store.setGlobalStateSystemFilterTag(tag);
				}



				
				jsonObject.put("success", "Filter by tag is on.");
			} else if ("filter_tag_off".equals(action)) {
				// Redirect to Home and SET as session FILTER, your tags.
				store.setGlobalStateSystemFilterTag(null);
				jsonObject.put("success", "Filter by tag is off.");
			} else if ("filter_status".equals(action)) {
				// Redirect to Home and SET as session FILTER, your tags.
				String filter = (String) req.getSession().getAttribute(
						FILTER_TAG);
				if (filter != null && filter.trim().length() > 0) {
					jsonObject.put("filter", filter.trim());
					jsonObject.put("status", "on");
				} else {
					jsonObject.put("filter", "");
					jsonObject.put("status", "off");
				}
			} else if ("delete_tag_from_store".equals(action)) {
				store.deleteTagFromStore(tag);
				jsonObject.put("success", "Deleted tag from all things.");

			} else if ("delete_tag_from_service".equals(action)) {
				Service service = store.getServiceById(new Long(serviceId));

				service.removeTagFromList(tag);
				store.saveOrUpdateService(service);
				jsonObject.put("success", "Deleted tag from Service.");

			} else if ("delete_tag_from_service_plan".equals(action)) {
				ServicePlan servicePlan = store.getServicePlanById(new Long(
						servicePlanId));

				servicePlan.removeTagFromList(tag);
				store.saveOrUpdateServicePlan(servicePlan);

				jsonObject.put("success", "Deleted tag from Service Plan.");

			} else if ("delete_tag_from_scenario".equals(action)) {
				Service service = store.getServiceById(new Long(serviceId));

				Scenario scenario = service.getScenario(new Long(scenarioId));
				scenario.removeTagFromList(tag);
				service.saveOrUpdateScenario(scenario);
				store.saveOrUpdateService(service);
				jsonObject.put("success", "Deleted tag from Scenario.");

			} else if ("update_service_tag".equals(action)) {
				Service service = store.getServiceById(new Long(serviceId));

				service.clearTagList();
				service.addTagToList(tag);
				store.saveOrUpdateService(service);
				jsonObject.put("success", "Updated tag(s) for this Service.");

			} else if ("update_scenario_tag".equals(action)) {
				Service service = store.getServiceById(new Long(serviceId));

				Scenario scenario = service.getScenario(new Long(scenarioId));
				scenario.clearTagList();
				scenario.addTagToList(tag);
				service.saveOrUpdateScenario(scenario);
				store.saveOrUpdateService(service);
				jsonObject.put("success", "Updated tag(s) for this Scenario.");

			}

			// PRESENT STATE
			//
			// OK, now that things are up to date (if any action occurred),
			// let's present the state in the JSON
			// Why get the Service again? Because, we could have removed/edited
			// the tag information from one of the steps above.
			if (serviceId != null) {
				Service service = store.getServiceById(new Long(serviceId));
				jsonObject.put("serviceId", "" + serviceId);

				if (scenarioId != null) {
					Scenario scenario = service
							.getScenario(new Long(scenarioId));
					jsonObject.put("scenarioId", "" + scenario.getId());
					jsonObject.put("tag", "" + scenario.getTag());
				} else {
					jsonObject.put("tag", "" + service.getTag());
				}
			}

		} catch (Exception e) {
			logger.debug("Unable to manage tag '" + tag + "' with action '"
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
