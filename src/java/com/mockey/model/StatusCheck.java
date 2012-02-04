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
package com.mockey.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Extend this class if you need to track meta information, which includes 'last
 * visited' and 'tags'
 * 
 * @author chad.lafontaine
 * 
 */
public abstract class StatusCheck {

	private List<String> tagList = new ArrayList<String>();
	private Long lastVisit = null;
	private final SimpleDateFormat formatter = new SimpleDateFormat(
			"MM/dd/yyyy");

	/**
	 * Add tag to the list. Method ensures no duplication, space trimming, and
	 * is case insensitive. Actually, this method will force lower-case
	 * 
	 * @param tag
	 */
	public void addTagToList(String tag) {

		this.tagList = createUniqueLowercaseTagList(this.tagList, tag);

	}

	/**
	 * Clear's tag list
	 */
	public void clearTagList() {
		this.tagList = new ArrayList<String>();
	}

	/**
	 * 
	 * @param tag
	 */
	public void removeTagFromList(String tag) {

		if (tag != null && tag.trim().length() > 0) {
			String cleanTag = tag.toLowerCase().trim();
			String delims = "[ ]+";
			String[] tokens = cleanTag.split(delims);
			if (this.tagList != null) {
				for (String arg : tokens) {
					this.tagList.remove(arg);
				}
			}
			
			
		}
	}

	public List<String> getTagList() {
		return this.tagList;
	}

	public void setTagList(List<String> argTagList) {
		this.tagList = createUniqueLowercaseTagList(argTagList, null);
	}

	public void setTag(String tag) {
		this.tagList = createUniqueLowercaseTagList(null, tag);
	}

	/**
	 * 
	 * @return alphabetic ordered list
	 */
	public String getTag() {
		StringBuffer sb = new StringBuffer();
		
		if (this.tagList != null) {
			List<String> orderedList = orderAlphabetically(this.tagList);
			for (String arg : orderedList) {
				sb.append(arg + " ");
			}
		}
		return sb.toString().trim().toLowerCase();
	}

	public Long getLastVisit() {
		return lastVisit;
	}

	public void setLastVisit(Long lastVisited) {
		this.lastVisit = lastVisited;
	}

	public boolean hasTag(String tag) {
		boolean hasTag = false;
		if (tag != null && tag.trim().length() > 0) {
			String cleanTag = tag.trim().toLowerCase();
			String delims = "[ ]+";
			String[] tokens = cleanTag.split(delims);
			for (String token : tokens) {
				for (String tagArg : this.tagList) {
					if (tagArg.equals(token)) {
						hasTag = true;
						return hasTag;
					}
				}
			}
		}
		return hasTag;

	}

	/**
	 * 
	 * @param tagList
	 *            - can be null; if not null, then all values will be lower
	 *            cased, duplicates removed, values trimmed. Example: [a, b, def
	 *            def, HIG] will return a list of [a, b, def, hig]
	 * @param tagArg
	 *            - can be null; if not null, value will be split based on the
	 *            'space' character. Example 'abc def' will have a return list
	 *            value of 2
	 * @return Always a String list containing 0 or more String values, each
	 *         lower-case
	 */
	private List<String> createUniqueLowercaseTagList(List<String> tagList,
			String tagArg) {

		// LIST CLEAN
		List<String> targetTagList = new ArrayList<String>();
		if (tagList != null) {
			// REMOVE DUPLICATES AND ENSURE TO SPLIT VALUES BASED ON 'space'
			// CHARACTER
			for (String obj : tagList) {

				String[] splitTagArg = obj.toLowerCase().trim().split(" ");
				for (String cleanObj : splitTagArg) {
					if (cleanObj.toLowerCase().trim().length() > 0
							&& !targetTagList.contains(cleanObj.toLowerCase()
									.trim())) {
						targetTagList.add(cleanObj);
					}
				}
			}
		}

		// TAG HANDLING
		//

		// Let's make sure no duplicates, case-insensitive
		if (tagArg != null) {
			String[] splitTagArg = tagArg.toLowerCase().trim().split(" ");
			for (String cleanTag : splitTagArg) {
				if (cleanTag.toLowerCase().trim().length() > 0
						&& !targetTagList.contains(cleanTag.toLowerCase()
								.trim())) {
					targetTagList.add(cleanTag);
				}
			}
		}
		return targetTagList;

	}

	/**
	 * Helper method.
	 * 
	 * @return if available, in MM/dd/yyyy format.
	 */
	public String getLastVisitSimple() {
		String time = "";

		if (this.getLastVisit() != null && this.getLastVisit() > 0) {
			time = formatter.format(new Date(new Long(this.getLastVisit())));
		}
		return time;
	}
	
	/**
	 * Returns the services list ordered alphabetically.
	 * 
	 * @param services
	 * @return
	 */
	private List<String> orderAlphabetically(
			List<String> stringList) {

		// Custom comparator
		class StringComparator implements Comparator<String> {

			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(
						s2);

			}

		}
		// Sort me.
		Collections.sort(stringList, new StringComparator());

		return stringList;
	}

}
