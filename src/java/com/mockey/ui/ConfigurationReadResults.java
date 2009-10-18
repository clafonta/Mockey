package com.mockey.ui;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationReadResults {
    private List<String> conflicts = new ArrayList<String>();
    private List<String> additions = new ArrayList<String>();
    public void addConflictMsg(String conflictMsg) {
        this.conflicts.add(conflictMsg);
    }
    public List<String> getConflictMsgs() {
        return conflicts;
    }
    public void addAdditionMsg(String additionMsg) {
        this.additions.add(additionMsg);
    }
    public List<String> getAdditionMessages() {
        return additions;
    }
    
    
}
