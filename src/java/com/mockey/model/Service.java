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
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.ClientExecuteProxy;
import com.mockey.ClientExecuteProxyException;
import com.mockey.OrderedMap;
import com.mockey.plugin.RequestInspectorDefinedByJson;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.ui.Util;
import java.io.Serializable;

/**
 * A Service is a remote url that can be called.
 * 
 * @author chad.lafontaine
 * 
 */
public class Service extends StatusCheck implements PersistableItem, ExecutableService, Serializable {

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
	private int hangTime = 0;
	private String requestInspectorName;
	private String requestInspectorJsonRules = "";
	private boolean requestInspectorJsonRulesEnableFlag = false;
	private OrderedMap<Scenario> scenarios = new OrderedMap<Scenario>();
	private int serviceResponseType = SERVICE_RESPONSE_TYPE_PROXY;
	private String httpMethod = "GET";
	private String url = "";
	private String responseSchema = "";
	private boolean responseSchemaFlag = false;

	private List<FulfilledClientRequest> fulfilledRequests;
	private List<Url> realServiceUrlList = new ArrayList<Url>();
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
	public String getDefaultScenarioName() {
		Scenario s = this.getScenario(this.defaultScenarioId);
		if (s != null) {
			return s.getScenarioName();
		} else {
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

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Service name:").append(this.getServiceName()).append("\n");
		sb.append("Real URL(s):\n");
		if (this.realServiceUrlList != null && !this.realServiceUrlList.isEmpty()) {
			Iterator<Url> iter = this.realServiceUrlList.iterator();
			while (iter.hasNext()) {
				sb.append(iter.next() + "\n");
			}
		} else {
			sb.append("(no real urls defined for this service)\n");
		}

		sb.append("Default scenario ID:").append(this.getDefaultScenarioId()).append("\n");
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

		for (Scenario s : orderedList) {

			if (s.getId().equals(this.getDefaultScenarioId())) {
				valid = true;
				break;
			}
		}
		if (!valid) {
			if (this.scenarios.getOrderedList().size() > 0) {
				this.setDefaultScenarioId(orderedList.get(0).getId());
			} else {
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

	public String getServiceResponseTypeAsString() {
		int x = getServiceResponseType();
		if (x == Service.SERVICE_RESPONSE_TYPE_PROXY) {
			return "proxy";
		} else if (x == Service.SERVICE_RESPONSE_TYPE_STATIC_SCENARIO) {
			return "static";
		} else if (x == Service.SERVICE_RESPONSE_TYPE_DYNAMIC_SCENARIO) {
			return "dynamic";
		} else {
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
			if (scenario.getId().equals(this.getErrorScenarioId())) {
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
				if (planItem.getServiceName().equals(this.getServiceName())) {
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
		this.setLastVisit(new Long(Calendar.getInstance().getTimeInMillis()));
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
			response = clientExecuteProxy.execute(proxyServer, realServiceUrl, allowRedirectFollow, request);
			response.setScenarioName("");

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
		response.setScenarioName("(No name; proxy response)");
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
			response.setScenarioName(scenario.getScenarioName());
			response.setScenarioTagsAsString(scenario.getTag());
			response.setBody(scenario.getResponseMessage());
			response.setServiceScenarioHangTime(scenario.getHangTime());
			response.setHttpResponseStatusCode(scenario.getHttpResponseStatusCode());
			scenario.setLastVisit(new Long(Calendar.getInstance().getTimeInMillis()));

			Map<String, String> headerInfo = scenario.getHeaderInfoHelper();
			List<Header> headerList = new ArrayList<Header>();
			for (String k : headerInfo.keySet()) {
				headerList.add(new BasicHeader(k, headerInfo.get(k)));
			}
			response.setHeaders(headerList.toArray(new Header[headerList.size()]));

		} else {
			response.setBody("NO SCENARIO SELECTED");
		}
		response.setRequestUrl(realServiceUrl);
		return response;
	}

	private ResponseFromService executeDynamicScenario(RequestFromClient request, Url realServiceUrl) {

		// To make things a little easy, we will
		// concatenate request Parameters and Body (if one was posted)
		// into 1 long String argument, and then evaluate for
		// the existence of a match string argument.
		// In addition, if this Service's mock URL is a
		// RESTful pattern, then we'll also try to extract the
		// token ID from the 'realServiceUrl' based on this
		// service's pattern.

		// STEP 1. "Build the request String to evaluate"
		logger.debug("mockeying a dynamic scenario.");
		StringBuffer rawRequestDataBuffer = new StringBuffer();

		// Optional REST token from the URL
		Url mockUrl = new Url(this.getUrl());
		// Example: "http://example.com/hotels/{hotel}/bookings/{room}"
		UriTemplate template = new UriTemplate(mockUrl.getFullUrl());
		// Example: "http://example.com/hotels/1/bookings/42"
		@SuppressWarnings("rawtypes")
		Map restTokenResults = template.match(realServiceUrl.getFullUrl());
		@SuppressWarnings("unchecked")
		Iterator<String> tokenKeyIterator = restTokenResults.keySet().iterator();
		while (tokenKeyIterator.hasNext()) {
			String key = tokenKeyIterator.next();
			rawRequestDataBuffer.append(restTokenResults.get(key));
		}

		// Optional parameters and body
		try {
			rawRequestDataBuffer.append(request.buildParameterRequest());
			if (request.hasPostBody()) {
				rawRequestDataBuffer.append(request.getBodyInfo());
			}
		} catch (UnsupportedEncodingException e) {

			logger.debug("Unable to extract content from request", e);
		}
		String rawRequestData = "";
		try {
			rawRequestData = URLDecoder.decode(rawRequestDataBuffer.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Unable to URL un-encode (or decode) the following: \n " + rawRequestDataBuffer.toString(), e);
		}

		// STEP 2. "We iterate through each Service Scenario and evaluate"

		// TOPIC: 'Scenario argument matching'
		// ===================================
		// A few things to note on this logic loop. Let's say the incoming
		// request has 'ABC123' in the request and we have Scenario A with
		// match argument '123' and Scenario B with match argument 'ABC123'.
		// The goal is to return Scenario B, in this case because there's a
		// match with 'ABC123'. Unfortunately, Scenario A works too, because
		// 'ABC123' contains '123'. Which should be returned?
		// In this particular case, we want to return Scenario B, because it
		// has the-longest-string match value, i.e. 6 character match vs.
		// 3 characters.
		ResponseFromService response = new ResponseFromService();
		List<Scenario> scenarios = this.getScenarios();
		Iterator<Scenario> iter = scenarios.iterator();
		String messageMatchFound = null;
		int httpResponseStatus = -1;
		int matchArgLength = -1;
		Scenario bestMatchedScenario = null;
		// We must visit ALL Scenarios, without any short circuits.
		while (iter.hasNext()) {
			Scenario scenario = iter.next();
			logger.debug("Checking: '" + scenario.getMatchStringArg() + "' in Scenario message: \n" + rawRequestData);
			int indexValue = -1;
			int tempTotalRuleSuccessfulEvaluationCount = -1;
			// For RESTful support of VERB (method type), we check if a Scenario
			// value is set, and if so, it matches incoming request method type.
			// All TYPES will be allowed if Scenario's method type is 'empty', 'null', or '*' (wildcard). 
			// Otherwise, ONLY a matching type will be looked at. 
			String incomingRequestMethod = request.getMethod();
			if (scenario.getHttpMethodType() == null || scenario.getHttpMethodType().trim().length() == 0
					|| "*".equals(scenario.getHttpMethodType().trim())
					|| scenario.getHttpMethodType().trim().equalsIgnoreCase(incomingRequestMethod)) {

				if (scenario.hasMatchArgument()) {
					if (scenario.isMatchStringArgEvaluationRulesFlag()) {

						try {
							RequestInspectorDefinedByJson jsonRulesInspector = new RequestInspectorDefinedByJson(
									scenario.getMatchStringArg());

							jsonRulesInspector.analyze(request);

							if (jsonRulesInspector.hasAnySuccessForAtLeastOneRuleType()) {

								// No errors, so we have a match.
								indexValue = 1;
								// Capture the number of _valid_ rules successfully processed.
								tempTotalRuleSuccessfulEvaluationCount = jsonRulesInspector.getValidRuleCount();

							} else {
								logger.debug("No match. Reason: " + jsonRulesInspector.getPostAnalyzeResultMessage());
							}

						} catch (JSONException e) {
							String msg = "Unable to parse JSON rules from scenario: " + scenario.getScenarioName();
							logger.debug(msg, e);
							// Unable to interpret this, so we assume
							// no match
						}

					} else {
						// Case insensitive
						tempTotalRuleSuccessfulEvaluationCount = scenario.getMatchStringArg().trim().length();
						indexValue = rawRequestData.toLowerCase().indexOf(scenario.getMatchStringArg().toLowerCase());
					}
				}
				// OK, we have found a match-argument that is in the REQUEST,
				// via 'indexValue > -1' but is it the longest matching argument
				// via 'tempArgLength > matchArgLength'?
				if ((indexValue > -1) && tempTotalRuleSuccessfulEvaluationCount > matchArgLength) {
					matchArgLength = tempTotalRuleSuccessfulEvaluationCount;
					bestMatchedScenario = scenario;
				}
			}
		}

		if (bestMatchedScenario != null) {
			logger.debug("FOUND - matching '" + bestMatchedScenario.getMatchStringArg() + "' ");
			messageMatchFound = bestMatchedScenario.getResponseMessage();
			httpResponseStatus = bestMatchedScenario.getHttpResponseStatusCode();
			// SET RULE_FOR_HEADERS
			Map<String, String> headerInfo = bestMatchedScenario.getHeaderInfoHelper();
			List<Header> headerList = new ArrayList<Header>();
			for (String k : headerInfo.keySet()) {
				headerList.add(new BasicHeader(k, headerInfo.get(k)));
			}
			response.setServiceScenarioHangTime(bestMatchedScenario.getHangTime());
			response.setScenarioName(bestMatchedScenario.getScenarioName());
			response.setHeaders(headerList.toArray(new Header[headerList.size()]));
			response.setScenarioName(bestMatchedScenario.getScenarioName());
			response.setScenarioTagsAsString(bestMatchedScenario.getTag());
		}
		// If we have no matches. Error handling is as follows:
		// 1) Does service have a default service error defined? If yes, return
		// message. If no...
		// 2) Does Mockey have a universal error message defined? If yes,
		// return, otherwise...
		// 3) Return a error message.
		if (messageMatchFound == null) {
			response.setScenarioName("(No matching scenario)");
			Scenario u = getErrorScenario();
			if (u == null) {
				u = store.getUniversalErrorScenario();
			}
			if (u != null) {
				messageMatchFound = u.getResponseMessage();
				httpResponseStatus = u.getHttpResponseStatusCode();
				
				// SET RULE_FOR_HEADERS
				Map<String, String> headerInfo = u.getHeaderInfoHelper();
				List<Header> headerList = new ArrayList<Header>();
				for (String k : headerInfo.keySet()) {
					headerList.add(new BasicHeader(k, headerInfo.get(k)));
				}
				
				response.setHeaders(headerList.toArray(new Header[headerList.size()]));
				response.setScenarioName(u.getScenarioName());
				response.setScenarioTagsAsString(u.getTag());
			} else {
				messageMatchFound = "Yikes, no love for you! Why? Well, it could be that this service setting "
						+ "is set to Dynamic but there is no found matching scenario, nor is there a default "
						+ "service-scenario-error defined, nor is there a universal-scenario-error defined "
						+ "for this incoming request. In otherwords, Mockey doesn't know what to do.";
			}

		}
		response.setRequestUrl(realServiceUrl);
		response.setBody(messageMatchFound);
		response.setHttpResponseStatusCode(httpResponseStatus);
		
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
		return realServiceUrlList;
	}

	public void clearRealServiceUrls() {
		realServiceUrlList = new ArrayList<Url>();
	}

	/**
	 * 
	 * @param url
	 */
	public void saveOrUpdateRealServiceUrl(Url url) {

		if (url != null) {

			boolean found = this.hasRealServiceUrl(url);
			if (!found && !url.getFullUrl().trim().isEmpty()) {
				this.realServiceUrlList.add(url);
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
		if (this.realServiceUrlList != null && otherService != null && !otherService.getRealServiceUrls().isEmpty()) {

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
			for (Url urlTmp : this.realServiceUrlList) {
				if (urlTmp.getFullUrl().trim().equalsIgnoreCase(url.getFullUrl())) {
					has = true;
					break;
				}
			}
		} catch (Exception e) {
			// do nothing
		}
		return has;
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
			d = this.realServiceUrlList.get(this.defaultRealUrlIndex);
		} catch (Exception e) {
			// OK, let's try and be smart.
			// Reset index.
			this.defaultRealUrlIndex = 0;
			if (!this.realServiceUrlList.isEmpty()) {
				d = this.realServiceUrlList.get(0);
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

	public boolean hasTag(String tag) {
		boolean has = super.hasTag(tag);
		if (!has) {
			// Check scenarios...
			for (Scenario s : this.getScenarios()) {
				has = s.hasTag(tag);
				if (has) {
					break;
				}
			}
		}
		return has;
	}

	/**
	 * 
	 * @return the Class Name of the Java class responsible for validation of
	 *         all incoming requests.
	 * @see com.mockey.plugin.IRequestInspector
	 */
	public String getRequestInspectorName() {
		return requestInspectorName;
	}

	public void setRequestInspectorName(String requestInspectorName) {
		this.requestInspectorName = requestInspectorName;
	}

	/**
	 * 
	 * @return request inspection/validation rules defined in JSON format.
	 */
	public String getRequestInspectorJsonRules() {

		return requestInspectorJsonRules;
	}

	/**
	 * 
	 * @param _requestInspectorJsonRules
	 *            can be null or empty or invalid JSON format to be able display
	 *            to customers invalid input. Validation will be done elsewhere.
	 *            If not null, argument will be trimmed prior to being set.
	 */
	public void setRequestInspectorJsonRules(String _requestInspectorJsonRules) {
		if (_requestInspectorJsonRules != null) {
			this.requestInspectorJsonRules = _requestInspectorJsonRules;
		} else {

			this.requestInspectorJsonRules = _requestInspectorJsonRules;
		}
	}

	/**
	 * 
	 * @return true if the request inspector's JSON rules should be processed
	 *         per request.
	 */
	public boolean isRequestInspectorJsonRulesEnableFlag() {
		return requestInspectorJsonRulesEnableFlag;
	}

	public void setRequestInspectorJsonRulesEnableFlag(boolean requestInspectorJsonRulesEnableFlag) {
		this.requestInspectorJsonRulesEnableFlag = requestInspectorJsonRulesEnableFlag;
	}

	/**
	 * Response schema is used to validate a Service's Scenario format, to help
	 * developers quickly find out if their Service Scenario(s) are invalid.
	 * 
	 * @return a string representing a schema.
	 */
	public String getResponseSchema() {
		return responseSchema;
	}

	/**
	 * 
	 * @param responseSchema
	 */
	public void setResponseSchema(String responseSchema) {
		this.responseSchema = responseSchema;
	}

	public boolean isResponseSchemaFlag() {
		return responseSchemaFlag;
	}

	public void setResponseSchemaFlag(boolean responseSchemaFlag) {
		this.responseSchemaFlag = responseSchemaFlag;
	}
}
