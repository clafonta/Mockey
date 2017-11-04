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
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

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
            String contextRootScenarioURL = Url.getAbsoluteURL(req, "/scenario"); 
            
            returnHTML.append("<script type=\"text/javascript\">");
            returnHTML.append("$(document).ready(function() {");
            returnHTML.append("    $('textarea.resizable:not(.processed)').TextAreaResizer();");
            returnHTML.append("    $('iframe.resizable:not(.processed)').TextAreaResizer();");
            returnHTML.append("});");
            returnHTML.append("</script>");
            returnHTML.append("<table class=\"history\" width=\"100%\">");
            returnHTML.append("     <tbody>");
            returnHTML.append("<tr>");
            returnHTML.append("<td>");
            returnHTML.append("<div class=\"conflict_message\">");
            String doitagainUrl = Url.getAbsoluteURL(req, "/doitagain"); 
            returnHTML.append("<form id=\"child\" action=\""+doitagainUrl+"\" method=\"post\" style=\"background-color:#FFD7D7\" >");
            returnHTML.append("<input type=\"hidden\" name=\"fulfilledClientRequestId\" value=\""+fCRequest.getId()+"\" />");
            returnHTML.append("<h2>Request:</h2>");
            returnHTML.append("<p><h4>" + fCRequest.getRawRequest() + "</h4></p>");
            returnHTML.append("<p>Header (pipe delimited)</p>");
            returnHTML.append("<p><textarea class=\"resizable\" name=\"requestHeader\" rows=\"5\" cols=\"100%\">"
                    + fCRequest.getClientRequestHeaders() + "</textarea></p>");
            returnHTML.append("<p>Parameters (pipe delimited)</p>");
            returnHTML.append("<p><textarea class=\"resizable\" name=\"requestParameters\" rows=\"5\" cols=\"100%\">"
                    + fCRequest.getClientRequestParameters() + "</textarea></p>");
            returnHTML.append("<p>Body</p>");
            returnHTML.append("<p><textarea class=\"resizable\" name=\"requestBody\" rows=\"10\" >"
                    + fCRequest.getClientRequestBody() + "</textarea></p>");
            returnHTML.append("<input type=\"submit\" name=\"NewParameters\" value=\"Send This Again\" class=\"button\" />");
            returnHTML.append(" This will build a request with the body, parameters, and header information above. ");
            returnHTML.append("</form>");
            returnHTML.append("</div>");
            returnHTML.append("</td>");
            returnHTML.append("</tr>");
            returnHTML.append(" <tr>");
            returnHTML.append("<td >");
            returnHTML.append("<form id=\"child\" action=\"" + contextRootScenarioURL + "\" method=\"post\">");
            returnHTML.append("<input type=\"hidden\" name=\"actionTypeGetFlag\" value=\"true\" />");
            returnHTML
                    .append("<input type=\"hidden\" name=\"serviceId\" value=\"" + fCRequest.getServiceId() + "\" />");
            returnHTML.append("<div id=\"scenario" + fCRequest.getId()
                    + "\" class=\"addition_message mockeyResponse\">");
            returnHTML.append("<h2>Response: </h2>");
            returnHTML.append("<p>Status</p>");
            returnHTML.append("<p>");
            returnHTML.append("    <textarea class=\"resizable\" name=\"responseStatus\" rows=\"1\" >"
                    + fCRequest.getResponseMessage().getHttpResponseStatusCode() + "</textarea>");
            returnHTML.append("</p>");
            returnHTML.append("<p>Header</p>");
            returnHTML.append("<p>");
            returnHTML.append("<textarea class=\"resizable\" name=\"responseHeader\" rows=\"10\" >"
                    + fCRequest.getResponseMessage().getHeaderInfo() + "</textarea>");
            returnHTML.append("</p>");
            returnHTML.append("<p>Body</p>");
            returnHTML.append("<p>");
            returnHTML
                    .append("<textarea style=\"margin-top: 0px;\" name=\"responseMessage\" class=\"resizable responseContent\" rows=\"10\" >"
                            + StringEscapeUtils.escapeXml(fCRequest.getResponseMessage().getBody()) + "</textarea>");
            returnHTML.append("</p>");
            returnHTML.append("<p>");
            returnHTML.append("<input type=\"submit\" name=\"Save\" value=\"Save Response as a Scenario\" />");
            String inspectFulfilledRequestURL = Url.getAbsoluteURL(req, "/inspect"); 
            returnHTML.append(" View response body as: ");
            returnHTML.append("<a href=\"" + inspectFulfilledRequestURL + "?content_type=text/xml;&fulfilledRequestId="
                    + fulfilledRequestId + "\">XML</a> ");
            returnHTML.append("<a href=\"" + inspectFulfilledRequestURL
                    + "?content_type=text/plain;&fulfilledRequestId=" + fulfilledRequestId + "\">Plain</a> ");
            returnHTML.append("<a href=\"" + inspectFulfilledRequestURL + "?content_type=text/css;&fulfilledRequestId="
                    + fulfilledRequestId + "\">CSS</a> ");
            returnHTML.append("<a href=\"" + inspectFulfilledRequestURL
                    + "?content_type=application/json;&fulfilledRequestId=" + fulfilledRequestId + "\">JSON</a> ");
            String encoded = URLEncoder.encode("text/html;charset=utf-8", "utf-8");
            returnHTML.append("<a href=\"" + inspectFulfilledRequestURL + "?content_type=" + encoded
                    + "&fulfilledRequestId=" + fulfilledRequestId + "\">HTML</a> ");
            returnHTML.append("</p>");
            returnHTML.append("</form>");
            returnHTML.append("</div>");
            returnHTML.append("</td>");
            returnHTML.append("</tr>");
            returnHTML.append("</tbody>");
            returnHTML.append("</table>");
            

            //

        } catch (Exception e) {
            returnHTML.append("Sorry, history for this request is not available.");
        }

        resp.setContentType("text/html");

        PrintStream out = new PrintStream(resp.getOutputStream());

        out.println(returnHTML.toString());
    }

}
