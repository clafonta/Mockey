package com.mockey.ui;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Long serviceId = new Long(req.getParameter("serviceId"));
		Service service = store.getServiceById(serviceId);
		Scenario scenario = null;
		try {
			scenario = service.getScenario(new Long(req
					.getParameter("scenarioId")));
		} catch (Exception e) {
			//
		}
//thin solid #bdbdbd
		PrintWriter out = resp.getWriter();
		StringBuffer output = new StringBuffer();
		output.append("<div class=\"scenario-preview\">");
		output.append("<p>Name:</p>");
		output.append("<p id=\"scenario-preview-name\">" + scenario.getScenarioName().trim() + "</p>");
		output.append("<p id=\"scenario-preview-response\" >Response:</p>");
		output.append("<p id=\"scenario-preview-response-textarea\"><textarea>" + scenario.getResponseMessage().trim() + "</textarea></p>");
		output.append("</div>");
		out.println(output.toString());
		out.flush();
		out.close();
		return;

	}
}
