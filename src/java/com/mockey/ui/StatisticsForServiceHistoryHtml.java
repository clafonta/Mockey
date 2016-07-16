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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.ServiceStat;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Returns JSON of the fulfilled request, designed to be consumed by an AJAX
 * call.
 * 
 */
public class StatisticsForServiceHistoryHtml extends HttpServlet {

	private static final long serialVersionUID = 3244374238073838813L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	private static Logger logger = Logger.getLogger(StatisticsForServiceHistoryHtml.class);

	/**
	 * 
	 */
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Long serviceId = null;
		Date startDate = new Date();
		Date endDate = new Date();
		
		
		try {

			// #1) Check for a filter of Service ID
			List<FulfilledClientRequest> historyOfServiceRequests = null;
			if (req.getParameter("serviceId") != null) {
				serviceId = new Long(req.getParameter("serviceId"));
				historyOfServiceRequests = store.getFulfilledClientRequestsForService(serviceId);
			} else {
				historyOfServiceRequests = store.getFulfilledClientRequests();
			}
			
			// #2) Get default start/end dates by going through the history 
			for (FulfilledClientRequest requestInstance : historyOfServiceRequests) {
				if(requestInstance.getTime().before(startDate)){
					startDate = requestInstance.getTime();
				}
			}
			
			

			if (historyOfServiceRequests != null && historyOfServiceRequests.size() > 0) {

				List<ServiceStat> statList = getServiceHitCount(historyOfServiceRequests, startDate, endDate);
				req.setAttribute("statList", statList);
				req.setAttribute("startDate", startDate);
				req.setAttribute("endDate", endDate);

			}

		} catch (Exception e) {
			try {
				Util.saveErrorMessage("Sorry, history for this service (service ID =" + serviceId, req);

			} catch (Exception e1) {
				logger.error("Unable to create JSON", e1);
			}
		}

		RequestDispatcher dispatch = req.getRequestDispatcher("historystats.jsp");
		dispatch.forward(req, resp);

	}

	/**
	 * 
	 * @return a List of service hit counts
	 */
	private List<ServiceStat> getServiceHitCount(List<FulfilledClientRequest> historyOfServiceRequests, Date startDate, Date endDate) {
		
		Map<String, ServiceStat> statMap = new HashMap<String, ServiceStat>();

		for (FulfilledClientRequest requestInstance : historyOfServiceRequests) {
			ServiceStat stat = statMap.get(requestInstance.getServiceName()); //
			if (stat == null) {
				stat = new ServiceStat();
			}
			stat.setScenarioName(requestInstance.getScenarioName());
			stat.setServiceName(requestInstance.getServiceName());
			// We only update the count if in the timerange. 
			Date timeOfRequest = requestInstance.getTime();
			if(timeOfRequest!=null && timeOfRequest.after(startDate) && startDate.before(endDate)) {
			    // In between the range?
				stat.setCount(stat.getCount() + 1);
			}
			statMap.put(stat.getServiceName(), stat);
		}
		List<ServiceStat> statList = new ArrayList<ServiceStat>(statMap.values());
		return statList;

	}

}
