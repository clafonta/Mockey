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
public class TestPluginStore {

	@Test
	public void testRequestInspector() {
		PluginStore store = PluginStore.getInstance();

		// List<String> list = store.getRequestInspectorImplClassNameList();
		// assert (list.size() == 2) : "Expected list size 2 but was " +
		// list.size();
		//
		// assert
		// ("com.mockey.plugin.SampleRequestInspector".equals(list.get(0))) :
		// "Expected to see 'com.mockey.plugin.SampleRequestInspector' but got: "
		// + list.get(0);

	}

	@Test
	public void testForDuplicateRequestInspectorClassNames() {
		PluginStore store = PluginStore.getInstance();

		// store.saveOrUpdateIReqInspectorImplClassName(SampleRequestInspector.class);

		// List<String> list = store.getRequestInspectorImplClassNameList();
		// assert (list.size() == 2) : "Expected list size 2 but was " +
		// list.size();
	}

	@Test
	public void testInstanceCreator() {
		PluginStore store = PluginStore.getInstance();

//		IRequestInspector inspector = store
//				.createInspectorInstanceByClassName("com.mockey.plugin.SampleRequestInspector");
//
//		assert (inspector != null) : "Expected instansiation of a class but got " + inspector;
	}

}
