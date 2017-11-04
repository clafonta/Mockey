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
package com.mockey;

import java.util.ArrayList;
import java.util.List;

import com.mockey.model.Scenario;
import com.mockey.model.SearchResult;
import com.mockey.model.SearchResultType;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;

/**
 * 
 * @author clafonta
 * 
 */
public class SearchResultBuilder {

	private String baseAppContextPath = null;

	/**
	 * 
	 * @param _baseAppContextPath
	 *            the application context path, which is needed to combine with
	 *            the mock URL for proper matching, since each service only
	 *            contains a portion of the full mock url (missing scheme,
	 *            hostname, port, etc.)
	 */
	public SearchResultBuilder(String _baseAppContextPath) {
		this.baseAppContextPath = _baseAppContextPath;
	}

	/**
	 * 
	 * @param term
	 * @param store
	 */
	public List<SearchResult> buildSearchResults(String term, IMockeyStorage store) {
		List<SearchResult> searchResultList = new ArrayList<SearchResult>();

		if (term != null && term.trim().length() > 0) {
			term = term.trim();

			// ******************************
			// SERVICE PLAN LIST
			// ******************************
			for (ServicePlan servicePlan : store.getServicePlans()) {
				SearchResult sr = buildSearchResult(term, servicePlan.getName() + " " + servicePlan.getTag());
				if (sr != null) {
					sr.setType(SearchResultType.SERVICE_PLAN);
					sr.setServicePlanId("" + servicePlan.getId());
					sr.setServicePlanName(servicePlan.getName());
					searchResultList.add(sr);
				}
			}

			// ******************************
			// SERVICE LIST
			// ******************************
			for (Service service : store.getServices()) {

				boolean serviceAdded = false;
				SearchResult sr = buildSearchResult(term, service.getServiceName() + " " + service.getTag() + " ");
				if (sr != null) {
					sr.setType(SearchResultType.SERVICE);
					sr.setServiceId("" + service.getId());
					searchResultList.add(sr);
					serviceAdded = true;
				}

				if (!serviceAdded) {
					// No match; lets check RealServiceUrls
					for (Url url : service.getRealServiceUrls()) {
						SearchResult subresult = buildSearchResult(term, url.toString());
						if (subresult != null) {
							subresult.setType(SearchResultType.SERVICE);
							subresult.setServiceId("" + service.getId());
							searchResultList.add(subresult);
							serviceAdded = true;
							break;
						}
					}
				}

				if (!serviceAdded) {
					// No match; lets check mock urls
					String mockurl = service.getUrl();
					SearchResult subresult = buildSearchResult(term, (this.baseAppContextPath + mockurl));
					if (subresult != null) {
						subresult.setType(SearchResultType.SERVICE);
						subresult.setServiceId("" + service.getId());
						searchResultList.add(subresult);
						serviceAdded = true;

					}

				}

				// *****************************
				// SERVICE SCENARIO
				// ****************************
				// REGARDLESS of Service being added, let's see if there is a
				// matching scenario by Scenario RESPONSE
				for (Scenario scenario : service.getScenarios()) {
					// Append tags, name, and response...
					// Why not? It's a hack to jumble all things together
					SearchResult subresult = buildSearchResult(term,
							scenario.getResponseMessage() + " " + scenario.getScenarioName() + " " + scenario.getTag());

					if (subresult != null) {
						subresult.setType(SearchResultType.SERVICE_SCENARIO);
						subresult.setServiceId("" + service.getId());
						subresult.setScenarioId("" + scenario.getId());
						subresult.setScenarioName(scenario.getScenarioName());
						searchResultList.add(subresult);
					}
				}

			}
		}
		return searchResultList;
	}

	private SearchResult buildSearchResult(String term, String content) {

		SearchResult result = null;
		if (term != null && content != null) {
			int index = content.trim().toLowerCase().indexOf(term.trim().toLowerCase());
			if (index > -1) {
				result = new SearchResult();

				String teaserContent = content.substring(index);

				if (teaserContent.length() > 150) {
					teaserContent.substring(0, 148);

				}
				result.setContent(teaserContent);

			}

		}
		return result;
	}
}
