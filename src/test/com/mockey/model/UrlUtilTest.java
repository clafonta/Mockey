package com.mockey.model;

import org.testng.annotations.Test;

@Test
public class UrlUtilTest {

	@Test
	public void evaluateRestFulPatternWithTokenAtTheEnd() {
		UrlPatternMatchResult result = UrlUtil.evaluateUrlPattern(
				"http://someservice.com/customer",
				"http://someservice.com/customer");
		assert (result.isMatchingUrlPattern()) : "Match should be true";

		result = UrlUtil.evaluateUrlPattern("http://someservice.com/customer",
				"http://someservice.com/customer/");
		assert (!result.isMatchingUrlPattern()) : "Match should be false";

		result = UrlUtil.evaluateUrlPattern(
				"http://someservice.com/customer/33",
				"http://someservice.com/customer/{ID}");
		assert (result.isMatchingUrlPattern()) : "Match should be true";
		assert ("33".equals(result.getRestTokenId())) : "Match should be '33' but was "+ result.getRestTokenId();
		assert (result.hasTokenId()) : "Has token should be true but was "
				+ result.getRestTokenId();

		
	}

	@Test
	public void evaluateRestFulPatternsWithTokenInTheMiddle() {
		UrlPatternMatchResult result = null;

		result = UrlUtil
				.evaluateUrlPattern(
						"https://api.someservice.com/v1/charges/ch_17Z1qIrDMa1UlO/refund",
						"https://api.someservice.com/v1/charges/{ID}/refund");
		assert (result.isMatchingUrlPattern()) : "Match should be true";
		assert ("ch_17Z1qIrDMa1UlO".equals(result.getRestTokenId())) : "No token match. Should be 'ch_17Z1qIrDMa1UlO' but was "
				+ result.getRestTokenId();

		result = UrlUtil
				.evaluateUrlPattern(
						"https://api.someservice.com/v1/charges/ch_17Z1qIrDMa1UlO/refund",
						null);
		assert (!result.isMatchingUrlPattern()) : "Match should be false";

		result = UrlUtil.evaluateUrlPattern(null, null);
		assert (!result.isMatchingUrlPattern()) : "Match should be false";

		
		result = UrlUtil.evaluateUrlPattern("http://customer/123/invoice",
				"http://customer/{TOKEN}/invoice");
		assert (result.isMatchingUrlPattern()) : "Match should be true";
		assert ("123".equals(result.getRestTokenId()) ): "Match should be 123 but was " + result.getRestTokenId();

	}
	
	@Test
	public void evaluateRestFulPatternsWithMixedCase() {
		UrlPatternMatchResult result = null;

		result = UrlUtil
				.evaluateUrlPattern(
						"https://API.SOMESERVICE.COM/v1/charges/CamelCase/refund",
						"https://api.someservice.com/v1/charges/{ID}/refund");
		assert (result.isMatchingUrlPattern()) : "Match should be true";
		assert ("CamelCase".equals(result.getRestTokenId())) : "No token match. Should be 'CamelCase' but was "
				+ result.getRestTokenId();

	}
	
	@Test
	public void evaluateRestFulPatternsWithBadArguments() {
		UrlPatternMatchResult result = null;

		result = UrlUtil
				.evaluateUrlPattern(
						null,
						"https://api.someservice.com/v1/charges/{ID}/refund");
		assert (!result.isMatchingUrlPattern()) : "Match should be false";
		assert (result.getRestTokenId() == null) : "No token match. Should be 'null' but was "
				+ result.getRestTokenId();

	}

}
