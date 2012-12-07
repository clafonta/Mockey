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
 * { 
 *     parameters: [
 *        key: value,   // Value is a reqex
 *        key: value],  // Value is a regex
 *     header: [ 
 *        // same as parameters
 *     body:   []
 * 
 * </pre>
 * 
 * @author chadlafontaine
 * 
 */
public class RequestInspectorDefinedByJson implements IRequestInspector {

	public static final String PARAMETERS = "parameters";
	public static final String PARAMETER_KEY = "key";
	public static final String PARAMETER_VALUE_RULE_ARG = "value_rule_arg";
	public static final String PARAMETER_VALUE_RULE_TYPE = "value_rule_type";
	private JSONObject json = null;
	private Map<String, String> errors = new HashMap<String, String>();

	/**
	 * 
	 * @param json
	 * @throws JSONException
	 */
	public RequestInspectorDefinedByJson(String json) throws JSONException {

		this.json = new JSONObject(json);

	}

	@Override
	public void analyze(HttpServletRequest request) {

		// Validate parameters.
		try {

			// FOR PARAMETERs
			JSONArray parameterArray = this.json.getJSONArray(PARAMETERS);

			for (int i = 0; i < parameterArray.length(); i++) {
				JSONObject keyValueRuleArg = parameterArray.getJSONObject(i);
				String key = keyValueRuleArg.getString(PARAMETER_KEY);
				String valueRuleArg = keyValueRuleArg
						.getString(PARAMETER_VALUE_RULE_ARG);
				String valueRuleType = keyValueRuleArg
						.getString(PARAMETER_VALUE_RULE_TYPE);
				String value = request.getParameter(key);

				if ((InspectorRuleType.REGEX_OPTIONAL
						.equalsString(valueRuleType) && value != null)
						|| (InspectorRuleType.REGEX_REQUIRED
								.equalsString(valueRuleType))) {

					String errorMsgRequired = "Parameter '"
							+ key
							+ "' is does not validate against the regular expression '"
							+ valueRuleArg + "' with value +'" + value + "'";
					try {

						Pattern pattern = Pattern.compile(valueRuleArg);
						Matcher matcher = pattern.matcher(value);
						if (!matcher.find()) {
							this.errors.put(key, errorMsgRequired);
						}
					} catch (Exception e) {
						this.errors.put(key, errorMsgRequired);
					}

				} else if (InspectorRuleType.STRING_REQUIRED
						.equalsString(valueRuleType)) {
					String msg = "Parameter key '" + key
							+ "' must contain string value '" + valueRuleArg
							+ "'";
					boolean found = false;
					String[] parameterValues = request.getParameterValues(key);
					if (parameterValues != null) {
						for (String parmVal : parameterValues) {
							if (parmVal.toLowerCase().indexOf(valueRuleArg.toLowerCase()) > -1) {
								found = true;
								break;
							}
						}
					}
					if (!found) {
						this.errors.put(key, msg);
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
		for (String key : this.errors.keySet()) {
			sb.append(this.errors.get(key) + " \n");
		}
		return sb.toString();
	}

	@Override
	public boolean isGlobal() {
		// TODO Auto-generated method stub
		return false;
	}

}
