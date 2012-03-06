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
package com.mockey.storage;

import java.util.List;

import org.testng.annotations.Test;

import com.mockey.model.IRequestInspector;
import com.mockey.model.SampleRequestInspector;

@Test
public class TestInMemoryMockeyStorage {

	public void testRequestInspector(){
		IMockeyStorage store = StorageRegistry.MockeyStorage;
		SampleRequestInspector arg = new SampleRequestInspector();
		store.saveOrUpdateIRequestInspector(arg);
		
		
		
		List<IRequestInspector> list = store.getRequestInspectorList();
		assert (list.size() == 1 ):"Expected list size 1 but was " +list.size();
		
		IRequestInspector arg2 = store.getRequestInspectorByClass(SampleRequestInspector.class);
		assert (arg2!=null) : "Was expecting a request inspector instance but got null";
		
		store.saveOrUpdateIRequestInspector(arg);
		list = store.getRequestInspectorList();
		assert (list.size() == 1 ):"Expected list size 1 but was " +list.size();
	}
}
