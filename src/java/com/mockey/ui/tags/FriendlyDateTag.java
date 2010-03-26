package com.mockey.ui.tags;

import java.io.IOException;
import java.util.Date;

import javax.management.timer.Timer;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Friendly date tag for HTML display. Instead of "Fri Mar 26 10:28:50 PDT 2010", this
 * tag will display "1 hour ago", "Yesterday", "long time ago" with the exact date and time
 * provided with a mouse hover.
 *  
 * @author chad.lafontaine
 */
public class FriendlyDateTag extends TagSupport {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8299069869594172964L;
	private Date date;
	private Date NOW = new Date();
    private Date ONEHOURAGO = new Date(NOW.getTime() + Timer.ONE_HOUR);
    private Date TWOHOURSAGO = new Date(NOW.getTime() + (Timer.ONE_HOUR * 2));
    private Date ONEDAYAGO = new Date(NOW.getTime() + (Timer.ONE_HOUR * 24));
    private Date AFEWDAYSAGO = new Date(NOW.getTime() + (Timer.ONE_HOUR * 48));
    

    @Override
    public int doStartTag() throws JspException {

        JspWriter out = pageContext.getOut();
        try {
        	String text = "Time unknown.";
        	if(date!=null){
        		text = "<a id=\"fdate\" title=\""+ date.toString() +"\">";
            	long theTimeThisOccured = date.getTime();
            
        		if(this.AFEWDAYSAGO.getTime() < theTimeThisOccured){
        			text = text + "Several days ago";
        		}else if((this.ONEDAYAGO.getTime() < theTimeThisOccured)
        				&& theTimeThisOccured < this.AFEWDAYSAGO.getTime()){
        			text = text + "About 1 day ago";
        		}else if((this.ONEDAYAGO.getTime() > theTimeThisOccured)
        				&& theTimeThisOccured > this.TWOHOURSAGO.getTime()){
        			text = text + "A few hours ago";
        		}else if((this.TWOHOURSAGO.getTime() > theTimeThisOccured)
        				&& ( this.ONEHOURAGO.getTime() < theTimeThisOccured)){
        			text = text + "About 1 hour ago";
        		}else {
        			text = text + "Recently";
        		}
        		text = text + "</a>";
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
