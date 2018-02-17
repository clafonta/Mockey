package com.mockey.api;

import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.ui.FilterHelper;
import com.mockey.ui.HomeServlet;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/service")
public class ServiceUpdate {
    private static IMockeyStorage store = StorageRegistry.MockeyStorage;
    private static Logger logger = Logger.getLogger(HomeServlet.class);

    // This method is called if TEXT_PLAIN is request
    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Service updateService() {


        String filterTagArg = store.getGlobalStateSystemFilterTag();
        FilterHelper filterHelper = new FilterHelper();
        Service model = new Service();

        return model;
    }

    @GET
    @Path("/list")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getServiceList() {

        String filterTagArg = store.getGlobalStateSystemFilterTag();

        FilterHelper filterHelper = new FilterHelper();
        List<Service> list = filterHelper.getFilteredServices(filterTagArg, store);
        return Response.ok(list).build();
    }

    @GET
    @Path("/item/{param}")
    public Response getMsg(@PathParam("param") String msg) {

        String output = "Jersey say : " + msg;

        return Response.status(200).entity(output).build();

    }
}
