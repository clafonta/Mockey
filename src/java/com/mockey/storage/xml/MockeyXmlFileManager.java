/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2009-2010  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
 * neil.cronin (neil AT rackle DOT com) 
 * lorin.kobashigawa (lkb AT kgawa DOT com)
 * rob.meyer (rob AT bigdis DOT com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.mockey.storage.xml;

import com.google.common.io.CharStreams;
import com.mockey.model.*;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.ui.ServiceMergeResults;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Consumes an XML file and configures Mockey services.
 * <p>
 * <pre>
 * + mock_service_definitions.xml
 * + mockey_def_depot
 * ++ <SERVICE NAME>
 * ++ <SERVICE NAME>
 * +++ <NAME>.xml
 * +++ scenarios
 * ++++ 1.xml
 * ++++ 2.xml
 *
 * </pre>
 *
 * @author Chad.Lafontaine
 */
public class MockeyXmlFileManager {

    private static Logger logger;

    static {
        try {
            logger = Logger.getLogger(MockeyXmlFileManager.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static final char[] VALID_FILE_NAME_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_"
            .toCharArray();

    private File basePathFile = null;
    private static IMockeyStorage store = StorageRegistry.MockeyStorage;
    private static MockeyXmlFileManager mockeyXmlFileManagerInstance = null;
    public static final String MOCK_SERVICE_DEFINITION = "mock_service_definitions.xml";
    public static final String SYSTEM_PROPERTY_MOCKEY_DEF_REPO_HOME = "mockeyDefinitionsRepoHome";

    protected static final String MOCK_SERVICE_FOLDER = "mockey_def_depot";
    protected static final String MOCK_SERVICE_SCENARIO_FOLDER = "scenarios";

    public static final String FILESEPERATOR = System.getProperty("file.separator");

    /**
     * Basic constructor. Will create a folder on the file system to store XML definitions.
     */
    private MockeyXmlFileManager(String path) {

        this.basePathFile = new File(path);
        if (!this.basePathFile.exists()) {
            this.basePathFile.mkdir();
        }

        File fileDepot = new File(this.getBasePathFile(), MOCK_SERVICE_FOLDER);
        if (!fileDepot.exists()) {
            boolean success = fileDepot.mkdirs();
            if (!success) {
                logger.fatal("Unable to create a folder called " + MOCK_SERVICE_FOLDER);
            } else {
                logger.info("Created directory: " + fileDepot.getAbsolutePath());
            }
        }
    }

    protected void cleanDirectory() {
        File fileDepot = new File(this.getBasePathFile(), MOCK_SERVICE_FOLDER);
        if (fileDepot.isDirectory()) {

            try {
                // Delete it.
                FileUtils.deleteDirectory(fileDepot);
                // Create new one.
                fileDepot.mkdirs();

            } catch (IOException io) {
                logger.error("Unable to delete" + fileDepot.getAbsolutePath(), io);
            }


        }
    }

    /**
     * Location of files is
     *
     * @return
     */
    public static MockeyXmlFileManager getInstance() {

        if (MockeyXmlFileManager.mockeyXmlFileManagerInstance == null) {

            // If no explicit path, then check for a system variable.
            // Check for SYSTEM PROPERTY
            String defaultUserDirectory = System.getProperty("user.dir");
            String systemVariableDirectory = System.getProperty(SYSTEM_PROPERTY_MOCKEY_DEF_REPO_HOME);
            if (systemVariableDirectory != null) {
                String msg = "System environment variable '" + SYSTEM_PROPERTY_MOCKEY_DEF_REPO_HOME
                        + "' value is provided. Writing local Mockey definition files here: " + systemVariableDirectory;
                logger.info(msg);
                System.out.println(msg);
                MockeyXmlFileManager.getInstanceWithRepoPath(systemVariableDirectory);
            } else {

                String msg = "No system environment variable,'" + SYSTEM_PROPERTY_MOCKEY_DEF_REPO_HOME
                        + "' variable is provided. Writing local Mockey definition files here: " + defaultUserDirectory;
                logger.info(msg);
                System.out.println(msg);
                MockeyXmlFileManager.getInstanceWithRepoPath(defaultUserDirectory);
            }


        }
        return MockeyXmlFileManager.mockeyXmlFileManagerInstance;
    }

    /**
     * @param path
     * @return TODO
     */
    public static MockeyXmlFileManager getInstanceWithRepoPath(String path) {


        MockeyXmlFileManager.mockeyXmlFileManagerInstance = new MockeyXmlFileManager(path);
        return MockeyXmlFileManager.mockeyXmlFileManagerInstance;

    }

    /**
     * @return location of Mockey definitions.
     */
    public File getBasePathFile() {
        if (this.basePathFile == null) {
            throw new RuntimeException("Base path is NOT set.");
        }
        return this.basePathFile;
    }

    /**
     * @param file - xml configuration file for Mockey
     * @throws IOException
     * @throws SAXException
     * @throws SAXParseException
     */
    public String getFileContentAsString(InputStream fstream) throws IOException, SAXParseException, SAXException {

        String inputStreamString = CharStreams.toString(new InputStreamReader(fstream, "UTF-8"));
        return inputStreamString;

    }

    /**
     * Loads from default file definition file.
     *
     * @return results of loading configuration, includes additions and possible conflicts.
     * @throws SAXParseException
     * @throws IOException
     */
    public ServiceMergeResults loadConfiguration() throws SAXParseException, IOException {

        File n = new File(this.getBasePathFile(), MOCK_SERVICE_DEFINITION);
        logger.debug("Loading configuration from " + n.getAbsolutePath());

        try {

            return loadConfigurationWithXmlDef(getFileContentAsString(new FileInputStream(n)), null);
        } catch (SAXException e) {
            logger.error("Ouch, unable to parse" + n.getAbsolutePath(), e);
        }
        return new ServiceMergeResults();
    }

    /**
     * @param strXMLDefintion
     * @param tagArguments
     * @return results (conflicts and additions).
     * @throws IOException
     * @throws SAXParseException
     * @throws SAXException
     */
    public ServiceMergeResults loadConfigurationWithXmlDef(String strXMLDefintion, String tagArguments)
            throws IOException, SAXParseException, SAXException {
        ServiceMergeResults mergeResults = new ServiceMergeResults();

        // ***** REMEMBER *****
        // Every time a saveOrUpdateXXXX is made, the entire STORE is written to
        // the file system.
        // If the STORE has many definitions, then each SAVE will loop over
        // every file and write.
        //
        // NOT GOOD FOR PERFORMANCE
        //
        // Solution: put the store in a temporary transient state
        // (memory-mode-only), then revert to original transient setting,
        // which could have been in memory-only or write-to-file in the
        // first place.
        //
        // *********************
        Boolean originalTransientState = store.getReadOnlyMode();
        store.setReadOnlyMode(true);

        // STEP #1. CREATE A TEMP STORE
        // Read the incoming XML file, and create a new/temporary store for the
        // need to ensure current store doesn't get overridden
        //
        MockeyXmlFileConfigurationReader msfr = new MockeyXmlFileConfigurationReader();
        IMockeyStorage mockServiceStoreTemporary = msfr.readDefinition(strXMLDefintion);

        // STEP #2. PROXY SETTINGS
        // If the proxy settings are _empty_, then set the incoming
        // proxy settings. Otherwise, call out a merge conflict.
        //
        ProxyServerModel proxyServerModel = store.getProxy();
        if (proxyServerModel.hasSettings()) {
            mergeResults.addConflictMsg("Proxy settings NOT set from incoming file.");
        } else {
            store.setProxy(mockServiceStoreTemporary.getProxy());
            mergeResults.addAdditionMsg("Proxy settings set.");
        }

        // STEP #3. BUILD SERVICE REFERENCES
        // Why is this needed?
        // We are adding _new_ services into the Store, and that means that the
        // store's state is always changing. We need references as a saved
        // snapshot list of store state prior to adding new services.
        // **********
        // I forget why we really need this though...
        // **********
        List<Service> serviceListFromRefs = new ArrayList<Service>();
        for (ServiceRef serviceRef : mockServiceStoreTemporary.getServiceRefs()) {
            try {

                File childFile = new File(this.getBasePathFile(), serviceRef.getFileName());
                String mockServiceDefinition = getFileContentAsString(new FileInputStream(childFile));

                // HACK:
                // I tried to find an easier way to use XML ENTITY and let
                // Digester
                // to the work of slurping up the XML but was unsuccessful.
                // Hence, the brute force.
                // YYYYY
                //

                List<Service> tmpList = msfr.readServiceDefinition(mockServiceDefinition);
                for (Service tmpService : tmpList) {
                    serviceListFromRefs.add(tmpService);
                }
            } catch (SAXParseException spe) {
                logger.error("Unable to parse file of name " + serviceRef.getFileName(), spe);
                mergeResults.addConflictMsg("File not parseable: " + serviceRef.getFileName());

            } catch (FileNotFoundException fnf) {
                logger.error("File not found: " + serviceRef.getFileName());
                mergeResults.addConflictMsg("File not found: " + serviceRef.getFileName());
            }

        }
        addServicesToStore(mergeResults, serviceListFromRefs, tagArguments);

        // STEP #4. MERGE SERVICES AND SCENARIOS
        // Since this gets complicated, logic was moved to it's own method.
        mergeResults = addServicesToStore(mergeResults, mockServiceStoreTemporary.getServices(), tagArguments);

        // STEP #5. UNIVERSAL RESPONSE SETTINGS
        // Important: usage of the temporary-store's Scenario reference
        // information is used to set the primary in-memory store. The primary
        // store has all the information and the TEMP store only needs to pass
        // the references, e.g. Service 1, Scenario 2.
        if (store.getUniversalErrorScenario() != null
                && mockServiceStoreTemporary.getUniversalErrorScenarioRef() != null) {
            mergeResults.addConflictMsg("Universal error message already defined with name '"
                    + store.getUniversalErrorScenario().getScenarioName() + "'");
        } else if (store.getUniversalErrorScenario() == null
                && mockServiceStoreTemporary.getUniversalErrorScenarioRef() != null) {
            store.setUniversalErrorScenarioRef(mockServiceStoreTemporary.getUniversalErrorScenarioRef());
            mergeResults.addAdditionMsg("Universal error response defined.");

        }

        // STEP #6. MERGE SERVICE PLANS
        for (ServicePlan servicePlan : mockServiceStoreTemporary.getServicePlans()) {
            if (tagArguments != null) {
                servicePlan.addTagToList(tagArguments);
            }
            store.saveOrUpdateServicePlan(servicePlan);
        }

        // STEP #7. TWIST CONFIGURATION
        for (TwistInfo twistInfo : mockServiceStoreTemporary.getTwistInfoList()) {
            store.saveOrUpdateTwistInfo(twistInfo);
        }

        // STEP #8. DEFAULT Service Plan ID
        // Only set a default service plan ID from the incoming XML file
        // if one is not already set in the current store.
        ServicePlan servicePlan = mockServiceStoreTemporary
                .getServicePlanById(mockServiceStoreTemporary.getDefaultServicePlanIdAsLong());
        if (servicePlan != null && store.getDefaultServicePlanIdAsLong() == null) {
            // OK, we have a 'default' service plan from the incoming file AND
            // the current store does not. Let's update the current store.
            store.setDefaultServicePlanId(mockServiceStoreTemporary.getDefaultServicePlanId());
            store.setServicePlan(servicePlan);

        }

        // Don't forget to set state back to original state.
        // NOTE: if transient state (read only) is false, then this method will
        // write to STORE to the file system.
        // Yeah!
        // *********************
        store.setReadOnlyMode(originalTransientState);
        // *********************

        return mergeResults;
    }

    // Let's Merge!
    private ServiceMergeResults addServicesToStore(ServiceMergeResults mergeResults, List<Service> serviceListToAdd,
                                                   String tagArguments) {
        // When loading a definition file, by default, we should
        // compare the uploaded Service list mock URL to what's currently
        // in memory.
        //
        // 1) MATCHING MOCK URL
        // If there is an existing/matching mockURL, then this isn't
        // a new service and we DON'T want to overwrite. But, we
        // want new Scenarios if they exist. See Scenario.equals()
        //
        // 2) NO MATCHING MOCK URL
        // If there is no matching service URL, then we want to create a new
        // service and associated scenarios. But here's an odd case. What if
        // we are merging two same-name Services, each with empty matching URL
        // lists?
        //

        for (Service uploadedServiceBean : serviceListToAdd) {
            List<Service> serviceBeansInMemory = store.getServices();
            Iterator<Service> inMemoryServiceIter = serviceBeansInMemory.iterator();

            boolean existingService = false;
            Service inMemoryServiceBean = null;

            while (inMemoryServiceIter.hasNext()) {
                inMemoryServiceBean = (Service) inMemoryServiceIter.next();

                // Same name?
                if (uploadedServiceBean.getServiceName().trim().toLowerCase()
                        .equals(inMemoryServiceBean.getServiceName().trim().toLowerCase())) {
                    existingService = true;
                    mergeResults.addConflictMsg("Service '" + uploadedServiceBean.getServiceName()
                            + "' not created because one with the same name already defined. '"
                            + inMemoryServiceBean.getServiceName() + "' ");

                }
            }
            if (!existingService) {
                // YES, no in-store matching Name.
                // We null ID, to not write-over on any in-store
                // services with same ID
                uploadedServiceBean.setId(null);

                // #TAG HANDLING - BEGIN
                // Ensure Service, and all it's child scenarios have
                // incoming/uploaded tag arguments
                uploadedServiceBean.addTagToList(tagArguments);
                for (Scenario scenarioTmp : uploadedServiceBean.getScenarios()) {
                    scenarioTmp.addTagToList(tagArguments);
                }
                // #TAG HANDLING - END

                // Save to the IN-MEMORY STORE
                store.saveOrUpdateService(uploadedServiceBean);
                mergeResults.addAdditionMsg(
                        "Uploaded Service '" + uploadedServiceBean.getServiceName() + "' created with scenarios.");

            } else {
                // We have an existing Service
                // Just merge scenarios per matching services
                mergeResults = this.mergeServices(uploadedServiceBean, inMemoryServiceBean, mergeResults, tagArguments);
            }

        }
        return mergeResults;
    }

    /**
     * This method will make an effort to take things that exist in the
     *
     * @param uploadedService
     * @param inMemoryService
     * @param readResults
     * @return
     */
    public ServiceMergeResults mergeServices(Service uploadedService, Service inMemoryService

            , ServiceMergeResults readResults, String tagArguments) {

        Boolean originalMode = store.getReadOnlyMode();
        store.setReadOnlyMode(true);

        if (uploadedService != null && inMemoryService != null
                && uploadedService.getServiceName().trim().equalsIgnoreCase(inMemoryService.getServiceName().trim())

                ) {

            // ********************** TAG - BEGIN ***********************
            // #TAG HANDLING for the Service - BEGIN
            // Ensure Service gets incoming/uploaded-file tag arguments
            inMemoryService.addTagToList(tagArguments);
            // Ensure Service gets uploaded Service tag arguments
            inMemoryService.addTagToList(uploadedService.getTag());
            // #TAG HANDLING for the Service - END
            // ********************** TAG - END *****************

            // ********************* SCENARIOS BEGIN *******************
            if (readResults == null) {
                readResults = new ServiceMergeResults();
            }

            Iterator<Scenario> uploadedListIter = uploadedService.getScenarios().iterator();
            Iterator<Scenario> inMemListIter = inMemoryService.getScenarios().iterator();

            while (uploadedListIter.hasNext()) {
                Scenario uploadedScenario = (Scenario) uploadedListIter.next();
                boolean inMemScenarioExistTemp = false;
                Scenario inMemScenarioTemp = null;

                while (inMemListIter.hasNext()) {
                    inMemScenarioTemp = (Scenario) inMemListIter.next();

                    if (inMemScenarioTemp.hasSameNameAndResponse(uploadedScenario)) {

                        inMemScenarioExistTemp = true;
                        break;
                    }
                }
                if (!inMemScenarioExistTemp) {

                    // Hey, we have a new scenario.
                    // NOTE: incoming/uploaded scenario has an ID.
                    // We MUST nullify it, to ensure there's no common Service's
                    // scenario's ID
                    uploadedScenario.setId(null);
                    uploadedScenario.setServiceId(inMemoryService.getId());
                    // Tag for Service:Scenario
                    uploadedScenario.addTagToList(tagArguments);
                    inMemoryService.saveOrUpdateScenario(uploadedScenario);

                    readResults.addAdditionMsg("Scenario name '" + uploadedScenario.getScenarioName()
                            + "' from uploaded service named '" + uploadedService.getServiceName()
                            + "' was merged into service '" + inMemoryService.getServiceName() + "' ");
                } else {
                    // OK, we have a MATCHING Scenario.
                    // Be sure to add the uploaded-file tags
                    inMemScenarioTemp.addTagToList(tagArguments);
                    // Be sure to add the uploaded-scenario tags
                    inMemScenarioTemp.addTagToList(uploadedScenario.getTag());
                    // Save the scenario to the Service
                    inMemoryService.saveOrUpdateScenario(inMemScenarioTemp);

                    // Although we still need to
                    readResults.addConflictMsg("Uploaded Scenario '" + uploadedScenario.getScenarioName()
                            + "' not added, already defined in in-memory service '" + inMemoryService.getServiceName()
                            + "' ");
                }

            }
            // ********************* SCENARIOS - END ******************

            // ********************* REAL URLS - BEGIN *******************
            for (Url uploadedUrl : uploadedService.getRealServiceUrls()) {
                inMemoryService.saveOrUpdateRealServiceUrl(uploadedUrl);
            }
            // ********************* REAL URLS - END *******************
            store.saveOrUpdateService(inMemoryService);

        }

        store.setReadOnlyMode(originalMode);
        return readResults;
    }

    /**
     * @param service
     * @return
     */
    protected File getServiceFile(Service service) {
        // Ensure the name is good.

        String serviceFileName = service.getServiceName();

        if (serviceFileName != null) {
            serviceFileName = getSafeForFileSystemName(serviceFileName);

            File serviceDirectoryFile = new File(this.getBasePathFile(),
                    MockeyXmlFileManager.MOCK_SERVICE_FOLDER + FILESEPERATOR + serviceFileName);
            // depot directory/<service ID> directory
            if (!serviceDirectoryFile.exists()) {
                serviceDirectoryFile.mkdir();
            }
            // depot directory/<service ID> directory/scenario directory/
            File serviceScenarioListDirectory = new File(
                    serviceDirectoryFile.getPath() + FILESEPERATOR + MOCK_SERVICE_SCENARIO_FOLDER);
            if (!serviceScenarioListDirectory.exists()) {
                serviceScenarioListDirectory.mkdir();
            }
            // depot directory/<service ID> directory/<service ID> file
            File serviceFile = new File(serviceDirectoryFile.getPath() + FILESEPERATOR + serviceFileName + ".xml");
            return serviceFile;
        } else {
            return null;
        }

    }

    protected File[] getServiceScenarioFileNames(Service service) {

        File serviceScenarioDir = new File(this.getBasePathFile(),
                getSafeForFileSystemName(service.getServiceName()) + FILESEPERATOR + MOCK_SERVICE_SCENARIO_FOLDER);
        return serviceScenarioDir.listFiles();

    }

    private File getServiceScenarioDirectoryAbsolutePath(Service service, Scenario scenario) {
        // mockey_def_depot/<service ID>/scenarios/<scenario_name>.xml
        File serviceScenarioFolder = new File(this.getBasePathFile(),
                MockeyXmlFileManager.MOCK_SERVICE_FOLDER + FILESEPERATOR
                        + getSafeForFileSystemName(service.getServiceName()) + FILESEPERATOR
                        + MOCK_SERVICE_SCENARIO_FOLDER);

        return serviceScenarioFolder;
    }

    protected File getServiceScenarioFileAbsolutePath(Service service, Scenario scenario) {
        // mockey_def_depot/<service ID>/scenarios/<scenario_name>.xml
        File serviceScenarioFolder = getServiceScenarioDirectoryAbsolutePath(service, scenario);
        File serviceScenarioFile = new File(
                serviceScenarioFolder.getPath() + FILESEPERATOR + getScenarioXmlFileName(scenario));

        return serviceScenarioFile;
    }

    /**
     * Example if file is here:
     * <p>
     * <pre>
     * // /Users/<User>/Work/Mockey/dist/mockey_def_depot/<ServiceName>/scenarios/<ScenarioName>.xml
     * </pre>
     * <p>
     * then returns
     * <p>
     * <pre>
     * mockey_def_depot/<ServiceName>/scenarios/<ScenarioName>.xml
     * </pre>
     *
     * @param service
     * @param scenario
     * @return a path name relative to the root mockey depot folder.
     */
    protected String getServiceScenarioFileRelativePathToDepotFolder(Service service, Scenario scenario) {

        String relativePath = MockeyXmlFileManager.MOCK_SERVICE_FOLDER + FILESEPERATOR
                + getSafeForFileSystemName(service.getServiceName()) + FILESEPERATOR + MOCK_SERVICE_SCENARIO_FOLDER
                + FILESEPERATOR + getSafeForFileSystemName(scenario.getScenarioName()) + ".xml";

        return relativePath;
    }

    /**
     * @param scenario
     * @return
     */
    public String getScenarioResponseFileName(Scenario scenario) {
        return getSafeForFileSystemName(scenario.getScenarioName()) + ".txt";
    }

    /**
     * @param scenario
     * @return
     */
    public String getScenarioXmlFileName(Scenario scenario) {
        return getSafeForFileSystemName(scenario.getScenarioName()) + ".xml";
    }

    /**
     * @param arg
     * @return a file name safe for a file system.
     * @see MockeyXmlFileManager#VALID_FILE_NAME_CHARS
     */
    public static String getSafeForFileSystemName(String arg) {

        // Let's make sure we only accept valid characters (AlphaNumberic +
        // '_').
        StringBuffer safe = new StringBuffer();
        for (int x = 0; x < arg.length(); x++) {
            boolean valid = false;
            for (int i = 0; i < VALID_FILE_NAME_CHARS.length; i++) {
                if (arg.charAt(x) == VALID_FILE_NAME_CHARS[i]) {
                    valid = true;
                    break;
                }
            }
            if (valid) {
                safe.append(arg.charAt(x));
            }
        }
        return safe.toString().toLowerCase();

        //
    }

    /**
     * @param fileArg
     * @return the RELATIVE path of the fileArg if a child of base file. Otherwise, returns the absolute path of the
     * fileArg.
     */
    public String getRelativePath(File fileArg) {
        String relativePath = "";
        String basePath = this.getBasePathFile().getAbsolutePath();
        String filePath = fileArg.getAbsolutePath();
        int index = filePath.indexOf(basePath);
        if (index > -1) {
            try {
                //
                relativePath = fileArg.getAbsolutePath().substring(index + basePath.length() + 1);
            } catch (Exception e) {
                logger.error("Unable to retrive a relative path from: " + fileArg.getAbsolutePath()
                        + "' with base path '" + basePath + "'", e);
                relativePath = "ERROR";
            }
        } else {
            relativePath = "ERROR";
            logger.error("Unable to retrive a relative path from: " + fileArg.getAbsolutePath() + "' with base path '"
                    + basePath + "'");
        }
        return relativePath;
    }

}
