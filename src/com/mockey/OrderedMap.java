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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mockey.model.PersistableItem;

/**
 * Keeps an ordered map of items, with a key of an number based on 'when defined and added to this 
 * object'.  Sort of like an auto-incrementing column in a db.
 * <p>
 * So:
 * <p>
 * <code>
 * OrderedMap<MyObj> myMap = new OrderedMap<MyObj>();<br>
 * 0 == myMap.save(new MyObj()).getId();<br>
 * 1 == myMap.save(new MyObj()).getId();<br>
 * 2 == myMap.save(new MyObj()).getId();<br>
 * </code>
 * 
 * @author chad.lafontaine
 *
 */
public class OrderedMap<T extends PersistableItem> extends HashMap<Long, T> implements Map<Long, T> {
	
	private static final long serialVersionUID = -1654150132938363942L;
	private static Logger logger = Logger.getLogger(OrderedMap.class);
	
	public PersistableItem save(T item){
		logger.debug("saving item: "+item.toString());
		if(item!=null){
			if(item.getId()!=null){
				this.put(item.getId(), item); 
			}else {
				Long nextNumber = this.getNextValue();
				item.setId(nextNumber);
				this.put(nextNumber, item);
			}
		}
		logger.debug("Saving to store with ID:"  + item.getId());
		return item;
	}
	
    private Long getNextValue(){
		Long nextValue = new Long(0);
		for (Long key : this.keySet()) {
			if(key > nextValue) {
				nextValue = key;
			}
		}
		nextValue = new Long(nextValue.longValue() + 1);
		return nextValue;
	}
	
    public List<T> getOrderedList(){
		// Temp
	    	List<Long> orderedListOfKeys = new ArrayList<Long>();
	    	for(Long key : this.keySet()) {
	    		int index = 0;
	    		for (Long current : orderedListOfKeys) {
	    			if(current > key) {
	    				break;
	    			}
	    			index++;
	    		}
	    		orderedListOfKeys.add(index, key);
	    	}

		// Ordered key list.
		List<T> orderedListOfValues = new ArrayList<T>();
		for (Long key : orderedListOfKeys) {
			orderedListOfValues.add(this.get(key));
		}

		return orderedListOfValues;
	}
}
