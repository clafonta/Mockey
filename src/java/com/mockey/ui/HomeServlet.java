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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.model.ApiDocAttribute;
import com.mockey.model.ApiDocFieldValue;
import com.mockey.model.ApiDocRequest;
import com.mockey.model.ApiDocResponse;
import com.mockey.model.ApiDocService;
import com.mockey.model.ConflictInfo;
import com.mockey.model.HttpStatusCodeStore;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.model.Url;
import com.mockey.plugin.PluginStore;
import com.mockey.runner.BSC;
import com.mockey.storage.IApiStorage;
import com.mockey.storage.IApiStorageInMemory;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.storage.xml.MockeyXmlFileManager;

public class HomeServlet extends HttpServlet {

	private static final long serialVersionUID = -5485332140449853235L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	private static Logger logger = Logger.getLogger(HomeServlet.class);
	private IApiStorage apiStore = IApiStorageInMemory.getInstance();
	private static final String API_CONFIGURATION_PARAMETER_ACTION_VALUE_DELETE = "deleteAllServices";
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";

	/**
	 * Loads up the HTTP API Documentation in memory for this service. The HTTP
	 * API information to describe this servlet's REQUEST and RESPONSE messaging
	 * is displayed to the end user via the Service API help page.
	 */
	public void init() throws ServletException {

		// Load up plugin store with at least one Sample inspectors
		PluginStore pluginStore = PluginStore.getInstance();
		pluginStore.initializeOrUpdateStore();

		// *****************************
		// THIS SERVICE API DESCRIPTION CONTRACT
		// *****************************
		// This information is used in the API JSP document, used to describe
		// how to make setting changes from a head-less client.

		if (apiStore.getApiDocServiceByName("Initialization") == null) {
			ApiDocService apiDocService = new ApiDocService();
			apiDocService.setName("Initialization");
			apiDocService.setServicePath("/home");
			apiDocService.setDescription(
					"If you need to initialize Mockey with a definitions file, then this API may serve your needs. ");
			// *****************************
			// REQUEST DEFINITION
			// *****************************

			ApiDocRequest apiDocRequest = new ApiDocRequest();

			// Parameter - 'action'
			ApiDocAttribute reqAttributeAction = new ApiDocAttribute();
			reqAttributeAction.setFieldName(BSC.ACTION);
			reqAttributeAction.addFieldValues(new ApiDocFieldValue(API_CONFIGURATION_PARAMETER_ACTION_VALUE_DELETE,
					"Delete all configurations, history, settings, etc., and start with a clean Mockey. "));
			reqAttributeAction.addFieldValues(new ApiDocFieldValue(BSC.INIT,
					"Will delete everything and configure Mockey with the defined file. "));
			apiDocRequest.addAttribute(reqAttributeAction);

			// Parameter - 'type'
			ApiDocAttribute reqAttributeType = new ApiDocAttribute();
			reqAttributeType.setFieldName(BSC.TYPE);
			reqAttributeType.addFieldValues(new ApiDocFieldValue("json",
					"Response will be in JSON. Any other value for 'type' is undefined and you may experience a 302 or get HTML back."));
			apiDocRequest.addAttribute(reqAttributeType);
			apiDocService.setApiRequest(apiDocRequest);

			// Parameter - 'transientState'
			ApiDocAttribute reqAttributeState = new ApiDocAttribute();
			reqAttributeState.setFieldName(BSC.TRANSIENT);
			reqAttributeState
					.addFieldValues(new ApiDocFieldValue("boolean", "Read only mode? Also known as transient."));
			apiDocRequest.addAttribute(reqAttributeState);
			apiDocService.setApiRequest(apiDocRequest);

			// *****************************
			// RESPONSE DEFINITION
			// *****************************

			ApiDocResponse apiResponse = new ApiDocResponse();
			// Building a JSON RESPONSE example
			try {
				JSONObject jsonResponseObject = new JSONObject();
				JSONObject jsonResultObject = new JSONObject();
				jsonResultObject.put(SUCCESS,
						"Some informative coaching message. If success isn't a value, then maybe you have a 'fail' message.");
				jsonResultObject.put("file", "Some file name");
				jsonResponseObject.put("result", jsonResultObject);
				apiResponse.setExample(jsonResponseObject.toString());
			} catch (Exception e) {
				logger.error("Unabel to build a sample JSON message. ", e);
			}

			// Response attribute 'success'
			ApiDocAttribute resAttributeSuccess = new ApiDocAttribute();
			resAttributeSuccess.setFieldName(SUCCESS);
			resAttributeSuccess.setFieldDescription(
					"Successfully initialized or deleted service definitions.  You get 'fail' or 'success', not both.");
			apiResponse.addAttribute(resAttributeSuccess);

			ApiDocAttribute resAttributeFail = new ApiDocAttribute();
			resAttributeFail.setFieldName(FAIL);
			resAttributeFail.setFieldDescription(
					"Failed to initialize or delete service definitions. You get 'fail' or 'success', not both.");
			apiResponse.addAttribute(resAttributeFail);

			apiDocService.setApiResponse(apiResponse);
			apiStore.saveOrUpdateService(apiDocService);
		}
	}

	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String action = req.getParameter(BSC.ACTION);
		String type = req.getParameter(BSC.TYPE);

