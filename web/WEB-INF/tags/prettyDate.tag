<%@ tag import="java.util.*" %>
<%@ tag import="java.text.*" %>
<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ attribute name="lastVisit" required="true"%>
<%
Calendar now = Calendar.getInstance();
SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
if(lastVisit!=null && lastVisit.trim().length() > 1 ){
Date time = new Date(new Long(lastVisit));
%><%=formatter.format(time)%><%
}
%>