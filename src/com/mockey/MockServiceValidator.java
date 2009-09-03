/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mockey;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Validator for contents of a MockServiceBean definition. 
 * @author chad.lafontaine
 *
 */
public class MockServiceValidator {
    /** Basic logger */
    private static Logger logger = Logger.getLogger(MockServiceValidator.class);
    private static MockServiceStore store = MockServiceStoreImpl.getInstance();

    /**
     * Return a mapping of input field names and error messages. If the mock
     * service state is valid, then an empty Map is returned.
     * 
     * @param ms
     * @return
     */
    public static Map validate(MockServiceBean ms) {
        Map errorMap = new HashMap();

        if ((ms.getServiceName() == null) || (ms.getServiceName().trim().length() < 1)
                || (ms.getServiceName().trim().length() > 250)) {
            errorMap.put("serviceName", "Service name must not be empty or greater than 250 chars.");
        }
        

        // Make sure there doesn't exist a service
        // w/ the same serviceURL.
        try {
            List testServices = store.getOrderedList();
            Iterator iter = testServices.iterator();
            MockServiceBean ts;

            while (iter.hasNext()) {
                ts = (MockServiceBean) iter.next();

                // We have a match AND service IDs don't match.
                if (ts.getMockServiceUrl().equals(ms.getMockServiceUrl()) && (ts.getId() != ms.getId())) {
                    errorMap.put("serviceUrl", "Mock service URI is already used by the '" + ts.getServiceName()
                            + "' service. Please choose another mock service URI pattern. ");

                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Unable to verify if there are duplicate service URLs", e);
        }

        return errorMap;
    }
}
