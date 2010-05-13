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
