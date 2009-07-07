package com.mockey.util;

import org.testng.annotations.Test;


@Test
public class UrlTest {

    @Test
    public void parsesFullUrl() {
        String google = "http://www.google.com/test";
        Url url = new Url(google);

        assert "http".equals(url.scheme);
        assert "80".equals(url.port);
        assert "www.google.com".equals(url.host);
        assert "/test".equals(url.path);        
    }

    @Test
    public void replacesEmptyPathWithSlash() {
        String google = "http://www.google.com";
        Url url = new Url(google);

        assert "/".equals(url.path);
    }

    @Test
    public void parsesPortFromUrl() {
        String tomcatUrl = "hTTp://localhost:8080";
        Url url = new Url(tomcatUrl);

        assert "http".equals(url.scheme);
        assert "8080".equals(url.port);
    }
}
