/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2010  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
 * neil.cronin (neil AT rackle DOT com) 
 * lorin.kobashigawa (lkb AT kgawa DOT com)
 * rob.meyer (rob AT bigdis DOT com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.mockey.util;

import java.net.MalformedURLException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.mockey.model.Url;

@Test
public class UrlTest {

	@Test
	public void parsesFullUrl() {
		String google = "http://www.google.com/test";
		Url url = new Url(google);

		assert "http".equals(url.getScheme());
		assert 80 == url.getPort();
		assert "www.google.com".equals(url.getHost());
		assert "/test".equals(url.getPath());
	}

	@Test
	public void replacesEmptyPathWithSlash() {
		String google = "http://www.google.com";
		Url url = new Url(google);

		assert "/".equals(url.getPath()) : "expected path to be / but was " + url.getPath();
	}

	@Test
	public void parsesPortFromUrl() {
		String tomcatUrl = "hTTp://localhost:8080";
		Url url = new Url(tomcatUrl);

		assert "http".equals(url.getScheme());
		assert 8080 == url.getPort();
	}
	
	@Test
	public void validateEmptySetings() {
		
		Url emptySettings = new Url("");
		assert !emptySettings.hasSettings() : " Expected FALSE; with empty String as argument in constructor, should be 'no settings'.";
		
		emptySettings = new Url("   ");
		assert !emptySettings.hasSettings() : " Expected FALSE; with empty String as argument in constructor, should be 'no settings'.";
		
		emptySettings = new Url("http://www.google.com");
		assert emptySettings.hasSettings() : " Expected FALSE; with empty String as argument in constructor, should be 'no settings'.";
	}

	@Test
	public void extractMockUrlFromFullUrl() throws MalformedURLException {
		String urlArg = "http://abc.google.com/xyz/someargument/";
		assert "http://abc.google.com/xyz/someargument/".equals(Url.getSchemeHostPortPathFromURL(urlArg)) : "Expected 'http://abc.google.com/xyz/someargument/', but was "
				+ Url.getSchemeHostPortPathFromURL(urlArg);
		
		urlArg = "http://abc.google.com/xyz/someargument//";
		assert "http://abc.google.com/xyz/someargument//".equals(Url.getSchemeHostPortPathFromURL(urlArg)) : "Expected 'http://abc.google.com/xyz/someargument//', but was "
				+ Url.getSchemeHostPortPathFromURL(urlArg);
		
		urlArg = "http://abc.google.com:80/xyz/someargument//";
		assert "http://abc.google.com:80/xyz/someargument//".equals(Url.getSchemeHostPortPathFromURL(urlArg)) : "Expected 'http://abc.google.com:80/xyz/someargument//', but was "
				+ Url.getSchemeHostPortPathFromURL(urlArg);
		
		urlArg = "http://abc.google.com/xyz/path/a/b/c";
		assert "http://abc.google.com/xyz/path/a/b/c".equals(Url.getSchemeHostPortPathFromURL(urlArg)) : "Expected 'http://abc.google.com/xyz/path/a/b/c', but was "
				+ Url.getSchemeHostPortPathFromURL(urlArg);
		
		urlArg = "http://abc.google.com/xyz/path/a/b/c?arg=oaa&arg1=";
		assert "http://abc.google.com/xyz/path/a/b/c".equals(Url.getSchemeHostPortPathFromURL(urlArg)) : "Expected 'http://abc.google.com/xyz/path/a/b/c', but was "
				+ Url.getSchemeHostPortPathFromURL(urlArg);
		
		urlArg = "abc.google.com/xyz/path/a/b/c?arg=oaa&arg1=";
		try {
			Url.getSchemeHostPortPathFromURL(urlArg);
			Assert.fail("Missing scheme (protocol) -> " + urlArg);
		} catch(MalformedURLException e) {
			
		}
		

		try {
			urlArg = null;
			Url.getSchemeHostPortPathFromURL(urlArg);
			Assert.fail("'null' should have thrown a MalformedURLException but got " + Url.getSchemeHostPortPathFromURL(urlArg));
		} catch (MalformedURLException e) {
			
		}
		
		try {
			urlArg = "sometextfile.txt";
			Url.getSchemeHostPortPathFromURL(urlArg);
			Assert.fail("'null' should have thrown a MalformedURLException but got " + Url.getSchemeHostPortPathFromURL(urlArg));
		} catch (MalformedURLException e) {
			
		}
	}

}
