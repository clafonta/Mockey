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

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * A Url is the specific http url of a Service.
 * 
 */
public class Url {
	/**
	 * The name of the mock service servlet name
	 */
	public static final String MOCK_SERVICE_PATH = "/service/";
	private Logger logger = Logger.getLogger(Url.class);
	private String scheme;
	private int port = 80;
	private String host;
	private String path;

	/**
	 * @return the scheme
	 */
	public String getScheme() {
		return scheme;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path != null && path.trim() != "" ? path : "/";
	}

	/**
	 * Constructor will parse the url argument to determine the port, host, and
	 * scheme (http or https).
	 * 
	 * @param url
	 */
	public Url(String url) {

		parse(url);
	}

	/**
	 * Empty constructor, needed for reflection but not useful unless parse
	 * method is called shortly after.
	 * 
	 */
	public Url() {

	}

	/**
	 * Calls parse. Method setter used in reflection.
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.parse(url);
	}

	/**
	 * If https or http cannot be determined, then HTTP will be the default. If
	 * path cannot be determined, then 80 for HTTP scheme is default, 443 for
	 * HTTPS.
	 * 
	 * @param url
	 * @throws UrlException
	 */
	public void parse(String url) {

		int beginIndex = url.indexOf(MOCK_SERVICE_PATH);
		if (beginIndex > -1) {
			url = url.substring(MOCK_SERVICE_PATH.length());

		}

		// Get rid of the garbage prior to the http.
		int indexStartOfHttp = url.indexOf("http");
		if (indexStartOfHttp > -1) {
			url = url.substring(indexStartOfHttp);
		}

		// extract the scheme
		if (url.matches("(?i)^https?://.*")) {
			this.scheme = url.substring(0, url.indexOf(":"));
			url = url.substring(url.indexOf("://") + 3, url.length());
		} else {
			this.scheme = "http";
		}

		// extract the host and port
		String hostAndPort;
		if (url.indexOf("/") > 0) {
			hostAndPort = url.substring(0, url.indexOf("/"));
		} else {
			hostAndPort = url;
		}
		if (hostAndPort.indexOf(":") > 0) {
			this.host = hostAndPort.substring(0, hostAndPort.indexOf(":"));
			String portArg = hostAndPort.substring(hostAndPort.indexOf(":") + 1, hostAndPort.length());
			try {
				this.port = Integer.valueOf(portArg);
			} catch (Exception e) {
				logger.debug(
						"Unable to determine port for URL: " + hostAndPort + "\n Setting to default based on scheme. ");
				if (this.scheme.equalsIgnoreCase("https")) {
					this.port = 443;
				} else {
					this.port = 80;
				}
			}
		} else {
			this.host = hostAndPort;
			if (this.scheme.equalsIgnoreCase("https")) {
				this.port = 443;
			} else {
				this.port = 80;
			}
		}

		if (url.indexOf("/") > 0) {
			this.path = url.substring(url.indexOf("/"), url.length());
		} else {
			this.path = "";
		}
		this.path = this.path.trim();

		// scheme and port are not case sensitive so normalize to lowercase
		this.scheme = this.scheme.toLowerCase();
		this.host = this.host.toLowerCase();
	}

	/**
	 * 
	 * @return true if port equates to default value based on scheme, e.g. 443
	 *         and 80 for HTTPS and HTTP respectively.
	 */
	public boolean isDefaultPort() {
		return ("https".equals(scheme) && 443 == port) || ("http".equals(scheme) && 80 == port);
	}

	/**
	 * Returns the URL: scheme and host, and optionally, the port. Only
	 * non-default HTTPS and HTTP port values will be appended to the URL.
	 * 
	 * @return
	 */
	public String getFullUrl() {
		StringBuilder builder = new StringBuilder();
		if (scheme != null && host != null && host.trim().length() > 0) {
			builder.append(scheme).append("://").append(host);
			if (!isDefaultPort()) {
				builder.append(":").append(port);
			}
		}
		if (path != null) {
			builder.append(path);
		}
		return builder.toString();
	}

	public String toString() {
		return getFullUrl();
	}

	/**
	 * Why build an absolute path, and not just use relative?
	 * 
	 * Here's why:
	 * 
	 * <pre>
	 * https://stackoverflow.com/questions/18891210/how-to-reproduce-sendredirect-issue-https-change-to-http
	 * </pre>
	 * 
	 * @param servletPath
	 *            - servletPath
	 * @return - returns absolute URL to the servletPath.
	 */
	public static String getAbsoluteURL(HttpServletRequest req, String servletPath) {

		String scheme = req.getScheme(); // http
		String serverName = req.getServerName(); // hostname.com
		int serverPort = req.getServerPort(); // 80
		String contextPath = req.getContextPath(); // /mywebapp

		// Reconstruct original requesting URL
		StringBuilder url = new StringBuilder();
		url.append(scheme).append("://").append(serverName);

		if (serverPort != 80 && serverPort != 443) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath).append(servletPath);

		return url.toString();
	}

	/**
	 * Given a valid URL with query and reference arguments, this method will
	 * extract remove those values and return a value with
	 * SCHEME://HOST[:PORT]//PATH
	 * 
	 * @param validUrl
	 * @return
	 * @throws MalformedURLException
	 */
	public static String getSchemeHostPortPathFromURL(String validUrl) throws MalformedURLException {
		URL aURL = new URL(validUrl);
		String port = "";
		if (aURL.getPort() > -1) {
			port = ":" + aURL.getPort();
		}
		String schemeHostPortPath = aURL.getProtocol() + "://" + aURL.getHost() + port + aURL.getPath();
		return schemeHostPortPath;
	}

	/**
	 * Convenience method. When did this get here?
	 * 
	 * @return
	 */
	public boolean hasSettings() {
		if (this.getFullUrl() != null && this.getFullUrl().trim().length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		// What's this?
		// URL not Url?
		// Ah...we're testing URL for help here.
		URL aURL;
		try {
			Url b = new Url();

			System.out.println(b.hasSettings());
			b = new Url("http://www.google.com");
			System.out.println(b.hasSettings());
			b = new Url("");
			System.out.println(b.hasSettings() + " '" + b.toString() + "'");

			aURL = new URL("http://java.sun.com:80/docs/books/tutorial/index.html////?");

			System.out.println("protocol = " + aURL.getProtocol());
			System.out.println("authority = " + aURL.getAuthority());
			System.out.println("host = " + aURL.getHost());
			System.out.println("port = " + aURL.getPort());
			System.out.println("path = " + aURL.getPath());
			System.out.println("query = " + aURL.getQuery());
			System.out.println("filename = " + aURL.getFile());
			System.out.println("ref = " + aURL.getRef());
			System.out.println("As a string = " + aURL.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
