<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ attribute name="lastVisit" required="true"%>
<%
java.util.Calendar now = java.util.Calendar.getInstance();
java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd ', ' hh:mm:ss a zzz");
if(lastVisit!=null && lastVisit.trim().length() > 0){

java.util.Date time = new java.util.Date(new Long(lastVisit));
%><%=formatter.format(time)%><%
}
%>





