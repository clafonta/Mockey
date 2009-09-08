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
package com.mockey.ui;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class StartUpServlet extends HttpServlet {

	private static final long serialVersionUID = -6466436642921760561L;
	private static Logger logger = Logger.getLogger(StartUpServlet.class);
	private static Properties appProps = new Properties();

	public void init() throws ServletException {

		String log4jFile = getInitParameter("log4j.properties");
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
