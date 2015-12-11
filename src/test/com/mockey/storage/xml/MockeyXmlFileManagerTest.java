package com.mockey.storage.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;
import com.mockey.ui.ServiceMergeResults;

@Test
public class MockeyXmlFileManagerTest {

	private static IMockeyStorage store = StorageRegistry.MockeyStorage;
	private static String TESTCONFIG_DIR = System.getProperty("user.dir")+File.separator  + "build" + File.separator + "test" + File.separator + "configfiles";
	
	@Test
	public void validateServiceScenarioFile(){
		
		MockeyXmlFileManager.getInstanceWithRepoPath(TESTCONFIG_DIR);
		Service feelingService = new Service();
		feelingService.setServiceName("feeling");
		feelingService.setUrl("/feeling");
		Scenario happyScenario = new Scenario();
		happyScenario.setScenarioName("happy");
		happyScenario.setResponseMessage("HAPPY");
		happyScenario = feelingService.saveOrUpdateScenario(happyScenario);
		File scenarioFile = MockeyXmlFileManager.getInstance().getServiceScenarioFileAbsolutePath(feelingService, happyScenario);
		assert(scenarioFile!=null) : "Scenario is null. It should not be.";	
		String correctPath  = TESTCONFIG_DIR + File.separator + "mockey_def_depot" +File.separator +"feeling" + File.separator +"scenarios" + File.separator +"happy.xml";
		assert(scenarioFile.getAbsolutePath().equals(correctPath)) : "Invalid scenario file path.  \n    WAS:" + scenarioFile.getAbsolutePath() +"\nCORRECT:" + correctPath;
		
	}
	
	
	@Test
	public void validateDefaultBasePath() {
		
		MockeyXmlFileManager.getInstanceWithRepoPath(TESTCONFIG_DIR);
		
		MockeyXmlFileManager fileManager = MockeyXmlFileManager.getInstance();
		
		File x = new File(TESTCONFIG_DIR);	
		assert(fileManager.getBasePathFile()!=null) : "Base file path is null. It should not be.";
		assert (x.getAbsolutePath().equals(fileManager.getBasePathFile().getAbsolutePath())) : "Base path didn't match. File manager: '" +fileManager.getBasePathFile().getAbsolutePath() 
		+ "' vs. '"+x.getAbsolutePath() + "'";

	}
	
	@Test
	public void validateNewBasePath() {
		MockeyXmlFileManager.getInstanceWithRepoPath(TESTCONFIG_DIR);
		MockeyXmlFileManager fileManager = MockeyXmlFileManager.getInstance();
		File x = new File(TESTCONFIG_DIR);
		if(!x.exists()){
			x.mkdir();
		}
		
		assert (x.getAbsolutePath().equals(fileManager.getBasePathFile().getAbsolutePath())) : "Base path didn't match. File manager: '" +fileManager.getBasePathFile().getAbsolutePath() 
		+ "' vs. '"+x.getAbsolutePath() + "'";

	}
	
	@Test
	public void validateServiceFileWithNewBasePath() {
		MockeyXmlFileManager.getInstanceWithRepoPath(TESTCONFIG_DIR);
		
		Service s = new Service();
		s.setServiceName("a_service_name");
		File x = MockeyXmlFileManager.getInstance().getServiceFile(s);
		// Path should be.."/Users/clafonta/Work/Mockey/dist/test/mockey_def_depot/a_service_name/a_service_name.xml".
		String path = TESTCONFIG_DIR +  File.separator + "mockey_def_depot" + File.separator +"a_service_name" + File.separator + "a_service_name.xml";
		
		
		assert (x.getAbsolutePath().equals(path) ) : "Base path didn't match. Should be '" + path + "' vs. '"+x.getAbsolutePath() + "'";

	}
	
	
	
	@Test
	public void validateServiceAdd() {
		// Clean Store
		store.deleteEverything();
		assert(store.getServices().size() == 0) : "Expected store to be empty (0) but got '" + store.getServices().size() +"'";
		for (Service service : getServiceList()) {
			store.saveOrUpdateService(service);
		}

		assert (store.getServices().size() == 2) : "Length should have been 2 but was "
				+ store.getServices().size();

	}

	@Test
	public void checkProxyAdditionMsg() {

		//
		// ***************************
		// Clean Store
		// ***************************
		store.deleteEverything();

		// ***************************
		// New Store
		// ***************************
		for (Service service : getServiceList()) {
			store.saveOrUpdateService(service);
		}
		assert (store.getServices().size() == 2) : "Length should have been 2 but was "
				+ store.getServices().size();

		// ***************************
		// Get the store as XML
		// ***************************
		String storeAsXml = getStoreAsXml();

		// ***************************
		// Upload the store again.
		// ***************************
		ServiceMergeResults mergeResults = getMergeResults(storeAsXml);

		// ******************************************************
		// There should be 1 Addition message e.g. 'Proxy set'
		// ******************************************************
		assert (mergeResults.getAdditionMessages().size() == 1) : "Length should have been 1 but was "
				+ mergeResults.getAdditionMessages().size()
				+ ". Addition messages were: \n"
				+ getPrettyPrint(mergeResults.getAdditionMessages());

	}

