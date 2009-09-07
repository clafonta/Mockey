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

import java.io.BufferedReader;
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
		String strXMLFilename = "c:\\mockservice.xml";

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
