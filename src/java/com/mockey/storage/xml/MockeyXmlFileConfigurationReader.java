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

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.xml.sax.InputSource;

import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.ServiceRef;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;

public class MockeyXmlFileConfigurationReader {

	/**
	 * Returns a
	 * 
	 * @param mockServicesDefinition
	 * @return
	 */
	public IMockeyStorage readDefinition(String mockServicesDefinition) throws org.xml.sax.SAXParseException,
			java.io.IOException, org.xml.sax.SAXException {

		BufferedReader br = new BufferedReader(new StringReader(mockServicesDefinition));

		MockeyXmlFileConfigurationParser msp = new MockeyXmlFileConfigurationParser();
		
		return msp.getMockeyStore(new InputSource(br));

	}
	
	/**
	 * Returns a
	 * 
	 * @param mockServicesDefinition
	 * @return
	 */
	public List<Service> readServiceDefinition(String mockServiceDefinition) throws org.xml.sax.SAXParseException,
			java.io.IOException, org.xml.sax.SAXException {

		BufferedReader br = new BufferedReader(new StringReader(mockServiceDefinition));

		MockeyXmlFileConfigurationParser msp = new MockeyXmlFileConfigurationParser();

		return msp.getMockService(new InputSource(br));

	}
	
	private static String getStorageAsString(IMockeyStorage storage) {
        StringBuffer sb = new StringBuffer();
        //sb.append(storage.toString());
        for (Service service : storage.getServices() ) {
            sb.append(getServiceString(service));
        }
        sb.append("\nService References:\n");
        for(ServiceRef ref : storage.getServiceRefs()){
            sb.append(ref.toString());

        }
        return sb.toString();
	}
	
	private static String getServiceString(Service service){
		StringBuffer sb = new StringBuffer();
        
            sb.append("Service ID: ").append(service.getId()).append("\n");
            sb.append("Service name: ").append(service.getServiceName()).append("\n");
            sb.append("Service description: ").append(service.getDescription()).append("\n");
            for (Url url : service.getRealServiceUrls()) {
                sb.append("    real URL : ").append(url.getFullUrl()).append("\n");
            }
            for (Scenario scenario : service.getScenarios()) {
                sb.append("    scenario name: ").append(scenario.getScenarioName()).append("\n");
                sb.append("    scenario request: ").append(scenario.getRequestMessage()).append("\n");
                sb.append("    scenario response: ").append(scenario.getResponseMessage()).append("\n");
            }
        
        return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		
		// FULL 
		String strXMLFilename = "/Users/chadlafontaine/Work/test/mock_service_definitions.xml";

		java.io.FileInputStream fis = new java.io.FileInputStream(strXMLFilename);

		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		int n = 0;

		byte[] buf = new byte[2048];

		while (n != -1) {
			n = fis.read(buf, 0, buf.length);

			if (n > 0) {
				baos.write(buf, 0, n);
			}
		}

		String strXMLRequest = new String(baos.toByteArray());
		MockeyXmlFileConfigurationReader fileReader = new MockeyXmlFileConfigurationReader();
		IMockeyStorage mockServiceList = fileReader.readDefinition(strXMLRequest);
		System.out.println(getStorageAsString(mockServiceList));
		
		for(ServiceRef ref : mockServiceList.getServiceRefs()){
			String fragXMLFilename = "/Users/chadlafontaine/Work/test/" ; //+ ref.getFileName();

			java.io.FileInputStream fragmentIs = new java.io.FileInputStream(fragXMLFilename);

			baos = new java.io.ByteArrayOutputStream();
			n = 0;
			buf = new byte[2048];

			while (n != -1) {
				n = fragmentIs.read(buf, 0, buf.length);

				if (n > 0) {
					baos.write(buf, 0, n);
				}
			}

			String fragXMLRequest = new String(baos.toByteArray());
			
			List<Service> list = fileReader.readServiceDefinition(fragXMLRequest);
			for(Service y : list){
				System.out.println(getServiceString(y));
			}
		}
		
		
		
		System.out.println("Done");
	}
	
	
}
