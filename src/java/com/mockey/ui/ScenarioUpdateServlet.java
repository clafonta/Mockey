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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class ScenarioUpdateServlet extends HttpServlet {

	private static final long serialVersionUID = -2964632050151431391L;
	private Logger log = Logger.getLogger(ScenarioUpdateServlet.class);

	private IMockeyStorage store = StorageRegistry.MockeyStorage;

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String serviceId = req.getParameter("serviceId");
		String hangTime = req.getParameter("hangTime");
		String scenarioId = req.getParameter("scenarioId");
		String httpContentType = req.getParameter("httpContentType");
		String serviceResponseType = req.getParameter("serviceResponseType");
		String defaultUrlIndex = req.getParameter("defaultUrlIndex");
		Service service = store.getServiceById(new Long(serviceId));

		try {
			if (serviceResponseType != null) {
				service
						.setServiceResponseType((new Integer(
								serviceResponseType)).intValue());
			}
		} catch (Exception e) {
			log
					.debug("Updating service without a 'service response type' value");
		}

		try {
			int index = Integer.parseInt(defaultUrlIndex);
			
			service.setDefaultRealUrlIndex(index-1);
		} catch (Exception e) {

		}

		try {
			if (hangTime != null) {
				service.setHangTime((new Integer(hangTime).intValue()));
			}
		} catch (Exception e) {
			log.debug("Updating service without a 'hang time' value");
		}
		try {
			if (httpContentType != null) {
				service.setHttpContentType(httpContentType);
			}
		} catch (Exception e) {
			log.debug("Updating service without a 'hang time' value");
		}
		try {
			if (scenarioId != null) {
				service.setDefaultScenarioId(new Long(scenarioId));
			}
		} catch (Exception e) {
			// Do nothing.
			log.debug("Updating service without a 'default scenario ID' value");
		}
		store.saveOrUpdateService(service);

		PrintWriter out = resp.getWriter();
		Map<String, String> objectMap = new HashMap<String, String>();
		Util.getJSON(objectMap);
		out.println(Util.getJSON(objectMap));
		out.flush();
		out.close();

	}
}
