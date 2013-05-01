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
import java.util.Calendar;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.model.ApiDocAttribute;
import com.mockey.model.ApiDocFieldValue;
import com.mockey.model.ApiDocRequest;
import com.mockey.model.ApiDocResponse;
import com.mockey.model.ApiDocService;
import com.mockey.model.PlanItem;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.storage.IApiStorage;
import com.mockey.storage.IApiStorageInMemory;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Management of SAVE, DELETE, or SET for a Service Plan, in addition to HTTP
 * Documentation.
 * 
 * @author chadlafontaine
 * 
 */
public class ServicePlanSetupServlet extends HttpServlet implements ServicePlanConfigurationAPI {
	private static final long serialVersionUID = -2964632050151431391L;
	private Log log = LogFactory.getLog(ServicePlanSetupServlet.class);
	private IMockeyStorage store = StorageRegistry.MockeyStorage;
	private IApiStorage apiStore = IApiStorageInMemory.getInstance();

	/**
	 * Loads up the HTTP API Documentation in memory for this service. The HTTP
	 * API information to describe this servlet's REQUEST and RESPONSE messaging
	 * is displayed to the end user via the Service API help page.
	 */
	public void init() throws ServletException {
		// *****************************
		// THIS SERVICE API DESCRIPTION CONTRACT
		// *****************************
		// This information is used in the API JSP document, used to describe
		// how to make setting changes from a head-less client.

		if (apiStore.getApiDocServiceByName(API_SERVICE_PLAN_CONFIGURATION_NAME) == null) {
			ApiDocService apiDocService = new ApiDocService();
			apiDocService.setName(API_SERVICE_PLAN_CONFIGURATION_NAME);
			apiDocService
					.setDescription("If you need Mockey to load a specific Service Plan, then this API may meet your needs. When to use: your automated test scripts need Mockey in a certain state of mind.");
			// TODO: We need to use a pattern matching replace e.g. ${0} ${1}
			// with array ["a", "b"] for VALUES
			apiDocService.setServicePath("/plan/setup");

			// *****************************
			// REQUEST DEFINITION
			// *****************************

			ApiDocRequest apiDocRequest = new ApiDocRequest();

			// Parameter - 'action'
			ApiDocAttribute reqAttributeAction = new ApiDocAttribute();
			reqAttributeAction.setFieldName(API_SETPLAN_PARAMETER_ACTION);
			reqAttributeAction.addFieldValues(new ApiDocFieldValue(API_SETPLAN_PARAMETER_ACTION_VALUE_DELETE_PLAN,
					"Delete the service plan definition given a valid plan_id parameter."));
			reqAttributeAction.addFieldValues(new ApiDocFieldValue(API_SETPLAN_PARAMETER_ACTION_VALUE_SAVE_PLAN,
					"Saves current configuration settings as a service plan definition."));
			reqAttributeAction.addFieldValues(new ApiDocFieldValue(API_SETPLAN_PARAMETER_ACTION_VALUE_SET_PLAN,
					"Sets a service plan given a valid plan_id parameter."));
			reqAttributeAction.addFieldValues(new ApiDocFieldValue(API_SETPLAN_PARAMETER_ACTION_VALUE_SET_AS_DEFAULT_PLAN,
					"Sets a service plan to be set as the default state upon a Mockey startup. Set " + API_SETPLAN_PARAMETER_PLAN_ID
					+" to 'none' if no desired default plan is to be set."));
			
			apiDocRequest.addAttribute(reqAttributeAction);

			// Parameter - 'plan_id'
			ApiDocAttribute reqAttributePlanId = new ApiDocAttribute();
			reqAttributePlanId.setFieldName(API_SETPLAN_PARAMETER_PLAN_ID);
			reqAttributePlanId.addFieldValues(new ApiDocFieldValue("[identifier]", "A valid service plan identifier."));
			reqAttributePlanId.setExample("123");
			apiDocRequest.addAttribute(reqAttributePlanId);

			// Parameter - 'service_plan_name'
			ApiDocAttribute reqAttributePlanName = new ApiDocAttribute();
			reqAttributePlanName.setFieldName(API_SET_SAVE_OR_UPDATE_PARAMETER_PLAN_NAME);
			reqAttributePlanName
					.addFieldValues(new ApiDocFieldValue(
							"[string]",
							"The service plan name needed to create or save/update. If plan_id not provided, then this value is used to locate the service plan for setting or updating."));
			reqAttributePlanName.setExample("The Gold Service Plan");
			apiDocRequest.addAttribute(reqAttributePlanName);

			// Parameter - 'service_plan_name'
			ApiDocAttribute reqAttributePlanTag = new ApiDocAttribute();
			reqAttributePlanTag.setFieldName(API_SET_SAVE_OR_UPDATE_PARAMETER_PLAN_TAG);
			reqAttributePlanTag.addFieldValues(new ApiDocFieldValue("[string]", "The service plan tag(s)."));
			reqAttributePlanTag.setExample("Tag1 Tag2 Tag3");
			apiDocRequest.addAttribute(reqAttributePlanTag);

			// Parameter - 'type'
			ApiDocAttribute reqAttributeType = new ApiDocAttribute();
			reqAttributeType.setFieldName(API_SETPLAN_PARAMETER_TYPE);
			reqAttributeType.addFieldValues(new ApiDocFieldValue("json",
					"Response will be in JSON. Any other value for 'type' is undefined and you may experience a 302."));
			apiDocRequest.addAttribute(reqAttributeType);
			apiDocService.setApiRequest(apiDocRequest);

			// Parameter - 'type'
			ApiDocAttribute reqTransientState = new ApiDocAttribute();
			reqTransientState.setFieldName(API_TRANSIENT_STATE);
			reqTransientState
					.addFieldValues(new ApiDocFieldValue(
							"[boolean]",
							"If available and set to 'true', then all settings in this call will be in-memory only, not persisted to the file system. Otherwise, state settings will be written to the file system."));
			apiDocRequest.addAttribute(reqTransientState);
			apiDocService.setApiRequest(apiDocRequest);

			// *****************************
			// RESPONSE DEFINITION
			// *****************************

			ApiDocResponse apiResponse = new ApiDocResponse();
			// Building a JSON RESPONSE example
			try {
				JSONObject jsonResponseObject = new JSONObject();
				JSONObject jsonResultObject = new JSONObject();
				jsonResultObject
						.put("success",
								"Some informative coaching message. If success isn't a value, then maybe you have a 'fail' message.");
				jsonResultObject.put("planId", "1234");
				jsonResultObject.put("planName", "Some service name");
				jsonResponseObject.put("result", jsonResultObject);
				apiResponse.setExample(jsonResponseObject.toString());
			} catch (Exception e) {
				log.error("Unabel to build a sample JSON message. ", e);
			}

			// Response attribute 'planId'
			ApiDocAttribute resAttributePlanId = new ApiDocAttribute();
			resAttributePlanId.setFieldName("planId");
			resAttributePlanId.setFieldDescription("Identifier of a Service Plan");
			apiResponse.addAttribute(resAttributePlanId);

			// Response attribute 'planName'
			ApiDocAttribute resAttributePlanName = new ApiDocAttribute();
			resAttributePlanName.setFieldName("planName");
			resAttributePlanName.setFieldDescription("Name of a Service Plan");
			apiResponse.addAttribute(resAttributePlanName);

			// Response attribute 'success'
			ApiDocAttribute resAttributeSuccess = new ApiDocAttribute();
			resAttributeSuccess.setFieldName("success");
			resAttributeSuccess
					.setFieldDescription("Successfully set, deleted, or saved a plan.  You get 'fail' or 'success', not both.");
			apiResponse.addAttribute(resAttributeSuccess);

			ApiDocAttribute resAttributeFail = new ApiDocAttribute();
			resAttributeFail.setFieldName("fail");
			resAttributeFail
					.setFieldDescription("Failed to set, delete, or save a plan. You get 'fail' or 'success', not both.");
			apiResponse.addAttribute(resAttributeFail);

			apiDocService.setApiResponse(apiResponse);
			apiStore.saveOrUpdateService(apiDocService);
		}
	}

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

