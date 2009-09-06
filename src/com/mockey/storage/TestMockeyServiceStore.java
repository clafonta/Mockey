package com.mockey.storage;

import java.util.ArrayList;
import java.util.List;

import com.mockey.model.PlanItem;
import com.mockey.model.ProxyServerModel;
import com.mockey.model.ServicePlan;
import com.mockey.model.RequestResponseTransaction;
import com.mockey.model.Service;
import com.mockey.model.Scenario;
import com.mockey.model.Url;

public class TestMockeyServiceStore implements IMockeyStorage {

    public void delete(Service mockServiceBean) {
        // TODO Auto-generated method stub

    }

    public void deleteHistoricalScenario(Long scenarioId) {
        // TODO Auto-generated method stub

    }

    public void deleteServicePlan(ServicePlan servicePlan) {
        // TODO Auto-generated method stub

    }

    public void flushHistoryRequestMsgs(Long serviceId) {
        // TODO Auto-generated method stub

    }

    public List getHistoryScenarios() {
        // TODO Auto-generated method stub
        return null;
    }

    public Service getMockServiceById(Long serviceId) {
        // TODO Auto-generated method stub
        return null;
    }

    public Service getServiceByUrl(String urlPath) {
        // TODO Auto-generated method stub
        return null;
    }

    public ServicePlan getMockServicePlan(Long servicePlanId) {
        // TODO Auto-generated method stub
        return null;
    }

    public List getServicePlans() {
        List planList = new ArrayList();
        ServicePlan msp = new ServicePlan();
        msp.setId(new Long(1));
        msp.setName("happy path");
        msp.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. ");
        PlanItem planItem = new PlanItem();
        planItem.setServiceResponseType(Service.SERVICE_RESPONSE_TYPE_PROXY);

        planItem.setScenarioId(new Long(123));
        planItem.setServiceId(new Long(234));
        msp.addPlanItem(planItem);
        planItem = new PlanItem();
        planItem.setServiceResponseType(Service.SERVICE_RESPONSE_TYPE_PROXY);
        planItem.setScenarioId(new Long(98));
        planItem.setServiceId(new Long(97877));
        msp.addPlanItem(planItem);
        planList.add(msp);

        msp = new ServicePlan();
        msp.setId(new Long(2));
        msp.setName("trial and tribulation");
        msp.setDescription("battle of beetles in bottles");
        planItem = new PlanItem();
        planItem.setServiceResponseType(Service.SERVICE_RESPONSE_TYPE_STATIC_SCENARIO);
        planItem.setScenarioId(new Long(1));
        planItem.setServiceId(new Long(2));
        msp.addPlanItem(planItem);
        planItem = new PlanItem();
        planItem.setServiceResponseType(Service.SERVICE_RESPONSE_TYPE_STATIC_SCENARIO);
        planItem.setScenarioId(new Long(3));
        planItem.setServiceId(new Long(4));
        msp.addPlanItem(planItem);
        planList.add(msp);
        return planList;
    }

    public List getOrderedListOfServices() {
        List beans = new ArrayList();
        Service bean = new Service(new Url("http://someservice:8000/eai2"));
        bean.setServiceName("testname");
        bean.setDescription("test description");
        
        
        Scenario mssb = new Scenario();
        mssb.setScenarioName("a");
        mssb.setRequestMessage("request message a");
        mssb.setResponseMessage("response message a");
        bean.updateScenario(mssb);
        mssb = new Scenario();
        mssb.setScenarioName("b");
        mssb.setRequestMessage("request message b");
        mssb.setResponseMessage("response message b");
        bean.updateScenario(mssb);
        beans.add(bean);
        Service bean2 = new Service(new Url("http://someservice:8000/eai2"));
        bean2.setServiceName("testname2");
        bean2.setDescription("test description2");
        
       
        Scenario mssb2 = new Scenario();
        mssb2.setScenarioName("a");
        mssb2.setRequestMessage("request message a");
        mssb2.setResponseMessage("response message a");
        bean2.updateScenario(mssb);
        mssb2 = new Scenario();
        mssb2.setScenarioName("b");
        mssb2.setRequestMessage("request message b");
        mssb2.setResponseMessage("response message b");
        bean2.updateScenario(mssb);
        beans.add(bean2);
        return beans;
    }

    public ProxyServerModel getProxyInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    public void saveOrUpdate(Service mockServiceBean) {
        // TODO Auto-generated method stub

    }

    public void saveOrUpdateServicePlan(ServicePlan servicePlan) {
        // TODO Auto-generated method stub

    }

    public void setProxyInfo(ProxyServerModel proxyInfoBean) {
        // TODO Auto-generated method stub

    }

    public Scenario getUniversalErrorResponse() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setUniversalErrorScenarioId(Long scenarioId) {
        // TODO Auto-generated method stub

    }

    public void setUniversalErrorServiceId(Long serviceId) {
        // TODO Auto-generated method stub

    }

    public void addHistoricalScenario(RequestResponseTransaction requestResponseX) {
        // TODO Auto-generated method stub

    }

    public void deleteAll() {
        // TODO Auto-generated method stub
        
    }

	@Override
	public List<String> uniqueClientIPs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> uniqueClientIPsForService(Service msb) {
		// TODO Auto-generated method stub
		return null;
	}

}
