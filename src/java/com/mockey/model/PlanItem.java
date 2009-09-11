/*
 * Copyright 2002-2009 the original author or authors.
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
package com.mockey.model;

/**
 * Mapping of desired settings for a service for the purpose of quickly setting
 * a desired state, part of a ServicePlan.
 * 
 * @see com.mockey.model.ServicePlan
 * 
 * @author chad.lafontaine
 * 
 */
public class PlanItem {
    private Long serviceId;
    private Long scenarioId;
    private int hangTime;
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

    public void setHangTime(int hangTime) {
        this.hangTime = hangTime;
    }

    public int getHangTime() {
        return hangTime;
    }

}
