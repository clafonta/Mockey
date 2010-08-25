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

import java.util.Iterator;
import java.util.List;

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
	@SuppressWarnings("unchecked")
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
