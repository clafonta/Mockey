package com.mockey.runner;

import com.centerkey.utils.BareBonesBrowserLaunch;

public class BrowserThread implements Runnable {
    private String port;
    private String url;
    private int waitSeconds;

    BrowserThread(String url, String port, int waitSeconds) {
        this.port = port;
        this.url = url;
        this.waitSeconds = waitSeconds;
    }

    public void run() {
        try {
            Thread.sleep(this.waitSeconds * 1000);
        } catch (InterruptedException e) {
            // don't do anything
        }finally {
            BareBonesBrowserLaunch.openURL(this.url + this.port);
        }

    }
}
