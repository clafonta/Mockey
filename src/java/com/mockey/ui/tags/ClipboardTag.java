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
package com.mockey.ui.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

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
