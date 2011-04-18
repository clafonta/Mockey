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
package com.mockey.storage.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.model.ServiceRef;
import com.mockey.model.TwistInfo;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.ui.ServiceMergeResults;

/**
 * Consumes an XML file and configures Mockey services.
 * 
 * @author Chad.Lafontaine
 * 
 */
public class MockeyXmlFileManager {

	private static final long serialVersionUID = 2874257060865115637L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
    public static final String MOCK_SERVICE_DEFINITION = "mock_service_definitions.xml";
    protected static final String MOCK_SERVICE_FOLDER = "mockey_def_depot";
	private static Logger logger = Logger.getLogger(MockeyXmlFileManager.class);
	private static final String FILESEPERATOR = System.getProperty("file.separator");

	/**
	 *  
	 * 
	 */
	public MockeyXmlFileManager() {
		File fileDepot = new File(MOCK_SERVICE_FOLDER);
		if(!fileDepot.exists()){
			boolean success = fileDepot.mkdir();
			if(!success){
				logger.fatal("Unable to create a folder called " + MOCK_SERVICE_FOLDER);
			}
		}
	}
	/**
	 * 
	 * @param file
	 *            - xml configuration file for Mockey
	 * @throws IOException
	 * @throws SAXException
	 * @throws SAXParseException
	 */
	private String getFileContentAsString(File file) throws IOException, SAXParseException, SAXException {
		
		FileInputStream fstream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Charset.forName(HTTP.UTF_8)));
        StringBuffer inputString = new StringBuffer();
        // Read File Line By Line
        String strLine = null;
        while ((strLine = br.readLine()) != null) {
            // Print the content on the console
            inputString.append(new String(strLine.getBytes(HTTP.UTF_8)));
        }
        return inputString.toString();

	}

	
	public ServiceMergeResults loadConfiguration() throws SAXParseException, IOException, SAXException {
		File n = new File(MOCK_SERVICE_DEFINITION);
		logger.debug("Loading configuration from " + MOCK_SERVICE_DEFINITION);
		return loadConfigurationWithXmlDef(getFileContentAsString(n));
	}
	/**
	 * 
	 * @param data
	 * @return results (conflicts and additions).
	 * @throws IOException
	 * @throws SAXException
	 * @throws SAXParseException
	 */
	public ServiceMergeResults loadConfigurationWithXmlDef(String strXMLDefintion) throws IOException, SAXParseException, SAXException {
		ServiceMergeResults mergeResults = new ServiceMergeResults();

		MockeyXmlFileConfigurationReader msfr = new MockeyXmlFileConfigurationReader();
		IMockeyStorage mockServiceStoreTemporary = msfr.readDefinition(strXMLDefintion);
		// PROXY SETTINGs
		store.setProxy(mockServiceStoreTemporary.getProxy());

		// UNIVERSAL RESPONSE SETTINGS
		if (store.getUniversalErrorScenario() != null && mockServiceStoreTemporary.getUniversalErrorScenario() != null) {
			mergeResults.addConflictMsg("Universal error message already defined with name '"
					+ store.getUniversalErrorScenario().getScenarioName() + "'");
		} else if (store.getUniversalErrorScenario() == null
				&& mockServiceStoreTemporary.getUniversalErrorScenario() != null) {

			store.setUniversalErrorScenarioId(mockServiceStoreTemporary.getUniversalErrorScenario().getId());
			store.setUniversalErrorServiceId(mockServiceStoreTemporary.getUniversalErrorScenario().getServiceId());
			mergeResults.addAdditionMsg("Universal error response defined.");

		}

		// Service References
		List<Service> serviceListFromRefs = new ArrayList<Service>();
		for (ServiceRef serviceRef : mockServiceStoreTemporary.getServiceRefs()) {
			String mockServiceDefinition = getFileContentAsString(new File(serviceRef.getFileName()));
			List<Service> tmpList = msfr.readServiceDefinition(mockServiceDefinition);
			for(Service tmpService : tmpList){
				serviceListFromRefs.add(tmpService);
			}
			
		}
		addServicesToStore(mergeResults, serviceListFromRefs);
		// Service
		mergeResults = addServicesToStore(mergeResults, mockServiceStoreTemporary.getServices());

		// PLANS
		for (ServicePlan servicePlan : mockServiceStoreTemporary.getServicePlans()) {
			store.saveOrUpdateServicePlan(servicePlan);
		}

		// TWIST CONFIGURATION
		for (TwistInfo twistInfo : mockServiceStoreTemporary.getTwistInfoList()) {
			store.saveOrUpdateTwistInfo(twistInfo);
		}

		return mergeResults;
	}

	private ServiceMergeResults addServicesToStore(ServiceMergeResults mergeResults, List<Service> serviceList) {
		// When loading a definition file, by default, we should
		// compare the uploaded Service list mock URL to what's currently
		// in memory.
		//
		// 1) MATCHING MOCK URL
		// If there is an existing/matching mockURL, then this isn't
		// a new service and we DON'T want to overwrite. But, we
		// want new Scenarios if they exist. A new scenario is based
		// on
		//
		// 2) NO MATCHING MOCK URL
		// If there is no matching service URL, then create a new
		// service and associated scenarios.
		for (Service uploadedServiceBean : serviceList) {
			List<Service> serviceBeansInMemory = store.getServices();
			Iterator<Service> iter3 = serviceBeansInMemory.iterator();
			boolean existingServiceWithMatchingMockUrl = false;
			Service inMemoryServiceBean = null;
			while (iter3.hasNext()) {
				inMemoryServiceBean = (Service) iter3.next();
				Url firstMatchingUrl = inMemoryServiceBean.getFirstMatchingRealServiceUrl(uploadedServiceBean);
				if (firstMatchingUrl != null) {
					existingServiceWithMatchingMockUrl = true;
					mergeResults.addConflictMsg("Service '" + uploadedServiceBean.getServiceName()
							+ "' not created; will try to merge into existing service labeled '"
							+ inMemoryServiceBean.getServiceName() + "' ");
					break;
				}
			}
			if (!existingServiceWithMatchingMockUrl) {
				// We null it, to not stomp on any services
				uploadedServiceBean.setId(null);
				store.saveOrUpdateService(uploadedServiceBean);
				mergeResults.addAdditionMsg("Service '" + uploadedServiceBean.getServiceName() + "' created. ");

			} else {
				// Just merge scenarios per matching services
				mergeResults = mergeServices(uploadedServiceBean, inMemoryServiceBean, mergeResults);
			}

		}
		return mergeResults;
	}

	/**
	 * 
	 * @param uploadedServiceBean
	 * @param inMemoryServiceBean
	 * @param readResults
	 * @return
	 */
	public ServiceMergeResults mergeServices(Service uploadedServiceBean, Service inMemoryServiceBean,
			ServiceMergeResults readResults) {
		if (uploadedServiceBean != null && inMemoryServiceBean != null) {
			// Merge Scenarios
			if (readResults == null) {
				readResults = new ServiceMergeResults();
			}
			Iterator<Scenario> uIter = uploadedServiceBean.getScenarios().iterator();
			Iterator<Scenario> mIter = inMemoryServiceBean.getScenarios().iterator();
			while (uIter.hasNext()) {
				Scenario uploadedScenario = (Scenario) uIter.next();
				boolean existingScenario = false;
				Scenario mBean = null;
				while (mIter.hasNext()) {
					mBean = (Scenario) mIter.next();
					if (mBean.equals(uploadedScenario)) {
						existingScenario = true;
						break;
					}
				}
				if (!existingScenario) {
					uploadedScenario.setServiceId(inMemoryServiceBean.getId());
					inMemoryServiceBean.saveOrUpdateScenario(uploadedScenario);
					store.saveOrUpdateService(inMemoryServiceBean);
					readResults.addAdditionMsg("Scenario '" + uploadedScenario.getScenarioName()
							+ "' added to service '" + inMemoryServiceBean.getServiceName() + "' ");
				} else {
					readResults
							.addConflictMsg("Scenario '" + mBean.getScenarioName()
									+ "' not added, already defined in service '"
									+ inMemoryServiceBean.getServiceName() + "' ");
				}

			}
			// Merge URLs
			try {
				for (Url url : uploadedServiceBean.getRealServiceUrls()) {
					if (inMemoryServiceBean.hasRealServiceUrl(url)) {
						readResults.addConflictMsg("Real url already defined: " + url.getFullUrl());
					} else {
						readResults.addAdditionMsg("Added real URL: " + url.getFullUrl());
						inMemoryServiceBean.saveOrUpdateRealServiceUrl(url);
					}
				}
			} catch (Exception e) {

			}
		}
		return readResults;
	}
	
	protected static String getServiceFileNameOutputString(Service s) {
		String result = null;
		String arg = s.getServiceName();
		if (arg != null) {
			result = arg.trim();
		} else {
			result = "";
		}
		if (result.length() == 0) {
			result = "autogenerated_name";
		}
		return MockeyXmlFileManager.MOCK_SERVICE_FOLDER+ FILESEPERATOR+ result+".xml";
	}

}
