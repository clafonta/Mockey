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
package com.mockey.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mockey.model.PlanItem;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class ServicePlanSetupServlet extends HttpServlet {

    private static final long serialVersionUID = -2964632050151431391L;

    private Log log = LogFactory.getLog(ServicePlanSetupServlet.class);

    private IMockeyStorage store = StorageRegistry.MockeyStorage;

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
        log.debug("Service Plan setup/delete");
        ServicePlan servicePlan = null;
        Long servicePlanId = null;
        List<Service> allServices = store.getServices();
        try {
            servicePlanId = new Long(req.getParameter("plan_id"));
            servicePlan = store.getServicePlanById(servicePlanId);
        } catch (Exception e) {
            // Do nothing
        }
        String action = req.getParameter("action");
        if ("delete_plan".equals(action)) {

        	Map<String, String> successMap = new HashMap<String, String>();
        	try{
        		store.deleteServicePlan(servicePlan);
        		successMap.put("success", "Service plan " + servicePlan.getName() + " deleted");
        	}catch(Exception e){
        		successMap.put("success", "Service plan deleted.");
        	}
            PrintWriter out = resp.getWriter();
			String resultingJSON = Util.getJSON(successMap);
			out.println(resultingJSON);
			out.flush();
			out.close();
            return;
        } else if ("set_plan".equals(action) && servicePlan != null) {

        	Map<String, String> successMap = new HashMap<String, String>();
        	try{
        		setPlan(servicePlan);
        		String msg = "Service plan " + servicePlan.getName() + " set";
        		successMap.put("success", msg);
        		Util.saveSuccessMessage(msg, req); // For redirect
        	}
            catch(Exception e){
            	successMap.put("fail", "Service plan not set. Please check your logs for insight.");
            }
            PrintWriter out = resp.getWriter();
			String resultingJSON = Util.getJSON(successMap);
			out.println(resultingJSON);
			out.flush();
			out.close();
            return;
        } else if ("save_plan".equals(action)) {
        	
        	if(servicePlan == null){
        		servicePlan = new ServicePlan();
        	}
        	servicePlan.setName(req.getParameter("servicePlanName"));
        	ServicePlan savedServicePlan = createOrUpdatePlan(servicePlan);
        	PrintWriter out = resp.getWriter();
			Map<String, String> successMap = new HashMap<String, String>();
			String msg = "Service plan " + servicePlan.getName() + " saved";
			Util.saveSuccessMessage(msg, req); // For redirect
			successMap.put("success",msg );
			successMap.put("planid", ""+savedServicePlan.getId());
			String resultingJSON = Util.getJSON(successMap);
			out.println(resultingJSON);
			out.flush();
			out.close();
			return;
        } 
        
        req.setAttribute("services", allServices);
        req.setAttribute("plans", store.getServicePlans());
        RequestDispatcher dispatch = req.getRequestDispatcher("/home.jsp");
        dispatch.forward(req, resp);
    }


    private ServicePlan createOrUpdatePlan(ServicePlan servicePlan){
    	List<PlanItem> planItemList = new ArrayList<PlanItem>();
    	for(Service service: store.getServices()){
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
