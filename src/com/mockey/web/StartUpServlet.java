/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mockey.web;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class StartUpServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6466436642921760561L;

	/** Logger */
	private static Logger logger = Logger.getLogger(StartUpServlet.class);

	protected static Properties appProps = new Properties();

	/**
	 * <p>
	 * To be called by the servlet engine when it first starts up. A few things
	 * get initialized here:
	 * </p>
	 * <li>Logging</li>
	 * <li>General application properties</li>
	 * <li>Handle to a connectin pool</li>
	 * 
	 * @throws ServletException
	 *             basic
	 */
	public void init() throws ServletException {
		// Configuration paths for log4J
		String log4jFile = getInitParameter("log4j.properties");

		// Configuration paths for application properties
		String appPropFile = getInitParameter("default.properties");

		// base directory of servlet context
		String contextPath = getServletContext().getRealPath(System.getProperty("file.separator"));

		try {
			contextPath = "/";

			InputStream log4jInputStream = getServletContext().getResourceAsStream(contextPath + log4jFile);
			Properties log4JProperties = new Properties();
			log4JProperties.load(log4jInputStream);
			PropertyConfigurator.configure(log4JProperties);

			InputStream appInputStream = getServletContext().getResourceAsStream(contextPath + appPropFile);
			appProps.load(appInputStream);
		} catch (Exception e) {
			logger.error("StartUpServlet:init()", e);
		}
	}
}
