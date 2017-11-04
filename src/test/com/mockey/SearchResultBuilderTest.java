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

import java.util.List;

import org.testng.annotations.Test;

import com.mockey.model.Scenario;
import com.mockey.model.SearchResult;
import com.mockey.model.SearchResultType;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.InMemoryMockeyStorage;

@Test
public class SearchResultBuilderTest {

	/**
	 * Term 'scenario' only appears 1 time, in type Scenario Term 'service' only
	 * appears 2 times, in type Scenario and Service
	 */
	@Test
	public void validateSearchResultByName() {

		IMockeyStorage store = new InMemoryMockeyStorage();
		Service a = new Service();
		a.setServiceName("Service A");
		Scenario aScenario = new Scenario();
		aScenario.setScenarioName("Service Scenario A");
		a.saveOrUpdateScenario(aScenario);
		store.saveOrUpdateService(a);

		String term = "scenario";
		SearchResultBuilder resultBuilder = new SearchResultBuilder("");
		List<SearchResult> resultList = resultBuilder.buildSearchResults(term,
				store);

		assert (resultList.size() == 1) : "Length should be: "
				+ resultList.size();
		assert (resultList.get(0).getType() == SearchResultType.SERVICE_SCENARIO) : "Incorrect type, should be '"
				+ SearchResultType.SERVICE_SCENARIO
				+ "' but was '"
				+ resultList.get(0).getType() + "'";

		term = "service";
		resultList = resultBuilder.buildSearchResults(term, store);

		assert (resultList.size() == 2) : "Length should be: 2 " + " but was '"
				+ resultList.size() + "'";
	}

	/**
	 * Term 'scenario' only appears 1 time, in type Scenario Term 'service' only
	 * appears 2 times, in type Scenario and Service
	 */
	@Test
	public void validateSearchResultScenarioContent() {

		IMockeyStorage store = new InMemoryMockeyStorage();
		Service a = new Service();
		a.setServiceName("Service A");
		Scenario aScenario = new Scenario();
		aScenario.setScenarioName("Scenario A");
		aScenario.setResponseMessage("lorem ipsum");
		a.saveOrUpdateScenario(aScenario);
		store.saveOrUpdateService(a);

		String term = "cats";
		SearchResultBuilder resultBuilder = new SearchResultBuilder("");
		List<SearchResult> resultList = resultBuilder.buildSearchResults(term,
				store);

		assert (resultList.size() == 0) : "Length should be 0 but was "
				+ resultList.size();

		term = "  lorem   ";
		resultBuilder = new SearchResultBuilder("");
		resultList = resultBuilder.buildSearchResults(term, store);
		assert (resultList.size() == 1) : "Length should be: 1 " + " but was '"
				+ resultList.size() + "'";
		assert (resultList.get(0).getType() == SearchResultType.SERVICE_SCENARIO) : "Search type result should be 'scenario' but was '"
				+ resultList.get(0).getType().toString() + "'";

		aScenario = new Scenario();
		aScenario.setScenarioName("Scenario B");
		aScenario.setResponseMessage("lorem ipsula");
		a.saveOrUpdateScenario(aScenario);
		store.saveOrUpdateService(a);

		term = "  lorem   ";
		resultBuilder = new SearchResultBuilder("");
		resultList = resultBuilder.buildSearchResults(term, store);
		// We should have TWO scenarios with 'lorem' in content/
		assert (resultList.size() == 2) : "Length should be: 2 " + " but was '"
				+ resultList.size() + "'";
	}

	/**
	 * Term 'scenario' only appears 1 time, in type Scenario Term 'service' only
	 * appears 2 times, in type Scenario and Service
	 */
	@Test
	public void validateSearchResultByServiceTag() {

		IMockeyStorage store = new InMemoryMockeyStorage();
		Service a = new Service();
		a.setServiceName("Service A");
		a.setTag("   tagCC");
		store.saveOrUpdateService(a);

		String term = "cats";
		SearchResultBuilder resultBuilder = new SearchResultBuilder("");
		List<SearchResult> resultList = resultBuilder.buildSearchResults(term,
				store);

		assert (resultList.size() == 0) : "Length should be 0 but was "
				+ resultList.size();

		term = "tagC";
		resultBuilder = new SearchResultBuilder("");
		resultList = resultBuilder.buildSearchResults(term, store);
		assert (resultList.size() == 1) : "Length should be 1 but was "
				+ resultList.size();
		assert (resultList.get(0).getType() == SearchResultType.SERVICE) : "Incorrect type, should be '"
				+ SearchResultType.SERVICE_SCENARIO
				+ "' but was '"
				+ resultList.get(0).getType() + "'";

	}
	
	/**
	 * Term 'scenario' only appears 1 time, in type Scenario Term 'service' only
	 * appears 2 times, in type Scenario and Service
	 */
	@Test
	public void validateSearchResultByServicePlanTag() {

		IMockeyStorage store = new InMemoryMockeyStorage();
		Service a = new Service();
		a.setServiceName("Service A");
		a.setTag("   tagCC");
		store.saveOrUpdateService(a);
		
		ServicePlan servicePlan = new ServicePlan();
		servicePlan.setName("ServicePlan A");
		servicePlan.setTag("service_plan_tag_123");
		store.saveOrUpdateServicePlan(servicePlan);
		
		String term = "cats";
		SearchResultBuilder resultBuilder = new SearchResultBuilder("");
		List<SearchResult> resultList = resultBuilder.buildSearchResults(term,
				store);

		assert (resultList.size() == 0) : "Length should be 0 but was "
				+ resultList.size();

		term = "service_plan_tag_123";
		resultBuilder = new SearchResultBuilder("");
		resultList = resultBuilder.buildSearchResults(term, store);
		assert (resultList.size() == 1) : "Length should be 1 but was "
				+ resultList.size();
		assert (resultList.get(0).getType() == SearchResultType.SERVICE_PLAN) : "Incorrect type, should be '"
				+ SearchResultType.SERVICE_PLAN
				+ "' but was '"
				+ resultList.get(0).getType() + "'";

	}
}
