package com.mockey.web.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

/**
 * 
 * @author chad.lafontaine
 */
public class ClipboardTag extends TagSupport {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6987731112461682834L;
	private String id;
    private String text;
    private String bgcolor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int doStartTag() throws JspException {
    	
    	HttpServletRequest request = (HttpServletRequest) pageContext
		.getRequest();

    	String contxtPth = request.getContextPath();
    	if(contxtPth!=null && contxtPth.trim().equals("/")){
    		contxtPth = ""; // App has root for context
    	}

        JspWriter out = pageContext.getOut();
        try {
            out.print("<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" width=\"110\" height=\"14\" id=\""+id+"\" >\n" +
"        <param name=\"movie\" value=\"/flash/clippy.swf\"/>\n" +
"        <param name=\"allowScriptAccess\" value=\"always\" />\n" +
"        <param name=\"quality\" value=\"high\" />\n" +
"        <param name=\"scale\" value=\"noscale\" />\n" +
"        <param name=\"BGCOLOR\" value=\""+ this.getBgcolor() +"\" />\n" +
"        <param NAME=\"FlashVars\" value=\"text="+text+"\">\n" +
"        <embed src=\""+contxtPth+"/flash/clippy.swf\"\n" +
"               width=\"110\"\n" +
"               height=\"14\"\n" +
"               name=\"clippy\"\n" +
"               quality=\"high\"\n" +
"               allowScriptAccess=\"always\"\n" +
"               bgcolor=\""+ this.getBgcolor() +"\"\n" +
"               type=\"application/x-shockwave-flash\"\n" +               
"               FlashVars=\"text="+text+"\"               \n" +
"        />\n" +
"        </object>");
        } catch (IOException e) {
            throw new JspException("Unable to write to the jsp output",e);
        }
        return SKIP_BODY;
    }

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	/**
	 * 
	 * @return custom bgcolor, but if null, returns default color #FFFFFF. 
	 */
	public String getBgcolor() {
		if(this.bgcolor == null){
			return "#FFFFFF";
		}else{
			return this.bgcolor;
		}
		
	}
}
