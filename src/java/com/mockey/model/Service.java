package com.mockey.model;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;

import com.mockey.ClientExecuteProxy;
import com.mockey.OrderedMap;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

/**
 * A Service is a remote url that can be called.
 * 
 * @author chad.lafontaine
 * 
 */
public class Service implements PersistableItem, ExecutableService {

	public final static int SERVICE_RESPONSE_TYPE_PROXY = 0;
	public final static int SERVICE_RESPONSE_TYPE_STATIC_SCENARIO = 1;
	public final static int SERVICE_RESPONSE_TYPE_DYNAMIC_SCENARIO = 2;

	private Long id;
	private String serviceName;
	private String description;
	private Long defaultScenarioId;
	private Long errorScenarioId;
	private String httpContentType = "text/html;charset=utf-8";
	private int hangTime = 0;
	private OrderedMap<Scenario> scenarios = new OrderedMap<Scenario>();
	private int serviceResponseType = SERVICE_RESPONSE_TYPE_PROXY;
	private String httpMethod = "GET";
	private List<FulfilledClientRequest> fulfilledRequests;
	private List<Url> realServiceUrls = new ArrayList<Url>();

	private static Log logger = LogFactory.getLog(Service.class);
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

	public List<FulfilledClientRequest> getFulfilledRequests() {
		return fulfilledRequests;
	}

	public void setFulfilledRequests(List<FulfilledClientRequest> transactions) {
		this.fulfilledRequests = transactions;
	}

