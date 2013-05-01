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
package com.mockey.storage;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.log4j.Logger;

import com.mockey.OrderedMap;
import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.PersistableItem;
import com.mockey.model.PlanItem;
import com.mockey.model.ProxyServerModel;
import com.mockey.model.Scenario;
import com.mockey.model.ScenarioRef;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.model.ServiceRef;
import com.mockey.model.TwistInfo;
import com.mockey.model.Url;
import com.mockey.model.UrlPatternMatchResult;
import com.mockey.model.UrlUtil;
import com.mockey.storage.xml.MockeyXmlFactory;
import com.mockey.storage.xml.MockeyXmlFileManager;

/**
 * In memory implementation to the storage of mock services and scenarios.
 * 
 * @author chad.lafontaine
 */
public class InMemoryMockeyStorage implements IMockeyStorage {

	private OrderedMap<FulfilledClientRequest> historyStore;
	private OrderedMap<Service> mockServiceStore = new OrderedMap<Service>();
	private OrderedMap<ServiceRef> serviceRefStore = new OrderedMap<ServiceRef>();
	private OrderedMap<ServicePlan> servicePlanStore = new OrderedMap<ServicePlan>();

	private OrderedMap<TwistInfo> twistInfoStore = new OrderedMap<TwistInfo>();
	private static Logger logger = Logger.getLogger(InMemoryMockeyStorage.class);
	private ProxyServerModel proxyInfoBean = new ProxyServerModel();
	private Long univeralTwistInfoId = null;
	private Long defaultServicePlanId = null;
	private Long universalErrorServiceId = null;
	private Long universalErrorScenarioId = null;
	private static InMemoryMockeyStorage store = new InMemoryMockeyStorage();
	// Yes, by default, we need this as TRUE.
	private Boolean transientState = new Boolean(true);
	private String globalFilterTag = null;

	/**
	 * 
	 * @return
	 */
	static InMemoryMockeyStorage getInstance() {

		return store;
	}

	/**
	 * 
	 * @return
	 */
	public void setReadOnlyMode(Boolean transientState) {
		if (transientState != null) {
			this.transientState = transientState;
		}

		if (!transientState) {
			this.writeMemoryToFile();
		}

	}

	/**
	 * 
	 * @return empty string if service plan id is null, otherwise long value as
	 *         string.
	 */
	public String getDefaultServicePlanId() {
		if (defaultServicePlanId != null) {
			return defaultServicePlanId.toString();
		} else {
			return "";
		}
	}

	/**
	 * 
	 * @return empty string if service plan id is null, otherwise long value as
	 *         string.
	 */
	public void setDefaultServicePlanId(String v) {
		try {
			this.defaultServicePlanId = new Long(v);
			this.writeMemoryToFile();
		} catch (Exception e) {
			// Do nothing. Leave value as is.
		}

	}

	public Long getDefaultServicePlanIdAsLong() {
		return this.defaultServicePlanId;
	}

	/**
	 * HACK: this class is supposed to be a singleton but making this public for
	 * XML parsing (Digester)
	 * 
	 * Error is:
	 * 
	 * Class org.apache.commons.digester.ObjectCreateRule can not access a
	 * member of class com.mockey.storage.InMemoryMockeyStorage with modifiers
	 * "private"
	 * 
	 * Possible Fix: write/implement objectcreatefactory classes.
	 * 
	 * Example:
	 * 
	 * <pre>
	 * http://jsp.codefetch.com/example/fr/storefront-source/com/oreilly/struts/storefront/service/memory/StorefrontMemoryDatabase.java?qy=parse+xml
	 * </pre>
	 */
	public InMemoryMockeyStorage() {
		initHistoryStore(); // Careful, more than ~45
		// and AJAX /JavaScript
		// gets funky.
	}

	public Service getServiceById(Long id) {
		return mockServiceStore.get(id);
	}

	public Service getServiceByName(String name) {
		if (name != null) {
			for (Service service : getServices()) {
				if (service.getServiceName() != null && service.getServiceName().trim().equalsIgnoreCase(name.trim())) {
					return service;
				}
			}
		}
		return null;
	}

