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

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

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
            req.setAttribute("services", store.getServices());
            
        }
        req.setAttribute("allservices", store.getServices());
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
            req.setAttribute("services", store.getServices());
        }
       
        req.setAttribute("allservices", store.getServices());
        RequestDispatcher dispatch = req.getRequestDispatcher("mobi.jsp");
        dispatch.forward(req, resp);
        
    }

}
