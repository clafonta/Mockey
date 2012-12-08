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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Given a JSON string containing rules to validate the HTTP request, this will
 * provide results
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
 * 	    ]
 * 	}
 * </pre>
 * 
 * @author chadlafontaine
 * 
 */
public class RequestInspectorDefinedByJson implements IRequestInspector {

	public static final String PARAMETERS = "parameters";
	public static final String HEADER = "headers";
	public static final String RULE_DESC = "desc";
	public static final String RULE_KEY = "key";
	public static final String VALUE_RULE_ARG = "value_rule_arg";
	public static final String VALUE_RULE_TYPE = "value_rule_type";
	private JSONObject json = null;
	private Map<String, String> errors = new HashMap<String, String>();

	/**
	 * 
	 * @param json
	 *            - request inspection rules.
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
	public void analyze(HttpServletRequest request) {

		// Since we apply the same evaluation logic to parameters and headers,
		// we'll move the key-value pairs into a Map, and process the rules
		// accordintly.

		// *************
		// Parameters
		// *************
		Map<String, String[]> valueMap = new HashMap<String, String[]>();
		Enumeration<String> enumNames = request.getParameterNames();
		while (enumNames.hasMoreElements()) {
			String paramKey = enumNames.nextElement();
			String[] parameterValues = request.getParameterValues(paramKey);
			StringBuffer sb = new StringBuffer();
			valueMap.put(paramKey, parameterValues);
		}
		analyze(PARAMETERS, valueMap);

		// *************
		// Headers
		// *************
		enumNames = request.getHeaderNames();
		valueMap = new HashMap<String, String[]>();
		while (enumNames.hasMoreElements()) {
			String paramKey = enumNames.nextElement();
			valueMap.put(paramKey, new String[] { request.getHeader(paramKey) });
		}
		analyze(HEADER, valueMap);

	}

	private void analyze(String type, Map<String, String[]> keyValues) {

		// Validate parameters.
		try {

			// FOR PARAMETERs
			JSONArray parameterArray = this.json.getJSONArray(type);

			for (int i = 0; i < parameterArray.length(); i++) {
				JSONObject keyValueRuleArg = parameterArray.getJSONObject(i);
				String desc = keyValueRuleArg.getString(RULE_DESC);
				if (desc == null) {
					desc = "";
				}
				String key = keyValueRuleArg.getString(RULE_KEY);
				String valueRuleArg = keyValueRuleArg.getString(VALUE_RULE_ARG);
				String valueRuleType = keyValueRuleArg
						.getString(VALUE_RULE_TYPE);
				String[] values = keyValues.get(key);

				if (InspectorRuleType.REGEX_REQUIRED
						.equalsString(valueRuleType) && values == null) {
					String errorMsgRequired = type
							+ " with key '"
							+ key
							+ "' requires a value but is 'null'. Rule argument is '"
							+ valueRuleArg + "'. " + desc;
					this.errors.put(key, errorMsgRequired);
				} else if ((InspectorRuleType.REGEX_OPTIONAL
						.equalsString(valueRuleType) && values != null)
						|| (InspectorRuleType.REGEX_REQUIRED
								.equalsString(valueRuleType))) {

					for (String value : values) {
						String errorMsgRequired = type
								+ " with key '"
								+ key
								+ "' does not validate against the regular expression '"
								+ valueRuleArg + "' with value +'" + value
								+ "'. " + desc;
						try {

							Pattern pattern = Pattern.compile(valueRuleArg);
							Matcher matcher = pattern.matcher(value);
							if (!matcher.find()) {
								this.errors.put(key, errorMsgRequired);
							}
						} catch (Exception e) {
							this.errors.put(key, errorMsgRequired);
						}
					}

				} else if (InspectorRuleType.STRING_REQUIRED
						.equalsString(valueRuleType)) {

					String errorMsg = type
							+ " with key '"
							+ key
							+ "' must contain a non-empty string value and contain string/character '"
							+ valueRuleArg + "'. " + desc;
					boolean found = false;
					if (values != null) {
						for (String value : values) {
							if (value != null
									&& value.toLowerCase()
											.trim()
											.indexOf(
													valueRuleArg.toLowerCase()
															.trim()) > -1) {
								found = true;
								break;
							}
						}
					}
					if (!found) {
						this.errors.put(key, errorMsg);
					}

				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			this.errors.put("Invalid JSON",
					"You have invalid JSON defined request validation rules: "
							+ e.getMessage());
		}

	}

	@Override
	public String getPostAnalyzeResultMessage() {
		StringBuffer sb = new StringBuffer();
		int i = 1;
		for (String key : this.errors.keySet()) {
			sb.append(i++ + ") " + this.errors.get(key) + " \n");
		}
		return sb.toString();
	}

	@Override
	public boolean isGlobal() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args) {
		String valueRuleArg = "^(1[0-2]|0?[1-9])/(3[01]|[12][0-9]|0?[1-9])/(?:[0-9]{2})?[0-9]{2}$";
		String value = " 10/23/1972";
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
