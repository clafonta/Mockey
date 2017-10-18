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
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.HistoryFilter;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * <code>HistoryServlet</code> produces a list of fulfilled requests and
 * responses (history). Moreover, this servlet accepts String tokens to filter
 * the end list.
 * 
 * 
 * @author Chad Lafontaine (chad.lafontaine)
 */
public class HistoryServlet extends HttpServlet {

    private static final long serialVersionUID = -2255013290808524662L;
    private static final Logger logger = Logger.getLogger(HistoryServlet.class);
    private static final String HISTORY_FILTER = "historyFilter";

    private static IMockeyStorage store = StorageRegistry.MockeyStorage;

    /**
     * 
     */
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String[] filterTokens = req.getParameterValues("token");
        HistoryFilter historyFilter = (HistoryFilter) req.getSession().getAttribute(HISTORY_FILTER);
        if (historyFilter == null) {
            historyFilter = new HistoryFilter();

        }

        String action = req.getParameter("action");

        if (action != null && "delete_all".equals(action)) {
            store.deleteFulfilledClientRequests();
            // don't allow reloads to re-delete.
            String absolutePath = Url.getAbsoluteURL(req, "/history");
            resp.sendRedirect(absolutePath); 
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

        }  else if (action != null && "tag".equals(action)) {
            String fulfilledRequestId = req.getParameter("fulfilledRequestId");
            
            try {
            	FulfilledClientRequest ffcr = store.getFulfilledClientRequestsById(new Long(fulfilledRequestId));
            	if(ffcr.getComment()!=null){
            		ffcr.setComment(null);
            	}else {
            	    ffcr.setComment("tagged");
            	}
            	store.saveOrUpdateFulfilledClientRequest(ffcr);
            } catch (Exception e) {
                logger.error("Unable to tag history of a fulfilled request with id:" + fulfilledRequestId, e);
            }
            // Ajax used in page, so don't return anything
            return;

        } else if (action != null && "remove_token".equals(action)) {
            historyFilter.deleteTokens(filterTokens);
        } else if (action != null && "remove_all_tokens".equals(action)) {
            historyFilter = new HistoryFilter();
        } else {

            historyFilter.addTokens(filterTokens);
        }
        List<FulfilledClientRequest> fulfilledRequests = store.getFulfilledClientRequest(historyFilter.getTokens());
        req.setAttribute("requests", fulfilledRequests);
        req.getSession().setAttribute(HISTORY_FILTER, historyFilter);
        RequestDispatcher dispatch = req.getRequestDispatcher("/history.jsp");
        dispatch.forward(req, resp);
    }
}
