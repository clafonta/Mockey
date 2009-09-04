package com.mockey.util;

import org.testng.annotations.Test;


@Test
public class UrlTest {

    @Test
    public void parsesFullUrl() {
        String google = "http://www.google.com/test";
        Url url = new Url(google);

        assert "http".equals(url.getScheme());
        assert "80".equals(url.getPort());
        assert "www.google.com".equals(url.getHost());
        assert "/test".equals(url.getPath());        
    }

    @Test
    public void replacesEmptyPathWithSlash() {
        String google = "http://www.google.com";
        Url url = new Url(google);

        assert "/".equals(url.getPath());
    }

    @Test
    public void parsesPortFromUrl() {
        String tomcatUrl = "hTTp://localhost:8080";
        Url url = new Url(tomcatUrl);

        assert "http".equals(url.getScheme());
        assert "8080".equals(url.getPort());
    }
}
