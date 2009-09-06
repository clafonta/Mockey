/*
 * Copyright 2002-2009 the original author or authors.
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
package com.mockey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mockey.model.Item;

/**
 * Keeps an ordered map of items, order based on 'when defined and added to this object'
 * 
 * @author chad.lafontaine
 *
 */
public class OrderedMap<T extends Item> extends HashMap<Long, T> 
	implements Map<Long, T> {
	
	private static final long serialVersionUID = -1654150132938363942L;
	private static Logger logger = Logger.getLogger(OrderedMap.class);
	private ArrayList<T> backingList = new ArrayList<T>();
	
	public Item save(T item){
		if(item!=null){
			if(item.getId()!=null){
				this.put(item.getId(), item); 
			}else {
				Long nextNumber = this.getNextValue();
				item.setId(nextNumber);
				this.put(nextNumber, item);
			}
			backingList.add(item);
		}
		logger.debug("Saving to store with ID:"  + item.getId());
		return item;
	}
	
	
	
	private Long getNextValue(){
		Iterator iter = this.keySet().iterator();
		Long nextValue = new Long(0);
		while (iter.hasNext()) {
			Long key = (Long) iter.next();
			if(key.longValue() > nextValue.longValue()){
				nextValue = key;
			}
		}
		nextValue = new Long(nextValue.longValue() + 1);
		return nextValue;
	}
	
	public List<T> getOrderedList(){

		// Temp
		List<Long> keyOrder = new ArrayList<Long>();		
		Iterator iter = this.keySet().iterator();
		while (iter.hasNext()) {
			Long key = (Long) iter.next();
			Iterator keyIter = keyOrder.iterator();
			int index = 0;
			while(keyIter.hasNext()){
				Long current = (Long)keyIter.next();
				if(current.longValue() > key.longValue()){
					break;
				}
				index++;
			}
			keyOrder.add(index, key);
		}

		// Ordered key list.
		List<T> arrayList = new ArrayList<T>();
		Iterator orderedIter = keyOrder.iterator();
		while(orderedIter.hasNext()){
			Long key = (Long)orderedIter.next();
			arrayList.add(this.get(key));
		}
		return arrayList;
	}
}
