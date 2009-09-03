package com.mockey.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import com.mockey.MockServiceBean;

/**
 * Wraps httpServletRequest and parses out the information we're looking for.
 */
public class RequestFromClient {
    private static final String[] HEADERS_TO_IGNORE = { "content-length", "host", "accept-encoding" };

    // we will ignore the accept-encoding for now to avoid dealing with GZIP
    // responses
    // if we decide to accept GZIP'ed data later, here is an example of how to
    // un-gzip
    // it
    // http://svn.apache.org/repos/asf/httpcomponents/httpclient/trunk/httpclient/src/examples/org/apache/http/examples/client/ClientGZipContentCompression.java

    private Log log = LogFactory.getLog(RequestFromClient.class);
    private HttpServletRequest rawRequest;
    private Map<String, String[]> parameters = new HashMap<String, String[]>();
    private Map<String, List<String>> headers = new HashMap<String, List<String>>();
    private String requestBody;

    public RequestFromClient(HttpServletRequest rawRequest) {
        this.rawRequest = rawRequest;
        parseRequestHeaders();
        parseRequestBody();
        parseParameters();
    }

    /**
     * Copy all necessary data from the request into a POST to the new server
     * 
     * @param serviceBean
     *            the path on the server to POST to
     * @return A fully populated HttpRequest object
     */
    public HttpRequest postToRealServer(MockServiceBean serviceBean) {
        // TODO: Cleanup the logic to handle creating a GET vs POST
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
                    log.info("  Header: " + name + " value: " + value);
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
     * 
     * @return All the parameters as a URL encoded string
     */
    public String buildParameterRequest() {
        StringBuffer requestMsg = new StringBuffer();
        for (String key : parameters.keySet()) {
            String[] values = parameters.get(key);
            for (String value : values) {
                requestMsg.append("&").append(key).append("=").append(value);
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
        
        try {
            InputStream is = rawRequest.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            requestBody = sb.toString();
            
        } catch (IOException e) {
            log.error("Unable to parse body from incoming request", e);
        }

    }

    private void parseParameters() {
        parameters = rawRequest.getParameterMap();
    }

    public String getHeaderInfo() {
        StringBuffer buf = new StringBuffer();

        Enumeration headerNames = rawRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = (String) headerNames.nextElement();
            buf.append(name).append(" = ").append(rawRequest.getHeader(name)).append("\n");
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

    /**
     * 
     * @return - true if incoming request is posting a body
     */
    public boolean hasPostBody() {
        return requestBody != null && requestBody.trim().length() > 0;
    }

    /**
     * 
     * @return the body content of this request.
     */
    public String getBodyInfo() {
        return requestBody;
    }

    /**
     * 
     * @return the parameters of this request
     */
    public String getParameterInfo() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            builder.append(entry.getKey()).append(" = ");
            for (String value : entry.getValue()) {
                builder.append(value);
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("---------- Headers ---------\n");
        builder.append(getHeaderInfo());
        builder.append("--------- Parameters ------------ \n");
        builder.append(getParameterInfo());
        builder.append("-------- Post BODY --------------\n");
        builder.append(getBodyInfo());
        return builder.toString();
    }
}
