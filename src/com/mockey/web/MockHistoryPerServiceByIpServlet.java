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
package com.mockey.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.MockServiceBean;
import com.mockey.MockServiceScenarioBean;
import com.mockey.MockServiceStore;
import com.mockey.MockServiceStoreImpl;

/**
 * <code>MockHistoryPerServiceByIpServlet</code> manages the display of past
 * requests to a service by calling IP address. 
 * 
 * @author Chad Lafontaine (chad.lafontaine)
 * @version $Id: MockHistoryRequestLogServlet.java,v 1.2 2005/05/03 22:28:54
 *          clafonta Exp $
 */
public class MockHistoryPerServiceByIpServlet extends HttpServlet {
	
	
	/**
     * 
     */
    private static final long serialVersionUID = -2255013290808524662L;
    private static MockServiceStore store = MockServiceStoreImpl.getInstance();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String iprequest = req.getParameter("iprequest");
		Long serviceId = new Long(req.getParameter("serviceId"));		
		String action = req.getParameter("action");
		if(action!=null && "delete".equals(action)){
		    // Delete from history
		    Long scenarioId = new Long(req.getParameter("scenarioId"));
		    
		    store.deleteHistoricalScenario(scenarioId);
		}

		MockServiceBean ms = store.getMockServiceById(serviceId);
		List scenarios = store.getHistoryScenarios();
		List scenarioHistoryList = new ArrayList();
		if(scenarios!=null){
		    Iterator iter = scenarios.iterator();
		    while (iter.hasNext()) {
                MockServiceScenarioBean type = (MockServiceScenarioBean) iter.next();
                if(type.getServiceId().equals(serviceId) && type.getConsumerId().equals(iprequest)){
                    scenarioHistoryList.add(type);
                }
                
            }
		}
		
		req.setAttribute("scenarioHistoryList", scenarioHistoryList);
		req.setAttribute("mockservice", ms);
		req.setAttribute("iprequest", iprequest);

		RequestDispatcher dispatch = req.getRequestDispatcher("/service_history_ip.jsp");
		dispatch.forward(req, resp);
	}
}
