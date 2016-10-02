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

import com.google.gson.Gson;
import com.mockey.model.FulfilledClientRequest;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Returns JSON of the fulfilled request, designed to be consumed by an AJAX
 * call.
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
		PrintStream out = null;
		Gson gson = new Gson();
		HistoryHelper hh = new HistoryHelper();
		try {

			fulfilledRequestId = new Long(req.getParameter("conversationRecordId"));
			FulfilledClientRequest fCRequest = store.getFulfilledClientRequestsById(fulfilledRequestId);

			hh.setConversationRecordId("" + fulfilledRequestId);
			hh.setServiceId("" + fCRequest.getServiceId());
			hh.setServiceName(fCRequest.getServiceName());
			hh.setRequestUrl(fCRequest.getRawRequest());
			hh.setRequestHeaders(fCRequest.getClientRequestHeaders());
			hh.setRequestParameters(fCRequest.getClientRequestParameters());
			hh.setRequestBody(fCRequest.getClientRequestBody());
			hh.setRequestCookies(fCRequest.getClientRequestCookies());
			hh.setResponseCookies(fCRequest.getClientResponseCookies());
			hh.setResponseStatus("" + fCRequest.getResponseMessage().getHttpResponseStatusCode());
			hh.setResponseHeader(fCRequest.getResponseMessage().getHeaderInfo());
			hh.setResponseBody(fCRequest.getResponseMessage().getBody());
			hh.setResponseScenarioName(fCRequest.getScenarioName());
			hh.setResponseScenarioTags(fCRequest.getScenarioTagsAsString());

			resp.setContentType("application/json");

			out = new PrintStream(resp.getOutputStream());
			Util.logMemoryFootprint();
			out.println(gson.toJson(hh));
			out.close();
			hh = null;

		} catch (Throwable e) {
			try {
				logger.error("Unable to create response: " + e.getMessage(), e);
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				out = new PrintStream(resp.getOutputStream());
				out.println("System error");
				out.close();
			} catch (Exception e1) {
				logger.error("Unable to create JSON", e1);
			}
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {

			}
		}

	}

	public static void main(String[] args) {
		HistoryHelper hh = new HistoryHelper();
		hh.setConversationRecordId("abc");
		Gson gson = new Gson();
		String json = gson.toJson(hh);
		System.out.println(json);

	}

}
