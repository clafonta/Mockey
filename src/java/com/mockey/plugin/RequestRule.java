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
package com.mockey.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestRule {

	public static final String RULE_DESC = "desc";
	public static final String RULE_KEY = "key";
	public static final String VALUE_RULE_ARG = "value_rule_arg";
	public static final String VALUE_RULE_TYPE = "value_rule_type";
	private List<String> issueList = new ArrayList<String>();
	private String desc = "";
	private String key = "";
	private String rule = "";
	private String ruleType = "";

	public RequestRule(JSONObject json) throws RequestRuleException {
		this.desc = getRuleValFromRule(RULE_DESC, json, false);
		this.key = getRuleValFromRule(RULE_KEY, json, true);
		this.rule = getRuleValFromRule(VALUE_RULE_ARG, json, true);
		this.ruleType = getRuleValFromRule(VALUE_RULE_TYPE, json, true);
	}

	public String getDesc() {
		return this.desc;
	}

	public String getKey() {
		return this.key;
	}

	public String getRule() {
		return this.rule;
	}

	public String getRuleType() {
		return this.ruleType;
	}

	private String getRuleValFromRule(String key, JSONObject jsonRule,
			boolean required) throws RequestRuleException {
		String value = "";
		try {
			value = jsonRule.getString(key);
		} catch (JSONException e) {

			if (required) {
				throw new RequestRuleException(
						"Request Inspection JSON rule is missing a required attribute with label '"
								+ key + "'");
			}
		}
		return value;

	}

	/**
	 * 
	 * @param values
	 * @return
	 */
	public boolean evaluate(String[] values) {

		if (InspectorRuleType.REGEX_REQUIRED.equalsString(this.getRuleType())
				&& values == null) {

			issueList
					.add("Requires a non-null value to be evaluated by a regex value.");

		} else if ((InspectorRuleType.REGEX_OPTIONAL.equalsString(this
				.getRuleType()) && values != null)
				|| (InspectorRuleType.REGEX_REQUIRED.equalsString(this
						.getRuleType()) && values != null)) {

			for (String value : values) {
				String errorMsgRequired = "Fails to validate against the regular expression '"
						+ this.getRule() + "' with value +'" + value + "'.";
				try {

					Pattern pattern = Pattern.compile(this.getRule());
					Matcher matcher = pattern.matcher(value);
					if (!matcher.find()) {
						issueList.add(errorMsgRequired);
					}
				} catch (Exception e) {
					issueList.add(errorMsgRequired);
				}
			}

		} else if (InspectorRuleType.STRING_REQUIRED.equalsString(this
				.getRuleType())) {

			boolean found = false;
			if (values != null) {
				for (String value : values) {
					if (value != null
							&& value.toLowerCase()
									.trim()
									.indexOf(
											this.getRule().toLowerCase().trim()) > -1) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				if (this.getRule().length() == 0) {
					this.issueList.add("Requires a value.");
				} else {
					this.issueList.add("Requires a value that contains '"
							+ this.getRule() + "'");
				}
			}

		}
		return hasIssues();

	}
	
	/**
	 * 
	 * @return true if this rule has issues
	 */
	public boolean hasIssues(){
		if (this.issueList.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public List<String> getIssues(){
		return this.issueList;
	}

}
