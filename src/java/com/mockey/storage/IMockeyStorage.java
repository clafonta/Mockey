/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2010  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
 * neil.cronin (neil AT rackle DOT com) 
 * lorin.kobashigawa (lkb AT kgawa DOT com)
 * rob.meyer (rob AT bigdis DOT com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.mockey.storage;

import java.util.Collection;
import java.util.*;

import com.mockey.model.*;

/**
 * How Mockey stores itself.
 * 
 * @author chad.lafontaine
 * 
 */
public interface IMockeyStorage {
	
	public Long getTimeOfCreation();

	public String getDefaultServicePlanId();

	public Long getDefaultServicePlanIdAsLong();

	public void setDefaultServicePlanId(String v);

	public void setReadOnlyMode(Boolean transientState);

	public String getGlobalStateSystemFilterTag();

	public Boolean getReadOnlyMode();

	public void deleteEverything();

	public List<TwistInfo> getTwistInfoList();

	public TwistInfo getTwistInfoById(Long id);

	public TwistInfo getTwistInfoByName(String name);

	public TwistInfo saveOrUpdateTwistInfo(TwistInfo twistInfo);

	public void deleteTwistInfo(TwistInfo twistInfo);

	public Service getServiceById(Long serviceId);

	public Service getServiceByUrl(String urlPath);

	public List<Long> getServiceIds();

	public List<Service> getServices();

	public Service saveOrUpdateService(Service service);

	public Service duplicateService(Service service);

	public void deleteService(Service service);

	public ServiceRef saveOrUpdateServiceRef(ServiceRef serviceRef);

	public List<ServiceRef> getServiceRefs();

	public ServicePlan getServicePlanById(Long servicePlanId);

	public ServicePlan getServicePlanByName(String servicePlanName);

	public List<ServicePlan> getServicePlans();

	public ServicePlan saveOrUpdateServicePlan(ServicePlan servicePlan);

	/**
	 * Goes through each ServicePlan and updates the Scenario Name associated to
	 * the matching Service and Scenario IDs.
	 * 
	 * @param serviceId
	 *            - needed to filter out only scenario updates associated with
	 *            the appropriate service.
	 * @param oldScenarioName
	 * @param newScenarioName
	 */
	public void updateServicePlansWithNewScenarioName(Long serviceId, String oldScenarioName, String newScenarioName);

	public void updateServicePlansWithNewServiceName(String oldServiceName, String newServiceName);

	public void deleteServicePlan(ServicePlan servicePlan);

	public ScenarioRef getUniversalErrorScenarioRef();

	public Scenario getUniversalErrorScenario();

	public Long getUniversalTwistInfoId();

	public void setUniversalTwistInfoId(Long twistInfoId);

	public void setUniversalErrorScenarioRef(ScenarioRef scenario);

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

	public void deleteTagFromStore(String tag);

	public Set<TagItem> getAllTagsFromStore();

	public void setGlobalStateSystemFilterTag(String tag);

	public Service getServiceByName(String serviceName);

	public void setServicePlan(ServicePlan servicePlan);
}
