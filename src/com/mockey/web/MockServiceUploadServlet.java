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
import java.util.ArrayList;
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
import com.mockey.MockServiceScenarioBean;
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
        List conflicts = new ArrayList();
        List additions = new ArrayList();
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
                    if (store.getUniversalErrorResponse() != null
                            && mockServiceStoreTemporary.getUniversalErrorResponse() != null) {
                        conflicts.add("<b>Universal error message</b>: one already defined with name '"
                                + store.getUniversalErrorResponse().getScenarioName() + "'");
                    } else if (store.getUniversalErrorResponse() == null
                            && mockServiceStoreTemporary.getUniversalErrorResponse() != null) {
                        
                        store.setUniversalErrorScenarioId(mockServiceStoreTemporary.getUniversalErrorResponse().getId());
                        store.setUniversalErrorServiceId(mockServiceStoreTemporary.getUniversalErrorResponse().getServiceId());
                        additions.add("<b>Universal error response defined.</b>");
                                
                    }
                    // When loading a definition file, by default, we should
                    // compare uploaded Service’s mock URL to what's currently
                    // in memory.
                    //
                    // 1) MATCHING MOCK URL
                    // If there is an existing/matching mockURL, then this isn't
                    // a new service and we DON'T want to overwrite. But, we
                    // want new Scenarios if they exist. A new scenario is based
                    // on
                    //
                    // 2) NO MATCHING MOCK URL
                    // If there is no matching service URL, then create a new
                    // service and associated scenarios.
                    List uploadedServices = mockServiceStoreTemporary.getOrderedList();
                    Iterator iter2 = uploadedServices.iterator();
                    while (iter2.hasNext()) {
                        MockServiceBean uploadedServiceBean = (MockServiceBean) iter2.next();
                        List serviceBeansInMemory = store.getOrderedList();
                        Iterator iter3 = serviceBeansInMemory.iterator();
                        boolean existingServiceWithMatchingMockUrl = false;
                        MockServiceBean inMemoryServiceBean = null;
                        while (iter3.hasNext()) {
                            inMemoryServiceBean = (MockServiceBean) iter3.next();
                            if (inMemoryServiceBean.getMockServiceUrl().equals(uploadedServiceBean.getMockServiceUrl())) {
                                existingServiceWithMatchingMockUrl = true;
                                conflicts.add("<b>Service not added</b>: Matching mock URL '"
                                        + uploadedServiceBean.getMockServiceUrl() + "' with" + " service name '"
                                        + uploadedServiceBean.getServiceName() + "'");
                                break;
                            }
                        }
                        if (!existingServiceWithMatchingMockUrl) {
                            // We null it, to not stomp on any services
                            uploadedServiceBean.setId(null);
                            store.saveOrUpdate(uploadedServiceBean);
                            additions.add("<b>Service Added</b>: '" + uploadedServiceBean.getServiceName() + "'");

                        } else {
                            // Just save scenarios
                            Iterator uIter = uploadedServiceBean.getScenarios().iterator();
                            Iterator mIter = inMemoryServiceBean.getScenarios().iterator();
                            while (uIter.hasNext()) {
                                MockServiceScenarioBean uBean = (MockServiceScenarioBean) uIter.next();
                                boolean existingScenario = false;
                                MockServiceScenarioBean mBean = null;
                                while (mIter.hasNext()) {
                                    mBean = (MockServiceScenarioBean) mIter.next();
                                    if (mBean.getScenarioName().equals(uBean.getScenarioName())) {
                                        existingScenario = true;
                                        break;
                                    }
                                }
                                if (!existingScenario) {
                                    uBean.setServiceId(inMemoryServiceBean.getId());
                                    inMemoryServiceBean.updateScenario(uBean);
                                    store.saveOrUpdate(inMemoryServiceBean);
                                    additions.add("<b>Scenario Added</b>: '" + uBean.getScenarioName()
                                            + "' added to existing service '" + inMemoryServiceBean.getServiceName()
                                            + "'");
                                } else {
                                    conflicts.add("<b>Scenario not added</b>: '" + mBean.getScenarioName()
                                            + "', already defined in service '" + inMemoryServiceBean.getServiceName()
                                            + "'");
                                }

                            }
                        }

                    }
                    // PLANS
                    List servicePlans = mockServiceStoreTemporary.getMockServicePlanList();
                    Iterator iter3 = servicePlans.iterator();
                    while (iter3.hasNext()) {
                        MockServicePlan servicePlan = (MockServicePlan) iter3.next();
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
        req.setAttribute("conflicts", conflicts);
        req.setAttribute("additions", additions);
        RequestDispatcher dispatch = req.getRequestDispatcher("/upload.jsp");
        dispatch.forward(req, resp);
    }
}
