/*
 * Copyright 2008-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mockey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.mockey.model.ProxyServerModel;
import com.mockey.model.RequestFromClient;
import com.mockey.model.ResponseFromService;
import com.mockey.model.Url;

/**
 * How to send a request via proxy using {@link HttpClient}.
 * 
 * @since 4.0
 */
public class ClientExecuteProxy {
	private Log log = LogFactory.getLog(ClientExecuteProxy.class);

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

	public ResponseFromService execute(ProxyServerModel proxyServer,
			Url realServiceUrl, String httpMethod, RequestFromClient request)
			throws Exception {
		log.info("Request: " + String.valueOf(realServiceUrl));

		// general setup
		SchemeRegistry supportedSchemes = new SchemeRegistry();

		// Register the "http" and "https" protocol schemes, they are
		// required by the default operator to look up socket factories.
		supportedSchemes.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));

		// prepare parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.ISO_8859_1);
		HttpProtocolParams.setUseExpectContinue(params, false);
		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
				supportedSchemes);
		DefaultHttpClient httpclient = new DefaultHttpClient(ccm, params);

		if (proxyServer.isProxyEnabled()) {
			// make sure to use a proxy that supports CONNECT
			httpclient.getCredentialsProvider().setCredentials(
					proxyServer.getAuthScope(), proxyServer.getCredentials());
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxyServer.getHttpHost());
		}

		HttpHost htttphost = new HttpHost(realServiceUrl.getHost(),
				realServiceUrl.getPort(), realServiceUrl.getScheme());

		HttpResponse response = httpclient.execute(htttphost, request
				.postToRealServer(realServiceUrl, httpMethod));

		ResponseFromService responseMessage = new ResponseFromService(response);

		// When HttpClient instance is no longer needed,
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources
		httpclient.getConnectionManager().shutdown();

		// Parse out the response information we're looking for

		return responseMessage;
	}

}
