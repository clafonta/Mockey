/*
d * This file is part of Mockey, a tool for testing application 
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileSystemManagerServlet extends HttpServlet {

	private static final long serialVersionUID = -7334916323927032682L;

	/**
	 * This service
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		FileSystemManager fsm = new FileSystemManager();

		FileInfo[] imageList = fsm.getImageFileList();

		String fileName = req.getParameter("filename");
		

		if (fileName != null) {

			try {

				BufferedOutputStream bos = new BufferedOutputStream(
						resp.getOutputStream());
				File fileToWriteOut = null;

				fileToWriteOut = fsm.getImageFile(fileName);

				resp.setContentType(new MimetypesFileTypeMap()
						.getContentType(fileToWriteOut));
				resp.setHeader("Content-disposition", "attachment; filename="
						+ fileName);
				FileInputStream fis = new FileInputStream(fileToWriteOut);

				int len;
				byte[] buf = new byte[1024];

				while ((len = fis.read(buf)) > 0) {
					bos.write(buf, 0, len);
				}

				bos.close();

				return;
			} catch (Exception e) {
				// do the following in a finally block:
			}

		} else {

			RequestDispatcher dispatch = req
					.getRequestDispatcher("filesysteminfo.jsp");
			req.setAttribute("imageList", imageList);
			
			dispatch.forward(req, resp);
		}
	}

}
