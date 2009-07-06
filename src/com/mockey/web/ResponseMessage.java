package com.mockey.web;

import org.apache.http.Header;
import org.apache.http.StatusLine;

public class ResponseMessage {

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
	
	
}