		try {
			// API BUSINESS LOGIC
			// log.debug("Service Plan setup/delete");
			ServicePlan servicePlan = null;
			Long servicePlanId = null;
			String servicePlanIdAsString = req.getParameter(API_SETPLAN_PARAMETER_PLAN_ID);
			List<Service> allServices = store.getServices();
			// *********************
			// BEST EFFORT HERE.
			// We try to find the service by ID.
			// If not found, we try by NAME.
			// Otherwise, let the rest of the logic do its thing.
			// *********************

			try {
				servicePlanId = new Long(servicePlanIdAsString);
				servicePlan = store.getServicePlanById(servicePlanId);
			} catch (Exception e) {
				if (req.getParameter(API_SETPLAN_PARAMETER_PLAN_ID) != null) {
					log.debug("No service plan with ID '" + req.getParameter(API_SETPLAN_PARAMETER_PLAN_ID)
							+ "' found.", e);
				}
			}
			if (servicePlan == null) {
				try {
					String servicePlanName = req.getParameter(API_SET_SAVE_OR_UPDATE_PARAMETER_PLAN_NAME);
					
					servicePlan = store.getServicePlanByName(servicePlanName.trim());
				} catch (Exception e) {
					if (req.getParameter(API_SET_SAVE_OR_UPDATE_PARAMETER_PLAN_NAME) != null) {
						log.debug(
								"No service plan with NAME '"
										+ req.getParameter(API_SET_SAVE_OR_UPDATE_PARAMETER_PLAN_NAME) + "' found.", e);
					}
				}
			}

			JSONObject jsonResultObject = new JSONObject();

			String action = req.getParameter(API_SETPLAN_PARAMETER_ACTION);
			String transientState = req.getParameter(API_TRANSIENT_STATE);
			try {
				if (transientState != null) {
					servicePlan.setTransientState(new Boolean(transientState));
				}
			} catch (Exception e) {
				log.debug("ServicePlan not set to transient state but a value was given as: " + transientState);

			}
			if (API_SETPLAN_PARAMETER_ACTION_VALUE_DELETE_PLAN.equals(action)) {
				JSONObject jsonObject = new JSONObject();

				try {
					store.deleteServicePlan(servicePlan);
					jsonObject.put("success", "Service plan '" + servicePlan.getName() + "' deleted");
					jsonObject.put("planId", "" + servicePlan.getId());
					jsonObject.put("planName", "" + servicePlan.getName());
				} catch (Exception e) {

					jsonObject.put("fail", "Service plan not deleted. Please check your logs for insight.");

				}
				resp.setContentType("application/json");
				PrintWriter out = resp.getWriter();
				jsonResultObject.put("result", jsonObject);
				out.println(jsonResultObject.toString());
				out.flush();
				out.close();
				return;
			} else if (API_SETPLAN_PARAMETER_ACTION_VALUE_SET_PLAN.equals(action) && servicePlan != null) {
				JSONObject jsonObject = new JSONObject();

				try {
					
					store.setServicePlan(servicePlan);
					String msg = "Service plan " + servicePlan.getName() + " set";
					jsonObject.put("success", msg);
					jsonObject.put("planid", "" + servicePlan.getId());
					jsonObject.put("planName", "" + servicePlan.getName());

					Util.saveSuccessMessage(msg, req); // For redirect
				} catch (Exception e) {
					jsonObject.put("fail", "Service plan not set. Please check your logs for insight.");
				}
				resp.setContentType("application/json");
				PrintWriter out = resp.getWriter();
				jsonResultObject.put("result", jsonObject);
				out.println(jsonResultObject.toString());
				out.flush();
				out.close();
				return;
				
			}
			else if(API_SETPLAN_PARAMETER_ACTION_VALUE_SET_AS_DEFAULT_PLAN.equals(action)){
				PrintWriter out = resp.getWriter();
				JSONObject jsonObject = new JSONObject();
				if("none".equalsIgnoreCase(servicePlanIdAsString)){
					store.setDefaultServicePlanId(null);
					// JSON response
					jsonObject.put("success", "Removed default plan.");
				}
				else if(servicePlan!=null && servicePlan.getId()!=null){
					store.setDefaultServicePlanId(servicePlan.getId().toString());
					// JSON response
					jsonObject.put("success", "Set as Default Service Plan");
					jsonObject.put("planid", "" + servicePlan.getId());
					jsonObject.put("planName", "" + servicePlan.getName());
					
				}else {
					jsonObject.put("fail", "Unable to set Default Service Plan. Unknown Service Plan ID.");
				}
				jsonResultObject.put("result", jsonObject);
				out.println(jsonResultObject.toString());
				out.flush();
				out.close();
				return;
				
			}
			else if (API_SETPLAN_PARAMETER_ACTION_VALUE_SAVE_PLAN.equals(action)) {

				if (servicePlan == null) {
					servicePlan = new ServicePlan();
				}

				String[] serviceIds = req.getParameterValues("service_ids[]");

				// ***************************
				// LET'S PREVENT EMPTY PLAN NAMES
				// ***************************

				String servicePlanName = req.getParameter(API_SET_SAVE_OR_UPDATE_PARAMETER_PLAN_NAME);
				String servicePlanTag = req.getParameter(API_SET_SAVE_OR_UPDATE_PARAMETER_PLAN_TAG);
				if (servicePlanName == null) {
					// If possible, carry over the name from an existing Plan.
					servicePlanName = servicePlan.getName();
				}
				// If all fails, inject a name.
				if (servicePlanName == null || servicePlanName.trim().length() == 0) {
					servicePlanName = "Plan (auto-generated-name)";
				}
				servicePlan.setName(servicePlanName.trim());

				if (servicePlanTag != null) {
					servicePlan.setTag(servicePlanTag);
				}
				// ***************************
				// SAVE/UPDATE THE PLAN
				// ***************************
				ServicePlan savedServicePlan = createOrUpdatePlan(servicePlan, serviceIds);

				// ***************************
				// SAVE/UPDATE THE PLAN
				// ***************************
				resp.setContentType("application/json");
				PrintWriter out = resp.getWriter();
				String msg = "Service plan " + servicePlan.getName() + " saved";

				// HACK: For redirect IF JavaScript decides to (if type is not
				// JSON)
				if (!"json".equalsIgnoreCase(req.getParameter(API_SETPLAN_PARAMETER_TYPE))) {
					Util.saveSuccessMessage(msg, req);
				}
				// JSON response
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("success", msg);
				jsonObject.put("planid", "" + savedServicePlan.getId());
				jsonObject.put("planName", "" + savedServicePlan.getName());
				jsonResultObject.put("result", jsonObject);
				out.println(jsonResultObject.toString());
				out.flush();
				out.close();
				return;
			}

			req.setAttribute("services", allServices);
			req.setAttribute("plans", store.getServicePlans());
			RequestDispatcher dispatch = req.getRequestDispatcher("/home.jsp");
			dispatch.forward(req, resp);
		} catch (JSONException jsonException) {
			throw new ServletException(jsonException);
		}
	}

	/**
	 * 
	 * @param servicePlan
	 * @param serviceIdArray
	 *            list of Service IDs that will be included in the plan.
	 * @return
	 */
	private ServicePlan createOrUpdatePlan(ServicePlan servicePlan, String[] serviceIdArray) {
		List<PlanItem> planItemList = new ArrayList<PlanItem>();
		if (serviceIdArray != null) {
			for (String serviceId : serviceIdArray) {

				Service service = store.getServiceById(new Long(serviceId));

				PlanItem planItem = new PlanItem();
				planItem.setHangTime(service.getHangTime());
				planItem.setServiceName(service.getServiceName());

				planItem.setScenarioName(service.getDefaultScenarioName());
				planItem.setServiceResponseType(service.getServiceResponseType());
				planItemList.add(planItem);

			}
		}
		servicePlan.setPlanItemList(planItemList);
		servicePlan.setLastVisit(new Long(Calendar.getInstance().getTimeInMillis()));

		return store.saveOrUpdateServicePlan(servicePlan);

	}

	
}
