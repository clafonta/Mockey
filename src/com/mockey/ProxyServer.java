package com.mockey;


/**
 * Defines a proxy server to communicate with
 */
public class ProxyServer {

	private boolean proxyEnabled;
	private String proxyUrl;
	private int proxyPort;
	private String proxyUsername;
	private String proxyPassword;
	private String proxyScheme;

	public boolean isProxyEnabled() {
		return proxyEnabled;
	}

	public void setProxyEnabled(boolean proxyEnabled) {
		this.proxyEnabled = proxyEnabled;
	}

	public String getProxyUrl() {
		return proxyUrl;
	}

	public void setProxyUrl(String proxyUrl) {
		this.proxyUrl = proxyUrl;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
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

	public void setProxyScheme(String scheme) {
		this.proxyScheme = scheme;
	}

	public String getProxyScheme() {
		return proxyScheme;
	}
}
