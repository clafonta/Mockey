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
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.MockServiceBean;
import com.mockey.MockServiceScenarioBean;
import com.mockey.MockServiceScenarioValidator;
import com.mockey.MockServiceStore;
import com.mockey.MockServiceStoreImpl;

public class MockServiceScenarioServlet extends HttpServlet {

	private static final long serialVersionUID = -5920793024759540668L;
	private static MockServiceStore store = MockServiceStoreImpl.getInstance();

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Long serviceId = new Long(req.getParameter("serviceId")); 
		Long scenarioId = null;
		try {
			scenarioId = new Long(req.getParameter("scenarioId"));
		}catch(Exception e){
			// 
		}
		
		// HACK: saving large message scenarios via GET will reach
		// the threshold for parameter size, thus we need to override
		// certain POST action types, and redirect them to the GET
		// method.
		String actionTypeGetFlag = req.getParameter("actionTypeGetFlag");

		if (req.getParameter("delete") != null && serviceId != null
				&& scenarioId != null) {
			MockServiceBean ms = store.getMockServiceById(serviceId);
			ms.deleteScenario(scenarioId);
			store.saveOrUpdate(ms);
			resp.sendRedirect("setup?id=" + serviceId);
			return;
		}
		if (req.getParameter("cancel") != null) {
			resp.sendRedirect("setup?id=" + serviceId);
			return;
		}

		if (actionTypeGetFlag != null) {
			doGet(req, resp);
		} else {
			super.service(req, resp);
		}

	}

	/**
	 * 
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
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Long serviceId = new Long(req.getParameter("serviceId"));
		Long scenarioId = null;
		try {
			scenarioId = new Long(req.getParameter("scenarioId"));
		} catch (Exception e) {
			//
		}

		
		String responseMsg = req.getParameter("responseMessage");

		MockServiceBean ms = store.getMockServiceById(serviceId);

		MockServiceScenarioBean mss = ms.getScenario(scenarioId);
		if (mss == null) {
			mss = new MockServiceScenarioBean();
		}
		
		if (responseMsg != null) {
			mss.setResponseMessage(responseMsg);
		}

		req.setAttribute("mockservice", ms);
		req.setAttribute("mockscenario", mss);

		RequestDispatcher dispatch = req
				.getRequestDispatcher("/service_scenario_setup.jsp");
		dispatch.forward(req, resp);
	}

	/**
	 * 
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
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Long serviceId = new Long(req.getParameter("serviceId"));
		MockServiceBean ms = store.getMockServiceById(serviceId);

		MockServiceScenarioBean mss = null;
		try {
			mss = ms.getScenario(new Long(req.getParameter("scenarioId")));
		} catch (Exception e) {
			//
		}

		if (mss == null) {
			mss = new MockServiceScenarioBean();
		}
		mss.setScenarioName(req.getParameter("scenarioName"));		
		mss.setResponseMessage(req.getParameter("responseMessage"));
		mss.setMatchStringArg(req.getParameter("matchStringArg"));

		Map errorMap = MockServiceScenarioValidator.validate(mss);

		if ((errorMap != null) && (errorMap.size() == 0)) {

			Util.saveSuccessMessage("Service updated", req);
			ms.updateScenario(mss);
			store.saveOrUpdate(ms);

		}

		req.setAttribute("mockscenario", mss);
		req.setAttribute("mockservice", ms);
		Util.saveErrorMap(errorMap, req);

		RequestDispatcher dispatch = req
				.getRequestDispatcher("/service_scenario_setup.jsp");
		dispatch.forward(req, resp);
	}
}
