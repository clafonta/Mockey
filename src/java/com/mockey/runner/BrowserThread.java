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
package com.mockey.runner;

import com.centerkey.utils.BareBonesBrowserLaunch;

public class BrowserThread implements Runnable {
    private String port;
    private String url;
    private String path;
    private int waitSeconds;

    BrowserThread(String url, String port, String path, int waitSeconds) {
        this.port = port;
        this.path = path != null ? path : "/";
        this.waitSeconds = waitSeconds;
        if (url.endsWith(":")) {
            url = url.substring(0, url.length() -1);
        }
        this.url = url;
    }

    public void run() {
        try {
            Thread.sleep(this.waitSeconds * 1000);
        } catch (InterruptedException e) {
            // don't do anything
        }finally {
            BareBonesBrowserLaunch.openURL(this.url +":"+ this.port+this.path);
        }

    }
}
