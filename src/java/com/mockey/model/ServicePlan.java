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

import java.util.List;

import com.mockey.OrderedMap;

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
public class ServicePlan extends StatusCheck implements PersistableItem {
	private Long id;
	private String name;
	private String description;
	private Boolean transientState = new Boolean(false);
	private OrderedMap<PlanItem> planItemStore = new OrderedMap<PlanItem>();

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
		return this.planItemStore.getOrderedList();
	}

	/**
	 * Clears the ServicePlan state, and updates with all plan items.
	 * 
	 * @param planItemList
	 */
	public void setPlanItemList(List<PlanItem> planItemList) {
		this.planItemStore = new OrderedMap<PlanItem>();
		for (PlanItem pI : planItemList) {
			pI.setId(null);
			this.planItemStore.save(pI);
		}
	}

	/**
	 * If plan item has an ID, then it will be updated. 
	 * @param planItem
	 */
	public void addPlanItem(PlanItem planItem) {
		planItemStore.save(planItem);
	}

	public void setTransientState(Boolean transientState) {
		this.transientState = transientState;
	}

	public Boolean getTransientState() {
		return transientState;
	}

	/**
	 * Helper method to check if this Service Plan manages a Service with a
	 * matching name.
	 * 
	 * @return
	 */
	public boolean hasServiceWithMatchingName(String serviceName) {
		boolean foundMatch = false;
		for (PlanItem pi : this.getPlanItemList()) {
			if (pi.getServiceName() != null
					&& pi.getServiceName().equals(serviceName)) {
				foundMatch = true;
				break;
			}

		}
		return foundMatch;
	}

	@Override
	public String toString() {
		return "ServicePlan [id=" + id + ", name=" + name + ", description=" + description + ", transientState="
				+ transientState + ", planItemStore=" + planItemStore + "]";
	}
	
	
}
