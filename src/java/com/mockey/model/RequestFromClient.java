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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * Wraps httpServletRequest and parses out the information we're looking for.
 */
public class RequestFromClient {

	/**
	 * We will ignore the accept-encoding for now to avoid dealing with GZIP
	 * responses if we decide to accept GZIP'ed data later, here is an example
	 * of how to un-gzip it:
	 * 
	 * <pre>
	 * http://svn.apache.org/repos/asf/httpcomponents/httpclient/trunk/httpclient/src/examples/org/apache/http/examples/client/ClientGZipContentCompression.java
	 * </pre>
	 * 
	 */
	public static final String[] HEADERS_TO_IGNORE = { "content-length",
			"host", "accept-encoding", "cookie" };

	private Log log = LogFactory.getLog(RequestFromClient.class);
	private List<Cookie> httpClientCookies = new ArrayList<Cookie>();
	private Map<String, String[]> parameters = new HashMap<String, String[]>();
	private Map<String, List<String>> headers = new HashMap<String, List<String>>();
	private String requestBody;
	private String method;
	private String fullURL;

	/**
	 * Initialization will extract Headers, Body, Parameters, and Cookies from
	 * the raw HTTP request. Note: This class will <i>_ignore_</i> some header
	 * information. See <code>HEADERS_TO_IGNORE</code>
	 * 
	 * @param rawRequest
	 */
	public RequestFromClient(HttpServletRequest rawRequest) {
		try {
			rawRequest.setCharacterEncoding(HTTP.ISO_8859_1); // "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		this.method = rawRequest.getMethod();
		this.fullURL = rawRequest.getRequestURL().toString();
		parseRequestHeaders(rawRequest);
		parseRequestBody(rawRequest);
		parseParameters(rawRequest);
		parseCookies(rawRequest);
	}

	public List<Cookie> getHttpClientCookies() {
		return this.httpClientCookies;
	}

	public String getRequestURL() {
		return this.fullURL;
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
	public HttpRequest postToRealServer(Url url) throws URISyntaxException,
			UnsupportedEncodingException {
		// TODO: Cleanup the logic to handle creating a GET vs POST
		HttpRequest request;
		String urlQuery = this.buildParameterRequest();
		if (urlQuery.length() == 0) {
			// If the query is empty, pass a null query to URIUtils.createURI(),
			// as an empty string
			// causes URIUtils.createURI() to append a ? to the URI.
			urlQuery = null;
		}
		URI uri = URIUtils.createURI(url.getScheme(), url.getHost(), -1,
				url.getPath(), urlQuery, null);

		if (("GET").equalsIgnoreCase(this.method)) {
			request = new HttpGet(uri);
		} else {
			HttpPost post = new HttpPost(uri);

			// copy the request body we received into the POST
			post.setEntity(constructHttpPostBody());
			request = post;

		}

		// copy the headers into the request to the real server
		for (Map.Entry<String, List<String>> stringListEntry : headers
				.entrySet()) {
			String name = stringListEntry.getKey();

			// ignore certain headers that httpclient will generate for us
			if (shouldIncludeHeader(name)) {
				for (String value : stringListEntry.getValue()) {
					request.addHeader(name, value);
				}
			}
		}

		/*
		 * If the port is the default one for the scheme, force HttpClient to
		 * not set it in the Host header. By default, HttpClient always
		 * specifies the port in the Host header, even if it's the default one -
		 * e.g., "Host: www.amazon.com:443". Some web servers do not like that.
		 */
		if (url.isDefaultPort()) {
			request.getParams().setParameter(ClientPNames.VIRTUAL_HOST,
					new HttpHost(url.getHost()));
		}

		return request;
	}

	private boolean shouldIncludeHeader(String name) {
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
						requestMsg.append(URLEncoder.encode(key, HTTP.UTF_8))
								.append("=")
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

	private void parseRequestHeaders(HttpServletRequest rawRequest) {

		// Put header information coming from client.
		Enumeration<String> e = rawRequest.getHeaderNames();

		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			// Let's ignore some headers
			if (this.shouldIncludeHeader(name)) {
				List<String> values = new ArrayList<String>();
				Enumeration<String> eValues = rawRequest.getHeaders(name);

				while (eValues.hasMoreElements()) {
					String value = (String) eValues.nextElement();
					values.add(value);
				}

				headers.put(name, values);
			}
		}
		// Override header information to prevent CACHING
		// As of 4/29/2011, updated Apache HttpClient. Result was the
		// following:
		// Testing with MAMP (Apache 2.0.63), I was seeing
		// this parameter being sent by Browsers Firefox 4 and
		// and Chrome 9, but NOT Safari 5.
		// To prevent caching, removing this attribute.
		e = rawRequest.getHeaderNames();
		List<String> p = new ArrayList<String>();
		p.add("Fri, 13 May 2006 23:54:18 GMT");
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();

			if ("if-none-match".equalsIgnoreCase(name)) {
				headers.remove(name);
			} else if ("If-modified-Since".equalsIgnoreCase(name)) {
				headers.put(name, p);
			}

		}

	}

	/**
	 * an org.apache.commons.httpclient.Cookie is NOT a
	 * javax.servlet.http.Cookie - and it looks like the two don't map onto each
	 * other without data loss...
	 * */

	private void parseCookies(HttpServletRequest rawRequest) {
		javax.servlet.http.Cookie[] cookies = rawRequest.getCookies();
		if (cookies != null) {
			// ******************
			// This doesn't seem right.
			// We have to map javax Cookies to httpclient Cookies?!?!
			//
			// ******************
			for (int i = 0; i < cookies.length; i++) {
				javax.servlet.http.Cookie c = cookies[i];
				String domain = c.getDomain();
				if (domain == null) {
					domain = rawRequest.getServerName();
				}
				String cpath = c.getPath();
				if (cpath == null) {
					cpath = rawRequest.getContextPath();
				}
				// Commented out; too much noise.
				// log.info("Cookie from client: " + c.getName() + " = " +
				// c.getValue());
				BasicClientCookie basicClientCookie = new BasicClientCookie(
						c.getName(), c.getValue());
				basicClientCookie.setDomain(domain);
				if (c.getMaxAge() > -1) {
					int seconds = c.getMaxAge();
					long currentTime = System.currentTimeMillis();
					Date expiryDate = new Date(currentTime + (seconds * 1000));
					basicClientCookie.setExpiryDate(expiryDate);
				}
				this.httpClientCookies.add(basicClientCookie);
			}
		}

	}

	private void parseRequestBody(HttpServletRequest rawRequest) {

		try {
			InputStream is = rawRequest.getInputStream();
			byte[] buffer = new byte[1024];
			StringBuilder sb = new StringBuilder();

			try {
				for (;;) {
					int bytesRead = is.read(buffer);
					if (bytesRead == -1) {
						break;
					}
					sb.append(new String(buffer, 0, bytesRead));
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

	private void parseParameters(HttpServletRequest rawRequest) {
		parameters = rawRequest.getParameterMap();
	}

	/**
	 * 
	 * @return readeable string output of header
	 */
	public String getHeaderInfo() {
		StringBuffer buf = new StringBuffer();

		for (String headerName : headers.keySet()) {
			buf.append(headerName + "\n");
			for (String headerValue : headers.get(headerName)) {
				buf.append("    " + headerValue + "\n");
			}
		}
		return buf.toString();
	}

	/**
	 * 
	 * @return 
	 */
	public Map<String, String[]> getHeaderInfoAsMap() {

		Map<String, String[]> headerMap = new HashMap<String, String[]>();

		for (String headerName : headers.keySet()) {
			List<String> arg = headers.get(headerName);
			headerMap.put(headerName, arg.toArray(new String[arg.size()]));

		}
		return headerMap;
	}

	public String getMethod() {
		return this.method;
	}

	public String getCookieInfoAsString() {
		StringBuffer buf = new StringBuffer();

		for (Cookie cookie : this.httpClientCookies) {

			buf.append(cookie.toString() + "\n\n");
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
				for (Map.Entry<String, String[]> entry : this.parameters
						.entrySet()) {
					for (String value : entry.getValue()) {
						parameters.add(new BasicNameValuePair(entry.getKey(),
								value));
					}
				}
				body = new UrlEncodedFormEntity(parameters, HTTP.ISO_8859_1); // .UTF_8);
			}

		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(
					"Unable to generate a POST from the incoming request", e);
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
		builder.append(getCookieInfoAsString());
		builder.append("--------- Parameters ------------ \n");
		builder.append(getParameterInfo());
		builder.append("-------- Post RULE_FOR_BODY --------------\n");
		builder.append(getBodyInfo());
		return builder.toString();
	}
}
