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
import java.util.ArrayList;
import java.util.List;

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
import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.storage.IApiStorage;
import com.mockey.storage.IApiStorageInMemory;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Information of a service and service scenarios, in addition to HTTP
 * Documentation.
 * 
 * @author chadlafontaine
 * 
 */
public class ServiceDefinitionInfoServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1191330345815660271L;

	private Logger log = Logger.getLogger(ServiceDefinitionInfoServlet.class);

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

		if (apiStore.getApiDocServiceByName(ServiceDefinitionInfoAPI.API_SERVICE_INFO_NAME) == null) {
			ApiDocService apiDocService = new ApiDocService();
			apiDocService.setName(ServiceDefinitionInfoAPI.API_SERVICE_INFO_NAME);
			apiDocService
					.setDescription("Do you need META-DATA on service definitions? Here's your meta-data. When to use: if you have a test script that needs to iterate over each service or service scenario and your test script needs information from Mockey, then this is a good source of information.");
			apiDocService.setServicePath("/definitions");

			// *****************************
			// REQUEST DEFINITION
			// *****************************

			ApiDocRequest apiDocRequest = new ApiDocRequest();
			String commonTxt = "(optional) Either a Service ID or Service Name is needed. Otherwise, all Service definitions meta-data will be provided.";
			ApiDocAttribute reqServiceId = new ApiDocAttribute();
			reqServiceId.setFieldName(ServiceConfigurationAPI.API_SERVICE_ID);
			reqServiceId.addFieldValues(new ApiDocFieldValue("[identifier]", commonTxt));
			reqServiceId.setExample("123");
			apiDocRequest.addAttribute(reqServiceId);

			ApiDocAttribute reqServiceName = new ApiDocAttribute();
			reqServiceName.setFieldName(ServiceConfigurationAPI.API_SERVICE_NAME);
			reqServiceName.addFieldValues(new ApiDocFieldValue("[string]", commonTxt));
			reqServiceName.setExample("My Service Name");
			apiDocRequest.addAttribute(reqServiceName);

			apiDocService.setApiRequest(apiDocRequest);

			// *****************************
			// RESPONSE DEFINITION
			// *****************************

			ApiDocResponse apiResponse = new ApiDocResponse();
			// Building a JSON RESPONSE example
			try {
				JSONObject jsonResponseObject = new JSONObject();
				JSONObject jsonResultObject = new JSONObject();
				jsonResultObject.put(ServiceConfigurationAPI.API_SERVICE_ID, "1234");
				jsonResultObject.put(ServiceConfigurationAPI.API_SERVICE_NAME, "Some service name");
				jsonResultObject.put(ServiceConfigurationAPI.API_DEFAULT_SCENARIO_ID, "5678");
				jsonResultObject.put(ServiceConfigurationAPI.API_DEFAULT_SCENARIO_NAME, "Some scenario name");
				jsonResultObject.put(ServiceConfigurationAPI.API_SERVICE_RESPONSE_TYPE,
						ServiceConfigurationAPI.API_SERVICE_RESPONSE_TYPE_VALUE_PROXY);
				jsonResultObject.put(ServiceConfigurationAPI.API_SERVICE_HANGTIME, "500");
				JSONObject jsonScenarioObject = new JSONObject();
				jsonScenarioObject.put(ServiceConfigurationAPI.API_SERVICE_SCENARIO_ID, 1);
				jsonScenarioObject.put(ServiceConfigurationAPI.API_SERVICE_SCENARIO_NAME, "Some scenario name");
				jsonResultObject.append("scenarioArray", jsonScenarioObject);
				jsonResponseObject.append("serviceDefinitions", jsonResultObject);
				apiResponse.setExample(jsonResponseObject.toString());
			} catch (Exception e) {
				log.error("Unabel to build a sample JSON message. ", e);
			}
			ApiDocAttribute resAttributeFail = new ApiDocAttribute();
			resAttributeFail.setFieldName("");
			resAttributeFail.setFieldDescription("Refer to example. This is a simple JSON dump of Mockey definitions. ");
			apiResponse.addAttribute(resAttributeFail);

			apiDocService.setApiResponse(apiResponse);
			apiStore.saveOrUpdateService(apiDocService);

			apiStore.saveOrUpdateService(apiDocService);
		}
	}

	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String serviceId = req.getParameter(ServiceConfigurationAPI.API_SERVICE_ID);
		String serviceName = req.getParameter(ServiceConfigurationAPI.API_SERVICE_NAME);

		Service service = null;
		JSONObject jsonResultObject = new JSONObject();

		if (serviceId != null) {
			service = store.getServiceById(new Long(serviceId));
		} else if (serviceName != null) {
			service = store.getServiceByName(serviceName);
		}

		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		JSONObject jsonResponseObject = new JSONObject();
		try {

			List<Service> servicesList = null;
			if (service != null) {
				servicesList = new ArrayList<Service>();
				servicesList.add(service);
			} else {
				servicesList = store.getServices();
			}

			for (Service tempService : servicesList) {
				JSONObject jsonServiceObject = new JSONObject();
				jsonServiceObject.put(ServiceConfigurationAPI.API_SERVICE_NAME, tempService.getServiceName());
				jsonServiceObject.put(ServiceConfigurationAPI.API_SERVICE_ID, tempService.getId());
				jsonServiceObject.put(ServiceConfigurationAPI.API_DEFAULT_SCENARIO_ID, tempService
						.getDefaultScenarioId());
				jsonServiceObject.put(ServiceConfigurationAPI.API_DEFAULT_SCENARIO_NAME, tempService
						.getDefaultScenarioName());
				jsonServiceObject.put(ServiceConfigurationAPI.API_SERVICE_RESPONSE_TYPE, tempService
						.getServiceResponseTypeAsString());
				jsonServiceObject.put(ServiceConfigurationAPI.API_SERVICE_HANGTIME, tempService.getHangTime());
				jsonServiceObject.put(ServiceConfigurationAPI.API_SERVICE_REQUEST_INSPECTOR_NAME, tempService.getRequestInspectorName());
				for (Scenario tempScenario : tempService.getScenarios()) {
					JSONObject jsonScenarioObject = new JSONObject();
					jsonScenarioObject.put(ServiceConfigurationAPI.API_SERVICE_SCENARIO_ID, tempScenario.getId());
					jsonScenarioObject.put(ServiceConfigurationAPI.API_SERVICE_SCENARIO_NAME, tempScenario
							.getScenarioName());
					jsonServiceObject.append("scenarioArray", jsonScenarioObject);
				}
				jsonResponseObject.append("serviceDefinitions", jsonServiceObject);
			}

			out.println(jsonResponseObject.toString());
		} catch (Exception e) {
			log.error("Unable to build a JSON response. ", e);
			try {
				jsonResultObject.put("fail", "Unable to create service definitions output.");
				jsonResponseObject.put("result", jsonResultObject);
				out.println(jsonResponseObject.toString());
			} catch (Exception ee) {
				log.error("Unable to again build an informative error JSON message response.", e);
			}

		}
		out.flush();
		out.close();

	}

}
