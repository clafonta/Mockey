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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.ServiceStat;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Returns JSON of the fulfilled request, designed to be consumed by an AJAX
 * call.
 * 
 */
public class StatisticsForServiceAjaxServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 920822164573080022L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	private static Logger logger = Logger.getLogger(StatisticsForServiceAjaxServlet.class);
	private static final SimpleDateFormat DATE_FORMATTER_FOR_EXCEL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 
	 */
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {

			List<FulfilledClientRequest> historyOfServiceRequests = store.getFulfilledClientRequests();
			List<ServiceStat> statList = new ArrayList<ServiceStat>();

			if (historyOfServiceRequests != null && historyOfServiceRequests.size() > 0) {

				for (FulfilledClientRequest requestInstance : historyOfServiceRequests) {
					ServiceStat stat = new ServiceStat();
					stat.setScenarioName(requestInstance.getScenarioName());
					stat.setServiceName(requestInstance.getServiceName());
					stat.setTime(requestInstance.getTime());
					stat.setCount(1);
					statList.add(stat);
				}

			}

			// What to return? JSON or CSV?
			String format = req.getParameter("format");
			if (format != null && "csv".equalsIgnoreCase(format.trim())) {

				// CSV
				resp.setHeader("Content-Encoding", "UTF-8");
				resp.setContentType("text/csv; charset=UTF-8");
				// resp.setHeader("Content-Disposition","inline;
				// filename=serviceStats.csv");
				StringBuffer sb = getCSV(statList);
				PrintWriter out = resp.getWriter();

				out.println(sb.toString());
				out.flush();
				out.close();

			} else {
				resp.setHeader("Content-Encoding", "UTF-8");
				resp.setContentType("text/json; charset=UTF-8");
				// BUILD a JSON response.
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("numberOfRequests", historyOfServiceRequests.size());
				jsonObject.put("history", statList);

				PrintWriter out = resp.getWriter();

				out.println(jsonObject.toString());
				out.flush();
				out.close();
			}

		} catch (Exception e) {

			logger.error("Unable to create JSON", e);

		}

	}

	private StringBuffer getCSV(List<ServiceStat> statList) {
		StringBuffer sb = new StringBuffer();
		sb.append("Service name");
		sb.append(",");
		sb.append("Scenario name");
		sb.append(",");
		sb.append("Timestamp");
		sb.append("\n");

		for (ServiceStat stat : statList) {
			sb.append(stat.getServiceName());
			sb.append(",");
			sb.append(stat.getScenarioName());
			sb.append(",");
			sb.append(DATE_FORMATTER_FOR_EXCEL.format(stat.getTime()));
			sb.append("\n");
		}
		return sb;
	}
	
	

}
