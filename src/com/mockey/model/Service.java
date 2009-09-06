/*
 * Copyright 2008-2010 the original author or authors.
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
package com.mockey.model;

import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpHost;

import com.mockey.OrderedMap;

/**
 * The mock service definition.
 * 
 * @author chad.lafontaine
 * 
 */
public class Service implements Item {

    public final static int SERVICE_RESPONSE_TYPE_PROXY = 0;
    public final static int SERVICE_RESPONSE_TYPE_STATIC_SCENARIO = 1;
    public final static int SERVICE_RESPONSE_TYPE_DYNAMIC_SCENARIO = 2;
    
    private Long id;
    private String serviceName;
    private String description;
    private Long defaultScenarioId;
    private Long errorScenarioId;
    private String httpHeaderDefinition;
    private int hangTime = 500;
    private OrderedMap<Scenario> scenarios = new OrderedMap<Scenario>();
    private int serviceResponseType = SERVICE_RESPONSE_TYPE_PROXY;
    private String httpMethod = "GET";
    private Url realServiceUrl;
	private List<RequestResponseTransaction> transactions;
	
    public List<RequestResponseTransaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<RequestResponseTransaction> transactions) {
		this.transactions = transactions;
	}    

    /**
     * Empty constructor needed for XML parsing of service definitions, 
     * 
     */
    public Service() {
    }

    public Service(Url realServiceUrl) {
        this.realServiceUrl = realServiceUrl;        
        this.setServiceName("Auto-Generated Service");
    }

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

    @SuppressWarnings("unchecked")
    public List<Scenario> getScenarios() {
        return scenarios.getOrderedList();
    }

    public Scenario getScenario(Long scenarioId) {
        return (Scenario) scenarios.get(scenarioId);
    }

    public void deleteScenario(Long scenarioId) {
        this.scenarios.remove(scenarioId);
    }

    public Scenario updateScenario(Scenario scenario) {
        scenario.setServiceId(this.id);
        return (Scenario)this.scenarios.save(scenario);
    }

    public String getMockServiceUrl() {
        if(this.realServiceUrl!=null){
            return realServiceUrl.getFullUrl();    
        }else {
            return "";
        }
        
    }

    /**
     * Helper method.
     * 
     * @return returns a the full URI path to this service, pre-pending
     *         "/service" to the mock service URL
     */
    public String getServiceUrl() {
        return (Url.MOCK_SERVICE_PATH + this.getMockServiceUrl());
    }

    public Url getUrl(){
        return this.realServiceUrl;
    }

    public void setRealServiceUrl(Url realServiceUrl) {
        this.realServiceUrl = realServiceUrl;
    }
    
    public void setRealServiceUrlByString(String realServiceUrl) {
        this.realServiceUrl = new Url(realServiceUrl);
    }

    public String getHttpHeaderDefinition() {
        return httpHeaderDefinition;
    }

    public void setHttpHeaderDefinition(String httpHeaderDefinition) {
        this.httpHeaderDefinition = httpHeaderDefinition;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Service name:").append(this.getServiceName()).append("\n");
        sb.append("Mock URL:").append(this.getMockServiceUrl()).append("\n");
        sb.append("Real URL:").append(this.getRealServiceUrl()).append("\n");
        sb.append("Scheme:").append(this.getUrl().getScheme()).append("\n");
        sb.append("Default scenario ID:").append(this.getDefaultScenarioId()).append("\n");
        sb.append("HTTP Content:").append(this.getHttpHeaderDefinition()).append("\n");
        sb.append("Hang time:");
        sb.append(this.getHangTime());
        sb.append("\n");

        return sb.toString();
    }

    public void setId(Long id) {
        this.id = id;

        // Recursively set this ID to child Scenarios, if any exist.
        for (Scenario scenario : getScenarios()) {
            scenario.setServiceId(this.id);
            this.updateScenario(scenario);
        }
    }

    public Long getId() {
        return id;
    }

    public String getRealServiceUrl() {
        if (this.realServiceUrl != null) {
            return String.valueOf(realServiceUrl);
        } else {
            return "";
        }
    }

    public HttpHost getHttpHost() {
        return new HttpHost(realServiceUrl.getHost(), realServiceUrl.getPort(), realServiceUrl.getScheme());
    }

    public void setServiceResponseType(int serviceResponseType) {
        this.serviceResponseType = serviceResponseType;
    }

    public int getServiceResponseType() {
        return serviceResponseType;
    }

    public void setErrorScenarioId(Long errorScenarioId) {
        this.errorScenarioId = errorScenarioId;
    }

    public Long getErrorScenarioId() {
        return errorScenarioId;
    }
}
