/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mockey.ui.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

public class HistoryFilterTag extends TagSupport {

    private static final long serialVersionUID = -8902512566431524818L;
    private String value;

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    public int doStartTag() {
        try {
            value = (String) ExpressionEvaluatorManager.evaluate("value", value, String.class, pageContext);
            StringBuffer url = new StringBuffer();
            HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
            String contextpath = request.getContextPath();
            if (!contextpath.endsWith("/")) {
                contextpath = contextpath + "/";
            }
            url.append(contextpath + "history");
            url.append("?action=remove_token");
            url.append("&token=" + value);

            JspWriter out = pageContext.getOut();

            out.println(url.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Error("All is not well in the world.", ex);
        }
        // Must return SKIP_BODY because we are not supporting a body for this
        // tag.
        return SKIP_BODY;
    }

}
