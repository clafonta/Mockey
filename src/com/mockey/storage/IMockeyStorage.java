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
import com.mockey.model.ClientRequest;
import com.mockey.model.Service;
import com.mockey.model.Scenario;

/**
 * How Mockey stores itself.
 * @author chad.lafontaine
 *
 */
public interface IMockeyStorage {	

	public void deleteEverything();

	public Service getServiceById(Long serviceId);
	public Service getServiceByUrl(String urlPath);
	public List<Service> getServices();
	public void saveOrUpdateService(Service service);
	public void delete(Service service);

	public ServicePlan getServicePlanById(Long servicePlanId);
	public List<ServicePlan> getServicePlans();
	public void saveOrUpdateServicePlan(ServicePlan servicePlan);
	public void deleteServicePlan(ServicePlan servicePlan);
	
	public Scenario getUniversalErrorScenario();
	public void setUniversalErrorServiceId(Long serviceId);
	public void setUniversalErrorScenarioId(Long scenarioId);

	public ProxyServerModel getProxy();
	public void setProxy(ProxyServerModel proxy);
	
	public List<String> uniqueClientIPs();
	public List<String> uniqueClientIPsForService(Service msb);
	
	public List<ClientRequest> getClientRequests();
	public void logClientRequest(ClientRequest requestResponseX);
	public void deleteLoggedClientRequest(Long clientRequestId);
	public void deleteAllLoggedClientRequestForService(Long serviceId);
}
