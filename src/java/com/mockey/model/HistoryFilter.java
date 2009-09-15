/*
 * Copyright 2008-2010 the original author or authors.
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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * List of unique filter tokens.
 * 
 * @author chad.lafontaine
 * 
 */
public class HistoryFilter {

    private Map<String, String> tokens = new LinkedHashMap<String, String>();

    public Collection<String> getTokens() {
        return this.tokens.keySet();

    }

    /**
     * Ensures no duplicate arguments are given to this filter.
     * 
     * @param arg
     */
    public void addToken(String arg) {
        if (arg != null && arg.trim().length()>0) {
            this.tokens.put(arg, arg);
        }

    }

    public void addTokens(String[] args) {

        if (args != null) {
            for (String arg : args) {
                this.addToken(arg);
            }
        }

    }

    public void deleteToken(String arg) {
        if (arg != null) {
            tokens.remove(arg);
        }

    }

    public void deleteTokens(String[] args) {
        if (args != null) {
            for (String arg : args) {
                this.deleteToken(arg);
            }
        }
    }

    /**
     * Pipe delimited set of filter tokens
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        Iterator<String> iter = this.tokens.keySet().iterator();
        while (iter.hasNext()) {
            buf.append(iter.next());
            if (iter.hasNext()) {
                buf.append("|");
            }
        }
        return buf.toString();
    }

}
