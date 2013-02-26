package com.mockey.ui;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.JsonValidators;
import com.github.fge.jsonschema.constants.ParseError;
import com.github.fge.jsonschema.constants.ValidateResponse;
import com.github.fge.jsonschema.main.JsonValidator;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.AsJson;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.annotations.VisibleForTesting;
import com.mockey.ServiceException;

public class JsonSchemaUtil {

	public static boolean validData(final String rawSchema, final String rawData) {
		boolean valid = false;
		try {
			final ProcessingReport report = buildReport(rawSchema, rawData, true, false);
			valid = report.isSuccess();
		} catch (ServiceException e) {
			return valid;
		} catch (IOException e) {
			return valid;
		}

		return valid;
	}

	/*
	 * Build the response. When we arrive here, we are guaranteed that we have
	 * the needed elements.
	 */
	@VisibleForTesting
	public static JsonNode buildResult(final String rawSchema,
			final String rawData, final boolean useV3, final boolean useId)
			throws IOException {
		final ObjectNode ret = JsonNodeFactory.instance.objectNode();
		try {
			final ProcessingReport report = buildReport(rawSchema, rawData,
					useV3, useId);
			final boolean success = report.isSuccess();
			ret.put(ValidateResponse.VALID, success);
			ret.put(ValidateResponse.RESULTS, ((AsJson) report).asJson());
			return ret;

		} catch (ServiceException e) {
			return ret;
		}

	}

	private static ProcessingReport buildReport(final String rawSchema,
			final String rawData, final boolean useV3, final boolean useId)
			throws IOException, ServiceException {
		final ObjectNode ret = JsonNodeFactory.instance.objectNode();

		final boolean invalidSchema = fillWithData(ret,
				ValidateResponse.SCHEMA, ValidateResponse.INVALID_SCHEMA,
				rawSchema);
		final boolean invalidData = fillWithData(ret, ValidateResponse.DATA,
				ValidateResponse.INVALID_DATA, rawData);
		if (invalidSchema || invalidData) {
			throw new ServiceException("Schema valid? " + invalidSchema
					+ " Data valid?" + invalidData);
		}
		final JsonNode schemaNode = ret.remove(ValidateResponse.SCHEMA);
		final JsonNode data = ret.remove(ValidateResponse.DATA);
		final JsonValidator validator = JsonValidators.withOptions(useV3, useId);
		final ProcessingReport report = validator.validateUnchecked(schemaNode,
				data);

		return report;
	}

	/*
	 * We have to use that since Java is not smart enough to detect that
	 * sometimes, a variable is initialized in all paths.
	 * 
	 * This returns true if the data is invalid.
	 */
	private static boolean fillWithData(final ObjectNode node,
			final String onSuccess, final String onFailure, final String raw)
			throws IOException {
		try {
			node.put(onSuccess, JsonLoader.fromString(raw));
			return false;
		} catch (JsonProcessingException e) {
			node.put(onFailure, buildParsingError(e, raw.contains("\r\n")));
			return true;
		}
	}

	private static JsonNode buildParsingError(final JsonProcessingException e,
			final boolean crlf) {
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
