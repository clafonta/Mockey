/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2010  Authors:
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
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.mockey.model.ApiDocAttribute;
import com.mockey.model.ApiDocFieldValue;
import com.mockey.model.ApiDocRequest;
import com.mockey.model.ApiDocResponse;
import com.mockey.model.ApiDocService;
import com.mockey.model.Service;
import com.mockey.storage.IApiStorage;
import com.mockey.storage.IApiStorageInMemory;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Management of Service configuration, in addition to HTTP Documentation.
 * 
 * @author chadlafontaine
 * 
 */
public class ServiceConfigurationServlet extends HttpServlet {

	private static final long serialVersionUID = 7762196322218894996L;

	private Logger log = Logger.getLogger(ServiceConfigurationServlet.class);

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

		if (apiStore
				.getApiDocServiceByName(ServiceConfigurationAPI.API_SERVICE_CONFIGURATION_NAME) == null) {
			ApiDocService apiDocService = new ApiDocService();
			apiDocService
					.setName(ServiceConfigurationAPI.API_SERVICE_CONFIGURATION_NAME);
			// TODO: We need to use a pattern matching replace e.g. ${0} ${1}
			// with array ["a", "b"] for VALUES
			apiDocService.setServicePath("/config/service");
			apiDocService
					.setDescription("If you need to configure Mockey services without a web browser (e.g. bots), then this API may serve your needs. ");

			// *****************************
			// REQUEST DEFINITION
			// *****************************

			ApiDocRequest apiDocRequest = new ApiDocRequest();

			ApiDocAttribute reqServiceId = new ApiDocAttribute();
			reqServiceId.setFieldName(ServiceConfigurationAPI.API_SERVICE_ID);
			reqServiceId.addFieldValues(new ApiDocFieldValue("[identifier]",
					"A valid service identifier."));
			reqServiceId.setExample("123");
			apiDocRequest.addAttribute(reqServiceId);

			ApiDocAttribute reqServiceName = new ApiDocAttribute();
			reqServiceName
					.setFieldName(ServiceConfigurationAPI.API_SERVICE_NAME);
			reqServiceName.addFieldValues(new ApiDocFieldValue("[string]",
					"A valid service name."));
			reqServiceName.setExample("My Service Name");
			apiDocRequest.addAttribute(reqServiceName);

			// SCHEMA
			ApiDocAttribute reqServiceSchema = new ApiDocAttribute();
			reqServiceSchema
					.setFieldName(ServiceConfigurationAPI.API_SERVICE_SCHEMA);
			reqServiceSchema.addFieldValues(new ApiDocFieldValue("[string]",
					"A valid JSON Schema definition."));
			reqServiceSchema
					.setExample("{\"type\":\"object\",\"$schema\": \"http://json-schema.org/draft-03/schema\",\"id\": \"#\",\"required\":false,\"properties\":{ \"address\": { \"type\":\"object\", \"id\": \"address\", \"required\":false, \"properties\":{ \"streetAddress\": { \"type\":\"string\", \"id\": \"streetAddress\", \"required\":false } } } }}");
			apiDocRequest.addAttribute(reqServiceSchema);

			ApiDocAttribute reqServiceSchemaEnableFlag = new ApiDocAttribute();
			reqServiceSchemaEnableFlag
					.setFieldName(ServiceConfigurationAPI.API_SERVICE_SCHEMA_ENABLE_FLAG);
			reqServiceSchemaEnableFlag
					.addFieldValues(new ApiDocFieldValue(
							"[boolean]",
							"Set to true for the service to validate each Service Scenario JSON response with the provided JSON Schema."));
			reqServiceSchemaEnableFlag.setExample("true");
			apiDocRequest.addAttribute(reqServiceSchemaEnableFlag);

			// REQUEST INSPECTOR RULES
			ApiDocAttribute reqInspectorRules = new ApiDocAttribute();
			reqInspectorRules
					.setFieldName(ServiceConfigurationAPI.API_SERVICE_REQUEST_INSPECTOR_RULES);
			reqInspectorRules.addFieldValues(new ApiDocFieldValue("[string]",
					"Request evaluation rules in JSON. "));
			reqInspectorRules.setExample("");
			apiDocRequest.addAttribute(reqInspectorRules);

			ApiDocAttribute reqInspectorRulesEnableFlag = new ApiDocAttribute();
			reqInspectorRulesEnableFlag
					.setFieldName(ServiceConfigurationAPI.API_SERVICE_REQUEST_INSPECTOR_RULES_ENABLE_FLAG);
			reqInspectorRulesEnableFlag
					.addFieldValues(new ApiDocFieldValue(
							"[boolean]",
							"Set to true for the service to validate each incoming request to ensure the appropriate parameters are being passed."));
			reqInspectorRulesEnableFlag.setExample("true");
			apiDocRequest.addAttribute(reqInspectorRulesEnableFlag);

			ApiDocAttribute reqScenarioId = new ApiDocAttribute();
			reqScenarioId
					.setFieldName(ServiceConfigurationAPI.API_SERVICE_SCENARIO_ID);
			reqScenarioId.addFieldValues(new ApiDocFieldValue("[identifier]",
					"A valid service scenario identifier."));
			reqScenarioId.setExample("123");
			apiDocRequest.addAttribute(reqScenarioId);

			ApiDocAttribute reqScenarioName = new ApiDocAttribute();
			reqScenarioName
					.setFieldName(ServiceConfigurationAPI.API_SERVICE_NAME);
			reqScenarioName.addFieldValues(new ApiDocFieldValue("[string]",
					"A valid service scenario name."));
			reqScenarioName.setExample("My Service Scenario Name");
			apiDocRequest.addAttribute(reqScenarioName);

			ApiDocAttribute reqHangtime = new ApiDocAttribute();
			reqHangtime
					.setFieldName(ServiceConfigurationAPI.API_SERVICE_HANGTIME);
			reqHangtime.addFieldValues(new ApiDocFieldValue("[int]",
					"Hang time in milliseconds."));
			reqHangtime.setExample("500");
			apiDocRequest.addAttribute(reqHangtime);

			ApiDocAttribute transientSet = new ApiDocAttribute();
			transientSet
					.setFieldName(ServiceConfigurationAPI.API_TRANSIENT_STATE);
			transientSet
					.addFieldValues(new ApiDocFieldValue(
							"[boolean]",
							"If available and set to 'true', then all settings in this call will be in-memory only, not persisted to the file system. Otherwise, state settings will be written to the file system."));
			transientSet.setExample("true");
			apiDocRequest.addAttribute(transientSet);

			ApiDocAttribute reqAttributeAction = new ApiDocAttribute();
			reqAttributeAction
					.setFieldName(ServiceConfigurationAPI.API_SERVICE_RESPONSE_TYPE);
			reqAttributeAction
					.addFieldValues(new ApiDocFieldValue(
							ServiceConfigurationAPI.API_SERVICE_RESPONSE_TYPE_VALUE_DYNAMIC,
							"Sets service to respond as dynamic."));
			reqAttributeAction
					.addFieldValues(new ApiDocFieldValue(
							ServiceConfigurationAPI.API_SERVICE_RESPONSE_TYPE_VALUE_PROXY,
							"Sets service to act as a proxy."));
			reqAttributeAction
					.addFieldValues(new ApiDocFieldValue(
							ServiceConfigurationAPI.API_SERVICE_RESPONSE_TYPE_VALUE_STATIC,
							"Sets service to respond with a static response"));
			apiDocRequest.addAttribute(reqAttributeAction);

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
				jsonResultObject.put(ServiceConfigurationAPI.API_SERVICE_ID,
						"1234");
				jsonResultObject.put(ServiceConfigurationAPI.API_SERVICE_NAME,
						"Some service name");
				jsonResultObject.put(
						ServiceConfigurationAPI.API_SERVICE_SCHEMA,
						"JSON Schema");
				jsonResultObject
						.put(ServiceConfigurationAPI.API_SERVICE_SCENARIO_ID,
								"5678");
				jsonResultObject.put(
						ServiceConfigurationAPI.API_SERVICE_SCENARIO_NAME,
						"Some scenario name");
				jsonResultObject
						.put(ServiceConfigurationAPI.API_SERVICE_RESPONSE_TYPE,
								ServiceConfigurationAPI.API_SERVICE_RESPONSE_TYPE_VALUE_PROXY);
				jsonResultObject.put(
						ServiceConfigurationAPI.API_SERVICE_HANGTIME, "500");
				jsonResultObject.put(
						ServiceConfigurationAPI.API_TRANSIENT_STATE, "true");
				jsonResponseObject.put("result", jsonResultObject);
				apiResponse.setExample(jsonResponseObject.toString());
			} catch (Exception e) {
				log.error("Unabel to build a sample JSON message. ", e);
			}

