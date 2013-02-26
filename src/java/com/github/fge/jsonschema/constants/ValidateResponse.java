package com.github.fge.jsonschema.constants;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

public final class ValidateResponse
{
    private ValidateResponse()
    {
    }

    public static final String INVALID_SCHEMA = "invalidSchema";
    public static final String INVALID_DATA = "invalidData";
    public static final String RESULTS = "results";
    public static final String VALID = "valid";

    public static final String SCHEMA = "schema";
    public static final String DATA = "data";

    @VisibleForTesting
    public static final Set<String> OUTPUTS = ImmutableSet.of(RESULTS, VALID);
}
