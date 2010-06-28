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
 * Returns an HTML form of the fulfilled request, designed to be consumed by an
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
            
            jsonObject.put("responseStatus", ""+fCRequest.getResponseMessage().getStatusLine());
            jsonObject.put("responseHeader", ""+fCRequest.getResponseMessage().getHeaderInfo());
            jsonObject.put("responseBody", ""+fCRequest.getResponseMessage().getBody());


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
