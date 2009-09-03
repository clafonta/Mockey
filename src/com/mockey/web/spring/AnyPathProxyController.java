package com.mockey.web.spring;

import com.mockey.MockServiceBean;
import com.mockey.MockServiceStore;
import com.mockey.util.Url;
import com.mockey.web.MockResponseServlet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Pass through proxy that will proxy any URL to a configured host.
 */
public class AnyPathProxyController extends AbstractController{
    @Autowired(required = true)
    private MockServiceStore mockServiceStore;
    private Logger logger = Logger.getLogger(AnyPathProxyController.class);

    public void setMockServiceStore(MockServiceStore mockServiceStore) {
        this.mockServiceStore = mockServiceStore;
        logger.debug("This store ID: " + this.mockServiceStore.toString());
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String requestedUri = (String) request.getAttribute(SimpleUrlHandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if(requestedUri.startsWith("/")) {
            requestedUri = requestedUri.substring(1,requestedUri.length());
        }

        Url url = new Url(requestedUri);

        // get the host we are proxying for from the request
        // figure out the path we are sending the request to
        // send the request along

        MockServiceBean service = new MockServiceBean(url);
        MockServiceBean existingService = mockServiceStore.getMockServiceByUrl(service.getMockServiceUrl());

        if (existingService == null) {
            logger.debug("Not found. Saving new service with URL: "  + service.getMockServiceUrl());
            mockServiceStore.saveOrUpdate(service);
        } else {
            logger.debug("Found! service with URL: "  + existingService.getMockServiceUrl());
            service = existingService;
        }
      
        request.getRequestDispatcher("/service"+service.getMockServiceUrl()).forward(request,response);
        return null;
    }
}
