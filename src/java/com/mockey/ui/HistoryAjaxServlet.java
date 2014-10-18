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

import com.mockey.model.FulfilledClientRequest;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Returns JSON of the fulfilled request, designed to be consumed by an
 * AJAX call.
 * 
 */
public class HistoryAjaxServlet extends HttpServlet {

	private static final long serialVersionUID = 4178219038104708097L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	private static Logger logger = Logger.getLogger(HistoryAjaxServlet.class);

    /**
     * 
     */
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Long fulfilledRequestId = null;
        JSONObject jsonObject = new JSONObject();
        try {

            fulfilledRequestId = new Long(req.getParameter("conversationRecordId"));
            FulfilledClientRequest fCRequest = store.getFulfilledClientRequestsById(fulfilledRequestId);
            
            jsonObject.put("conversationRecordId", ""+fulfilledRequestId);
            jsonObject.put("serviceId", ""+fCRequest.getServiceId());
            jsonObject.put("serviceName", fCRequest.getServiceName());
            jsonObject.put("requestUrl", ""+fCRequest.getRawRequest());
            jsonObject.put("requestHeaders", ""+fCRequest.getClientRequestHeaders());
            jsonObject.put("requestParameters", ""+fCRequest.getClientRequestParameters());
            jsonObject.put("requestBody", ""+fCRequest.getClientRequestBody());
            jsonObject.put("requestCookies", ""+fCRequest.getClientRequestCookies());
            jsonObject.put("responseCookies", ""+fCRequest.getClientResponseCookies());
            jsonObject.put("responseStatus", ""+fCRequest.getResponseMessage().getHttpResponseStatusCode());
            jsonObject.put("responseHeader", ""+fCRequest.getResponseMessage().getHeaderInfo());
            jsonObject.put("responseBody", ""+fCRequest.getResponseMessage().getBody());
            jsonObject.put("responseScenarioName", ""+fCRequest.getScenarioName());
            jsonObject.put("responseScenarioTags", ""+fCRequest.getScenarioTagsAsString());


        } catch (Exception e) {
        	 try {
				jsonObject.put("error", ""+"Sorry, history for this conversation (fulfilledRequestId="+fulfilledRequestId
						 +") is not available.");
			} catch (JSONException e1) {
				logger.error("Unable to create JSON", e1);
			}
        } 

        resp.setContentType("application/json");

        PrintStream out = new PrintStream(resp.getOutputStream());

        out.println(jsonObject.toString());
    }

}
