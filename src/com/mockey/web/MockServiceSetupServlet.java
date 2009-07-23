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
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mockey.MockServiceBean;
import com.mockey.MockServicePlan;
import com.mockey.MockServiceStore;
import com.mockey.MockServiceStoreImpl;
import com.mockey.MockServiceValidator;
import com.mockey.PlanItem;
import com.mockey.util.Url;

public class MockServiceSetupServlet extends HttpServlet {
    private Log log = LogFactory.getLog(MockServiceSetupServlet.class);
	
	private static final long serialVersionUID = 5503460488900643184L;
	private static MockServiceStore store = MockServiceStoreImpl.getInstance();

	/**
	 * 
	 */
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.debug("Setting up a new service");
		Long serviceId = null;
		try{
			serviceId = new Long(req.getParameter("serviceId"));
		}catch(Exception e){
			// Do nothing
		}

		if (req.getParameter("delete") != null && serviceId != null) {
			MockServiceBean bean = store.getMockServiceById(serviceId);
			store.delete(bean);
			Util.saveSuccessMessage("Service '"+bean.getServiceName()+"' was deleted.", req);
			// Check to see if any plans need an update. 
			List planList = store.getMockServicePlanList();
			Iterator iter = planList.iterator();
			String errorMessage = null;
			while(iter.hasNext()){
				MockServicePlan msp = (MockServicePlan)iter.next();
				Iterator planItemIter = msp.getPlanItemList().iterator();
				while(planItemIter.hasNext()){
					PlanItem planItem = (PlanItem)planItemIter.next();
					if(planItem.getServiceId().equals(serviceId)){
						errorMessage = "Warning: the deleted service is referenced in service plans.";
						
						break;
					}
				}
			}
			if(errorMessage!=null){
			Util.saveErrorMessage(errorMessage, req);
			}
			String contextRoot = req.getContextPath();
			resp.sendRedirect(Url.getContextAwarePath("home", contextRoot));
			return;
		}

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
		Long serviceId = null;
		try{
			serviceId = new Long(req.getParameter("serviceId"));
		}catch(Exception e){
			// Do nothing
		}
		MockServiceBean ms = null;

		if (serviceId != null) {
			ms = store.getMockServiceById(serviceId);
		}
		if (ms == null) {
			ms = new MockServiceBean();
		}

		req.setAttribute("mockservice", ms);

		RequestDispatcher dispatch = req.getRequestDispatcher("/service_setup.jsp");
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
		MockServiceBean ms = new MockServiceBean();
		Long serviceId = null;
		
		try {
			 serviceId = new Long(req.getParameter("serviceId"));
		}
		catch(Exception e){
			// Do nothing
		}
		if(serviceId!=null){
			ms = this.store.getMockServiceById(serviceId);
		}

		ms.setServiceName(req.getParameter("serviceName"));
		ms.setDescription(req.getParameter("description"));
		ms.setRealServiceUrl(req.getParameter("realServiceUrl"));
        if(req.getParameter("mockServiceUrl") != null && req.getParameter("mockServiceUrl").trim().length() > 0) {
            ms.setMockServiceUrl(req.getParameter("mockServiceUrl"));
        }else{
            ms.setMockServiceUrl(ms.getRealPath());
        }
        
		ms.setHttpHeaderDefinition(req.getParameter("httpHeaderDefinition"));		
		Map errorMap = MockServiceValidator.validate(ms);

		if ((errorMap != null) && (errorMap.size() == 0)) {
			// no errors, so create service.
			
			Util.saveSuccessMessage("Service updated.", req);
			store.saveOrUpdate(ms);
			
		} else {
			Util.saveErrorMessage("Service not added/updated.", req);
			Util.saveErrorMap(errorMap, req);
			
		}
		req.setAttribute("mockservice", ms);
		RequestDispatcher dispatch = req.getRequestDispatcher("/service_setup.jsp");
		dispatch.forward(req, resp);
	}
}
