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

import java.util.List;

import com.mockey.model.ProxyServerModel;
import com.mockey.model.ServicePlan;
import com.mockey.model.RequestResponseTransaction;
import com.mockey.model.Service;
import com.mockey.model.Scenario;

/**
 * Storage interface to service and scenario definitions. 
 * @author chad.lafontaine
 *
 */
public interface IMockeyStorage {

	public Service getMockServiceById(Long serviceId);

	public ServicePlan getMockServicePlan(Long servicePlanId);
	/**
	 * 
	 * @return a list of MockServicePlan objects. 
	 */
	public List<ServicePlan> getServicePlans();
	public void saveOrUpdateServicePlan(ServicePlan servicePlan);
	public void deleteServicePlan(ServicePlan servicePlan);
	
	public Scenario getUniversalErrorResponse();
	public void setUniversalErrorServiceId(Long serviceId);
	public void setUniversalErrorScenarioId(Long scenarioId);
	/**
	 * 
	 * @param urlPath
	 * @return MockServiceBean if urlPath equals a mock URI, null otherwise.
	 */
	public Service getServiceByUrl(String urlPath);

	public void saveOrUpdate(Service service);

	public void deleteAll();
	public void delete(Service service);

	/**
	 * Support for proxy server 
	 * @return
	 */
	public ProxyServerModel getProxyInfo();
	
	/**
	 * 
	 * @param proxyInfoBean
	 */
	public void setProxyInfo(ProxyServerModel proxyInfoBean);
	
	/**
	 * 
	 * @return list of Service objects, ordered by id
	 */
	public List<Service> getOrderedListOfServices();

	/**
	 * 
	 * @return list of MockServiceScenarioBean objects
	 */
	public List<RequestResponseTransaction> getHistoryScenarios();
	
	public List<String> uniqueClientIPs();
	public List<String> uniqueClientIPsForService(Service msb);
	
	public void addHistoricalScenario(RequestResponseTransaction requestResponseX);
	
	public void deleteHistoricalScenario(Long scenarioId);
 
	public void flushHistoryRequestMsgs(Long serviceId);
}
