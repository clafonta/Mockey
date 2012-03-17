/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2012  Authors:
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

import org.testng.annotations.Test;

@Test
public class TestPackageInfoPeerClassFinder {
	
	@Test 
	public void testGetPackageNameFromPackageInfoClass(){
		
		String val = "com.xxxx.yyyy";
		String arg1 = PackageInfoPeerClassFinder.getPackageNameFromPackageInfoClass("com/xxxx/yyyy/package-info.class");
		assert (arg1.equals(val)): "Was expecting " +val+" but was "+arg1;
		
		String arg2 = PackageInfoPeerClassFinder.getPackageNameFromPackageInfoClass(null);
		assert (arg2 == null): "Was expecting null but was "+arg2;
		
	}
	@Test
	public void testCleanClassName() {
		
		String val = "com.xxx.yyy.Class";
		String arg1 = PackageInfoPeerClassFinder.getCleanClassName("com/xxx/yyy/Class.class");
		assert (arg1.equals(val)): "Was expecting " +val+" but was "+arg1;
		
		String arg2 = PackageInfoPeerClassFinder.getCleanClassName("com/xxx/yyy/Class");
		assert (arg2.equals(val)): "Was expecting " +val+" but was "+arg2;

		String arg3 = PackageInfoPeerClassFinder.getCleanClassName("com.xxx/yyy/Class");
		assert (arg3.equals(val)): "Was expecting " +val+" but was "+arg3;
		
		String arg4 = PackageInfoPeerClassFinder.getCleanClassName("com.xxx.yyy.Class");
		assert (arg4.equals(val)): "Was expecting " +val+" but was "+arg4;
		
		String arg5 = PackageInfoPeerClassFinder.getCleanClassName(null);
		assert (arg5 == null): "Was expecting to null but was "+arg5;

	}

}