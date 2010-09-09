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
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.mockey.model.ProxyServerModel;
import com.mockey.model.RequestFromClient;
import com.mockey.model.ResponseFromService;
import com.mockey.model.TwistInfo;
import com.mockey.model.Url;

/**
 * How to send a request via proxy using {@link HttpClient}.
 * 
 * @since 4.0
 */
public class ClientExecuteProxy {
	
	// Static cookie store for sessions
	private static CookieStore cookieStore = null;
	private Log log = LogFactory.getLog(ClientExecuteProxy.class);
	
	
	/**
	 * 
	 * @return a new Client 
	 */
	public static ClientExecuteProxy getClientExecuteProxyInstance() {
		
		return new ClientExecuteProxy();
	}
	
	/**
	 * Cookie store can be null, otherwise it is needed to support sticky session over multiple 
	 * client proxy executions. 
	 */
	public static void resetStickySession(){
		ClientExecuteProxy.cookieStore = null;
	}
	


	private ClientExecuteProxy(){
	}
	public ResponseFromService execute(TwistInfo twistInfo, ProxyServerModel proxyServer, Url realServiceUrl, String httpMethod,
			RequestFromClient request) throws Exception {
		log.info("Request: " + String.valueOf(realServiceUrl));

		// general setup
		SchemeRegistry supportedSchemes = new SchemeRegistry();

		// Register the "http" and "https" protocol schemes, they are
		// required by the default operator to look up socket factories.
		supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		// prepare parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.ISO_8859_1);
		HttpProtocolParams.setUseExpectContinue(params, false);
		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, supportedSchemes);
		DefaultHttpClient httpclient = new DefaultHttpClient(ccm, params);
		
		
		if(ClientExecuteProxy.cookieStore == null) {
			cookieStore = httpclient.getCookieStore();
		}else {
			httpclient.setCookieStore(cookieStore);
		}
		
		// Show what cookies are in the store. 
		for(Cookie cookie: ClientExecuteProxy.cookieStore.getCookies()){
			log.info("Cookie in the cookie STORE: " + cookie.toString());
		}
		
		if (proxyServer.isProxyEnabled()) {
			// make sure to use a proxy that supports CONNECT
			httpclient.getCredentialsProvider()
					.setCredentials(proxyServer.getAuthScope(), proxyServer.getCredentials());
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyServer.getHttpHost());
		}

		// TWISTING
		Url originalRequestUrlBeforeTwisting = null;
		if(twistInfo!=null){
			String fullurl = realServiceUrl.getFullUrl();
			String twistedUrl = twistInfo.getTwistedValue(fullurl);
			if(twistedUrl!=null){
				originalRequestUrlBeforeTwisting = realServiceUrl;
				realServiceUrl = new Url(twistedUrl);
			}
		}
		HttpHost htttphost = new HttpHost(realServiceUrl.getHost(), realServiceUrl.getPort(),
				realServiceUrl.getScheme());

		HttpResponse response = httpclient.execute(htttphost, request.postToRealServer(realServiceUrl, httpMethod));

		ResponseFromService responseMessage = new ResponseFromService(response);
		
		responseMessage.setOriginalRequestUrlBeforeTwisting(originalRequestUrlBeforeTwisting);
		responseMessage.setRequestUrl(realServiceUrl);
		// When HttpClient instance is no longer needed,
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources
		httpclient.getConnectionManager().shutdown();

		// Parse out the response information we're looking for

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
