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
package com.mockey.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mockey.MockServiceBean;
import com.mockey.MockServicePlan;
import com.mockey.MockServicePlanValidator;
import com.mockey.MockServiceStore;
import com.mockey.MockServiceStoreImpl;
import com.mockey.PlanItem;
import com.mockey.util.Url;

public class MockServicePlanSetupServlet extends HttpServlet {

	private static final long serialVersionUID = -2964632050151431391L;

	private Log log = LogFactory.getLog(MockServicePlanSetupServlet.class);

	private MockServiceStore store = MockServiceStoreImpl.getInstance();

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
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.debug("Service Plan setup/delete");
		MockServicePlan servicePlan = null;
		Long servicePlanId = null;
		List allServices = store.getOrderedList();
		try {
			servicePlanId = new Long(req.getParameter("plan_id"));
			servicePlan = store.getMockServicePlan(servicePlanId);
		} catch (Exception e) {
			// Do nothing
		}
		String action = req.getParameter("action");
		if ("delete_plan".equals(action) && servicePlan != null) {

			store.deleteServicePlan(servicePlan);
			String contextRoot = req.getContextPath();
			resp.sendRedirect(Url.getContextAwarePath("home", contextRoot));
			return;
		} else if ("set_plan".equals(action) && servicePlan != null) {

			setPlan(servicePlan);
			Util.saveSuccessMessage("Service plan " + servicePlan.getName() + " is set.", req);
			String contextRoot = req.getContextPath();
			resp.sendRedirect(Url.getContextAwarePath("home", contextRoot));
			return;
		} else if ("edit_plan".equals(action)) {
			req.setAttribute("mode", "edit_plan");
			
			if (servicePlan != null) {
				allServices = new ArrayList();
				Iterator iter = servicePlan.getPlanItemList().iterator();
				while (iter.hasNext()) {
					PlanItem pi = (PlanItem) iter.next();
					MockServiceBean msb = store.getMockServiceById(pi.getServiceId());
					if (msb != null) {
						msb.setDefaultScenarioId(pi.getScenarioId());
						msb.setServiceResponseType(pi.getServiceResponseType());
						allServices.add(msb);
					}
				}
			}

		}
		if (servicePlan == null) {
			servicePlan = new MockServicePlan();
		}
		req.setAttribute("services", allServices);
		req.setAttribute("plans", store.getMockServicePlanList());
		req.setAttribute("plan", servicePlan);
		RequestDispatcher dispatch = req.getRequestDispatcher("/home.jsp");
		dispatch.forward(req, resp);
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
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		MockServicePlan servicePlan = new MockServicePlan();
		Long servicePlanId = null;

		try {
			servicePlanId = new Long(req.getParameter("plan_id"));
		} catch (Exception e) {
			// Do nothing
		}
		if (servicePlanId != null) {
			servicePlan = this.store.getMockServicePlan(servicePlanId);
		}
		servicePlan.setName(req.getParameter("plan_name"));
		servicePlan.setDescription(req.getParameter("plan_description"));
		String[] planItems = req.getParameterValues("plan_item");
		boolean createPlan = true;
		if (planItems != null) {
			for (int i = 0; i < planItems.length; i++) {

				String serviceId = planItems[i];
				String ssIdKey = "scenario_" + serviceId;
				String serviceResponseTypeKey = "serviceResponseType_" + serviceId;
				String scenarioId = req.getParameter(ssIdKey);
				String serviceOnlyButtonKey = req.getParameter("update_service_" + serviceId);
				int serviceResponseTypeKeyInt = 0;
				try{
				    serviceResponseTypeKeyInt = Integer.parseInt(req.getParameter(serviceResponseTypeKey));
				}catch(Exception e){
				    
				}
				
				if (serviceOnlyButtonKey != null) {
					createPlan = false;
					MockServiceBean msb = store.getMockServiceById(Long.parseLong(serviceId));
					if(scenarioId!=null){
						msb.setDefaultScenarioId(Long.parseLong(scenarioId));
					}
					msb.setServiceResponseType(serviceResponseTypeKeyInt);
					store.saveOrUpdate(msb);
					Util.saveSuccessMessage("Service \"" + msb.getServiceName() + "\" updated.", req);
					break;
				}
				PlanItem planItem = new PlanItem();
				if (scenarioId != null) {
						planItem.setScenarioId(new Long(scenarioId));	
				}
				planItem.setServiceId(new Long(serviceId));
				planItem.setServiceResponseType(serviceResponseTypeKeyInt);
                servicePlan.addPlanItem(planItem);
			}
		}

		if (createPlan) {
			// Save changes for all services as a plan.
			Map errorMap = MockServicePlanValidator.validate(servicePlan);

			if ((errorMap != null) && (errorMap.size() == 0)) {
				// no errors, so create service.

				Util.saveSuccessMessage("Service plan updated.", req);
				store.saveOrUpdateServicePlan(servicePlan);

			} else {
				Util.saveErrorMessage("Service plan not added/updated.", req);

			}
		}

		req.setAttribute("services", store.getOrderedList());
		req.setAttribute("plans", store.getMockServicePlanList());
		req.setAttribute("plan", servicePlan);
		RequestDispatcher dispatch = req.getRequestDispatcher("/home.jsp");
		dispatch.forward(req, resp);
	}

	@SuppressWarnings( { "unchecked" })
	private void setPlan(MockServicePlan servicePlan) {
		List planItems = servicePlan.getPlanItemList();
		Iterator iter = planItems.iterator();
		while (iter.hasNext()) {
			PlanItem pi = (PlanItem) iter.next();
			MockServiceBean msb = store.getMockServiceById(pi.getServiceId());
			if (msb != null) {
				msb.setDefaultScenarioId(pi.getScenarioId());
				msb.setServiceResponseType(pi.getServiceResponseType());
				store.saveOrUpdate(msb);
			}
		}

	}
}
