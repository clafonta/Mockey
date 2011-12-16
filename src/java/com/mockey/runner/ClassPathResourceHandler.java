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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

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
