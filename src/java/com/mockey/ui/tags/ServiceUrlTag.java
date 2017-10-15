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
package com.mockey.ui.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

import com.mockey.model.Url;

public class ServiceUrlTag extends TagSupport {

	private static final long serialVersionUID = -8902512566431524818L;
	private String value;
	private int breakpoint = -1;

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	public int doStartTag() {
		try {
			value = (String) ExpressionEvaluatorManager.evaluate("value",
					value, String.class, pageContext);
			StringBuffer url = new StringBuffer();
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			
			String protocol = "http://";
			if(request.isSecure()){
				protocol = "https://";
			}

			url.append(protocol + request.getServerName() + ":"
					+ request.getServerPort() + request.getContextPath());
			if (!value.startsWith(Url.MOCK_SERVICE_PATH)) {
				url.append(Url.MOCK_SERVICE_PATH);
			}
			url.append(value);
			JspWriter out = pageContext.getOut();
			if (this.breakpoint > -1) {
				out.println(insertPeriodically(url.toString(), "&#8203;",breakpoint) );
			} else {
				out.println(url.toString());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Error("All is not well in the world.", ex);
		}
		// Must return SKIP_BODY because we are not supporting a body for this
		// tag.
		return SKIP_BODY;
	}

	/**
	 * Inserts
	 * 
	 * <pre>
	 * &#8203;
	 * </pre>
	 * 
	 * (zero-width space) into _value_ at every 'breakPoint' position.
	 * 
	 * @param breakPoint
	 */
	public void setBreakpoint(int breakPoint) {
		this.breakpoint = breakPoint;
	}

	public int getBreakPoint() {
		return breakpoint;
	}

	// From Stackoverflow
	private String insertPeriodically(String text, String insert, int period) {
		StringBuilder builder = new StringBuilder(text.length()
				+ insert.length() * (text.length() / period) + 1);

		int index = 0;
		String prefix = "";
		while (index < text.length()) {
			// Don't put the insert in the very first iteration.
			// This is easier than appending it *after* each substring
			builder.append(prefix);
			prefix = insert;
			builder.append(text.substring(index, Math.min(index + period, text
					.length())));
			index += period;
		}
		return builder.toString();
	}

}
