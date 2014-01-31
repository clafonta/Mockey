/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2010  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
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
package com.mockey.ui;

import java.util.List;

import com.mockey.model.ConflictInfo;
import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.Url;
import com.mockey.storage.xml.MockeyXmlFileManager;

/**
 * Utility/helper class to help flag 'conflicts', possible problems in the
 * setup.
 * 
 * @author clafonta
 * 
 */
public class ConflictHelper {

	private static final String MATCH_NAME = "Has matching Service name. Evaluation is based on comparing lowercase, alphanumberic values only. Example 'AAAA*' matches 'aaaa' ";
	private static final String MATCH_REAL_URL = "Has a the same real URL.";
	private static final String MATCH_SCENARIO = "Has duplicate scenario(s).";
	private static final String MATCH_MOCK_URL = "Has a matching mock URL(s)";

	/**
	 * 
	 * @param tagFilter
	 * @param store
	 * @return
	 */
	public ConflictInfo getConflictInfo(List<Service> serviceList) {
		ConflictInfo conflictInfo = new ConflictInfo();

		for (Service serviceA : serviceList) {

			// Check for conflicts with other services
			for (Service serviceB : serviceList) {

				// Don't compare Service to itself!
				if (!serviceA.getId().equals(serviceB.getId())) {

					// NAME
					if (MockeyXmlFileManager.getSafeForFileSystemName(serviceA.getServiceName()).equals(
							MockeyXmlFileManager.getSafeForFileSystemName(serviceB.getServiceName()))) {
						conflictInfo.addConflict(serviceA, serviceB, "Service with name'" + serviceA.getServiceName()
								+ "' compared to service with name: '" + serviceB.getServiceName() + "'. Info: " + MATCH_NAME);
					}

					// REAL URLs
					for (Url urlA : serviceA.getRealServiceUrls()) {
						for (Url urlB : serviceB.getRealServiceUrls()) {
							if (urlA.equals(urlB)) {
								conflictInfo.addConflict(serviceA, serviceB, MATCH_REAL_URL);
							}
						}
					}

					// SAME MOCK URL
					if (serviceA.getUrl() != null && serviceA.getUrl().equals(serviceB.getUrl())) {
						conflictInfo.addConflict(serviceA, serviceB, MATCH_MOCK_URL);
					}

				}
			}

			// Check for conflicts with
			for (Scenario scenarioA : serviceA.getScenarios()) {
				for (Scenario scenarioB : serviceA.getScenarios()) {

					if (scenarioA.hasSameNameAndResponse(scenarioB) && !scenarioA.getId().equals(scenarioB.getId())) {
						conflictInfo.addConflict(serviceA, serviceA, MATCH_SCENARIO);
					}
				}
			}

		}
		return conflictInfo;
	}

}
