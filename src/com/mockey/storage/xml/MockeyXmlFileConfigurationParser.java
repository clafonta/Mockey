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
package com.mockey.storage.xml;

import org.apache.commons.digester.Digester;
import org.xml.sax.InputSource;

import com.mockey.model.PlanItem;
import com.mockey.model.ServicePlan;
import com.mockey.model.Service;
import com.mockey.model.Scenario;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.InMemoryMockeyStorage;

/**
 * 
 * @author Chad.Lafontaine
 * 
 */
public class MockeyXmlFileConfigurationParser {

	private final static String ROOT = "mockservice";
	private final static String SERVICE = ROOT + "/service";
	private final static String SCENARIO = SERVICE + "/scenario";
	private final static String PLAN = ROOT + "/service_plan";
	private final static String PLAN_ITEM = PLAN + "/plan_item";
	private final static String SCENARIO_MATCH = SCENARIO + "/scenario_match";
	private final static String SCENARIO_REQUEST = SCENARIO + "/scenario_request";
	private final static String SCENARIO_RESPONSE = SCENARIO + "/scenario_response";

	public IMockeyStorage getMockServices(InputSource inputSource) throws org.xml.sax.SAXParseException,
			java.io.IOException, org.xml.sax.SAXException {

		Digester digester = new Digester();
		digester.setValidating(false);
		digester.addObjectCreate(ROOT, InMemoryMockeyStorage.class);
		digester.addSetProperties(ROOT, "universal_error_service_id", "universalErrorServiceId");//   
		digester.addSetProperties(ROOT, "universal_error_scenario_id", "universalErrorScenarioId");//   
		
		digester.addObjectCreate(SERVICE, Service.class);
		//digester.addSetProperties(SERVICE, "id", "id");//    
		digester.addSetProperties(SERVICE, "name", "serviceName");//           
		digester.addSetProperties(SERVICE, "description", "description");
		digester.addSetProperties(SERVICE, "http_header_definition", "httpHeaderDefinition");
		digester.addSetProperties(SERVICE, "hang_time", "hangTime");//  
		digester.addSetProperties(SERVICE, "proxyurl", "realServiceUrlByString");
		digester.addSetProperties(SERVICE, "service_response_type", "serviceResponseType");
		digester.addSetProperties(SERVICE, "default_scenario_id", "defaultScenarioId");
	
		digester.addSetNext(SERVICE, "saveOrUpdateService");

		digester.addObjectCreate(SCENARIO, Scenario.class);
		digester.addSetProperties(SCENARIO, "id", "id");
		digester.addSetProperties(SCENARIO, "name", "scenarioName");
		digester.addBeanPropertySetter(SCENARIO_MATCH, "matchStringArg");
		digester.addBeanPropertySetter(SCENARIO_REQUEST, "requestMessage");
		digester.addBeanPropertySetter(SCENARIO_RESPONSE, "responseMessage");
		digester.addSetNext(SCENARIO, "updateScenario");
		
		// PLAN
		digester.addObjectCreate(PLAN, ServicePlan.class);
		digester.addSetProperties(PLAN, "name", "name");//     
		digester.addSetProperties(PLAN, "description", "description");//
		digester.addSetProperties(PLAN, "id", "id");
		digester.addSetNext(PLAN, "saveOrUpdateServicePlan");
		digester.addObjectCreate(PLAN_ITEM, PlanItem.class);
		digester.addSetProperties(PLAN_ITEM, "hang_time", "hangTime");
		digester.addSetProperties(PLAN_ITEM, "service_id", "serviceId");
		digester.addSetProperties(PLAN_ITEM, "scenario_id", "scenarioId");
		digester.addSetProperties(PLAN_ITEM, "service_response_type", "serviceResponseType");
		digester.addSetNext(PLAN_ITEM, "addPlanItem");
		IMockeyStorage c = (IMockeyStorage) digester.parse(inputSource);
		return c;

	}

}