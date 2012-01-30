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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;



public class FileUploadOctetStreamReaderServlet extends HttpServlet {

	private static final long serialVersionUID = -8429482476914060900L;

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		PrintWriter writer = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			writer = response.getWriter();
		} catch (IOException ex) {
			log(FileUploadOctetStreamReaderServlet.class.getName() + "has thrown an exception: " + ex.getMessage());
		}

		

		try {
			String filename = URLDecoder.decode(request.getHeader("X-File-Name"), "UTF-8");
			is = request.getInputStream();
			FileSystemManager fsm = new FileSystemManager();
			File fileToWriteTo = fsm.getImageFile(filename);
			fos = new FileOutputStream(fileToWriteTo);
			
			IOUtils.copy(is, fos);
			response.setStatus(HttpServletResponse.SC_OK);
			writer.print("{success: true}");
		} catch (FileNotFoundException ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writer.print("{success: false}");
			log(FileUploadOctetStreamReaderServlet.class.getName() + "has thrown an exception: " + ex.getMessage());
		} catch (IOException ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writer.print("{success: false}");
			log(FileUploadOctetStreamReaderServlet.class.getName() + "has thrown an exception: " + ex.getMessage());
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}

			} catch (IOException ignored) {
			}
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException ignored) {
			}
		}

		writer.flush();
		writer.close();
	}
}
