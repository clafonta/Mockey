package com.mockey.ui;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.mockey.model.ProxyServerModel;
import com.mockey.storage.IMockeyStorage;
import com.mockey.storage.StorageRegistry;

public class ProxyInfoFilter implements Filter 
{
	private static IMockeyStorage store = StorageRegistry.MockeyStorage;

    public void doFilter(ServletRequest request, 
                         ServletResponse response,
                         FilterChain chain)
        throws IOException, ServletException 
    {

    	ProxyServerModel proxyInfo = store.getProxy();
		request.setAttribute("proxyInfo", proxyInfo);
		chain.doFilter(request, response);
    }

	@Override
	public void destroy() {
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
}
