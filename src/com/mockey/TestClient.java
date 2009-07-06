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
package com.mockey;

import java.io.ByteArrayInputStream;

import org.apache.http.client.HttpClient;



/**
 * 
 * A sample client to call a mock web service. 
 * 
 */
public class TestClient {

	/**
	 * 
	 * @param strURL - the URL of the mock service
	 * @param msg - the string message to be sent to the mock service. 
	 * @return - the response from the mock service as a String. 
	 * @throws Exception
	 */
	public static String getResponseMsg(String strURL, String msg) throws Exception {

		String responseBody = null;

		// Prepare HTTP post
//		PostMethod post = new PostMethod(strURL);
//
//		// Request content will be retrieved directly
//		// from the input stream
//
//		byte currentXMLBytes[] = msg.getBytes();
//		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(currentXMLBytes);
//		post.setRequestBody(byteArrayInputStream);
//		
//		// Per default, the request content needs to be buffered
//		// in order to determine its length.
//		// Request body buffering can be avoided when
//		// = content length is explicitly specified
//		// = chunk-encoding is used
//		if (msg.length() < Integer.MAX_VALUE) {
//			post.setRequestContentLength((int) msg.length());
//		} else {
//			post.setRequestContentLength(EntityEnclosingMethod.CONTENT_LENGTH_CHUNKED);
//		}
//
//		// Specify content type and encoding
//		// If content encoding is not explicitly specified
//		// ISO-8859-1 is assumed
//		post.setRequestHeader("Content-type", "text/xml; charset=UTF-8");
//
//		// Get HTTP client
//		HttpClient httpclient = new HttpClient();
//
//		// Execute request
//		int result = httpclient.executeMethod(post);
//
//		// Display status code
//		System.out.println("Response status code: " + result);
//
//		responseBody = post.getResponseBodyAsString();
//
//		// Release current connection to the connection pool once you are done
//		post.releaseConnection();
		return responseBody;
	}

	/**
	 * Basic example of calling a mock service. 
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String urlString = "http://localhost:8080/mockey/service/xmethods";
		//String urlString ="http://www.webservicemart.com/uszip.asmx?op=ValidateZip";
		String msg =
		 "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tns=\"http://webservicemart.com/ws/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">"+
			   "<soap:Body>" +
			      "<tns:ValidateZip>"+
			         "<tns:ZipCode>94602</tns:ZipCode>"+
			      "</tns:ValidateZip>"+
			   "</soap:Body>"+
			"</soap:Envelope>"; 
		 
		
		String responseMsg = TestClient.getResponseMsg(urlString, msg);
		System.out.println(responseMsg);
		System.out.println("DONE");
	}
}
