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


/**
 * Represents the snap-shot of what-just-happened between the Client and Server,
 * response may be a mock scenario/service definition or a real response from
 * the server. 
 * 
 * @author chad.lafontaine
 * 
 */
public class RequestResponseTransaction implements Item {

    private Long id;
    private Scenario serviceInfo;
    private String clientRequestBody;
    private String clientRequestHeaders;
    private String clientRequestParameters;

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

    private ResponseMessage responseMessage;

    public Scenario getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(Scenario serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
   

    public ResponseMessage getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(ResponseMessage responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Long getId() {
     
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        
    }

}
