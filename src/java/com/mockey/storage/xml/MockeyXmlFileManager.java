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
import java.io.FileNotFoundException;
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

import com.mockey.model.ProxyServerModel;
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

	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	public static final String MOCK_SERVICE_DEFINITION = "mock_service_definitions.xml";
	protected static final String MOCK_SERVICE_FOLDER = "mockey_def_depot";
	private static Logger logger = Logger.getLogger(MockeyXmlFileManager.class);
	private static final String FILESEPERATOR = System
			.getProperty("file.separator");

	/**
	 * Basic constructor. Will create a folder on the file system to store XML
	 * definitions.
	 * 
	 */
	public MockeyXmlFileManager() {
		File fileDepot = new File(MOCK_SERVICE_FOLDER);
		if (!fileDepot.exists()) {
			boolean success = fileDepot.mkdir();
			if (!success) {
				logger.fatal("Unable to create a folder called "
						+ MOCK_SERVICE_FOLDER);
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
	private String getFileContentAsString(File file) throws IOException,
			SAXParseException, SAXException {

		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream,
				Charset.forName(HTTP.UTF_8)));
		StringBuffer inputString = new StringBuffer();
		// Read File Line By Line
		String strLine = null;
		while ((strLine = br.readLine()) != null) {
			// Print the content on the console
			inputString.append(new String(strLine.getBytes(HTTP.UTF_8)));
		}
		return inputString.toString();

	}

	/**
	 * Loads from default file definition file.
	 * 
	 * @return results of loading configuration, includes additions and possible
	 *         conflicts.
	 * 
	 * @throws SAXParseException
	 * @throws IOException
	 */
	public ServiceMergeResults loadConfiguration() throws SAXParseException,
			IOException {
		File n = new File(MOCK_SERVICE_DEFINITION);
		logger.debug("Loading configuration from " + MOCK_SERVICE_DEFINITION);

		try {
			return loadConfigurationWithXmlDef(getFileContentAsString(n), null);
		} catch (SAXException e) {
			logger.error("Ouch, unable to parse" + n.getAbsolutePath(), e);
		}
		return new ServiceMergeResults();
	}

	/**
	 * 
	 * @param strXMLDefintion
	 * @param tagArguments
	 * @return results (conflicts and additions).
	 * @throws IOException
	 * @throws SAXParseException
	 * @throws SAXException
	 */
	public ServiceMergeResults loadConfigurationWithXmlDef(
			String strXMLDefintion, String tagArguments) throws IOException,
			SAXParseException, SAXException {
		ServiceMergeResults mergeResults = new ServiceMergeResults();

		// ***** REMEMBER *****
		// Every time a saveOrUpdateXXXX is made, the entire STORE is written to
		// the file system.
		// If the STORE has many definitions, then each SAVE will loop over
		// every file and write.
		//
		// NOT GOOD FOR PERFORMANCE
		//
		// Solution: put the store in a temporary transient state
		// (memory-mode-only), then revert to original transient setting,
		// which could have been in memory-only or write-to-file in the
		// first place.
		//
		// *********************
		Boolean originalTransientState = store.getReadOnlyMode();
		store.setReadOnlyMode(true);

		// STEP #1. CREATE A TEMP STORE
		// Read the incoming XML file, and create a new/temporary store.
		//
		MockeyXmlFileConfigurationReader msfr = new MockeyXmlFileConfigurationReader();
		IMockeyStorage mockServiceStoreTemporary = msfr
				.readDefinition(strXMLDefintion);

		// STEP #2. PROXY SETTINGS
		// If the proxy settings are _empty_, then set the incoming
		// proxy settings. Otherwise, call out a merge conflict.
		//
		ProxyServerModel proxyServerModel = store.getProxy();
		if (proxyServerModel.hasSettings()) {
			mergeResults
					.addConflictMsg("Proxy settings NOT set from incoming file.");
		} else {
			store.setProxy(mockServiceStoreTemporary.getProxy());
			mergeResults.addAdditionMsg("Proxy settings set.");
		}

		// STEP #3. UNIVERSAL RESPONSE SETTINGS
		if (store.getUniversalErrorScenario() != null
				&& mockServiceStoreTemporary.getUniversalErrorScenario() != null) {
			mergeResults
					.addConflictMsg("Universal error message already defined with name '"
							+ store.getUniversalErrorScenario()
									.getScenarioName() + "'");
		} else if (store.getUniversalErrorScenario() == null
				&& mockServiceStoreTemporary.getUniversalErrorScenario() != null) {

			store.setUniversalErrorScenarioId(mockServiceStoreTemporary
					.getUniversalErrorScenario().getId());
			store.setUniversalErrorServiceId(mockServiceStoreTemporary
					.getUniversalErrorScenario().getServiceId());
			mergeResults.addAdditionMsg("Universal error response defined.");

		}

		// STEP #4. BUILD SERVICE REFERENCES
		// *** I totally forget why I do this. ***
		// Come on! Comments needed. Why references?
		// Can we not use them?
		List<Service> serviceListFromRefs = new ArrayList<Service>();
		for (ServiceRef serviceRef : mockServiceStoreTemporary.getServiceRefs()) {
			try {
				String mockServiceDefinition = getFileContentAsString(new File(
						serviceRef.getFileName()));

				List<Service> tmpList = msfr
						.readServiceDefinition(mockServiceDefinition);
				for (Service tmpService : tmpList) {
					serviceListFromRefs.add(tmpService);
				}
			} catch (SAXParseException spe) {
				logger.error(
						"Unable to parse file of name "
								+ serviceRef.getFileName(), spe);
				mergeResults.addConflictMsg("File not parseable: "
						+ serviceRef.getFileName());

			} catch (FileNotFoundException fnf) {
				logger.error("File not found: " + serviceRef.getFileName());
				mergeResults.addConflictMsg("File not found: "
						+ serviceRef.getFileName());
			}

		}
		addServicesToStore(mergeResults, serviceListFromRefs, tagArguments);

		// STEP #5. MERGE SERVICES AND SCENARIOS
		// Since this gets complicated, logic was moved to it's own method.
		mergeResults = addServicesToStore(mergeResults,
				mockServiceStoreTemporary.getServices(), tagArguments);

		// STEP #6. MERGE SERVICE PLANS
		for (ServicePlan servicePlan : mockServiceStoreTemporary
				.getServicePlans()) {
			if (tagArguments != null) {
				servicePlan.addTagToList(tagArguments);
			}
			store.saveOrUpdateServicePlan(servicePlan);
		}

		// TWIST CONFIGURATION
		for (TwistInfo twistInfo : mockServiceStoreTemporary.getTwistInfoList()) {
			store.saveOrUpdateTwistInfo(twistInfo);
		}

		// Don't forget to set state back to original state.
		// NOTE: if transient state (read only) is false, then this method will
		// write to STORE to the file system.
		// Yeah!
		// *********************
		store.setReadOnlyMode(originalTransientState);
		// *********************

		return mergeResults;
	}

	// Let's Merge!
	private ServiceMergeResults addServicesToStore(
			ServiceMergeResults mergeResults, List<Service> serviceListToAdd,
			String tagArguments) {
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
		// If there is no matching service URL, then we want to create a new
		// service and associated scenarios. But here's an odd case. What if
		// we are merging two same-name Services, each with empty matching URL
		// lists?
		//

		for (Service uploadedServiceBean : serviceListToAdd) {
			List<Service> serviceBeansInMemory = store.getServices();
			Iterator<Service> inMemoryServiceIter = serviceBeansInMemory
					.iterator();

			boolean existingService = false;
			Service inMemoryServiceBean = null;

			while (inMemoryServiceIter.hasNext()) {
				inMemoryServiceBean = (Service) inMemoryServiceIter.next();

				// Same name?
				if (uploadedServiceBean
						.getServiceName()
						.trim()
						.toLowerCase()
						.equals(inMemoryServiceBean.getServiceName().trim()
								.toLowerCase())) {
					existingService = true;
					mergeResults
							.addConflictMsg("Service '"
									+ uploadedServiceBean.getServiceName()
									+ "' not created; will try to merge into existing service labeled '"
									+ inMemoryServiceBean.getServiceName()
									+ "' ");

				} else {
					// First, check if both have empty Real service list
					Url firstMatchingUrl = inMemoryServiceBean
							.getFirstMatchingRealServiceUrl(uploadedServiceBean);
					if (firstMatchingUrl != null) {
						existingService = true;
						mergeResults
								.addConflictMsg("Service '"
										+ uploadedServiceBean.getServiceName()
										+ "' not created; will try to merge into existing service labeled '"
										+ inMemoryServiceBean.getServiceName()
										+ "' ");
						break;
					}
				}
			}
			if (!existingService) {
				// YES, no in-store matching URL. WARNING: This is a new
				// service, but it's NAME may be the same as another existing
				// service. First, we null ID, to not write-over on any in-store
				// services with same ID
				uploadedServiceBean.setId(null);

				// #TAG HANDLING - BEGIN
				// Ensure Service, and all it's child scenarios have
				// incoming/uploaded tag arguments
				uploadedServiceBean.addTagToList(tagArguments);
				for (Scenario scenarioTmp : uploadedServiceBean.getScenarios()) {
					scenarioTmp.setTag(tagArguments);
				}
				// #TAG HANDLING - END

				// Save to the IN-MEMORY STORE
				store.saveOrUpdateService(uploadedServiceBean);
				mergeResults.addAdditionMsg("Service '"
						+ uploadedServiceBean.getServiceName() + "' created. ");

			} else {
				// Just merge scenarios per matching services
				mergeResults = mergeServices(uploadedServiceBean,
						inMemoryServiceBean, mergeResults, tagArguments);
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
	public ServiceMergeResults mergeServices(Service uploadedServiceBean,
			Service inMemoryServiceBean, ServiceMergeResults readResults,
			String tagArguments) {

		Boolean originalMode = store.getReadOnlyMode();
		store.setReadOnlyMode(true);

		if (uploadedServiceBean != null && inMemoryServiceBean != null) {

			// #TAG HANDLING for the Service - BEGIN
			// Ensure Service gets incoming/uploaded tag arguments
			inMemoryServiceBean.addTagToList(tagArguments);
			// #TAG HANDLING for the Service - END

			if (readResults == null) {
				readResults = new ServiceMergeResults();
			}
			Iterator<Scenario> uploadedServiceScenarioListIter = uploadedServiceBean
					.getScenarios().iterator();
			Iterator<Scenario> mIter = inMemoryServiceBean.getScenarios()
					.iterator();
			while (uploadedServiceScenarioListIter.hasNext()) {
				Scenario uploadedScenario = (Scenario) uploadedServiceScenarioListIter
						.next();
				boolean existingScenarioBool = false;
				Scenario inMemoryScenarioBean = null;
				while (mIter.hasNext()) {
					inMemoryScenarioBean = (Scenario) mIter.next();
					if (inMemoryScenarioBean.equals(uploadedScenario)) {

						existingScenarioBool = true;
						break;
					}
				}
				if (!existingScenarioBool) {
					// Hey, we have a new scenario.
					uploadedScenario.setServiceId(inMemoryServiceBean.getId());
					// Tag for Service:Scenario
					uploadedScenario.addTagToList(tagArguments);
					inMemoryServiceBean.saveOrUpdateScenario(uploadedScenario);

					store.saveOrUpdateService(inMemoryServiceBean);
					readResults.addAdditionMsg("Scenario '"
							+ uploadedScenario.getScenarioName()
							+ "' added to service '"
							+ inMemoryServiceBean.getServiceName() + "' ");
				} else {
					// SAVE TAGS
					inMemoryScenarioBean.addTagToList(tagArguments);
					inMemoryServiceBean
							.saveOrUpdateScenario(inMemoryScenarioBean);
					store.saveOrUpdateService(inMemoryServiceBean);
					// Although we still need to
					readResults.addConflictMsg("Scenario '"
							+ inMemoryScenarioBean.getScenarioName()
							+ "' not added, already defined in service '"
							+ inMemoryServiceBean.getServiceName() + "' ");
				}

			}
			// Merge URLs
			try {
				for (Url url : uploadedServiceBean.getRealServiceUrls()) {
					if (inMemoryServiceBean.hasRealServiceUrl(url)) {
						readResults.addConflictMsg("Real url already defined: "
								+ url.getFullUrl());
					} else {
						readResults.addAdditionMsg("Added real URL: "
								+ url.getFullUrl());
						inMemoryServiceBean.saveOrUpdateRealServiceUrl(url);
					}
				}
			} catch (Exception e) {

			}
		}

		store.setReadOnlyMode(originalMode);
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
		return MockeyXmlFileManager.MOCK_SERVICE_FOLDER + FILESEPERATOR
				+ result + ".xml";
	}

}
