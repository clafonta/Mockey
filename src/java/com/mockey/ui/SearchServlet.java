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
package com.mockey.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mockey.model.Scenario;
import com.mockey.model.SearchResult;
import com.mockey.model.Service;
import com.mockey.model.Url;
import com.mockey.storage.IApiStorage;
import com.mockey.storage.IApiStorageInMemory;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * For searching anything in Mockey definitions, configurations, and results.
 * 
 * @author chadlafontaine
 * 
 */
public class SearchServlet extends HttpServlet implements ServicePlanConfigurationAPI {

	private Log log = LogFactory.getLog(SearchServlet.class);

	private IMockeyStorage store = StorageRegistry.MockeyStorage;
	private IApiStorage apiStore = IApiStorageInMemory.getInstance();

	/**
	 * 
	 * 
	 * @param req
	 *            basic request
	 * @param resp
	 *            basic resp
	 * @throws ServletException
	 *             basic
	 * @throws IOException
	 *             basic
	 */
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		List<SearchResult> searchResultList = new ArrayList<SearchResult>();
		String term = req.getParameter("term");

		if (term != null && term.trim().length() > 0) {
			term = term.trim();
			for (Service service : store.getServices()) {

				SearchResult sr = buildSearchResult(term, service.getServiceName());
				if(sr!=null){
					sr.setType("service");
					sr.setServiceId(""+service.getId());
					searchResultList.add(sr);
					
				}
				
				for (Url url : service.getRealServiceUrls()) {
					SearchResult subresult = buildSearchResult(term, url.toString());
					if(subresult!=null){
						subresult.setType("service");
						subresult.setServiceId(""+service.getId());
						searchResultList.add(subresult);
					}
				}

				for (Scenario scenario : service.getScenarios()) {
					SearchResult subresult = buildSearchResult(term, scenario.getResponseMessage());
					if(subresult!=null){
						subresult.setType("scenario");
						subresult.setServiceId(""+service.getId());
						subresult.setScenarioId(""+scenario.getId());
						subresult.setScenarioName(scenario.getScenarioName());
						searchResultList.add(subresult);
					}
				}

			}
		}

		req.setAttribute("results", searchResultList);
		req.setAttribute("term", term);
		RequestDispatcher dispatch = req.getRequestDispatcher("/search_result.jsp");
		dispatch.forward(req, resp);

	}

	private SearchResult buildSearchResult(String term, String content) {

		SearchResult result = null;
		if (term != null && content != null) {
			int index = content.toLowerCase().indexOf(term.toLowerCase());
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
