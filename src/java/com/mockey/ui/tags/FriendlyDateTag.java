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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Friendly date tag for HTML display. Instead of
 * "Fri Mar 26 10:28:50 PDT 2010", this tag will display "1 hour ago",
 * "Yesterday", "long time ago" with the exact date and time provided with a
 * mouse hover.
 * 
 * TODO: this needs testing. 
 * 
 * @author chad.lafontaine
 */
public class FriendlyDateTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8299069869594172964L;
	private Date date;
	
	private static final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd hh:mm:ss a");
	private static final DateFormat dateFormatShort = new SimpleDateFormat(
			"hh:mm:ss a");

	@Override
	public int doStartTag() throws JspException {

		// return dateFormat.format(date);

		JspWriter out = pageContext.getOut();
		try {
			String text = "Time unknown.";
			if (date != null) {
				text = "<a id=\"fdate\" title=\"" + dateFormat.format(date) + "\">" 
				  + dateFormatShort.format(date) + "</a>";
			}

			out.print(text);
		} catch (IOException e) {
			throw new JspException("Unable to write to the jsp output", e);
		}
		return SKIP_BODY;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}
}
