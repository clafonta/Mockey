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
