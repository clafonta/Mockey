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
            String contextRootScenarioURL = Url.getContextAwarePath("/scenario", req.getContextPath());
            
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
            String contextRoot = req.getContextPath();
            String doitagainUrl = Url.getContextAwarePath("/doitagain", contextRoot);
            returnHTML.append("<form id=\"child\" action=\""+doitagainUrl+"\" method=\"post\" style=\"background-color:#FFD7D7\" >");
            returnHTML.append("<input type=\"hidden\" name=\"fulfilledClientRequestId\" value=\""+fCRequest.getId()+"\" />");
            returnHTML.append("<h2>Request:</h2>");
            returnHTML.append("<p><h4>" + fCRequest.getRawRequest() + "</h4></p>");
            returnHTML.append("<p>Header (pipe delimited)</p>");
            returnHTML.append("<p><textarea class=\"resizable\" name=\"requestHeader\" rows=\"5\" cols=\"100%\">"
                    + fCRequest.getClientRequestHeaders() + "</textarea></p>");
            returnHTML.append("<p>Parameters (pipe delimited)</p>");
            returnHTML.append("<textarea class=\"resizable\" name=\"requestParameters\" rows=\"5\" cols=\"100%\">"
                    + fCRequest.getClientRequestParameters() + "</textarea>");
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
                    + fCRequest.getResponseMessage().getStatusLine() + "</textarea>");
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
            String inspectFulfilledRequestURL = Url.getContextAwarePath("/inspect", req.getContextPath());
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
