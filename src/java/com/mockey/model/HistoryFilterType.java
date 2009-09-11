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
 * Type of things we are filtering on in request history.
 * 
 * @author chad.lafontaine
 *
 */
public class HistoryFilterType {

    private String key;
    private String description;
    private HistoryFilterType(String key, String description){
        this.key = key;
        this.description = description;
        
    }
    
    public static final HistoryFilterType IP = new HistoryFilterType("iprequest", "Requesting Client IP");
    public static final HistoryFilterType SERVICE = new HistoryFilterType("serviceId", "Service");
    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }
    
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
}
