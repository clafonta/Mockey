package com.mockey.util;

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
    public void chadsMainMethodTest() {

        // TODO: ASSERT these things.
        // String arg = Url.getContextAwarePath("/home", "/mockey");
        // assert "/mockey/home =? ".equals(arg);
        //         
        // System.out.println("/mockey/home =? " +
        // Url.getContextAwarePath("/","/mockey"));
        // System.out.println("/mockey/home =? " +
        // Url.getContextAwarePath("/home/", "mockey"));
        // System.out.println("/mockey/home =? " +
        // Url.getContextAwarePath("/home/", "/mockey"));
        // System.out.println("/home/ =? " + Url.getContextAwarePath("/home/",
        // "/"));
        // System.out.println("/home =? " + Url.getContextAwarePath("home",
        // "/"));
        // System.out.println("/home =? " + Url.getContextAwarePath("home",
        // ""));
        //
        // System.out.println("A) " + new
        // Url("http://www.google.com:80").toString());
        // System.out.println("B) " + new Url("htp://www.google0").toString());
        // System.out.println("C) " + new
        // Url("http://www.google.com:43").toString());
        // System.out.println("D) " + new Url("http://google.com").toString());
        // System.out.println("E) " + new
        // Url("https://www.google.com").toString());
        // System.out.println("F) " + new Url("www.google.com:80").toString());
        // System.out.println("G) " + new
        // Url("https://www.google.com:9000").toString());
        // System.out.println("H) " + new Url("google.com:80").toString());
        // System.out.println("I) " + new
        // Url("http://www.google.com:443").toString());
        // System.out.println("J) " + new
        // Url("https://www.google.com:443").toString());

    }

}
