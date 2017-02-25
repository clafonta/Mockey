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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * A Scenario is a specific response from a Service.
 * 
 * @author chad.lafontaine
 */
public class Scenario extends StatusCheck implements PersistableItem {

	private Long id;
	private Long serviceId;
	// ****************
	// Why empty string and not use null?
	// We write/persist Scenario to XML and handling null and empty string gets
	// weird. XML doesn't allow 'attribute = null' but has 'attribute = ""'
	// ************
	private String scenarioName = "";
	private String requestMessage = "";
	private String responseMessage = "";
	private String matchStringArg = "";
	private boolean matchStringArgEvaluationRulesFlag = false;
	private String httpMethodType = "";
	private String responseHeader = "Accept-Language: en-US | Accept: text/plain";
	private int httpResponseStatusCode = HttpServletResponse.SC_OK;
	private int hangTime = 0;
	
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
		sb.append("Scenario name: " + this.getScenarioName() + "\n");
		sb.append("Match string : " + this.getMatchStringArg() + "\n");
		sb.append("Request msg  : " + this.getRequestMessage() + "\n");
		sb.append("Response msg : " + this.getResponseMessage() + "\n");
		// sb.append("Response code: " + this. + "\n");
		sb.append("Tag          : " + this.getTag() + "\n");
		sb.append("Hangtime     : " + this.getHangTime() + "\n");
		sb.append("Last visit   : " + this.getLastVisitSimple() + "\n");
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
	public boolean hasSameNameAndResponse(Scenario otherScenario) {
		try {

			if (isMatching(this.scenarioName, otherScenario.getScenarioName())
					&& isMatching(this.responseMessage,
							otherScenario.getResponseMessage())) {
				return true;

			}

		} catch (Exception e) {

		}
		return false;
	}

	public boolean isMatching(String arg1, String arg2) {
		boolean match = false;
		if (arg1 != null && arg2 != null) {
			if (arg1.trim().equalsIgnoreCase(arg2.trim())) {
				match = true;
			}
		} else if (arg1 == null && arg2 == null) {
			match = true;
		}
		return match;
	}

	public int getHttpResponseStatusCode() {
		return httpResponseStatusCode;
	}

	public void setHttpResponseStatusCode(int httpResponseStatusCode) {
		this.httpResponseStatusCode = httpResponseStatusCode;
	}

	public void setResponseHeader(String responseHeader) {
		this.responseHeader = responseHeader;
	}

	public String getResponseHeader() {
		return this.responseHeader;
	}

	/**
	 * Helper class to parse the header response into key value pairs.
	 * 
	 * @return
	 */
	public Map<String, String> getHeaderInfoHelper() {
		Map<String, String> m = new HashMap<String, String>();

		String[] args = this.responseHeader.split("\\|");

		for (String k : args) {
			int beginIndex = k.indexOf(":");
			if (beginIndex > -1) {
				String key = k.substring(0, beginIndex);
				String val = k.substring(beginIndex + 1);
				m.put(key.trim(), val.trim());
			}
		}
		return m;
	}

	/**
	 * 
	 * @return true if this Scenario's match argument should be treated as
	 *         evaluation rules in JSON format, otherwise false.
	 */
	public boolean isMatchStringArgEvaluationRulesFlag() {
		return matchStringArgEvaluationRulesFlag;
	}

	/**
	 * 
	 * @param matchStringArgEvaluationRulesFlag
	 *            set to true if this Scenario's match argument should be
	 *            treated as evaluation rules in JSON format
	 */
	public void setMatchStringArgEvaluationRulesFlag(
			boolean matchStringArgEvaluationRulesFlag) {
		this.matchStringArgEvaluationRulesFlag = matchStringArgEvaluationRulesFlag;
	}

	public String getHttpMethodType() {
		return httpMethodType;
	}

	public void setHttpMethodType(String httpMethodType) {
		this.httpMethodType = httpMethodType;
	}
	public int getHangTime() {
		return hangTime;
	}

	public void setHangTime(int hangTime) {
		this.hangTime = hangTime;
	}

}
