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
package com.mockey.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * Convenience class 
 * @author chadlafontaine
 *
 */
public class HttpStatusCodeStore {

	private List<StatusCodeEntry> store = new ArrayList<StatusCodeEntry>();
	private static HttpStatusCodeStore instance = new HttpStatusCodeStore();

	public static HttpStatusCodeStore getInstance() {
		return instance;
	}

	private HttpStatusCodeStore() {
		this.add(HttpServletResponse.SC_ACCEPTED, "202:  a request was accepted for processing, but was not completed.");
		this.add(HttpServletResponse.SC_BAD_GATEWAY 
		, "502:the HTTP server received an invalid response from a server it consulted when acting as a proxy or gateway.");
		this.add(HttpServletResponse.SC_BAD_REQUEST 
		, "400:indicating the request sent by the client was syntactically incorrect.");
		this.add(HttpServletResponse.SC_CONFLICT 
		, "409:the request could not be completed due to a conflict with the current state of the resource.");
		this.add(HttpServletResponse.SC_CONTINUE 
		, "100:indicating the client can continue.");
		this.add(HttpServletResponse.SC_CREATED 
		, "201:indicating the request succeeded and created a new resource on the server.");
		this.add(HttpServletResponse.SC_EXPECTATION_FAILED 
		, "417:the server could not meet the expectation given in the Expect request header.");
		this.add(HttpServletResponse.SC_FORBIDDEN 
		, "403:indicating the server understood the request but refused to fulfill it.");
		this.add(HttpServletResponse.SC_FOUND 
		, "302:the resource reside temporarily under a different URI.");
		this.add(HttpServletResponse.SC_GATEWAY_TIMEOUT 
		, "504:the server did not receive a timely response from the upstream server while acting as a gateway or proxy.");
		this.add(HttpServletResponse.SC_GONE 
		, "410:the resource is no longer available at the server and no forwarding address is known.");
		this.add(HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED 
		, "505:the server does not support or refuses to support the HTTP protocol version that was used in the request message.");
		this.add(HttpServletResponse.SC_INTERNAL_SERVER_ERROR 
		, "500:indicating an error inside the HTTP server which prevented it from fulfilling the request.");
		this.add(HttpServletResponse.SC_LENGTH_REQUIRED 
		, "411:the request cannot be handled without a defined Content-Length.");
		this.add(HttpServletResponse.SC_METHOD_NOT_ALLOWED 
		, "405:the method specified in the Request-Line is not allowed for the resource identified by the Request-URI.");
		this.add(HttpServletResponse.SC_MOVED_PERMANENTLY 
		, "301:the resource has permanently moved to a new location, and that future references should use a new URI with their requests.");
		this.add(HttpServletResponse.SC_MOVED_TEMPORARILY 
		, "302:the resource has temporarily moved to another location, but that future references should still use the original URI to access the resource.");
		this.add(HttpServletResponse.SC_MULTIPLE_CHOICES 
		, "300:the requested resource corresponds to any one of a set of representations, each with its own specific location.");
		this.add(HttpServletResponse.SC_NO_CONTENT 
		, "204:the request succeeded but that there was no new information to return.");
		this.add(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION 
		, "203:the meta information presented by the client did not originate from the server.");
		this.add(HttpServletResponse.SC_NOT_ACCEPTABLE 
		, "406:the resource identified by the request is only capable of generating response entities which have content characteristics not acceptable according to the accept headers sent in the request.");
		this.add(HttpServletResponse.SC_NOT_FOUND 
		, "404:the requested resource is not available.");
		this.add(HttpServletResponse.SC_NOT_IMPLEMENTED 
		, "501:indicating the HTTP server does not support the functionality needed to fulfill the request.");
		this.add(HttpServletResponse.SC_NOT_MODIFIED 
		, "304:a conditional GET operation found that the resource was available and not modified.");
		this.add(HttpServletResponse.SC_OK 
		, "200:indicating the request succeeded normally.");
		this.add(HttpServletResponse.SC_PARTIAL_CONTENT 
		, "206:the server has fulfilled the partial GET request for the resource.");
		this.add(HttpServletResponse.SC_PAYMENT_REQUIRED 
		, "402:reserved for future use.");
		this.add(HttpServletResponse.SC_PRECONDITION_FAILED 
		, "412:the precondition given in one or more of the request-header fields evaluated to false when it was tested on the server.");
		this.add(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED 
		, "407:the client MUST first authenticate itself with the proxy.");
		this.add(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE 
		, "413:the server is refusing to process the request because the request entity is larger than the server is willing or able to process.");
		this.add(HttpServletResponse.SC_REQUEST_TIMEOUT 
		, "408:the client did not produce a request within the time that the server was prepared to wait.");
		this.add(HttpServletResponse.SC_REQUEST_URI_TOO_LONG 
		, "414:the server is refusing to service the request because the Request-URI is longer than the server is willing to interpret.");
		this.add(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE 
		, "416:the server cannot serve the requested byte range.");
		this.add(HttpServletResponse.SC_RESET_CONTENT 
		, "205:the agent SHOULD reset the document view which caused the request to be sent.");
		this.add(HttpServletResponse.SC_SEE_OTHER 
		, "303:the response to the request can be found under a different URI.");
		this.add(HttpServletResponse.SC_SERVICE_UNAVAILABLE 
		, "503:the HTTP server is temporarily overloaded, and unable to handle the request.");
		this.add(HttpServletResponse.SC_SWITCHING_PROTOCOLS 
		, "101:indicating the server is switching protocols according to Upgrade header.");
		this.add(HttpServletResponse.SC_TEMPORARY_REDIRECT 
		, "307:the requested resource resides temporarily under a different URI.");
		this.add(HttpServletResponse.SC_UNAUTHORIZED 
		, "401:the request requires HTTP authentication.");
		this.add(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE 
		, "415:the server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method.");
		this.add(HttpServletResponse.SC_USE_PROXY 
		, "305:the requested resource MUST be accessed through the proxy given by the Location field.");	
	}

	private void add(int code, String text) {
		store.add(new StatusCodeEntry(code, text));
	}

	public List<StatusCodeEntry> getCodeEntryList() {
		return orderNumerically(store);
	}
	
	/**
	 * Returns the services list ordered alphabetically.
	 * 
	 * @param services
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<StatusCodeEntry> orderNumerically(
			List<StatusCodeEntry> stringList) {

		class IntComparator implements Comparator{
			   
		    public int compare(Object emp1, Object emp2){
		   
		        /*
		         * parameter are of type Object, so we have to downcast it
		         * to Employee objects
		         */
		       
		        int emp1Age = ((StatusCodeEntry)emp1).getCode();     
		        int emp2Age = ((StatusCodeEntry)emp2).getCode();
		       
		        if(emp1Age > emp2Age)
		            return 1;
		        else if(emp1Age < emp2Age)
		            return -1;
		        else
		            return 0;    
		    }
		   
		}
		// Sort me.
		Collections.sort(stringList, new IntComparator());

		return stringList;
	}

	public class StatusCodeEntry {
		private int code;
		private String text;

		StatusCodeEntry(int _code, String _text) {
			this.code = _code;
			this.text = _text;
		}

		public int getCode() {
			return this.code;
		}

		public String getText() {
			return this.text;
		}

	}

	public StatusCodeEntry getStatusCodeEntry(int arg) {
		StatusCodeEntry temp = null;
		for (StatusCodeEntry sce : this.getCodeEntryList()) {
			if (sce.getCode() == arg) {
				temp = sce;
				break;
			}
		}
		return temp;

	}

}
