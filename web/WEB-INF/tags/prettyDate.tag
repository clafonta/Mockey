<%@ tag import="java.util.*" %>
<%@ tag import="java.text.*" %>
<%@ tag body-content="empty" %> 
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="lastVisit" required="true" %>
<%Calendar now = Calendar.getInstance();
SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
if(lastVisit!=null && lastVisit.trim().length() > 1 ){ Date time = new Date(new Long(lastVisit));%><%=formatter.format(time)%><%}
else {
	%> <p class="code_text help_optional">Never visited.</p><%
}
%>