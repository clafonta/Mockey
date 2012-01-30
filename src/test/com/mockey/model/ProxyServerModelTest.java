package com.mockey.model;

import org.testng.annotations.Test;

@Test
public class ProxyServerModelTest {

	@Test
	public void validateSettingChecker() {

		/*
		 * Why empty string is not counted as a value? Mockey reads and writes
		 * to files, (XML files!) and there are many areas where configuration
		 * settings are manually over written by hand, merging definitions, etc.
		 */
		ProxyServerModel psm = new ProxyServerModel();

		assert !psm.hasSettings() : "Expected false but got true. ";
		psm.setProxyUrl("");
		assert !psm.hasSettings() : "Expected false but got true; empty strings should not be counted as a setting. ";
		psm.setProxyUsername("");
		assert !psm.hasSettings() : "Expected false but got true; empty strings should not be counted as a setting. ";
		psm.setProxyUsername("ausername");
		assert psm.hasSettings() : "Expected true but got false; non empty username was set. ";
		psm = new ProxyServerModel();
		psm.setProxyPassword("apassword");
		assert psm.hasSettings() : "Expected true but got false; non empty password was set. ";
		psm = new ProxyServerModel();
		psm.setProxyUrl("");
		assert !psm.hasSettings() : "Expected false but got true; empty URL value should not be counted as a setting. ";
	}

}
