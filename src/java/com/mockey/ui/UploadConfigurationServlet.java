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
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;

import com.mockey.storage.xml.MockeyXmlFileManager;

/**
 * 
 * @author Chad.Lafontaine
 * 
 */
public class UploadConfigurationServlet extends HttpServlet {

    private static final long serialVersionUID = 2874257060865115637L;

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

        try {
            // Create a new file upload handler
            DiskFileUpload upload = new DiskFileUpload();

            // Parse the request
            List<FileItem> items = upload.parseRequest(req);
            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (!item.isFormField()) {

                    byte[] data = item.get();
                    MockeyXmlFileManager configurationReader = new MockeyXmlFileManager();
                    ServiceMergeResults results = configurationReader.loadConfigurationWithXmlDef(new String(data));

                    Util.saveSuccessMessage("Service definitions uploaded.", req);
                    req.setAttribute("conflicts", results.getConflictMsgs());
                    req.setAttribute("additions", results.getAdditionMessages());

                }
            }
        } catch (Exception e) {
            Util.saveErrorMessage("Unable to upload or parse file.", req);
        }

        RequestDispatcher dispatch = req.getRequestDispatcher("/upload.jsp");
        dispatch.forward(req, resp);
    }
}
