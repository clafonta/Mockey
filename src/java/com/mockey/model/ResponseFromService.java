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
package com.mockey.model;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * Wrapper with print and helper functions for a HTTP response message.
 * 
 * @author chad.lafontaine -
 * 
 */
public class ResponseFromService {

    private static final String[] IGNORE_HEADERS = { "Transfer-Encoding" };

    private Log log = LogFactory.getLog(ResponseFromService.class);
    private String body;
    private boolean valid;
    private String errorMsg;
    private Header[] headers;
    private StatusLine statusLine;

    /**
     * Empty constructor
     */
    public ResponseFromService() {
    }

    /**
     * 
     * @param rsp
     *            - parses the response
     */
    public ResponseFromService(HttpResponse rsp) {
        HttpEntity entity = rsp.getEntity();

        setStatusLine(rsp.getStatusLine());
        headers = rsp.getAllHeaders();
        setHeaders(headers);

        if (entity != null) {
            // System.out.println(EntityUtils.toString(entity));
            try {
                setBody(EntityUtils.toString(entity));
            } catch (IOException e) {
                throw new IllegalStateException("Unable to parse resonse", e);
            }
            setValid(true);
        }

    }

    /**
     * @return the responseMsg
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body
     *            the responseMsg to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @param valid
     *            the valid to set
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * @return the errorMsg
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * @param errorMsg
     *            the errorMsg to set
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public Header[] getHeaders() {
        return headers;
    }

    /**
     * 
     * @return - pretty print header information.
     */
    public String getHeaderInfo() {
        StringBuffer sb = new StringBuffer();
        if (headers != null) {
            for (int i = 0; i < headers.length; i++) {
                Header header = headers[i];
                sb.append(header.getName() + "=" + header.getValue() + "\n");
            }
        }
        return sb.toString();
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void writeToOutput(HttpServletResponse resp) throws IOException {
        // copy the headers out
        if (headers != null) {
            for (Header header : headers) {

                // copy the cookies
                if (ignoreHeader(header.getName())) {
                    log.debug("Ignoring header: " + header.getName());
                } else if (header.getName().equals("Set-Cookie")) {

                    String[] cookieParts = header.getValue().split("=", 2);
                    String cookieBody = cookieParts[1];

                    String[] cookieBodyParts = cookieBody.split("; ");

                    Cookie cookie = new Cookie(cookieParts[0], cookieBodyParts[0]);
                    resp.addCookie(cookie);

                    log.info("Adding header: " + cookieParts[0] + " value: " + cookieBodyParts[0]);
                    log.info("cookie ---> " + cookie.toString());
                } else if (header.getName().equals("Content-Type")) {
                    // copy the content type
                    resp.setContentType(header.getValue());
                } else
                    resp.setHeader(header.getName(), header.getValue());
            }
        }
        if(body!=null){
            byte[] myISO88591asBytes = body.getBytes(HTTP.ISO_8859_1);            
            new PrintStream(resp.getOutputStream()).write(myISO88591asBytes); 
            resp.getOutputStream().flush();    
        }else {
            PrintStream out = new PrintStream(resp.getOutputStream());
            out.println(body);
        }
       

    }

    private boolean ignoreHeader(String name) {
        for (String header : IGNORE_HEADERS) {
            if (header.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
