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
import java.util.Collection;
import java.util.List;

import org.apache.http.Header;
import org.apache.log4j.Logger;

import com.mockey.OrderedMap;
import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.ProxyServerModel;
import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.model.Url;

/**
 * In memory implementation to the storage of mock services and scenarios.
 * 
 * @author chad.lafontaine
 */
public class InMemoryMockeyStorage implements IMockeyStorage {

    private OrderedMap<FulfilledClientRequest> historyStore = new OrderedMap<FulfilledClientRequest>();
    private OrderedMap<Service> mockServiceStore = new OrderedMap<Service>();
    private OrderedMap<ServicePlan> servicePlanStore = new OrderedMap<ServicePlan>();

    private static Logger logger = Logger.getLogger(InMemoryMockeyStorage.class);
    private ProxyServerModel proxyInfoBean = new ProxyServerModel();

    private Long univeralErrorServiceId = null;
    private Long univeralErrorScenarioId = null;
    private static InMemoryMockeyStorage store = new InMemoryMockeyStorage();

    /**
     * 
     * @return
     */
    static InMemoryMockeyStorage getInstance() {
        return store;
    }

    /**
     * HACK: this class is supposed to be a singleton but making this public for
     * XML parsing (Digester)
     * 
     * Error is:
     * 
     * Class org.apache.commons.digester.ObjectCreateRule can not access a
     * member of class com.mockey.storage.InMemoryMockeyStorage with modifiers
     * "private"
     * 
     * Possible Fix: write/implement objectcreatefactory classes.
     * 
     * Example:
     * 
     * <pre>
     * http://jsp.codefetch.com/example/fr/storefront-source/com/oreilly/struts/storefront/service/memory/StorefrontMemoryDatabase.java?qy=parse+xml
     * </pre>
     */
    public InMemoryMockeyStorage() {
    }

    public Service getServiceById(Long id) {
        return mockServiceStore.get(id);
    }

    public Service getServiceByUrl(String url) {

        try {
            for (Service service : getServices()) {
                if (service.getUrl().getFullUrl().equals(url)) {
                    return service;
                }
            }
        } catch (Exception e) {
            logger.error("Unable to retrieve service w/ url pattern: " + url, e);
        }

        logger.debug("Didn't find service with Service path: " + url + ".  Creating a new one.");
        Service service = new Service(new Url(url));
        store.saveOrUpdateService(service);
        return service;
    }

    public void saveOrUpdateService(Service mockServiceBean) {
        mockServiceStore.save(mockServiceBean);
    }

    public void deleteService(Service mockServiceBean) {
        if (mockServiceBean != null) {
            mockServiceStore.remove(mockServiceBean.getId());
        }
    }

    public List<Service> getServices() {
        return this.mockServiceStore.getOrderedList();
    }

    public String toString() {
        return new MockeyStorageWriter().StorageAsString(this);
    }

    /**
     * @return list of FulfilledClientRequest objects
     */
    public List<FulfilledClientRequest> getFulfilledClientRequests() {
        return this.historyStore.getOrderedList();
    }

    public void deleteFulfilledClientRequestsFromIP(Long scenarioId) {
        historyStore.remove(scenarioId);
    }

    public void logClientRequest(FulfilledClientRequest request) {
        logger.debug("saving a request.");
        historyStore.save(request);
    }

