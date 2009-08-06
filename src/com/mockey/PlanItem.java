package com.mockey;

public class PlanItem  {
	private Long serviceId;
	private Long scenarioId;
	private int serviceResponseType;
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
    public void setServiceResponseType(int serviceResponseType) {
        this.serviceResponseType = serviceResponseType;
    }
    public int getServiceResponseType() {
        return serviceResponseType;
    }
	
}
