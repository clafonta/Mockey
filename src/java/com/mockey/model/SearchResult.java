/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2010  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
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
 * Very basic container for search results
 * 
 * @author chadlafontaine
 *
 */
public class SearchResult {
	
	private String content = null;
	private SearchResultType type = null;
	private String serviceId = null;
	private String scenarioId = null;
	private String servicePlanId = null;
	private String scenarioName = null;
	private String serviceName = null;
	private String servicePlanName = null;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String id) {
		this.serviceId = id;
	}
	public void setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
	}
	public String getScenarioId() {
		return scenarioId;
	}
	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}
	public String getScenarioName() {
		return scenarioName;
	}
	public SearchResultType getType() {
		return type;
	}
	public void setType(SearchResultType type) {
		this.type = type;
	}
	
	public String getServicePlanId() {
		return servicePlanId;
	}
	public void setServicePlanId(String servicePlanId) {
		this.servicePlanId = servicePlanId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServicePlanName() {
		return servicePlanName;
	}
	public void setServicePlanName(String servicePlanName) {
		this.servicePlanName = servicePlanName;
	}
	public String getTypeAsString(){
		if(this.type!=null){
			return this.type.toString();
		}else {
			return null;
		}
	}
	

}
