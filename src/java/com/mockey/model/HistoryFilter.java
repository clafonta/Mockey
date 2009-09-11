/*
 * Copyright 2002-2010 the original author or authors.
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
package com.mockey.model;

/**
 * Fulfilled request history filter. 
 * 
 * @author chad.lafontaine
 *
 */
public class HistoryFilter {

    private String value;
    private HistoryFilterType type;
    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    /**
     * @return the type
     */
    public HistoryFilterType getType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(HistoryFilterType type) {
        this.type = type;
    }
    
}
