package com.mockey.web;

import com.mockey.MockServiceBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Wraps httpServletRequest and parses out the information we're looking for.
 */
public class RequestFromClient {
    private static final String[] HEADERS_TO_IGNORE = {"content-length", "host", "accept-encoding"};

    // we will ignore the accept-encoding for now to avoid dealing with GZIP responses
    // if we decide to accept GZIP'ed data later, here is an example of how to un-gzip
    // it http://svn.apache.org/repos/asf/httpcomponents/httpclient/trunk/httpclient/src/examples/org/apache/http/examples/client/ClientGZipContentCompression.java
    
    private Log log = LogFactory.getLog(RequestFromClient.class);
    HttpServletRequest rawRequest;
    Map<String, String[]> parameters = new HashMap<String, String[]>();
    Map<String, List<String>> headers = new HashMap<String, List<String>>();
    String requestBody;


    public RequestFromClient(HttpServletRequest rawRequest) {
        this.rawRequest = rawRequest;
        parseRequestHeaders();
        parseRequestBody();
        parseParameters();
    }

    /**
     * Copy all necessary data from the request into a POST to the new server
     *
     * @param serviceBean the path on the server to POST to
     * @return A fully populated HttpRequest object
     */
    public HttpRequest postToRealServer(MockServiceBean serviceBean) {
        //TODO: Cleanup the logic to handle creating a GET vs POST
        HttpRequest request;

        if (serviceBean.getHttpMethod().equals("GET")) {
            request = new HttpGet(serviceBean.getRealPath());
        } else {


            // Construct an HTTP Post object
            HttpPost post = new HttpPost(serviceBean.getRealPath());

            // copy the request body we recieved into the POST
            post.setEntity(constructHttpPostBody());
            request = post;
        }

        // copy the headers into the request to the real server
        for (Map.Entry<String, List<String>> stringListEntry : headers.entrySet()) {
            String name = stringListEntry.getKey();

            // ignore certain headers that httpclient will generate for us
            if (includeHeader(name)) {
                for (String value : stringListEntry.getValue()) {
                    request.addHeader(name, value);
                    log.info("  Header: "+name+" value: "+value);
                }
            }
        }
        return request;
    }

    private boolean includeHeader(String name) {
        for (String header : HEADERS_TO_IGNORE) {
            if (header.equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Used by the existing code in MockResponseServlet.
     *
     * @return All the parameters as a URL encoded string
     * @throws IOException if the body is not parseable
     * @deprecated Prefer to use this class as an object and add properties for things we need
     */
    public String getParametersAsString() throws IOException {
        StringBuffer requestMsg = new StringBuffer();

        // CHECK to see if the request is GET/POST with PARAMS instead
        // of a BODY message
        if (!hasPostBody()) {
            // OK..let's build the request message from Params.
            // Is this a HACK? I dunno yet.
            log.debug("Request message is EMPTY; building request message out of Parameters. ");
            // FIRST, let's informe the end user of what we are doing.
            requestMsg.append("NOTE: Incoming request BODY is EMPTY; Building message request body out of PARAMS. ");

            for (String key : parameters.keySet()) {
                String[] values = parameters.get(key);
                for (String value : values) {
                    requestMsg.append("&").append(key).append("=").append(value);
                }
            }
        }

        return requestMsg.toString();
    }

    private void parseRequestHeaders() {
        Enumeration e = rawRequest.getHeaderNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            List<String> values = new ArrayList<String>();
            Enumeration eValues = rawRequest.getHeaders(name);

            while (eValues.hasMoreElements()) {
                String value = (String) eValues.nextElement();
                values.add(value);                
            }
            headers.put(name, values);

        }
    }

    private void parseRequestBody() {
        StringBuffer buf = new StringBuffer();

        BufferedReader br;
        try {
            br = new BufferedReader(rawRequest.getReader());

            String thisLine;
            while ((thisLine = br.readLine()) != null) {
                buf.append(thisLine);
            }
            if (buf.length() > 0) {
                requestBody = buf.toString();
            }
        } catch (IOException e) {
            log.error("Unable to parse body from incoming request", e);
        }

    }

    private void parseParameters() {
        parameters = rawRequest.getParameterMap();
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

    private HttpEntity constructHttpPostBody() {

        HttpEntity body;
        try {
            if (requestBody != null) {
                body = new StringEntity(requestBody);
            } else {
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String[]> entry : this.parameters.entrySet()) {
                    for (String value : entry.getValue()) {
                        parameters.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                body = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
            }

        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to generate a POST from the incoming request", e);
        }

        return body;

    }


    private boolean hasPostBody() {
        return requestBody == null || requestBody.trim().length() == 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("---------- Headers ---------\n");
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for(String value : entry.getValue()) {
                if(!includeHeader(entry.getKey())) {
                    builder.append("IGNORED:  ");
                }
                builder.append(entry.getKey()).append(" = ");                
                builder.append(value);
            }
            builder.append("\n");
        }
        builder.append("--------- Parameters ------------ \n");
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            builder.append(entry.getKey()).append(" = ");
            for(String value : entry.getValue()) {
                builder.append(value).append("|");
            }
            builder.append("\n");
        }
        builder.append("-------- Post BODY --------------\n");
        builder.append(requestBody);
        return builder.toString();
    }
}
