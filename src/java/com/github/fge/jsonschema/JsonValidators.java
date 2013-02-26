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

package com.github.fge.jsonschema;

import com.github.fge.jsonschema.cfg.LoadingConfiguration;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonSchemaFactoryBuilder;
import com.github.fge.jsonschema.main.JsonValidator;

import static com.github.fge.jsonschema.load.Dereferencing.*;

public final class JsonValidators
{
    private static final JsonValidator DRAFTV4_NO_ID;
    private static final JsonValidator DRAFTV4_WITH_ID;
    private static final JsonValidator DRAFTV3_NO_ID;
    private static final JsonValidator DRAFTV3_WITH_ID;

    /*
     * In theory, we should build one factory each time someone wants to support
     * "id". But that is bloody expensive. So, no. What is more, the application
     * is stateless.
     */
    static {
        final JsonSchemaFactoryBuilder builder
            = JsonSchemaFactory.newBuilder();
        LoadingConfiguration loadingCfg  = LoadingConfiguration.byDefault();
        ValidationConfiguration validationCfg
            = ValidationConfiguration.byDefault();

        /*
         * By default: draft v4, canonical dereferencing
         */
        DRAFTV4_NO_ID = builder.freeze().getValidator();

        /*
         * Now with inline dereferencing
         */
        loadingCfg = loadingCfg.thaw().dereferencing(INLINE).freeze();
        builder.setLoadingConfiguration(loadingCfg);
        DRAFTV4_WITH_ID = builder.freeze().getValidator();

        /*
         * Now with draft v3
         */
        validationCfg = validationCfg.thaw()
            .setDefaultVersion(SchemaVersion.DRAFTV3).freeze();
        builder.setValidationConfiguration(validationCfg);
        DRAFTV3_WITH_ID = builder.freeze().getValidator();

        /*
         * Now with canonical dereferencing
         */
        loadingCfg = loadingCfg.thaw().dereferencing(CANONICAL).freeze();
        builder.setLoadingConfiguration(loadingCfg);
        DRAFTV3_NO_ID = builder.freeze().getValidator();
    }

    private JsonValidators()
    {
    }

    public static JsonValidator withOptions(final boolean useDraftV3,
        final boolean useId)
    {
        if (useDraftV3)
            return useId ? DRAFTV3_WITH_ID : DRAFTV3_NO_ID;

        return useId ? DRAFTV4_WITH_ID : DRAFTV4_NO_ID;
    }
}