	/**
	 * This will only return a Service
	 * 
	 * @see #getGlobalStateSystemFilterTag()
	 */
	public Service getServiceByUrl(String url) {

		Service service = null;
		String filterTag = this.getGlobalStateSystemFilterTag();
		try {

			// **************************************************
			// Be sure to include Filter match if non-empty
			// **************************************************

			Iterator<Service> iter = getServices().iterator();
			while (iter.hasNext()) {
				Service serviceTmp = iter.next();
				service = findServiceBasedOnUrlPattern(url, serviceTmp);
				if (service != null) {
					break;
				}
			}
		} catch (Exception e) {
			logger.error("Unable to retrieve service w/ url pattern: " + url, e);
		}

		if (service != null && filterTag != null && filterTag.trim().length() > 0 && service.hasTag(filterTag)) {
			logger.debug("Found service with Service path: " + url + ", with tag filter '" + filterTag + "'.");
			return service;
		} else if (service != null && filterTag != null && filterTag.trim().length() > 0 && !service.hasTag(filterTag)) {
			logger.debug("Found service with Service path: " + url + ", but DOES NOT have a matching tag filter of '"
					+ filterTag + "', so Mockey is returning not-found.");
			service = null;
		} else if (service != null) {
			logger.debug("Found service with Service path: " + url + ". No tag filter. ");
			return service;
		} else {
			logger.debug("Didn't find service with Service path: " + url + ".  Creating a new one.");
		}

		service = new Service();
		try {
			Url newUrl = new Url(Url.getSchemeHostPortPathFromURL(url));
			service.setUrl(newUrl.getFullUrl());
			service.saveOrUpdateRealServiceUrl(newUrl);
			store.saveOrUpdateService(service);

		} catch (MalformedURLException e) {
			logger.error("Unable to build a Service with URL '" + url + "'", e);
		}

		service.setTag(this.getGlobalStateSystemFilterTag());
		return service;
	}

	/**
	 * This will return a Service with a matching URL pattern, a Service's
	 * filter tags, and is RESTful aware
	 * 
	 * For example, we need to support the following:
	 * 
	 * <pre>
	 * http://www.service.com/customers - returns a list of customers. 
	 * http://www.service.com/customers/{ID} - returns a customer
	 * http://www.service.com/customers/{ID}/subscription - returns a customer's subscriptions entity
	 * </pre>
	 * 
	 * 
	 * @param url
	 *            - Incoming URL being requested.
	 * @param serviceToEvaluate
	 *            service state to compare to the url
	 * @return service if url pattern matches
	 */
	private Service findServiceBasedOnUrlPattern(String url, Service serviceToEvaluate) {
		Url fullUrl = new Url(serviceToEvaluate.getUrl());
		UrlPatternMatchResult result = UrlUtil.evaluateUrlPattern(url, fullUrl.getFullUrl());

		Service foundService = null;
		if (!result.isMatchingUrlPattern()) {
			// OK, not found based on primary Mock url.
			// Let's look at secondary list of real URLs
			List<Url> serviceUrlList = serviceToEvaluate.getRealServiceUrls();
			Iterator<Url> altUrlIter = serviceUrlList.iterator();
			while (altUrlIter.hasNext()) {
				Url altUrl = altUrlIter.next();
				result = UrlUtil.evaluateUrlPattern(url, altUrl.getFullUrl());
				if (result.isMatchingUrlPattern()) {
					foundService = serviceToEvaluate;
					break;
				}
			}
		} else {
			foundService = serviceToEvaluate;
		}
		return foundService;
	}

	/**
	 * Deep clone of a Service.
	 * 
	 * @param service
	 */
	public Service duplicateService(Service service) {
		Service newService = new Service();
		newService.setHangTime(service.getHangTime());
		newService.setHttpMethod(service.getHttpMethod());
		newService.setServiceName(service.getServiceName());
		newService.setServiceResponseTypeByString(service.getServiceResponseTypeAsString());
		newService.setDefaultRealUrlIndex(service.getDefaultRealUrlIndex());
		newService.setUrl(service.getUrl());
		// We don't do this because the Default scenario ID is not guaranteed to
		// be the same as the duplicate items are created.
		// newService.setDefaultScenarioId(service.getDefaultScenarioId());
		newService.setDescription(service.getDescription());
		// Meta data
		for (Url url : service.getRealServiceUrls()) {
			newService.saveOrUpdateRealServiceUrl(url);

		}
		// Why save, and save again below?
		// - The first save gets a Service ID created.
		// - Scenario's refer to the Service ID
		newService = this.saveOrUpdateService(newService);
		// Now add scenarios
		for (Scenario scenario : service.getScenarios()) {
			Scenario newScenario = new Scenario();
			newScenario.setHttpResponseStatusCode(scenario.getHttpResponseStatusCode());
			newScenario.setMatchStringArg(scenario.getMatchStringArg());
			newScenario.setResponseHeader(scenario.getResponseHeader());
			newScenario.setResponseMessage(scenario.getResponseMessage());
			newScenario.setScenarioName(scenario.getScenarioName());
			newScenario.setTag(scenario.getTag());

			newService.saveOrUpdateScenario(newScenario);

		}
		// Save AGAIN.
		newService = this.saveOrUpdateService(newService);
		return newService;
	}

