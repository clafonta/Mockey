package com.mockey;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import org.apache.http.util.EntityUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mockey.web.ResponseMessage;

/**
 * How to send a request via proxy using {@link HttpClient}.
 * 
 * @since 4.0
 */
public class ClientExecuteProxy {
    private Log log = LogFactory.getLog(ClientExecuteProxy.class);

	public static void main(String[] args) throws Exception {
		ProxyServer proxyInfoBean = new ProxyServer();
		proxyInfoBean.setProxyEnabled(true);
		proxyInfoBean.setProxyPassword("YOUR_PROXY_PASSWORD_HERE");
		proxyInfoBean.setProxyPort(8080); // YOUR_PROXY_PORT
		proxyInfoBean.setProxyUrl("YOUR_PROXY_URL_HERE");
		proxyInfoBean.setProxyUsername("YOUR_PROXY_USERNAME_HERE");
		proxyInfoBean.setProxyScheme("http");
		MockServiceBean serviceBean = new MockServiceBean();
		serviceBean.setRealServiceScheme("https");
		serviceBean.setRealServiceUrl("issues.apache.org");
		// serviceBean.sets
		ClientExecuteProxy p = new ClientExecuteProxy();
		ResponseMessage rm = p.execute(proxyInfoBean, serviceBean, "");
		System.out.println("executing request to " + serviceBean.getRealServiceUrl() + " via " + proxyInfoBean.getProxyUrl());
		System.out.println("----------------------------------------");
		System.out.println(rm.getStatusLine());
		Header[] headers = rm.getHeaders();

		for (int i = 0; i < headers.length; i++) {
			System.out.println(headers[i]);
		}
		System.out.println("----------------------------------------");
		;
		System.out.println(rm.getResponseMsg());

	}

	public ResponseMessage execute(ProxyServer proxyInfo, MockServiceBean serviceBean, String requestMsg) throws Exception {
        log.info("Request: " + String.valueOf(serviceBean));


        ResponseMessage responseMessage = new ResponseMessage();
        // make sure to use a proxy that supports CONNECT

        HttpHost target = new HttpHost(serviceBean.getRealHost(), 443, serviceBean.getRealServiceScheme());
        HttpHost proxy = new HttpHost(proxyInfo.getProxyUrl(), proxyInfo.getProxyPort(), proxyInfo.getProxyScheme());

        // general setup
        SchemeRegistry supportedSchemes = new SchemeRegistry();

        // Register the "http" and "https" protocol schemes, they are
        // required by the default operator to look up socket factories.
        supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        supportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        // prepare parameters
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUseExpectContinue(params, true);

        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, supportedSchemes);

        DefaultHttpClient httpclient = new DefaultHttpClient(ccm, params);
        httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(proxyInfo.getProxyUrl(), proxyInfo.getProxyPort()),
                new UsernamePasswordCredentials(proxyInfo.getProxyUsername(), proxyInfo.getProxyPassword()));

        httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);


        HttpResponse rsp = null;
        if (serviceBean.getHttpMethod().equals("GET")) {
            HttpGet req = new HttpGet(serviceBean.getRealPath());
            rsp = httpclient.execute(target, req);
        }else{
            HttpPost post = new HttpPost(serviceBean.getRealPath());
            StringEntity body = new StringEntity(requestMsg);
            post.setEntity(body);

            rsp = httpclient.execute(target, post);
        }


        HttpEntity entity = rsp.getEntity();

        responseMessage.setStatusLine(rsp.getStatusLine());
        Header[] headers = rsp.getAllHeaders();
        responseMessage.setHeaders(headers);

        if (entity != null) {
            // System.out.println(EntityUtils.toString(entity));
            responseMessage.setResponseMsg(EntityUtils.toString(entity));
            responseMessage.setValid(true);
        }

        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();

        log.info("Response: "+responseMessage);
        return responseMessage;
    }

}
