package com.github.fge.jsonschema.constants;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public final class ParseError
{
    private ParseError()
    {
    }

    public static final String LINE = "line";
    public static final String OFFSET = "offset";
    public static final String MESSAGE = "message";

    public static final Set<String> ALL_FIELDS
        = ImmutableSet.of(LINE, OFFSET, MESSAGE);
}
