/*
 * Copyright 2002-2006 the original author or authors.
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
package com.mockey;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * A sample client to call a mock web service.
 * 
 */
public class TestClient {

    public static void main(String[] args) throws Exception {
        String action = "http://localhost:8080/Mockey/service/http://e-services.doh.go.th/dohweb/dohwebservice.asmx?wsdl";
        System.out.println("Start sending " + action + " request");
        URL url = new URL( action );
        HttpURLConnection rc = (HttpURLConnection)url.openConnection();
        //System.out.println("Connection opened " + rc );
        rc.setRequestMethod("POST");
        rc.setDoOutput( true );
        rc.setDoInput( true ); 
        rc.setRequestProperty( "Content-Type", "text/xml; charset=utf-8" );
        rc.setRequestProperty("SOAPAction", "http://e-services.doh.go.th/dohweb/RequestStatusByCitizenID" );  
        String reqStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soap:Body>"
                + "<RequestStatusByCitizenID xmlns=\"http://e-services.doh.go.th/dohweb/\">"
                + " <citizen_id>123</citizen_id>"
                + "</RequestStatusByCitizenID>"
                + "</soap:Body>"
                + "</soap:Envelope>";

        int len = reqStr.length();
        rc.setRequestProperty( "Content-Length", Integer.toString( len ) );
        
        rc.connect();    
        OutputStreamWriter out = new OutputStreamWriter( rc.getOutputStream() ); 
        out.write( reqStr, 0, len );
        out.flush();
        System.out.println("Request sent, reading response ");
        InputStreamReader read = new InputStreamReader( rc.getInputStream() );
        StringBuilder sb = new StringBuilder();   
        int ch = read.read();
        while( ch != -1 ){
          sb.append((char)ch);
          ch = read.read();
        }
        String response = sb.toString();
        read.close();
        rc.disconnect();
        System.out.println(response);
        System.out.println("Done");
      }        
}
