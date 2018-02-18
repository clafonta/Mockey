/*
 * This file is part of Mockey, a tool for testing application
 * interactions over HTTP, with a focus on testing web services,
 * specifically web applications that consume XML, JSON, and HTML.
 *
 * Copyright (C) 2009-2010  Authors:
 *
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
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

public class TagItem implements Comparable<TagItem>{
    public TagItem(String value, boolean state){
        this.value = value;
        this.state = state;

    }
    private String value = null;
    private boolean state = false;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }



    @Override
    public int compareTo(TagItem other) {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than
        // other and 0 if they are supposed to be equal
        return this.value.compareTo(other.value);
    }
    @Override
    public String toString() {
        return "TagItem{" +
                "value='" + value + '\'' +
                ", state=" + state +
                '}';
    }
}
