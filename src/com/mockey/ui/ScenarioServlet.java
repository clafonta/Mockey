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
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.ScenarioValidator;
import com.mockey.model.Service;
import com.mockey.model.Scenario;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.XmlMockeyStorage;

public class ScenarioServlet extends HttpServlet {

    private static final long serialVersionUID = -5920793024759540668L;
    private static IMockeyStorage store = XmlMockeyStorage.getInstance();

    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Long serviceId = new Long(req.getParameter("serviceId"));
        Long scenarioId = null;
        try {
            scenarioId = new Long(req.getParameter("scenarioId"));
        } catch (Exception e) {
            // 
        }

        // HACK: saving large message scenarios via GET will reach
        // the threshold for parameter size, thus we need to override
        // certain POST action types, and redirect them to the GET
        // method.
        String actionTypeGetFlag = req.getParameter("actionTypeGetFlag");

        if (req.getParameter("delete") != null && serviceId != null && scenarioId != null) {
            Service service = store.getMockServiceById(serviceId);
            service.deleteScenario(scenarioId);
            store.saveOrUpdate(service);
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
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Long serviceId = new Long(req.getParameter("serviceId"));
        Long scenarioId = null;
        try {
            scenarioId = new Long(req.getParameter("scenarioId"));
        } catch (Exception e) {
            //
        }

        String responseMsg = req.getParameter("responseMessage");

        Service service = store.getMockServiceById(serviceId);

        Scenario scenario = service.getScenario(scenarioId);
        if (scenario == null) {
            scenario = new Scenario();
        }

        if (responseMsg != null) {
            scenario.setResponseMessage(responseMsg);
        }

        req.setAttribute("mockservice", service);
        req.setAttribute("mockscenario", scenario);
        req.setAttribute("universalErrorScenario", store.getUniversalErrorResponse());
        RequestDispatcher dispatch = req.getRequestDispatcher("/service_scenario_setup.jsp");
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
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Long serviceId = new Long(req.getParameter("serviceId"));
        Service service = store.getMockServiceById(serviceId);

        Scenario scenario = null;
        try {
            scenario = service.getScenario(new Long(req.getParameter("scenarioId")));
        } catch (Exception e) {
            //
        }

        if (scenario == null) {
            scenario = new Scenario();
        }

        scenario.setScenarioName(req.getParameter("scenarioName"));
        scenario.setResponseMessage(req.getParameter("responseMessage"));
        scenario.setMatchStringArg(req.getParameter("matchStringArg"));

        Map<String, String> errorMap = ScenarioValidator.validate(scenario);

        if ((errorMap != null) && (errorMap.size() == 0)) {

            scenario = service.updateScenario(scenario);

            // Error response for this service.
            if (req.getParameter("errorScenario") != null) {
                service.setErrorScenarioId(scenario.getId());
            } else if (service.getErrorScenarioId() == scenario.getId()) {
                service.setErrorScenarioId(null);
            }

            // Universal error response, for all services.
            if (req.getParameter("universalErrorScenario") != null) {
                store.setUniversalErrorScenarioId(scenario.getId());
                store.setUniversalErrorServiceId(serviceId);
                
            } else if (store.getUniversalErrorResponse() != null
                    && store.getUniversalErrorResponse().getId() == scenario.getId()) {
                store.setUniversalErrorScenarioId(null);
                store.setUniversalErrorServiceId(null);
            }

            store.saveOrUpdate(service);
            Util.saveSuccessMessage("Service updated", req);

        }

        req.setAttribute("mockscenario", scenario);
        req.setAttribute("mockservice", service);
        req.setAttribute("universalErrorScenario", store.getUniversalErrorResponse());
        Util.saveErrorMap(errorMap, req);

        RequestDispatcher dispatch = req.getRequestDispatcher("/service_scenario_setup.jsp");
        dispatch.forward(req, resp);
    }
}
