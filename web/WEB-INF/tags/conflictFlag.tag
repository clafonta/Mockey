<%@ tag import="java.util.*" %>
<%@ tag import="java.text.*" %>
<%@ tag import="com.mockey.model.*" %>
<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ attribute name="service" required="true" type="com.mockey.model.Service" %>
<%@ attribute name="conflictInfo" required="true" type="com.mockey.model.ConflictInfo" %>

<%

if(conflictInfo.hasConflictFlag(service)){
   
   %>
   <p class="tiny" style="margin-top:0.8em;">
    <i aria-hidden="true" class="icon-info"></i> <a href="#" id="conflict-link_${service.id}" style="color:red;" class="tiny toggle-conflict-link" onclick="return false;">Conflict</a>
   
   <div class="tooltip alert_message" id="conflict-info_${service.id}">
   
   <%
   for(ConflictInfo.Conflict conflict : conflictInfo.getConflictList(service)){
   		
   		%>
   		<c:url value="/setup" var="setupUrl">
		  <c:param name="serviceId" value="${service.id}" />
		</c:url>
   		Service '<a href="${setupUrl}"><%= conflict.getService().getServiceName() %>'</a>
   		<ul>
   		<%
   		for(String issue : conflict.getConflictMessageList()){
   		  %><li class="tiny"><%= issue %></li><%
   		}
   		%></ul><%
   }
   %>
   </div>
   </p>
   
   <%
}
%>





