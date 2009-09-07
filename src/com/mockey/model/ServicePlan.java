/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
	@SuppressWarnings("unchecked")
	private List planItemList = new ArrayList();
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
	/**
	 * 
	 * @return a list of PlanItem objects
	 */
	@SuppressWarnings("unchecked")
	public List getPlanItemList() {
		return planItemList;
	}
	@SuppressWarnings("unchecked")
	public void setPlanItemList(List planItemList) {
		this.planItemList = planItemList;
	}
	@SuppressWarnings("unchecked")
	public void addPlanItem(PlanItem planItem){
		this.planItemList.add(planItem);
	}
	
	
	
	
}
