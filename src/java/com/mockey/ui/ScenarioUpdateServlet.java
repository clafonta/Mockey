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
		String returnHTML = "Updated";

		PrintWriter out = resp.getWriter();

		out.println("<weather><report>" + returnHTML + "</report></weather>");
		out.flush();
		out.close();

	}
}
