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
