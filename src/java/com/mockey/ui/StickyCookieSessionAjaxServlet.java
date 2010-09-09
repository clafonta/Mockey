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
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.ClientExecuteProxy;

/**
 * Returns a JSON to validate that sticky cookie session has been reset/flushed.
 * 
 */
public class StickyCookieSessionAjaxServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1361928261096258669L;
	private static Logger logger = Logger.getLogger(StickyCookieSessionAjaxServlet.class);

    /**
     * 
     */
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JSONObject jsonObject = new JSONObject();
        try {
        	ClientExecuteProxy.resetStickySession();
            
            jsonObject.put("reset",true);
           


        } catch (Exception e) {
        	 try {
				jsonObject.put("error", "Unable to reset sticky session");
			} catch (JSONException e1) {
				logger.error("Unable to create JSON", e1);
			}
        } 

        resp.setContentType("application/json");

        PrintStream out = new PrintStream(resp.getOutputStream());

        out.println(jsonObject.toString());
    }

}