	@Test
	public void checkForMergedRealUrls() {

		//
		// ***************************
		// Clean Store
		// ***************************
		store.deleteEverything();

		// ***************************
		// New Store
		// ***************************
		Service service = new Service();
		service.setServiceName("Service 1");
		service.setTag("abc");
		service.saveOrUpdateRealServiceUrl(new Url("http://www.abc.com"));

		store.saveOrUpdateService(service);

		// ***************************
		// Get the store as XML
		// ***************************
		String storeAsXml = getStoreAsXml();

		// ***************************
		// Rebuild the store with:
		// - Same Service
		// - Different URL
		// ***************************
		store.deleteEverything();
		service = new Service();
		service.setServiceName("Service 1");
		service.setTag("def");

		service.saveOrUpdateRealServiceUrl(new Url("http://www.def.com"));

		store.saveOrUpdateService(service);

		// ***************************
		// Upload/Merge the store again.
		// Result should be 1 Service in the store with 2 Real URLS (merged)
		// ***************************
		getMergeResults(storeAsXml);

		// ******************************************************
		// There should be 1 Conflict messages e.g. 'Service not added because
		// of conflicting name'
		// ******************************************************
		List<Service> storeServices = store.getServices();
		assert (storeServices.size() == 1) : "Number of Services in the Store should have been 1 but was "
				+ storeServices.size();

		Service serviceToTest = storeServices.get(0);
		assert (serviceToTest.getRealServiceUrls().size() == 2) : "Number of Real URLS in the Service should have been 2 but was "
				+ serviceToTest.getRealServiceUrls().size();

	}

	@Test
	public void checkForMergedServiceTags() {

		//
		// ***************************
		// Clean Store
		// ***************************
		store.deleteEverything();

		// ***************************
		// New Store
		// ***************************
		Service service = new Service();
		service.setServiceName("Service 1");
		service.setTag("abc");
		store.saveOrUpdateService(service);

		// ***************************
		// Get the store as XML
		// ***************************
		String storeAsXml = getStoreAsXml();

		// ***************************
		// Rebuild the store with:
		// - Same Service
		// - Different URL
		// ***************************
		store.deleteEverything();
		service = new Service();
		service.setServiceName("Service 1");
		service.setTag("def");
		store.saveOrUpdateService(service);

		// ***************************
		// Upload/Merge the store again.
		// Result should be 1 Service in the store with 2 Real URLS (merged)
		// ***************************
		getMergeResults(storeAsXml);

		List<Service> storeServices = store.getServices();
		assert (storeServices.size() == 1) : "Number of Services in the Store should have been 1 but was "
				+ storeServices.size();
		Service serviceToTest = storeServices.get(0);
		assert (serviceToTest.getTagList().size() == 2) : "Number of Tags in the Service should have been size 2, with value 'abc def' but was size "
				+ serviceToTest.getTagList().size()
				+ " with value '"
				+ serviceToTest.getTag() + "'";

	}

	@Test
	public void checkForMergedServiceScenarioTags() {

		//
		// ***************************
		// Clean Store
		// ***************************
		store.deleteEverything();

		// ***************************
		// New Store
		// ***************************
		Service service = new Service();
		service.setServiceName("Service 1");
		Scenario scenario = new Scenario();
		scenario.setScenarioName("ABC");
		scenario.setTag("abc");
		service.saveOrUpdateScenario(scenario);
		store.saveOrUpdateService(service);

		// ***************************
		// Get the store as XML
		// ***************************
		String storeAsXml = getStoreAsXml();

		// ***************************
		// Rebuild the store with:
		// - Same Service, same scenario
		// - Service scenario has different tag
		// ***************************
		store.deleteEverything();
		service = new Service();
		service.setServiceName("Service 1");
		scenario = new Scenario();
		scenario.setScenarioName("ABC");
		scenario.setTag("def");
		service.saveOrUpdateScenario(scenario);
		store.saveOrUpdateService(service);

		// ***************************
		// Upload/Merge the store again.
		// Result should be 1 Service in the store with 2 Real URLS (merged)
		// ***************************
		getMergeResults(storeAsXml);

		List<Service> storeServices = store.getServices();
		assert (storeServices.size() == 1) : "Number of Services in the Store should have been 1 but was "
				+ storeServices.size();
		Service serviceToTest = storeServices.get(0);
		List<Scenario> scenarioList = serviceToTest.getScenarios();
		assert (scenarioList.size() == 1) : "Number of Service scenarios in the Store should have been 1 but was "
				+ scenarioList.size()
				+ " with value: \n"
				+ getScenarioListAsString(scenarioList);

		Scenario scenarioTest = scenarioList.get(0);
		assert (scenarioTest.getTagList().size() == 2) : "Number of Tags in the Service Scenario should have been size 2, with value 'abc def' but was size "
				+ scenarioTest.getTagList().size()
				+ " with value '"
				+ scenarioTest.getTag() + "'";

	}
	
