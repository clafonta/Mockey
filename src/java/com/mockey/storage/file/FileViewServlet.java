/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2010  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
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
package com.mockey.storage.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileViewServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4799258651817808844L;

	// This method is called by the servlet container to process a GET request.
	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String fileName = req.getParameter("filename");
		ServletContext sc = getServletContext();
		FileSystemManager fileManager = new FileSystemManager();
		File image = fileManager.getImageFile(fileName);

		// Get the MIME type of the image
		// Hack: looks like getMimeType in Tomcat 6.0xx doesn't like uppercase .PNG!
		String mimeType = sc.getMimeType(fileName.toLowerCase());
		if (mimeType == null) {
			sc.log("Could not get MIME type of " + image.getName());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		// Set content type
		resp.setContentType(mimeType);

		// Set content size

		resp.setContentLength((int) image.length());

		// Open the file and output streams
		FileInputStream in = new FileInputStream(image);
		OutputStream out = resp.getOutputStream();

		// Copy the contents of the file to the output stream
		byte[] buf = new byte[1024];
		int count = 0;
		while ((count = in.read(buf)) >= 0) {
			out.write(buf, 0, count);
		}
		in.close();
		out.close();
	}
}
