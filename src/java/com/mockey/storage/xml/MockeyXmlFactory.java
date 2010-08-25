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

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.TestMockeyServiceStore;

public class MockeyXmlFactory {
	

	/**
	 * Returns a <code>Document</code> object representing a ???
	 * 
	 * @param mockServices List of services to convert into an xml document
     * @return <code>Document</code> object representing a cXML order request
	 */
	public Document getAsDocument(IMockeyStorage store) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document document = docBuilder.newDocument();

			MockeyXmlFileConfigurationGenerator xmlGeneratorSupport = new MockeyXmlFileConfigurationGenerator();
			
			Element xmlRootElement = xmlGeneratorSupport.getElement(document, store);
			document.appendChild(xmlRootElement);

			return document;
		} catch (ParserConfigurationException pce) {
			System.out.println(":" + pce.getMessage());

			return null;
		}
	}

	

	/**
	 * Convert document to string. Helper method.
	 * 
	 * @param document
	 *            the document object.
	 * @return String.
     * @throws java.io.IOException when unable to write the xml
     * @throws javax.xml.transform.TransformerException when unable to transform the document
	 */
	public static String documentToString(Document document) throws IOException, TransformerException {
		String soapRequest = null;

		if (document != null) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml"); 
            transformer.setOutputProperty(OutputKeys.ENCODING, HTTP.UTF_8);
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            return result.getWriter().toString();
		}

		return soapRequest;
	}

	public static void main(String[] args) throws IOException, TransformerException {
		MockeyXmlFactory g = new MockeyXmlFactory();
		Document result = g.getAsDocument(new TestMockeyServiceStore()); // MockeyXmlFactory.buildMockObject());
		System.out.println(MockeyXmlFactory.documentToString(result));
	}
}
