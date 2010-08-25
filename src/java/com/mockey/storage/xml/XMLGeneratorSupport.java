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
package com.mockey.storage.xml;

import java.text.SimpleDateFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

class XmlGeneratorSupport {
	/**
	 * A <code>SimpleDateFormat</code> used to represent ISO 8601 dates and
	 * times.
	 */
	protected static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	/**
	 * A <code>SimpleDateFormat</code> used to represent ISO 8601 dates. This
	 * format does <b>not</b> display the time.
	 */
	protected static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Sets the given element attribute to the specified value.
	 * 
	 * @param element
	 *            the element containing the attribute to set
	 * @param name
	 *            the name of the attribute to set
	 * @param value
	 *            the value to set the attribute to; if <code>null</code>,
	 *            then the attribute is not set
	 */
	public void setAttribute(Element element, String name, String value) {
		if (value != null) {
			element.setAttribute(name, value);
		}
	}

	/**
	 * Sets the given element text to be specified to text.
	 * 
	 * @param document
	 *            the parent document used to create a text node.
	 * @param element
	 *            the element containing the text to set
	 * @param text
	 *            the text to be contained in the element; if <code>null</code>,
	 *            then no text is provided.
	 */
	public void setText(Document document, Element element, String text) {
		if (text != null) {
			Text textNode = document.createTextNode(text);
			element.appendChild(textNode);
		}
	}
}
