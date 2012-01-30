package com.mockey.model;

import org.testng.annotations.Test;

import com.mockey.model.ConflictInfo.Conflict;

@Test
public class ConflictInfoTest {

	@Test
	public void checkServiceConflict() {

		Service a = new Service();
		a.setServiceName("Service Cat");
		a.setId(new Long(123));

		Service b = new Service();
		b.setServiceName("Service Cat");
		b.setId(new Long(345));

		ConflictInfo conflictInfo = new ConflictInfo();
		conflictInfo.addConflict(a, b, "Same service name");
		conflictInfo.addConflict(a, b, "Same matching URL");

		assert (conflictInfo.getConflictList(a).size() == 1) : "Expected conflict list to be size 1 but was "
				+ conflictInfo.getConflictList(a).size();

		Conflict conflict = conflictInfo.getConflictList(a).get(0);

		assert (conflict.getService().getId().equals(new Long(345))) : "Expected conflict Service ID to be 345 but was "
				+ conflict.getService().getId();

		assert (conflict.getConflictMessageList().size() == 2) : "Expected conflict message list length to be 2 but was "
				+ conflict.getConflictMessageList().size();

	}
	
	@Test
	public void checkDuplicateScenarioConflict() {

		Service a = new Service();
		a.setServiceName("Service Cat");
		a.setId(new Long(123));
		
		ConflictInfo conflictInfo = new ConflictInfo();
		conflictInfo.addConflict(a, a, "Duplicate scenario(s)");
		
		assert (conflictInfo.getConflictList(a).size() == 1) : "Expected conflict list to be size 1 but was "
		+ conflictInfo.getConflictList(a).size();
	}

}
