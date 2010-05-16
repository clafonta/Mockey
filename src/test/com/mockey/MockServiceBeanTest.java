package com.mockey;

import org.testng.annotations.Test;

import com.mockey.model.Service;
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
        Service bean = new Service();
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
