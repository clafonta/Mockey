/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mockey.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.storage.xml.MockeyXmlFileConfigurationReader;

/**
 * Consumes an XML file and configures Mockey services.
 * 
 * @author Chad.Lafontaine
 * 
 */
public class ConfigurationReader {

	private static final long serialVersionUID = 2874257060865115637L;
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	private static Logger logger = Logger.getLogger(ConfigurationReader.class);

	/**
	 * 
	 * @param file
	 *            - xml configuration file for Mockey
	 * @throws IOException
	 * @throws SAXException
	 * @throws SAXParseException
	 */
	public void inputFile(File file) throws IOException, SAXParseException,
			SAXException {
		InputStream is = new FileInputStream(file);

		long length = file.length();

		if (length > Integer.MAX_VALUE) {

			logger.error("File too large");
		} else {

			// Create the byte array to hold the data
			byte[] bytes = new byte[(int) length];
			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file "
						+ file.getName());
			}

			// Close the input stream and return bytes
			is.close();

			loadConfiguration(bytes);
		}

	}

	/**
	 * 
	 * @param data
	 * @return results (conflicts and additions).
	 * @throws IOException
	 * @throws SAXException
	 * @throws SAXParseException
	 */
	public ServiceMergeResults loadConfiguration(byte[] data)
			throws IOException, SAXParseException, SAXException {
		ServiceMergeResults mergeResults = new ServiceMergeResults();

		String strXMLDefintion = new String(data);
		MockeyXmlFileConfigurationReader msfr = new MockeyXmlFileConfigurationReader();
		IMockeyStorage mockServiceStoreTemporary = msfr
				.readDefinition(strXMLDefintion);
		// PROXY SETTINGs
		store.setProxy(mockServiceStoreTemporary.getProxy());

		// UNIVERSAL RESPONSE SETTINGS
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
		// When loading a definition file, by default, we should
		// compare uploaded Service’s mock URL to what's currently
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
		List<Service> uploadedServices = mockServiceStoreTemporary
				.getServices();
		Iterator<Service> iter2 = uploadedServices.iterator();
		while (iter2.hasNext()) {
			Service uploadedServiceBean = (Service) iter2.next();
			List<Service> serviceBeansInMemory = store.getServices();
			Iterator<Service> iter3 = serviceBeansInMemory.iterator();
			boolean existingServiceWithMatchingMockUrl = false;
			Service inMemoryServiceBean = null;
			while (iter3.hasNext()) {
				inMemoryServiceBean = (Service) iter3.next();
				Url firstMatchingUrl = inMemoryServiceBean
						.getFirstMatchingRealServiceUrl(uploadedServiceBean);
				if (firstMatchingUrl != null) {
					existingServiceWithMatchingMockUrl = true;
					mergeResults
							.addConflictMsg("Service '"
									+ uploadedServiceBean.getServiceName()
									+ "' not created; will try to merge into existing service labeled '"
									+ inMemoryServiceBean.getServiceName()
									+ "' ");
					break;
				}
			}
			if (!existingServiceWithMatchingMockUrl) {
				// We null it, to not stomp on any services
				uploadedServiceBean.setId(null);
				store.saveOrUpdateService(uploadedServiceBean);
				mergeResults.addAdditionMsg("Service '"
						+ uploadedServiceBean.getServiceName() + "' created. ");

			} else {
				// Just merge scenarios per matching services
				mergeResults = mergeServices(uploadedServiceBean,
						inMemoryServiceBean, mergeResults);
			}

		}
		// PLANS
		List<ServicePlan> servicePlans = mockServiceStoreTemporary
				.getServicePlans();
		Iterator<ServicePlan> iter3 = servicePlans.iterator();
		while (iter3.hasNext()) {
			ServicePlan servicePlan = iter3.next();
			store.saveOrUpdateServicePlan(servicePlan);
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
			Service inMemoryServiceBean, ServiceMergeResults readResults) {
		if (uploadedServiceBean != null && inMemoryServiceBean != null) {
			// Merge Scenarios
			if (readResults == null) {
				readResults = new ServiceMergeResults();
			}
			Iterator<Scenario> uIter = uploadedServiceBean.getScenarios()
					.iterator();
			Iterator<Scenario> mIter = inMemoryServiceBean.getScenarios()
					.iterator();
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
					readResults.addAdditionMsg("Scenario '"
							+ uploadedScenario.getScenarioName()
							+ "' added to service '"
							+ inMemoryServiceBean.getServiceName() + "' ");
				} else {
					readResults.addConflictMsg("Scenario '"
							+ mBean.getScenarioName()
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
}
