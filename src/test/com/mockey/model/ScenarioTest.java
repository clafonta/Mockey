package com.mockey.model;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

@Test
public class ScenarioTest {

	@Test
	public void addTagsToScenario() {
		Scenario scenario = new Scenario();
		String arg1 = "development";
		String arg2 = "development";
		String arg3 = " development ";
		String arg4 = "DEVELOPMENT";
		scenario.addTagToList(arg1);
		scenario.addTagToList(arg2);
		scenario.addTagToList(arg3);
		scenario.addTagToList(arg4);
		
		assert (scenario.getTagList().size() == 1);
		List<String> argList = new ArrayList<String>();
		argList.add(arg1);

	}
	
	@Test
	public void addTagListToScenario() {
		Scenario scenario = new Scenario();
		String arg1 = "development";
		String arg2 = "development";
		String arg3 = " development ";
		String arg4 = "DEVELOPMENT";
		
		
		
		List<String> argList = new ArrayList<String>();
		argList.add(arg1);
		argList.add(arg2);
		argList.add(arg3);
		argList.add(arg4);
		
		assert (scenario.getTagList().size() != 1);
		
		scenario.setTagList(argList);
		assert (scenario.getTagList().size() == 1);

	}
}
