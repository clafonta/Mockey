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
import javax.servlet.ServletContext;
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
import com.mockey.model.Url;
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
public class ServicePlanSetupServlet extends HttpServlet {

	private static final long serialVersionUID = -2964632050151431391L;

	private Log log = LogFactory.getLog(ServicePlanSetupServlet.class);

	private IMockeyStorage store = StorageRegistry.MockeyStorage;
	private IApiStorage apiStore = IApiStorageInMemory.getInstance();
	public static final String API_SETPLAN_SERVICE_NAME = "Service Plan (Save, Delete, Set)";
	private static final String API_SETPLAN_PARAMETER_ACTION = "action";
	private static final String API_SETPLAN_PARAMETER_TYPE = "type";

	private static final String API_SETPLAN_PARAMETER_PLAN_ID = "plan_id";
	private static final String API_ACTION_DELETE_PLAN = "delete_plan";
	private static final String API_ACTION_SAVE_PLAN = "save_plan";
	private static final String API_ACTION_SET_PLAN = "set_plan";

	/**
	 * Loads up the HTTP API Documentation in memory for this service. The HTTP
	 * API information to describe this servlet's REQUEST and RESPONSE messaging
	 * is displayed to the end user via the Service API help page.
	 */
	public void init() throws ServletException {
		// THIS SERVICE API DESCRIPTION CONTRACT

		if (apiStore.getApiDocServiceByName(API_SETPLAN_SERVICE_NAME) == null) {
			ApiDocService apiDocService = new ApiDocService();
			apiDocService.setName(API_SETPLAN_SERVICE_NAME);
			ServletContext context = getServletContext();
			String contextRoot = context.getContextPath();
			apiDocService.setServicePath(Url.getContextAwarePath("/plan/setup?type=json&action=set_plan&plan_id=", contextRoot));
			
			// *****************************
			// REQUEST DEFINITION
			// *****************************

			ApiDocRequest apiDocRequest = new ApiDocRequest();

			// Parameter - 'action'
			ApiDocAttribute reqAttributeAction = new ApiDocAttribute();
			reqAttributeAction.setFieldName(API_SETPLAN_PARAMETER_ACTION);
			reqAttributeAction.addFieldValues(new ApiDocFieldValue(API_ACTION_DELETE_PLAN,
					"Delete the service plan definition given a valid plan_id parameter."));
			reqAttributeAction.addFieldValues(new ApiDocFieldValue(API_ACTION_SAVE_PLAN,
					"Saves current configuration settings as a service plan definition."));
			reqAttributeAction.addFieldValues(new ApiDocFieldValue(API_ACTION_SET_PLAN,
					"Sets a service plan given a valid plan_id parameter."));
			apiDocRequest.addAttribute(reqAttributeAction);
			// Parameter - 'plan_id'
			ApiDocAttribute reqAttributePlanId = new ApiDocAttribute();
			reqAttributePlanId.setFieldName(API_SETPLAN_PARAMETER_PLAN_ID);
			reqAttributePlanId.addFieldValues(new ApiDocFieldValue("[identifier]", "A valid service plan identifier."));
			reqAttributeAction.setExample("123");
			apiDocRequest.addAttribute(reqAttributePlanId);

			// Parameter - 'type'
			ApiDocAttribute reqAttributeType = new ApiDocAttribute();
			reqAttributeType.setFieldName(API_SETPLAN_PARAMETER_TYPE);
			reqAttributeType.addFieldValues(new ApiDocFieldValue("json",
					"Response will be in JSON. Any other value for 'type' is undefined and you may experience a 302."));
			apiDocRequest.addAttribute(reqAttributeType);
			apiDocService.setApiRequest(apiDocRequest);

			// *****************************
			// RESPONSE DEFINITION
			// *****************************

			ApiDocResponse apiResponse = new ApiDocResponse();
			// Building a JSON RESPONSE example
			try {
				JSONObject jsonResponseObject = new JSONObject();
				JSONObject jsonResultObject = new JSONObject();
				jsonResultObject.put("success",
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
			resAttributeSuccess.setFieldDescription("Successfully set, deleted, or saved a plan.  You get 'fail' or 'success', not both.");
			apiResponse.addAttribute(resAttributeSuccess);
			
			ApiDocAttribute resAttributeFail = new ApiDocAttribute();
			resAttributeFail.setFieldName("fail");
			resAttributeFail.setFieldDescription("Failed to set, delete, or save a plan. You get 'fail' or 'success', not both.");
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
			log.debug("Service Plan setup/delete");
			ServicePlan servicePlan = null;
			Long servicePlanId = null;
			List<Service> allServices = store.getServices();
			try {
				servicePlanId = new Long(req.getParameter(API_SETPLAN_PARAMETER_PLAN_ID));
				servicePlan = store.getServicePlanById(servicePlanId);
			} catch (Exception e) {
				// Do nothing
			}
			JSONObject jsonResultObject = new JSONObject();

			String action = req.getParameter(API_SETPLAN_PARAMETER_ACTION);
			if (API_ACTION_DELETE_PLAN.equals(action)) {
				JSONObject jsonObject = new JSONObject();

				try {
					store.deleteServicePlan(servicePlan);
					jsonObject.put("success", "Service plan '" + servicePlan.getName() + "' deleted");
					jsonObject.put("planId", "" + servicePlan.getId());
					jsonObject.put("planName", "" + servicePlan.getName());
				} catch (Exception e) {

					jsonObject.put("fail", "Service plan not deleted. Please check your logs for insight.");

				}
				PrintWriter out = resp.getWriter();
				jsonResultObject.put("result", jsonObject);
				out.println(jsonResultObject.toString());
				out.flush();
				out.close();
				return;
			} else if (API_ACTION_SET_PLAN.equals(action) && servicePlan != null) {
				JSONObject jsonObject = new JSONObject();

				try {
					setPlan(servicePlan);
					String msg = "Service plan " + servicePlan.getName() + " set";
					jsonObject.put("success", msg);
					jsonObject.put("planid", "" + servicePlan.getId());
					jsonObject.put("planName", "" + servicePlan.getName());

					Util.saveSuccessMessage(msg, req); // For redirect
				} catch (Exception e) {
					jsonObject.put("fail", "Service plan not set. Please check your logs for insight.");
				}
				PrintWriter out = resp.getWriter();
				jsonResultObject.put("result", jsonObject);
				out.println(jsonResultObject.toString());
				out.flush();
				out.close();
				return;
			} else if (API_ACTION_SAVE_PLAN.equals(action)) {

				if (servicePlan == null) {
					servicePlan = new ServicePlan();
				}
				servicePlan.setName(req.getParameter("servicePlanName"));
				ServicePlan savedServicePlan = createOrUpdatePlan(servicePlan);
				PrintWriter out = resp.getWriter();
				// Map<String, String> successMap = new HashMap<String,
				// String>();
				String msg = "Service plan " + servicePlan.getName() + " saved";
				Util.saveSuccessMessage(msg, req); // For redirect
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

	private ServicePlan createOrUpdatePlan(ServicePlan servicePlan) {
		List<PlanItem> planItemList = new ArrayList<PlanItem>();
		for (Service service : store.getServices()) {
			PlanItem planItem = new PlanItem();
			planItem.setHangTime(service.getHangTime());
			planItem.setServiceId(service.getId());
			planItem.setScenarioId(service.getDefaultScenarioId());
			planItem.setServiceResponseType(service.getServiceResponseType());
			planItemList.add(planItem);

		}
		servicePlan.setPlanItemList(planItemList);
		return store.saveOrUpdateServicePlan(servicePlan);

	}

	private void setPlan(ServicePlan servicePlan) {
		if (servicePlan == null) {
			servicePlan = new ServicePlan();
		}
		for (PlanItem planItem : servicePlan.getPlanItemList()) {
			Service service = store.getServiceById(planItem.getServiceId());
			if (service != null) {
				service.setHangTime(planItem.getHangTime());
				service.setDefaultScenarioId(planItem.getScenarioId());
				service.setServiceResponseType(planItem.getServiceResponseType());
				store.saveOrUpdateService(service);
			}
		}
	}
}
