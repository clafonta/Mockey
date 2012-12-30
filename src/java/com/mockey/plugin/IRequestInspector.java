/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2012  Authors:
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
package com.mockey.plugin;

import com.mockey.model.RequestFromClient;

/**
 * Used to inspect an incoming request to Mockey. This implementation can be
 * applied to an individual service or if the global flag is set to true, will
 * be applied to each incoming request mapped to a Service. What is the purpose
 * of this? This is designed to inform developers and QA whether or not their
 * service requests is missing important information, e.g. a session token,
 * cookie information, etc. 
 * 
 * This is designed for complex Request validation. 
 * 
 * @see com.mockey.model.Service#getRequestInspectorName()
 * @see com.mockey.storage.IMockeyStorage#getRequestInspectorByClassName(String)
 * 
 * @author clafonta
 * 
 */
public interface IRequestInspector {

	/**
	 * Implementation will evaluate incoming request, example may include
	 * incoming parameters, headers, body, etc.
	 * 
	 * @param request
	 */
	public void analyze(RequestFromClient request);

	/**
	 * 
	 * @return message to display
	 */
	public String getPostAnalyzeResultMessage();

	/**
	 * 
	 * @return true if this implementation should be applied to each service,
	 *         false otherwise.
	 */
	public boolean isGlobal();

}
