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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Purpose of this class is to understand if there is a possible conflict or
 * 'noise' in the Mockey store. Conflict being one of the following issues:
 * 
 * <pre>
 * <ul>
 *   <li>Service A and Service B have the same Name </li>
 *   <li>Service A and Service B have 1 or more matching real URLs</li>
 *   <li>Service A and Service B have 1 or more matching mock URLs</li>
 *   <li>Service A and Service B have 1 or more matching Scenarios</li>
 *   <li>Service A and Service C have 1 or more matching real URLs</li>
 * </ul>
 * </pre>
 * 
 * In short, Service A may have 1 or more services with a possible conflict. And
 * each conflicting Service may have 1 ore more reasons for a conflict.
 * 
 * @author clafonta
 * 
 */
public class ConflictInfo {

	// Ugly!!
	private Map<Service, List<Conflict>> conflictMap = new HashMap<Service, List<Conflict>>();

	/**
	 * There can be 1 or more conflicting Services per 1 Service key.
	 * 
	 * @param key
	 *            - Service A
	 * @param conflict
	 *            - Service that has 1 or more matching attributes as Service A,
	 *            e.g. name, url (real or mocked).
	 * @param message
	 *            - a description of the issue.
	 */
	public void addConflict(Service key, Service conflictService, String message) {
		
		List<Conflict> conflictList = this.conflictMap.get(key);
		if(conflictList==null){
			conflictList = new ArrayList<Conflict>();
		}
		
		Conflict conflict = null;
		int index = 0;
		for(Conflict c: conflictList){
			if(c.getService().getId().equals(conflictService.getId())){
				conflict = c;
				break;
			}
			index++;
		}
		
		if(conflict== null){
			conflict = new Conflict(conflictService);
			index = -1;
		}
		
		conflict.addConflictMessage(message);
		if(index>-1){
			conflictList.set(index, conflict);
		}else {
			conflictList.add(conflict);
		}
		this.conflictMap.put(key, conflictList);
		
		
		
	}

	public boolean hasConflictFlag(Service service) {
		boolean conflict = false;
		if(this.conflictMap.get(service)!=null){
			conflict = true;
		}else {
			conflict = false;
		}
		return conflict;
	}

	public List<Conflict> getConflictList(Service service) {
		return this.conflictMap.get(service);
	}

	public class Conflict {
		private Service conflictService = null;
		private List<String> conflictMessageList = new ArrayList<String>();
		public Conflict(Service service){
			this.conflictService = service;
		}
		public Service getService() {
			return this.conflictService;
		}

		public void addConflictMessage(String message) {
			if (!this.conflictMessageList.contains(message)) {
				this.conflictMessageList.add(message);
			}
		}

		public List<String> getConflictMessageList() {
			return this.conflictMessageList;
		}
	}

}
