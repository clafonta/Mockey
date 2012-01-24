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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.model.ApiDocAttribute;
import com.mockey.model.ApiDocFieldValue;
import com.mockey.model.ApiDocRequest;
import com.mockey.model.ApiDocResponse;
import com.mockey.model.ApiDocService;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.model.Url;
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

	private static final String API_CONFIGURATION_NAME = "Initialization";
	private static final String API_CONFIGURATION_PARAMETER_ACTION = "action";
	private static final String API_CONFIGURATION_PARAMETER_TYPE = "type";
	private static final String API_CONFIGURATION_PARAMETER_FILE = "file";
	private static final String API_CONFIGURATION_PARAMETER_ACTION_VALUE_DELETE = "deleteAllServices";
	private static final String API_CONFIGURATION_PARAMETER_ACTION_VALUE_INIT = "init";
	private static final String API_CONFIGURATION_PARAMETER_ACTION_VALUE_TRANSIENT_STATE = "transientState";
	
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";

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

		if (apiStore.getApiDocServiceByName(API_CONFIGURATION_NAME) == null) {
			ApiDocService apiDocService = new ApiDocService();
			apiDocService.setName(API_CONFIGURATION_NAME);
			apiDocService.setServicePath("/home");
			apiDocService.setDescription("If you need to initialize Mockey with a definitions file, then this API may serve your needs. ");
			// *****************************
			// REQUEST DEFINITION
			// *****************************

			ApiDocRequest apiDocRequest = new ApiDocRequest();

			// Parameter - 'action'
			ApiDocAttribute reqAttributeAction = new ApiDocAttribute();
			reqAttributeAction.setFieldName(API_CONFIGURATION_PARAMETER_ACTION);
			reqAttributeAction.addFieldValues(new ApiDocFieldValue(API_CONFIGURATION_PARAMETER_ACTION_VALUE_DELETE,
					"Delete all configurations, history, settings, etc., and start with a clean Mockey. "));
			reqAttributeAction.addFieldValues(new ApiDocFieldValue(API_CONFIGURATION_PARAMETER_ACTION_VALUE_INIT,
					"Will delete everything and configure Mockey with the defined file. "));
			apiDocRequest.addAttribute(reqAttributeAction);

			// Parameter - 'file'
			ApiDocAttribute reqAttributeFile = new ApiDocAttribute();
			reqAttributeFile.setFieldName(API_CONFIGURATION_PARAMETER_FILE);
			reqAttributeFile.addFieldValues(new ApiDocFieldValue("[string]",
					"Relative path to the service definitions configuration file. Required if 'action' is 'init'"));
			reqAttributeFile.setExample("../some_file.xml or /Users/someuser/Work/some_file.xml");
			apiDocRequest.addAttribute(reqAttributeFile);

			// Parameter - 'type'
			ApiDocAttribute reqAttributeType = new ApiDocAttribute();
			reqAttributeType.setFieldName(API_CONFIGURATION_PARAMETER_TYPE);
			reqAttributeType
					.addFieldValues(new ApiDocFieldValue("json",
							"Response will be in JSON. Any other value for 'type' is undefined and you may experience a 302 or get HTML back."));
			apiDocRequest.addAttribute(reqAttributeType);
			apiDocService.setApiRequest(apiDocRequest);
			
			// Parameter - 'transientState'
			ApiDocAttribute reqAttributeState = new ApiDocAttribute();
			reqAttributeState.setFieldName(API_CONFIGURATION_PARAMETER_ACTION_VALUE_TRANSIENT_STATE);
			reqAttributeState
					.addFieldValues(new ApiDocFieldValue("boolean",
							"Read only mode? Also known as transient."));
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
				jsonResultObject
						.put(SUCCESS,
								"Some informative coaching message. If success isn't a value, then maybe you have a 'fail' message.");
				jsonResultObject.put("file", "Some file name");
				jsonResponseObject.put("result", jsonResultObject);
				apiResponse.setExample(jsonResponseObject.toString());
			} catch (Exception e) {
				logger.error("Unabel to build a sample JSON message. ", e);
			}

			// Response attribute 'file'
			ApiDocAttribute resAttributeFile = new ApiDocAttribute();
			resAttributeFile.setFieldName(API_CONFIGURATION_PARAMETER_FILE);
			resAttributeFile.setFieldDescription("Name of file used to initialize Mockey.");
			apiResponse.addAttribute(resAttributeFile);

			// Response attribute 'success'
			ApiDocAttribute resAttributeSuccess = new ApiDocAttribute();
			resAttributeSuccess.setFieldName(SUCCESS);
			resAttributeSuccess
					.setFieldDescription("Successfully initialized or deleted service definitions.  You get 'fail' or 'success', not both.");
			apiResponse.addAttribute(resAttributeSuccess);

			ApiDocAttribute resAttributeFail = new ApiDocAttribute();
			resAttributeFail.setFieldName(FAIL);
			resAttributeFail
					.setFieldDescription("Failed to initialize or delete service definitions. You get 'fail' or 'success', not both.");
			apiResponse.addAttribute(resAttributeFail);

			apiDocService.setApiResponse(apiResponse);
			apiStore.saveOrUpdateService(apiDocService);
		}
	}

	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String action = req.getParameter(API_CONFIGURATION_PARAMETER_ACTION);
		String type = req.getParameter(API_CONFIGURATION_PARAMETER_TYPE);
		if (action != null && "init".equals(action)) {

			// Flush - clean slate.
			IMockeyStorage store = StorageRegistry.MockeyStorage;
			JSONObject jsonResultObject = new JSONObject();

			// Load with local file.
			String fileName = req.getParameter("file");
			Boolean transientState = new Boolean(true);
			try{
				transientState = new Boolean(req.getParameter(API_CONFIGURATION_PARAMETER_ACTION_VALUE_TRANSIENT_STATE));
				store.setReadOnlyMode(transientState);
				logger.debug("Read only mode? " + transientState);
			}catch(Exception e){
				
			}
			try {
				File f = new File(fileName);
				if (f.exists()) {
					// Slurp it up and initialize definitions.
					FileInputStream fstream = new FileInputStream(f);
					BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Charset.forName(HTTP.UTF_8)));
					StringBuffer inputString = new StringBuffer();
					// Read File Line By Line
					String strLine = null;
					// READ FIRST
					while ((strLine = br.readLine()) != null) {
						// Print the content on the console
						inputString.append(new String(strLine.getBytes(HTTP.UTF_8)));
					}
					// DELETE SECOND
					store.deleteEverything();
					MockeyXmlFileManager reader = new MockeyXmlFileManager();

					reader.loadConfigurationWithXmlDef(inputString.toString(), null);
					logger.info("Loaded definitions from " + fileName);
					jsonResultObject.put(SUCCESS, "Loaded definitions from " + fileName);
					jsonResultObject.put(API_CONFIGURATION_PARAMETER_FILE, fileName);
				} else {
					logger.info(fileName + " does not exist. doing nothing.");
					jsonResultObject.put(FAIL, fileName + " does not exist. doing nothing.");
				}
			} catch (Exception e) {
				logger.debug("Unable to load service definitions with name: '" + fileName + "'", e);
				try {
					jsonResultObject.put(FAIL, "Unable to load service definitions with name: '" + fileName + "'");
				} catch (Exception ef) {
					logger.error("Unable to produce a JSON response.", e);
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
				String contextRoot = req.getContextPath();
				resp.sendRedirect(Url.getContextAwarePath("/home", contextRoot));

				return;
			}

		} else if (action != null && "deleteAllServices".equals(action)) {
			// Flush - clean slate.
			IMockeyStorage store = StorageRegistry.MockeyStorage;
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
				String contextRoot = req.getContextPath();
				resp.sendRedirect(Url.getContextAwarePath("/home", contextRoot));

				return;
			}
		}
		
		
		List<Service> serviceList = null; 
		List<ServicePlan> servicePlanList = null; 
		String tagFilter = (String)req.getSession().getAttribute(TagHelperServlet.FILTER_TAG);
		
		//******************
		// Filter 
		//******************
		if(tagFilter!=null && tagFilter.trim().length()>0){
			//************************************
			// Filter by SERVICES
			//************************************
			List<Service> filteredList = new ArrayList<Service>();
			for(Service tempService: store.getServices()){
				if(tempService.hasTag(tagFilter) ){
					filteredList.add(tempService);
				} 
			}
			serviceList = Util.orderAlphabeticallyByServiceName(filteredList);
			
			//************************************
			// Filter by SERVICE PLANS
			//************************************
			List<ServicePlan> filteredPlanList = new ArrayList<ServicePlan>();
			for(ServicePlan tempServicePlan: store.getServicePlans()){
				if(tempServicePlan.hasTag(tagFilter) ){
					filteredPlanList.add(tempServicePlan);
				} 
			}
			
			serviceList = Util.orderAlphabeticallyByServiceName(filteredList);
			servicePlanList = Util.orderAlphabeticallyByServicePlanName(filteredPlanList);
			
			//
		}else {
			serviceList = Util.orderAlphabeticallyByServiceName(store.getServices());
			servicePlanList = Util.orderAlphabeticallyByServicePlanName(store.getServicePlans());
		}
		req.setAttribute("services", serviceList);
		req.setAttribute("plans",servicePlanList);  // );

		RequestDispatcher dispatch = req.getRequestDispatcher("home.jsp");
		dispatch.forward(req, resp);
	}

}
