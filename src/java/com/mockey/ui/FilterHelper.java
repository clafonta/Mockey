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

import java.util.ArrayList;
import java.util.List;

import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.storage.IMockeyStorage;

/**
 * Utility/helper class to help flag 'noise', possible problems in the setup.
 * 
 * @author clafonta
 * 
 */
public class FilterHelper {

	/**
	 * 
	 * @param service
	 * @param filterTag
	 * @return
	 */
	public List<Service> getFlaggedServicesInfo(String tagFilter,
			IMockeyStorage store) {

		List<Service> serviceList = this.getFilteredServices(tagFilter, store);

		if (tagFilter != null && tagFilter.trim().length() > 0) {

			List<Service> filteredList = new ArrayList<Service>();
			for (Service tempService : store.getServices()) {
				if (tempService.hasTag(tagFilter)) {
					filteredList.add(tempService);
				}
			}
			serviceList = Util.orderAlphabeticallyByServiceName(filteredList);

		} else {
			serviceList = Util.orderAlphabeticallyByServiceName(store
					.getServices());

		}
		return serviceList;

	}

	/**
	 * 
	 * @param service
	 * @param filterTag
	 * @return Services with matching tag(s)
	 */
	public List<Service> getFilteredServices(String tagFilter,
			IMockeyStorage store) {

		List<Service> serviceList = null;

		if (tagFilter != null && tagFilter.trim().length() > 0) {

			List<Service> filteredList = new ArrayList<Service>();
			for (Service tempService : store.getServices()) {
				if (tempService.hasTag(tagFilter)) {
					filteredList.add(tempService);
				}
			}
			serviceList = Util.orderAlphabeticallyByServiceName(filteredList);

			//
		} else {
			serviceList = Util.orderAlphabeticallyByServiceName(store
					.getServices());

		}
		return serviceList;

	}

	/**
	 * 
	 * @param service
	 * @param filterTag
	 * @return
	 */
	public List<ServicePlan> getFilteredServicePlans(String tagFilter,
			IMockeyStorage store) {

		List<ServicePlan> servicePlanList = null;

		if (tagFilter != null && tagFilter.trim().length() > 0) {

			List<ServicePlan> filteredPlanList = new ArrayList<ServicePlan>();
			for (ServicePlan tempServicePlan : store.getServicePlans()) {
				if (tempServicePlan.hasTag(tagFilter)) {
					filteredPlanList.add(tempServicePlan);
				}
			}

			servicePlanList = Util
					.orderAlphabeticallyByServicePlanName(filteredPlanList);

			//
		} else {

			servicePlanList = Util.orderAlphabeticallyByServicePlanName(store
					.getServicePlans());
		}
		return servicePlanList;

	}

}
