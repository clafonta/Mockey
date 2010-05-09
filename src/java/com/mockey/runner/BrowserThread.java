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
