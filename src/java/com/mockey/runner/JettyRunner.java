package com.mockey.runner;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.InputStream;
import java.util.Properties;

public class JettyRunner {
        public static void main(String[] args) throws Exception {
        System.out.println("Starting!");

        int port = 8080;

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
        new Thread(new BrowserThread("http://localhost",String.valueOf(port), "/home", 0)).start();

        server.join();
    }

}
