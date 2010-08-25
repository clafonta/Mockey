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
import java.io.File;
import java.io.StringReader;

import org.xml.sax.InputSource;

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

		return msp.getMockServices(new InputSource(br));

	}

	public static void main(String[] args) throws Exception {
		
		String strXMLFilename = "/Users/chadlafontaine/Work/Mockey/mock_service_definitions.xml";

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
		System.out.println(mockServiceList.toString());

		System.out.println("Done");
	}
}
