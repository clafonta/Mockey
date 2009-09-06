package com.mockey.web;

import java.io.IOException;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mockey.model.Url;

/**
 * Help Servlet
 * 
 * @author chad.lafontaine
 *
 */
public class HelpServlet extends HttpServlet {

   
    private static final long serialVersionUID = 8793774336587312539L;

    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

       
        RequestDispatcher dispatch = req.getRequestDispatcher("help.jsp");

        // HINT Message
        URL serverURLObj = new URL(req.getScheme(), // http
                req.getServerName(), // host
                req.getServerPort(), // port
                "");

        String contextRoot = req.getContextPath();
        String hintRecordURL1 = serverURLObj.toString();
        String hintRecordURL2 = serverURLObj.toString();

        
        if (contextRoot != null && contextRoot.length() > 0 ) {
            hintRecordURL1 = hintRecordURL1 + contextRoot;
            hintRecordURL2 = hintRecordURL2 + contextRoot;
        }
        hintRecordURL1 = hintRecordURL1 + Url.MOCK_SERVICE_PATH + "http://www.google.com/search?q=flavor";
        hintRecordURL2 = hintRecordURL2 + Url.MOCK_SERVICE_PATH + "http://e-services.doh.go.th/dohweb/dohwebservice.asmx?wsdl";
        req.setAttribute("hintRecordUrl1", hintRecordURL1);
        req.setAttribute("hintRecordUrl2", hintRecordURL2);
        dispatch.forward(req, resp);
    }

}

