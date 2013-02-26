/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * This file contains common elements between (for now) the two existing,
 * "active" pages.
 */

var TextAreas = {
    fillJson: function(selector, value)
    {
        $(selector).val(JSON.stringify(value, undefined, 4));
    },
    clear: function(selector)
    {
        $(selector).val("");
    }
};

// jQuery selectors for global elements
var DomElements = {
    FORM: "#validate",
    STARTHIDDEN: ".starthidden"
};


