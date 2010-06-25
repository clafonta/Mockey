package com.mockey.ui;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Returns an HTML representations of the service .scenario
 * 
 * @author chad.lafontaine
 * 
 */
public class ScenarioViewAjaxServlet extends HttpServlet {

	private static final long serialVersionUID = 6258997861605811341L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		Long serviceId = new Long(req.getParameter("serviceId"));
		String scenarioIdAsString = req.getParameter("scenarioId");
		Service service = store.getServiceById(serviceId);
		Scenario scenario = null;
		PrintWriter out = resp.getWriter();
		try {
			scenario = service.getScenario(new Long(scenarioIdAsString));
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("serviceId", ""+serviceId);
			jsonObject.put("scenarioId", ""+ scenario.getId());
			jsonObject.put("name", scenario.getScenarioName());
			jsonObject.put("match", scenario.getMatchStringArg());
			jsonObject.put("response", scenario.getResponseMessage());
			/*
			output.append("<div id='edit-scenario-scenarioId_" + scenario.getId() + "serviceId_" + serviceId
					+ "' class='scenario-preview'>");
			output.append("<p class='edit-scenario-name-label'>Name:</p>");
			output.append("<p><input type='text' class='edit-scenario-name-value text ui-widget-content ui-corner-all' id='edit-scenario-name-scenarioId_" + scenario.getId() + "serviceId_"
					+ serviceId + "' value='" + scenario.getScenarioName().trim() + "'/></p>");
			output.append("<p class='edit-scenario-match-label'>Match:</p>");
			output.append("<p><input type='text' class='edit-scenario-match-value text ui-widget-content ui-corner-all' id='edit-scenario-match-scenarioId_" + scenario.getMatchStringArg()
					+ "serviceId_" + serviceId + "' value='" + scenario.getScenarioName().trim() + "'/></p>");
			output.append("<p class='edit-scenario-response-label'>Response:</p>");
			output.append("<p><textarea class='resizable edit-scenario-response-value text ui-widget-content ui-corner-all' id='edit-scenario-response-scenarioId_" + scenario.getMatchStringArg()
					+ "serviceId_" + serviceId + "'  rows='10'>"
					+ scenario.getResponseMessage().trim() + "</textarea></p>");
			output.append("<p><a href='#' class='edit-scenario-save-button' id='edit-scenario-save-button'>Save scenario</a></p>");
			output.append("</div>");
			*/
			out.println(jsonObject.toString());
		} catch (Exception e) {
			out.println("{ \"error\": \"Unable to find scenario \"}");
		}
		out.flush();
		out.close();
		return;

	}
}
