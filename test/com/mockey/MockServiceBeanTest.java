package com.mockey;

import org.testng.annotations.Test;


@Test
public class MockServiceBeanTest {

    @Test
    public void parsesRealServiceUrlIntoHostAndPath() {
        MockServiceBean bean = new MockServiceBean();

        bean.setRealServiceUrl("mfasa-qa2.chase.com/auth/fcc/login");

        assert "mfasa-qa2.chase.com".equals(bean.getRealHost()) : "Real Service Host should be: mfasa-qa2.chase.com";
        assert "/auth/fcc/login".equals(bean.getRealPath()) : "Real service path should be: /auth/fcc/login";
    }

    @Test
    public void parsesSchemeFromRealServiceUrl() {
        MockServiceBean bean = new MockServiceBean();

        bean.setRealServiceUrl("HTtP://www.google.com");
        assert "www.google.com".equals(bean.getRealServiceUrl()) : "expected www.google.com got "+bean.getRealServiceUrl();
        assert "http".equalsIgnoreCase(bean.getRealServiceScheme());

        bean.setRealServiceUrl("https://gmail.com");
        assert "gmail.com".equals(bean.getRealServiceUrl());
        assert "https".equals(bean.getRealServiceScheme());

        bean.setRealServiceUrl("wired.com");
        assert "wired.com".equals(bean.getRealServiceUrl());
        assert "http".equals(bean.getRealServiceScheme());
    }
}
