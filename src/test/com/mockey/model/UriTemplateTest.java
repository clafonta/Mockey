package com.mockey.model;

import java.util.Map;

import org.testng.annotations.Test;

@Test
public class UriTemplateTest {

	@Test
	public void validateParametizedUrl() {
		UriTemplate template = new UriTemplate("http://feeling/{id}");
		@SuppressWarnings("rawtypes")
		Map results = template.match("http://feeling/happy");
		assert(results.size() > 0) : "Dang, expected a match!";
		
	}
	
	@Test
	public void validateSimpleURL() {
		UriTemplate template = new UriTemplate("http://feeling");
		@SuppressWarnings("rawtypes")
		Map results = template.match("http://feeling");
		assert(results.size() == 0) : "Expected size 0, but got " + results.size();
		
	}
	
	@Test
	public void validateMultipleVariablesInURL() {
		UriTemplate template = new UriTemplate("http://id/{ID1}/otherid/{ID2}/test");
		@SuppressWarnings("rawtypes")
		Map results = template.match("http://id/23/otherid/23/test");
		assert(results.size() == 2) : "Expected size 2, but got " + results.size();
		
	}
	
	@Test
	public void validateOddMatchVariablesInURL() {
		UriTemplate template = new UriTemplate("http://id/{ID}/test");
		@SuppressWarnings("rawtypes")
		Map results = template.match("http://id/23/otherid/23/test");
		// NOTE: {ID} should not equal '23/otherid/23'
		assert(results.size() == 0) : "Expected size 0, but got " + results.size();
		
	}
	
	@Test
	public void validateCaseInsensitiveVariablesInURL() {
		UriTemplate template = new UriTemplate("http://id/{ID1}/OTHERid/{ID2}/test");
		@SuppressWarnings("rawtypes")
		Map results = template.match("http://id/23/otherID/23/test");
		assert(results.size() == 2) : "Expected size 2, but got " + results.size();
		
	}
}
