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
package com.mockey.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;



public class Util {

	private static final String SUCCESS = "successMessages";
	private static final String ERROR = "errorMessages";
	/**
	 * 
	 * @param message
	 * @param req
	 */
	private static void save(String message,String messageKey, HttpServletRequest req){
		
		List msgs = (List)req.getSession().getAttribute(messageKey);
		if(msgs==null){
			msgs = new ArrayList();
		}
		msgs.add(message);
		req.getSession().setAttribute(messageKey, msgs);
	}
	
	/**
	 * 
	 * @param message
	 * @param req
	 */
	public static void saveErrorMessage(String message,HttpServletRequest req){
		save(message, ERROR, req);
	}
	
	public static void saveSuccessMessage(String message,HttpServletRequest req){
		save(message, SUCCESS, req);
		
	}
	public static void saveErrorMap(Map errorMap,HttpServletRequest req){
		if(errorMap!=null){
			Iterator iter = errorMap.keySet().iterator();
			while(iter.hasNext()){
				String key = (String)iter.next();
				String value = (String)errorMap.get(key);
				save((key+" : " + value),ERROR,req);
			}
		}
		
	}
}
