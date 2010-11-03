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

import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

public class StartUpServlet extends HttpServlet {

    private static final long serialVersionUID = -6466436642921760561L;
    private static Logger logger = Logger.getLogger(StartUpServlet.class);
    private static Properties appProps = new Properties();
    public static final String MOCK_SERVICE_DEFINITION = "mock_service_definitions.xml";


    public void init() throws ServletException {

        String log4jFile = getInitParameter("log4j.properties");
        String appPropFile = getInitParameter("default.properties");
        // base directory of servlet context
        String contextPath = getServletContext().getRealPath(System.getProperty("file.separator"));
        contextPath = "/";

        try {
            InputStream log4jInputStream = getServletContext().getResourceAsStream(contextPath + log4jFile);
            Properties log4JProperties = new Properties();
            log4JProperties.load(log4jInputStream);
            PropertyConfigurator.configure(log4JProperties);

        }catch(Exception npe) {
            System.out.println("Unable to find log4j.properties in servlet context");
        }

        try {
            //logger.info("default.properties: "+getServletContext().getResource("/web.xml"));
            InputStream appInputStream = getServletContext().getResourceAsStream(contextPath + appPropFile);
            if(appInputStream == null) {
                // try classpath
                appInputStream = getClass().getResourceAsStream(contextPath + appPropFile);
            }
            appProps.load(appInputStream);

            // Doesn't the HomeServlet do this? Yes but 
            // this is one duplicate activity that allows for 
            // sandbox development (i.e. within Eclipse)
            // since we're not using JettyRunner, which contains 
            // logic to pass/tell HomeServlet _how_ to initialize.
            File f = new File(MOCK_SERVICE_DEFINITION);
            if (f.exists()) {
                // Slurp it up and initialize definitions.
                FileInputStream fstream = new FileInputStream(f);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Charset.forName(HTTP.UTF_8)));
                StringBuffer inputString = new StringBuffer();
                // Read File Line By Line
                String strLine = null;
                while ((strLine = br.readLine()) != null) {
                    // Print the content on the console
                    inputString.append(new String(strLine.getBytes(HTTP.UTF_8)));
                }
                ConfigurationReader reader = new ConfigurationReader();
                reader.loadConfiguration(inputString.toString().getBytes(HTTP.UTF_8));
                logger.info("first initialization with "+ MOCK_SERVICE_DEFINITION);
            }
        } 
       
        
        catch (Exception e) {
            logger.error("StartUpServlet:init()", e);
        }
    }
}
