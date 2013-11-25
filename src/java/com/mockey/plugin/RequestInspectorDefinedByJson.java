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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mockey.model.RequestFromClient;

/**
 * Given a JSON string containing rules to validate the HTTP request, this will
 * provide the logic to inspect and validate HTTP request and build an
 * informative message for results. A few things to note: 
 * 
 * All rules per TYPE will be treated as 'AND'. For example, all key/value 
 * pairs in 'parameters' must exist. 
 * 
 * All rules between TYPEs will be treated as 'OR'. For example, all key/value
 * pair rules must be TRUE in 'parameters' OR all key/value rules must be true
 * for 'headers'. 
 * 
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
 * 	    ],
 *      "url": [
 * 	        {
 * 	            "desc": "The value '123' is required to be present in the RESTful URL.",
 * 	            "value_rule_arg": "\\b123\\b",
 * 	            "value_rule_type": "regex_required"
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
	private Map<RequestRuleType, List<String>> errorMapByKey = new HashMap<RequestRuleType, List<String>>();
	private Map<RequestRuleType, Boolean> rulesDefinedForType = new HashMap<RequestRuleType, Boolean>();
	private int totalRuleCount = 0; 
	private int validRuleCount = 0;

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
	 * 
	 * @return the number of rules processed post analysis.
	 */
	public int getTotalRuleCount() {
		return this.totalRuleCount;
	}
	
	/**
	 * 
	 * @return the number of rules that had a positive outcome. 
	 */
	public int getValidRuleCount() {
		return this.validRuleCount;
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
		analyze(RequestRuleType.RULE_TYPE_FOR_PARAMETERS,
				request.getParameters());
		
		// *************
		// Headers
		// *************
		analyze(RequestRuleType.RULE_TYPE_FOR_HEADERS,
				request.getHeaderInfoAsMap());

		// *************
		// RULE_FOR_BODY ??
		// *************
		Map<String, String[]> postBodyMap = new HashMap<String, String[]>();
		if (request.hasPostBody()) {
			postBodyMap.put(RequestRuleType.RULE_TYPE_FOR_BODY.toString(),
					new String[] { request.getBodyInfo() });
		}
		analyze(RequestRuleType.RULE_TYPE_FOR_BODY, postBodyMap);

		// *************
		// Url
		// *************
		Map<String, String[]> urlMap = new HashMap<String, String[]>();
		urlMap.put(RequestRuleType.RULE_TYPE_FOR_URL.toString(),
				new String[] { request.getRequestURL() });
		analyze(RequestRuleType.RULE_TYPE_FOR_URL, urlMap);

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
	private void analyze(RequestRuleType ruleType,
			Map<String, String[]> keyValues) {

		// Validate parameters.
		try {

			// FOR PARAMETERs and HEADERs
			JSONArray parameterArray = this.json.getJSONArray(ruleType
					.toString());

			for (int i = 0; i < parameterArray.length(); i++) {
				JSONObject jsonRule = parameterArray.getJSONObject(i);

				this.totalRuleCount++;
				try {
					RequestRule requestRule = new RequestRule(jsonRule,
							ruleType);
					this.rulesDefinedForType.put(ruleType, new Boolean(true));
					if (RequestRuleType.RULE_TYPE_FOR_BODY.equals(ruleType)) {
						String[] values = keyValues
								.get(RequestRuleType.RULE_TYPE_FOR_BODY
										.toString());
						if (requestRule.evaluate(values)) {
							addErrorMessage(ruleType, requestRule);
						}else {
							this.validRuleCount++;
						}
					} else if (RequestRuleType.RULE_TYPE_FOR_URL
							.equals(ruleType)) {
						String[] values = keyValues
								.get(RequestRuleType.RULE_TYPE_FOR_URL
										.toString());
						if (requestRule.evaluate(values)) {
							addErrorMessage(ruleType, requestRule);
						}else {
							this.validRuleCount++;
						}
					} else {
						// For HEADERS and PARAMETERS
						if (requestRule.getKey() != null && requestRule
										.getKey().contains("*")) {
							// We treat this as a wild-card.
							Iterator<String> allKeys = keyValues.keySet()
									.iterator();
							List<String> allValues = new ArrayList<String>();
							while (allKeys.hasNext()) {
								String key = allKeys.next();
								String[] vals = keyValues.get(key);
								for (String v : vals) {
									allValues.add(v);
								}
							}
							// Get ALL values from all parameters, and evaluate. 
							if (requestRule.evaluate(allValues
									.toArray(new String[allValues.size()]))) {
								addErrorMessage(ruleType,
										requestRule);
							}else {
								this.validRuleCount++;
							}
						} else {
							// We have non-null, and non-empty keys.
							// Apply specific rules.
							// Keys in RULES and INCOMING maps should 
							// be case insensitive!
							Iterator<String> allKeys = keyValues.keySet()
									.iterator();
							while (allKeys.hasNext()) {
								String key = allKeys.next();
								if(key.equalsIgnoreCase(requestRule
										.getKey())) {
									String[] values = keyValues.get(key);
									if (requestRule.evaluate(values)) {
										addErrorMessage(ruleType,
												requestRule);
									} else {
										this.validRuleCount++;
									}
								}
								
							}
						}
					}

				} catch (RequestRuleException e) {
					addErrorMessage(ruleType,
							"Invalid JSON rule setup. " + e.getMessage());
				}

			}

		} catch (JSONException e) {

			// Not necessarily an error. Could be missing
			logger.debug("Request Inspection JSON rules does not have rule defined for '"
					+ ruleType.toString() + "'");
		}

	}

	/**
	 * 
	 * @param type
	 * @param error
	 */
	private void addErrorMessage(RequestRuleType type, RequestRule requestRule) {
		List<String> errorListByKeyType = this.errorMapByKey.get(type);
		if (errorListByKeyType == null) {
			errorListByKeyType = new ArrayList<String>();
		}

		// Build
		StringBuilder sb = new StringBuilder();
		sb.append("ISSUE: Rule of type '" + type + "'. ");
		if (!RequestRuleType.RULE_TYPE_FOR_BODY.equals(type)
				&& !RequestRuleType.RULE_TYPE_FOR_URL.equals(type)) {
			sb.append(" Belonging to key name of '" + requestRule.getKey()
					+ "'. ");
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
	private void addErrorMessage(RequestRuleType type, String msg) {
		List<String> errorListByKeyType = this.errorMapByKey.get(type);
		if (errorListByKeyType == null) {
			errorListByKeyType = new ArrayList<String>();
		}
		errorListByKeyType.add(msg);
		this.errorMapByKey.put(type, errorListByKeyType);
	}

//	/**
//	 * Method should be called post analysis.
//	 * 
//	 * @return true if one or more errors exist regardless of type.
//	 */
//	public boolean hasErrors_() {
//		if (this.errorMapByKey.isEmpty()) {
//			return false;
//		} else {
//			return true;
//		}
//	}
	
	/**
	 * 
	 * @return true if ALL rules pass for PARAMETERS or BODY or HEADERS or URL
	 */
	public boolean hasAnySuccessForAtLeastOneRuleType(){
		boolean success = false;
		
		Iterator<RequestRuleType> iter = rulesDefinedForType.keySet().iterator();
		while(iter.hasNext()){
			RequestRuleType type = iter.next();
			List<String> errors = this.errorMapByKey.get(type);
			if(errors==null || errors.size() == 0){
				success = true;
				break;
			}
		}
		return success;
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
		for (RequestRuleType key : this.errorMapByKey.keySet()) {
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

	public static void main_(String[] args) {
		// String valueRuleArg =
		// "^((1[0-2]|0?[1-9])/(3[01]|[12][0-9]|0?[1-9])/(?:[0-9]{2})?[0-9]{2})?$";
		// String value = "10/23/1972";
		// try {
		//
		// Pattern pattern = Pattern.compile(valueRuleArg);
		// Matcher matcher = pattern.matcher(value);
		// if (!matcher.find()) {
		// System.out.println("No match");
		// } else {
		// System.out.println("yes, Match");
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//Map<String, String[]> test = new HashMap<String, String[]>();

	}
}
