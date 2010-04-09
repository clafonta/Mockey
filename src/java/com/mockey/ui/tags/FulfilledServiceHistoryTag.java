package com.mockey.ui.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Prints out an HTML span with ID of type of service performed. Proxy, Static,
 * or Dynamic.
 * 
 * @author chad.lafontaine
 */
public class FulfilledServiceHistoryTag extends TagSupport {

	/**
     * 
     */
	private static final long serialVersionUID = 702927192030153426L;
	private int type = -1;
	private Long serviceId = null;
	private static final String CSS_CLASS_RESPONSE_PROXY = "response_proxy";
	private static final String CSS_CLASS_RESPONSE_STATIC = "response_static";
	private static final String CSS_CLASS_RESPONSE_DYNAMIC = "response_dynamic";
	private static final String CSS_CLASS_RESPONSE_NOT = "response_not";
	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public int doStartTag() throws JspException {

		/*
		 * The output we want is:
		 * <pre>
		 * P S D
		 * </pre>
		 * Each letter is a link with HREF
		 * The  
		 */
		JspWriter out = pageContext.getOut();
		try {
			String text = "";
			String proxyClass = CSS_CLASS_RESPONSE_NOT;
			String staticClass = CSS_CLASS_RESPONSE_NOT;
			String dynamicClass = CSS_CLASS_RESPONSE_NOT;
			String serviceIdentifier = "";
			if(this.serviceId!=null){
				serviceIdentifier = this.serviceId.toString();
			}
			switch (type) {
			case 0:
				proxyClass = CSS_CLASS_RESPONSE_PROXY;
				break;
			case 1:
				staticClass = CSS_CLASS_RESPONSE_STATIC;
				break;
			case 2:
				dynamicClass = CSS_CLASS_RESPONSE_DYNAMIC;
				break;
			default:
				text = "";
				break;
			}
			
			text = "<a class=\"serviceResponseTypeProxyLink "+proxyClass+"\" id=\"serviceResponseTypeProxy_"+serviceIdentifier+"\" onclick=\"return false;\" href=\"#\" title=\"Proxy response\">P</a>"
			   +  " <a class=\"serviceResponseTypeStaticLink "+staticClass+"\" id=\"serviceResponseTypeStatic_"+serviceIdentifier+"\" onclick=\"return false;\" href=\"#\" title=\"Static response\">S</a>"
			   +  " <a class=\"serviceResponseTypeDynamicLink "+dynamicClass+"\" id=\"serviceResponseTypeDynamic_"+serviceIdentifier+"\" onclick=\"return false;\" href=\"#\" title=\"Dynamic response\">D</a>";
			
			/*
			 <a class="serviceResponseTypeProxyLink" id="serviceResponseTypeProxy_${status.count}" onclick="return false;" href="#" title="Proxy response">P</a>
	         <a class="serviceResponseTypeStaticLink" id="serviceResponseTypeStatic_${status.count}" onclick="return false;" href="#" title="Static response">S</a>    
	         <a class="serviceResponseTypeDynamicLink" id="serviceResponseTypeDynamic_${status.count}" onclick="return false;" href="#" title="Dynamic response">D</a>      
	                                
			 */
			
			
			out.print(text);
		} catch (IOException e) {
			throw new JspException("Unable to write to the jsp output", e);
		}
		return SKIP_BODY;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	
}
