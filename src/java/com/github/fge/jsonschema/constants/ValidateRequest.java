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

package com.github.fge.jsonschema.constants;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public final class ValidateRequest
{
    private ValidateRequest()
    {
    }

    public static final String SCHEMA = "schema";
    public static final String DATA = "data";
    public static final String USE_V3 = "useV3";
    public static final String USE_ID = "useId";

    public static final Set<String> REQUIRED_PARAMS
        = ImmutableSet.of(SCHEMA, DATA);

    public static final Set<String> VALID_PARAMS
        = ImmutableSet.of(SCHEMA, DATA, USE_V3, USE_ID);
}
