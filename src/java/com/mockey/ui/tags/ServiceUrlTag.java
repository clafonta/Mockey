/*
 * Copyright 2002-2006 the original author or authors.
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

			url.append("http://" + request.getServerName() + ":"
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
