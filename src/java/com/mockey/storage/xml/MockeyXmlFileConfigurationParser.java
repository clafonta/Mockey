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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.digester3.Digester;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mockey.model.PlanItem;
import com.mockey.model.ProxyServerModel;
import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.model.ServiceRef;
import com.mockey.model.TwistInfo;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.InMemoryMockeyStorage;
import com.mockey.ui.PatternPair;

/**
 * This class consumes the mock service definitions file and saves it to the
 * store.
 * 
 * @author Chad.Lafontaine
 * 
 */
public class MockeyXmlFileConfigurationParser {

	private final static String ROOT = "mockservice";
	private final static String ROOT_PROXYSERVER = ROOT + "/proxy_settings";
	private final static String ROOT_SERVICE = ROOT + "/service";
	private final static String ROOT_SERVICEREF = ROOT + "/serviceref";

	private final static String ROOT_SERVICE_REAL_URL = ROOT_SERVICE + "/real_url";
	private final static String ROOT_SERVICE_SCENARIO = ROOT_SERVICE + "/scenario";
	private final static String ROOT_SERVICE_REQUEST_INSPECTOR_JSON_RULES = ROOT_SERVICE
			+ "/request_inspector_json_rules";
	private final static String ROOT_SERVICE_RESPONSE_SCHEMA = ROOT_SERVICE + "/response_schema";
	private final static String ROOT_PLAN = ROOT + "/service_plan";
	private final static String ROOT_PLAN_ITEM = ROOT_PLAN + "/plan_item";
	private final static String ROOT_TWIST_CONFIG = ROOT + "/twist_config";
	private final static String ROOT_TWIST_CONFIG_ITEM = ROOT_TWIST_CONFIG + "/twist_pattern";

