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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.ServiceStat;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Returns JSON of the fulfilled request, designed to be consumed by an
 * AJAX call.
 * 
 */
public class StatisticsForServiceAjaxServlet extends HttpServlet {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 920822164573080022L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	private static Logger logger = Logger.getLogger(StatisticsForServiceAjaxServlet.class);

    /**
     * 
     */
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Long serviceId = null;
        JSONObject jsonObject = new JSONObject();
        try {
        	
        	List<FulfilledClientRequest> historyOfServiceRequests =null;
        	if(req.getParameter("serviceId")!=null){
        		serviceId = new Long(req.getParameter("serviceId"));
        		historyOfServiceRequests = store.getFulfilledClientRequestsForService(serviceId);
        	}else {
        		historyOfServiceRequests = store.getFulfilledClientRequests();
        	}
                        
            
            if(historyOfServiceRequests!=null && historyOfServiceRequests.size() > 0){
            	jsonObject.put("numberOfRequests", historyOfServiceRequests.size());
            	List<ServiceStat> statList = new ArrayList<ServiceStat>();
            	for(FulfilledClientRequest requestInstance : historyOfServiceRequests ){
            		ServiceStat stat = new ServiceStat();
            		stat.setScenarioName(requestInstance.getScenarioName());
            		stat.setServiceName(requestInstance.getServiceName());
            		stat.setTime(requestInstance.getTime());
            		statList.add(stat);
            		
            	}
            	jsonObject.put("history", statList);
            }
            
            


        } catch (Exception e) {
        	 try {
				jsonObject.put("error", ""+"Sorry, history for this service (service ID ="+serviceId
						 +") is not available.");
			} catch (JSONException e1) {
				logger.error("Unable to create JSON", e1);
			}
        } 

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
		out.println(jsonObject.toString());
		out.flush();
		out.close();

    }

}
