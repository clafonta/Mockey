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
package com.mockey.xml;

import com.mockey.MockServiceBean;
import com.mockey.MockServiceScenarioBean;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class MockServiceXMLFactory {
	/** Basic logger */
	private static Logger logger = Logger.getLogger(MockServiceXMLFactory.class);

	/**
	 * Returns a <code>Document</code> object representing a ???
	 * 
	 * @param mockServices List of services to convert into an xml document
     * @return <code>Document</code> object representing a cXML order request
	 */
	public Document getAsDocument(List mockServices) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document document = docBuilder.newDocument();

			MockServiceXMLGenerator xmlGeneratorSupport = new MockServiceXMLGenerator();

			logger.debug("Building XML representation to services of size: " + mockServices.size());
			Element xmlRootElement = xmlGeneratorSupport.getElement(document, mockServices);
			document.appendChild(xmlRootElement);

			return document;
		} catch (ParserConfigurationException pce) {
			System.out.println(":" + pce.getMessage());

			return null;
		}
	}

	/**
	 * Mock object. Used for unit testing.
	 * 
	 * @return a sample list of services
	 */
	private static List buildMockObject() {
		// <?xml version="1.0" encoding="UTF-8"?>
		// <!--
		// This contains a list of one or more mock-services.
		// Each mock-service can contain one or more scenarios.
		// $Id: MockServiceXMLFactory.java,v 1.1 2005/05/04 21:51:14 clafonta
		// Exp $
		// -->
		// <mockservice>
		// <services>
		// <service name="relizon" description="" url="/mockservice/relizon">
		// <scenarios>
		// <scenario name="a">
		// <scenario_request>some request message</scenario_request>
		// <scenario_response>some response message</scenario_response>
		// </scenario>
		// <scenario name="b">
		// <scenario_request>some request message</scenario_request>
		// <scenario_response>some response message</scenario_response>
		// </scenario>
		// <scenario name="c">
		// <scenario_request>some request message</scenario_request>
		// <scenario_response>some response message</scenario_response>
		// </scenario>
		// </scenarios>
		// </service>
		// </services>
		// </mockservice>

		List beans = new ArrayList();
		MockServiceBean bean = new MockServiceBean();
		bean.setServiceName("testname");
		bean.setDescription("test description");
		bean.setRealServiceUrl("http://someservice:8000/eai");
		bean.setMockServiceUrl("/service/relizon");
		MockServiceScenarioBean mssb = new MockServiceScenarioBean();
		mssb.setScenarioName("a");
		mssb.setRequestMessage("request message a");
		mssb.setResponseMessage("response message a");
		bean.updateScenario(mssb);
		mssb = new MockServiceScenarioBean();
		mssb.setScenarioName("b");
		mssb.setRequestMessage("request message b");
		mssb.setResponseMessage("response message b");
		bean.updateScenario(mssb);
		beans.add(bean);
		MockServiceBean bean2 = new MockServiceBean();
		bean2.setServiceName("testname2");
		bean2.setDescription("test description2");
		bean2.setRealServiceUrl("http://someservice:8000/eai2");
		bean2.setMockServiceUrl("/service/relizon2");
		MockServiceScenarioBean mssb2 = new MockServiceScenarioBean();
		mssb2.setScenarioName("a");
		mssb2.setRequestMessage("request message a");
		mssb2.setResponseMessage("response message a");
		bean2.updateScenario(mssb);
		mssb2 = new MockServiceScenarioBean();
		mssb2.setScenarioName("b");
		mssb2.setRequestMessage("request message b");
		mssb2.setResponseMessage("response message b");
		bean2.updateScenario(mssb);
		beans.add(bean2);
		return beans;
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

            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            return result.getWriter().toString();
		}

		return soapRequest;
	}

	public static void main(String[] args) throws IOException, TransformerException {
		MockServiceXMLFactory g = new MockServiceXMLFactory();
		Document result = g.getAsDocument(MockServiceXMLFactory.buildMockObject());
		System.out.println(MockServiceXMLFactory.documentToString(result));
	}
}
