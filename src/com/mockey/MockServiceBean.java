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

import java.util.List;

import org.apache.http.HttpHost;

import com.mockey.util.Url;

/**
 * The mock service definition.
 * 
 * @author chad.lafontaine
 * 
 */
public class MockServiceBean implements Item {

    public final static int SERVICE_RESPONSE_TYPE_PROXY = 0;
    public final static int SERVICE_RESPONSE_TYPE_STATIC_SCENARIO = 1;
    public final static int SERVICE_RESPONSE_TYPE_DYNAMIC_SCENARIO = 2;
    private Long id;
    private String serviceName;
    private String description;
    private Long defaultScenarioId;
    private String httpHeaderDefinition;
    private int hangTime = 500;
    private OrderedMap scenarios = new OrderedMap();
    private int serviceResponseType = SERVICE_RESPONSE_TYPE_PROXY;
    private String httpMethod = "GET";
    private String mockServiceUrl;
    private Url realServiceUrl;

    public MockServiceBean() {
    }

    public MockServiceBean(Url realServiceUrl) {
        this.realServiceUrl = realServiceUrl;
        this.setMockServiceUrl(realServiceUrl.getFullUrl());
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

    public List getScenarios() {
        return scenarios.getOrderedList();
    }

    public MockServiceScenarioBean getScenario(Long scenarioId) {
        return (MockServiceScenarioBean) scenarios.get(scenarioId);
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
     * 
     * @return returns a the full URI path to this service, pre-pending
     *         "/service" to the mock service URL
     */
    public String getServiceUrl() {
        return ("/service" + cleanUrl(this.getMockServiceUrl()));
    }

    /**
     * Method will ensure the service URL starts with '/'. If not, will prepend
     * it to the mock uri
     * 
     * @param mockServiceUrl
     *            the path to the service as it should be accessed in our system
     */
    public void setMockServiceUrl(String mockServiceUrl) {
        if (mockServiceUrl != null && !mockServiceUrl.trim().startsWith("/") && mockServiceUrl.trim().length() > 0) {
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
        sb.append("Scheme:").append(this.getRealServiceScheme()).append("\n");
        sb.append("Default scenario ID:").append(this.getDefaultScenarioId()).append("\n");
        sb.append("HTTP Content:").append(this.getHttpHeaderDefinition()).append("\n");
        sb.append("Hang time:");
        sb.append(this.getHangTime());
        sb.append("\n");

        return sb.toString();
    }

    private String cleanUrl(String arg) {
        int index = arg.indexOf(";");
        if (index > -1) {
            return arg.substring(0, index);
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
}
