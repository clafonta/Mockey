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
import java.util.List;

/**
 * 
 * @author chadlafontaine
 *
 */
public class ApiDocAttribute {

	private String fieldName = null;
	private String fieldDescription = null;
	private String fieldOption = null;
	private String example = null;
	private List<ApiDocFieldValue> fieldValues = new ArrayList<ApiDocFieldValue>();
	private List<ApiDocAttribute> childAttributes = new ArrayList<ApiDocAttribute>();
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldDescription() {
		return fieldDescription;
	}
	public void setFieldDescription(String fieldDescription) {
		this.fieldDescription = fieldDescription;
	}
	public String getFieldOption() {
		return fieldOption;
	}
	public void setFieldOption(String fieldOption) {
		this.fieldOption = fieldOption;
	}
	public String getExample() {
		return example;
	}
	public void setExample(String example) {
		this.example = example;
	}
	public void setChildAttributes(List<ApiDocAttribute> childAttributes) {
		this.childAttributes = childAttributes;
	}
	public void addChildAttributes(ApiDocAttribute childAttribute) {
		this.childAttributes.add(childAttribute);
	}
	public List<ApiDocAttribute> getChildAttributes() {
		return childAttributes;
	}
	public void setFieldValues(List<ApiDocFieldValue> fieldValues) {
		this.fieldValues = fieldValues;
	}
	public List<ApiDocFieldValue> getFieldValues() {
		return fieldValues;
	}
	public void addFieldValues(ApiDocFieldValue fieldValue) {
		this.fieldValues.add(fieldValue);
	}
	
	
	
}
