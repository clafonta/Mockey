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

import java.util.HashMap;
import java.util.Map;

/**
 * Validates creation of MockServiceScenarioBean. 
 */
public class MockServiceScenarioValidator {

	private final static int SERVICE_NAME_SIZE_LIMIT = 250;
	
	/**
	 *  
	 * @param ms MockServiceScenarioBean to validate.
	 * @return a mapping of input field names and error messages, key value pairs. If no errors, then empty Map. 
	 */
	public static Map validate(MockServiceScenarioBean mss) {
		Map errorMap = new HashMap();

		// TRIM input in case user entered only spaces in input fields.
		if ((mss.getScenarioName() == null) || (mss.getScenarioName().trim().length() < 1)
				|| (mss.getScenarioName().trim().length() > SERVICE_NAME_SIZE_LIMIT)) {
			errorMap.put("name", "Service scenario name must not be empty or greater than "+SERVICE_NAME_SIZE_LIMIT +" chars.");
		}

		return errorMap;
	}
}
