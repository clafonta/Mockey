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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Yah, that's right, this Servlet is here so we can update a mock service from a
 * crappy-ass browser, like a BlackBerry browser.
 * 
 * @author chad.lafontaine
 * 
 */
public class MobiServlet extends HttpServlet {

    private static final long serialVersionUID = -4357829693367863051L;
    private static IMockeyStorage store = StorageRegistry.MockeyStorage;

    /**
     * Get all the services
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String serviceId = req.getParameter("serviceId");
        if(serviceId!=null){
            Long id = new Long(serviceId);
            req.setAttribute("filter", "yes");
            List<Service> list = new ArrayList<Service>();
            list.add(store.getServiceById(id));
            req.setAttribute("services", list);
        }else {
            req.setAttribute("services", Util.orderAlphabeticallyByServiceName(store.getServices()));
            
        }
        req.setAttribute("allservices", Util.orderAlphabeticallyByServiceName(store.getServices()));
        RequestDispatcher dispatch = req.getRequestDispatcher("mobi.jsp");
        dispatch.forward(req, resp);
    }

    /**
     * Update a Service and return all the services. 
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            Long serviceId = new Long(req.getParameter("serviceId"));
            int hangTime = Integer.parseInt(req.getParameter("hangTime"));
            int serviceResponseType = Integer.parseInt(req.getParameter("serviceResponseType"));
            Long defaultScenarioId = Long.parseLong(req.getParameter("scenario"));
            Service service = store.getServiceById(serviceId);
            service.setServiceResponseType(serviceResponseType);
            service.setHangTime(hangTime);
            service.setDefaultScenarioId(defaultScenarioId);
            store.saveOrUpdateService(service);
            String filter = req.getParameter("filter");
            if(filter!=null && "yes".equals(filter)){
                req.setAttribute("filter", "yes");
                List<Service> list = new ArrayList<Service>();
                list.add(service);
                req.setAttribute("services", list);
            }else {
                req.setAttribute("services", store.getServices());
            }

        } catch (Exception e) {
            req.setAttribute("services", Util.orderAlphabeticallyByServiceName(store.getServices()));
        }
        
        req.setAttribute("allservices", Util.orderAlphabeticallyByServiceName(store.getServices()));
        RequestDispatcher dispatch = req.getRequestDispatcher("mobi.jsp");
        dispatch.forward(req, resp);
        
    }

}
