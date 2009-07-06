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
import java.util.Iterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.MockServiceBean;
import com.mockey.MockServiceScenarioBean;
import com.mockey.MockServiceStore;
import com.mockey.MockServiceStoreImpl;

public class MockServiceConfigureServlet extends HttpServlet {

	private static final long serialVersionUID = 6213235739994307983L;
	private static MockServiceStore store = MockServiceStoreImpl.getInstance();

	/**
	 * 
	 * 
	 * @param req
	 *            basic request
	 * @param resp
	 *            basic resp
	 * 
	 * @throws ServletException
	 *             basic
	 * @throws IOException
	 *             basic
	 */
	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Long serviceId = new Long(req.getParameter("serviceId"));
		MockServiceBean ms = store.getMockServiceById(serviceId);
		String hangTime = req.getParameter("hangTime");
		String proxyOn = req.getParameter("proxyOn");
		String replyWithMatchingRequest = req
				.getParameter("replyWithMatchingRequest");

		try {
			if (replyWithMatchingRequest != null) {
				boolean replyWithMatchingRequestBool = Boolean.valueOf(
						replyWithMatchingRequest).booleanValue();
				ms.setReplyWithMatchingRequest(replyWithMatchingRequestBool);
			}
		} catch (Exception e) {
			// DO NOTHING. If previously set, then
			// should not override it.
		}

		// PROXY SETTING
		try {
			if (proxyOn != null) {
				boolean proxyOnAsBool = Boolean.valueOf(proxyOn).booleanValue();
				ms.setProxyOn(proxyOnAsBool);
			}
		} catch (Exception e) {
			// DO NOTHING. If previously set, then
			// should not override it.
		}

		// HANG TIME
		try {
			if (hangTime != null) {
				int hangTimeAsInt = Integer.parseInt(hangTime);
				ms.setHangTime(hangTimeAsInt);
			}
		} catch (Exception e) {
			ms.setHangTime(0);

		}

		// SETTING DEFAULT SCENARIO ID
		try {
			ms.setDefaultScenarioId(new Long(req
					.getParameter("defaultScenarioId")));

		} catch (Exception e) {
			// DO NOTHING. If default scenario was set before, nothing
			// should override it.
		}

		store.saveOrUpdate(ms);

		// And...check if flush cached requests has been checked.
		String clearRequest = req.getParameter("clearRequests");

		if (clearRequest != null) {
			store.flushHistoryRequestMsgs(serviceId);
		}

		req.setAttribute("mockservice", ms);

		if (req.getParameter("update") != null) {
			Util.saveSuccessMessage("Configuration updated", req);
		}
		RequestDispatcher dispatch = req
				.getRequestDispatcher("/service_configure.jsp");
		dispatch.forward(req, resp);
	}

	/**
	 * 
	 * 
	 * @param responseType
	 * @param ms
	 *            DOCUMENT ME!
	 * 
	 * @return
	 */
	private boolean isValidScenarioId(Long scenarioId, MockServiceBean ms) {
		if ((scenarioId != null)) {
			Iterator iter = ms.getScenarios().iterator();

			while (iter.hasNext()) {
				MockServiceScenarioBean mss = (MockServiceScenarioBean) iter
						.next();

				if (scenarioId.equals(mss.getId())) {
					return true;
				}
			}
		}

		return false;
	}
}
