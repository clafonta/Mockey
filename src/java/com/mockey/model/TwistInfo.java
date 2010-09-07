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

import com.mockey.ui.PatternPair;

/**
 * Provides information for pattern match and replace - if X matches anything in
 * the origination pattern match list, then replace it with destination pattern.
 * 
 * Why is this needed? This is useful when an application is making requests to
 * URLs belonging to <i>User Acceptance Testing Environment A</i> but one really
 * wants to be pointing to <i>Developer Sandbox Environment</i>. 
 * 
 * @author chadlafontaine
 * 
 */
public class TwistInfo implements PersistableItem {

	private Long id;
	private String name;
	private List<PatternPair> patternPairList = new ArrayList<PatternPair>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PatternPair> getPatternPairList() {
		return patternPairList;
	}

	public void setPatternPairList(List<PatternPair> patternPairList) {
		this.patternPairList = patternPairList;
	}

	public void addPatternPair(PatternPair patternPair) {
		this.patternPairList.add(patternPair);
	}
	
}
