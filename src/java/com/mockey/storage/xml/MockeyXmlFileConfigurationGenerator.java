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

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
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
import java.util.Date;
/**
 * Builds DOM representing Mockey Service configurations.
 * 
 * @author chad.lafontaine
 * 
 */
public class MockeyXmlFileConfigurationGenerator extends XmlGeneratorSupport {
	/** Basic logger */
	private static Logger logger = Logger
			.getLogger(MockeyXmlFileConfigurationGenerator.class);

	private Document getDocument() {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setXIncludeAware(true);
			factory.setNamespaceAware(true);
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			if (!docBuilder.isXIncludeAware()) {
				throw new IllegalStateException("Dang, not xinclude aware.");
			}

			Document document = docBuilder.newDocument();

			return document;
		} catch (ParserConfigurationException pce) {
			logger.error("Unable to parse the store", pce);
			return null;
		}

	}

	/**
	 * 
	 * @param mockServiceBean
	 * @return Service as an XML definition.
	 */
	public Document getServiceAsDocument(Service mockServiceBean,
			boolean includeScenarioDefinitions) {
		Document document = getDocument();
		Element rootElement = document.createElement("mockservice");

		this.setAttribute(rootElement, "xml:lang", "en-US");
		this.setAttribute(rootElement, "version", "1.0");
		Element serviceElement = this.getServiceAsElement(document,
				mockServiceBean, includeScenarioDefinitions);
		rootElement.appendChild(serviceElement);
		document.appendChild(rootElement);
		return document;
	}

	/**
	 * 
	 * @param scenario
	 * @return Scenario as an XML definition.
	 */
	public Document getServiceScenarioAsDocument(Scenario scenario,
			boolean scenarioResponseAsXIncludeTxtFile) {
		Document document = getDocument();
		Element serviceElement = this.getScenarioAsElement(document, scenario,
				scenarioResponseAsXIncludeTxtFile);
		this.setAttribute(serviceElement, "xml:lang", "en-US");
		this.setAttribute(serviceElement, "version", "1.0");
		document.appendChild(serviceElement);
		return document;
	}

	/**
	 * 
	 * @param document
	 * @param mockServiceBean
	 * @param includeScenarioDefinitions
	 * @return
	 */
	private Element getServiceAsElement(Document document,
			Service mockServiceBean, boolean includeScenarioDefinitions) {

		Element serviceElement = document.createElement("service");

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
			serviceElement.setAttribute("name",
					mockServiceBean.getServiceName());
			serviceElement
					.setAttribute("description",
							getSafeForXmlOutputString(mockServiceBean
									.getDescription()));
			serviceElement.setAttribute(
					"hang_time",
					getSafeForXmlOutputString(""
							+ mockServiceBean.getHangTime()));
			serviceElement.setAttribute(
					"request_inspector_name",
					getSafeForXmlOutputString(""
							+ mockServiceBean.getRequestInspectorName()));
			serviceElement.setAttribute("url", getSafeForXmlOutputString(""
					+ mockServiceBean.getUrl()));
			serviceElement.setAttribute("tag",
					getSafeForXmlOutputString(mockServiceBean.getTag()));
			// CHANGE on March 2013
			// Last visit will always change, and there's no need to persist
			// this to a repository.
			// This information is for in-memory use only, and displayed to
			// users ONLY.
			// serviceElement.setAttribute("last_visit",
			// getSafeForXmlOutputString("" + mockServiceBean.getLastVisit()));
			serviceElement.setAttribute(
					"default_scenario_id",
					getSafeForXmlOutputString(""
							+ (mockServiceBean.getDefaultScenarioId())));
			serviceElement.setAttribute(
					"error_scenario_id",
					getSafeForXmlOutputString(""
							+ (mockServiceBean.getErrorScenarioId())));

			serviceElement.setAttribute(
					"service_response_type",
					getSafeForXmlOutputString(""
							+ mockServiceBean.getServiceResponseType()));
			serviceElement.setAttribute(
					"default_real_url_index",
					getSafeForXmlOutputString(""
							+ mockServiceBean.getDefaultRealUrlIndex()));

			// Request validation rules in JSON format definition.
			Element requestInspectorJsonRulesElement = document
					.createElement("request_inspector_json_rules");
			requestInspectorJsonRulesElement.setAttribute("enable_flag", ""
					+ mockServiceBean.isRequestInspectorJsonRulesEnableFlag());
			CDATASection cdataJsonRulesElement = document
					.createCDATASection(getSafeForXmlOutputString(mockServiceBean
							.getRequestInspectorJsonRules()));
			requestInspectorJsonRulesElement.appendChild(cdataJsonRulesElement);
			serviceElement.appendChild(requestInspectorJsonRulesElement);

			// Service Scenario Response Schema
			Element responseSchemaElement = document
					.createElement("response_schema");
			responseSchemaElement.setAttribute("enable_flag", ""
					+ mockServiceBean.isResponseSchemaFlag());
			CDATASection cdataresponseSchemaElement = document
					.createCDATASection(getSafeForXmlOutputString(mockServiceBean
							.getResponseSchema()));
			responseSchemaElement.appendChild(cdataresponseSchemaElement);
			serviceElement.appendChild(responseSchemaElement);

			// New real service URLs
			for (Url realUrl : mockServiceBean.getRealServiceUrls()) {
				Element urlElement = document.createElement("real_url");
				urlElement.setAttribute("url",
						getSafeForXmlOutputString(realUrl.getFullUrl()));
				// urlElement.appendChild(cdataResponseElement);
				serviceElement.appendChild(urlElement);
			}

			// Scenarios
			// TODO.
			// includeScenarioDefinitions = true;
			if (includeScenarioDefinitions) {
				for (Scenario scenario : mockServiceBean.getScenarios()) {
					Element scenarioElement = getScenarioAsElement(document,
							scenario, false);
					serviceElement.appendChild(scenarioElement);
				}
			} else {
				for (Scenario scenario : mockServiceBean.getScenarios()) {
					Element include = document.createElementNS(
							"http://www.w3.org/2001/XInclude", "xi:include");
					String path = MockeyXmlFileManager.getInstance()
							.getServiceScenarioFileRelativePathToDepotFolder(
									mockServiceBean, scenario);
					include.setAttribute("href", getSafeIncludePathForOS(path));
					include.setAttribute("parse", "xml");
					serviceElement.appendChild(include);
				}
			}
		}
		return serviceElement;
	}

	private Element getScenarioAsElement(Document document, Scenario scenario,
			boolean scenarioResponseAsXIncludeTxtFile) {
		Element scenarioElement = document.createElement("scenario");
		scenarioElement.setAttribute("id", scenario.getId().toString());
		scenarioElement.setAttribute("name",
				getSafeForXmlOutputString(scenario.getScenarioName()));
		scenarioElement.setAttribute("tag",
				getSafeForXmlOutputString(scenario.getTag()));
		scenarioElement.setAttribute("hang_time",
				getSafeForXmlOutputString("" + scenario.getHangTime()));
		// REMOVED March 2013. We don't need this in a repot for persistence.
		// It's only for a visual/at-run-time queue.
		// scenarioElement.setAttribute("last_visit",
		// getSafeForXmlOutputString("" + scenario.getLastVisit()));
		scenarioElement.setAttribute(
				"http_resp_status_code",
				getSafeForXmlOutputString(""
						+ scenario.getHttpResponseStatusCode()));
		scenarioElement.setAttribute("http_method_type",
				getSafeForXmlOutputString("" + scenario.getHttpMethodType()));

		Element scenarioMatchStringElement = document
				.createElement("scenario_match");
		scenarioMatchStringElement.setAttribute(
				"scenario_match_evaluation_rules_flag", Boolean
						.toString(scenario
								.isMatchStringArgEvaluationRulesFlag()));
		CDATASection cdataMatchElement = document
				.createCDATASection(getSafeForXmlOutputString(scenario
						.getMatchStringArg()));
		scenarioMatchStringElement.appendChild(cdataMatchElement);
		scenarioElement.appendChild(scenarioMatchStringElement);

		// responseHeader
		Element scenarioResponseElement = document
				.createElement("scenario_response");
		if (scenarioResponseAsXIncludeTxtFile) {
			Element include = document.createElementNS(
					"http://www.w3.org/2001/XInclude", "xi:include");
			include.setAttribute("href", MockeyXmlFileManager.getInstance()
					.getScenarioResponseFileName(scenario));
			include.setAttribute("parse", "text");
			scenarioResponseElement.appendChild(include);
		} else {
			CDATASection cdataResponseElement = document
					.createCDATASection(getSafeForXmlOutputString(scenario
							.getResponseMessage()));
			scenarioResponseElement.appendChild(cdataResponseElement);
		}
		scenarioElement.appendChild(scenarioResponseElement);

		Element scenarioResponseHeaderElement = document
				.createElement("scenario_response_header");
		CDATASection cdataResponseHeaderElement = document
				.createCDATASection(getSafeForXmlOutputString(scenario
						.getResponseHeader()));
		scenarioResponseHeaderElement.appendChild(cdataResponseHeaderElement);
		scenarioElement.appendChild(scenarioResponseHeaderElement);
		return scenarioElement;
	}

	/**
	 * 
	 * @param document
	 *            - Handle to create XML artifacts
	 * @param store
	 *            - state of all service definitions
	 * @param fullDefinition
	 *            - if true, builds full definition of the DOM, with no Service
	 *            references, but includes all service definitions
	 * @return
	 */
	public Document getStoreAsDocument(IMockeyStorage store,
			boolean nonRefFullDefinition) {

		Date date = new Date();
		Document document = this.getDocument();
		Element rootElement = document.createElement("mockservice");
		Scenario mssb = store.getUniversalErrorScenario();
		this.setAttribute(rootElement, "xml:lang", "en-US");
		this.setAttribute(rootElement, "version", "1.0");
		this.setAttribute(rootElement, "timestamp", Long.toString(date.getTime()));
		// Universal Service settings
		if (mssb != null) {
			this.setAttribute(rootElement, "universal_error_service_id", ""
					+ mssb.getServiceId());
			this.setAttribute(rootElement, "universal_error_scenario_id", ""
					+ mssb.getId());
		}
		if (store.getUniversalTwistInfoId() != null) {
			this.setAttribute(rootElement, "universal_twist_info_id", ""
					+ store.getUniversalTwistInfoId());
		}

		if (store.getDefaultServicePlanIdAsLong() != null) {
			this.setAttribute(rootElement, "default_service_plan_id", ""
					+ store.getDefaultServicePlanId());
		}

		// Proxy settings
		ProxyServerModel psm = store.getProxy();
		if (psm != null) {
			Element proxyElement = document.createElement("proxy_settings");
			proxyElement.setAttribute("proxy_url", psm.getProxyUrl());
			proxyElement.setAttribute("proxy_enabled",
					"" + psm.isProxyEnabled());
			rootElement.appendChild(proxyElement);
		}

		// SERVICE LIST
		// logger.debug("Building non-reference, full definition? " +
		// nonRefFullDefinition);
		if (nonRefFullDefinition) {
			// Includes ALL service definitions in the DOM
			for (Service mockServiceBean : store.getServices()) {
				Element serviceElement = this.getServiceAsElement(document,
						mockServiceBean, true);
				rootElement.appendChild(serviceElement);
			}
		} else {
			// Includes reference pointers to service definitions in the DOM
			for (Service mockServiceBean : store.getServices()) {

				Element serviceElement = document.createElement("serviceref");
				MockeyXmlFileManager mxfm = MockeyXmlFileManager.getInstance();
				File serviceFile = mxfm.getServiceFile(mockServiceBean);
				String relativePathToServiceFile = mxfm
						.getRelativePath(serviceFile);
				serviceElement.setAttribute("file", getSafeIncludePathForOS(relativePathToServiceFile));
				rootElement.appendChild(serviceElement);
			}
		}

		// SERVICE PLAN LIST
		if (store.getServicePlans() != null) {
			for (ServicePlan servicePlan : store.getServicePlans()) {
				Element servicePlanElement = document
						.createElement("service_plan");
				servicePlanElement.setAttribute("name", servicePlan.getName());
				servicePlanElement.setAttribute("description",
						servicePlan.getDescription());
				servicePlanElement.setAttribute("id", "" + servicePlan.getId());
				servicePlanElement.setAttribute("tag", servicePlan.getTag());
				// REMOVED March 2013.
				// No need to persist. Visual queue only.
				// servicePlanElement.setAttribute("last_visit",
				// getSafeForXmlOutputString("" + servicePlan.getLastVisit()));
				for (PlanItem pi : servicePlan.getPlanItemList()) {
					Element planItemElement = document
							.createElement("plan_item");
					planItemElement.setAttribute("hang_time",
							"" + pi.getHangTime());
					planItemElement.setAttribute("service_name",
							getSafeForXmlOutputString(pi.getServiceName()));
					planItemElement.setAttribute("scenario_name",
							getSafeForXmlOutputString(pi.getScenarioName()));
					planItemElement.setAttribute("service_response_type", ""
							+ pi.getServiceResponseType());

					servicePlanElement.appendChild(planItemElement);
				}

				rootElement.appendChild(servicePlanElement);

			}
		}

		// TWIST CONFIGURATION
		if (store.getTwistInfoList() != null) {
			for (TwistInfo twistInfo : store.getTwistInfoList()) {
				Element twistConfigElement = document
						.createElement("twist_config");
				twistConfigElement.setAttribute("name", twistInfo.getName());
				twistConfigElement.setAttribute("id", "" + twistInfo.getId());
				for (PatternPair patternPair : twistInfo.getPatternPairList()) {
					Element patternPairElement = document
							.createElement("twist_pattern");
					patternPairElement.setAttribute("origination", ""
							+ patternPair.getOrigination());
					patternPairElement.setAttribute("destination", ""
							+ patternPair.getDestination());
					twistConfigElement.appendChild(patternPairElement);
				}
				rootElement.appendChild(twistConfigElement);
			}

		}
		document.appendChild(rootElement);
		return document;
	}

	private String getSafeForXmlOutputString(String arg) {
		if (arg != null) {
			return arg.trim();
		} else {
			return "";
		}
	}

	/*
	 * On Windows, relative file paths "directory\somewhere\another\place" needs
	 * to be XML file path safe. e.g. "directory/somewhere/another/place"
	 */
	private String getSafeIncludePathForOS(String path) {

		String safePath = path.replace('\\', '/');
		return safePath;
	}

}
