/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2013  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
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
/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2010  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
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
package com.mockey.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.model.RequestFromClient;

/**
 * Given a JSON string containing rules to validate the HTTP request, this will
 * provide the logic to inspect and validate HTTP request and build an
 * informative message for results.
 * 
 * <pre>
 *  	{
 * 	    "parameters": [
 * 	        {
 * 	            "key": "ticker",
 * 	            "desc": "A value must be provided with the 'ticker' parameter, and it must contain the letter 'g'. Providing 'GOOG' is valid, but 'FB' will flag an error.",
 * 	            "value_rule_arg": "g",
 * 	            "value_rule_type": "string_required"
 * 	        },
 * 	        {
 * 	            "key": "date",
 * 	            "desc": "Optional date value, but if provided, must satisfy mm/DD/yyyy format.",
 * 	            "value_rule_arg": "^(((0[1-9]|[12]\\d|3[01])\\/(0[13578]|1[02])\\/((19|[2-9]\\d)\\d{2}))|((0[1-9]|[12]\\d|30)\\/(0[13456789]|1[012])\\/((19|[2-9]\\d)\\d{2}))|((0[1-9]|1\\d|2[0-8])\\/02\\/((19|[2-9]\\d)\\d{2}))|(29\\/02\\/((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))))$",
 * 	            "value_rule_type": "regex_optional"
 * 	        }
 * 	    ],
 * 	    "headers": [
 * 	        {
 * 	            "key": "page_id",
 * 	            "desc": "A page_id value MUST be provided, any non-empty string value.",
 * 	            "value_rule_arg": "",
 * 	            "value_rule_type": "string_required"
 * 	        }
 * 	    ],
 *      "body": [
 * 	        {
 * 	            "desc": "The text 'username' is required to be present in the POST body.",
 * 	            "value_rule_arg": "username",
 * 	            "value_rule_type": "string_required"
 * 	        }
 * 	    ]
 * 	}
 * </pre>
 * 
 * @author chadlafontaine
 * 
 */
public class RequestInspectorDefinedByJson implements IRequestInspector {

	

	private JSONObject json = null;
	private Logger logger = Logger
			.getLogger(RequestInspectorDefinedByJson.class);
	private Map<String, List<String>> errorMapByKey = new HashMap<String, List<String>>();

	/**
	 * 
	 * @param json
	 *            - request inspection rules
	 * @throws JSONException
	 */
	public RequestInspectorDefinedByJson(String json) throws JSONException {

		this.json = new JSONObject(json);

	}

	/**
	 * Will apply request inspection rules as defined in JSON, only looking at
	 * parameters and headers, not Body.
	 * 
	 * @param request
	 *            - HTTP request to analyze.
	 */
	public void analyze(RequestFromClient request) {

		// Since we apply the same evaluation logic to parameters and headers,
		// we'll move the key-value pairs into a Map, and process the rules
		// accordingly.

		// *************
		// Parameters
		// *************
		analyze(RequestRuleType.RULE_TYPE_FOR_PARAMETERS, request.getParameters());

		// *************
		// Headers
		// *************
		analyze(RequestRuleType.RULE_TYPE_FOR_HEADERS, request.getHeaderInfoAsMap());

		// *************
		// RULE_FOR_BODY ??
		// *************
		Map<String, String[]> postBodyMap = new HashMap<String, String[]>();
		if (request.hasPostBody()) {
			postBodyMap.put(RequestRuleType.RULE_TYPE_FOR_BODY.toString(), new String[] {request.getBodyInfo()});
		}
		analyze(RequestRuleType.RULE_TYPE_FOR_BODY, postBodyMap);

	}

