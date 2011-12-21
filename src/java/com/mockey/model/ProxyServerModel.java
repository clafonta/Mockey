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

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;

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
		String username = getProxyUsername();
		String pass = getProxyPassword();
		if (username == null) {
			username = "";
		}
		if (pass == null) {
			username = "";
		}
		// Can't pass null
		return new UsernamePasswordCredentials(username, pass);
	}

	/**
	 * Convenience method to see if there are any proxy model settings.
	 * 
	 * @return false if all attributes of this instance are null or empty, true
	 *         otherwise
	 */
	public boolean hasSettings() {
		boolean yesSomeSettingExists = false;
		if(this.proxyUrl!=null && this.proxyUrl.hasSettings()){
			yesSomeSettingExists = true;
		}else if(this.proxyUsername!=null && this.proxyUsername.trim().length() > 0){
			yesSomeSettingExists = true;
		}else if(this.proxyPassword!=null && this.proxyPassword.trim().length() > 0){
			yesSomeSettingExists = true;
		}else {
			yesSomeSettingExists = false;
		}
		return yesSomeSettingExists;
	}
}
