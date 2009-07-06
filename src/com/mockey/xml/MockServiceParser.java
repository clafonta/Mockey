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
package com.mockey.xml;

import org.apache.commons.digester.Digester;
import org.xml.sax.InputSource;

import com.mockey.MockServiceBean;
import com.mockey.MockServiceScenarioBean;
import com.mockey.MockServiceStore;
import com.mockey.MockServiceStoreImpl;

/**
 * 
 * @author Chad.Lafontaine
 * 
 */
public class MockServiceParser {

	private final static String ROOT = "mockservice";

	private final static String SERVICE = ROOT + "/service";

	private final static String SCENARIO = SERVICE + "/scenario";

	private final static String SCENARIO_MATCH = SCENARIO + "/scenario_match";

	private final static String SCENARIO_REQUEST = SCENARIO + "/scenario_request";

	private final static String SCENARIO_RESPONSE = SCENARIO + "/scenario_response";

	public MockServiceParser() {

	}

	/**
	 * 
	 * @param inputSource
	 * @return
	 */
	public MockServiceStore getMockServices(InputSource inputSource) throws org.xml.sax.SAXParseException,
			java.io.IOException, org.xml.sax.SAXException {

		Digester digester = new Digester();
		digester.setValidating(false);
		digester.addObjectCreate(ROOT, MockServiceStoreImpl.class);
		digester.addObjectCreate(SERVICE, MockServiceBean.class);
		digester.addSetProperties(SERVICE, "name", "serviceName");//           
		digester.addSetProperties(SERVICE, "description", "description");
		digester.addSetProperties(SERVICE, "http_header_definition", "httpHeaderDefinition");
		digester.addSetProperties(SERVICE, "hang_time", "hangTime");//  
		digester.addSetProperties(SERVICE, "proxyurl", "realServiceUrl");
		digester.addSetProperties(SERVICE, "url", "mockServiceUrl");
		digester.addSetProperties(SERVICE, "proxy_on", "proxyOn");
		digester.addSetProperties(SERVICE, "default_scenario_id", "defaultScenarioId");
		digester.addSetProperties(SERVICE, "reply_matching_request", "replyWithMatchingRequest");
		
		//serviceElement.setAttribute("reply_matching_request", "" + mockServiceBean.isReplyWithMatchingRequest());
		//serviceElement.setAttribute("proxy_on", "" + mockServiceBean.isProxyOn());

		digester.addSetNext(SERVICE, "saveOrUpdate");

		digester.addObjectCreate(SCENARIO, MockServiceScenarioBean.class);
		digester.addSetProperties(SCENARIO, "id", "id");
		digester.addSetProperties(SCENARIO, "name", "scenarioName");
		digester.addBeanPropertySetter(SCENARIO_MATCH, "matchStringArg");
		digester.addBeanPropertySetter(SCENARIO_REQUEST, "requestMessage");
		digester.addBeanPropertySetter(SCENARIO_RESPONSE, "responseMessage");
		digester.addSetNext(SCENARIO, "updateScenario");
		MockServiceStore c = (MockServiceStore) digester.parse(inputSource);
		return c;

	}

}