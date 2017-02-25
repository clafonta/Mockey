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

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.mockey.plugin.RequestInspectionResult;

/**
 * Wrapper with print and helper functions for a HTTP response message.
 * 
 * @author chad.lafontaine -
 * 
 */
public class ResponseFromService {

	private static final String[] IGNORE_HEADERS = { "Transfer-Encoding" };

	private Log log = LogFactory.getLog(ResponseFromService.class);
	private String scenarioName = null;
	private String scenarioTagsAsString = null;
	private String body;
	private boolean valid;
	private String errorMsg;
	private Header[] headers;
	private List<Cookie> cookieList = new ArrayList<Cookie>();
	private int httpResponseStatusCode;
	private int serviceScenarioHangTime;
	private Url originalRequestUrlBeforeTwisting;
	private Url requestUrl;
	private RequestInspectionResult requestInspectionResult;

	/**
	 * Empty constructor
	 */
	public ResponseFromService() {
	}

	/**
	 * 
	 * @param rsp
	 *            - parses the response
	 */
	public ResponseFromService(HttpResponse rsp) {
		HttpEntity entity = rsp.getEntity();

		setHttpResponseStatusCode(rsp.getStatusLine().getStatusCode());
		headers = rsp.getAllHeaders();
		setHeaders(headers);

		setCookiesFromHeader(headers);
		if (entity != null) {
			try {
				setBody(EntityUtils.toString(entity));
			} catch (IOException e) {
				throw new IllegalStateException("Unable to parse response", e);
			}
			setValid(true);
		}

	}

	/**
	 * @return the responseMsg
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the responseMsg to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid
	 *            the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg
	 *            the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}

	public Header[] getHeaders() {
		return headers;
	}

	/**
	 * 
	 * @return - pretty print header information.
	 */
	public String getHeaderInfo() {
		StringBuffer sb = new StringBuffer();
		if (headers != null) {
			for (int i = 0; i < headers.length; i++) {
				Header header = headers[i];
				sb.append(header.getName() + ":" + header.getValue());
				if((i+1)<headers.length){
					sb.append("|\n");
				}
			}
		}
		return sb.toString();
	}

	
	public int getServiceScenarioHangTime() {
		return serviceScenarioHangTime;
	}

	public void setServiceScenarioHangTime(int serviceScenarioHangTime) {
		this.serviceScenarioHangTime = serviceScenarioHangTime;
	}

	public void setHttpResponseStatusCode(int statusCode) {
		this.httpResponseStatusCode = statusCode;
	}

	public int getHttpResponseStatusCode() {
		return this.httpResponseStatusCode;
	}
	
	private void setCookiesFromHeader(Header[] headers){
		for (Header header : headers) {
			
			if (header.getName().equals("Set-Cookie")) {
				String headerValue = header.getValue();
	            // Parse cookie
	            String[] fields = headerValue.split(";\\s*");

	            String expires = null;
	            String path = null;
	            String domain = null;
	            boolean secure = false;
				boolean httpOnly = false;

	            // Parse each field
	            for (int j=1; j<fields.length; j++) {
	                if ("secure".equalsIgnoreCase(fields[j])) {
	                    secure = true;
	                } else if ("httpOnly".equalsIgnoreCase(fields[j])) {
	                    httpOnly = true;
	                } else if (fields[j].indexOf('=') > 0) {
	                    String[] f = fields[j].split("=");
	                    if ("expires".equalsIgnoreCase(f[0])) {
	                        expires = f[1];
	                    } else if ("domain".equalsIgnoreCase(f[0])) {
	                        domain = f[1];
	                    } else if ("path".equalsIgnoreCase(f[0])) {
	                        path = f[1];
	                    }
	                }
	            }

	            String[] cookieParts = headerValue.split("=", 2);
				String cookieBody = cookieParts[1];
				String[] cookieBodyParts = cookieBody.split("; ");
				Cookie cookie = new Cookie(cookieParts[0], cookieBodyParts[0]);

// Mockey currently manages cookies on behalf of the client.
// There should be no need for the client to view the cookies.
// For now, we only forward cookie name and value to the client.
// If the need arises to forward additional cookie attributes,
// uncomment and revisit the code below.
//
//				if (path != null) {
//					cookie.setPath(path);
//				}
//				if (domain != null) {
//					cookie.setDomain(domain);
//				}
//				cookie.setSecure(secure);
//				cookie.setHttpOnly(httpOnly);
//
//				if(expires!=null){
//				Date expiresTime = null;
//				try {
//					expiresTime = HttpCookieDateUtil.parseDate(expires);
//					Date nowTime = new Date();
//					long maxAge = nowTime.getTime() - expiresTime.getTime();
//					cookie.setMaxAge((int) maxAge/1000);
//				}catch(Exception e){
//					log.error("Unable to calculate maxAge with expiration date "+expiresTime, e);
//				}
//				}

				this.cookieList.add(cookie);
	        }
		
		}
	}


