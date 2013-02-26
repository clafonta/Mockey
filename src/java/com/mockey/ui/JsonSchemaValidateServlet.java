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

package com.mockey.ui;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.JsonValidators;
import com.github.fge.jsonschema.constants.ParseError;
import com.github.fge.jsonschema.constants.ValidateRequest;
import com.github.fge.jsonschema.constants.ValidateResponse;
import com.github.fge.jsonschema.main.JsonValidator;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.AsJson;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import com.google.common.net.MediaType;
import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Set;

/**
 * Servlet responsible of validating a schema/data pair
 * 
 * <p>
 * It returns a JSON Object as a result with the appropriate media type (thanks
 * Guava for providing {@link MediaType#JSON_UTF_8}!).
 * </p>
 * 
 * <p>
 * This object has the following members:
 * </p>
 * 
 * <ul>
 * <li>{@code invalidSchema}: boolean indicating whether the provided schema was
 * valid JSON;</li>
 * <li>{@code invalidData}: same, but for the data;</li>
 * <li>{@code valid} (only if the schema and data are valid): whether the
 * validation has succeeded;</li>
 * <li>{@code results} (only if the schema and data are valid): the result of
 * {@link ProcessingReport#getMessages()}.</li>
 * </ul>
 */
public final class JsonSchemaValidateServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2625630469996134777L;

	private static final Logger logger = LoggerFactory
			.getLogger(JsonSchemaValidateServlet.class);

	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			Long serviceId = new Long(req.getParameter("serviceId"));
			Long scenarioId = null;
			scenarioId = new Long(req.getParameter("scenarioId"));
			Service service = store.getServiceById(serviceId);
			Scenario scenario = service.getScenario(scenarioId);
			req.setAttribute("service", service);
			req.setAttribute("scenario", scenario);
		} catch (Exception e) {
			logger.debug("Unable to retrieve a Service of ID: "
					+ req.getParameter("serviceId"));
		}

		// Get the service.
		RequestDispatcher dispatch = req
				.getRequestDispatcher("/jsonschemavalidate.jsp");
		dispatch.forward(req, resp);
	}

	@Override
	public void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		final Set<String> params = Sets.newHashSet();
		final Enumeration<String> enumeration = req.getParameterNames();

		// FIXME: no duplicates, it seems, but I cannot find the spec which
		// guarantees that
		while (enumeration.hasMoreElements())
			params.add(enumeration.nextElement());

		// We have required parameters
		if (!params.containsAll(ValidateRequest.REQUIRED_PARAMS)) {
			logger.warn("Missing parameters! Someone using me as a web service?");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Missing parameters");
			return;
		}

		// We don't want extraneous parameters
		params.removeAll(ValidateRequest.VALID_PARAMS);

		if (!params.isEmpty()) {
			logger.warn("Invalid parameters! Someone using me as a web service?");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Invalid parameters");
			return;
		}

		final String rawSchema = req.getParameter(ValidateRequest.SCHEMA);
		final String data = req.getParameter(ValidateRequest.DATA);

		// Set correct content type
		resp.setContentType(MediaType.JSON_UTF_8.toString());

		final boolean useV3 = Boolean.parseBoolean(req
				.getParameter(ValidateRequest.USE_V3));
		final boolean useId = Boolean.parseBoolean(req
				.getParameter(ValidateRequest.USE_ID));

		final JsonNode ret = JsonSchemaUtil.buildResult(rawSchema, data, useV3, useId);

		final OutputStream out = resp.getOutputStream();

		try {
			out.write(ret.toString().getBytes(Charset.forName("UTF-8")));
			out.flush();
		} finally {
			Closeables.closeQuietly(out);
		}
	}

//	/*
//	 * Build the response. When we arrive here, we are guaranteed that we have
//	 * the needed elements.
//	 */
//	@VisibleForTesting
//	static JsonNode buildResult(final String rawSchema, final String rawData,
//			final boolean useV3, final boolean useId) throws IOException {
//		final ObjectNode ret = JsonNodeFactory.instance.objectNode();
//
//		final boolean invalidSchema = fillWithData(ret,
//				ValidateResponse.SCHEMA, ValidateResponse.INVALID_SCHEMA,
//				rawSchema);
//		final boolean invalidData = fillWithData(ret, ValidateResponse.DATA,
//				ValidateResponse.INVALID_DATA, rawData);
//
//		final JsonNode schemaNode = ret.remove(ValidateResponse.SCHEMA);
//		final JsonNode data = ret.remove(ValidateResponse.DATA);
//
//		if (invalidSchema || invalidData)
//			return ret;
//
//		final JsonValidator validator = JsonValidators
//				.withOptions(useV3, useId);
//		final ProcessingReport report = validator.validateUnchecked(schemaNode,
//				data);
//
//		final boolean success = report.isSuccess();
//		ret.put(ValidateResponse.VALID, success);
//		ret.put(ValidateResponse.RESULTS, ((AsJson) report).asJson());
//		return ret;
//	}
//
//	/*
//	 * We have to use that since Java is not smart enough to detect that
//	 * sometimes, a variable is initialized in all paths.
//	 * 
//	 * This returns true if the data is invalid.
//	 */
//	private static boolean fillWithData(final ObjectNode node,
//			final String onSuccess, final String onFailure, final String raw)
//			throws IOException {
//		try {
//			node.put(onSuccess, JsonLoader.fromString(raw));
//			return false;
//		} catch (JsonProcessingException e) {
//			node.put(onFailure, buildParsingError(e, raw.contains("\r\n")));
//			return true;
//		}
//	}
//
//	private static JsonNode buildParsingError(final JsonProcessingException e,
//			final boolean crlf) {
//		final JsonLocation location = e.getLocation();
//		final ObjectNode ret = JsonNodeFactory.instance.objectNode();
//
//		/*
//		 * Unfortunately, for some reason, Jackson botches the column number in
//		 * its JsonPosition -- I cannot figure out why exactly. However, it does
//		 * have a correct offset into the buffer.
//		 * 
//		 * The problem is that if the input has CR/LF line terminators, its
//		 * offset will be "off" by the number of lines minus 1 with regards to
//		 * what JavaScript sees as positions in text areas. Make the necessary
//		 * adjustments so that the caret jumps at the correct position in this
//		 * case.
//		 */
//		final int lineNr = location.getLineNr();
//		int offset = (int) location.getCharOffset();
//		if (crlf)
//			offset = offset - lineNr + 1;
//		ret.put(ParseError.LINE, lineNr);
//		ret.put(ParseError.OFFSET, offset);
//
//		// Finally, put the message
//		ret.put(ParseError.MESSAGE, e.getOriginalMessage());
//		return ret;
//	}
}
