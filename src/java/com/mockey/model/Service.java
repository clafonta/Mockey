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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.mockey.ClientExecuteProxy;
import com.mockey.ClientExecuteProxyException;
import com.mockey.OrderedMap;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.ui.Util;

/**
 * A Service is a remote url that can be called.
 * 
 * @author chad.lafontaine
 * 
 */
public class Service implements PersistableItem, ExecutableService {

	public final static int SERVICE_RESPONSE_TYPE_PROXY = 0;
	public final static int SERVICE_RESPONSE_TYPE_STATIC_SCENARIO = 1;
	public final static int SERVICE_RESPONSE_TYPE_DYNAMIC_SCENARIO = 2;

	private Long id;
	private String serviceName;
	private String description;
	private Boolean transientState = new Boolean(false);
	private Long defaultScenarioId;
	private int defaultRealUrlIndex = 0;
	private Long errorScenarioId;
	private String httpContentType = "text/html;charset=utf-8";
	private int hangTime = 0;
	private OrderedMap<Scenario> scenarios = new OrderedMap<Scenario>();
	private int serviceResponseType = SERVICE_RESPONSE_TYPE_PROXY;
	private String httpMethod = "GET";
	private String url = "";
	private List<FulfilledClientRequest> fulfilledRequests;
	private List<Url> realServiceUrls = new ArrayList<Url>();
	private boolean allowRedirectFollow = true;
	private static Log logger = LogFactory.getLog(Service.class);
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	public List<FulfilledClientRequest> getFulfilledRequests() {
		return fulfilledRequests;
	}

	public void setFulfilledRequests(List<FulfilledClientRequest> transactions) {
		this.fulfilledRequests = transactions;
	}