			// Response attribute 'planId'
			ApiDocAttribute resAttributePlanId = new ApiDocAttribute();
			resAttributePlanId
					.setFieldName(ServiceConfigurationAPI.API_SERVICE_ID);
			resAttributePlanId.setFieldDescription("Identifier of a Service");
			apiResponse.addAttribute(resAttributePlanId);

			// Response attribute 'planName'
			ApiDocAttribute resAttributePlanName = new ApiDocAttribute();
			resAttributePlanName
					.setFieldName(ServiceConfigurationAPI.API_SERVICE_NAME);
			resAttributePlanName.setFieldDescription("Name of a Service");
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
	 */
	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String serviceId = req
				.getParameter(ServiceConfigurationAPI.API_SERVICE_ID);
		String serviceName = req
				.getParameter(ServiceConfigurationAPI.API_SERVICE_NAME);
		// SCHEMA
		String serviceResponseSchema = req
				.getParameter(ServiceConfigurationAPI.API_SERVICE_SCHEMA);
		String serviceResponseSchemaEnableFlag = req
				.getParameter(ServiceConfigurationAPI.API_SERVICE_SCHEMA_ENABLE_FLAG);

		// REQUEST Evaluations
		String reqInspectorRules = req
				.getParameter(ServiceConfigurationAPI.API_SERVICE_REQUEST_INSPECTOR_RULES);
		String reqInspectorRulesEnableFlag = req
				.getParameter(ServiceConfigurationAPI.API_SERVICE_REQUEST_INSPECTOR_RULES_ENABLE_FLAG);

