package com.mockey.web;

import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.PrintStream;
import java.io.IOException;

public class ResponseMessage {
    private Log log = LogFactory.getLog(ResponseMessage.class);
	private String responseMsg;
	private boolean valid;
	private String errorMsg;
	private Header[] headers;
	private StatusLine statusLine;
	/**
	 * @return the responseMsg
	 */
	public String getResponseMsg() {
		return responseMsg;
	}
	/**
	 * @param responseMsg the responseMsg to set
	 */
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}
	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}
	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}
	public Header[] getHeaders() {
		return headers;
	}
	public void setStatusLine(StatusLine statusLine) {
		this.statusLine = statusLine;
	}
	public StatusLine getStatusLine() {
		return statusLine;
	}


    public void writeToOutput(HttpServletResponse resp) throws IOException {
        // copy the headers out
        for (Header header : headers) {

            // copy the cookies
            if(header.getName().equals("Set-Cookie")) {

                String[] cookieParts = header.getValue().split("=",2);
                String cookieName = cookieParts[0];
                String cookieBody = cookieParts[1];

                String[] cookieBodyParts = cookieBody.split("; ");
                
                Cookie cookie = new Cookie(cookieParts[0], cookieBodyParts[0]);
                resp.addCookie(cookie);
                
                log.info("Adding header: "+header.getName() + " value: "+header.getValue());
            }if(header.getName().equals("Content-Type")) {
                // copy the content type
                resp.setContentType(header.getValue());
            }
        }
        
        PrintStream out = new PrintStream(resp.getOutputStream());
	    out.println(responseMsg);
    }
}
