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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;

public class MockeyXmlFactory {

	private static Logger logger = Logger.getLogger(MockeyXmlFactory.class);

	/**
	 * Convert document to string. Helper method.
	 * 
	 * @param document
	 *            the document object.
	 * @return String.
	 * @throws java.io.IOException
	 *             when unable to write the xml
	 * @throws javax.xml.transform.TransformerException
	 *             when unable to transform the document
	 */
	public String getStoreAsString(IMockeyStorage store, boolean nonReferenceFullDocument) throws IOException,
			TransformerException {
		MockeyXmlFileConfigurationGenerator xmlGeneratorSupport = new MockeyXmlFileConfigurationGenerator();
		Document document = xmlGeneratorSupport.getStoreAsDocument(store, nonReferenceFullDocument);
		return getDocumentAsString(document);
	}

	private String getDocumentAsString(Document document) throws IOException, TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, HTTP.UTF_8);
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(document);
		transformer.transform(source, result);
		return result.getWriter().toString();
	}

	public void writeStoreToXML(IMockeyStorage sourceStore, String destinationFileName) {

		try {
			// WRITE STORE META FIRST
			File f = new File(destinationFileName);
			FileOutputStream fop = new FileOutputStream(f);

			String fileOutput = getStoreAsString(sourceStore, false);
			byte[] fileOutputAsBytes = fileOutput.getBytes(HTTP.UTF_8);
			fop.write(fileOutputAsBytes);
			fop.flush();
			fop.close();

			// WRITE EACH SERVICE
			for (Service service : sourceStore.getServices()) {
				File serviceFile = new File(MockeyXmlFileManager.getServiceFileNameOutputString(service));
				FileOutputStream serviceFOP = new FileOutputStream(serviceFile);
				MockeyXmlFileConfigurationGenerator xmlGeneratorSupport = new MockeyXmlFileConfigurationGenerator();
				Document serviceDoc = xmlGeneratorSupport.getServiceAsDocument(service);
				String serviceOutput = this.getDocumentAsString(serviceDoc);

				byte[] serviceOutputAsBytes = serviceOutput.getBytes(HTTP.UTF_8);
				serviceFOP.write(serviceOutputAsBytes);
				serviceFOP.flush();
				serviceFOP.close();
				logger.debug("Written to: " + serviceFile.getAbsolutePath());
			}
			
		} catch (Exception e) {
			logger.debug("Unable to write file", e);
		}
	}
}
