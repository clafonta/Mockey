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
public class PlanItem implements PersistableItem{
	private Long id;
	private String serviceName;
	private String scenarioName;
    
    private int hangTime;
    private int serviceResponseType;


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

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "PlanItem [id=" + id + ", serviceName=" + serviceName + ", scenarioName=" + scenarioName + ", hangTime="
				+ hangTime + ", serviceResponseType=" + serviceResponseType + "]";
	}
	
	

}
