package com.mockey.api;

import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.ui.FilterHelper;
import com.mockey.ui.HomeServlet;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.List;
import javax.ws.rs.core.GenericEntity;

@Path("/")
public class ServiceList {
    private static IMockeyStorage store = StorageRegistry.MockeyStorage;
    private static Logger logger = Logger.getLogger(ServiceList.class);
    @GET
    @Path("/mockservicelist")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getServiceList() {

        String filterTagArg = store.getGlobalStateSystemFilterTag();

        FilterHelper filterHelper = new FilterHelper();
        List<Service> list = filterHelper.getFilteredServices(filterTagArg, store);

//        GenericEntity<List<Service>> list = new GenericEntity<List<Service>>(filteredServiceList) {
//        };

        return Response.ok(list).build();
    }

    @GET
    @Path("/{param}")
    public Response getMsg(@PathParam("param") String msg) {

        String output = "Jersey say : " + msg;

        return Response.status(200).entity(output).build();

    }
}
