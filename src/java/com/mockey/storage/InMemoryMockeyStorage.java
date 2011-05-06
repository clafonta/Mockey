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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.log4j.Logger;

import com.mockey.OrderedMap;
import com.mockey.model.FulfilledClientRequest;
import com.mockey.model.PersistableItem;
import com.mockey.model.ProxyServerModel;
import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.model.ServiceRef;
import com.mockey.model.TwistInfo;
import com.mockey.model.Url;
import com.mockey.storage.xml.MockeyXmlFileManager;
import com.mockey.storage.xml.MockeyXmlFactory;

/**
 * In memory implementation to the storage of mock services and scenarios.
 * 
 * @author chad.lafontaine
 */
public class InMemoryMockeyStorage implements IMockeyStorage {

	private OrderedMap<FulfilledClientRequest> historyStore = new OrderedMap<FulfilledClientRequest>();
	private OrderedMap<Service> mockServiceStore = new OrderedMap<Service>();
	private OrderedMap<ServiceRef> serviceRefStore = new OrderedMap<ServiceRef>();
	private OrderedMap<ServicePlan> servicePlanStore = new OrderedMap<ServicePlan>();

	private OrderedMap<TwistInfo> twistInfoStore = new OrderedMap<TwistInfo>();

	private static Logger logger = Logger.getLogger(InMemoryMockeyStorage.class);
	private ProxyServerModel proxyInfoBean = new ProxyServerModel();

	private Long univeralTwistInfoId = null;
	private Long univeralErrorServiceId = null;
	private Long univeralErrorScenarioId = null;
	private static InMemoryMockeyStorage store = new InMemoryMockeyStorage();
	// Yes, by default, we need this as TRUE.
	private Boolean transientState = new Boolean(true);

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
		if(transientState!=null) {
			this.transientState = transientState;
		}
		
		if(!transientState){
			this.writeMemoryToFile();
		}
		
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
		this.historyStore.setMaxSize(new Integer(25)); // Careful, more than ~45
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

	public Service getServiceByUrl(String url) {

		try {
			for (Service service : getServices()) {
				// 1. Check for matching name.

				Url serviceMockUrl = new Url(service.getUrl());
				// logger.debug("Comparing: " + url.trim()+
				// " == "+serviceMockUrl.getFullUrl().trim() );
				if (url.trim().equalsIgnoreCase(serviceMockUrl.getFullUrl().trim())) {

					return service;
				}
				// 2. If no Mock url match, then check real URLs.
				Iterator<Url> altUrlIter = service.getRealServiceUrls().iterator();
				while (altUrlIter.hasNext()) {
					Url altUrl = altUrlIter.next();
					// logger.debug("Comparing: " + url.trim()+
					// " == "+serviceMockUrl.getFullUrl().trim() );

					if (url.trim().equalsIgnoreCase(altUrl.getFullUrl().trim())) {
						return service;
					}
				}

			}
		} catch (Exception e) {
			logger.error("Unable to retrieve service w/ url pattern: " + url, e);
		}

		logger.debug("Didn't find service with Service path: " + url + ".  Creating a new one.");

		Service service = new Service();

		Url newUrl = null;

		try {
			newUrl = new Url(Url.getSchemeHostPortPathFromURL(url));
			service.setUrl(newUrl.getFullUrl());
			service.saveOrUpdateRealServiceUrl(newUrl);
			store.saveOrUpdateService(service);

		} catch (MalformedURLException e) {
			logger.error("Unable to build a Service with URL '" + url + "'", e);
		}

		return service;
	}

	public Service saveOrUpdateService(Service mockServiceBean) {
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

	public Scenario getUniversalErrorScenario() {
		Scenario error = null;
		Service service = getServiceById(this.univeralErrorServiceId);
		if (service != null) {
			error = service.getScenario(this.univeralErrorScenarioId);
		}
		return error;
	}

	public void setUniversalErrorScenarioId(Long scenarioId) {
		this.univeralErrorScenarioId = scenarioId;
		this.writeMemoryToFile();
	}

	public void setUniversalErrorServiceId(Long serviceId) {
		this.univeralErrorServiceId = serviceId;
		this.writeMemoryToFile();
	}

	public void deleteEverything() {
		historyStore = new OrderedMap<FulfilledClientRequest>();
		mockServiceStore = new OrderedMap<Service>();
		servicePlanStore = new OrderedMap<ServicePlan>();
		twistInfoStore = new OrderedMap<TwistInfo>();
		this.univeralErrorServiceId = null;
		this.univeralErrorScenarioId = null;
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
		historyStore = new OrderedMap<FulfilledClientRequest>();

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

	public Long getUniversalErrorScenarioId() {

		return this.univeralErrorScenarioId;
	}

	public Long getUniversalErrorServiceId() {

		return this.univeralErrorServiceId;
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
		if(this.transientState == null){
			this.transientState = new Boolean(true);
		}
		return this.transientState;
	}

}
