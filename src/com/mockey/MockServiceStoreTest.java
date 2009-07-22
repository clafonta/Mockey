package com.mockey;

import java.util.ArrayList;
import java.util.List;

public class MockServiceStoreTest implements MockServiceStore {

	public void addHistoricalScenario(MockServiceScenarioBean mockServiceScenarioBean) {
		// TODO Auto-generated method stub

	}

	public void delete(MockServiceBean mockServiceBean) {
		// TODO Auto-generated method stub

	}

	public void deleteHistoricalScenario(Long scenarioId) {
		// TODO Auto-generated method stub

	}

	public void deleteServicePlan(MockServicePlan servicePlan) {
		// TODO Auto-generated method stub

	}

	public void flushHistoryRequestMsgs(Long serviceId) {
		// TODO Auto-generated method stub

	}

	public List getHistoryScenarios() {
		// TODO Auto-generated method stub
		return null;
	}

	public MockServiceBean getMockServiceById(Long serviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	public MockServiceBean getMockServiceByUrl(String urlPath) {
		// TODO Auto-generated method stub
		return null;
	}

	public MockServicePlan getMockServicePlan(Long servicePlanId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getMockServicePlanList() {
		List planList = new ArrayList();
		MockServicePlan msp = new MockServicePlan();
		msp.setId(new Long(1));
		msp.setName("happy path");
		msp.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. ");
		PlanItem planItem = new PlanItem();
		planItem.setProxyOn(true);
		planItem.setScenarioId(new Long(123));
		planItem.setServiceId(new Long(234));
		msp.addPlanItem(planItem);
		planItem = new PlanItem();
		planItem.setProxyOn(true);
		planItem.setScenarioId(new Long(98));
		planItem.setServiceId(new Long(97877));
		msp.addPlanItem(planItem);
		planList.add(msp);
		
		msp = new MockServicePlan();
		msp.setId(new Long(2));
		msp.setName("trial and tribulation");
		msp.setDescription("battle of beetles in bottles");
		planItem = new PlanItem();
		planItem.setProxyOn(false);
		planItem.setScenarioId(new Long(1));
		planItem.setServiceId(new Long(2));
		msp.addPlanItem(planItem);
		planItem = new PlanItem();
		planItem.setProxyOn(true);
		planItem.setScenarioId(new Long(3));
		planItem.setServiceId(new Long(4));
		msp.addPlanItem(planItem);
		planList.add(msp);
		return planList;
	}

	public List getOrderedList() {
		List beans = new ArrayList();
		MockServiceBean bean = new MockServiceBean();
		bean.setServiceName("testname");
		bean.setDescription("test description");
		bean.setRealServiceUrl("http://someservice:8000/eai");
		bean.setMockServiceUrl("/service/relizon");
		MockServiceScenarioBean mssb = new MockServiceScenarioBean();
		mssb.setScenarioName("a");
		mssb.setRequestMessage("request message a");
		mssb.setResponseMessage("response message a");
		bean.updateScenario(mssb);
		mssb = new MockServiceScenarioBean();
		mssb.setScenarioName("b");
		mssb.setRequestMessage("request message b");
		mssb.setResponseMessage("response message b");
		bean.updateScenario(mssb);
		beans.add(bean);
		MockServiceBean bean2 = new MockServiceBean();
		bean2.setServiceName("testname2");
		bean2.setDescription("test description2");
		bean2.setRealServiceUrl("http://someservice:8000/eai2");
		bean2.setMockServiceUrl("/service/relizon2");
		MockServiceScenarioBean mssb2 = new MockServiceScenarioBean();
		mssb2.setScenarioName("a");
		mssb2.setRequestMessage("request message a");
		mssb2.setResponseMessage("response message a");
		bean2.updateScenario(mssb);
		mssb2 = new MockServiceScenarioBean();
		mssb2.setScenarioName("b");
		mssb2.setRequestMessage("request message b");
		mssb2.setResponseMessage("response message b");
		bean2.updateScenario(mssb);
		beans.add(bean2);
		return beans;
	}

	public ProxyServer getProxyInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveOrUpdate(MockServiceBean mockServiceBean) {
		// TODO Auto-generated method stub

	}

	public void saveOrUpdateServicePlan(MockServicePlan servicePlan) {
		// TODO Auto-generated method stub

	}

	public void setProxyInfo(ProxyServer proxyInfoBean) {
		// TODO Auto-generated method stub

	}

}
