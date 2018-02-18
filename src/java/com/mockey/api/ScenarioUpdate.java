package com.mockey.api;


import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.ui.FilterHelper;
import com.mockey.ui.HomeServlet;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/scenario")
public class ScenarioUpdate {
    private static IMockeyStorage store = StorageRegistry.MockeyStorage;
    private static Logger logger = Logger.getLogger(HomeServlet.class);

    // This method is called if TEXT_PLAIN is request
    @POST
    //@Consumes({MediaType.APPLICATION_JSON})
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Scenario updateScenario(Scenario model) {


        String filterTagArg = store.getGlobalStateSystemFilterTag();
        FilterHelper filterHelper = new FilterHelper();
        Scenario scenario = new Scenario();
                                 model.toString();
        return model;
    }
}
