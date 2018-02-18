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

import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;

public class MockeyXmlFactory {

	private static Logger logger = Logger.getLogger(MockeyXmlFactory.class);

	/**
	 * Convert document to string. Helper method. 
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
			// 
			MockeyXmlFileManager.getInstance().cleanDirectory();
			// WRITE STORE META FIRST
			File parentFolder = MockeyXmlFileManager.getInstance().getBasePathFile();
			File f = new File(parentFolder,destinationFileName);
			FileOutputStream fop = new FileOutputStream(f);

			String fileOutput = getStoreAsString(sourceStore, false);
			byte[] fileOutputAsBytes = fileOutput.getBytes(HTTP.UTF_8);
			fop.write(fileOutputAsBytes);
			fop.flush();
			fop.close();

			// WRITE EACH SERVICE
			for (Service service : sourceStore.getServices()) {

				// File to write out
				File serviceFile = MockeyXmlFileManager.getInstance().getServiceFile(service);

				FileOutputStream serviceFOP = new FileOutputStream(serviceFile);
				MockeyXmlFileConfigurationGenerator xmlGeneratorSupport = new MockeyXmlFileConfigurationGenerator();
				Document serviceDoc = xmlGeneratorSupport.getServiceAsDocument(service, false);
				String serviceOutput = this.getDocumentAsString(serviceDoc);
				for (Scenario scenario : service.getScenarios()) {

					File scenarioFile = MockeyXmlFileManager.getInstance().getServiceScenarioFileAbsolutePath(service, scenario);
					this.writeServiceScenarioToXMLFile(scenarioFile, scenario);
				}

				byte[] serviceOutputAsBytes = serviceOutput.getBytes(HTTP.UTF_8);
				serviceFOP.write(serviceOutputAsBytes);
				serviceFOP.flush();
				serviceFOP.close();
				logger.debug("Written to: " + serviceFile.getAbsolutePath());
			}

		} catch (Exception e) {
			logger.error("Unable to write file", e);
		}
	}

	/**
	 * 
	 * @param scenarioFile
	 *            The file to write the scenario XML definition to.
	 * @param scenario
	 *            Scenario definitions/model to write to XML.
	 * @throws IOException
	 * @throws TransformerException
	 */
	private void writeServiceScenarioToXMLFile(File scenarioFile, Scenario scenario) throws IOException,
			TransformerException {
		
		
		FileOutputStream serviceFOP = new FileOutputStream(scenarioFile);
		MockeyXmlFileConfigurationGenerator xmlGeneratorSupport = new MockeyXmlFileConfigurationGenerator();

		// Yes, hard coded for now. As of April 18th, 2013
		boolean writeScenarioResponseToTxtFile = true;
		// TRUE means the scenario response will NOT be included in the Scenario
		// XML definition file. The Scenario response will be written to its
		// own '.txt' file. For example:
		// + <scenario def>.xml // Includes a x:include pointer to the *.txt file.
		// + <scenario response>.txt // contains the scenario response.
		//
		// FALSE means the scenario response will include in the Scenario response 
		// as a CDATA element. 

		Document serviceDoc = xmlGeneratorSupport
				.getServiceScenarioAsDocument(scenario, writeScenarioResponseToTxtFile);
		// Write the XML
		String serviceOutput = this.getDocumentAsString(serviceDoc);
		byte[] serviceOutputAsBytes = serviceOutput.getBytes(HTTP.UTF_8);
		serviceFOP.write(serviceOutputAsBytes);
		serviceFOP.flush();
		serviceFOP.close();

		if (writeScenarioResponseToTxtFile) {
			byte[] scenarioResponseOutputAsBytes = scenario.getResponseMessage().getBytes(HTTP.UTF_8);
			String xmlDefinitionFilePath = scenarioFile.getPath();
			File scenarioResponseFile = new File(swapFileExtensions(xmlDefinitionFilePath));
			FileOutputStream scenarioResponseFOP = new FileOutputStream(scenarioResponseFile);
			scenarioResponseFOP.write(scenarioResponseOutputAsBytes);
			scenarioResponseFOP.flush();
			scenarioResponseFOP.close();
		}
		logger.debug("Written to: " + scenarioFile.getAbsolutePath());

	}

	// Quick utility to swap file extensions
	private String swapFileExtensions(String arg) {
		int extIndex = arg.lastIndexOf(".");
		if (extIndex != -1) {
			String ext = arg.substring(extIndex);
			if (ext.equalsIgnoreCase(".xml")) {
				arg = arg.substring(0, extIndex) + ".txt";
			}
		}
		return arg;
	}
}
