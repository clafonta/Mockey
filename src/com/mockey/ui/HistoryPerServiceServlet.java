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
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * 
 * 
 * @author chad.lafontaine
 */
public class HistoryPerServiceServlet extends HttpServlet {

    private static final long serialVersionUID = 6606106522000844746L;
    private static IMockeyStorage store = StorageRegistry.MockeyStorage;
    private static Logger logger = Logger.getLogger(HistoryPerServiceServlet.class);

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    	Long serviceId = Long.parseLong(req.getParameter("serviceId"));
    	logger.debug("getting requests for service: "+serviceId);
    	
    	List<String> ips = store.uniqueClientIPsForService(serviceId);
    	if (ips.size()==1) {
    		resp.sendRedirect("detail?serviceId="+req.getParameter("serviceId")+"&iprequest="+ips.get(0));
    		return;
    	}
		req.setAttribute("serviceId", serviceId);
		req.setAttribute("uniqueIPs", ips);
		
        RequestDispatcher dispatch = req.getRequestDispatcher("/service_history.jsp");
        dispatch.forward(req, resp);
    }

}