		String hangTime = req
				.getParameter(ServiceConfigurationAPI.API_SERVICE_HANGTIME);
		String scenarioId = req
				.getParameter(ServiceConfigurationAPI.API_SERVICE_SCENARIO_ID);
		String scenarioName = req
				.getParameter(ServiceConfigurationAPI.API_SERVICE_SCENARIO_NAME);
		String requestInspectorName = req
				.getParameter(ServiceConfigurationAPI.API_SERVICE_REQUEST_INSPECTOR_NAME);
		String serviceResponseType = req
				.getParameter(ServiceConfigurationAPI.API_SERVICE_RESPONSE_TYPE);
		String defaultUrlIndex = req.getParameter("defaultUrlIndex");
		String transientState = req
				.getParameter(ServiceConfigurationAPI.API_TRANSIENT_STATE);
		Service service = null;
		JSONObject jsonResultObject = new JSONObject();

		if (serviceId != null) {
			service = store.getServiceById(new Long(serviceId));
		} else {
			service = store.getServiceByName(serviceName);
		}

		try {
			service.setServiceResponseTypeByString(serviceResponseType);

		} catch (Exception e) {
			log.debug("Updating service without a 'service response type' value");
		}

		try {
			int index = Integer.parseInt(defaultUrlIndex);

			service.setDefaultRealUrlIndex(index - 1);
		} catch (Exception e) {

		}

		try {
			if (requestInspectorName != null) {
				service.setRequestInspectorName(requestInspectorName);
			}
		} catch (Exception e) {
			log.debug("Updating service without a 'request Inspector Name' value although, one was given:"
					+ requestInspectorName);
		}

		// SCHEMA
		try {
			if (serviceResponseSchemaEnableFlag != null) {
				service.setResponseSchemaFlag(Boolean
						.valueOf(serviceResponseSchemaEnableFlag));
			}
		} catch (Exception e) {
			log.debug("Unable to set the Service JSON Schema enable flag. Non-null value given: "
					+ serviceResponseSchemaEnableFlag);
		}

		try {
			if (serviceResponseSchema != null) {
				service.setResponseSchema(serviceResponseSchema);
			}
		} catch (Exception e) {
			// Do nothing.
		}
		
		// ******************************
		// REQUEST Evaluation Rules
		// ******************************
		try {
			if (reqInspectorRulesEnableFlag != null) {
				service.setRequestInspectorJsonRulesEnableFlag(Boolean
						.valueOf(reqInspectorRulesEnableFlag));
			}
		} catch (Exception e) {
			log.debug("Unable to set the Service JSON Schema enable flag. Non-null value given: "
					+ serviceResponseSchemaEnableFlag);
		}
		try {
			if (reqInspectorRules != null) {
				service.setRequestInspectorJsonRules(reqInspectorRules);
			}
		} catch (Exception e) {
			// TODO: we should add JSON Schema to evaluate the rules. Right? 
		}

