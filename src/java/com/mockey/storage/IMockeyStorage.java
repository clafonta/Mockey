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

import java.util.Collection;
import java.util.List;

import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.ProxyServerModel;
import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;

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
	public Service saveOrUpdateService(Service service);
	public void deleteService(Service service);

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
	public List<String> uniqueClientIPsForService(Long serviceId);
	
	public List<FulfilledClientRequest> getFulfilledClientRequests();
	public FulfilledClientRequest getFulfilledClientRequestsById(Long fulfilledClientRequestId);
	public List<FulfilledClientRequest> getFulfilledClientRequestsFromIP(String ip);
	public List<FulfilledClientRequest> getFulfilledClientRequestsForService(Long serviceId);
	public List<FulfilledClientRequest> getFulfilledClientRequestsFromIPForService(String ip, Long serviceId);
	public List<FulfilledClientRequest> getFulfilledClientRequest(Collection<String> filterArguments);
 	public void saveOrUpdateFulfilledClientRequest(FulfilledClientRequest requestResponseX);
	public void deleteFulfilledClientRequests();
	public void deleteFulfilledClientRequestById(Long fulfilledRequestID);
	public void deleteFulfilledClientRequestsFromIP(Long ip);	
	public void deleteFulfilledClientRequestsForService(Long serviceId);
	public void deleteFulfilledClientRequestsFromIPForService(String ip, Long serviceId);
}
