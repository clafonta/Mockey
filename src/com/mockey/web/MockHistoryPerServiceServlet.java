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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.MockServiceBean;
import com.mockey.MockServiceStore;
import com.mockey.MockServiceStoreImpl;

/**
 * 
 * 
 * @author chad.lafontaine
 */
public class MockHistoryPerServiceServlet extends HttpServlet {

    private static final long serialVersionUID = 6606106522000844746L;
    private static MockServiceStore store = MockServiceStoreImpl.getInstance();

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String serviceIdAsString = req.getParameter("serviceId");
        Long serviceId = new Long(serviceIdAsString);
        MockServiceBean ms = store.getMockServiceById(new Long(serviceId));
        req.setAttribute("mockservice", ms);
        
        List htmlOutput = new ArrayList();
        List scenarioList = store.getHistoryScenarios();
		Map visitedAddresses = new HashMap();
		if (scenarioList != null && scenarioList.size() > 0) {
			Iterator iter = scenarioList.iterator();
			while (iter.hasNext()) {
			    RequestResponseTransaction item = (RequestResponseTransaction) iter.next();
				if (item.getServiceInfo().getServiceId().equals(serviceId) && visitedAddresses.get(item.getServiceInfo().getConsumerId()) == null) {
					StringBuffer sb = new StringBuffer();
					sb.append("<div class=\"normal\">");
					sb.append("IP: <a href=\"detail?serviceId=" + serviceId + "&iprequest="
							+ item.getServiceInfo().getConsumerId() + "\">" + item.getServiceInfo().getConsumerId() + "</a>");
					sb.append("</div>");
					htmlOutput.add(sb.toString());
					visitedAddresses.put(item.getServiceInfo().getConsumerId(), new Boolean(true));
				}
			}
		} else {
			htmlOutput.add("Empty - no requests have been made.");
		}
		
		req.setAttribute("ip_addresses", htmlOutput);
        RequestDispatcher dispatch = req.getRequestDispatcher("/service_history.jsp");
        dispatch.forward(req, resp);
    }

}
