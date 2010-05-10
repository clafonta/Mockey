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
        new Thread(new BrowserThread("http://localhost", String.valueOf(port), "/home", 0)).start();

        server.join();
    }

}
