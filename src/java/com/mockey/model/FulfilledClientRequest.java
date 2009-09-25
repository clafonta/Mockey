/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mockey.model;

import java.util.Date;


/**
 * Represents the snap-shot of what-just-happened between the Client and Server,
 * response may be a mock scenario/service definition or a real response from
 * the server. 
 * 
 * @author chad.lafontaine
 * 
 */
public class FulfilledClientRequest implements PersistableItem {

    private Long id;
    private Long serviceId;
    private String serviceName;
    private String clientRequestBody;
    private String clientRequestHeaders;
    private String clientRequestParameters;
    private String requestorIP;
    private String rawRequest;
    
    /**
     * @return the rawRequest
     */
    public String getRawRequest() {
        return rawRequest;
    }

    /**
     * @param rawRequest the rawRequest to set
     */
    public void setRawRequest(String rawRequest) {
        this.rawRequest = rawRequest;
    }

    private ResponseFromService responseMessage;
	private Date time = new Date();
	
    public Date getTime() {
		return time;
	}
    
    public String getRequestorIP() {
        return requestorIP;
    }

    public void setRequestorIP(String ip) {
        String requestIp = ip;
        // on macs sometimes localhost resolves to the IPV6 format IP
        if (requestIp.equals("0:0:0:0:0:0:0:1%0")) {
            requestIp = "127.0.0.1";
        }
        this.requestorIP = ip;
    }
    
    public String getClientRequestBody() {
        return clientRequestBody;
    }

    public void setClientRequestBody(String clientRequestBody) {
        this.clientRequestBody = clientRequestBody;
    }

    public String getClientRequestHeaders() {
        return clientRequestHeaders;
    }

    public void setClientRequestHeaders(String clientRequestHeaders) {
        this.clientRequestHeaders = clientRequestHeaders;
    }

    public String getClientRequestParameters() {
        return clientRequestParameters;
    }

    public void setClientRequestParameters(String clientRequestParameters) {
        this.clientRequestParameters = clientRequestParameters;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
   
    public ResponseFromService getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(ResponseFromService response) {
        this.responseMessage = response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id; 
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

}
