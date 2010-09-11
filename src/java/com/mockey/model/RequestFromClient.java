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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * Wraps httpServletRequest and parses out the information we're looking for.
 */
public class RequestFromClient {
	private static final String[] HEADERS_TO_IGNORE = { "content-length", "host", "accept-encoding" };

	// we will ignore the accept-encoding for now to avoid dealing with GZIP
	// responses
	// if we decide to accept GZIP'ed data later, here is an example of how to
	// un-gzip
	// it
	// http://svn.apache.org/repos/asf/httpcomponents/httpclient/trunk/httpclient/src/examples/org/apache/http/examples/client/ClientGZipContentCompression.java

	private Log log = LogFactory.getLog(RequestFromClient.class);
	private HttpServletRequest rawRequest;
	private Map<String, String[]> parameters = new HashMap<String, String[]>();
	private Map<String, List<String>> headers = new HashMap<String, List<String>>();
	private String requestBody;

	public RequestFromClient(HttpServletRequest rawRequest) {
		this.rawRequest = rawRequest;
		try {
			this.rawRequest.setCharacterEncoding(HTTP.ISO_8859_1); // "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parseRequestHeaders();
		parseRequestBody();
		parseParameters();
	}

	public String getRawRequestAsString(Url url) {

		try {
			URI uri = URIUtils.createURI(url.getScheme(), url.getHost(), -1, url.getPath(),
					this.buildParameterRequest(), null);
			return uri.toString();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return "??";

	}

	/**
	 * Copy all necessary data from the request into a POST to the new server
	 * 
	 * @param serviceBean
	 *            the path on the server to POST to
	 * @return A fully populated HttpRequest object
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	public HttpRequest postToRealServer(Url url, String httpMethod) throws URISyntaxException,
			UnsupportedEncodingException {
		// TODO: Cleanup the logic to handle creating a GET vs POST
		HttpRequest request;
		URI uri = URIUtils.createURI(url.getScheme(), url.getHost(), -1, url.getPath(), this.buildParameterRequest(),
				null);
		if (("GET").equalsIgnoreCase(httpMethod)) {
			request = new HttpGet(uri);
		} else {
			HttpPost post = new HttpPost(uri);

			// copy the request body we recieved into the POST
			post.setEntity(constructHttpPostBody());
			request = post;
		}

		// copy the headers into the request to the real server
		for (Map.Entry<String, List<String>> stringListEntry : headers.entrySet()) {
			String name = stringListEntry.getKey();

			// ignore certain headers that httpclient will generate for us
			if (includeHeader(name)) {
				for (String value : stringListEntry.getValue()) {
					request.addHeader(name, value);
					log.info("  Header: " + name + " value: " + value);
				}
			}
		}
		return request;
	}

	private boolean includeHeader(String name) {
		for (String header : HEADERS_TO_IGNORE) {
			if (header.equalsIgnoreCase(name)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Parameter key and value(s).
	 * 
	 * @return
	 */
	public Map<String, String[]> getParameters() {
		return this.parameters;
	}

	/**
	 * 
	 * @return All the parameters as a URL encoded string
	 * @throws UnsupportedEncodingException
	 */
	public String buildParameterRequest() throws UnsupportedEncodingException {
		StringBuffer requestMsg = new StringBuffer();
		// Checking for this case: /someurl?wsdl
		boolean first = true;
		for (String key : parameters.keySet()) {
			String[] values = parameters.get(key);

			if (!first) {
				requestMsg.append("&");
			}
			if (values != null && values.length > 0) {
				for (String value : values) {
					if (value.trim().length() > 0) {
						requestMsg.append(URLEncoder.encode(key, HTTP.UTF_8)).append("=")
								.append(URLEncoder.encode(value, HTTP.UTF_8));
					} else {
						requestMsg.append(URLEncoder.encode(key, HTTP.UTF_8));
					}
				}
			}
			if (first) {
				first = false;
			}

		}
		return requestMsg.toString();
	}

	private void parseRequestHeaders() {
		Enumeration e = rawRequest.getHeaderNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			List<String> values = new ArrayList<String>();
			Enumeration eValues = rawRequest.getHeaders(name);

			while (eValues.hasMoreElements()) {
				String value = (String) eValues.nextElement();
				values.add(value);
			}
			headers.put(name, values);

		}
	}

	private void parseRequestBody() {

		try {
			InputStream is = rawRequest.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			requestBody = sb.toString();

		} catch (IOException e) {
			log.error("Unable to parse body from incoming request", e);
		}

	}

	@SuppressWarnings("unchecked")
	private void parseParameters() {
		parameters = rawRequest.getParameterMap();
	}

	@SuppressWarnings("unchecked")
	public String getHeaderInfo() {
		StringBuffer buf = new StringBuffer();

		Enumeration<String> headerNames = rawRequest.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			buf.append(name).append("=").append(rawRequest.getHeader(name)).append(" \n");
		}
		return buf.toString();
	}

	public String getCookieInfo() {
		StringBuffer buf = new StringBuffer();

		if(rawRequest.getCookies()!=null) {
			for (Cookie cookie : rawRequest.getCookies()) {
	
				buf.append(String.format("Cookie: name=%s, domain=%s, value=%s", cookie.getName(), cookie.getDomain(),
						cookie.getValue()));
			}
		}
		return buf.toString();
	}

	private HttpEntity constructHttpPostBody() {

		HttpEntity body;
		try {
			if (requestBody != null) {
				body = new StringEntity(requestBody);
			} else {
				List<NameValuePair> parameters = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String[]> entry : this.parameters.entrySet()) {
					for (String value : entry.getValue()) {
						parameters.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
				body = new UrlEncodedFormEntity(parameters, HTTP.ISO_8859_1); // .UTF_8);
			}

		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unable to generate a POST from the incoming request", e);
		}

		return body;

	}

	/**
	 * 
	 * @return - true if incoming request is posting a body
	 */
	public boolean hasPostBody() {
		return requestBody != null && requestBody.trim().length() > 0;
	}

	/**
	 * 
	 * @return the body content of this request.
	 */
	public String getBodyInfo() {
		return requestBody;
	}

	/**
	 * 
	 * @return the parameters of this request
	 */
	public String getParameterInfo() {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			builder.append(entry.getKey()).append("=");
			for (String value : entry.getValue()) {
				builder.append(value);
			}
			builder.append("|");
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("---------- Headers ---------\n");
		builder.append(getHeaderInfo());
		builder.append("---------- Cookies ---------\n");
		builder.append(getCookieInfo());
		builder.append("--------- Parameters ------------ \n");
		builder.append(getParameterInfo());
		builder.append("-------- Post BODY --------------\n");
		builder.append(getBodyInfo());
		return builder.toString();
	}
}
