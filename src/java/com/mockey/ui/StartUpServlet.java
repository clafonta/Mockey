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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.mockey.storage.xml.MockeyXmlFileManager;

public class StartUpServlet extends HttpServlet {

	private static final long serialVersionUID = -6466436642921760561L;
	// private static Logger logger = Logger.getLogger(StartUpServlet.class);
	private static final String SYSTEM_PROPERTY_KEY_DEBUG_FILE = "pathToMockeyDebugFile";

	public static final String MOCKEY_DEBUG = "mockeyDebugFile.log";

	private static File debugFile = null;

	/**
	 * 
	 * @return Location of debug output from
	 *         <code>org.apache.log4j.RollingFileAppender</code>
	 * @see org.apache.log4j.RollingFileAppender
	 */
	public static File getDebugFile() {

		if (debugFile == null || !debugFile.exists()) {
			// ***************
			// JETTY & TOMCAT compatible
			// Not context
			// ***************
			// If no explicit path, then check for a system variable.
			// Check for SYSTEM PROPERTY
			String repoHome = System.getProperty(MockeyXmlFileManager.SYSTEM_PROPERTY_MOCKEY_DEF_REPO_HOME);
			if (repoHome != null) {
				String msg = "System environment '" + MockeyXmlFileManager.SYSTEM_PROPERTY_MOCKEY_DEF_REPO_HOME
						+ "' value is provided. Writing debug file here: " + repoHome;
				System.out.println(msg);
			}
			//
			try {
				String debugFilePath = null;
				if (repoHome != null) {
					debugFilePath = repoHome + File.separatorChar + MOCKEY_DEBUG;
				} else {
					debugFilePath = MOCKEY_DEBUG;
				}
				debugFile = new File(debugFilePath);
				debugFile.createNewFile();
				String abPath = getDebugFile().getAbsolutePath();
				System.out.println("Created debug file " + abPath);
				System.setProperty(SYSTEM_PROPERTY_KEY_DEBUG_FILE, abPath);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return debugFile;
	}

	public void init() throws ServletException {

		// Init
		getDebugFile();

		try {

			MockeyXmlFileManager reader = MockeyXmlFileManager.getInstance();
			reader.loadConfiguration();

		} catch (FileNotFoundException fnf) {

			System.out.println("File used to initialize Mockey not found. "
					+ "It's OK; one will be created if Mockey is not in 'memory-mode-only' "
					+ "meaning you have to tell Mockey to 'write-to-file' via the web browser interface. ");

		} catch (Exception e) {
			// logger.error("StartUpServlet:init()", e);
			e.printStackTrace();
		}

	}
}
