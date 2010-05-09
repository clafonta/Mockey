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
                    ConfigurationReader configurationReader = new ConfigurationReader();
                    ConfigurationReadResults results = configurationReader.loadConfiguration(data);

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
