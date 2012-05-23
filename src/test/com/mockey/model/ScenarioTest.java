package com.mockey.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

@Test
public class ScenarioTest {

	@Test
	public void addTagsToScenario() {
		Scenario scenario = new Scenario();
		String arg1 = "development";
		String arg2 = "dEvelOpment";
		String arg3 = " development ";
		String arg4 = "DEVELOPMENT";
		String arg5 = null;
		scenario.addTagToList(arg1);
		scenario.addTagToList(arg2);
		scenario.addTagToList(arg3);
		scenario.addTagToList(arg4);
		scenario.addTagToList(arg5);
		assert (scenario.getTagList().size() == 1) : "Tag list should be length 1, but was " + scenario.getTagList().size();
		
		assert (scenario.getTagList().size() == 1);
		List<String> argList = new ArrayList<String>();
		argList.add(arg1);

	}
	
	@Test
	public void checkAlphabeticOrderOfTags() {
		Scenario scenario = new Scenario();
		String arg1 = "abc";
		String arg2 = "def";
		String arg3 = "xyz";
		List<String> argList = new ArrayList<String>();
		argList.add(arg1);
		argList.add(arg2);
		argList.add(arg3);
		scenario.setTagList(argList);
		assert (scenario.getTag().equals("abc def xyz")) : "Tag not alphabetic. Should have been 'abc def xyz' but was "
			+ scenario.getTag();

	}
	
	@Test
	public void addCheckSameScenario() {
		Scenario scenario1 = new Scenario();
		scenario1.setScenarioName("ABC");
		Scenario scenario2 = new Scenario();
		scenario2.setScenarioName("ABC");
		
		assert (scenario1.hasSameNameAndResponse(scenario2)) : "Scenarios should be the same (match == true)";

	}
	
	@Test
	public void addCheckNotSameScenario() {
		Scenario scenario1 = new Scenario();
		scenario1.setScenarioName("ABC");
		Scenario scenario2 = new Scenario();
		scenario2.setScenarioName("ABC");
		scenario2.setResponseMessage("xxx");
		
		assert (!scenario1.hasSameNameAndResponse(scenario2)) : "Scenarios should NOT be the same (match == false)";

	}
	
	@Test 
	public void testdHeaderValue() {
		Scenario scenario1 = new Scenario();
		scenario1.setResponseHeader("Content-Type: text/html; charset=utf-8 | Cache-Control: max-age=3600");
		Map<String, String> m = scenario1.getHeaderInfoHelper();
		assert (m.get("Content-Type").equals("text/html; charset=utf-8")) : "Expecting 'text/html; charset=utf-8' but got " + m.get("Content-Type");
	}
}
