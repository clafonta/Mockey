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
import org.xml.sax.SAXException;

import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.storage.xml.MockeyXmlFileConfigurationReader;

/**
 * 
 * @author Chad.Lafontaine
 * 
 */
public class UploadConfigurationServlet extends HttpServlet {

    private static final long serialVersionUID = 2874257060865115637L;
    private static IMockeyStorage store = StorageRegistry.MockeyStorage;
    //private static Logger logger = Logger.getLogger(UploadConfigurationServlet.class);

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
        List<String> conflicts = new ArrayList<String>();
        List<String> additions = new ArrayList<String>();
        try {
            // Create a new file upload handler
            DiskFileUpload upload = new DiskFileUpload();
            

            // Parse the request
            List<FileItem> items = upload.parseRequest(req);
            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (!item.isFormField()) {
                    //logger.debug("Upload servlet: found file " + item.getFieldName());

                    byte[] data = item.get();
                    String strXMLDefintion = new String(data);
                    
                    MockeyXmlFileConfigurationReader msfr = new MockeyXmlFileConfigurationReader();
                    IMockeyStorage mockServiceStoreTemporary = msfr.readDefinition(strXMLDefintion);
                    // PROXY SETTINGs
                    store.setProxy(mockServiceStoreTemporary.getProxy());
                    
                    // UNIVERSAL RESPONSE SETTINGS
                    if (store.getUniversalErrorScenario() != null
                            && mockServiceStoreTemporary.getUniversalErrorScenario() != null) {
                        conflicts.add("<b>Universal error message</b>: one already defined with name '"
                                + store.getUniversalErrorScenario().getScenarioName() + "'");
                    } else if (store.getUniversalErrorScenario() == null
                            && mockServiceStoreTemporary.getUniversalErrorScenario() != null) {
                        
                        store.setUniversalErrorScenarioId(mockServiceStoreTemporary.getUniversalErrorScenario().getId());
                        store.setUniversalErrorServiceId(mockServiceStoreTemporary.getUniversalErrorScenario().getServiceId());
                        
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
                    List uploadedServices = mockServiceStoreTemporary.getServices();
                    Iterator iter2 = uploadedServices.iterator();
                    while (iter2.hasNext()) {
                        Service uploadedServiceBean = (Service) iter2.next();
                        List serviceBeansInMemory = store.getServices();
                        Iterator iter3 = serviceBeansInMemory.iterator();
                        boolean existingServiceWithMatchingMockUrl = false;
                        Service inMemoryServiceBean = null;
                        while (iter3.hasNext()) {
                            inMemoryServiceBean = (Service) iter3.next();
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
                            store.saveOrUpdateService(uploadedServiceBean);
                            additions.add("<b>Service Added</b>: '" + uploadedServiceBean.getServiceName() + "'");

                        } else {
                            // Just save scenarios
                            Iterator uIter = uploadedServiceBean.getScenarios().iterator();
                            Iterator mIter = inMemoryServiceBean.getScenarios().iterator();
                            while (uIter.hasNext()) {
                                Scenario uBean = (Scenario) uIter.next();
                                boolean existingScenario = false;
                                Scenario mBean = null;
                                while (mIter.hasNext()) {
                                    mBean = (Scenario) mIter.next();
                                    if (mBean.getScenarioName().equals(uBean.getScenarioName())) {
                                        existingScenario = true;
                                        break;
                                    }
                                }
                                if (!existingScenario) {
                                    uBean.setServiceId(inMemoryServiceBean.getId());
                                    inMemoryServiceBean.updateScenario(uBean);
                                    store.saveOrUpdateService(inMemoryServiceBean);
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
                    List servicePlans = mockServiceStoreTemporary.getServicePlans();
                    Iterator iter3 = servicePlans.iterator();
                    while (iter3.hasNext()) {
                        ServicePlan servicePlan = (ServicePlan) iter3.next();
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
