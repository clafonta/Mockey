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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.model.Service;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class HomeServlet extends HttpServlet {

	private static final long serialVersionUID = -5485332140449853235L;	
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String action = req.getParameter("action");
		if (action != null && "deleteAllServices".equals(action)) {
			IMockeyStorage store = StorageRegistry.MockeyStorage;
			store.deleteEverything();
			String contextRoot = req.getContextPath();
			resp.sendRedirect(Url.getContextAwarePath("/home", contextRoot));
			return;
		} else {
			List<Service> orderedServices = new ArrayList<Service>();
			for (Service serviceToInsert : store.getServices()) {
				int insertIndex = 0;
				for(Service existingService: orderedServices){
					char a = serviceToInsert.getServiceName().toUpperCase().trim().charAt(0);
					char b = existingService.getServiceName().toUpperCase().trim().charAt(0);
					if(a<b){
						break;
					}
					insertIndex++;
				}
				orderedServices.add(insertIndex, serviceToInsert);
			}
			req.setAttribute("services", orderedServices);
			req.setAttribute("plans", store.getServicePlans());
		}

		RequestDispatcher dispatch = req.getRequestDispatcher("home.jsp");

		dispatch.forward(req, resp);
	}

}
