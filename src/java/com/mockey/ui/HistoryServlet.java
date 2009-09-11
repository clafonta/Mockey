/*
 * Copyright 2002-2010 the original author or authors.
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

import org.apache.log4j.Logger;

import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.HistoryFilter;
import com.mockey.model.HistoryFilterType;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * <code>HistoryServlet</code> manages the display of past requests and accepts
 * arguments to filter display.
 * 
 * @author Chad Lafontaine (chad.lafontaine)
 */
public class HistoryServlet extends HttpServlet {

    private static final long serialVersionUID = -2255013290808524662L;
    private static final Logger logger = Logger.getLogger(HistoryServlet.class);

    private static IMockeyStorage store = StorageRegistry.MockeyStorage;

    /**
     * 
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Filter by request IP
        List<HistoryFilter> historyFilterList = new ArrayList<HistoryFilter>();
        String filterByIP = req.getParameter(HistoryFilterType.IP.getKey());
        if(filterByIP!=null){
            historyFilterList.add(new HistoryFilter());
        }
        Long filterByServiceId = null;

        try {
            filterByServiceId = new Long(req.getParameter(HistoryFilterType.SERVICE.getKey()));

        } catch (Exception e) {

        }
        String action = req.getParameter("action");

        if (action != null && "delete_all".equals(action)) {
            store.deleteFulfilledClientRequests();
            // don't allow reloads to re-delete.
            String contextRoot = req.getContextPath();
            resp.sendRedirect(Url.getContextAwarePath("/history", contextRoot));
            return;
        } else if (action != null && "delete".equals(action)) {
            String fulfilledRequestId = req.getParameter("fulfilledRequestId");
            try {                
                store.deleteFulfilledClientRequestById(new Long(fulfilledRequestId));
            } catch (Exception e) {
                logger.error("Unable to delete fulfilled request with id:" + fulfilledRequestId, e);
            }
            // Ajax used in page, so don't return anything
            return;

        }
        List<FulfilledClientRequest> fulfilledRequests = null;
        

        if (filterByIP == null && filterByServiceId == null) {
            fulfilledRequests = store.getFulfilledClientRequests();
        } else if (filterByIP != null && filterByServiceId == null) {
            fulfilledRequests = store.getFulfilledClientRequestsFromIP(filterByIP);
        } else if (filterByIP == null && filterByServiceId != null) {
            fulfilledRequests = store.getFulfilledClientRequestsForService(filterByServiceId);
        } else if (filterByIP != null && filterByServiceId != null) {
            fulfilledRequests = store.getFulfilledClientRequestsFromIPForService(filterByIP, filterByServiceId);
        }

        req.setAttribute("requests", fulfilledRequests);
        req.setAttribute("iprequest", filterByIP);
        RequestDispatcher dispatch = req.getRequestDispatcher("/history.jsp");
        dispatch.forward(req, resp);
    }
}
