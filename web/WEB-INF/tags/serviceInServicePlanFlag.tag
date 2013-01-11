<%@ tag import="java.util.*" %>
<%@ tag import="java.text.*" %>
<%@ tag import="com.mockey.model.*" %>
<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ attribute name="service" required="true" type="com.mockey.model.Service" %>
<%@ attribute name="servicePlan" required="true" type="com.mockey.model.ServicePlan" %>

<%

if(servicePlan!=null && servicePlan.hasServiceWithMatchingName(service.getServiceName())){
   
   %>
    checked
   <%
}
%>





