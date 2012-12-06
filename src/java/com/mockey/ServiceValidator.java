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
package com.mockey;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

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

		if ((ms.getServiceName() == null) || (ms.getServiceName().trim().length() < 1)
				|| (ms.getServiceName().trim().length() > 250)) {
			errorMap.put("serviceName", "Service name must not be empty or greater than 250 chars.");
		}
		
		// Validate JSON format. 
		if(ms.getRequestInspectorJsonRules() !=null && ms.getRequestInspectorJsonRules().trim().length()>0)
		try {
			new JSONObject(ms.getRequestInspectorJsonRules());
			
		} catch (JSONException e1) {
			errorMap.put("requestInspectorJsonRules", "Invalid JSON format. ");
			logger.debug("Invalid JSON format for rules " + e1.getMessage());
			
		}
		

		// This validation is important
		// for bad URL checking, but
		// prevents people from creating
		// a simple Mockey mock up.
		// E.g. http://localhost:8080/Mockey/service/dummy
//		if (ms.getUrl() != null && ms.getUrl().trim().length() > 0) {
//			URL aURL;
//			try {
//				aURL = new URL(ms.getUrl());
//				// Let's make sure user doesn't have any REF or QUERY arguments
//				// Why? Because Mockey tries to find match incoming request
//				// to service Real and Mock URLs, and when people append
//				// random parameters on the end of similar URL, it gets
//				// hard to map URL X to URL X.
//				if (aURL.getQuery() != null || aURL.getRef() != null) {
//					errorMap
//							.put("urlMsg",
//									"It looks like you have a well form URL but you can't have any QUERY or REFERENCE arguments.");
//					return errorMap;
//				}
//
//			} catch (MalformedURLException e) {
//
//				errorMap.put("urlMsg", "It looks like you have a malformed URL: " + e.getMessage());
//
//				return errorMap;
//			}
//		}
		// Make sure there doesn't exist a service
		// w/ the same non-empty real URL.
		try {

			for (Service testService : store.getServices()) {

				Url firstMatch = testService.getFirstMatchingRealServiceUrl(ms);
				if (firstMatch != null && !testService.getId().equals(ms.getId())) {

					errorMap.put("serviceUrlMsg", "One of your Real service URL entries is already managed by the '"
							+ testService.getServiceName() + "' service. Please choose another real URL pattern. ");
					errorMap.put("serviceUrl", firstMatch.getFullUrl());
					break;
				} else if (testService.getUrl() != null && ms.getUrl() != null) {
					if (testService.getUrl().trim().equalsIgnoreCase(ms.getUrl().trim())
							&& !testService.getId().equals(ms.getId())) {

						errorMap.put("urlMsg", "Your Mock service URL entry is already used by the '"
								+ testService.getServiceName() + "' service. Please choose another mock URL pattern. ");
						errorMap.put("url", ms.getUrl().trim());
						break;
					}
				}
			}

		} catch (Exception e) {
			logger.error("Unable to verify if there are duplicate service URLs", e);
		}

		return errorMap;
	}
	
}
