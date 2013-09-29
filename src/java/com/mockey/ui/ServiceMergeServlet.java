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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.storage.xml.MockeyXmlFileManager;

public class ServiceMergeServlet extends HttpServlet {
	private Log log = LogFactory.getLog(ServiceMergeServlet.class);

	private static final long serialVersionUID = 5503460488900643184L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		req.setAttribute("services", Util.orderAlphabeticallyByServiceName(store.getServices()));
		req.setAttribute("plans", store.getServicePlans());

		RequestDispatcher dispatch = req
				.getRequestDispatcher("service_merge.jsp");

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
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Boolean originalMode = store.getReadOnlyMode();
		store.setReadOnlyMode(true);
		
		String[] serviceMergeIdList = req
				.getParameterValues("serviceIdMergeSource[]");
		Enumeration<String> names = (Enumeration<String>)req.getParameterNames();
		while(names.hasMoreElements()){
			log.debug(names.nextElement());
		}
		Long serviceIdMergeSource = null;
		Long serviceIdMergeDestination = null;
		ServiceMergeResults mergeResults = new ServiceMergeResults();
		Map<String, String> responseMap = new HashMap<String, String>();
		try {
			for (int i = 0; i < serviceMergeIdList.length; i++) {

				serviceIdMergeSource = new Long(serviceMergeIdList[i]);
				serviceIdMergeDestination = new Long(req
						.getParameter("serviceIdMergeDestination"));
				if (!serviceIdMergeSource.equals(serviceIdMergeDestination)) {

					Service serviceMergeSource = store
							.getServiceById(serviceIdMergeSource);
					Service serviceMergeDestination = store
							.getServiceById(serviceIdMergeDestination);
					MockeyXmlFileManager configurationReader = MockeyXmlFileManager.getInstance();
					mergeResults = configurationReader.mergeServices(
							serviceMergeSource, serviceMergeDestination,
							mergeResults, null);

				}
				responseMap.put("additions", mergeResults.getAdditionMsg());
				responseMap.put("conflicts", mergeResults.getConflictMsg());
			}
			
		} catch (Exception e) {
			// Do nothing
			log.error("Something wrong with merging services.", e);
			responseMap.put("conflicts", "Unable to merge services. The services selected may be missing or contain bad data. Sorry about this.");

		}
		
		// IF NO CONFLICTS, THEN DELETE OLD SOURCE SERVICES
		if(mergeResults!=null && (mergeResults.getConflictMsgs()==null || mergeResults.getConflictMsgs().isEmpty())){
			for (int i = 0; i < serviceMergeIdList.length; i++) {
				serviceIdMergeSource = new Long(serviceMergeIdList[i]);
				Service service = store.getServiceById(serviceIdMergeSource);
				store.deleteService(service);
			}
			
		}
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();

		String resultingJSON = Util.getJSON(responseMap);
		out.println(resultingJSON);

		out.flush();
		out.close();
		store.setReadOnlyMode(originalMode);
		return;
		// AJAX thing. Return nothing at this time.
	}
}
