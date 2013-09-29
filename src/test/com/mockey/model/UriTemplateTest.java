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
}
