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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author clafonta
 *
 */
public class FileDeleteServlet extends HttpServlet {

	private static final long serialVersionUID = -7334916323927032682L;

	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		FileSystemManager fsm = new FileSystemManager();

		String fileName = req.getParameter("filename");
		boolean deletedFile = false;
		String msg = fileName;

		if (fileName != null) {
			fsm.deleteImageFile(fileName);
		} else {
			msg = "Missing sourceDir and/or filename arguments.";
		}

		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		JSONObject json = new JSONObject();
		String resultingJSON = null;
		try {
			json.append("status", deletedFile);
			json.append("msg", msg);

			resultingJSON = json.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		out.println(resultingJSON);
		out.flush();
		out.close();
		return;

	}

}