	public Service saveOrUpdateService(Service mockServiceBean) {
		// System.out.println("XXXXXXXXXXXXX Saving Service. Name is: " +
		// mockServiceBean.getServiceName());
		PersistableItem item = mockServiceStore.save(mockServiceBean);
		if (mockServiceBean != null && !mockServiceBean.getTransientState()) {
			this.writeMemoryToFile();
		}
		return (Service) item;
	}

	public void deleteService(Service mockServiceBean) {
		if (mockServiceBean != null) {
			mockServiceStore.remove(mockServiceBean.getId());
			if (mockServiceBean != null && !mockServiceBean.getTransientState()) {
				this.writeMemoryToFile();
			}
		}
	}

	public List<Long> getServiceIds() {
		List<Long> ids = new ArrayList<Long>();
		for (Service service : this.getServices()) {
			ids.add(service.getId());
		}
		return ids;
	}

	public List<Service> getServices() {
		return this.mockServiceStore.getOrderedList();
	}

	// public String toString() {
	// return new MockeyStorageWriter().StorageAsString(this);
	// }

	/**
	 * @return list of FulfilledClientRequest objects
	 */
	public List<FulfilledClientRequest> getFulfilledClientRequests() {
		return this.historyStore.getOrderedList();
	}

	public void deleteFulfilledClientRequestsFromIP(Long scenarioId) {
		historyStore.remove(scenarioId);
	}

	public void saveOrUpdateFulfilledClientRequest(FulfilledClientRequest request) {
		logger.debug("saving a request.");
		historyStore.save(request);
	}

	public void deleteFulfilledClientRequestsForService(Long serviceId) {
		for (FulfilledClientRequest req : historyStore.getOrderedList()) {
			if (req.getServiceId().equals(serviceId)) {
				this.historyStore.remove(req.getId());
			}
		}
	}

	public ProxyServerModel getProxy() {
		return this.proxyInfoBean;
	}

	public void setProxy(ProxyServerModel proxyInfoBean) {
		this.proxyInfoBean = proxyInfoBean;
		this.writeMemoryToFile();
	}

	public void deleteServicePlan(ServicePlan servicePlan) {
		if (servicePlan != null) {
			this.servicePlanStore.remove(servicePlan.getId());
			this.writeMemoryToFile();
		}
	}

	public void updateServicePlansWithNewServiceName(String oldServiceName, String newServiceName) {

		for (ServicePlan servicePlan : this.getServicePlans()) {
			for (PlanItem planItem : servicePlan.getPlanItemList()) {
				if (planItem.getServiceName() != null && planItem.getServiceName().equals(oldServiceName)) {
					planItem.setServiceName(newServiceName);
					// 'Add' will 'update' too.
					servicePlan.addPlanItem(planItem);

				}
			}

		}

		this.writeMemoryToFile();
	}

	public void updateServicePlansWithNewScenarioName(Long serviceId, String oldScenarioName, String newScenarioName) {

		Service service = this.getServiceById(serviceId);
		if (service != null) {

			for (ServicePlan servicePlan : this.getServicePlans()) {
				for (PlanItem planItem : servicePlan.getPlanItemList()) {
					if (planItem.getServiceName() != null && planItem.getServiceName().equals(service.getServiceName())
							&& planItem.getScenarioName().equals(oldScenarioName)) {
						planItem.setScenarioName(newScenarioName);
						// 'Add' will 'update' too.
						servicePlan.addPlanItem(planItem);
					}
				}
			}

		}
		this.writeMemoryToFile();
	}

	public ServicePlan getServicePlanById(Long servicePlanId) {
		return servicePlanStore.get(servicePlanId);
	}

