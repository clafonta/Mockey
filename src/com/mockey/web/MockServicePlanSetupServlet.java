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

	private static MockServiceStore store = MockServiceStoreImpl.getInstance();
	
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
		try {
			servicePlanId = new Long(req.getParameter("plan_id"));
			servicePlan = store.getMockServicePlan(servicePlanId);
		} catch (Exception e) {
			// Do nothing
		}

		if (req.getParameter("delete") != null && servicePlan != null) {
			
			store.deleteServicePlan(servicePlan);
			String contextRoot = req.getContextPath();
			resp.sendRedirect(Url.getContextAwarePath("home", contextRoot));
			return;
		}
		if (req.getParameter("set") != null && servicePlan != null) {
			
			setPlan(servicePlan);
			Util.saveSuccessMessage("Service plan " + servicePlan.getName() + " is set.", req);
			String contextRoot = req.getContextPath();
			resp.sendRedirect(Url.getContextAwarePath("home", contextRoot));
			return;
		}
		if(servicePlan==null){
			servicePlan = new MockServicePlan();
		}
		req.setAttribute("services", store.getOrderedList());
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
		if (planItems != null) {
			for (int i = 0; i < planItems.length; i++) {

				String serviceId = planItems[i];
				String ssIdKey = "plan_item_scenario_" + serviceId;
				String proxyOnString = "proxyOn_" + serviceId;
				
				String scenarioId = req.getParameter(ssIdKey);
				if (scenarioId != null) {
					PlanItem planItem = new PlanItem();
					planItem.setScenarioId(new Long(scenarioId));
					planItem.setServiceId(new Long(serviceId));
					planItem.setProxyOn(Boolean.parseBoolean(proxyOnString));
					servicePlan.addPlanItem(planItem);
				}
			}
		}

		String updateServiceAction = req.getParameter("update_service");
		String createOrUpdatePlan = req.getParameter("create_or_update_plan");
		if(updateServiceAction!=null){
			Util.saveSuccessMessage("Service updated.", req);
			this.setPlan(servicePlan);
		}else if(createOrUpdatePlan!=null){
			Map errorMap = MockServicePlanValidator.validate(servicePlan);

			if ((errorMap != null) && (errorMap.size() == 0)) {
				// no errors, so create service.

				Util.saveSuccessMessage("Service plan updated.", req);
				store.saveOrUpdateServicePlan(servicePlan);

			} else {
				Util.saveErrorMessage("Service plan not added/updated.", req);

			}
		}else {
			
		}
		
		req.setAttribute("services", store.getOrderedList());
		req.setAttribute("plans", store.getMockServicePlanList());
		req.setAttribute("plan", servicePlan);
		RequestDispatcher dispatch = req.getRequestDispatcher("/home.jsp");
		dispatch.forward(req, resp);
	}
	
	private void setPlan(MockServicePlan servicePlan){
		List planItems = servicePlan.getPlanItemList();
		Iterator iter = planItems.iterator();
		while(iter.hasNext()){
			PlanItem pi = (PlanItem)iter.next();
			MockServiceBean msb = store.getMockServiceById(pi.getServiceId());
			if(msb!=null){
				msb.setDefaultScenarioId(pi.getScenarioId());
				msb.setProxyOn(pi.isProxyOn());
				store.saveOrUpdate(msb);
			}
		}
	
		
	}
}
