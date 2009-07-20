package com.mockey;

public class PlanItem  {
	private Long serviceId;
	private Long scenarioId;
	private boolean proxyOn;
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public Long getScenarioId() {
		return scenarioId;
	}
	public void setScenarioId(Long scenarioId) {
		this.scenarioId = scenarioId;
	}
	public boolean isProxyOn() {
		return proxyOn;
	}
	public void setProxyOn(boolean proxyOn) {
		this.proxyOn = proxyOn;
	}
	
}
