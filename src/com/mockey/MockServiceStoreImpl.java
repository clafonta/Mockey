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

import org.apache.log4j.Logger;

import java.util.List;

/**
 * In memory implementation to the storage of mock services and scenarios.
 *
 * @author chad.lafontaine
 */
public class MockServiceStoreImpl implements MockServiceStore {

    /**
     * Basic logger
     */
    private static Logger logger = Logger.getLogger(MockServiceStoreImpl.class);
    private ProxyServer proxyInfoBean = new ProxyServer();
    private OrderedMap historyCache = new OrderedMap();
    private OrderedMap mockServiceStore = new OrderedMap();
    private OrderedMap servicePlanStore = new OrderedMap();
    private Long univeralErrorServiceId = null;
    private Long univeralErrorScenarioId = null;
    
    private static MockServiceStoreImpl store = new MockServiceStoreImpl();

    public static MockServiceStoreImpl getInstance() {
        return store;
    }

    public MockServiceBean getMockServiceById(Long id) {
        return (MockServiceBean) mockServiceStore.get(id);
    }

    public MockServiceBean getMockServiceByUrl(String urlPath) {
        String servicePath;
        if (urlPath.startsWith("/service")) {
            int index = urlPath.indexOf("/service");
            servicePath = urlPath.substring(index + "/service".length());
        } else {
            servicePath = urlPath;
        }
        try {
            for (Object o : mockServiceStore.keySet()) {
                Long id = (Long) o;
                MockServiceBean theService = (MockServiceBean) mockServiceStore.get(id);
                String tempString = theService.getMockServiceUrl();
                if (tempString.equals(servicePath)) {
                    return theService;
                }
            }

        } catch (Exception e) {
            logger.error("Unable to retrieve service w/ url pattern: " + urlPath, e);

        }
        return null;
    }

    public void saveOrUpdate(MockServiceBean mockServiceBean) {
        mockServiceStore.save(mockServiceBean);
        
    }

    public void delete(MockServiceBean mockServiceBean) {
        if (mockServiceBean != null) {
            mockServiceStore.remove(mockServiceBean.getId());
        }
    }

    public List getOrderedList() {
        return this.mockServiceStore.getOrderedList();
    }

    public String toString() {
        StringBuffer stringBuf = new StringBuffer();
        for (Object o : this.mockServiceStore.keySet()) {
            Long key = (Long) o;
            MockServiceBean element = (MockServiceBean) mockServiceStore.get(key);
            stringBuf.append("Service ID: ").append(element.getId()).append("\n");
            stringBuf.append("Service name: ").append(element.getServiceName()).append("\n");
            stringBuf.append("Service description: ").append(element.getDescription()).append("\n");
            stringBuf.append("Service url: ").append(element.getMockServiceUrl()).append("\n");
            stringBuf.append("Service proxyurl: ").append(element.getRealServicePath()).append("\n");
            List scenarios = element.getScenarios();
            for (Object scenario : scenarios) {
                MockServiceScenarioBean b = (MockServiceScenarioBean) scenario;
                stringBuf.append("    scenario name: ").append(b.getScenarioName()).append("\n");
                stringBuf.append("    scenario request: ").append(b.getRequestMessage()).append("\n");
                stringBuf.append("    scenario response: ").append(b.getResponseMessage()).append("\n");

            }

        }
        return stringBuf.toString();
    }

    /**
     * @return list of MockServiceScenarioBean objects
     */
    public List getHistoryScenarios() {
        return this.historyCache.getOrderedList();
    }

    public void deleteHistoricalScenario(Long scenarioId) {

        historyCache.remove(scenarioId);

    }

    public void addHistoricalScenario(MockServiceScenarioBean mssb) {

        historyCache.save(mssb);

    }

    public void flushHistoryRequestMsgs(Long serviceId) {
        for (Object o : historyCache.getOrderedList()) {
            MockServiceScenarioBean object = (MockServiceScenarioBean) o;
            if (object.getServiceId().equals(serviceId)) {
                this.historyCache.remove(object.getId());
            }
        }
    }

    public ProxyServer getProxyInfo() {
        return this.proxyInfoBean;
    }

    public void setProxyInfo(ProxyServer proxyInfoBean) {
        this.proxyInfoBean = proxyInfoBean;

    }

	public void deleteServicePlan(MockServicePlan servicePlan) {
		if(servicePlan!=null){
			this.servicePlanStore.remove(servicePlan.getId());
		}
		
	}

	public MockServicePlan getMockServicePlan(Long servicePlanId) {
		return (MockServicePlan)this.servicePlanStore.get(servicePlanId);
	}

		
	@SuppressWarnings("unchecked")
	public List getMockServicePlanList() {
		return this.servicePlanStore.getOrderedList();
	}

	public void saveOrUpdateServicePlan(MockServicePlan servicePlan) {
		this.servicePlanStore.save(servicePlan);
		
	}

    public MockServiceScenarioBean getUniversalErrorResponse() {
        MockServiceScenarioBean uErrorBean = null;
        MockServiceBean msb = getMockServiceById(this.univeralErrorServiceId);
        if(msb!=null){
            uErrorBean = msb.getScenario(this.univeralErrorScenarioId);
        }
        return uErrorBean;
    }

   
    public void setUniversalErrorScenarioId(Long scenarioId) {
        this.univeralErrorScenarioId = scenarioId;
        
    }

    public void setUniversalErrorServiceId(Long serviceId) {
        this.univeralErrorServiceId = serviceId;
        
    }

}
