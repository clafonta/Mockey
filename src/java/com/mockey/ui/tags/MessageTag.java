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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.mockey.ui.Util;

public class MessageTag extends TagSupport {

	private static final long serialVersionUID = -8902512566431524818L;
	private String separator = null;
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	public int doStartTag() {
		StringBuffer successMessage = new StringBuffer();
		try {
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			List<String> messages = (List<String>) request
					.getAttribute(Util.SUCCESS);
			if(messages==null){
				messages = (List<String>)request.getSession().getAttribute(Util.SUCCESS);
			}
			if (messages != null) {
				Iterator<String> keyIter = messages.iterator();
				while (keyIter.hasNext()) {
					String key = keyIter.next();
					successMessage.append(key);
					if(separator!=null){
						successMessage.append(this.separator);
					}
				}
			}
			request.removeAttribute(Util.SUCCESS);
			request.getSession().removeAttribute(Util.SUCCESS);
			JspWriter out = pageContext.getOut();

			out.println(successMessage.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Error("All is not well in the world.", ex);
		}

		// Must return SKIP_BODY because we are not supporting a body for this
		// tag.
		return SKIP_BODY;
	}

}
