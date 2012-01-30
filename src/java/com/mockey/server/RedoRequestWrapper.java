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
package com.mockey.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.collections.iterators.IteratorEnumeration;

public class RedoRequestWrapper extends HttpServletRequestWrapper {
    private String body = "";
    private Map parameterMap = new HashMap();
    private Map headerMap = new HashMap();
    private String contentType = "";
    private String requestURI = "";

    public RedoRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getParameter(String name) {
        Object object = this.parameterMap.get(name);

        if (object != null) {
            return object.toString();
        } else {
            return null;
        }

    }
    
    public String getHeader(String name){
        Object arg = this.headerMap.get(name);
        if(arg!=null){
            return arg.toString();
        }else {
            return null;
        }
    }
    
    public Enumeration  getHeaderNames(){
        return new IteratorEnumeration(headerMap.keySet().iterator());
    }
    
    public Enumeration  getHeaders(String name){
        Object object = this.headerMap.get(name);
        if(object==null){
            return null;
        }else if(object instanceof String[]){
            List<String> array = new ArrayList<String>();
            String[] g = (String[]) object;
            for (int i = 0; i < g.length; i++) {
                array.add(g[i]);
            }
            return new IteratorEnumeration(array.iterator());
        } else {
            List<String> list = new ArrayList<String>();
            list.add((String)object);
            return new IteratorEnumeration(list.iterator());
        } 
    }
    
    public void addHeader(String name, String value){
        Object object = this.headerMap.get(name);
        if (object == null) {
            this.headerMap.put(name, value);
        } else if (object instanceof String[]) {
            List<String> array = new ArrayList<String>();
            String[] g = (String[]) object;
            for (int i = 0; i < g.length; i++) {
                array.add(g[i]);
            }
            array.add(value);
            this.headerMap.put(name, array.toArray(new String[array.size()]));
        }
    }
    
    

    public Map getParameterMap() {
        return this.parameterMap;
    }

    public Enumeration getParameterNames() {

        return new IteratorEnumeration(parameterMap.keySet().iterator());

    }
    
    public String getRequestURI(){
        return requestURI;
    }

    
    public void setRequestURI(String requestURI){
        this.requestURI = requestURI;
    }
    
    public void addParameter(String name, String value) {
        List<String> array = new ArrayList<String>();
        String[] objectArray = (String[])this.parameterMap.get(name);
        if (objectArray != null) {
            String[] g = (String[]) objectArray;
            for (int i = 0; i < g.length; i++) {
                array.add(g[i]);
            }
        }
        array.add(value);
        this.parameterMap.put(name, array.toArray(new String[array.size()]));
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        ServletInputStream servletInputStream = new ServletInputStream() {
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
