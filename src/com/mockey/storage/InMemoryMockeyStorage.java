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
package com.mockey.storage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mockey.OrderedMap;
import com.mockey.model.ProxyServerModel;
import com.mockey.model.ServicePlan;
import com.mockey.model.ClientRequest;
import com.mockey.model.Service;
import com.mockey.model.Scenario;

/**
 * In memory implementation to the storage of mock services and scenarios.
 *
 * @author chad.lafontaine
 */
public class InMemoryMockeyStorage implements IMockeyStorage {

    private OrderedMap<ClientRequest> historyStore = new OrderedMap<ClientRequest>();
    private OrderedMap<Service> mockServiceStore = new OrderedMap<Service>();
    private OrderedMap<ServicePlan> servicePlanStore = new OrderedMap<ServicePlan>();
    
    private static Logger logger = Logger.getLogger(InMemoryMockeyStorage.class);
    private ProxyServerModel proxyInfoBean = new ProxyServerModel();
    private Long univeralErrorServiceId = null;
    private Long univeralErrorScenarioId = null;
    
    private static InMemoryMockeyStorage store = new InMemoryMockeyStorage();

    public static InMemoryMockeyStorage getInstance() {
        return store;
    }

    public Service getServiceById(Long id) {
        return (Service) mockServiceStore.get(id);
    }

    public Service getServiceByUrl(String urlPath) {
        try {
            for (Long id : mockServiceStore.keySet()) {
                Service theService = (Service) mockServiceStore.get(id);
                String tempString = theService.getMockServiceUrl();
                if (tempString.equals(urlPath)) {
                    return theService;
                }
            }

        } catch (Exception e) {
            logger.error("Unable to retrieve service w/ url pattern: " + urlPath, e);
        }
        logger.debug("Didn't find service with Service path: " +urlPath);
        return null;
    }

    public void saveOrUpdateService(Service mockServiceBean) {
        mockServiceStore.save(mockServiceBean);
    }

    public void delete(Service mockServiceBean) {
        if (mockServiceBean != null) {
            mockServiceStore.remove(mockServiceBean.getId());
        }
    }

    public List getServices() {
        return this.mockServiceStore.getOrderedList();
    }

    public String toString() {
        
        StringBuffer stringBuf = new StringBuffer();
        stringBuf.append(super.toString());
        for (Object o : this.mockServiceStore.keySet()) {
            Long key = (Long) o;
            Service element = (Service) mockServiceStore.get(key);
            stringBuf.append("Service ID: ").append(element.getId()).append("\n");
            stringBuf.append("Service name: ").append(element.getServiceName()).append("\n");
            stringBuf.append("Service description: ").append(element.getDescription()).append("\n");
            stringBuf.append("Service url: ").append(element.getMockServiceUrl()).append("\n");
            stringBuf.append("Service proxyurl: ").append(element.getUrl().getPath()).append("\n");
            List scenarios = element.getScenarios();
            for (Object scenario : scenarios) {
                Scenario b = (Scenario) scenario;
                stringBuf.append("    scenario name: ").append(b.getScenarioName()).append("\n");
                stringBuf.append("    scenario request: ").append(b.getRequestMessage()).append("\n");
                stringBuf.append("    scenario response: ").append(b.getResponseMessage()).append("\n");

            }

        }
        return stringBuf.toString();
    }

    /**
     * @return list of ClientRequest objects
     */
    public List<ClientRequest> getClientRequests() {
        return this.historyStore.getOrderedList();
    }

    public void deleteLoggedClientRequest(Long scenarioId) {

        historyStore.remove(scenarioId);

    }

    public void logClientRequest(ClientRequest mssb) {

        historyStore.save(mssb);

    }

    public void deleteAllLoggedClientRequestForService(Long serviceId) {
        for (Object o : historyStore.getOrderedList()) {
            ClientRequest object = (ClientRequest) o;
            if (object.getServiceInfo().getServiceId().equals(serviceId)) {
                this.historyStore.remove(object.getId());
            }
        }
    }

    public ProxyServerModel getProxy() {
        return this.proxyInfoBean;
    }

    public void setProxy(ProxyServerModel proxyInfoBean) {
        this.proxyInfoBean = proxyInfoBean;

    }

	public void deleteServicePlan(ServicePlan servicePlan) {
		if(servicePlan!=null){
			this.servicePlanStore.remove(servicePlan.getId());
		}
		
	}

	public ServicePlan getServicePlanById(Long servicePlanId) {
		return (ServicePlan)this.servicePlanStore.get(servicePlanId);
	}

		
	@SuppressWarnings("unchecked")
	public List getServicePlans() {
		return this.servicePlanStore.getOrderedList();
	}

	public void saveOrUpdateServicePlan(ServicePlan servicePlan) {
		this.servicePlanStore.save(servicePlan);
		
	}

    public Scenario getUniversalErrorScenario() {
        Scenario uErrorBean = null;
        Service msb = getServiceById(this.univeralErrorServiceId);
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

    public void deleteEverything() {
        historyStore = new OrderedMap();
        mockServiceStore = new OrderedMap();
        servicePlanStore = new OrderedMap();
        
    }

	@Override
	public List<String> uniqueClientIPs() {
		List<String> uniqueIPs = new ArrayList<String>();
		for (ClientRequest tx : this.store.getClientRequests()) {
			String tmpIP = tx.getServiceInfo().getRequestorIP();
			if (!uniqueIPs.contains(tmpIP)) {
				uniqueIPs.add(tmpIP);
			}
		}
		return uniqueIPs;
	}

	@Override
	public List<String> uniqueClientIPsForService(Service msb) {
		List<String> uniqueIPs = new ArrayList<String>();
		for (ClientRequest tx : this.store.getClientRequests()) {
			String ip = tx.getServiceInfo().getRequestorIP();
			if (!uniqueIPs.contains(ip) && tx.getServiceInfo().getServiceId()==msb.getId()) {
				uniqueIPs.add(ip);
			}
		}
		return uniqueIPs;
	}
}
