package com.mockey;

import org.testng.annotations.Test;


@Test
public class MockServiceBeanTest {

    @Test
    public void parsesRealServiceUrlIntoHostAndPath() {
        MockServiceBean bean = new MockServiceBean();

        bean.setRealServiceUrlByString("mfasa-qa2.chase.com/auth/fcc/login");

        assert "mfasa-qa2.chase.com".equals(bean.getUrl().getHost()) : "Real Service Host should be: mfasa-qa2.chase.com";
        assert "/auth/fcc/login".equals(bean.getUrl().getPath()) : "Real service path should be: /auth/fcc/login";
    }

    @Test
    public void parsesSchemeFromRealServiceUrl() {
        MockServiceBean bean = new MockServiceBean();

        bean.setRealServiceUrlByString("HTtP://www.google.com");
        assert "www.google.com".equals(bean.getUrl().getHost()) : "expected www.google.com got "+bean.getUrl().getHost();
        assert "http".equalsIgnoreCase(bean.getUrl().getScheme());

        bean.setRealServiceUrlByString("https://gmail.com");
        assert "gmail.com".equals(bean.getUrl().getHost());
        assert "https".equals(bean.getUrl().getScheme());

        bean.setRealServiceUrlByString("wired.com");
        assert "wired.com".equals(bean.getUrl().getHost());
        assert "http".equals(bean.getUrl().getScheme());
    }
}
