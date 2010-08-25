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

import java.io.File;

import org.mortbay.jetty.runner.Runner;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.SimpleJSAP;

public class Main {


    public static void main(String args[]) throws Exception {
        if(args == null) args = new String[0];
        

        // Initialize the argument parser
        SimpleJSAP jsap = new SimpleJSAP("java -jar Mockey.jar","Starts a Jetty server running Mockey");
        jsap.registerParameter(new FlaggedOption("port", JSAP.INTEGER_PARSER,"8080",JSAP.NOT_REQUIRED,'p',"port", "port to run Jetty on"));


        // parse the command line options
        JSAPResult config = jsap.parse(args);

        // Bail out if they asked for the --help
        if ( jsap.messagePrinted() ) System.exit( 1 );


        // Construct the new arguments for jetty-runner
        String port = String.valueOf(config.getInt("port"));
        String[] argv = {"--port", port, "Mockey.war"};


        // Start the default browser in 10 seconds
        new Thread(new BrowserThread("http://127.0.0.1:", port, "/home", 10)).start();


        // start Jetty
        Runner runner = new Runner();
        runner.configure(argv);
        runner.run();

        new File("Mockey.war").deleteOnExit();
    }

}

