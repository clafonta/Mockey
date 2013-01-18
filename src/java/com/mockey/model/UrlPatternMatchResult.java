/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2013  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
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

/**
 * Result of comparing real URLs to Mockey Service's mock-URLs, which may result
 * in a token value if a RESTful URL is evaluated.
 * 
 * @author clafonta
 * 
 */
public class UrlPatternMatchResult {

	private String restTokenId = null;
	private boolean matchingUrlPattern = false;

	/**
	 * 
	 * @return may be null, empty, or non-empty string.
	 * @see #hasTokenId()
	 */
	public String getRestTokenId() {

		return restTokenId;

	}

	/**
	 * 
	 * @return true if non-null, non-empty string, false otherwise.
	 */
	public boolean hasTokenId() {
		if (this.restTokenId != null && this.restTokenId.trim().length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param restTokenId
	 */
	public void setRestTokenId(String restTokenId) {

		this.restTokenId = restTokenId;

	}

	/**
	 * 
	 * @return default value is false.
	 */
	public boolean isMatchingUrlPattern() {

		return matchingUrlPattern;

	}

	/**
	 * 
	 * @param matchingUrlPattern
	 */
	public void setMatchingUrlPattern(boolean match) {

		this.matchingUrlPattern = match;

	}

}
