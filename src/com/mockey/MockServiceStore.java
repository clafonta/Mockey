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

/**
 * Storage interface to service and scenario definitions. 
 * @author chad.lafontaine
 *
 */
public interface MockServiceStore {

	public MockServiceBean getMockServiceById(Long serviceId);

	public MockServicePlan getMockServicePlan(Long servicePlanId);
	/**
	 * 
	 * @return a list of MockServicePlan objects. 
	 */
	public List getMockServicePlanList();
	public void saveOrUpdateServicePlan(MockServicePlan servicePlan);
	public void deleteServicePlan(MockServicePlan servicePlan);
	/**
	 * 
	 * @param urlPath
	 * @return MockServiceBean if urlPath equals a mock URI, null otherwise.
	 */
	public MockServiceBean getMockServiceByUrl(String urlPath);

	public void saveOrUpdate(MockServiceBean mockServiceBean);

	public void delete(MockServiceBean mockServiceBean);

	/**
	 * Support for proxy server 
	 * @return
	 */
	public ProxyServer getProxyInfo();
	
	/**
	 * 
	 * @param proxyInfoBean
	 */
	public void setProxyInfo(ProxyServer proxyInfoBean);
	
	/**
	 * 
	 * @return list of MockServiceBean objects, ordered by id
	 */
	public List getOrderedList();

	/**
	 * 
	 * @return list of MockServiceScenarioBean objects
	 */
	public List getHistoryScenarios();
	
	public void addHistoricalScenario(MockServiceScenarioBean mockServiceScenarioBean);
	
	public void deleteHistoricalScenario(Long scenarioId);
 
	public void flushHistoryRequestMsgs(Long serviceId);
}
