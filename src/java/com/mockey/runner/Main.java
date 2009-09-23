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
        new Thread(new BrowserThread("http://127.0.0.1:", port, 10)).start();


        // start Jetty
        Runner runner = new Runner();
        runner.configure(argv);
        runner.run();

        new File("Mockey.war").deleteOnExit();
    }

}

