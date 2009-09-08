package com.mockey.storage;

import com.mockey.model.Scenario;
import com.mockey.model.Service;

class MockeyStorageWriter {
	
	String StorageAsString(IMockeyStorage storage) {
        StringBuffer sb = new StringBuffer();
        sb.append(storage.toString());
        for (Service service : storage.getServices() ) {
            sb.append("Service ID: ").append(service.getId()).append("\n");
            sb.append("Service name: ").append(service.getServiceName()).append("\n");
            sb.append("Service description: ").append(service.getDescription()).append("\n");
            sb.append("Service url: ").append(service.getMockServiceUrl()).append("\n");
            sb.append("Service proxyurl: ").append(service.getUrl().getPath()).append("\n");
            for (Scenario scenario : service.getScenarios()) {
                sb.append("    scenario name: ").append(scenario.getScenarioName()).append("\n");
                sb.append("    scenario request: ").append(scenario.getRequestMessage()).append("\n");
                sb.append("    scenario response: ").append(scenario.getResponseMessage()).append("\n");
            }
        }
        return sb.toString();
	}
}
