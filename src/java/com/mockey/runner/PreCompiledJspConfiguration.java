package com.mockey.runner;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class PreCompiledJspConfiguration extends WebXmlConfiguration {

    @Override
    public Resource findWebXml(WebAppContext webAppContext) throws IOException, MalformedURLException {
        URL path = getClass().getClassLoader().getResource("WEB-INF/web.xml");
        return  Resource.newResource(path);
    }
}