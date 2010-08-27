package com.mockey.storage;

import java.util.List;

import com.mockey.OrderedMap;
import com.mockey.model.ApiDocService;
import com.mockey.model.PersistableItem;

public class IApiStorageInMemory implements IApiStorage {

	private OrderedMap<ApiDocService> apiStore = new OrderedMap<ApiDocService>();
	private static IApiStorageInMemory store = new IApiStorageInMemory();

	/**
	 * 
	 * @return
	 */
	public static IApiStorageInMemory getInstance() {
		return store;
	}


	@Override
	public ApiDocService getApiDocServiceById(Long serviceId) {
		return apiStore.get(serviceId);
	}

	@Override
	public List<ApiDocService> getApiDocServices() {
		return this.apiStore.getOrderedList();

	}

	@Override
	public ApiDocService saveOrUpdateService(ApiDocService apiDocService) {
		PersistableItem item = apiStore.save(apiDocService);
		return (ApiDocService) item;
	}


	@Override
	public ApiDocService getApiDocServiceByName(String name) {
		ApiDocService service = null;
		for(ApiDocService s : getApiDocServices()){
			if(name!=null && name.trim().equalsIgnoreCase(s.getName()) ){
				service = s;
				break;
			}
			
		}
		
		return service;
	}

}
