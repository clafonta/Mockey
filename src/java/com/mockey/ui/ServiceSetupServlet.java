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
import java.util.Date;
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
import com.mockey.model.IRequestInspector;
import com.mockey.model.Service;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class ServiceSetupServlet extends HttpServlet {
	private Log log = LogFactory.getLog(ServiceSetupServlet.class);

	private static final long serialVersionUID = 5503460488900643184L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	private static final Boolean TRANSIENT_STATE = new Boolean(true);
	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			"MM/dd/yyyy");

	/**
	 * 
	 */
	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (req.getParameter("all") != null
				&& req.getParameter("responseType") != null) {
			List<Service> services = store.getServices();
			// #1. Get a handle of the original read-only-mode (transient?)
			Boolean origReadOnlyMode = store.getReadOnlyMode();
			try {
				// #2. Put the store in TRANSIENT STATE (memory only)
				// why? To prevent repeating file writes to the file system
				store.setReadOnlyMode(TRANSIENT_STATE);
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
			// #3 Return store back to original setting.
			store.setReadOnlyMode(origReadOnlyMode);
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			Map<String, String> successMessage = new HashMap<String, String>();
			successMessage.put("success", "updated");
			String resultingJSON = Util.getJSON(successMessage);
			out.println(resultingJSON);
			out.flush();
			out.close();
			return;
		}
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
		req.setAttribute("requestInspectorList", store.getRequestInspectorList());
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
			
			for(Url urlItem: newUrlList){
				service.saveOrUpdateRealServiceUrl(urlItem);
			}
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

		// TAG - optional
		if (req.getParameter("tag") != null) {
			service.setTag(req.getParameter("tag"));
		}

		// Last visist
		if (req.getParameter("lastVisit") != null) {
			try {
				String lastvisit = req.getParameter("lastVisit");
				if (lastvisit.trim().length() > 0 && !"mm/dd/yyyy".equals(lastvisit.trim().toLowerCase())) {
					Date f = formatter.parse(lastvisit);
					service.setLastVisit(f.getTime());
				} else {
					service.setLastVisit(null);
				}
			} catch (Exception e) {

			}

		}
		String classNameForRequestInspector = req.getParameter("requestInspectorName");
		if(classNameForRequestInspector!=null){
			
			IRequestInspector iri = store.getRequestInspectorByClassName(classNameForRequestInspector);
			if(iri!=null){
				service.setRequestInspectorName(classNameForRequestInspector);
			}else {
				service.setRequestInspectorName("");
			}
		}

		// DESCRIPTION - optional
		if (req.getParameter("description") != null) {
			service.setDescription(req.getParameter("description"));
		}

		// MOCK URL - optional
		if (req.getParameter("url") != null) {
			service.setUrl(req.getParameter("url"));
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
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			String resultingJSON = "{ \"result\": { \"redirect\": \""
					+ redirectUrl + "\"}}";
			out.println(resultingJSON);
			out.flush();
			out.close();
			return;

		} else {
			resp.setContentType("application/json");
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
