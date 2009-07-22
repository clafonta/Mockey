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
package com.mockey.web;

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
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.mockey.MockServiceBean;
import com.mockey.MockServicePlan;
import com.mockey.MockServiceStore;
import com.mockey.MockServiceStoreImpl;
import com.mockey.xml.MockServiceFileReader;

/**
 * 
 * @author Chad.Lafontaine
 *
 */
public class MockServiceUploadServlet extends HttpServlet {
	
	private static final long serialVersionUID = 2874257060865115637L;
	private static MockServiceStore store = MockServiceStoreImpl.getInstance();
	private static Logger logger = Logger.getLogger(MockServiceUploadServlet.class);

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
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			// Create a new file upload handler
			DiskFileUpload upload = new DiskFileUpload();
			logger.debug("Upload servlet");
			// Parse the request
			List items = upload.parseRequest(req);
			Iterator iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();

				if (!item.isFormField()) {
					logger.debug("Upload servlet: found file " + item.getFieldName());

					byte[] data = item.get();
					String strXMLDefintion = new String(data);

					MockServiceFileReader msfr = new MockServiceFileReader();
					MockServiceStore mockServiceStoreTemporary = msfr.readDefinition(strXMLDefintion);
					// SERVICES
					List uploadedServices = mockServiceStoreTemporary.getOrderedList();
					Iterator iter2 = uploadedServices.iterator();
					while (iter2.hasNext()) {
						MockServiceBean object = (MockServiceBean) iter2.next();
						store.saveOrUpdate(object);
						
					}
					// PLANS
					List servicePlans = mockServiceStoreTemporary.getMockServicePlanList();
					Iterator iter3 = servicePlans.iterator();
					while(iter3.hasNext()){
						MockServicePlan servicePlan = (MockServicePlan)iter3.next();
						store.saveOrUpdateServicePlan(servicePlan);
					}
					Util.saveSuccessMessage("Service definitions uploaded.", req);
					
				}
			}
		} catch (FileUploadException e) {
			Util.saveErrorMessage("Unable to upload file.", req);
		}

		catch (SAXException e) {			
			Util.saveErrorMessage("Unable to parse file.", req);
			
		}

		RequestDispatcher dispatch = req.getRequestDispatcher("/upload.jsp");
		dispatch.forward(req, resp);
	}
}
