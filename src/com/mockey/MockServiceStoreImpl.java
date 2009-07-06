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

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * In memory implementation to the storage of mock services and scenarios.
 * 
 * @author chad.lafontaine
 * 
 */
public class MockServiceStoreImpl implements MockServiceStore {

    /** Basic logger */
    private static Logger logger = Logger.getLogger(MockServiceStoreImpl.class);
    private ProxyServer proxyInfoBean;
    private OrderedMap historyCache = new OrderedMap();
    private OrderedMap mockServiceStore = new OrderedMap();
    private static MockServiceStoreImpl store = new MockServiceStoreImpl();

    public static MockServiceStoreImpl getInstance() {
        return store;
    }

    public MockServiceBean getMockServiceById(Long id) {
        return (MockServiceBean) mockServiceStore.get(id);
    }

    public MockServiceBean getMockServiceByUrl(String urlPath) {

        int index = urlPath.indexOf("/service");
        String servicePath = urlPath.substring(index + "/service".length());

        try {
            Iterator iter = mockServiceStore.keySet().iterator();

            while (iter.hasNext()) {
                Long id = (Long) iter.next();
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
        Iterator iter = this.mockServiceStore.keySet().iterator();
        while (iter.hasNext()) {
            Long key = (Long) iter.next();
            MockServiceBean element = (MockServiceBean) mockServiceStore.get(key);
            stringBuf.append("Service ID: " + element.getId() + "\n");
            stringBuf.append("Service name: " + element.getServiceName() + "\n");
            stringBuf.append("Service description: " + element.getDescription() + "\n");
            stringBuf.append("Service url: " + element.getMockServiceUrl() + "\n");
            stringBuf.append("Service proxyurl: " + element.getRealServiceUrl() + "\n");
            List scenarios = element.getScenarios();
            Iterator iter2 = scenarios.iterator();
            while (iter2.hasNext()) {
                MockServiceScenarioBean b = (MockServiceScenarioBean) iter2.next();
                stringBuf.append("    scenario name: " + b.getScenarioName() + "\n");
                stringBuf.append("    scenario request: " + b.getRequestMessage() + "\n");
                stringBuf.append("    scenario response: " + b.getResponseMessage() + "\n");

            }

        }
        return stringBuf.toString();
    }

    /**
     * 
     * @return list of MockServiceScenarioBean objects
     */
    public List getHistoryScenarios(){
        return this.historyCache.getOrderedList();
    }
    
    public void deleteHistoricalScenario(Long scenarioId) {
        
        historyCache.remove(scenarioId);
        
    }
    
    public void addHistoricalScenario(MockServiceScenarioBean mssb) {

        historyCache.save(mssb);
        
    }

    public void flushHistoryRequestMsgs(Long serviceId){
        Iterator iter = historyCache.getOrderedList().iterator();
        while(iter.hasNext()){
            MockServiceScenarioBean object = (MockServiceScenarioBean)iter.next();
            if(object.getServiceId().equals(serviceId)){
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

}