	@Test
	public void checkForRelativePath() {
		File randomFile = new File(File.separator + "makebelieve" + File.separator + "somenewfile");
		File seedFile = new File(File.separator + "seed");
		MockeyXmlFileManager.getInstanceWithRepoPath(seedFile.getAbsolutePath());
		MockeyXmlFileManager mxfm = MockeyXmlFileManager.getInstance();
		Service s = new Service();
		s.setServiceName("AnAccountService");
		File serviceFile = MockeyXmlFileManager.getInstance().getServiceFile(s);
		assert (("mockey_def_depot" +File.separator +"anaccountservice"+File.separator+"anaccountservice.xml").equals(mxfm.getRelativePath(serviceFile))) : "Fail in catching invalid child file path. Expected '"
			+ "mockey_def_depo"+File.separator+"anaccountservice"+File.separator+"anaccountservice.xml' but got '"+mxfm.getRelativePath(serviceFile)+"'";
		assert ("ERROR".equals(mxfm.getRelativePath(randomFile))) : "Fail in catching BAD file.";
	}

	@Test
	public void checkForConflictingServiceName() {

		//
		// ***************************
		// Clean Store
		// ***************************
		store.deleteEverything();

		// ***************************
		// New Store
		// ***************************
		Service service = new Service();
		service.setServiceName("Service 1");
		store.saveOrUpdateService(service);

		// ***************************
		// Get the store as XML
		// ***************************
		String storeAsXml = getStoreAsXml();

		// ***************************
		// Upload the store again.
		// ***************************
		ServiceMergeResults mergeResults = getMergeResults(storeAsXml);

		// ******************************************************
		// There should be 1 Conflict messages e.g. 'Service not added because
		// of conflicting name'
		// ******************************************************
		assert (mergeResults.getConflictMsgs().size() == 1) : "Length should have been 1 but was "
				+ mergeResults.getConflictMsgs().size()
				+ ". Conflict messages were: \n"
				+ getPrettyPrint(mergeResults.getConflictMsgs());

	}

	// ************************************************************************************************************
	// HELPFUL UTILIITY METHODS BELOW
	// ************************************************************************************************************

	private String getStoreAsXml() {
		MockeyXmlFactory g = new MockeyXmlFactory();

		String storeAsXml = null;

		try {
			storeAsXml = g.getStoreAsString(store, true);
		} catch (IOException e) {

			e.printStackTrace();
		} catch (TransformerException e) {

			e.printStackTrace();
		}
		return storeAsXml;
	}

	private String getScenarioListAsString(List<Scenario> scenarioList) {
		StringBuffer sb = new StringBuffer();
		for (Scenario scenario : scenarioList) {
			sb.append(scenario.toString() + "\n");

		}
		return sb.toString();
	}

	private List<Service> getServiceList() {
		List<Service> serviceList = new ArrayList<Service>();

		Service service = new Service();
		service.setServiceName("Service 1");
		service.saveOrUpdateRealServiceUrl(new Url("http://www.abc.com"));
		service.saveOrUpdateRealServiceUrl(new Url("http://www.nbc.com"));
		serviceList.add(service);

		Service serviceB = new Service();
		serviceB.setServiceName("Service 22");
		serviceB.saveOrUpdateRealServiceUrl(new Url("http://www.abc.com"));
		serviceB.saveOrUpdateRealServiceUrl(new Url("http://www.nbc.com"));
		serviceList.add(serviceB);
		return serviceList;
	}

	private ServiceMergeResults getMergeResults(String storeAsXml) {
		MockeyXmlFileManager.getInstanceWithRepoPath("");
		MockeyXmlFileManager configurationReader = MockeyXmlFileManager.getInstance();
		ServiceMergeResults mergeResults = null;
		try {
			mergeResults = configurationReader.loadConfigurationWithXmlDef(
					storeAsXml, "");

		} catch (SAXParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mergeResults;
	}

	private String getPrettyPrint(List<String> g) {
		StringBuffer sb = new StringBuffer();
		for (String gg : g) {
			sb.append(gg);
			sb.append("\n");
		}
		return sb.toString();
	}
}
