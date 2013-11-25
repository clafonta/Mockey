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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.storage.xml.MockeyXmlFileManager;

/**
 * Access to application debug file.
 * 
 * @author chad.lafontaine
 * 
 */
public class ConsoleServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -566808206001734404L;

	/**
	 * Reads debug file from the file system and outputs to HTML.
	 */
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// resp.setContentType("application/html");
		resp.setContentType("text/html;charset=ISO-8859-1");
		PrintWriter out = resp.getWriter();
		File console = StartUpServlet.getDebugFile();
		if (console.exists()) {
			out.println(getFileContentAsString(console));
		} else {
			out.println("No debug file availalble");
		}

		return;
	}

	/**
	 * 
	 * @param file
	 * @return - debugfile as string
	 * @throws IOException
	 */
	private String getFileContentAsString(File file) throws IOException {

		
		FileInputStream fstream = new FileInputStream(file);
		System.out.println("Console file: "  + file.getAbsolutePath() + " (Size:" +file.length()+" bytes)");
		MockeyXmlFileManager mxfm = MockeyXmlFileManager.getInstance();
		String arg = null;
		if(file.length() > 0) {
			try {
				arg = mxfm.getFileContentAsString(fstream);
			} catch (Exception e) {
				throw new IOException(e);
			} 
		}else {
			arg = "Console is empty. No debug information is available here. File is located here: " + file.getAbsolutePath();
		}
		return arg;

	}

}
