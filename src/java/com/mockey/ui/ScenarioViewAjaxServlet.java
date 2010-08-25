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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Returns an HTML representations of the service .scenario
 * 
 * @author chad.lafontaine
 * 
 */
public class ScenarioViewAjaxServlet extends HttpServlet {

	private static final long serialVersionUID = 6258997861605811341L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Long serviceId = new Long(req.getParameter("serviceId"));
		String scenarioIdAsString = req.getParameter("scenarioId");
		Service service = store.getServiceById(serviceId);
		Scenario scenario = null;
		PrintWriter out = resp.getWriter();
		try {
			scenario = service.getScenario(new Long(scenarioIdAsString));
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("serviceId", ""+serviceId);
			jsonObject.put("serviceName", ""+service.getServiceName());
			jsonObject.put("scenarioId", ""+ scenario.getId());
			jsonObject.put("name", scenario.getScenarioName());
			jsonObject.put("match", scenario.getMatchStringArg());
			jsonObject.put("response", scenario.getResponseMessage());

			out.println(jsonObject.toString());
		} catch (Exception e) {
			out.println("{ \"error\": \"Unable to find scenario \"}");
		}
		out.flush();
		out.close();
		return;

	}
}
