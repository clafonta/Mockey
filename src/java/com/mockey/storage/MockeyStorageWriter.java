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

import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.Url;

class MockeyStorageWriter {
	
	String StorageAsString(IMockeyStorage storage) {
        StringBuffer sb = new StringBuffer();
        sb.append(storage.toString());
        for (Service service : storage.getServices() ) {
            sb.append("Service ID: ").append(service.getId()).append("\n");
            sb.append("Service name: ").append(service.getServiceName()).append("\n");
            sb.append("Service description: ").append(service.getDescription()).append("\n");
            for (Url url : service.getRealServiceUrls()) {
                sb.append("    real URL : ").append(url.getFullUrl()).append("\n");
            }
            for (Scenario scenario : service.getScenarios()) {
                sb.append("    scenario name: ").append(scenario.getScenarioName()).append("\n");
                sb.append("    scenario request: ").append(scenario.getRequestMessage()).append("\n");
                sb.append("    scenario response: ").append(scenario.getResponseMessage()).append("\n");
            }
        }
        return sb.toString();
	}
}
