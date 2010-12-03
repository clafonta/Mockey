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
Service exampleService = null;
for(Service s: store.getServices()) {
    if(s.getScenarios().size() > 0 ) {
        exampleService = s;
        break;
    }
}
servicePlans = Util.orderAlphabeticallyByServicePlanName(servicePlans);
request.setAttribute("servicePlans",servicePlans);
request.setAttribute("exampleService",exampleService);


URL serverURLObj = new URL(request.getScheme(), // http
		request.getServerName(), // host
		request.getServerPort(), // port
        "");
request.setAttribute("baseUrl",serverURLObj.toString());
request.setAttribute("api_setplan_service_name",ServicePlanConfigurationAPI.API_SERVICE_PLAN_CONFIGURATION_NAME);
request.setAttribute("api_conf_service_name",ServiceConfigurationAPI.API_SERVICE_CONFIGURATION_NAME);
request.setAttribute("api_info_service_name",ServiceDefinitionInfoAPI.API_SERVICE_INFO_NAME);


%>
<c:if test="${serviceName eq api_setplan_service_name}">
<h3>Sample API Calls</h3>
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
        
        <c:forEach var="servicePlan" items="${servicePlans}"  varStatus="status" end="6">
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

<c:if test="${serviceName eq api_conf_service_name}">
<h3>Sample API Calls</h3>
<c:choose>
  <c:when test="${empty exampleService}">
  <div class="info_message">
  <strong>No Service with a scenario is defined</strong>, so there's nothing you can do with this configuration API - <i>yet</i>. Create a 
  Service Scenario and then come back here. 
  </div>
  </c:when>
  <c:otherwise>
        <table class="api">
        <tr><th>Service Scenario</th><th>Configuration URL</th></tr>
             <c:forEach var="exampleScenario" items="${exampleService.scenarios}"  varStatus="status" end="6">
              <tr>
                <td valign="top">${exampleScenario.scenarioName}</td>
                <td valign="top" class="tiny">
                <c:url var="setServiceScenarioByIdUrl" value="${servicePath}">
                    <c:param name="<%= ServiceConfigurationAPI.API_SERVICE_ID %>" value="${exampleService.id}" />
                    <c:param name="<%= ServiceConfigurationAPI.API_SERVICE_SCENARIO_ID %>" value="${exampleScenario.id}" />
                </c:url>
                <c:url var="setServiceScenarioByNameUrl" value="${servicePath}">
                    <c:param name="<%= ServiceConfigurationAPI.API_SERVICE_NAME %>" value="${exampleService.serviceName}" />
                    <c:param name="<%= ServiceConfigurationAPI.API_SERVICE_SCENARIO_NAME %>" value="${exampleScenario.scenarioName}" />
                </c:url>
                 
                <a href="${baseUrl}${setServiceScenarioByNameUrl}">${baseUrl}${setServiceScenarioByNameUrl}</a> OR <br/>
                <a href="${baseUrl}${setServiceScenarioByIdUrl}">${baseUrl}${setServiceScenarioByIdUrl}</a>
                </td>
              </tr>
             </c:forEach>

        </table>
    </c:otherwise>
</c:choose>   
</c:if>
<c:if test="${serviceName eq api_info_service_name}">
<h3>Sample API Request(s)</h3>
<c:url var="setDefintionsUrl" value="/definitions"/>
<table class="api">
<tr><td><a href="${baseUrl}${setDefintionsUrl}">${baseUrl}${setDefintionsUrl}</a></td></tr>
<c:if test="${exampleService != null}">
<c:url var="setDefintionsByIdUrl" value="${servicePath}">
    <c:param name="<%= ServiceConfigurationAPI.API_SERVICE_ID %>" value="${exampleService.id}" />
</c:url>
<c:url var="setDefintionsByNameUrl" value="${servicePath}">
    <c:param name="<%= ServiceConfigurationAPI.API_SERVICE_NAME %>" value="${exampleService.serviceName}" />
</c:url>
<tr><td><a href="${baseUrl}${setDefintionsByIdUrl}">${baseUrl}${setDefintionsByIdUrl}</a></td></tr>
<tr><td><a href="${baseUrl}${setDefintionsByNameUrl}">${baseUrl}${setDefintionsByNameUrl}</a></td></tr>

</c:if>
</table> 

</c:if>

