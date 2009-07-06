/*
 * $Header: /cvsroot/mockey/mockey/src/com/mockey/PostXML.java,v 1.2 2009/06/23 23:10:10 clafonta Exp $
 * $Revision: 1.2 $
 * $Date: 2009/06/23 23:10:10 $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "HttpClient", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * [Additional notices, if required by prior licensing conditions]
 *
 */
package com.mockey;

import java.io.File;
import java.io.FileInputStream;

import org.apache.http.client.HttpClient;


/**
 * 
 * This is a sample application that demonstrates how to use the Jakarta
 * HttpClient API.
 * 
 * This application sends an XML document to a remote web server using HTTP POST
 * 
 * @author Sean C. Sullivan
 * @author Ortwin Gl?ck
 * @author Oleg Kalnichevski
 */
public class PostXML {
	/**
	 * 
	 * Usage: java PostXML http://mywebserver:80/ c:\foo.xml
	 * 
	 * @param args
	 *            command line arguments Argument 0 is a URL to a web server
	 *            Argument 1 is a local filename
	 * 
	 */
	public static void main(String[] args) throws Exception {
//		// Get target URL
//		String strURL = "http://oh01ux93.relizon.com:9004/invoke/relizon.tn.b2b.cxml/receiveCXML";
//
//		// Get file to be posted
//		String strXMLFilename = "c:\\relizon_order_request_sample1.xml";
//		File input = new File(strXMLFilename);
//
//		// Prepare HTTP post
//		PostMethod post = new PostMethod(strURL);
//
//		// Request content will be retrieved directly
//		// from the input stream
//		post.setRequestBody(new FileInputStream(input));
//
//		// Per default, the request content needs to be buffered
//		// in order to determine its length.
//		// Request body buffering can be avoided when
//		// = content length is explicitly specified
//		// = chunk-encoding is used
//		if (input.length() < Integer.MAX_VALUE) {
//			post.setRequestContentLength((int) input.length());
//		} else {
//			post.setRequestContentLength(EntityEnclosingMethod.CONTENT_LENGTH_CHUNKED);
//		}
//
//		// Specify content type and encoding
//		// If content encoding is not explicitly specified
//		// ISO-8859-1 is assumed
//		post.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");
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
//		// Display response
//		System.out.println("Response body: ");
//		System.out.println(post.getResponseBodyAsString());
//
//		// Release current connection to the connection pool once you are done
//		post.releaseConnection();
	}
}
