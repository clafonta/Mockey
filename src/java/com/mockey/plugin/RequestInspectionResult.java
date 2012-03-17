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

import java.util.ArrayList;
import java.util.List;

/**
 * Captures 0 or more messages as a result of evaluating incoming request via 1
 * or more RequestInspector(s).
 * 
 * @author chadlafontaine
 * 
 */
public class RequestInspectionResult {
	private List<String> resultMessageList = new ArrayList<String>();

	/**
	 * 
	 * @return - true if a result message is available, false otherwise.
	 */
	public boolean hasResultMessages() {
		if (this.resultMessageList != null && this.resultMessageList.size() > 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 
	 * @return non-null list, could be empty.
	 * @see #hasResultMessages()
	 */
	public List<String> getResultMessageList() {
		return resultMessageList;
	}

	
	/**
	 * 
	 * @param resultMessage 
	 */
	void addResultMessage(String resultMessage) {
		if (resultMessage != null && resultMessage.trim().length() > 0 && !this.resultMessageList.contains(resultMessage)) {
			this.resultMessageList.add(resultMessage);
		}

	}

}
