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

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Prints out an HTML span with ID of type of service performed. Proxy, Static,
 * or Dynamic.
 * 
 * @author chad.lafontaine
 */
public class ServiceResponseTypeTag extends TagSupport {

	/**
     * 
     */
	private static final long serialVersionUID = 702927192030153426L;
	private int type = -1;
	private int style = -1;

	private Long serviceId = null;
	private static final String CSS_CLASS_RESPONSE_SET = "response_set";
	private static final String CSS_CLASS_RESPONSE_NOT = "response_not";

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public int doStartTag() throws JspException {

		/*
		 * The output we want is: <pre> P S D </pre> Each letter is a link with
		 * HREF The
		 */
		JspWriter out = pageContext.getOut();
		try {
			String text = "";
			String proxyClass = CSS_CLASS_RESPONSE_NOT;
			String staticClass = CSS_CLASS_RESPONSE_NOT;
			String dynamicClass = CSS_CLASS_RESPONSE_NOT;
			String serviceIdentifier = "";
			if (this.serviceId != null) {
				serviceIdentifier = this.serviceId.toString();
			}
			switch (type) {
			case 0:
				proxyClass = CSS_CLASS_RESPONSE_SET;
				text = "Proxy";
				break;
			case 1:
				staticClass = CSS_CLASS_RESPONSE_SET;
				text = "Static";
				break;
			case 2:
				dynamicClass = CSS_CLASS_RESPONSE_SET;
				text = "Dynamic";
				break;
			default:
				text = "";
				break;
			}
			if (this.style == -1) {
			
				text = "<a class=\"serviceResponseTypeLink "
						+ proxyClass
						+ " serviceResponseType_0_"
						+ serviceIdentifier
						+ "\" id=\"serviceResponseType_0_"
						+ serviceIdentifier
						+ "\" onclick=\"return false;\" href=\"#\" title=\"Proxy response\">P</a>"
						+ " <a class=\"serviceResponseTypeLink "
						+ staticClass
						+ " serviceResponseType_1_"
						+ serviceIdentifier
						+ "\" id=\"serviceResponseType_1_"
						+ serviceIdentifier
						+ "\" onclick=\"return false;\" href=\"#\" title=\"Static response\">S</a>"
						+ " <a class=\"serviceResponseTypeLink "
						+ dynamicClass
						+ " serviceResponseType_2_"
						+ serviceIdentifier
						+ "\" id=\"serviceResponseType_2_"
						+ serviceIdentifier
						+ "\" onclick=\"return false;\" href=\"#\" title=\"Dynamic response\">D</a>";
			}
	

			out.print(text);
		} catch (IOException e) {
			throw new JspException("Unable to write to the jsp output", e);
		}
		return SKIP_BODY;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

}
