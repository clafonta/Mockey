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

		try {

			// #1) Set default start/end
			// End date/time is NOW + 1 minute.
			// Start date/time is the earliest record of a Mockey response.
			Date rangeEndDate = ServiceStatHelper.getNowPlusOneMinute();
			Date rangeStartDate = new Date();

			// #2) Go through history, and get the oldest known time from past
			// fulfilled request, and make this the range start.
			List<FulfilledClientRequest> historyOfServiceRequests = store.getFulfilledClientRequests();
			if (historyOfServiceRequests.size() > 0) {
				for (FulfilledClientRequest requestInstance : historyOfServiceRequests) {
					rangeStartDate = ServiceStatHelper.getEarlierTime(rangeStartDate, requestInstance.getTime());
				}
				req.removeAttribute("statFlag");
			} else {
				// Set flag that there are no request.
				req.setAttribute("statFlag", "nostats");
			}

			// #3) Check if 'filter' dates are given. If not, then they default
			// to range dates.
			String filterStartAsDate = req.getParameter("filterStartDate");
			String filterEndAsDate = req.getParameter("filterEndDate");
			Date filterStartDate = ServiceStatHelper.getDateFromString(filterStartAsDate, rangeStartDate);
			Date filterEndDate = ServiceStatHelper.getDateFromString(filterEndAsDate, rangeEndDate);

			// #4) Increment the number of Service visit count.
			Map<String, ServiceStat> statMap = buildFilteredServiceHitCountMap(filterStartDate, filterEndDate);

			// #5) Set sate.
			req.setAttribute("statList", new ArrayList<ServiceStat>(statMap.values()));
			req.setAttribute("rangeStartDate", ServiceStatHelper.getStringFromDate(rangeStartDate));
			req.setAttribute("rangeEndDate", ServiceStatHelper.getStringFromDate(rangeEndDate));
			req.setAttribute("filterStartDate", ServiceStatHelper.getStringFromDate(filterStartDate));
			req.setAttribute("filterEndDate", ServiceStatHelper.getStringFromDate(filterEndDate));

		} catch (Exception e) {
			try {
				Util.saveErrorMessage("Sorry, a problem occurred.", req);

			} catch (Exception e1) {
				logger.error("Unable to create JSON", e1);
			}
		}

		RequestDispatcher dispatch = req.getRequestDispatcher("historystats.jsp");
		dispatch.forward(req, resp);

	}

	/**
	 * 
	 * @param filterStartDate
	 * @param filterEndDate
	 * @return Map of ServiceStats, with count incremented if timestamp is
	 *         between and inclusive of filter dates
	 */
	private Map<String, ServiceStat> buildFilteredServiceHitCountMap(Date filterStartDate, Date filterEndDate) {

		// #1 - Build a list of all possible Service names
		Map<String, ServiceStat> statMap = ServiceStatHelper.getMapListOfAllServices(store.getServices());

		// #2 - Only increment a service stat count to those Fulfilled Requests
		// within the matching range time.
		statMap = ServiceStatHelper.incrementServiceStatCount(statMap, store.getFulfilledClientRequests(),
				filterStartDate, filterEndDate);

		return statMap;

	}

}
