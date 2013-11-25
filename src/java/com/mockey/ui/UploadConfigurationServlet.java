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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import com.mockey.storage.xml.MockeyXmlFileManager;

/**
 * 
 * @author Chad.Lafontaine
 * 
 */
public class UploadConfigurationServlet extends HttpServlet {

	private static final long serialVersionUID = 2874257060865115637L;
	private static Logger logger = Logger.getLogger(UploadConfigurationServlet.class);

	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.service(req, resp);
	}

	/**
	 * 
	 * 
	 * @param req
	 *            basic request
	 * @param resp
	 *            basic resp
	 * @throws ServletException
	 *             basic
	 * @throws IOException
	 *             basic
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		RequestDispatcher dispatch = req.getRequestDispatcher("/upload.jsp");
		dispatch.forward(req, resp);
	}

	/**
	 * 
	 * 
	 * @param req
	 *            basic request
	 * @param resp
	 *            basic resp
	 * @throws ServletException
	 *             basic
	 * @throws IOException
	 *             basic
	 */
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String url = null;
		String definitionsAsString = null;
		String taglistValue = "";

		// ***********************************
		// STEP #1 - READ DATA
		// ***********************************
		if (req.getParameter("viaUrl") != null) {
			url = req.getParameter("url");
			taglistValue = req.getParameter("taglist");
			try {
				InputStream fstream = new URL(url).openStream();
				if (fstream != null) {

					BufferedReader br = new BufferedReader(new InputStreamReader(fstream, Charset.forName(HTTP.UTF_8)));
					StringBuffer inputString = new StringBuffer();
					// Read File Line By Line
					String strLine = null;
					// READ FIRST
					while ((strLine = br.readLine()) != null) {
						// Print the content on the console
						inputString.append(new String(strLine.getBytes(HTTP.UTF_8)));
					}
					definitionsAsString = inputString.toString();
				}
			} catch (Exception e) {
				logger.error("Unable to reach url: " + url, e);
				Util.saveErrorMessage("Unable to reach url: " + url, req);
			}
		} else {
			byte[] data = null;
			try {
				// STEP 1. GATHER UPLOADED ITEMS
				// Create a new file upload handler
				DiskFileUpload upload = new DiskFileUpload();

				// Parse the request
				List<FileItem> items = upload.parseRequest(req);
				Iterator<FileItem> iter = items.iterator();
				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();

					if (!item.isFormField()) {

						data = item.get();

					} else {

						String name = item.getFieldName();
						if ("taglist".equals(name)) {
							taglistValue = item.getString();
						}

					}
				}
				if (data != null && data.length > 0) {
					definitionsAsString = new String(data);
				}
			} catch (Exception e) {
				logger.error("Unable to read or parse file: ", e);
				Util.saveErrorMessage("Unable to upload or parse file.", req);
			}
		}

		// ***********************************
		// STEP #2 - PERSIST DATA
		// ***********************************
		try {

			if (definitionsAsString != null) {
				MockeyXmlFileManager configurationReader = MockeyXmlFileManager.getInstance();
				
				ServiceMergeResults results = configurationReader.loadConfigurationWithXmlDef(definitionsAsString,
						taglistValue);

				Util.saveSuccessMessage("Service definitions uploaded.", req);
				req.setAttribute("conflicts", results.getConflictMsgs());
				req.setAttribute("additions", results.getAdditionMessages());
			} else {
				Util.saveErrorMessage("Unable to upload or parse empty file.", req);
			}
		} catch (Exception e) {
			Util.saveErrorMessage("Unable to upload or parse file.", req);
		}

		RequestDispatcher dispatch = req.getRequestDispatcher("/upload.jsp");
		dispatch.forward(req, resp);
	}
}
