package com.mockey.model;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.Credentials;


/**
 * Defines a proxy server to communicate with
 */
public class ProxyServerModel {

	private boolean proxyEnabled = false;
    private Url proxyUrl;
	private String proxyUsername;
	private String proxyPassword;


	public boolean isProxyEnabled() {
		return proxyEnabled;
	}

	public void setProxyEnabled(boolean proxyEnabled) {
		this.proxyEnabled = proxyEnabled;
	}

	public String getProxyHost() {
		return proxyUrl.getHost();
	}

	public void setProxyUrl(String proxyUrl) {
		this.proxyUrl = new Url(proxyUrl);
	}

    public String getProxyUrl() {
        return proxyUrl != null ? proxyUrl.toString() : null;
    }

    public int getProxyPort() {
		return proxyUrl.getPort();
	}

	public String getProxyUsername() {
		return this.proxyUsername;
	}

	public void setProxyUsername(String username) {
		this.proxyUsername = username;
	}

	public void setProxyPassword(String password) {
		this.proxyPassword = password;
	}

	public String getProxyPassword() {
		return this.proxyPassword;
	}

	public String getProxyScheme() {
		return proxyUrl.getScheme();
	}

    public HttpHost getHttpHost() {
        return new HttpHost(getProxyHost(), getProxyPort(), getProxyScheme());
    }
    public AuthScope getAuthScope() {
        return new AuthScope(getProxyHost(), getProxyPort());
    }
    
    public Credentials getCredentials() {
        return new UsernamePasswordCredentials(getProxyUsername(), getProxyPassword());
    }
}
