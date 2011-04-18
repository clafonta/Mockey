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
package com.mockey;

import org.testng.annotations.Test;

import com.mockey.model.Url;


@Test
public class MockServiceBeanTest {

    @Test
    public void parsesRealServiceUrlIntoHostAndPath() {
        
        Url url = new Url("mfasa-qa2.chase.com/auth/fcc/login");
        assert "mfasa-qa2.chase.com".equals(url.getHost()) : "Real Service Host should be: mfasa-qa2.chase.com";
        assert "/auth/fcc/login".equals(url.getPath()) : "Real service path should be: /auth/fcc/login";
    }

    @Test
    public void parsesSchemeFromRealServiceUrl() {
        Url url = new Url("HTtP://www.google.com");
        assert "www.google.com".equals(url.getHost()) : "expected www.google.com got "+url.getHost();
        assert "http".equalsIgnoreCase(url.getScheme());

        url.setUrl("https://gmail.com");
        assert "gmail.com".equals(url.getHost());
        assert "https".equals(url.getScheme());

        url.setUrl("wired.com");
        assert "wired.com".equals(url.getHost());
        assert "http".equals(url.getScheme());
    }
}
