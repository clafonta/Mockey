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

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.SimpleJSAP;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.InputStream;
import java.util.Properties;

public class JettyRunner {
    public static void main(String[] args) throws Exception {
        if (args == null) args = new String[0];

        // Initialize the argument parser
        SimpleJSAP jsap = new SimpleJSAP("java -jar Mockey.jar", "Starts a Jetty server running Mockey");
        jsap.registerParameter(new FlaggedOption("port", JSAP.INTEGER_PARSER, "8080", JSAP.NOT_REQUIRED, 'p', "port", "port to run Jetty on"));


        // parse the command line options
        JSAPResult config = jsap.parse(args);

        // Bail out if they asked for the --help
        if (jsap.messagePrinted()) System.exit(1);


        // Construct the new arguments for jetty-runner
        int port = config.getInt("port");

        InputStream log4jInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("WEB-INF/log4j.properties");
        Properties log4JProperties = new Properties();
        log4JProperties.load(log4jInputStream);
        PropertyConfigurator.configure(log4JProperties);

        Server server = new Server(port);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        webapp.setConfigurations(new Configuration[]{new PreCompiledJspConfiguration()});

        ClassPathResourceHandler resourceHandler = new ClassPathResourceHandler();
        resourceHandler.setContextPath("/");

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.addHandler(resourceHandler);

        contexts.addHandler(webapp);

        server.setHandler(contexts);

        server.start();
        new Thread(new BrowserThread("http://localhost", String.valueOf(port), "/startup.html", 0)).start();

        server.join();
    }

}
