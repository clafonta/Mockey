package com.mockey.api;

import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.ui.FilterHelper;
import com.mockey.ui.HomeServlet;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/mockservicelist")
public class MockServiceList {

    private static IMockeyStorage store = StorageRegistry.MockeyStorage;
    private static Logger logger = Logger.getLogger(HomeServlet.class);

    // This method is called if TEXT_PLAIN is request
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Service> getServiceList() {


        String filterTagArg = store.getGlobalStateSystemFilterTag();
        FilterHelper filterHelper = new FilterHelper();
        List<Service> filteredServiceList = filterHelper.getFilteredServices(filterTagArg, store);
        return filteredServiceList;
    }

    @GET
    @Path("/{param}")
    public Response getMsg(@PathParam("param") String msg) {

        String output = "Jersey say : " + msg;

        return Response.status(200).entity(output).build();

    }

    

}