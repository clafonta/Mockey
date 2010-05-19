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
package com.mockey;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mockey.model.Service;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Validator for contents of a MockServiceBean definition.
 * 
 * @author chad.lafontaine
 * 
 */
public class ServiceValidator {
	/** Basic logger */
	private static Logger logger = Logger.getLogger(ServiceValidator.class);
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	/**
	 * Return a mapping of input field names and error messages. If the mock
	 * service state is valid, then an empty Map is returned.
	 * 
	 * @param ms
	 * @return
	 */
	public static Map<String, String> validate(Service ms) {
		Map<String, String> errorMap = new HashMap<String, String>();

		if ((ms.getServiceName() == null)
				|| (ms.getServiceName().trim().length() < 1)
				|| (ms.getServiceName().trim().length() > 250)) {
			errorMap
					.put("serviceName",
							"Service name must not be empty or greater than 250 chars.");
		}

		// Make sure there doesn't exist a service
		// w/ the same non-empty real URL.
		try {

			for (Service testService : store.getServices()) {

				Url firstMatch = testService.getFirstMatchingRealServiceUrl(ms);
				if (firstMatch != null
						&& !testService.getId().equals(ms.getId())) {

					errorMap
							.put(
									"serviceUrlMsg",
									"One of your Real service URL entries is already managed by the '"
											+ testService.getServiceName()
											+ "' service. Please choose another real URL pattern. ");
					errorMap.put("serviceUrl", firstMatch.getFullUrl());
					break;
				} else if (testService.getUrl() != null && ms.getUrl() != null) {
					if (testService.getUrl().trim().equalsIgnoreCase(
							ms.getUrl().trim()) && !testService.getId().equals(ms.getId())) {

						errorMap
								.put(
										"urlMsg",
										"Your Mock service URL entry is already used by the '"
												+ testService.getServiceName()
												+ "' service. Please choose another mock URL pattern. ");
						errorMap.put("url", ms.getUrl().trim());
						break;
					}
				}
			}

		} catch (Exception e) {
			logger.error(
					"Unable to verify if there are duplicate service URLs", e);
		}

		return errorMap;
	}
}
