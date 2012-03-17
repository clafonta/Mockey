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
import java.util.List;

/**
 * A helper class to manage pointers to classes within a package. Why not use
 * <code>java.lang.Package</code>? This helper object is used in brute force
 * search results of classes, possibly not yet lazily-loaded by the class
 * loader.
 * 
 * @author chadlafontaine
 * 
 */
class PackageInfo {

	private String name;
	private List<String> classesInPackageList = new ArrayList<String>();

	public PackageInfo(String packageName) {

		this.name = packageName;
	}

	public String getName() {
		return name;
	}

	public List<String> getClassList() {
		return this.classesInPackageList;

	}

	public void addClassNameToPackage(String className) {
		this.classesInPackageList.add(className);
	}

}
