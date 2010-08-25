/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2010  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
 * neil.cronin (neil AT rackle DOT com) 
 * lorin.kobashigawa (lkb AT kgawa DOT com)
 * rob.meyer (rob AT bigdis DOT com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
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
