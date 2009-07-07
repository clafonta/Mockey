package com.mockey.web;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Wraps httpServletRequest and parses out the information we're looking for. 
 */
public class RequestFromClient {
    private Log log = LogFactory.getLog(RequestFromClient.class);
    
    HttpServletRequest rawRequest;

    public RequestFromClient(HttpServletRequest rawRequest) {
        this.rawRequest = rawRequest;
    }

    public String dumpHeaders() {
        StringBuffer buf = new StringBuffer();

        Enumeration headerNames = rawRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = (String) headerNames.nextElement();
            buf.append("Header: ").append(name).append(" = ").append(rawRequest.getHeader(name));
        }
        return buf.toString();
    }

    
    public HttpPost generatePostToRealServer(String realPath) {
        HttpPost post = new HttpPost(realPath);

        StringEntity body;
        try {
            body = new StringEntity(getPostBody());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to generate a POST from the incoming request",e);
        }
        post.setEntity(body);

        return post;
    }

    private String getPostBody() {
        return null;
    }

    public String getParametersAsString() throws IOException {
        		// There are several options to look at:
		// 1. Respond with real service response
		// 2. Respond with scenario response independent of request message.
		// 3. Respond with scenario response dependent on matching request
		// message.
		//
		StringBuffer requestMsg = new StringBuffer();
		String thisLine;

		// NOTE: Get reader only retrieves the BODY of the message
		// but no "params".
		BufferedReader br = new BufferedReader(rawRequest.getReader());




		while ((thisLine = br.readLine()) != null) {
			requestMsg.append(thisLine);
		}

		// CHECK to see if the request is GET/POST with PARAMS instead
		// of a BODY message
		if (requestMsg.toString().trim().length() == 0) {
			// OK..let's build the request message from Params.
			// Is this a HACK? I dunno yet.
			log.debug("Request message is EMPTY; building request message out of Parameters. ");
			// FIRST, let's informe the end user of what we are doing.
			requestMsg.append("NOTE: Incoming request BODY is EMPTY; Building message request body out of PARAMS. ");
			Enumeration parameterNameEnum = rawRequest.getParameterNames();
			while (parameterNameEnum.hasMoreElements()) {
				String paramName = (String) parameterNameEnum.nextElement();
				String[] paramValues = rawRequest.getParameterValues(paramName);
				// IF foo has multiple VALUES, then create a message
				// like "&foo=x&foo=y" to capture someone matching
				// key value pair. Still, this is a hack; can't predict
				// what people are going to try to match to.
				for (int i = 0; i < paramValues.length; i++) {
                    requestMsg.append("&").append(paramName).append("=").append(paramValues[i]);
				}
			}

		}
    }
}
