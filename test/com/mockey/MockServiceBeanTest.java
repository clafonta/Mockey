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
}
