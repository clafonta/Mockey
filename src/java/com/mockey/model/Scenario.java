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
 * A Scenario is a specific response from a Service.
 * 
 * @author chad.lafontaine
 */
public class Scenario extends StatusCheck implements PersistableItem {

	private Long id;
	private Long serviceId;
	private String scenarioName;
	private String requestMessage;
	private String responseMessage;
	private String matchStringArg = null;
	
	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String name) {
		this.scenarioName = name;
	}

	public String getRequestMessage() {
		return requestMessage;
	}

	public void setRequestMessage(String requestMessage) {
		this.requestMessage = requestMessage;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getMatchStringArg() {
		return matchStringArg;
	}

	public void setMatchStringArg(String matchStringArg) {
		this.matchStringArg = matchStringArg;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Scenario name:" + this.getScenarioName());
		sb.append("Match string:" + this.getMatchStringArg());
		sb.append("Request msg:" + this.getRequestMessage());
		sb.append("Response msg:" + this.getResponseMessage());
		return sb.toString();
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public boolean hasMatchArgument() {
		if (getMatchStringArg() != null
				&& getMatchStringArg().trim().length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param otherScenario
	 * @return true if scenario name and scenario response message are equal
	 *         (case ignored), otherwise false.
	 */
	public boolean equals(Scenario otherScenario) {
		try {
			if (this.scenarioName.equalsIgnoreCase(otherScenario
					.getScenarioName())
					&& this.responseMessage.equalsIgnoreCase(otherScenario
							.getResponseMessage())) {
				return true;
			}
		} catch (Exception e) {

		}
		return false;
	}

	
}
