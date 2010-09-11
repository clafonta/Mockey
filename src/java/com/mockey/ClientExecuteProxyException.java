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

import com.mockey.model.Url;

public class ClientExecuteProxyException extends Throwable {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1858670956465614515L;
	private String errorMsg = null;
	 private Url requestUrl = null;

		
		public ClientExecuteProxyException(String msg, Url requestUrl, Exception e) {
			super(msg,e);
			this.errorMsg = msg;
			this.requestUrl = requestUrl;
		}

		public String getMessage() {
			return errorMsg;
		}
		
		public Url getRequestUrl(){
			return requestUrl;
		}
}