	// default constructor for xml.
	// DO NOT REMOVE. DO NOT CALL.
	public Service() {
		this.setServiceName("");
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public Long getDefaultScenarioId() {
		return defaultScenarioId;
	}

	public void setDefaultScenarioId(Long defaultScenarioId) {
		this.defaultScenarioId = defaultScenarioId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String name) {

		this.serviceName = name;

	}

	public int getHangTime() {
		return hangTime;
	}

	public void setHangTime(int hangTime) {
		this.hangTime = hangTime;
	}

	public List<Scenario> getScenarios() {
		return scenarios.getOrderedList();
	}

	public Scenario getScenario(Long scenarioId) {
		return (Scenario) scenarios.get(scenarioId);
	}

	public void deleteScenario(Long scenarioId) {
		this.scenarios.remove(scenarioId);
	}

	public Scenario saveOrUpdateScenario(Scenario scenario) {
		scenario.setServiceId(this.id);
		return (Scenario) this.scenarios.save(scenario);
	}


	/**
	 * DO NOT REMOVE. This is needed by XML reader and has a reference to the
	 * method signature via reflection. Thank Digester.
	 * 
	 * @param realServiceUrl
	 * @deprecated - this method will call
	 *             <code>saveOrUpdateRealServiceUrl(Url)</code>
	 * @see #saveOrUpdateRealServiceUrl(Url)
	 */
	public void setRealServiceUrlByString(String realServiceUrl) {
		this.saveOrUpdateRealServiceUrl(new Url(realServiceUrl));
	}

	public String getHttpContentType() {
		return httpContentType;
	}

	public void setHttpContentType(String httpContentType) {
		this.httpContentType = httpContentType;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Service name:").append(this.getServiceName()).append("\n");
		sb.append("Real URL(s):\n"); 
		if (this.realServiceUrls != null && !this.realServiceUrls.isEmpty()) {
			Iterator<Url> iter = this.realServiceUrls.iterator();
			while (iter.hasNext()) {
				sb.append(iter.next() + "\n");
			}
		} else {
			sb.append("(no real urls defined for this service)\n");
		}

		sb.append("Default scenario ID:").append(this.getDefaultScenarioId())
				.append("\n");
		sb.append("HTTP Content:").append(this.getHttpContentType()).append(
				"\n");
		sb.append("Hang time:");
		sb.append(this.getHangTime());
		sb.append("\n");

		return sb.toString();
	}

	public void setId(Long id) {
		this.id = id;

		// Recursively set this ID to child Scenarios, if any exist.
		for (Scenario scenario : getScenarios()) {
			scenario.setServiceId(this.id);
			this.saveOrUpdateScenario(scenario);
		}
	}

	public Long getId() {
		return id;
	}

	/**
	 * 
	 * @deprecated
	 * @see #getRealServiceUrls()
	 */
	public String getRealServiceUrl() {
		return "[DEPRECATED]";

	}

	/**
	 * @deprecated - create on httphost per real service url.
	 * @return - may return null
	 */
//	public HttpHost getHttpHost() {
//		Url realServiceUrl = this.getUrl();
//		if (realServiceUrl != null) {
//			return new HttpHost(realServiceUrl.getHost(), realServiceUrl
//					.getPort(), realServiceUrl.getScheme());
//		} else {
//			return null;
//		}
//	}

	public void setServiceResponseType(int serviceResponseType) {
		this.serviceResponseType = serviceResponseType;
	}

	public int getServiceResponseType() {
		// If no scenarios, then proxy is automatically on.
		if (this.getScenarios().size() == 0) {
			return SERVICE_RESPONSE_TYPE_PROXY;
		} else {
			return serviceResponseType;
		}
	}

	public void setErrorScenarioId(Long errorScenarioId) {
		this.errorScenarioId = errorScenarioId;
	}

	public Long getErrorScenarioId() {
		return errorScenarioId;
	}

	public Scenario getErrorScenario() {
		// FIND SERVICE ERROR, IF EXIST.
		for (Scenario scenario : this.getScenarios()) {
			if (scenario.getId() == this.getErrorScenarioId()) {
				return scenario;
			}
		}
		// No service error defined, therefore, let's use the universal
		// error.
		return StorageRegistry.MockeyStorage.getUniversalErrorScenario();
	}

	public Boolean isReferencedInAServicePlan() {
		Boolean isReferenced = false;
		for (ServicePlan plan : StorageRegistry.MockeyStorage.getServicePlans()) {
			for (PlanItem planItem : plan.getPlanItemList()) {
				if (planItem.getServiceId().equals(this.getId())) {
					isReferenced = true;
					break;
				}
			}
		}
		return isReferenced;
	}

	/**
	 * The core method to execute the request as either a Proxy, Dynamic, or
	 * Static Scenario.
	 */
	public ResponseFromService execute(RequestFromClient request, Url realServiceUrl, String methodType) {
		ResponseFromService response = null;
		if (this.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_PROXY) {
			response = proxyTheRequest(request,realServiceUrl, methodType);
		} else if (this.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_DYNAMIC_SCENARIO) {
			response = executeDynamicScenario(request);
		} else if (this.getServiceResponseType() == Service.SERVICE_RESPONSE_TYPE_STATIC_SCENARIO) {
			response = executeStaticScenario();
		}
		return response;
	}

	private ResponseFromService proxyTheRequest(RequestFromClient request, Url realServiceUrl, String methodType) {

		logger.debug("proxying a moxie.");
		// If proxy on, then
		// 1) Capture request message.
		// 2) Set up a connection to the real service URL
		// 3) Forward the request message to the real service URL
		// 4) Read the reply from the real service URL.
		// 5) Save request + response as a historical scenario.

		// There are 2 proxy things going on here:
		// 1. Using Mockey as a 'proxy' to a real service.
		// 2. The proxy server between Mockey and the real service.
		//
		// For the proxy server between Mockey and the real service,
		// we do the following:
		ProxyServerModel proxyServer = store.getProxy();
		ClientExecuteProxy clientExecuteProxy = new ClientExecuteProxy();
		ResponseFromService response = null;
		try {
			logger.debug("Initiating request through proxy");
			response = clientExecuteProxy.execute(proxyServer, realServiceUrl, methodType, request);
		} catch (Exception e) {
			// We're here for various reasons.
			// 1) timeout from calling real service.
			// 2) unable to parse real response.
			// 3) magic!
			// Before we throw an exception, check:
			// (A) does this mock service have a default error response. If
			// no, then
			// (B) see if Mockey has a universal error response
			// If neither, then throw the exception.
			response = new ResponseFromService();

			Scenario error = this.getErrorScenario();
			if (error != null) {
				response.setBody(error.getResponseMessage());
			} else {
				String maybe = "";
				if (e instanceof UnknownHostException) {
					maybe = "\n<span class=\"info_message\">Looks like your proxy settings may be invalid. We can't find the system you need to talk to. </span>\n\n";
				}
				response
						.setBody("Yikes! We encountered this error (let's hope it's informative). "
								+ maybe + e.getClass() + ": " + e.getMessage());
			}
		}
		return response;
	}

	private ResponseFromService executeStaticScenario() {

		logger.debug("mockeying a static scenario");

		// Proxy is NOT on. Therefore we use a scenario to figure out a reply.
		// Either:
		// 1) Based on matching the request message to one of the scenarios
		// or
		// 2) Based on scenario selected.
		//
		Scenario scenario = this.getScenario(this.getDefaultScenarioId());
		ResponseFromService response = new ResponseFromService();

		if (scenario != null) {
			response.setBody(scenario.getResponseMessage());
		} else {
			response.setBody("NO SCENARIO SELECTED");
		}
		return response;
	}

	private ResponseFromService executeDynamicScenario(RequestFromClient request) {

		logger.debug("mockeying a dynamic scenario.");
		String rawRequestData = "";
		try {
			rawRequestData = new String();
			if (!request.hasPostBody()) {
				// OK..let's build the request message from Params.
				// Is this a HACK? I dunno yet.
				logger
						.debug("Request message is EMPTY; building request message out of Parameters. ");
				rawRequestData = request.buildParameterRequest();
			} else {
				rawRequestData = request.getBodyInfo();
			}
		} catch (UnsupportedEncodingException e) {
			// uhm.
		}

		ResponseFromService response = new ResponseFromService();
		List<Scenario> scenarios = this.getScenarios();
		Iterator<Scenario> iter = scenarios.iterator();
		String messageMatchFound = null;
		while (iter.hasNext()) {
			Scenario scenario = iter.next();
			logger.debug("Checking: '" + scenario.getMatchStringArg()
					+ "' in Scenario message: \n" + rawRequestData);
			int indexValue = -1;
			if (scenario.hasMatchArgument()) {
				if (request.hasPostBody()) {
					indexValue = request.getBodyInfo().indexOf(
							scenario.getMatchStringArg());
				} else {
					indexValue = rawRequestData.indexOf(scenario
							.getMatchStringArg());
				}
			}
			if ((indexValue > -1)) {
				logger.debug("FOUND - matching '"
						+ scenario.getMatchStringArg() + "' ");
				messageMatchFound = scenario.getResponseMessage();
				break;
			}
		}
		// OK, no matches. Error handling is as follows:
		// 1) Does service have a default service error defined? If yes, return
		// message. If no...
		// 2) Does Mockey have a universal error message defined? If yes,
		// return, otherwise...
		// 3) Return a error message.
		if (messageMatchFound == null) {
			Scenario u = getErrorScenario();
			if (u == null) {
				u = store.getUniversalErrorScenario();
			}
			if (u != null) {
				messageMatchFound = u.getResponseMessage();
			} else {
				messageMatchFound = "Ouch, no love for you! Why? Well, it could be that this service setting "
						+ "is set to Dynamic but there is no found matching scenario, nor is there a default "
						+ "service-scenario-error defined, nor is there a universal-scenario-error defined "
						+ "for this incoming request. In otherwords, Mockey doesn't know what to do.";
			}

		}
		response.setBody(messageMatchFound);
		return response;
	}

	private String getNiceNameForService(String arg) {
		String name = arg;
		// Remove parameters
		int index = arg.indexOf("?");
		if (index > 0) {
			arg = arg.substring(0, index);
		}
		StringTokenizer st = new StringTokenizer(arg, "/");
		while (st.hasMoreTokens()) {
			// Eventually, we get the last token, and
			// we use it as the name.
			name = st.nextToken();
		}
		name = name + " (auto generated)";

		return name;
	}

	public List<Url> getRealServiceUrls() {
		return realServiceUrls;
	}

	public void setRealServiceUrls(List<Url> realServiceUrls) {
		this.realServiceUrls = realServiceUrls;
	}

	/**
	 * 
	 * @param url
	 */
	public void saveOrUpdateRealServiceUrl(Url url) {

		if (url != null) {

			boolean found = false;
			for (int i = 0; i < this.realServiceUrls.size(); i++) {
				Url tmpUrl = this.realServiceUrls.get(i);
				if (tmpUrl.getFullUrl().equalsIgnoreCase(url.getFullUrl())) {
					this.realServiceUrls.remove(i);
					this.realServiceUrls.add(i, url);
					found = true;
					break;
				}
			}
			if (!found && !url.getFullUrl().trim().isEmpty()) {
				this.realServiceUrls.add(url);
			}

			// BONUS
			// If this service name is undefined, then we try to determine
			// an informative name based on the url
			if (this.serviceName != null && this.serviceName.trim().isEmpty()) {
				this.setServiceName(this
						.getNiceNameForService(url.getFullUrl()));
			}

		}
	}

	/**
	 * 
	 * @param otherService
	 * @return non null if _this_ and otherService both have non-empty list of
	 *         <code>Url</code> objects with a matching <code>Url</code> object.
	 *         Otherwise, returns false;
	 */
	public Url getFirstMatchingRealServiceUrl(Service otherService) {

		Url matchUrl = null;
		if (this.realServiceUrls != null && otherService != null
				&& !otherService.getRealServiceUrls().isEmpty()) {

			for (Url otherUrl : otherService.getRealServiceUrls()) {
				if (this.hasRealServiceUrl(otherUrl)) {
					matchUrl = otherUrl;
					break;
				}
			}
		}
		return matchUrl;
	}

	public boolean hasRealServiceUrl(Url url) {
		boolean has = false;
		try {
			for (Url urlTmp : this.realServiceUrls) {
				if (urlTmp.getFullUrl().equalsIgnoreCase(url.getFullUrl())) {
					has = true;
					break;
				}
			}
		} catch (Exception e) {
			// do nothing
		}
		return has;
	}

	public static void main(String[] args) {

		Service a = new Service();
		Service b = new Service();
		Url aUrl = new Url("http://www.google.com");
		Url cUrl = new Url("http://www.cnn.com");
		Url bUrl = new Url("http://www.cnn.com");
		a.saveOrUpdateRealServiceUrl(aUrl);
		a.saveOrUpdateRealServiceUrl(cUrl);
		b.saveOrUpdateRealServiceUrl(bUrl);
		System.out.print("Answer: " + a.getFirstMatchingRealServiceUrl(b));

	}
}