	// default constructor for xml.
	// DO NOT REMOVE. DO NOT CALL.
	public Service() {
		this.setServiceName("");
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public Long getDefaultScenarioId() {
		
		return this.defaultScenarioId;
	}
	
	/**
	 *
	 * @return null if no default scenario defined, otherwise, returns name
	 */
	public String getDefaultScenarioName(){
		Scenario s = this.getScenario(this.defaultScenarioId);
		if(s!=null){
			return s.getScenarioName();
		}else {
			return null;
		}
	}

	public void setDefaultScenarioId(Long did) {

		this.defaultScenarioId = did;
	}

	/**
	 * Finds a service scenario with matching name. If a match is found, then
	 * its ID is set as the default scenario id. If no match is found, then no
	 * change. Name matching is case insensitive, and leading and ending
	 * whitespace is trimmed.
	 * 
	 * @param scenarioName
	 */
	public void setDefaultScenarioByName(String scenarioName) {
		if (scenarioName != null) {
			for (Scenario scenario : this.scenarios.getOrderedList()) {
				if (scenarioName.trim().equalsIgnoreCase((scenario.getScenarioName().trim()))) {
					this.setDefaultScenarioId(scenario.getId());
					break;
				}
			}
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String name) {

		this.serviceName = name;

	}

	public int getHangTime() {
		return hangTime;
	}

	public void setHangTime(int hangTime) {
		this.hangTime = hangTime;
	}

	public List<Scenario> getScenarios() {
		return Util.orderAlphabeticallyByScenarioName(scenarios.getOrderedList());
	}

	public Scenario getScenario(Long scenarioId) {
		return (Scenario) scenarios.get(scenarioId);
	}

	public void deleteScenario(Long scenarioId) {
		this.scenarios.remove(scenarioId);
	}

	public Scenario saveOrUpdateScenario(Scenario scenario) {
		scenario.setServiceId(this.id);
		return (Scenario) this.scenarios.save(scenario);
	}

	/**
	 * DO NOT REMOVE. This is needed by XML reader and has a reference to the
	 * method signature via reflection. Thank Digester.
	 * 
	 * @param realServiceUrl
	 * @deprecated - this method will call
	 *             <code>saveOrUpdateRealServiceUrl(Url)</code>
	 * @see #saveOrUpdateRealServiceUrl(Url)
	 */
	public void setRealServiceUrlByString(String realServiceUrl) {
		this.saveOrUpdateRealServiceUrl(new Url(realServiceUrl));
	}

	public String getHttpContentType() {
		return httpContentType;
	}

	public void setHttpContentType(String httpContentType) {
		this.httpContentType = httpContentType;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Service name:").append(this.getServiceName()).append("\n");
		sb.append("Real URL(s):\n");
		if (this.realServiceUrls != null && !this.realServiceUrls.isEmpty()) {
			Iterator<Url> iter = this.realServiceUrls.iterator();
			while (iter.hasNext()) {
				sb.append(iter.next() + "\n");
			}
		} else {
			sb.append("(no real urls defined for this service)\n");
		}

		sb.append("Default scenario ID:").append(this.getDefaultScenarioId()).append("\n");
		sb.append("HTTP Content:").append(this.getHttpContentType()).append("\n");
		sb.append("Hang time:");
		sb.append(this.getHangTime());
		sb.append("\n");

		return sb.toString();
	}

	public void setId(Long id) {
		this.id = id;

		// Recursively set this ID to child Scenarios, if any exist.
		for (Scenario scenario : getScenarios()) {
			scenario.setServiceId(this.id);
			this.saveOrUpdateScenario(scenario);
		}
	}

	public Long getId() {
		return id;
	}

	/**
	 * 
	 * @deprecated
	 * @see #getRealServiceUrls()
	 */
	public String getRealServiceUrl() {
		return "[DEPRECATED]";

	}

	/**
	 * 
	 * @param serviceResponseType
	 *            - 0 (proxy), 1 (static), or 2 (dynamic). Any other value will
	 *            default to PROXY.
	 */
	public void setServiceResponseType(int serviceResponseType) {
		if (serviceResponseType == 1 || serviceResponseType == 0 || serviceResponseType == 2) {
			this.serviceResponseType = serviceResponseType;
		} else {
			this.serviceResponseType = SERVICE_RESPONSE_TYPE_PROXY;
		}
		validateDefaultScenarioId();

	}

	// HELPER method - let's validate the 'defaultScenarioId'. If
	// defaultScenarioId doesn't equal any of the scenario IDs, then
	// auto-set the defaultID to the 'first' scenario
	private void validateDefaultScenarioId() {
		boolean valid = false;
		List<Scenario> orderedList = this.scenarios.getOrderedList();
		
		for(Scenario s: orderedList){
	
			if(s.getId().equals(this.getDefaultScenarioId()) ) {
				valid = true;
				break;
			}
		}
		if(!valid){
			if(this.scenarios.getOrderedList().size() > 0){
				this.setDefaultScenarioId(orderedList.get(0).getId());
			}else {
				// Reset
				this.setDefaultScenarioId(null);
			}
		}
	}

	/**
	 * Takes 'proxy', 'static', or 'dynamic' arguments and translates them to
	 * appropriate 'int' values and then calls
	 * <code>setServiceResponseType</code>
	 * 
	 * @see #setServiceResponseType(int)
	 */
	public void setServiceResponseTypeByString(String arg) {
		if (arg != null) {
			if ("proxy".trim().equalsIgnoreCase(arg.trim()) || "0".equalsIgnoreCase(arg.trim())) {
				setServiceResponseType(Service.SERVICE_RESPONSE_TYPE_PROXY);
			} else if ("static".trim().equalsIgnoreCase(arg.trim()) || "1".equalsIgnoreCase(arg.trim())) {
				setServiceResponseType(Service.SERVICE_RESPONSE_TYPE_STATIC_SCENARIO);
			} else if ("dynamic".trim().equalsIgnoreCase(arg.trim()) || "2".equalsIgnoreCase(arg.trim())) {
				setServiceResponseType(Service.SERVICE_RESPONSE_TYPE_DYNAMIC_SCENARIO);
			}
		}
	}

	public int getServiceResponseType() {
		// If no scenarios, then proxy is automatically on.
		if (this.getScenarios().size() == 0) {
			return SERVICE_RESPONSE_TYPE_PROXY;
		} else {
			return serviceResponseType;
		}
	}
	
	public String getServiceResponseTypeAsString(){
		int x = getServiceResponseType();
		if(x == Service.SERVICE_RESPONSE_TYPE_PROXY){
			return "proxy";
		}else if(x == Service.SERVICE_RESPONSE_TYPE_STATIC_SCENARIO){
			return "static";
		}else if(x == Service.SERVICE_RESPONSE_TYPE_DYNAMIC_SCENARIO){
			return "dynamic";			
		}else {
			return "";
		}
		
	}

	public void setErrorScenarioId(Long errorScenarioId) {
		this.errorScenarioId = errorScenarioId;
	}

	public Long getErrorScenarioId() {
		return errorScenarioId;
	}

	public Scenario getErrorScenario() {
		// FIND SERVICE ERROR, IF EXIST.
		for (Scenario scenario : this.getScenarios()) {
			if (scenario.getId() == this.getErrorScenarioId()) {
				return scenario;
			}
		}
		// No service error defined, therefore, let's use the universal
		// error.
		return StorageRegistry.MockeyStorage.getUniversalErrorScenario();
	}

	public Boolean isReferencedInAServicePlan() {
		Boolean isReferenced = false;
		for (ServicePlan plan : StorageRegistry.MockeyStorage.getServicePlans()) {
			for (PlanItem planItem : plan.getPlanItemList()) {
				if (planItem.getServiceId().equals(this.getId())) {
					isReferenced = true;
					break;
				}
			}
		}
		return isReferenced;
	}

	/**
	 * The core method to execute the request as either a Proxy, Dynamic, or
	 * Static Scenario.
	 */
	public ResponseFromService execute(RequestFromClient request, Url realServiceUrl) {
		ResponseFromService response = null;
		if (this.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_PROXY) {
			response = proxyTheRequest(request, realServiceUrl);
		} else if (this.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_DYNAMIC_SCENARIO) {
			response = executeDynamicScenario(request, realServiceUrl);
		} else if (this.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_STATIC_SCENARIO) {
			response = executeStaticScenario(realServiceUrl);
		}
		return response;
	}

	private ResponseFromService proxyTheRequest(RequestFromClient request, Url realServiceUrl) {

		logger.debug("proxying a moxie.");
		// If proxy on, then
		// 1) Capture request message.
		// 2) Set up a connection to the real service URL
		// 3) Forward the request message to the real service URL
		// 4) Read the reply from the real service URL.
		// 5) Save request + response as a historical scenario.

		// There are 2 proxy things going on here:
		// 1. Using Mockey as a 'proxy' to a real service.
		// 2. The proxy server between Mockey and the real service.
		//
		// For the proxy server between Mockey and the real service,
		// we do the following:
		ProxyServerModel proxyServer = store.getProxy();

		ClientExecuteProxy clientExecuteProxy = ClientExecuteProxy.getClientExecuteProxyInstance();
		ResponseFromService response = null;

		// If Twisting is on, then
		// 1)
		try {
			logger.debug("Initiating request through proxy");
			TwistInfo twistInfo = store.getTwistInfoById(store.getUniversalTwistInfoId());
			
			response = clientExecuteProxy.execute(twistInfo, proxyServer, realServiceUrl, allowRedirectFollow, request);

		} catch (ClientExecuteProxyException e) {
			// We're here for various reasons.
			// 1) timeout from calling real service.
			// 2) unable to parse real response.
			// 3) magic!
			// Before we throw an exception, check:
			// (A) does this mock service have a default error response. If
			// no, then
			// (B) see if Mockey has a universal error response
			// If neither, then throw the exception.
			response = new ResponseFromService();
			response.setRequestUrl(e.getRequestUrl());
			Scenario error = this.getErrorScenario();
			if (error != null) {
				response.setBody(error.getResponseMessage());
			} else {
				StringBuffer msg = new StringBuffer();
				JSONObject jsonResponseObject = new JSONObject();
				try {
					jsonResponseObject
							.put("fail",
									"We encountered an error. Here's some information to help point out what may have gone wrong.");
					if (proxyServer != null && proxyServer.isProxyEnabled()) {
						if (proxyServer.getProxyHost() != null && proxyServer.getProxyHost().trim().length() > 0) {
							jsonResponseObject.put("proxyInfo", "Internet proxy settings are ENABLED pointing to -->"
									+ proxyServer.getProxyHost() + "<-- ");
						} else {
							jsonResponseObject.put("proxyInfo",
									"Internet proxy settings are ENABLED but Internet Proxy Server value is EMPTY.");
						}
					} else {
						jsonResponseObject.put("proxyInfo", "Proxy settings are NOT ENABLED. ");
					}
					msg.append(jsonResponseObject.toString());
				} catch (Exception ae) {
					logger.error("Nothing is going right here.", ae);
					msg.append("Experiencing some difficulties. ");
				}
				response.setBody(msg.toString());
			}
		}
		return response;
	}

	private ResponseFromService executeStaticScenario(Url realServiceUrl) {

		logger.debug("mockeying a static scenario");

		// Proxy is NOT on. Therefore we use a scenario to figure out a reply.
		// Either:
		// 1) Based on matching the request message to one of the scenarios
		// or
		// 2) Based on scenario selected.
		//
		Scenario scenario = this.getScenario(this.getDefaultScenarioId());
		ResponseFromService response = new ResponseFromService();

		if (scenario != null) {
			response.setBody(scenario.getResponseMessage());
		} else {
			response.setBody("NO SCENARIO SELECTED");
		}
		response.setRequestUrl(realServiceUrl);
		return response;
	}

	private ResponseFromService executeDynamicScenario(RequestFromClient request, Url realServiceUrl) {

		logger.debug("mockeying a dynamic scenario.");
		StringBuffer rawRequestDataBuffer = new StringBuffer();
		try {
			rawRequestDataBuffer.append(request.buildParameterRequest());
			if (request.hasPostBody()) {
				rawRequestDataBuffer.append(request.getBodyInfo());
			}
		} catch (UnsupportedEncodingException e) {
			// uhm.
			logger.debug("Unable to extract content from request", e);
		}
		String rawRequestData = rawRequestDataBuffer.toString();
		ResponseFromService response = new ResponseFromService();
		List<Scenario> scenarios = this.getScenarios();
		Iterator<Scenario> iter = scenarios.iterator();
		String messageMatchFound = null;
		while (iter.hasNext()) {
			Scenario scenario = iter.next();
			logger.debug("Checking: '" + scenario.getMatchStringArg() + "' in Scenario message: \n" + rawRequestData);
			int indexValue = -1;
			if (scenario.hasMatchArgument()) {
				indexValue = rawRequestData.indexOf(scenario.getMatchStringArg());
			}
			if ((indexValue > -1)) {
				logger.debug("FOUND - matching '" + scenario.getMatchStringArg() + "' ");
				messageMatchFound = scenario.getResponseMessage();
				break;
			}
		}
		// OK, no matches. Error handling is as follows:
		// 1) Does service have a default service error defined? If yes, return
		// message. If no...
		// 2) Does Mockey have a universal error message defined? If yes,
		// return, otherwise...
		// 3) Return a error message.
		if (messageMatchFound == null) {
			Scenario u = getErrorScenario();
			if (u == null) {
				u = store.getUniversalErrorScenario();
			}
			if (u != null) {
				messageMatchFound = u.getResponseMessage();
			} else {
				messageMatchFound = "Ouch, no love for you! Why? Well, it could be that this service setting "
						+ "is set to Dynamic but there is no found matching scenario, nor is there a default "
						+ "service-scenario-error defined, nor is there a universal-scenario-error defined "
						+ "for this incoming request. In otherwords, Mockey doesn't know what to do.";
			}

		}
		response.setRequestUrl(realServiceUrl);
		response.setBody(messageMatchFound);
		return response;
	}

	private String getNiceNameForService(String arg) {
		String name = arg;
		// Remove parameters
		int index = arg.indexOf("?");
		if (index > 0) {
			arg = arg.substring(0, index);
		}
		StringTokenizer st = new StringTokenizer(arg, "/");
		while (st.hasMoreTokens()) {
			// Eventually, we get the last token, and
			// we use it as the name.
			name = st.nextToken();
		}
		name = name + " (auto generated)";

		return name;
	}

	public List<Url> getRealServiceUrls() {
		return realServiceUrls;
	}

	public void setRealServiceUrls(List<Url> realServiceUrls) {
		this.realServiceUrls = realServiceUrls;
	}

	/**
	 * 
	 * @param url
	 */
	public void saveOrUpdateRealServiceUrl(Url url) {

		if (url != null) {

			boolean found = false;
			for (int i = 0; i < this.realServiceUrls.size(); i++) {
				Url tmpUrl = this.realServiceUrls.get(i);
				if (tmpUrl.getFullUrl().equalsIgnoreCase(url.getFullUrl())) {

					this.realServiceUrls.set(i, url);

					found = true;
					break;
				}
			}
			if (!found && !url.getFullUrl().trim().isEmpty()) {
				this.realServiceUrls.add(url);
			}

			// BONUS
			// If this service name is undefined, then we try to determine
			// an informative name based on the url
			if (this.serviceName != null && this.serviceName.trim().isEmpty()) {
				this.setServiceName(this.getNiceNameForService(url.getFullUrl()));
			}

		}
	}

	/**
	 * 
	 * @param otherService
	 * @return non null if _this_ and otherService both have non-empty list of
	 *         <code>Url</code> objects with a matching <code>Url</code> object.
	 *         Otherwise, returns false;
	 */
	public Url getFirstMatchingRealServiceUrl(Service otherService) {

		Url matchUrl = null;
		if (this.realServiceUrls != null && otherService != null && !otherService.getRealServiceUrls().isEmpty()) {

			for (Url otherUrl : otherService.getRealServiceUrls()) {
				if (this.hasRealServiceUrl(otherUrl)) {
					matchUrl = otherUrl;
					break;
				}
			}
		}
		return matchUrl;
	}

	public boolean hasRealServiceUrl(Url url) {
		boolean has = false;
		try {
			for (Url urlTmp : this.realServiceUrls) {
				if (urlTmp.getFullUrl().equalsIgnoreCase(url.getFullUrl())) {
					has = true;
					break;
				}
			}
		} catch (Exception e) {
			// do nothing
		}
		return has;
	}

	public static void main(String[] args) {

		Service a = new Service();
		Service b = new Service();
		Url aUrl = new Url("http://www.google.com");
		Url cUrl = new Url("http://www.cnn.com");
		Url bUrl = new Url("http://www.cnn.com");
		a.saveOrUpdateRealServiceUrl(aUrl);
		a.saveOrUpdateRealServiceUrl(cUrl);
		b.saveOrUpdateRealServiceUrl(bUrl);
		System.out.print("Answer: " + a.getFirstMatchingRealServiceUrl(b));

	}

	/**
	 * 
	 * @param url
	 * @see #getUrl()
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Mock URL. It's possible that this URL looks like one of the Real URLs.
	 * But, this value can be anything, but should be unique in the list of
	 * Services.
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	public int getDefaultRealUrlIndex() {
		return this.defaultRealUrlIndex;
	}

	public void setDefaultRealUrlIndex(int i) {
		this.defaultRealUrlIndex = i;
	}

	public Url getDefaultRealUrl() {
		Url d = null;
		try {
			d = this.realServiceUrls.get(this.defaultRealUrlIndex);
		} catch (Exception e) {
			// OK, let's try and be smart.
			// Reset index.
			this.defaultRealUrlIndex = 0;
			if (!this.realServiceUrls.isEmpty()) {
				d = this.realServiceUrls.get(0);
			}
		}
		return d;
	}

	public void setTransientState(Boolean transientState) {
		this.transientState = transientState;
	}

	public Boolean getTransientState() {
		return transientState;
	}
}
