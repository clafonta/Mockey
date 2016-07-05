/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2010  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
 * neil.cronin (neil AT rackle DOT com) 
 * lorin.kobashigawa (lkb AT kgawa DOT com)
 * rob.meyer (rob AT bigdis DOT com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.mockey.ui;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.apache.http.protocol.HTTP;

import com.google.common.net.MediaType;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.storage.xml.MockeyXmlFactory;

/**
 * Export service definitions to XML.
 * 
 * @author Chad.Lafontaine
 * 
 */
public class ExportConfigurationServlet extends HttpServlet {
	
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

    private static final long serialVersionUID = -8618555367432628615L;

    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		MockeyXmlFactory g = new MockeyXmlFactory();

        String fileOutput;
        try {
            fileOutput = g.getStoreAsString(store, true);
        } catch (TransformerException e) {
            throw new ServletException(e);
        }
        
        resp.setContentType(MediaType.XML_UTF_8.toString());
        resp.setCharacterEncoding(HTTP.UTF_8);
        resp.setContentLength(fileOutput.getBytes(HTTP.UTF_8).length);
        
        if(req.getParameter("download")!=null){
        	resp.setHeader("Content-disposition", "attachment; filename=mockservice.xml");
        	resp.setHeader("Content-type", MediaType.XML_UTF_8.toString());
        }

        PrintWriter out = resp.getWriter();
        out.println(fileOutput);
        out.flush();
		out.close();
		return;
    }
}
