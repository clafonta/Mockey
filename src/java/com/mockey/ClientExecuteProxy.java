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
package com.mockey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.mockey.model.ProxyServerModel;
import com.mockey.model.RequestFromClient;
import com.mockey.model.ResponseFromService;
import com.mockey.model.Url;

/**
 * How to send a request via proxy using {@link HttpClient}.
 * 
 * @since 4.1
 */
public class ClientExecuteProxy {

	/**
	 * Shared, thread-safe cookie store needed to support sticky session over
	 * multiple client proxy executions.
	 */
	private static CookieStore cookieStore = new BasicCookieStore();
	private Log log = LogFactory.getLog(ClientExecuteProxy.class);

	/**
	 * 
	 * @return a new Client
	 */
	public static ClientExecuteProxy getClientExecuteProxyInstance() {

		return new ClientExecuteProxy();
	}

	public static void resetStickySession() {
		ClientExecuteProxy.cookieStore.clear();
	}

	private ClientExecuteProxy() {
	}

	/**
	 * 
	 * @param twistInfo
	 * @param proxyServer
	 * @param realServiceUrl
	 * @param httpMethod
	 * @param request
	 * @return
	 * @throws ClientExecuteProxyException
	 */
	public ResponseFromService execute(ProxyServerModel proxyServer, Url realServiceUrl, boolean allowRedirectFollow,
			RequestFromClient request) throws ClientExecuteProxyException {
		log.info("Request: " + String.valueOf(realServiceUrl));

		// general setup
		SchemeRegistry supportedSchemes = new SchemeRegistry();

		// Register the "http" and "https" protocol schemes, they are
		// required by the default operator to look up socket factories.
		supportedSchemes.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		supportedSchemes.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		// prepare parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.ISO_8859_1);
		HttpProtocolParams.setUseExpectContinue(params, false);

		ClientConnectionManager ccm = new ThreadSafeClientConnManager(supportedSchemes);
		DefaultHttpClient httpclient = new DefaultHttpClient(ccm, params);

		if (!allowRedirectFollow) {
			// Do NOT allow for 302 REDIRECT
			httpclient.setRedirectStrategy(new DefaultRedirectStrategy() {
				public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) {
					boolean isRedirect = false;
					try {
						isRedirect = super.isRedirected(request, response, context);
					} catch (ProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (!isRedirect) {
						int responseCode = response.getStatusLine().getStatusCode();
						if (responseCode == 301 || responseCode == 302) {
							return true;
						}
					}
					return isRedirect;
				}
			});
		} else {
			// Yes, allow for 302 REDIRECT
			// Nothing needed here.
		}

		// Prevent CACHE, 304 not modified
		// httpclient.addRequestInterceptor(new HttpRequestInterceptor() {
		// public void process(final HttpRequest request, final HttpContext
		// context) throws HttpException, IOException {
		//
		// request.setHeader("If-modified-Since",
		// "Fri, 13 May 2006 23:54:18 GMT");
		//
		// }
		// });

		// Use shared cookie store
		httpclient.setCookieStore(ClientExecuteProxy.cookieStore);

		StringBuffer requestCookieInfo = new StringBuffer();
		// Show what cookies are in the store .
		for (Cookie cookie : ClientExecuteProxy.cookieStore.getCookies()) {
			log.debug("Cookie in the cookie STORE: " + cookie.toString());
			requestCookieInfo.append(cookie.toString() + "\n\n\n");

		}

		if (proxyServer.isProxyEnabled()) {
			// make sure to use a proxy that supports CONNECT
			httpclient.getCredentialsProvider()
					.setCredentials(proxyServer.getAuthScope(), proxyServer.getCredentials());
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyServer.getHttpHost());
		}

		ResponseFromService responseMessage = null;
		try {
			HttpHost htttphost = new HttpHost(realServiceUrl.getHost(), realServiceUrl.getPort(),
					realServiceUrl.getScheme());

			HttpResponse response = httpclient.execute(htttphost, request.postToRealServer(realServiceUrl));
			if (response.getStatusLine().getStatusCode() == 302) {
				log.debug("FYI: 302 redirect occuring from " + realServiceUrl.getFullUrl());
			}
			responseMessage = new ResponseFromService(response);
			responseMessage.setRequestUrl(realServiceUrl);
		} catch (Exception e) {
			log.error(e);
			throw new ClientExecuteProxyException("Unable to retrieve a response. ", realServiceUrl, e);
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}

		// Parse out the response information we're looking for
		// StringBuffer responseCookieInfo = new StringBuffer();
		// // Show what cookies are in the store .
		// for (Cookie cookie : ClientExecuteProxy.cookieStore.getCookies()) {
		// log.info("Cookie in the cookie STORE: " + cookie.toString());
		// responseCookieInfo.append(cookie.toString() + "\n\n\n");
		//
		// }
		// responseMessage.setRequestCookies(requestCookieInfo.toString());
		// responseMessage.setResponseCookies(responseCookieInfo.toString());
		return responseMessage;
	}

	// public static void main(String[] args) throws Exception {
	// ProxyServer proxyInfoBean = new ProxyServer();
	// proxyInfoBean.setProxyEnabled(true);
	// proxyInfoBean.setProxyPassword("YOUR_PROXY_PASSWORD_HERE");
	// proxyInfoBean.setProxyPort(8080); // YOUR_PROXY_PORT
	// proxyInfoBean.setProxyUrl("YOUR_PROXY_URL_HERE");
	// proxyInfoBean.setProxyUsername("YOUR_PROXY_USERNAME_HERE");
	// proxyInfoBean.setProxyScheme("http");
	// MockServiceBean serviceBean = new MockServiceBean();
	//
	// serviceBean.setRealServiceUrl("https://issues.apache.org");
	// // serviceBean.sets
	// ClientExecuteProxy p = new ClientExecuteProxy();
	// ResponseMessage rm = p.execute(proxyInfoBean, serviceBean, null);
	// System.out.println("executing request to " +
	// serviceBean.getRealServicePath() + " via " +
	// proxyInfoBean.getProxyUrl());
	// System.out.println("----------------------------------------");
	// System.out.println(rm.getStatusLine());
	// Header[] headers = rm.getHeaders();
	//
	// for (int i = 0; i < headers.length; i++) {
	// System.out.println(headers[i]);
	// }
	// System.out.println("----------------------------------------");
	// ;
	// System.out.println(rm.getBody());
	//
	// }
}