	private final static String SCENARIO_MATCH = ROOT_SERVICE_SCENARIO + "/scenario_match";
	private final static String SCENARIO_REQUEST = ROOT_SERVICE_SCENARIO + "/scenario_request";
	private final static String SCENARIO_RESPONSE = ROOT_SERVICE_SCENARIO + "/scenario_response";
	private final static String SCENARIO_RESPONSE_HEADER = ROOT_SERVICE_SCENARIO + "/scenario_response_header";
	private static Digester fullSetDigester = null;
	static {
		MockeyXmlFileConfigurationParser.fullSetDigester = new Digester();
		
		fullSetDigester.setNamespaceAware(true);
		fullSetDigester.setXIncludeAware(true);
		fullSetDigester.setEntityResolver(new EntityResolver() {
			@Override
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				if (systemId.endsWith(".xml")) {
					try {
						// Why URI? Because system id comes over as
						// "file://value"
						// Let URI handle the 'file://' pretext
						
						// The real value we're looking for, is RELATIVE to the base directory. 
						// By default, the base directory is the user directory.
						String defaultUserDirectory = System.getProperty("user.dir");
						String basePath = MockeyXmlFileManager.getInstance().getBasePathFile().getAbsolutePath();
						String updatedSystemId = systemId.replace(defaultUserDirectory, basePath);
						
						File x2 = new File(new URI(updatedSystemId));
						MockeyXmlFileManager mxfm = MockeyXmlFileManager.getInstance();
						FileInputStream fstream = new FileInputStream(x2);
						String inputStreamString = mxfm.getFileContentAsString(fstream);
						InputSource is = new InputSource(new StringReader(inputStreamString));
						is.setSystemId(updatedSystemId);
						return is;
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
				
				return null;
			}
		});
		fullSetDigester.setValidating(false);
		fullSetDigester.addObjectCreate(ROOT, InMemoryMockeyStorage.class);

		fullSetDigester.addSetProperties(ROOT, "universal_error_service_id", "universalErrorServiceId");

		fullSetDigester.addSetProperties(ROOT, "universal_error_scenario_id", "universalErrorScenarioId");
		fullSetDigester.addSetProperties(ROOT, "universal_twist_info_id", "universalTwistInfoId");
		fullSetDigester.addSetProperties(ROOT, "default_service_plan_id", "defaultServicePlanId");

		fullSetDigester.addObjectCreate(ROOT_PROXYSERVER, ProxyServerModel.class);
		fullSetDigester.addSetProperties(ROOT_PROXYSERVER, "proxy_url", "proxyUrl");
		fullSetDigester.addSetProperties(ROOT_PROXYSERVER, "proxy_enabled", "proxyEnabled");
		fullSetDigester.addSetNext(ROOT_PROXYSERVER, "setProxy");

		fullSetDigester.addObjectCreate(ROOT_SERVICEREF, ServiceRef.class);
		fullSetDigester.addSetProperties(ROOT_SERVICEREF, "file", "fileName");
		fullSetDigester.addSetNext(ROOT_SERVICEREF, "saveOrUpdateServiceRef");

		fullSetDigester.addObjectCreate(ROOT_SERVICE, Service.class);
		fullSetDigester.addSetNext(ROOT_SERVICE, "saveOrUpdateService");

		fullSetDigester.addSetProperties(ROOT_SERVICE, "name", "serviceName");
		fullSetDigester.addSetProperties(ROOT_SERVICE, "description", "description");
		fullSetDigester.addSetProperties(ROOT_SERVICE, "request_inspector_name", "requestInspectorName");
		fullSetDigester.addSetProperties(ROOT_SERVICE, "hang_time", "hangTime");
		fullSetDigester.addSetProperties(ROOT_SERVICE, "url", "url");
		fullSetDigester.addSetProperties(ROOT_SERVICE, "tag", "tag");
		// REMOVED March 2013.
		// No need to persist to a repot'. At run time, visual queue only.
		// fullSetDigester.addSetProperties(ROOT_SERVICE, "last_visit",
		// "lastVisit");
		fullSetDigester.addSetProperties(ROOT_SERVICE, "error_scenario_id", "errorScenarioId");
		fullSetDigester.addSetProperties(ROOT_SERVICE, "proxyurl", "realServiceUrlByString");
		fullSetDigester.addSetProperties(ROOT_SERVICE, "default_real_url_index", "defaultRealUrlIndex");
		fullSetDigester.addSetProperties(ROOT_SERVICE, "service_response_type", "serviceResponseType");
		fullSetDigester.addSetProperties(ROOT_SERVICE, "default_scenario_id", "defaultScenarioId");
		//
		fullSetDigester.addBeanPropertySetter(ROOT_SERVICE_REQUEST_INSPECTOR_JSON_RULES, "requestInspectorJsonRules");
		fullSetDigester.addSetProperties(ROOT_SERVICE_REQUEST_INSPECTOR_JSON_RULES, "enable_flag",
				"requestInspectorJsonRulesEnableFlag");
		//
		fullSetDigester.addBeanPropertySetter(ROOT_SERVICE_RESPONSE_SCHEMA, "responseSchema");
		fullSetDigester.addSetProperties(ROOT_SERVICE_RESPONSE_SCHEMA, "enable_flag", "responseSchemaFlag");
		fullSetDigester.addObjectCreate(ROOT_SERVICE_REAL_URL, Url.class);
		fullSetDigester.addSetProperties(ROOT_SERVICE_REAL_URL, "url", "url");
		fullSetDigester.addSetNext(ROOT_SERVICE_REAL_URL, "saveOrUpdateRealServiceUrl");
		fullSetDigester.addObjectCreate(ROOT_SERVICE_SCENARIO, Scenario.class);
		fullSetDigester.addSetProperties(ROOT_SERVICE_SCENARIO, "id", "id");
		fullSetDigester.addSetProperties(ROOT_SERVICE_SCENARIO, "name", "scenarioName");
		// CHANGE on March 2013
		// Last visit will always change, and there's no need to persist this to
		// a repository.
		// This information is for in-memory use only, and displayed to users
		// ONLY.
		// fullSetDigester.addSetProperties(ROOT_SERVICE_SCENARIO, "last_visit",
		// "lastVisit");
		fullSetDigester.addSetProperties(ROOT_SERVICE_SCENARIO, "http_resp_status_code", "httpResponseStatusCode");
		fullSetDigester.addSetProperties(ROOT_SERVICE_SCENARIO, "http_method_type", "httpMethodType");
		fullSetDigester.addSetProperties(ROOT_SERVICE_SCENARIO, "tag", "tag");
		fullSetDigester.addSetProperties(ROOT_SERVICE_SCENARIO, "hang_time", "hangTime");
		fullSetDigester.addBeanPropertySetter(SCENARIO_MATCH, "matchStringArg");
		fullSetDigester.addSetProperties(SCENARIO_MATCH, "scenario_match_evaluation_rules_flag",
				"matchStringArgEvaluationRulesFlag");
		fullSetDigester.addBeanPropertySetter(SCENARIO_REQUEST, "requestMessage");
		fullSetDigester.addBeanPropertySetter(SCENARIO_RESPONSE, "responseMessage");
		fullSetDigester.addBeanPropertySetter(SCENARIO_RESPONSE_HEADER, "responseHeader");
		fullSetDigester.addSetNext(ROOT_SERVICE_SCENARIO, "saveOrUpdateScenario");

		// PLAN
		fullSetDigester.addObjectCreate(ROOT_PLAN, ServicePlan.class);
		fullSetDigester.addSetProperties(ROOT_PLAN, "name", "name");//
		fullSetDigester.addSetProperties(ROOT_PLAN, "description", "description");//
		fullSetDigester.addSetProperties(ROOT_PLAN, "id", "id");
		fullSetDigester.addSetProperties(ROOT_PLAN, "tag", "tag");
		// REMOVED.
		// March 2013, no need to persist this value to a repository.
		// fullSetDigester.addSetProperties(ROOT_PLAN, "last_visit",
		// "lastVisit");
		fullSetDigester.addSetNext(ROOT_PLAN, "saveOrUpdateServicePlan");
		fullSetDigester.addObjectCreate(ROOT_PLAN_ITEM, PlanItem.class);
		fullSetDigester.addSetProperties(ROOT_PLAN_ITEM, "hang_time", "hangTime");
		fullSetDigester.addSetProperties(ROOT_PLAN_ITEM, "service_name", "serviceName");
		fullSetDigester.addSetProperties(ROOT_PLAN_ITEM, "scenario_name", "scenarioName");
		fullSetDigester.addSetProperties(ROOT_PLAN_ITEM, "service_response_type", "serviceResponseType");
		fullSetDigester.addSetNext(ROOT_PLAN_ITEM, "addPlanItem");

		// TWIST CONFIGURATION
		fullSetDigester.addObjectCreate(ROOT_TWIST_CONFIG, TwistInfo.class);
		fullSetDigester.addSetProperties(ROOT_TWIST_CONFIG, "name", "name");//
		fullSetDigester.addSetProperties(ROOT_TWIST_CONFIG, "id", "id");
		fullSetDigester.addSetNext(ROOT_TWIST_CONFIG, "saveOrUpdateTwistInfo");
		fullSetDigester.addObjectCreate(ROOT_TWIST_CONFIG_ITEM, PatternPair.class);
		fullSetDigester.addSetProperties(ROOT_TWIST_CONFIG_ITEM, "origination", "origination");
		fullSetDigester.addSetProperties(ROOT_TWIST_CONFIG_ITEM, "destination", "destination");
		fullSetDigester.addSetNext(ROOT_TWIST_CONFIG_ITEM, "addPatternPair");
	}

	/**
	 * 
	 * @param inputSource
	 *            - Mockey XML definition file, which is tightly tied to this
	 *            class <code>Digester</code> configuration.
	 * @return
	 * @throws org.xml.sax.SAXParseException
	 * @throws java.io.IOException
	 * @throws org.xml.sax.SAXException
	 */
	public IMockeyStorage getMockeyStore(InputSource inputSource) throws org.xml.sax.SAXParseException,
			java.io.IOException, org.xml.sax.SAXException {

		// For initialization (by default), the store is in transient mode,
		// which is important to prevent
		// file writing. Too much, too slow. Yuck.
		// inputSource.getByteStream();
		IMockeyStorage c = (IMockeyStorage) MockeyXmlFileConfigurationParser.fullSetDigester.parse(inputSource);
		return c;
	}

	/**
	 * 
	 * @param inputSource
	 *            - XML fragment
	 * @return
	 * @throws org.xml.sax.SAXParseException
	 * @throws java.io.IOException
	 * @throws org.xml.sax.SAXException
	 */
	public List<Service> getMockService(InputSource inputSource) throws org.xml.sax.SAXParseException,
			java.io.IOException, org.xml.sax.SAXException {

		IMockeyStorage c = getMockeyStore(inputSource);
		List<Service> list = c.getServices();
		return list;
	}

}