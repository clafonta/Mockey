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
package com.mockey.runner;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.SimpleJSAP;
import com.mockey.storage.xml.MockeyXmlFileManager;
import com.mockey.ui.StartUpServlet;

public class JettyRunner {
	public static void main(String[] args) throws Exception {
		if (args == null)
			args = new String[0];

		// Initialize the argument parser
		SimpleJSAP jsap = new SimpleJSAP("java -jar Mockey.jar", "Starts a Jetty server running Mockey");
		jsap.registerParameter(new FlaggedOption("port", JSAP.INTEGER_PARSER, "8080", JSAP.NOT_REQUIRED, 'p', "port",
				"port to run Jetty on"));
		jsap.registerParameter(new FlaggedOption("file", JSAP.STRING_PARSER, MockeyXmlFileManager.MOCK_SERVICE_DEFINITION,
				JSAP.NOT_REQUIRED, 'f', "file", "relative path to file to initialize Mockey"));
		
		jsap.registerParameter(new FlaggedOption("transientState", JSAP.BOOLEAN_PARSER, "true",
				JSAP.NOT_REQUIRED, 't', "transientState", "Read only mode if set to true, no updates are made to the file system."));

		jsap.registerParameter(new FlaggedOption("filterTag", JSAP.STRING_PARSER, "",
				JSAP.NOT_REQUIRED, 'F', "filterTag", "Filter tag for services and scenarios, useful for 'only use information with this tag'. "));

		// parse the command line options
		JSAPResult config = jsap.parse(args);

		// Bail out if they asked for the --help
		if (jsap.messagePrinted()){
			System.exit(1);
		}

		// Construct the new arguments for jetty-runner
		int port = config.getInt("port");
		boolean transientState = true;
		
		try {
			transientState = config.getBoolean("transientState");
		}catch(Exception e){
			//
		}
		
		// Initialize Log4J file roller appender.
		StartUpServlet.getDebugFile();
		InputStream log4jInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
				"WEB-INF/log4j.properties");
		Properties log4JProperties = new Properties();
		log4JProperties.load(log4jInputStream);
		PropertyConfigurator.configure(log4JProperties);

		Server server = new Server(port);

		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setConfigurations(new Configuration[] { new PreCompiledJspConfiguration() });

		ClassPathResourceHandler resourceHandler = new ClassPathResourceHandler();
		resourceHandler.setContextPath("/");

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.addHandler(resourceHandler);

		contexts.addHandler(webapp);

		server.setHandler(contexts);

		server.start();
		// Construct the arguments for Mockey
		String file = String.valueOf(config.getString("file"));
		String filterTag = config.getString("filterTag");
		String fTagParam = "";
		if(filterTag!=null){
			fTagParam = "&filterTag="+ URLEncoder.encode(filterTag, "UTF-8");
		}
		// Startup displays a big message and URL redirects after x seconds. Snazzy.
		String initUrl = "/home";
		// BUT...if a file is defined, (which it *should*),
		// then let's initialize with it instead.
		if (file != null && file.trim().length() > 0) {
			URLEncoder.encode(initUrl, "UTF-8");
			initUrl = "/home?action=init&transientState="+transientState+"&file=" + URLEncoder.encode(file, "UTF-8") + fTagParam;
		}else {
			initUrl = "/home?action=init&transientState="+transientState+"&file=" + URLEncoder.encode(MockeyXmlFileManager.MOCK_SERVICE_DEFINITION, "UTF-8") + fTagParam;
			
		}

		new Thread(new BrowserThread("http://127.0.0.1", String.valueOf(port), initUrl, 0)).start();

		server.join();
	}

}
