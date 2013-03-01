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
package com.mockey.ui;

public interface ServiceConfigurationAPI {

	public static final String API_SERVICE_CONFIGURATION_NAME = "Service Configuration";
	public static final String API_SERVICE_NAME = "serviceName";
	public static final String API_SERVICE_ID = "serviceId";
	// SCENARIO RESONSE JSON SCHEMA
	public static final String API_SERVICE_SCHEMA = "serviceSchema";
	public static final String API_SERVICE_SCHEMA_ENABLE_FLAG = "serviceSchemaEnableFlag";

	// REQUEST JSON VALIDATION RULES

	public static final String API_SERVICE_REQUEST_INSPECTOR_RULES = "requestInspectorJsonRules";
	public static final String API_SERVICE_REQUEST_INSPECTOR_RULES_ENABLE_FLAG = "requestInspectorJsonRulesEnableFlag";

	public static final String API_TRANSIENT_STATE = "transientState";
	public static final String API_DEFAULT_SCENARIO_ID = "defaultScenarioId";
	public static final String API_DEFAULT_SCENARIO_NAME = "defaultScenarioName";
	public static final String API_SERVICE_SCENARIO_NAME = "scenarioName";
	public static final String API_SERVICE_SCENARIO_ID = "scenarioId";
	public static final String API_SERVICE_RESPONSE_TYPE = "serviceResponseType";
	public static final String API_SERVICE_RESPONSE_TYPE_VALUE_PROXY = "proxy";
	public static final String API_SERVICE_RESPONSE_TYPE_VALUE_STATIC = "static";
	public static final String API_SERVICE_RESPONSE_TYPE_VALUE_DYNAMIC = "dynamic";
	public static final String API_SERVICE_HANGTIME = "hangTime";
	public static final String API_SERVICE_REQUEST_INSPECTOR_NAME = "requestInspectorName";
}
