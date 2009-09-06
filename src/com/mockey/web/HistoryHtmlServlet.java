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
package com.mockey.web;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.model.Scenario;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.XmlMockeyStorage;

/**
 * Accepts requests for "who hit this service" and returns content. Servlet does
 * not forward data to a JSP, instead produces content to display inline with
 * HTML/JSP. Consumer of this servlet is an AJAX call.
 * 
 */
public class HistoryHtmlServlet extends HttpServlet {

	private static final long serialVersionUID = 9089211154525468963L;
	private static IMockeyStorage store = XmlMockeyStorage.getInstance();

	/**
	 * Handles three types of requests:
	 * 
	 * <pre>
	 *       1 - returns a list of IP addresses on service name
	 *       2 - returns a list of recorded scenarios based on IP address
	 *       3 - returns a recorded scenario  based on &quot;IP &amp; Time&quot; key.
	 * </pre>
	 * 
	 * @param req
	 *            basic request
	 * @param resp
	 *            basic resp
	 * @throws ServletException
	 *             basic
	 * @throws IOException
	 *             basic
	 */
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// HOW to get the list of requesting IP addresses...
		//
		String returnHTML = new String();
		Long serviceId = null;
		try {
			serviceId = new Long(req.getParameter("serviceId"));
		} catch (Exception e) {
			// do nothing
		}
		String ipAddressAsKey = req.getParameter("iprequest");
		String scenarioNameAsIpAndTime = req.getParameter("scenarioNameAsIpAndTime");

		// 1. If only service name, then return list of IP addresses.
		if (serviceId != null && ipAddressAsKey == null && scenarioNameAsIpAndTime == null) {
			returnHTML = getListOfIpAddresses(serviceId);
		}
		// 2. If serviceName AND ipAddressAsKey only, then return the list of
		// recorded scenarios.
		else if (serviceId != null && ipAddressAsKey != null && scenarioNameAsIpAndTime == null) {
			returnHTML = getListOfRecordedScenarios(serviceId, ipAddressAsKey);
		}
		// 3. If you have all three arguments, then get the XML for the
		// recorded service scenario
		else if (serviceId != null && ipAddressAsKey != null && scenarioNameAsIpAndTime != null) {
			returnHTML = getRecordedServiceScenario(serviceId, ipAddressAsKey, scenarioNameAsIpAndTime);
		} else {
			returnHTML = ("<div class=\"normal\">Nothing here</div>");
		}

		PrintStream out = new PrintStream(resp.getOutputStream());
		out.println(returnHTML);
	}

	/**
	 * Produces a list of HTML <i>div</i> elements, each containing an IP
	 * address. The list of IP addresses are clients that made a request to the
	 * specific service of <i>serviceId</i>.
	 * 
	 * @param serviceId
	 * @return
	 */
	private String getListOfIpAddresses(Long serviceId) {
		StringBuffer sb = new StringBuffer();

		List scenarioList = store.getHistoryScenarios();
		Map visitedAddresses = new HashMap();
		if (scenarioList != null && scenarioList.size() > 0) {
			Iterator iter = scenarioList.iterator();
			while (iter.hasNext()) {
				Scenario item = (Scenario) iter.next();
				if (item.getServiceId().equals(serviceId) && visitedAddresses.get(item.getRequestorIP()) == null) {
					sb.append("<div class=\"normal\">");
					sb.append("IP: <a href=\"detail?serviceId=" + serviceId.longValue() + "&iprequest="
							+ item.getRequestorIP() + "\">" + item.getRequestorIP() + "</a>");
					sb.append("</div>");
					visitedAddresses.put(item.getRequestorIP(), new Boolean(true));
				}
			}
		} else {
			sb.append("Empty - no requests have been made.");
		}
		return sb.toString();
	}

	/**
	 * Produces a list of HTML <i>div</i> elements, each containing a recorded
	 * <i>request</i> and <i>response</i>. The list of recorded scenarios is
	 * associated to a service of <i>serviceId</i> and the specific IP.
	 * 
	 * @param serviceId
	 * @return
	 */
	private String getListOfRecordedScenarios(Long serviceId, String requestIp) {
		StringBuffer sb = new StringBuffer();
		List scenarioList = store.getHistoryScenarios();
		if (scenarioList != null && scenarioList.size() > 0) {
			Iterator iter = scenarioList.iterator();
			while (iter.hasNext()) {
				Scenario item = (Scenario) iter.next();
				if (item.getServiceId().equals(serviceId) && requestIp.equalsIgnoreCase(item.getRequestorIP())) {
					sb.append("<div class=\"normal\">");
					sb.append("IP: <a href=\"checkforhistory?id=" + serviceId + "&iprequest=" + requestIp
							+ "&scenarioNameAsIpAndTime=" + item.getScenarioName() + "\">" + item.getScenarioName()
							+ "</a>");
					sb.append("</div>");
				}
			}
		}
		if (sb.length() == 0) {
			sb.append("Empty - could not find scenarios with service id '");
			sb.append(serviceId + "' and IP address '");
			sb.append(requestIp + "'");
		}
		return sb.toString();
	}

	private String getRecordedServiceScenario(Long id, String ipAddressAsKey, String scenarioNameAsIpAndTime) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		sb.append("<service-scenario>");
		sb.append("<scenario-name>");
		sb.append("</scenario-name>");
		sb.append("<scenario-request>");
		sb.append("</scenario-request>");
		sb.append("<scenario-response>");
		sb.append("</scenario-response>");
		sb.append("</service-scenario>");
		return sb.toString();

	}

}
