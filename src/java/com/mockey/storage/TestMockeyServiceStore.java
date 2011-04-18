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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.PlanItem;
import com.mockey.model.ProxyServerModel;
import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.model.ServiceRef;
import com.mockey.model.TwistInfo;

public class TestMockeyServiceStore implements IMockeyStorage {

	public void deleteService(Service mockServiceBean) {

	}

	public void deleteFulfilledClientRequestsFromIP(Long scenarioId) {

	}

	public void deleteServicePlan(ServicePlan servicePlan) {

	}

	public FulfilledClientRequest getFulfilledClientRequestsById(Long fulfilledClientRequestId) {
		return null;
	}

	public void deleteFulfilledClientRequestsForService(Long serviceId) {

	}

	public List<FulfilledClientRequest> getFulfilledClientRequests() {

		return null;
	}

	public Service getServiceById(Long serviceId) {

		return null;
	}

	public Service getServiceByUrl(String urlPath) {

		return null;
	}

	public ServicePlan getServicePlanById(Long servicePlanId) {

		return null;
	}

	public List<ServicePlan> getServicePlans() {
		List<ServicePlan> planList = new ArrayList<ServicePlan>();
		ServicePlan servicePlan = new ServicePlan();
		servicePlan.setId(new Long(1));
		servicePlan.setName("happy path");
		servicePlan.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. ");
		PlanItem planItem = new PlanItem();
		planItem.setServiceResponseType(Service.SERVICE_RESPONSE_TYPE_PROXY);

		planItem.setScenarioId(new Long(123));
		planItem.setServiceId(new Long(234));
		servicePlan.addPlanItem(planItem);
		planItem = new PlanItem();
		planItem.setServiceResponseType(Service.SERVICE_RESPONSE_TYPE_PROXY);
		planItem.setScenarioId(new Long(98));
		planItem.setServiceId(new Long(97877));
		servicePlan.addPlanItem(planItem);
		planList.add(servicePlan);

		servicePlan = new ServicePlan();
		servicePlan.setId(new Long(2));
		servicePlan.setName("trial and tribulation");
		servicePlan.setDescription("battle of beetles in bottles");
		planItem = new PlanItem();
		planItem.setServiceResponseType(Service.SERVICE_RESPONSE_TYPE_STATIC_SCENARIO);
		planItem.setScenarioId(new Long(1));
		planItem.setServiceId(new Long(2));
		servicePlan.addPlanItem(planItem);
		planItem = new PlanItem();
		planItem.setServiceResponseType(Service.SERVICE_RESPONSE_TYPE_STATIC_SCENARIO);
		planItem.setScenarioId(new Long(3));
		planItem.setServiceId(new Long(4));
		servicePlan.addPlanItem(planItem);
		planList.add(servicePlan);
		return planList;
	}

	public List<Service> getServices() {
		List<Service> services = new ArrayList<Service>();
		Service service = new Service();
		service.setServiceName("testname");
		service.setDescription("test description");

		Scenario scenario = new Scenario();
		scenario.setScenarioName("a");
		scenario.setRequestMessage("request message a");
		scenario.setResponseMessage("response message a");
		service.saveOrUpdateScenario(scenario);
		scenario = new Scenario();
		scenario.setScenarioName("b");
		scenario.setRequestMessage("request message b");
		scenario.setResponseMessage("response message b");
		service.saveOrUpdateScenario(scenario);
		services.add(service);
		Service service2 = new Service();
		service2.setServiceName("testname2");
		service2.setDescription("test description2");

		Scenario scenario2 = new Scenario();
		scenario2.setScenarioName("a");
		scenario2.setRequestMessage("request message a");
		scenario2.setResponseMessage("response message a");
		service2.saveOrUpdateScenario(scenario);
		scenario2 = new Scenario();
		scenario2.setScenarioName("b");
		scenario2.setRequestMessage("request message b");
		scenario2.setResponseMessage("response message b");
		service2.saveOrUpdateScenario(scenario);
		services.add(service2);
		return services;
	}

	public ProxyServerModel getProxy() {

		return null;
	}

	public Service saveOrUpdateService(Service mockServiceBean) {

		return null;

	}

	public ServicePlan saveOrUpdateServicePlan(ServicePlan servicePlan) {

		return null;

	}

	public void setProxy(ProxyServerModel proxyInfoBean) {

	}

	public Scenario getUniversalErrorScenario() {

		return null;
	}

	public void setUniversalErrorScenarioId(Long scenarioId) {

	}

	public void setUniversalErrorServiceId(Long serviceId) {

	}

	public void saveOrUpdateFulfilledClientRequest(FulfilledClientRequest requestResponseX) {

	}

	public void deleteEverything() {

	}

	public List<String> uniqueClientIPs() {

		return null;
	}

	public List<String> uniqueClientIPsForService(Long serviceId) {

		return null;
	}

	public List<FulfilledClientRequest> getFulfilledClientRequestsForService(Long serviceId) {

		return null;
	}

	public List<FulfilledClientRequest> getFulfilledClientRequestsFromIP(String ip) {

		return null;
	}

	public List<FulfilledClientRequest> getFulfilledClientRequestsFromIPForService(String ip, Long serviceId) {

		return null;
	}

	public void deleteFulfilledClientRequests() {

	}

	public void deleteFulfilledClientRequestsFromIPForService(String ip, Long serviceId) {

	}

	public void deleteFulfilledClientRequestById(Long fulfilledRequestID) {

	}

	public List<FulfilledClientRequest> getFulfilledClientRequest(List<String> filterArguments) {

		return null;
	}

	public List<FulfilledClientRequest> getFulfilledClientRequest(Collection<String> filterArguments) {

		return null;
	}

	public List<Long> getServiceIds() {

		return null;
	}

	public ServicePlan getServicePlanByName(String servicePlanName) {

		return null;
	}

	public List<TwistInfo> getTwistInfoList() {

		return null;
	}

	public TwistInfo getTwistInfoById(Long id) {

		return null;
	}

	public TwistInfo getTwistInfoByName(String name) {

		return null;
	}

	public TwistInfo saveOrUpdateTwistInfo(TwistInfo twistInfo) {

		return null;
	}

	public void deleteTwistInfo(TwistInfo twistInfo) {

	}

	public Long getUniversalTwistInfoId() {

		return null;

	}

	public void setUniversalTwistInfoId(Long twistInfoId) {

	}

	public Long getUniversalErrorScenarioId() {

		return null;
	}

	public Long getUniversalErrorServiceId() {

		return null;
	}

	public Service getServiceByName(String serviceName) {

		return null;
	}

	public List<ServiceRef> getServiceRefs() {

		return null;
	}

	public ServiceRef saveOrUpdateServiceRef(ServiceRef serviceRef) {

		return null;
	}

}
