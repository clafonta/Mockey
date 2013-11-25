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

import com.mockey.model.Scenario;

/**
 * Validates creation of MockServiceScenarioBean.
 */
public class ScenarioValidator {

	private final static int SERVICE_NAME_SIZE_LIMIT = 250;

	/**
	 * 
	 * @param ms
	 *            MockServiceScenarioBean to validate.
	 * @return a mapping of input field names and error messages, key value
	 *         pairs. If no errors, then empty Map.
	 */
	public static Map<String, String> validate(Scenario mss) {
		Map<String, String> errorMap = new HashMap<String, String>();

		// TRIM input in case user entered only spaces in input fields.
		if ((mss.getScenarioName() == null) || (mss.getScenarioName().trim().length() < 1)
				|| (mss.getScenarioName().trim().length() > SERVICE_NAME_SIZE_LIMIT)) {
			errorMap.put("name", "Service scenario name must not be empty or greater than " + SERVICE_NAME_SIZE_LIMIT
					+ " chars.");
		}

		return errorMap;
	}
}
