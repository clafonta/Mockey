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
package com.mockey.model;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class SampleRequestInspector implements IRequestInspector {
	private static Logger logger = Logger.getLogger(SampleRequestInspector.class);

	@Override
	public void analyze(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String hello = request.getParameter("hello");
		if(hello!=null){
			System.out.println("Hello " + hello);
			logger.debug("Success; got a value for parameter 'hello': " + hello);
		}else {
			System.out.println("Hello " + hello);
			logger.error("Expected a parameter value for 'hello'");
		}

	}

}
