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

import org.apache.log4j.Logger;

public class UrlUtil {
	private static Logger logger = Logger.getLogger(UrlUtil.class);

	/**
	 * As example:
	 * 
	 * Evaluating:
	 * 
	 * <pre>
	 * url: "http://someservice.com/customer"
	 * patttern: "http://someservice.com/customer"
	 * </pre>
	 * 
	 * Results in a match==true, token==null.
	 * 
	 * Evaluating:
	 * 
	 * <pre>
	 * url: "http://someservice.com/customer/33"
	 * patttern: "http://someservice.com/customer/{ID}"
	 * </pre>
	 * 
	 * Results in match==true, token == '33'
	 * 
	 * Evaluating:
	 * 
	 * <pre>
	 * url: "http://someservice.com/customer"
	 * patttern: "http://someservice.com/customerssss"
	 * </pre>
	 * 
	 * Results in match==false, token == null
	 * 
	 * @param url
	 *            - URL to evaluate
	 * @param urlPattern
	 *            - pattern to evaluate. If contains '{' and '}' characters,
	 *            then it will be considered a RESTful pattern and extract the
	 *            value from 'url' if a successful pattern match.
	 * @return non null result, which may contain a token value if url is
	 *         RESTful in nature, based on the urlPattern
	 */
	public static UrlPatternMatchResult evaluateUrlPattern(String url,
			String urlPattern) {

		UrlPatternMatchResult matchResult = new UrlPatternMatchResult();

		if (url != null && urlPattern != null) {
			// If '{' and/or '} exist, then pattern matching will

			// be tested.

			if (urlPattern != null && urlPattern.indexOf("{") > -1) {

				try {
					int indexOfStart = urlPattern.indexOf("{");
					int indexOfEnd = urlPattern.indexOf("}");
					String startOfRest = urlPattern.substring(0, indexOfStart);
					String endOfRest = null;

					String endOfUrl = null;
					if (urlPattern.length() <= indexOfEnd + 1) {
						endOfRest = "";
					} else {
						endOfRest = urlPattern.substring(indexOfEnd + 1);
					}
					if (url.toLowerCase().startsWith(startOfRest.toLowerCase())) {
						String remainingUrl = url.substring(startOfRest
								.length());
						int indexOfNextSlash = remainingUrl.indexOf("/");
						if (indexOfNextSlash > -1) {
							endOfUrl = remainingUrl.substring(indexOfNextSlash);
						}
					}

					if (url.toLowerCase().startsWith(startOfRest.toLowerCase())
							&& endOfRest.length() > 0
							&& url.toLowerCase().endsWith(
									endOfRest.toLowerCase())) {
						matchResult.setMatchingUrlPattern(true);
						int indexOfEndOfRest = url.indexOf(endOfRest);
						String val = url.substring(startOfRest.length(),
								indexOfEndOfRest);
						matchResult.setRestTokenId(val);
					} else if (url.toLowerCase().startsWith(
							startOfRest.toLowerCase())
							&& endOfRest.length() == 0 && endOfUrl == null) {
						matchResult.setMatchingUrlPattern(true);
						String val = url.substring(startOfRest.length());
						matchResult.setRestTokenId(val);
					}
				} catch (StringIndexOutOfBoundsException t) {
					logger.error("Unable to process url '" + url
							+ "' and matchArg '" + urlPattern + "'", t);
				}
			} else if (url != null && urlPattern != null) {

				if (url.toLowerCase().trim()
						.equals(urlPattern.trim().toLowerCase())) {
					matchResult.setMatchingUrlPattern(true);
					matchResult.setRestTokenId(null);
				} else {
					matchResult.setMatchingUrlPattern(false);
					matchResult.setRestTokenId(null);
				}
			} else {
				matchResult.setMatchingUrlPattern(false);
				matchResult.setRestTokenId(null);
			}
		}else {
			matchResult.setMatchingUrlPattern(false);
			matchResult.setRestTokenId(null);
		}
		return matchResult;
	}

	public static void main(String[] args) {

		String url = "http://customer/123";
		String urlPattern = "http://customer/{token}";

		UrlPatternMatchResult result = UrlUtil.evaluateUrlPattern(url,
				urlPattern);
		System.out.println("Match: " + result.isMatchingUrlPattern()
				+ " Token: " + result.getRestTokenId());
		System.out.println("Done");
	}

}
