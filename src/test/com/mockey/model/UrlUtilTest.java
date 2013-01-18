package com.mockey.model;

import org.testng.annotations.Test;

@Test
public class UrlUtilTest {

	@Test
	public void addTagsToService() {
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

		result = UrlUtil.evaluateUrlPattern("http://someservice.com/customer/",
				"http://someservice.com/customer");
		assert (!result.isMatchingUrlPattern()) : "Match should be false";

	}

}
