<%@ tag import="java.util.*" %>
<%@ tag import="java.net.*" %>
<%@ tag import="com.mockey.storage.*" %>
<%@ tag import="com.mockey.model.*" %>
<%@ tag import="com.mockey.ui.*" %>
<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="servicePath" required="true"%>
<%@ attribute name="serviceName" required="true"%>

<%
IMockeyStorage store = StorageRegistry.MockeyStorage;
List<ServicePlan> servicePlans = store.getServicePlans();
request.setAttribute("servicePlans",servicePlans);
URL serverURLObj = new URL(request.getScheme(), // http
		request.getServerName(), // host
		request.getServerPort(), // port
        "");
request.setAttribute("fullUrl",serverURLObj.toString());
request.setAttribute("api_setplan_service_name",ServicePlanSetupServlet.API_SETPLAN_SERVICE_NAME);

%>
<c:if test="${serviceName eq api_setplan_service_name}">
<c:choose>
  <c:when test="${empty servicePlans}">
  <div class="info_message">
  <strong>No Service Plans defined</strong>, so there's nothing you can do with this configuration API - <i>yet</i>. Create a 
  Service Plan first (see <a href="<c:url value="/home"/>">here</a> and then hit the <b>Plans</b> tab), and come back here. 
  </div>
  </c:when>
  <c:otherwise>
		<table class="api">
		<tr><th>Service Plan</th><th>Configuration URL</th></tr>
		<c:forEach var="servicePlan" items="${servicePlans}"  varStatus="status">
		      <tr>
		        <td valign="top">${servicePlan.name}</td>
		        <td valign="top" class="tiny">
		        <a href="${fullUrl}<c:url value="${servicePath}"/>${servicePlan.id}">${fullUrl}<c:url value="${servicePath}"/>${servicePlan.id}</a></td>
		      </tr>
		</c:forEach>
		</table>
    </c:otherwise>
</c:choose>   
</c:if>
