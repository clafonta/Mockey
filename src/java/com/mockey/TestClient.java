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
package com.mockey;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * A sample client to call a mock web service.
 * 
 */
public class TestClient {

	public void testPostCall() throws Exception {
		String action = "http://localhost:8080/Mockey/service/http://somedomain.com/tickerdata/xyz/blah/blah";
		System.out.println("Start sending " + action + " request");
		URL url = new URL(action);
		HttpURLConnection rc = (HttpURLConnection) url.openConnection();

		rc.setRequestMethod("POST");
		rc.setDoOutput(true);
		rc.setDoInput(true);
		rc.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		rc.setRequestProperty("ticker", "GOOG");
		
		String reqStr = "ticker=[\"wax\"]";

		int len = reqStr.length();
		rc.setRequestProperty("Content-Length", Integer.toString(len));

		rc.connect();
		OutputStreamWriter out = new OutputStreamWriter(rc.getOutputStream());
		out.write(reqStr, 0, len);
		out.flush();
		System.out.println("Request sent, reading response ");
		InputStreamReader read = new InputStreamReader(rc.getInputStream());
		StringBuilder sb = new StringBuilder();
		int ch = read.read();
		while (ch != -1) {
			sb.append((char) ch);
			ch = read.read();
		}
		String response = sb.toString();
		read.close();
		rc.disconnect();
		System.out.println(response);
	}

	public static void testPostXmlProxy() throws Exception {
		String action = "http://localhost:8080/Mockey/service/http://e-services.doh.go.th/dohweb/dohwebservice.asmx?wsdl";
		System.out.println("Start sending " + action + " request");
		URL url = new URL(action);
		HttpURLConnection rc = (HttpURLConnection) url.openConnection();
		// System.out.println("Connection opened " + rc );
		rc.setRequestMethod("POST");
		rc.setDoOutput(true);
		rc.setDoInput(true);
		rc.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		rc.setRequestProperty("SOAPAction",
				"http://e-services.doh.go.th/dohweb/RequestStatusByCitizenID");
		String reqStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body>"
				+ "<RequestStatusByCitizenID xmlns=\"http://e-services.doh.go.th/dohweb/\">"
				+ " <citizen_id>123</citizen_id>"
				+ "</RequestStatusByCitizenID>" + "</soap:Body>"
				+ "</soap:Envelope>";

		int len = reqStr.length();
		rc.setRequestProperty("Content-Length", Integer.toString(len));

		rc.connect();
		OutputStreamWriter out = new OutputStreamWriter(rc.getOutputStream());
		out.write(reqStr, 0, len);
		out.flush();
		System.out.println("Request sent, reading response ");
		InputStreamReader read = new InputStreamReader(rc.getInputStream());
		StringBuilder sb = new StringBuilder();
		int ch = read.read();
		while (ch != -1) {
			sb.append((char) ch);
			ch = read.read();
		}
		String response = sb.toString();
		read.close();
		rc.disconnect();
		System.out.println(response);
		System.out.println("Done");
	}

	public static void main(String[] args) throws Exception {
		TestClient tc = new TestClient();
		tc.testPostCall();
		System.out.println("Done");
	}
}
