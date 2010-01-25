package com.mockey.ui.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Slug a long txt for JSP display.
 * @author chad.lafontaine
 */
public class SlugTag extends TagSupport {

    /**
     * 
     */
    private static final long serialVersionUID = 702927192030153426L;
    private int maxLength = 80;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int doStartTag() throws JspException {

        JspWriter out = pageContext.getOut();
        try {
            String slugTxt = this.text;
            if (slugTxt != null && slugTxt.length() > maxLength) {
                slugTxt = slugTxt.substring(0, maxLength - 1);
                slugTxt = slugTxt + "...";
            }
            out.print(slugTxt);
        } catch (IOException e) {
            throw new JspException("Unable to write to the jsp output", e);
        }
        return SKIP_BODY;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }
}