	public ServicePlan getServicePlanByName(String servicePlanName) {
		ServicePlan sp = null;
		for (ServicePlan servicePlan : this.getServicePlans()) {
			if (servicePlan.getName() != null && servicePlan.getName().equalsIgnoreCase(servicePlanName)) {
				sp = servicePlan;
				break;
			}
		}
		return sp;
	}

	public List<ServicePlan> getServicePlans() {
		return this.servicePlanStore.getOrderedList();
	}

	public ServicePlan saveOrUpdateServicePlan(ServicePlan servicePlan) {
		PersistableItem item = this.servicePlanStore.save(servicePlan);
		if (servicePlan != null && !servicePlan.getTransientState()) {
			this.writeMemoryToFile();
		}
		return (ServicePlan) item;

	}

	public ScenarioRef getUniversalErrorScenarioRef() {
		ScenarioRef scenarioRef = null; // new ScenarioRef();
		if (this.universalErrorScenarioId != null && this.universalErrorServiceId != null) {
			scenarioRef = new ScenarioRef(this.universalErrorScenarioId, this.universalErrorServiceId);
		}
		return scenarioRef;
	}

	public Scenario getUniversalErrorScenario() {
		Scenario error = null;
		Service service = getServiceById(this.universalErrorServiceId);
		if (service != null) {
			error = service.getScenario(this.universalErrorScenarioId);
		}
		return error;
	}

	public void setUniversalErrorScenarioRef(ScenarioRef scenarioRef) {

		if (scenarioRef != null) {
			this.universalErrorServiceId = scenarioRef.getServiceId();
			this.universalErrorScenarioId = scenarioRef.getId();
			this.writeMemoryToFile();
		} else {
			this.universalErrorServiceId = null;
			this.universalErrorScenarioId = null;
			this.writeMemoryToFile();
		}
	}

	/**
	 * Convenience Method for the XML writers...
	 */
	public void setUniversalErrorScenarioId(String id) {
		try {

			this.universalErrorScenarioId = new Long(id);
		} catch (Exception e) {
			// By design, ignore.
		}
	}

	/**
	 * Convenience Method for the XML writers...
	 */
	public void setUniversalErrorServiceId(String id) {
		try {

			this.universalErrorServiceId = new Long(id);
		} catch (Exception e) {
			// By design, ignore.
		}
	}

	public void deleteEverything() {
        initHistoryStore();
		mockServiceStore = new OrderedMap<Service>();
		servicePlanStore = new OrderedMap<ServicePlan>();
		twistInfoStore = new OrderedMap<TwistInfo>();
		this.proxyInfoBean = new ProxyServerModel();
		this.globalFilterTag = "";
		this.universalErrorServiceId = null;
		this.universalErrorScenarioId = null;
		this.writeMemoryToFile();
	}

	public List<String> uniqueClientIPs() {
		List<String> uniqueIPs = new ArrayList<String>();
		for (FulfilledClientRequest tx : this.historyStore.getOrderedList()) {
			String tmpIP = tx.getRequestorIP();
			if (!uniqueIPs.contains(tmpIP)) {
				uniqueIPs.add(tmpIP);
			}
		}
		return uniqueIPs;
	}

	public List<String> uniqueClientIPsForService(Long serviceId) {

		logger.debug("getting IPs for serviceId: " + serviceId + ". there are a total of " + this.historyStore.size()
				+ " requests currently stored.");

		List<String> uniqueIPs = new ArrayList<String>();
		for (FulfilledClientRequest tx : this.historyStore.getOrderedList()) {
			String ip = tx.getRequestorIP();
			if (!uniqueIPs.contains(ip) && tx.getServiceId().equals(serviceId)) {
				uniqueIPs.add(ip);
			}
		}
		return uniqueIPs;
	}

	public FulfilledClientRequest getFulfilledClientRequestsById(Long fulfilledClientRequestId) {

		return this.historyStore.get(fulfilledClientRequestId);

	}

	public List<FulfilledClientRequest> getFulfilledClientRequestsForService(Long serviceId) {
		logger.debug("getting requests for serviceId: " + serviceId + ". there are a total of "
				+ this.historyStore.size() + " requests currently stored.");
		List<FulfilledClientRequest> rv = new ArrayList<FulfilledClientRequest>();
		for (FulfilledClientRequest req : this.historyStore.getOrderedList()) {
			if (req.getServiceId().equals(serviceId)) {
				rv.add(req);
			}
		}
		return rv;
	}