		try {
			if (hangTime != null) {
				service.setHangTime((new Integer(hangTime).intValue()));
			}
		} catch (Exception e) {
			log.debug("Updating service without a 'hang time' value");
		}

		try {
			if (transientState != null) {
				service.setTransientState((new Boolean(transientState)));
			}
		} catch (Exception e) {
			log.debug("Updating service without a 'transient state' value");
		}

		try {
			if (scenarioId != null) {
				service.setDefaultScenarioId(new Long(scenarioId));
			} else {
				service.setDefaultScenarioByName(scenarioName);
			}
		} catch (Exception e) {
			// Do nothing.
			log.debug("Updating service without a 'default scenario ID' value");
		}
		service = store.saveOrUpdateService(service);

		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();

		JSONObject jsonResponseObject = new JSONObject();
		try {

			if (service != null) {

				jsonResultObject.put("success", "updated");
				jsonResultObject.put(ServiceConfigurationAPI.API_SERVICE_NAME,
						service.getServiceName());
				jsonResultObject.put(ServiceConfigurationAPI.API_SERVICE_ID,
						service.getId());
				jsonResultObject.put(
						ServiceConfigurationAPI.API_SERVICE_SCENARIO_ID,
						service.getDefaultScenarioId());
				
				jsonResultObject.put(
						ServiceConfigurationAPI.API_SERVICE_SCHEMA_ENABLE_FLAG,
						service.isResponseSchemaFlag());
				
				jsonResultObject.put(
						ServiceConfigurationAPI.API_SERVICE_REQUEST_INSPECTOR_RULES_ENABLE_FLAG,
						service.isRequestInspectorJsonRulesEnableFlag());
				
				jsonResultObject.put(
						ServiceConfigurationAPI.API_SERVICE_SCENARIO_NAME,
						service.getDefaultScenarioName());
				jsonResultObject.put(
						ServiceConfigurationAPI.API_SERVICE_RESPONSE_TYPE,
						service.getServiceResponseTypeAsString());
				jsonResultObject.put(
						ServiceConfigurationAPI.API_SERVICE_HANGTIME,
						service.getHangTime());
				jsonResultObject
						.put(ServiceConfigurationAPI.API_SERVICE_REQUEST_INSPECTOR_NAME,
								service.getRequestInspectorName());
				jsonResponseObject.put("result", jsonResultObject);
			} else {

				StringBuffer outputInfo = new StringBuffer();
				outputInfo.append(ServiceConfigurationAPI.API_SERVICE_ID + ":"
						+ serviceId + " ");
				outputInfo.append(ServiceConfigurationAPI.API_SERVICE_NAME
						+ ":" + serviceName + " ");
				outputInfo.append(ServiceConfigurationAPI.API_SERVICE_HANGTIME
						+ ":" + hangTime + " ");
				outputInfo
						.append(ServiceConfigurationAPI.API_SERVICE_REQUEST_INSPECTOR_NAME
								+ ":" + requestInspectorName);
				outputInfo
						.append(ServiceConfigurationAPI.API_SERVICE_SCENARIO_ID
								+ ":" + scenarioId + " ");
				outputInfo
						.append(ServiceConfigurationAPI.API_SERVICE_SCENARIO_NAME
								+ ":" + scenarioName + " ");
				outputInfo
						.append(ServiceConfigurationAPI.API_SERVICE_RESPONSE_TYPE
								+ ":" + serviceResponseType + " ");
				outputInfo.append("defaultUrlIndex" + ":" + defaultUrlIndex
						+ " ");
				outputInfo.append(ServiceConfigurationAPI.API_TRANSIENT_STATE
						+ ":" + transientState + " ");

				jsonResultObject.put("fail",
						"Unable to update service configuration. ");
				jsonResultObject.put("info", outputInfo.toString());

			}
			out.println(jsonResponseObject.toString());
		} catch (Exception e) {
			log.error("Unable to build a JSON response. ", e);
			try {
				jsonResultObject.put("fail", "Unable to configure service.");
				jsonResponseObject.put("result", jsonResultObject);
				out.println(jsonResponseObject.toString());
			} catch (Exception ee) {
				log.error(
						"Unable to again build an informative error JSON message response.",
						e);
			}

		}
		out.flush();
		out.close();

	}

}