    public void deleteFulfilledClientRequestsForService(Long serviceId) {
        for (FulfilledClientRequest req : historyStore.getOrderedList()) {
            if (req.getServiceId().equals(serviceId)) {
                this.historyStore.remove(req.getId());
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
        if (servicePlan != null) {
            this.servicePlanStore.remove(servicePlan.getId());
        }
    }

    public ServicePlan getServicePlanById(Long servicePlanId) {
        return servicePlanStore.get(servicePlanId);
    }

    public List<ServicePlan> getServicePlans() {
        return this.servicePlanStore.getOrderedList();
    }

    public void saveOrUpdateServicePlan(ServicePlan servicePlan) {
        this.servicePlanStore.save(servicePlan);
    }

    public Scenario getUniversalErrorScenario() {
        Scenario error = null;
        Service service = getServiceById(this.univeralErrorServiceId);
        if (service != null) {
            error = service.getScenario(this.univeralErrorScenarioId);
        }
        return error;
    }

    public void setUniversalErrorScenarioId(Long scenarioId) {
        this.univeralErrorScenarioId = scenarioId;
    }

    public void setUniversalErrorServiceId(Long serviceId) {
        this.univeralErrorServiceId = serviceId;
    }

    public void deleteEverything() {
        historyStore = new OrderedMap<FulfilledClientRequest>();
        mockServiceStore = new OrderedMap<Service>();
        servicePlanStore = new OrderedMap<ServicePlan>();
    }

    public List<String> uniqueClientIPs() {
        List<String> uniqueIPs = new ArrayList<String>();
        for (FulfilledClientRequest tx : this.historyStore.getOrderedList()) {
            String tmpIP = tx.getRequestorIP();
            if (!uniqueIPs.contains(tmpIP)) {
                uniqueIPs.add(tmpIP);
            }
        }
        return uniqueIPs;
    }

    public List<String> uniqueClientIPsForService(Long serviceId) {

        logger.debug("getting IPs for serviceId: " + serviceId + ". there are a total of " + this.historyStore.size()
                + " requests currently stored.");

        List<String> uniqueIPs = new ArrayList<String>();
        for (FulfilledClientRequest tx : this.historyStore.getOrderedList()) {
            String ip = tx.getRequestorIP();
            if (!uniqueIPs.contains(ip) && tx.getServiceId().equals(serviceId)) {
                uniqueIPs.add(ip);
            }
        }
        return uniqueIPs;
    }

    public List<FulfilledClientRequest> getFulfilledClientRequestsForService(Long serviceId) {
        logger.debug("getting requests for serviceId: " + serviceId + ". there are a total of "
                + this.historyStore.size() + " requests currently stored.");
        List<FulfilledClientRequest> rv = new ArrayList<FulfilledClientRequest>();
        for (FulfilledClientRequest req : this.historyStore.getOrderedList()) {
            if (req.getServiceId().equals(serviceId)) {
                rv.add(req);
            }
        }
        return rv;
    }

    public List<FulfilledClientRequest> getFulfilledClientRequestsFromIP(String ip) {
        List<FulfilledClientRequest> rv = new ArrayList<FulfilledClientRequest>();
        for (FulfilledClientRequest req : this.historyStore.getOrderedList()) {
            if (req.getRequestorIP().equals(ip)) {
                rv.add(req);
            }
        }
        return rv;
    }

    public List<FulfilledClientRequest> getFulfilledClientRequestsFromIPForService(String ip, Long serviceId) {
        List<FulfilledClientRequest> rv = new ArrayList<FulfilledClientRequest>();
        for (FulfilledClientRequest req : this.historyStore.getOrderedList()) {
            if (req.getServiceId().equals(serviceId) && req.getRequestorIP().equals(ip)) {
                rv.add(req);
            }
        }
        return rv;
    }

    public void deleteFulfilledClientRequests() {
        historyStore = new OrderedMap<FulfilledClientRequest>();

    }

    public void deleteFulfilledClientRequestsFromIPForService(String ip, Long serviceId) {
        for (FulfilledClientRequest req : historyStore.getOrderedList()) {
            if (req.getServiceId().equals(serviceId) && req.getRequestorIP().equals(ip)) {
                this.historyStore.remove(req.getId());
            }
        }

    }

    public void deleteFulfilledClientRequestById(Long fulfilledRequestID) {
        for (FulfilledClientRequest req : historyStore.getOrderedList()) {
            if (req.getId().equals(fulfilledRequestID)) {
                this.historyStore.remove(req.getId());
            }
        }

    }

    /**
     * Filters list with AND not OR.
     */
    public List<FulfilledClientRequest> getFulfilledClientRequest(Collection<String> filterArguments) {

        List<FulfilledClientRequest> rv = new ArrayList<FulfilledClientRequest>();
        if (filterArguments.size() == 0) {
            rv = this.getFulfilledClientRequests();
        } else {

            for (FulfilledClientRequest req : this.historyStore.getOrderedList()) {
                boolean allFilterTokensPresentInReq = true;
                for (String filterArg : filterArguments) {
                    boolean tokenFound = false;
                    if (req.getServiceId().toString().indexOf(filterArg) > -1) {
                        tokenFound = true;

                    } else if (req.getClientRequestBody().indexOf(filterArg) > -1) {
                        tokenFound = true;

                    } else if (req.getClientRequestHeaders().indexOf(filterArg) > -1) {
                        tokenFound = true;

                    } else if (req.getClientRequestParameters().indexOf(filterArg) > -1) {
                        tokenFound = true;

                    } else if (req.getRequestorIP().indexOf(filterArg) > -1) {
                        tokenFound = true;

                    } else if (req.getResponseMessage().getBody().indexOf(filterArg) > -1) {
                        tokenFound = true;

                    } else if (req.getResponseMessage().getHeaderInfo().indexOf(filterArg) > -1) {
                        tokenFound = true;

                    } else if (req.getServiceName().indexOf(filterArg) > -1) {
                        tokenFound = true;

                    } else {
                        Header[] headers = req.getResponseMessage().getHeaders();
                        for (Header header : headers) {
                            if (header.getName().indexOf(filterArg) > -1) {
                                tokenFound = true;
                                break;
                            } else if (header.getValue().indexOf(filterArg) > -1) {
                                tokenFound = true;
                                break;

                            }
                        }
                    }
                    if (!tokenFound) {
                        allFilterTokensPresentInReq = false;
                        break;
                    }

                }
                if (allFilterTokensPresentInReq) {
                    rv.add(req);
                }

            }
        }
        return rv;
    }
}