		// FILTER
		// ***********
		String filterTagParameter = req.getParameter(BSC.FILTERTAG);
		if (filterTagParameter != null) {
			store.setGlobalStateSystemFilterTag(filterTagParameter);
		}

		if (action != null && "init".equals(action)) {

			JSONObject jsonResultObject = new JSONObject();

			// Load with a definitions file.
			String fileName = req.getParameter("file");
			// Or with a URL
			String fileUrl = req.getParameter(BSC.URL);

			Boolean transientState = new Boolean(true);
			try {
				transientState = new Boolean(req.getParameter(BSC.TRANSIENT));
				store.setReadOnlyMode(transientState);
				logger.debug("In memory-mode (i.e. do not write to file system)? " + transientState);
			} catch (Exception e) {

			}
			InputStream fstream = null;
			try {

				if (fileUrl != null) {
					// or, from a URL, then retrieve an InputStream from a URL
					fstream = new URL(fileUrl).openStream();
				} else if (fileName != null) {
					File f = new File(fileName);
					if (f.exists()) {
						fstream = new FileInputStream(f);
					} else {
						logger.info("Filename '" + fileName + "' does not exist. doing nothing.");
						jsonResultObject.put(FAIL, fileName + " does not exist. doing nothing.");
					}
				}

				if (fstream != null) {

					// DELETE SECOND
					MockeyXmlFileManager reader = MockeyXmlFileManager.getInstance();
					String inputAsString = reader.getFileContentAsString(fstream);
					store.deleteEverything();
					reader.loadConfigurationWithXmlDef(inputAsString, null);
					logger.info("Loaded definitions from " + fileName);
					jsonResultObject.put(SUCCESS, "Loaded definitions from " + fileName);

				}
			} catch (Exception e) {

				logger.debug("Unable to load service definitions with name: '" + fileName + "' or URL: " + fileUrl, e);
				try {
					jsonResultObject.put(FAIL,
							"Unable to load service definitions with filename: '" + fileName + "' or URL: " + fileUrl);
				} catch (Exception ef) {
					logger.error("Unable to produce a JSON response.", e);
				}
			} finally {
				if (fstream != null) {
					try {
						fstream.close();
					} catch (Exception e) {

					}
				}
			}

			// OK, return JSON or HTML?

			if (type != null && type.trim().equalsIgnoreCase("json")) {
				resp.setContentType("application/json;");
				PrintWriter out = resp.getWriter();
				JSONObject jsonResponseObject = new JSONObject();

				try {
					jsonResponseObject.put("result", jsonResultObject);
				} catch (JSONException e) {
					logger.error("Unable to produce a JSON result.", e);
				}
				out.println(jsonResponseObject.toString());
				return;
			} else {
				String absolutePath = Url.getAbsoluteURL(req, "/home");
				resp.sendRedirect(absolutePath);
				return;
			}

		} else if (action != null && "deleteAllServices".equals(action)) {
			// Flush - clean slate.

			store.deleteEverything();

			if (type != null && type.trim().equalsIgnoreCase("json")) {
				resp.setContentType("application/json;");
				PrintWriter out = resp.getWriter();
				JSONObject jsonResponseObject = new JSONObject();
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put(SUCCESS, "All is deleted. You have a clean slate. Enjoy.");
					jsonResponseObject.put("result", jsonObject);
				} catch (JSONException e) {
					logger.error("Unable to produce a JSON result.", e);
				}
				out.println(jsonResponseObject.toString());
				return;
			} else {
				resp.sendRedirect(Url.getAbsoluteURL(req, "/home"));
				return;
			}
		}
		// If a Service Plan ID was passed in, then we need to
		// highlight the Services (provide a visual cue) to the
		// user for which Services are included in the plan.
		String servicePlanId = req.getParameter("plan_id");
		if (servicePlanId != null) {
			try {
				ServicePlan sp = store.getServicePlanById(new Long(servicePlanId));
				req.setAttribute("servicePlan", sp);

			} catch (Exception e) {
				logger.debug("Service Plan with ID '" + servicePlanId + "' does not exist.", e);
			}
		}

		String filterTagArg = store.getGlobalStateSystemFilterTag();

		FilterHelper filterHelper = new FilterHelper();
		List<Service> filteredServiceList = filterHelper.getFilteredServices(filterTagArg, store);

		ConflictHelper conflictHelper = new ConflictHelper();
		ConflictInfo conflictInfo = conflictHelper.getConflictInfo(filteredServiceList);
		req.setAttribute("services", filteredServiceList);
		req.setAttribute("conflictInfo", conflictInfo);
		req.setAttribute("plans", filterHelper.getFilteredServicePlans(filterTagArg, store));
		req.setAttribute("filterTag", filterTagArg);
		req.setAttribute("filterTagList", store.getAllTagsFromStore());
		
		if (store.getDefaultServicePlanIdAsLong() != null) {
			req.setAttribute("defaultServicePlanId", store.getDefaultServicePlanId());
		} else {
			req.setAttribute("defaultServicePlanId", "");
		}
		req.setAttribute("httpRespCodeList", HttpStatusCodeStore.getInstance().getCodeEntryList());
		RequestDispatcher dispatch = req.getRequestDispatcher("home.jsp");
		dispatch.forward(req, resp);
	}

}
