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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mockey.ServiceValidator;
import com.mockey.model.Service;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class ServiceSetupServlet extends HttpServlet {
	private Log log = LogFactory.getLog(ServiceSetupServlet.class);

	private static final long serialVersionUID = 5503460488900643184L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (req.getParameter("all") != null
				&& req.getParameter("responseType") != null) {
			List<Service> services = store.getServices();
			try {
				int serviceResponseType = Integer.parseInt(req
						.getParameter("responseType"));
				for (Iterator<Service> iterator = services.iterator(); iterator
						.hasNext();) {
					Service service = iterator.next();

					service.setServiceResponseType(serviceResponseType);
					store.saveOrUpdateService(service);
				}
			} catch (Exception e) {
				log.error("Unable to update service(s", e);
			}
			PrintWriter out = resp.getWriter();
			Map<String, String> successMessage = new HashMap<String, String>();
			successMessage.put("success", "updated");
			String resultingJSON = Util.getJSON(successMessage);
			out.println(resultingJSON);
			out.flush();
			out.close();
			return;
		}
		log.debug("Setting up a new service");
		Long serviceId = null;
		try {
			serviceId = new Long(req.getParameter("serviceId"));
		} catch (Exception e) {
			// Do nothing
		}

		if (req.getParameter("deleteService") != null && serviceId != null) {
			Service service = store.getServiceById(serviceId);
			store.deleteService(service);
			store.deleteFulfilledClientRequestsForService(serviceId);

			Util.saveSuccessMessage("Service '" + service.getServiceName()
					+ "' was deleted.", req);

			// Check to see if any plans need an update.
			String errorMessage = null;
			if (service.isReferencedInAServicePlan()) {
				errorMessage = "Warning: the deleted service is referenced in service plans.";
			}

			if (errorMessage != null) {
				Util.saveErrorMessage(errorMessage, req);
			}
			String contextRoot = req.getContextPath();
			resp.sendRedirect(Url.getContextAwarePath("home", contextRoot));
			return;
		}

		super.service(req, resp);
	}

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
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Long serviceId = null;
		try {
			serviceId = new Long(req.getParameter("serviceId"));
		} catch (Exception e) {
			// Do nothing
		}
		Service service = null;

		if (serviceId != null) {
			service = store.getServiceById(serviceId);
		}
		if (service == null) {
			service = new Service();
		}

		req.setAttribute("mockservice", service);

		RequestDispatcher dispatch = req
				.getRequestDispatcher("/service_setup.jsp");
		dispatch.forward(req, resp);
	}

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
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String[] realSrvUrl = req.getParameterValues("realServiceUrl[]");

		Service service = new Service();

		Long serviceId = null;

		try {
			serviceId = new Long(req.getParameter("serviceId"));
			service = store.getServiceById(serviceId);
		} catch (Exception e) {
			// Do nothing
		}
		if (service == null) {
			service = new Service();
		}
		// NEW REAL URL LIST
		// 1. Overwrite list of predefined URLs
		// 2. Ensure non-empty trim String for new Url objects. 
		if (realSrvUrl != null) {
			List<Url> newUrlList = new ArrayList<Url>();
			for (int i = 0; i < realSrvUrl.length; i++) {
				String url = realSrvUrl[i];
				if (url.trim().length() > 0) {

					newUrlList.add(new Url(realSrvUrl[i].trim()));
				}

			}
			service.setRealServiceUrls(newUrlList);
		}
		
		// UPDATE HANGTIME - optional
		try {
			service.setHangTime(Integer.parseInt(req.getParameter("hangTime")));

		} catch (Exception e) {
			// DO NOTHING
		}

		// NAME - optional
		if (req.getParameter("serviceName") != null) {
		   service.setServiceName(req.getParameter("serviceName"));
		}
		
		// DESCRIPTION - optional
		if (req.getParameter("description") != null) {
			service.setDescription(req.getParameter("description"));
		}
		// CONTENT TYPE - optional
		if (req.getParameter("httpContentType") != null) {
			service.setHttpContentType(req.getParameter("httpContentType"));
		}

		Map<String, String> errorMap = ServiceValidator.validate(service);

		if ((errorMap != null) && (errorMap.size() == 0)) {
			// no errors, so create service.

			Util.saveSuccessMessage("Service updated.", req);
			Service updatedService = store.saveOrUpdateService(service);

			String redirectUrl = Url.getContextAwarePath("/setup?serviceId="
					+ updatedService.getId(), req.getContextPath());
			PrintWriter out = resp.getWriter();
			String resultingJSON = "{ \"result\": { \"redirect\": \""
					+ redirectUrl + "\"}}";
			out.println(resultingJSON);
			out.flush();
			out.close();
			return;

		} else {

			PrintWriter out = resp.getWriter();
			String resultingJSON = Util.getJSON(errorMap);
			out.println(resultingJSON);

			out.flush();
			out.close();

		}
		return;
		// AJAX thing. Return nothing at this time.
	}
}