	/**
	 * Based on type, method will extra validation rules and evaluate the
	 * keyValues mapping.
	 * 
	 * @param type
	 *            - Valid values are RULE_FOR_HEADERS or RULE_FOR_PARAMETERS
	 * @param keyValues
	 *            - An array of possible values associated to a key
	 * @see #RULE_FOR_HEADERS
	 * @see #RULE_FOR_PARAMETERS
	 */
	private void analyze(RequestRuleType ruleType, Map<String, String[]> keyValues) {

		// Validate parameters.
		try {

			// FOR PARAMETERs and HEADERs
			JSONArray parameterArray = this.json.getJSONArray(ruleType.toString());

			for (int i = 0; i < parameterArray.length(); i++) {
				JSONObject jsonRule = parameterArray.getJSONObject(i);

				try {
					RequestRule requestRule = new RequestRule(jsonRule, ruleType);

					if (RequestRuleType.RULE_TYPE_FOR_BODY.equals(ruleType)) {
						String[] values = keyValues.get(RequestRuleType.RULE_TYPE_FOR_BODY.toString());
						if (requestRule.evaluate(values)) {
							addErrorMessage(ruleType.toString(), requestRule);
						}
					} else {

						String[] values = keyValues.get(requestRule.getKey());
						if (requestRule.evaluate(values)) {
							addErrorMessage(ruleType.toString(), requestRule);
						}
					}

				} catch (RequestRuleException e) {
					addErrorMessage(ruleType.toString(),
							"Invalid JSON rule setup. " + e.getMessage());
				}

			}

		} catch (JSONException e) {

			// Not necessarily an error. Could be missing
			logger.debug(
					"Request Inspection JSON rules does not have rule defined for '"
							+ ruleType.toString() + "'", e);
		}

	}

	/**
	 * 
	 * @param type
	 * @param error
	 */
	private void addErrorMessage(String type, RequestRule requestRule) {
		List<String> errorListByKeyType = this.errorMapByKey.get(type);
		if (errorListByKeyType == null) {
			errorListByKeyType = new ArrayList<String>();
		}

		// Build
		StringBuilder sb = new StringBuilder();
		sb.append("ISSUE: Rule of type '"+type+"'. ");
		if(!RequestRuleType.RULE_TYPE_FOR_BODY.toString().equals(type)){
			sb.append(" Belonging to key name of '"+ requestRule.getKey()+ "'. " );
		}
		for (String issue : requestRule.getIssues()) {
			sb.append(issue + " ");
		}
		errorListByKeyType.add(sb.toString() + " RULE DESC: "
				+ requestRule.getDesc());
		this.errorMapByKey.put(type, errorListByKeyType);
	}

	/**
	 * 
	 * @param type
	 * @param error
	 */
	private void addErrorMessage(String type, String msg) {
		List<String> errorListByKeyType = this.errorMapByKey.get(type);
		if (errorListByKeyType == null) {
			errorListByKeyType = new ArrayList<String>();
		}
		errorListByKeyType.add(msg);
		this.errorMapByKey.put(type, errorListByKeyType);
	}

	/**
	 * If errors exists, this method will build 1 long string representation of
	 * all broken rules, inserting a counter i.e. 1, 2, 3, etc. in front of each
	 * message.
	 * 
	 * @return the result of the validate rules, can be an empty string, but
	 *         never null.
	 */
	public String getPostAnalyzeResultMessage() {
		StringBuffer sb = new StringBuffer();
		int i = 1;
		for (String key : this.errorMapByKey.keySet()) {
			for (String value : this.errorMapByKey.get(key)) {
				sb.append(i++ + ") " + value + " \n");
			}
		}
		return sb.toString();
	}

	@Override
	public boolean isGlobal() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args) {
		String valueRuleArg = "^((1[0-2]|0?[1-9])/(3[01]|[12][0-9]|0?[1-9])/(?:[0-9]{2})?[0-9]{2})?$";
		String value = "10/23/1972";
		try {

			Pattern pattern = Pattern.compile(valueRuleArg);
			Matcher matcher = pattern.matcher(value);
			if (!matcher.find()) {
				System.out.println("No match");
			} else {
				System.out.println("yes, Match");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
