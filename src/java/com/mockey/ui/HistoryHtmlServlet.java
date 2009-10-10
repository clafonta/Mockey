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

import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Returns an HTML form of the fulfilled request, designed to be consumed by an
 * AJAX call.
 * 
 */
public class HistoryHtmlServlet extends HttpServlet {

    private static final long serialVersionUID = 9089211154525468963L;
    private static IMockeyStorage store = StorageRegistry.MockeyStorage;

    /**
     * 
     */
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        StringBuffer returnHTML = new StringBuffer();

        Long fulfilledRequestId = null;
        try {

            fulfilledRequestId = new Long(req.getParameter("fulfilledRequestId"));
            FulfilledClientRequest fCRequest = store.getFulfilledClientRequestsById(fulfilledRequestId);
            String contextRootScenarioURL = Url.getContextAwarePath("/scenario", req.getContextPath());
            returnHTML.append("<form id=\"child\" action=\"" + contextRootScenarioURL + "\" method=\"post\">");
            returnHTML.append("<input type=\"hidden\" name=\"actionTypeGetFlag\" value=\"true\" />");
            returnHTML
                    .append("<input type=\"hidden\" name=\"serviceId\" value=\"" + fCRequest.getServiceId() + "\" />");
            returnHTML.append("<table class=\"history\" width=\"100%\">");
            returnHTML.append("     <tbody>");
            returnHTML.append("<tr>");
            returnHTML.append("<td>");
            returnHTML.append("<div class=\"conflict_message\">");
            returnHTML.append("<h2>Request:</h2>");
            returnHTML.append("<p><h4>" + fCRequest.getRawRequest() + "</h4></p>");
            returnHTML.append("<p>Header</p>");
            returnHTML.append("<p><textarea name=\"requestHeader\" rows=\"10\" cols=\"80%\">"
                    + fCRequest.getClientRequestHeaders() + "</textarea></p>");
            returnHTML.append("<p>Parameters</p>");
            returnHTML.append("<p><textarea name=\"requestHeader\" rows=\"10\" cols=\"80%\">"
                    + fCRequest.getClientRequestParameters() + "</textarea></p>");
            returnHTML.append("<p>Body</p>");
            returnHTML.append("<p><textarea name=\"requestMessage\" rows=\"10\" cols=\"80%\">"
                    + fCRequest.getClientRequestBody() + "</textarea></p>");
            returnHTML.append("</div>");
            returnHTML.append("</td>");
            returnHTML.append("</tr>");
            returnHTML.append(" <tr>");
            returnHTML.append("<td >");
            returnHTML.append("<div id=\"scenario" + fCRequest.getId()
                    + "\" class=\"addition_message mockeyResponse\">");
            returnHTML.append("<h2>Response: </h2>");
            returnHTML.append("<p>Status</p>");
            returnHTML.append("<p>");
            returnHTML.append("    <textarea name=\"responseStatus\" rows=\"1\" cols=\"80%\">"
                    + fCRequest.getResponseMessage().getStatusLine() + "</textarea>");
            returnHTML.append("</p>");
            returnHTML.append("<p>Header</p>");
            returnHTML.append("<p>");
            returnHTML.append("<textarea name=\"responseHeader\" rows=\"10\" cols=\"80%\">"
                    + fCRequest.getResponseMessage().getHeaderInfo() + "</textarea>");
            returnHTML.append("</p>");
            returnHTML.append("<p>Body</p>");
            returnHTML.append("<p>");
            returnHTML
                    .append("<button class=\"formatButton\" style=\"border: 1px solid #006; background: #ccf; margin-left: 60%; border-bottom-width:0;\">Format Body</button>");
            returnHTML
                    .append("<textarea style=\"margin-top: 0px;\" name=\"responseMessage\" class=\"responseContent\" rows=\"10\" cols=\"80%\">"
                            + fCRequest.getResponseMessage().getBody() + "</textarea>");
            returnHTML.append("</p>");
            returnHTML.append("<p>");
            returnHTML.append("<input type=\"submit\" name=\"Save\" value=\"Save Response as a Scenario\" />");
            returnHTML.append("</p>");
            returnHTML.append("</div>");
            returnHTML.append("</td>");
            returnHTML.append("</tr>");
            returnHTML.append("</tbody>");
            returnHTML.append("</table>");
            returnHTML.append("</form>");
            //

        } catch (Exception e) {
            returnHTML.append("Sorry, history for this request is not available.");
        }

        PrintStream out = new PrintStream(resp.getOutputStream());
        out.println(returnHTML);
    }

}
