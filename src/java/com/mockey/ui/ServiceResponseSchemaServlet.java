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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class ServiceResponseSchemaServlet extends HttpServlet {

	private static final long serialVersionUID = -5458265418944545531L;
	private static Logger logger = Logger
			.getLogger(ServiceResponseSchemaServlet.class);

	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			Long serviceId = new Long(req.getParameter("serviceId"));
			Long scenarioId = null;
			scenarioId = new Long(req.getParameter("scenarioId"));
			Service service = store.getServiceById(serviceId);
			Scenario scenario = service.getScenario(scenarioId);
			req.setAttribute("service", service);
			req.setAttribute("scenario", scenario);
		} catch (Exception e) {
			// Do nothing. If the value doesn't exist,
			// then we'll create a new Scenario
			// for this service.
			logger.debug("Unable to retrieve a Service of ID: " + req.getParameter("serviceId"));
		}

		// Get the service.

		RequestDispatcher dispatch = req
				.getRequestDispatcher("/response_schema.jsp");
		dispatch.forward(req, resp);
	}

}
