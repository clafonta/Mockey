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
