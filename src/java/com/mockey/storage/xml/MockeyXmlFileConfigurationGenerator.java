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
package com.mockey.storage.xml;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mockey.model.PlanItem;
import com.mockey.model.ProxyServerModel;
import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.model.TwistInfo;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.ui.PatternPair;

/**
 * Builds DOM representing Mockey Service configurations.
 * 
 * @author chad.lafontaine
 * 
 */
public class MockeyXmlFileConfigurationGenerator extends XmlGeneratorSupport {
	/** Basic logger */
	// private static Logger logger =
	// Logger.getLogger(MockeyXmlFileConfigurationGenerator.class);

	/**
	 * Returns an element representing a mock service definitions file in XML.
	 * 
	 * @param document
	 *            parent DOM object of this element.
	 * @param cxmlObject
	 *            value object used to build element.
	 * @return Returns an element representing a cXML root element; if request
	 *         <code>null</code>, then empty element is returned e.g.
	 *         &lt;cXML/&gt;
	 */
	public Element getElement(Document document, IMockeyStorage store) {

		Element rootElement = document.createElement("mockservice");
		Scenario mssb = store.getUniversalErrorScenario();
		this.setAttribute(rootElement, "xml:lang", "en-US");
		this.setAttribute(rootElement, "version", "1.0");
		// Universal Service settings
		if (mssb != null) {
			this.setAttribute(rootElement, "universal_error_service_id", "" + mssb.getServiceId());
			this.setAttribute(rootElement, "universal_error_scenario_id", "" + mssb.getId());
		}
		this.setAttribute(rootElement, "universal_twist_info_id", "" + store.getUniversalTwistInfoId());

		// Proxy settings
		ProxyServerModel psm = store.getProxy();
		if (psm != null) {
			Element proxyElement = document.createElement("proxy_settings");
			proxyElement.setAttribute("proxy_url", psm.getProxyUrl());
			proxyElement.setAttribute("proxy_enabled", "" + psm.isProxyEnabled());
			rootElement.appendChild(proxyElement);
		}
		
		for(Service mockServiceBean : store.getServices()) {
			Element serviceElement = document.createElement("service");
			rootElement.appendChild(serviceElement);

			if (mockServiceBean != null) {
				// logger.debug("building XML representation for MockServiceBean:\n"
				// + mockServiceBean.toString());
				// *************************************
				// We do NOT want to write out ID.
				// If we did, then someone uploading this xml definition may
				// overwrite services
				// defined with the same ID.
				// serviceElement.setAttribute("id", mockServiceBean.getId());
				// *************************************
				serviceElement.setAttribute("name", mockServiceBean.getServiceName());
				serviceElement.setAttribute("description", getSafeForXmlOutputString(mockServiceBean.getDescription()));
				serviceElement.setAttribute("hang_time", getSafeForXmlOutputString("" + mockServiceBean.getHangTime()));
				serviceElement.setAttribute("url", getSafeForXmlOutputString("" + mockServiceBean.getUrl()));
				serviceElement.setAttribute("http_content_type",
						getSafeForXmlOutputString("" + mockServiceBean.getHttpContentType()));
				serviceElement.setAttribute("default_scenario_id",
						getSafeForXmlOutputString("" + (mockServiceBean.getDefaultScenarioId())));
				serviceElement.setAttribute("service_response_type",
						getSafeForXmlOutputString("" + mockServiceBean.getServiceResponseType()));
				serviceElement.setAttribute("default_real_url_index",
						getSafeForXmlOutputString("" + mockServiceBean.getDefaultRealUrlIndex()));

				// New real service URLs
				for (Url realUrl : mockServiceBean.getRealServiceUrls()) {
					Element urlElement = document.createElement("real_url");
					urlElement.setAttribute("url", getSafeForXmlOutputString(realUrl.getFullUrl()));
					// urlElement.appendChild(cdataResponseElement);
					serviceElement.appendChild(urlElement);
				}

				// Scenarios
				for(Scenario scenario :  mockServiceBean.getScenarios() ) {
					// logger.debug("building XML representation for MockServiceScenarioBean:\n"
					// + scenario.toString());
					Element scenarioElement = document.createElement("scenario");
					scenarioElement.setAttribute("id", scenario.getId().toString());
					scenarioElement.setAttribute("name", getSafeForXmlOutputString(scenario.getScenarioName()));

					Element scenarioMatchStringElement = document.createElement("scenario_match");
					CDATASection cdataMatchElement = document.createCDATASection(getSafeForXmlOutputString(scenario
							.getMatchStringArg()));
					scenarioMatchStringElement.appendChild(cdataMatchElement);
					scenarioElement.appendChild(scenarioMatchStringElement);

					Element scenarioResponseElement = document.createElement("scenario_response");
					CDATASection cdataResponseElement = document.createCDATASection(getSafeForXmlOutputString(scenario
							.getResponseMessage()));
					scenarioResponseElement.appendChild(cdataResponseElement);
					scenarioElement.appendChild(scenarioResponseElement);
					serviceElement.appendChild(scenarioElement);
				}
			}
		}

		// SERVICE PLANS
		if (store.getServicePlans() != null) {
			for (ServicePlan servicePlan : store.getServicePlans()) {
				Element servicePlanElement = document.createElement("service_plan");
				servicePlanElement.setAttribute("name", servicePlan.getName());
				servicePlanElement.setAttribute("description", servicePlan.getDescription());
				servicePlanElement.setAttribute("id", "" + servicePlan.getId());
				for (PlanItem pi : servicePlan.getPlanItemList()) {
					Element planItemElement = document.createElement("plan_item");
					planItemElement.setAttribute("hang_time", "" + pi.getHangTime());
					planItemElement.setAttribute("service_id", "" + pi.getServiceId());
					planItemElement.setAttribute("scenario_id", "" + pi.getScenarioId());
					planItemElement.setAttribute("service_response_type", "" + pi.getServiceResponseType());

					servicePlanElement.appendChild(planItemElement);
				}

				rootElement.appendChild(servicePlanElement);

			}
		}

		// TWIST CONFIGURATION
		if (store.getTwistInfoList() != null) {
			for (TwistInfo twistInfo : store.getTwistInfoList()) {
				Element twistConfigElement = document.createElement("twist_config");
				twistConfigElement.setAttribute("name", twistInfo.getName());
				twistConfigElement.setAttribute("id", "" + twistInfo.getId());
				for (PatternPair patternPair : twistInfo.getPatternPairList()) {
					Element patternPairElement = document.createElement("twist_pattern");
					patternPairElement.setAttribute("origination", "" + patternPair.getOrigination());
					patternPairElement.setAttribute("destination", "" + patternPair.getDestination());
					twistConfigElement.appendChild(patternPairElement);
				}
				rootElement.appendChild(twistConfigElement);
			}

		}

		return rootElement;
	}

	private String getSafeForXmlOutputString(String arg) {
		if (arg != null) {
			return arg.trim();
		} else {
			return "";
		}
	}
}
