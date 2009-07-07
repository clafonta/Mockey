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
package com.mockey;

import org.apache.http.HttpHost;

import java.util.List;

import com.mockey.util.Url;

/**
 * The mock service definition.  
 * 
 * @author chad.lafontaine
 *
 */
public class MockServiceBean implements Item {

	public final static String HTTP_HEADER_XML = "text/xml";

	public final static String HTTP_HEADER_HTML = "text/html";

	public final static String HTTP_HEADER_PLAIN = "text/plain";

	private Long id;
	
	private String serviceName;

	private String description;

	private Long defaultScenarioId;
	
	private String httpHeaderDefinition = HTTP_HEADER_XML;

	private int hangTime = 500;

	private OrderedMap scenarios = new OrderedMap();

	private boolean proxyOn = false;

	private boolean replyWithMatchingRequest = false;

    private String httpMethod = "GET";

    private String mockServiceUrl;
    private Url realServiceUrl;

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Long getDefaultScenarioId() {
		return defaultScenarioId;
	}

	public void setDefaultScenarioId(Long defaultScenarioId) {
		this.defaultScenarioId = defaultScenarioId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String name) {
		this.serviceName = name;
	}

	public int getHangTime() {
		return hangTime;
	}

	public void setHangTime(int hangTime) {
		this.hangTime = hangTime;
	}

	public List getScenarios() {
		return scenarios.getOrderedList();
	}

	public MockServiceScenarioBean getScenario(Long scenarioId) {
		return (MockServiceScenarioBean)scenarios.get(scenarioId);
	}

	public void deleteScenario(Long scenarioId) {
		this.scenarios.remove(scenarioId);
	}

	public void updateScenario(MockServiceScenarioBean mss) {
		
		this.scenarios.save(mss);
	}

	public String getMockServiceUrl() {
		return mockServiceUrl;
	}


    public String getRealHost() {
        return realServiceUrl.getHost();
    }

    public String getRealPath() {
        return realServiceUrl.getPath();
    }

	/**
	 * Helper method. 
	 * @return returns a the full URI path to this service, pre-pending "/service" to the mock service URL 
	 */
	public String getServiceUrl() {
		return ("/service" + cleanUrl(this.getMockServiceUrl()));
	}
	
	/**
	 * Method will ensure the service URL starts with '/'. If not, will prepend it to the mock uri
	 * @param mockServiceUrl
	 */
	public void setMockServiceUrl(String mockServiceUrl) {
		if (mockServiceUrl != null && !mockServiceUrl.trim().startsWith("/")) {
			this.mockServiceUrl = "/" + mockServiceUrl.trim();
		} else {
			this.mockServiceUrl = mockServiceUrl;
		}
	}

	public String getRealServicePath() {
		return realServiceUrl.getPath();
	}

	public void setRealServiceUrl(String realServiceUrl) {
        this.realServiceUrl = new Url(realServiceUrl);
	}

	public boolean isProxyOn() {
		return proxyOn;
	}

	public void setProxyOn(boolean proxyOn) {
		this.proxyOn = proxyOn;
	}

	public boolean isReplyWithMatchingRequest() {
		return replyWithMatchingRequest;
	}

	public void setReplyWithMatchingRequest(boolean replyWithMatchingRequest) {
		this.replyWithMatchingRequest = replyWithMatchingRequest;
	}

	public String getHttpHeaderDefinition() {
		return httpHeaderDefinition;
	}

	public void setHttpHeaderDefinition(String httpHeaderDefinition) {
		this.httpHeaderDefinition = httpHeaderDefinition;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Service name:" + this.getServiceName() +"\n");
		sb.append("Mock URL:" + this.getMockServiceUrl()+"\n");
		sb.append("Real URL:" + this.getRealServiceUrl()+"\n");
		sb.append("Scheme:" + this.getRealServiceScheme()+"\n");
		sb.append("Default scenario ID:" + this.getDefaultScenarioId()+"\n");
		sb.append("HTTP Content:" + this.getHttpHeaderDefinition()+"\n");
		sb.append("Hang time:" + this.getHangTime()+"\n");

		return sb.toString();
	}

	private String cleanUrl(String arg) {
		int index = arg.indexOf(";");
		if (index > -1) {
			String t = arg.substring(0, index);
			return t;
		} else {
			return arg;
		}
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getRealServiceScheme() {
		return realServiceUrl.getScheme();
	}

    public String getRealServiceUrl() {
        return realServiceUrl.toString();
    }

    public HttpHost getHttpHost() {
        return new HttpHost(getRealHost(), 443, getRealServiceScheme());
    }
}
