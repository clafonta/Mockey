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
 * A Mock Service Plan is a set of desired scenarios. When selected, a plan will
 * enable a specific scenario per service. For example, a plan called 'Happy
 * Path' would enable the desired scenario per service to satisfy this story:
 * "User logs in, views account, and checks their in-box", while a plan called
 * 'Notify User' would enable the desired scenario per service to satisfy "user
 * logged in and is presented a notification'.
 * 
 * @author chad.lafontaine
 * 
 */
public class ServicePlan implements PersistableItem {
	private Long id;
	private String name;
	private String description;
	private Boolean transientState = new Boolean(false);
	private List<PlanItem> planItemList = new ArrayList<PlanItem>();
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
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<PlanItem> getPlanItemList() {
		return planItemList;
	}
	
	public void setPlanItemList(List<PlanItem> planItemList) {
		this.planItemList = planItemList;
	}
	
	public void addPlanItem(PlanItem planItem){
		this.planItemList.add(planItem);
	}
	public void setTransientState(Boolean transientState) {
		this.transientState = transientState;
	}
	public Boolean getTransientState() {
		return transientState;
	}	
}
