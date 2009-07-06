package com.mockey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class OrderedMap extends HashMap implements Map{
	
	private static final long serialVersionUID = -1654150132938363942L;

    public void save(Item item){
		if(item!=null){
			if(item.getId()!=null){
				this.put(item.getId(), item);
			}else {
				Long nextNumber = this.getNextValue();
				item.setId(nextNumber);
				this.put(nextNumber, item);
			}
		}
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
	
	public List getOrderedList(){

		// Temp
		List keyOrder = new ArrayList();		
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
		List arrayList = new ArrayList();
		Iterator orderedIter = keyOrder.iterator();
		while(orderedIter.hasNext()){
			Long key = (Long)orderedIter.next();
			arrayList.add(this.get(key));
		}
		return arrayList;
	}
	
}
