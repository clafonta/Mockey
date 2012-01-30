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
package com.mockey;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;

import com.mockey.model.PersistableItem;

/**
 * Keeps an ordered map of items, with a key of an number based on 'when defined
 * and added to this object'. Sort of like an auto-incrementing column in a db.
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
public class OrderedMap<T extends PersistableItem> extends ConcurrentHashMap<Long, T> implements Map<Long, T> {

    private static final long serialVersionUID = -1654150132938363942L;
    private Integer maxSize = null;

    
    /**
     * Will save item. If maximum size of this Map is set (non-null, positive),
     * this method will purge the oldest value, the value with the smallest key
     * value.
     * 
     * @param item
     * @return item saved, with ID set.
     * @see #getMaxSize()
     */
    public PersistableItem save(T item) {

        if (item != null) {
            if (item.getId() != null) {
                this.put(item.getId(), item);
            } else {
                Long nextNumber = this.getNextValue();
                item.setId(nextNumber);
                this.put(nextNumber, item);
            }
        }

        if (this.maxSize != null && this.maxSize > 0) {

            while (this.size() > this.maxSize) {
                Long removeMe = getSmallestValue();
                this.remove(removeMe);
            }

        }
        return item;
    }

    public T get(Object key) {
      if (key == null) {
        return null;
      }
      return super.get(key);
    }

    private Long getSmallestValue() {
        Long smallestValue = null;
        for (Long key : this.keySet()) {
            if (smallestValue == null) {
                smallestValue = key;
            } else if (key < smallestValue) {
                smallestValue = key;
            }

        }
        return smallestValue;
    }

    private Long getNextValue() {
        Long nextValue = new Long(0);
        for (Long key : this.keySet()) {
            if (key > nextValue) {
                nextValue = key;
            }
        }
        nextValue = new Long(nextValue.longValue() + 1);
        return nextValue;
    }

    public List<T> getOrderedList() {

        List<Long> orderedListOfKeys = new ArrayList<Long>();
        for (Long key : this.keySet()) {
            int index = 0;
            for (Long current : orderedListOfKeys) {
                if (current > key) {
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

    /**
     * 
     * @param maxSize
     * @see #getMaxSize()
     */
    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Maximum number of <code>PersistableItem</code> allowed in this ordered
     * map. Once this map has reached its limit, if set, it takes a first-in,
     * first-out (FIFO) persistence approach, purging the oldest object.
     * 
     * @return null if not set (no size limit), otherwise returns the maximum
     *         size value.
     */
    public Integer getMaxSize() {
        return maxSize;
    }
}
