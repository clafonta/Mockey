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

package com.github.fge.jsonschema.servlets;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.constants.ParseError;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.library.syntax.DraftV4SyntaxCheckerDictionary;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.processors.syntax.SyntaxProcessor;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import com.google.common.net.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Set;

import static com.github.fge.jsonschema.constants.SyntaxValidateServletConstants.*;

public final class SyntaxValidateServlet
    extends HttpServlet
{
    private static final Logger log
        = LoggerFactory.getLogger(SyntaxValidateServlet.class);

    private static final SyntaxProcessor PROCESSOR
        = new SyntaxProcessor(DraftV4SyntaxCheckerDictionary.get());

    @Override
    public void doPost(final HttpServletRequest req,
        final HttpServletResponse resp)
        throws ServletException, IOException
    {
        final Set<String> params = Sets.newHashSet();

        /*
         * First, check our parameters
         */
        /*
         * Why, in 2013, doesn't servlet-api provide an Iterator<String>?
         *
         * Well, at least, Jetty's implementation has a generified Enumeration.
         * Still, that sucks.
         */
        final Enumeration<String> enumeration = req.getParameterNames();

        // FIXME: no duplicates, it seems, but I cannot find the spec which
        // guarantees that
        while (enumeration.hasMoreElements())
            params.add(enumeration.nextElement());

        // We have required parameters
        if (!params.containsAll(Request.required())) {
            log.warn("Missing parameters! Someone using me as a web service?");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Missing parameters");
            return;
        }

        // We don't want extraneous parameters
        params.removeAll(Request.valid());

        if (!params.isEmpty()) {
            log.warn("Invalid parameters! Someone using me as a web service?");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                "Invalid parameters");
            return;
        }

        final String rawSchema = req.getParameter(Request.SCHEMA);

        // Set correct content type
        resp.setContentType(MediaType.JSON_UTF_8.toString());

        final JsonNode ret;
        try {
            ret = buildResult(rawSchema);
        } catch (ProcessingException e) {
            // Should not happen!
            log.error("Uh, syntax validation failed!", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        final OutputStream out = resp.getOutputStream();

        try {
            out.write(ret.toString().getBytes(Charset.forName("UTF-8")));
            out.flush();
        } finally {
            Closeables.closeQuietly(out);
        }
    }

    /*
     * Build the response. When we arrive here, we are guaranteed that we have
     * the needed elements.
     */
    @VisibleForTesting
    static JsonNode buildResult(final String rawSchema)
        throws IOException, ProcessingException
    {
        final ObjectNode ret = JsonNodeFactory.instance.objectNode();

        final boolean invalidSchema = fillWithData(ret, Response.SCHEMA,
            Response.INVALID_SCHEMA, rawSchema);

        final JsonNode schemaNode = ret.remove(Response.SCHEMA);

        if (invalidSchema)
            return ret;

        final SchemaTree tree = new CanonicalSchemaTree(schemaNode);
        final SchemaHolder holder = new SchemaHolder(tree);
        final ListProcessingReport report = new ListProcessingReport();

        PROCESSOR.process(report, holder);
        final boolean success = report.isSuccess();

        ret.put(Response.VALID, success);
        ret.put(Response.RESULTS, report.asJson());
        return ret;
    }

    /*
     * We have to use that since Java is not smart enough to detect that
     * sometimes, a variable is initialized in all paths.
     *
     * This returns true if the data is invalid.
     */
    private static boolean fillWithData(final ObjectNode node,
        final String onSuccess, final String onFailure, final String raw)
        throws IOException
    {
        try {
            node.put(onSuccess, JsonLoader.fromString(raw));
            return false;
        } catch (JsonProcessingException e) {
            node.put(onFailure, buildParsingError(e, raw.contains("\r\n")));
            return true;
        }
    }

    private static JsonNode buildParsingError(final JsonProcessingException e,
        final boolean crlf)
    {
        final JsonLocation location = e.getLocation();
        final ObjectNode ret = JsonNodeFactory.instance.objectNode();

        /*
         * Unfortunately, for some reason, Jackson botches the column number in
         * its JsonPosition -- I cannot figure out why exactly. However, it does
         * have a correct offset into the buffer.
         *
         * The problem is that if the input has CR/LF line terminators, its
         * offset will be "off" by the number of lines minus 1 with regards to
         * what JavaScript sees as positions in text areas. Make the necessary
         * adjustments so that the caret jumps at the correct position in this
         * case.
         */
        final int lineNr = location.getLineNr();
        int offset = (int) location.getCharOffset();
        if (crlf)
            offset = offset - lineNr + 1;
        ret.put(ParseError.LINE, lineNr);
        ret.put(ParseError.OFFSET, offset);

        // Finally, put the message
        ret.put(ParseError.MESSAGE, e.getOriginalMessage());
        return ret;
    }
}
