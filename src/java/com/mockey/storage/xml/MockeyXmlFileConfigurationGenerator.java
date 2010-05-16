package com.mockey.storage.xml;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mockey.model.PlanItem;
import com.mockey.model.ProxyServerModel;
import com.mockey.model.Scenario;
import com.mockey.model.Service;
import com.mockey.model.ServicePlan;
import com.mockey.model.Url;
import com.mockey.storage.IMockeyStorage;

/**
 * Builds DOM representing Mockey Service configurations. 
 * @author chad.lafontaine
 *
 */
public class MockeyXmlFileConfigurationGenerator extends XmlGeneratorSupport {
	/** Basic logger */
	//private static Logger logger = Logger.getLogger(MockeyXmlFileConfigurationGenerator.class);

	/**
	 * Returns an element representing a mock service definitions file in XML. 
	 * 
	 * @param document
	 *            parent DOM object of this element.
	 * @param cxmlObject
	 *            value object used to build element.
	 * @return Returns an element representing a cXML root element; if request
	 *         <code>null</code>, then empty element is returned e.g.
	 *         &lt;cXML/&gt;
	 */
	@SuppressWarnings("unchecked")
	public Element getElement(Document document, IMockeyStorage store) {
		
		Element rootElement = document.createElement("mockservice");
		Scenario mssb = store.getUniversalErrorScenario();		
		this.setAttribute(rootElement, "xml:lang", "en-US");
		this.setAttribute(rootElement, "version", "1.0");
		// Universal Service settings
		if(mssb!=null){
            this.setAttribute(rootElement,"universal_error_service_id", ""+mssb.getServiceId());
            this.setAttribute(rootElement,"universal_error_scenario_id", ""+mssb.getId());
        }
		// Proxy settings
		ProxyServerModel psm = store.getProxy();
		if(psm!=null){
		    Element proxyElement = document.createElement("proxy_settings");
		    proxyElement.setAttribute("proxy_url", psm.getProxyUrl());
		    proxyElement.setAttribute("proxy_enabled", ""+psm.isProxyEnabled());
		    rootElement.appendChild(proxyElement);
		}

		Iterator iterator = store.getServices().iterator();
		//logger.debug("building DOM:");
		while (iterator.hasNext()) {
			Service mockServiceBean = (Service) iterator.next();
			Element serviceElement = document.createElement("service");
			rootElement.appendChild(serviceElement);

			if (mockServiceBean != null) {
				//logger.debug("building XML representation for MockServiceBean:\n" + mockServiceBean.toString());
				// *************************************
				// We do NOT want to write out ID.
				// If we did, then someone uploading this xml definition may overwrite services
				// defined with the same ID.
				// serviceElement.setAttribute("id", mockServiceBean.getId());
				// *************************************
				serviceElement.setAttribute("name", mockServiceBean.getServiceName());
				serviceElement.setAttribute("description", mockServiceBean.getDescription());
				
				// DEPRECATED. 
				// 'proxyurl' was used when Service only supported 1 real url. 
				//serviceElement.setAttribute("proxyurl", mockServiceBean.getRealServiceUrl());
				serviceElement.setAttribute("hang_time", "" + mockServiceBean.getHangTime());
				serviceElement.setAttribute("http_content_type", "" + mockServiceBean.getHttpContentType());
				serviceElement.setAttribute("default_scenario_id", "" + (mockServiceBean.getDefaultScenarioId()!=null ?  mockServiceBean.getDefaultScenarioId(): ""));
				serviceElement.setAttribute("service_response_type", "" + mockServiceBean.getServiceResponseType());

				// New real service URLs
				for(Url realUrl: mockServiceBean.getRealServiceUrls()){
					Element urlElement = document.createElement("real_url");
					urlElement.setAttribute("url", realUrl.getFullUrl());
					//urlElement.appendChild(cdataResponseElement);
					serviceElement.appendChild(urlElement);
				}
				
				// Scenarios
				List scenarios = mockServiceBean.getScenarios();
				Iterator iter = scenarios.iterator();

				while (iter.hasNext()) {
					Scenario scenario = (Scenario) iter.next();
					//logger.debug("building XML representation for MockServiceScenarioBean:\n" + scenario.toString());
					Element scenarioElement = document.createElement("scenario");
					scenarioElement.setAttribute("id", scenario.getId().toString());
					scenarioElement.setAttribute("name", scenario.getScenarioName());

					Element scenarioMatchStringElement = document.createElement("scenario_match");
					CDATASection cdataMatchElement = document.createCDATASection(scenario.getMatchStringArg());
					scenarioMatchStringElement.appendChild(cdataMatchElement);
					scenarioElement.appendChild(scenarioMatchStringElement);
					
					Element scenarioResponseElement = document.createElement("scenario_response");
					CDATASection cdataResponseElement = document.createCDATASection(scenario.getResponseMessage());
					scenarioResponseElement.appendChild(cdataResponseElement);
					scenarioElement.appendChild(scenarioResponseElement);
					serviceElement.appendChild(scenarioElement);
				}
			}
		}
		
		// SERVICE PLANS
		
		List servicePlans = store.getServicePlans();
		if(servicePlans!=null){
			Iterator iter = servicePlans.iterator();
			while(iter.hasNext()){
				ServicePlan servicePlan = (ServicePlan)iter.next();
				Element servicePlanElement = document.createElement("service_plan");
				servicePlanElement.setAttribute("name", servicePlan.getName());
				servicePlanElement.setAttribute("description", servicePlan.getDescription());
				servicePlanElement.setAttribute("id", ""+servicePlan.getId());
				
				Iterator planItemIter = servicePlan.getPlanItemList().iterator();
				while(planItemIter.hasNext()){
					PlanItem  pi = (PlanItem)planItemIter.next();
					Element planItemElement = document.createElement("plan_item");
					planItemElement.setAttribute("hang_time", ""+pi.getHangTime());
					planItemElement.setAttribute("service_id", ""+pi.getServiceId());
					planItemElement.setAttribute("scenario_id", ""+pi.getScenarioId());
					planItemElement.setAttribute("service_response_type", ""+pi.getServiceResponseType());
					
					servicePlanElement.appendChild(planItemElement);
				}
				
				rootElement.appendChild(servicePlanElement);
				
			}
		}

		return rootElement;
	}
}
