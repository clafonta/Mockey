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
import com.mockey.model.Url;

public class TestMockeyServiceStore implements IMockeyStorage {

    public void deleteService(Service mockServiceBean) {
        // TODO Auto-generated method stub

    }

    public void deleteFulfilledClientRequestsFromIP(Long scenarioId) {
        // TODO Auto-generated method stub

    }

    public void deleteServicePlan(ServicePlan servicePlan) {
        // TODO Auto-generated method stub

    }
    
    public FulfilledClientRequest getFulfilledClientRequestsById(Long fulfilledClientRequestId){
        return null;
    }

    public void deleteFulfilledClientRequestsForService(Long serviceId) {
        // TODO Auto-generated method stub

    }

    public List<FulfilledClientRequest> getFulfilledClientRequests() {
        // TODO Auto-generated method stub
        return null;
    }

    public Service getServiceById(Long serviceId) {
        // TODO Auto-generated method stub
        return null;
    }

    public Service getServiceByUrl(String urlPath) {
        // TODO Auto-generated method stub
        return null;
    }

    public ServicePlan getServicePlanById(Long servicePlanId) {
        // TODO Auto-generated method stub
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
        Service service = new Service(new Url("http://someservice:8000/eai2"));
        service.setServiceName("testname");
        service.setDescription("test description");
        
        
        Scenario scenario = new Scenario();
        scenario.setScenarioName("a");
        scenario.setRequestMessage("request message a");
        scenario.setResponseMessage("response message a");
        service.updateScenario(scenario);
        scenario = new Scenario();
        scenario.setScenarioName("b");
        scenario.setRequestMessage("request message b");
        scenario.setResponseMessage("response message b");
        service.updateScenario(scenario);
        services.add(service);
        Service service2 = new Service(new Url("http://someservice:8000/eai2"));
        service2.setServiceName("testname2");
        service2.setDescription("test description2");
        
       
        Scenario scenario2 = new Scenario();
        scenario2.setScenarioName("a");
        scenario2.setRequestMessage("request message a");
        scenario2.setResponseMessage("response message a");
        service2.updateScenario(scenario);
        scenario2 = new Scenario();
        scenario2.setScenarioName("b");
        scenario2.setRequestMessage("request message b");
        scenario2.setResponseMessage("response message b");
        service2.updateScenario(scenario);
        services.add(service2);
        return services;
    }

    public ProxyServerModel getProxy() {
        // TODO Auto-generated method stub
        return null;
    }

    public void saveOrUpdateService(Service mockServiceBean) {
        // TODO Auto-generated method stub

    }

    public void saveOrUpdateServicePlan(ServicePlan servicePlan) {
        // TODO Auto-generated method stub

    }

    public void setProxy(ProxyServerModel proxyInfoBean) {
        // TODO Auto-generated method stub

    }

    public Scenario getUniversalErrorScenario() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setUniversalErrorScenarioId(Long scenarioId) {
        // TODO Auto-generated method stub

    }

    public void setUniversalErrorServiceId(Long serviceId) {
        // TODO Auto-generated method stub

    }

    public void saveOrUpdateFulfilledClientRequest(FulfilledClientRequest requestResponseX) {
        // TODO Auto-generated method stub

    }

    public void deleteEverything() {
        // TODO Auto-generated method stub
        
    }


	public List<String> uniqueClientIPs() {
		// TODO Auto-generated method stub
		return null;
	}


	public List<String> uniqueClientIPsForService(Long serviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<FulfilledClientRequest> getFulfilledClientRequestsForService(
			Long serviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<FulfilledClientRequest> getFulfilledClientRequestsFromIP(
			String ip) {
		// TODO Auto-generated method stub
		return null;
	}


	public List<FulfilledClientRequest> getFulfilledClientRequestsFromIPForService(
			String ip, Long serviceId) {
		// TODO Auto-generated method stub
		return null;
	}

    public void deleteFulfilledClientRequests() {
        // TODO Auto-generated method stub
        
    }

    public void deleteFulfilledClientRequestsFromIPForService(String ip, Long serviceId) {
        // TODO Auto-generated method stub
        
    }

    public void deleteFulfilledClientRequestById(Long fulfilledRequestID) {
        // TODO Auto-generated method stub
        
    }

    public List<FulfilledClientRequest> getFulfilledClientRequest(List<String> filterArguments) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<FulfilledClientRequest> getFulfilledClientRequest(Collection<String> filterArguments) {
        // TODO Auto-generated method stub
        return null;
    }

}
