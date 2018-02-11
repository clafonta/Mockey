package com.mockey.api;


import com.mockey.model.Scenario;
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
import javax.ws.rs.core.Response;


@Path("/scenario")
public class ScenarioUpdate {
    private static IMockeyStorage store = StorageRegistry.MockeyStorage;
    private static Logger logger = Logger.getLogger(HomeServlet.class);

    // This method is called if TEXT_PLAIN is request
    @GET
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Scenario updateScenario() {


        String filterTagArg = store.getGlobalStateSystemFilterTag();
        FilterHelper filterHelper = new FilterHelper();
        Scenario model = new Scenario();

        return model;
    }
}