	public List<FulfilledClientRequest> getFulfilledClientRequestsFromIP(String ip) {
		List<FulfilledClientRequest> rv = new ArrayList<FulfilledClientRequest>();
		for (FulfilledClientRequest req : this.historyStore.getOrderedList()) {
			if (req.getRequestorIP().equals(ip)) {
				rv.add(req);
			}
		}
		return rv;
	}

	public List<FulfilledClientRequest> getFulfilledClientRequestsFromIPForService(String ip, Long serviceId) {
		List<FulfilledClientRequest> rv = new ArrayList<FulfilledClientRequest>();
		for (FulfilledClientRequest req : this.historyStore.getOrderedList()) {
			if (req.getServiceId().equals(serviceId) && req.getRequestorIP().equals(ip)) {
				rv.add(req);
			}
		}
		return rv;
	}

	public void deleteFulfilledClientRequests() {
        initHistoryStore();
	}

    private void initHistoryStore() {
        historyStore = new OrderedMap<FulfilledClientRequest>();
        historyStore.setMaxSize(new Integer(25));
    }

    public void deleteFulfilledClientRequestsFromIPForService(String ip, Long serviceId) {
		for (FulfilledClientRequest req : historyStore.getOrderedList()) {
			if (req.getServiceId().equals(serviceId) && req.getRequestorIP().equals(ip)) {
				this.historyStore.remove(req.getId());
			}
		}

	}

	public void deleteFulfilledClientRequestById(Long fulfilledRequestID) {
		for (FulfilledClientRequest req : historyStore.getOrderedList()) {
			if (req.getId().equals(fulfilledRequestID)) {
				this.historyStore.remove(req.getId());
			}
		}

	}

	/**
	 * Filters list with AND not OR. If string starts with "!", we consider it
	 * NOT.
	 */
	public List<FulfilledClientRequest> getFulfilledClientRequest(Collection<String> filterArguments) {

		List<FulfilledClientRequest> rv = new ArrayList<FulfilledClientRequest>();
		if (filterArguments.size() == 0) {
			rv = this.getFulfilledClientRequests();
		} else {

			for (FulfilledClientRequest req : this.historyStore.getOrderedList()) {
				boolean allFilterTokensPresentInReq = true;
				for (String filterArg : filterArguments) {
					boolean notValue = filterArg.startsWith("!");

					boolean tokenFound = hasToken(req, filterArg);
					if (notValue && tokenFound) {
						allFilterTokensPresentInReq = false;
						break;
					} else if (!tokenFound && !notValue) {
						allFilterTokensPresentInReq = false;
						break;
					} else if (!tokenFound && notValue) {
						allFilterTokensPresentInReq = true;
					}

				}
				if (allFilterTokensPresentInReq) {
					rv.add(req);
				}

			}
		}
		return rv;
	}

	/**
	 * Filters list with AND not OR. If string starts with "!", we consider it
	 * NOT.
	 */
	private boolean hasToken(FulfilledClientRequest req, String filterArg) {

		boolean notValue = filterArg.startsWith("!");
		if (notValue) {
			try {
				// get the value
				filterArg = filterArg.substring(1);
			} catch (Exception e) {
				// do nothing. exception may occur
				// with out of index
			}
		}
		boolean tokenFound = false;
		if (req.getServiceId().toString().indexOf(filterArg) > -1) {
			tokenFound = true;

		} else if (req.getClientRequestBody().indexOf(filterArg) > -1) {
			tokenFound = true;

		} else if (req.getClientRequestHeaders().indexOf(filterArg) > -1) {
			tokenFound = true;

		} else if (req.getClientRequestParameters().indexOf(filterArg) > -1) {
			tokenFound = true;

		} else if (req.getRequestorIP().indexOf(filterArg) > -1) {
			tokenFound = true;

		} else if (req.getRawRequest().indexOf(filterArg) > -1) {
			tokenFound = true;

		} else if (req.getResponseMessage().getBody().indexOf(filterArg) > -1) {
			tokenFound = true;

		} else if (req.getResponseMessage().getHeaderInfo().indexOf(filterArg) > -1) {
			tokenFound = true;

		} else if (req.getServiceName().indexOf(filterArg) > -1) {
			tokenFound = true;

		} else {
			Header[] headers = req.getResponseMessage().getHeaders();
			if (headers != null) {
				for (Header header : headers) {
					if (header.getName().indexOf(filterArg) > -1) {
						tokenFound = true;
						break;
					} else if (header.getValue().indexOf(filterArg) > -1) {
						tokenFound = true;
						break;

					}
				}
			}
		}
		return tokenFound;

	}

