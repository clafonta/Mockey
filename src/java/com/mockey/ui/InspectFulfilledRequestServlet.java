package com.mockey.ui;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mockey.model.FulfilledClientRequest;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * Responsible for serving mock responses. Based on configuration, returns
 * desired content either from a source (Mockey being used as a proxy) or from a
 * defined scenario.
 * 
 * @author chad.lafontaine
 * 
 */
public class InspectFulfilledRequestServlet extends HttpServlet {

    private static final long serialVersionUID = 8401356766354139506L;
    private IMockeyStorage store = StorageRegistry.MockeyStorage;
    private Logger logger = Logger.getLogger(InspectFulfilledRequestServlet.class);


    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
    	String contentType = req.getParameter("content_type");
    	Long fulfilledRequestId = new Long(req.getParameter("fulfilledRequestId"));
    	FulfilledClientRequest fulfilledClientRequest = store.getFulfilledClientRequestsById(fulfilledRequestId);
    	resp.setContentType(contentType);
    	new PrintStream(resp.getOutputStream()).println(fulfilledClientRequest.getResponseMessage().getBody());

    }    
    
  }
