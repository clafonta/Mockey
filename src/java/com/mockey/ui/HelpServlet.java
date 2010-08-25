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
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.model.Url;

/**
 * Help Servlet
 * 
 * @author chad.lafontaine
 *
 */
public class HelpServlet extends HttpServlet {

   
    private static final long serialVersionUID = 8793774336587312539L;

    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

       
        RequestDispatcher dispatch = req.getRequestDispatcher("help.jsp");

        // HINT Message
        URL serverURLObj = new URL(req.getScheme(), // http
                req.getServerName(), // host
                req.getServerPort(), // port
                "");

        String contextRoot = req.getContextPath();
        String hintRecordURL1 = serverURLObj.toString();
        String hintRecordURL2 = serverURLObj.toString();

        
        if (contextRoot != null && contextRoot.length() > 0 ) {
            hintRecordURL1 = hintRecordURL1 + contextRoot;
            hintRecordURL2 = hintRecordURL2 + contextRoot;
        }
        hintRecordURL1 = hintRecordURL1 + Url.MOCK_SERVICE_PATH + "http://www.google.com/search?q=flavor";
        hintRecordURL2 = hintRecordURL2 + Url.MOCK_SERVICE_PATH + "http://e-services.doh.go.th/dohweb/dohwebservice.asmx?wsdl";
        req.setAttribute("hintRecordUrl1", hintRecordURL1);
        req.setAttribute("hintRecordUrl2", hintRecordURL2);
        dispatch.forward(req, resp);
    }

}