	/**
	 * Every time something gets saved, we write to memory.
	 */
	private synchronized void writeMemoryToFile() {

		if (!transientState) {
			MockeyXmlFactory g = new MockeyXmlFactory();
			g.writeStoreToXML(store, MockeyXmlFileManager.MOCK_SERVICE_DEFINITION);
		}

	}

	public List<TwistInfo> getTwistInfoList() {
		return this.twistInfoStore.getOrderedList();
	}

	public TwistInfo getTwistInfoById(Long id) {
		return (TwistInfo) this.twistInfoStore.get(id);
	}

	public TwistInfo getTwistInfoByName(String name) {
		TwistInfo item = null;
		for (TwistInfo i : getTwistInfoList()) {
			if (i.getName() != null && i.getName().equals(name)) {
				item = i;
				break;
			}
		}
		return item;
	}

	public TwistInfo saveOrUpdateTwistInfo(TwistInfo twistInfo) {
		PersistableItem item = twistInfoStore.save(twistInfo);
		this.writeMemoryToFile();
		return (TwistInfo) item;
	}

	public void deleteTwistInfo(TwistInfo twistInfo) {
		if (twistInfo != null) {
			this.twistInfoStore.remove(twistInfo.getId());
			this.writeMemoryToFile();
		}
	}

	public Long getUniversalTwistInfoId() {
		return this.univeralTwistInfoId;
	}

	public void setUniversalTwistInfoId(Long twistInfoId) {
		this.univeralTwistInfoId = twistInfoId;
		this.writeMemoryToFile();
	}

	public List<ServiceRef> getServiceRefs() {
		return this.serviceRefStore.getOrderedList();
	}

	public ServiceRef saveOrUpdateServiceRef(ServiceRef serviceRef) {
		PersistableItem item = this.serviceRefStore.save(serviceRef);
		this.writeMemoryToFile();
		return (ServiceRef) item;
	}

	public Boolean getReadOnlyMode() {
		if (this.transientState == null) {
			this.transientState = new Boolean(true);
		}
		return this.transientState;
	}

	public void deleteTagFromStore(String tag) {

		if (tag != null && tag.trim().length() > 0) {
			for (Service service : mockServiceStore.getOrderedList()) {
				service.removeTagFromList(tag);
				for (Scenario scenario : service.getScenarios()) {
					scenario.removeTagFromList(tag);
					if (tag.trim().toLowerCase().equals(scenario.getLastVisitSimple())) {
						scenario.setLastVisit(null);
					}
				}

				if (tag.trim().toLowerCase().equals(service.getLastVisitSimple())) {
					service.setLastVisit(null);
				}
			}
			for (ServicePlan servicePlan : servicePlanStore.getOrderedList()) {
				servicePlan.removeTagFromList(tag);

				if (tag.trim().toLowerCase().equals(servicePlan.getLastVisitSimple())) {
					servicePlan.setLastVisit(null);
				}
			}
			this.writeMemoryToFile();
		}

	}

	public String getGlobalStateSystemFilterTag() {
		if (this.globalFilterTag == null) {
			this.globalFilterTag = "";
		} else {
			this.globalFilterTag = this.globalFilterTag.toLowerCase().trim();
		}
		return this.globalFilterTag;
	}

	public void setGlobalStateSystemFilterTag(String filterTag) {
		if (filterTag == null) {
			this.globalFilterTag = "";
		} else {
			this.globalFilterTag = filterTag.toLowerCase().trim();
		}
	}

	@Override
	public void setServicePlan(ServicePlan servicePlan) {

		if (servicePlan != null) {

			for (PlanItem planItem : servicePlan.getPlanItemList()) {

				Service service = this.getServiceByName(planItem.getServiceName());

				if (service != null) {
					service.setHangTime(planItem.getHangTime());
					service.setDefaultScenarioByName(planItem.getScenarioName());
					service.setServiceResponseType(planItem.getServiceResponseType());
					this.saveOrUpdateService(service);
				}
			}
			// Why do we save the Plan here?
			// To save the lastVisit time
			servicePlan.setLastVisit(new Long(Calendar.getInstance().getTimeInMillis()));
			this.saveOrUpdateServicePlan(servicePlan);
		}
	}

}
