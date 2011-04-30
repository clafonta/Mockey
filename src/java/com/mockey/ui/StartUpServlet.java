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
	//private static Logger logger = Logger.getLogger(StartUpServlet.class);
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
		
		if (debugFile==null || !debugFile.exists()) {
			// ***************
			// JETTY & TOMCAT compatible
			// Not context 
			// ***************
			try {
				debugFile = new File(MOCKEY_DEBUG);
				debugFile.createNewFile();
				String abPath =  getDebugFile().getAbsolutePath();
				System.out.println("Created debug file " + abPath);
				System.setProperty(SYSTEM_PROPERTY_KEY_DEBUG_FILE, abPath);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return debugFile;
	}
	public void init() throws ServletException {

		//Init
		getDebugFile();

		try {

			// Doesn't the HomeServlet do this? Yes but
			// this is one duplicate activity that allows for
			// sandbox development (i.e. within Eclipse)
			// since we're not using JettyRunner, which contains
			// logic to pass/tell HomeServlet _how_ to initialize.

			MockeyXmlFileManager reader = new MockeyXmlFileManager();
			reader.loadConfiguration();

		}
		catch (FileNotFoundException fnf) {
			System.out.println("File used to initialize Mockey not found. It's OK; one will be created. ");
			
		}
		catch (Exception e) {
			//logger.error("StartUpServlet:init()", e);
			e.printStackTrace();
		}
	}
}
