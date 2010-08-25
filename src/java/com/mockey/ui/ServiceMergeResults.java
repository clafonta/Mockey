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
package com.mockey.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServiceMergeResults {
    private List<String> conflicts = new ArrayList<String>();
    private List<String> additions = new ArrayList<String>();
    public void addConflictMsg(String conflictMsg) {
        this.conflicts.add(conflictMsg);
    }
    public List<String> getConflictMsgs() {
        return conflicts;
    }
    public String getConflictMsg() {
    	return buildMsg(this.conflicts);
    }
    
    public String getAdditionMsg() {
    	return buildMsg(this.additions);
    }
    public void addAdditionMsg(String additionMsg) {
        this.additions.add(additionMsg);
    }
    public List<String> getAdditionMessages() {
        return additions;
    }
    private String buildMsg(List<String> list) {
    	StringBuffer s = new StringBuffer();
    	Iterator<String> iter = list.iterator();
    	while(iter.hasNext()){
    		s.append(iter.next());
    	}
        return s.toString();
    }
    
    
    
    
}
