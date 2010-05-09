package com.mockey.runner;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ClassPathResourceHandler extends ContextHandler {
    private ResourceHandler realResourceHandler = null;

    public ClassPathResourceHandler() {
        realResourceHandler = new ResourceHandlerImplementation();
    }

    @Override
    public void doHandle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        realResourceHandler.handle(s,request,httpServletRequest,httpServletResponse);
    }

    private class ResourceHandlerImplementation extends ResourceHandler {

        @Override
        protected Resource getResource(HttpServletRequest httpServletRequest) throws MalformedURLException {
            String requestedFile = httpServletRequest.getRequestURI();

            URL path = getClass().getResource(requestedFile);

            try {
                Resource resource = Resource.newResource(path);
                if(resource != null && resource.exists() && !resource.isDirectory()) {
                    return resource;
                }else{
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
        }
    }
}
