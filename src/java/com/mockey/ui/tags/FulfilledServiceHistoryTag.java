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

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public int doStartTag() throws JspException {

		JspWriter out = pageContext.getOut();
		try {
			String text = "";
			String serviceIdentifier = "";
			if(this.serviceId!=null){
				serviceIdentifier = "id=\"" +"serviceresponsetype_" + this.serviceId.toString() +"\"";
			}
			switch (type) {
			case 0:
				text = "<a class=\"response_proxy\" "+serviceIdentifier+" onclick=\"return true;\" href=\"#\" title=\"Proxy response\">P</a>";
				break;
			case 1:
				text = "<a class=\"response_static\" "+serviceIdentifier+" onclick=\"return true;\" href=\"#\" title=\"Static response\">S</a>";
				break;
			case 2:
				text = "<a class=\"response_dynamic\" "+serviceIdentifier+" onclick=\"return true;\" href=\"#\" title=\"Dynamic response\">D</a>";
				break;
			default:
				text = "<a class=\"response_unknown\" "+serviceIdentifier+" onclick=\"return true;\" href=\"#\" title=\"Undefined type response\">(?)</a>";
				break;
			}
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