	public void writeToOutput(HttpServletResponse resp) throws IOException {
		// copy the headers out
		if (headers != null) {
			for (Header header : headers) {

				// copy the cookies
				if (ignoreHeader(header.getName())) {
					log.debug("Ignoring header: " + header.getName());
				} else if (header.getName().equalsIgnoreCase("Set-Cookie")) {
					// Ignore...
				} else if (header.getName().equals("Content-Type")) {
					// copy the content type
					resp.setContentType(header.getValue());
				} else
					resp.setHeader(header.getName(), header.getValue());
			}
		}
		
		/*
		 * HttpServletResponse adds double quotes to cookie values that
		 * contain special characters. That throws off certain clients.
		 * Therefore, build the Set-Cookie headers manually.
		 */
		for (String setCookieHeaderValue: buildSetCookieHeaderValues()) {
			resp.addHeader("Set-Cookie", setCookieHeaderValue);
		}

		if (body != null) {
			byte[] myISO88591asBytes = body.getBytes(HTTP.ISO_8859_1);
			new PrintStream(resp.getOutputStream()).write(myISO88591asBytes);
			resp.getOutputStream().flush();
		} else {
			PrintStream out = new PrintStream(resp.getOutputStream());
			out.println(body);
		}

	}

	private String[] buildSetCookieHeaderValues() {
		String[] result = new String[cookieList.size()];
		int idx = 0;
		for(Cookie cookie: this.cookieList){
			StringBuilder sb = new StringBuilder();
			sb.append(cookie.getName() + "=" + cookie.getValue());
			if (cookie.getDomain() != null) {
				sb.append("; domain=");
				sb.append(cookie.getDomain());
			}
			if (cookie.getPath() != null) {
				sb.append("; path=");
				sb.append(cookie.getPath());
			}
			if (cookie.getSecure()) {
				sb.append("; secure");
			}
			// TODO: upgrade to Server 3.0
			/*
			if (cookie.isHttpOnly()) {
				sb.append("; httpOnly");
			}
			*/
			if (cookie.getMaxAge() >= 0) {
				sb.append("; max-age=");
				sb.append(cookie.getMaxAge());
			}
			result[idx++] = sb.toString();
		}
		return result;
	}

	private boolean ignoreHeader(String name) {
		for (String header : IGNORE_HEADERS) {
			if (header.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public void setOriginalRequestUrlBeforeTwisting(Url originalRequestUrlBeforeTwisting) {
		this.originalRequestUrlBeforeTwisting = originalRequestUrlBeforeTwisting;
	}

	public Url getOriginalRequestUrlBeforeTwisting() {
		return originalRequestUrlBeforeTwisting;
	}

	public void setRequestUrl(Url requestUrl) {
		this.requestUrl = requestUrl;
	}

	public Url getRequestUrl() {
		return requestUrl;
	}

	public String getResponseCookiesAsString() {
		StringBuffer responseCookies = new StringBuffer();
		for(Cookie cookie: this.cookieList){
			responseCookies.append(String.format("Cookie---> %s = %s\n", cookie.getName(), cookie.getValue()));
		}
		return responseCookies.toString();
	}

	public RequestInspectionResult getRequestInspectionResult() {
		return requestInspectionResult;
	}

	public void setRequestInspectionResult(RequestInspectionResult requestInspectionResult) {
		this.requestInspectionResult = requestInspectionResult;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public String getScenarioTagsAsString() {
		return scenarioTagsAsString;
	}

	public void setScenarioTagsAsString(String scenarioTagsAsString) {
		this.scenarioTagsAsString = scenarioTagsAsString;
	}
	

}
