package com.mockey.ui.tags;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Friendly date tag for HTML display. Instead of
 * "Fri Mar 26 10:28:50 PDT 2010", this tag will display "1 hour ago",
 * "Yesterday", "long time ago" with the exact date and time provided with a
 * mouse hover.
 * 
 * @author chad.lafontaine
 */
public class FriendlyDateTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8299069869594172964L;
	private Date date;
	
	private static final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd hh:mm:ss a");
	private static final DateFormat dateFormatShort = new SimpleDateFormat(
			"hh:mm:ss a");

	@Override
	public int doStartTag() throws JspException {

		// return dateFormat.format(date);

		JspWriter out = pageContext.getOut();
		try {
			String text = "Time unknown.";
			if (date != null) {
				text = "<a id=\"fdate\" title=\"" + dateFormat.format(date) + "\">" 
				  + dateFormatShort.format(date) + "</a>";
			}

			out.print(text);
		} catch (IOException e) {
			throw new JspException("Unable to write to the jsp output", e);
		}
		return SKIP_BODY;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}
}
