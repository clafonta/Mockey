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
servicePlans = Util.orderAlphabeticallyByServicePlanName(servicePlans);
request.setAttribute("servicePlans",servicePlans);
URL serverURLObj = new URL(request.getScheme(), // http
		request.getServerName(), // host
		request.getServerPort(), // port
        "");
request.setAttribute("baseUrl",serverURLObj.toString());
request.setAttribute("api_setplan_service_name",ServicePlanConfigurationAPI.API_SERVICE_PLAN_CONFIGURATION_NAME);

%>
<c:if test="${serviceName eq api_setplan_service_name}">
<h3>Available API Calls</h3>
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
                <c:url var="setPlanByIdUrl" value="${servicePath}">
                    <c:param name="<%= ServicePlanConfigurationAPI.API_SETPLAN_PARAMETER_ACTION %>" value="set_plan" />
                    <c:param name="<%= ServicePlanConfigurationAPI.API_SETPLAN_PARAMETER_TYPE %>" value="json" />
                    <c:param name="<%= ServicePlanConfigurationAPI.API_SETPLAN_PARAMETER_PLAN_ID %>" value="${servicePlan.id}" />
                </c:url>
                <c:url var="setPlanByNameUrl" value="${servicePath}">
                    <c:param name="<%= ServicePlanConfigurationAPI.API_SETPLAN_PARAMETER_ACTION %>" value="set_plan" />
                    <c:param name="<%= ServicePlanConfigurationAPI.API_SETPLAN_PARAMETER_TYPE %>" value="json" />
                    <c:param name="<%= ServicePlanConfigurationAPI.API_SET_SAVE_OR_UPDATE_PARAMETER_PLAN_NAME %>" value="${servicePlan.name}" />  
                </c:url>
                <a href="${baseUrl}${setPlanByIdUrl}">${baseUrl}${setPlanByIdUrl}</a> OR <br/>
                <a href="${baseUrl}${setPlanByNameUrl}">${baseUrl}${setPlanByNameUrl}</a>
                </td>
              </tr>
        </c:forEach>
        </table>
    </c:otherwise>
</c:choose>   
</c:if>
