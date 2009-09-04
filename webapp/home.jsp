<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="pageTitle" value="Home" scope="request" />
<c:set var="currentTab" value="home" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
    <div id="main">        
        <%@ include file="/WEB-INF/common/message.jsp" %>        
        <c:choose>
	        <c:when test="${!empty services}">
	           
	            <c:if test="${mode eq 'edit_plan'}">
	            <form action="<c:url value="/plan/setup" />" method="post">
	            </c:if>
	           
		<c:if test="${mode eq 'edit_plan'}">
			
			<h1 class="highlight">Edit Service Plan: <c:out value="${plan.name}" /></h1>
			

			<c:if test="${!empty plan.id}">
				<input type="hidden" name="plan_id"
					value="<c:out value="${plan.id}"/>" />
			</c:if>
			
			
			<table width="100%">
				<tbody>
					<tr>
						<th width="20%">
						<p>Plan Name:</p>
						</th>
						<td>
						<p><input type="text" style="width: 60%;" name="plan_name"
							value="<c:out value="${plan.name}"/>" /></p>
						</td>
					</tr>
					
					<tr>
						<td></td>
						<td>
						<p align="right">
						<c:choose>
							<c:when test="${!empty plan.id}">
								<input type="submit" name="create_or_update_plan"
									value="Update Plan" class="button" />
							</c:when>
							<c:otherwise>
								<input type="submit" name="create_or_update_plan"
									value="Save as a Plan" class="button" />
							</c:otherwise>
						</c:choose> <a href="<c:url value="/home" />">Cancel</a>
						</p>
						</td>
					</tr>
				</tbody>
			</table>
			<br />
		</c:if>
		        <div>
                <h1>Mock Services</h1>
                <hr />
               
                      </div>
		        <table class="simple" width="100%" cellspacing="0">
			        <thead>
			            <tr>
						 <th width="15%" style="text-align:center;background-color:#bdbdbd;">Service Name</th>
						 <th style="text-align:center;background-color:#bdbdbd;">Service Settings</th>			            </tr>
			        </thead>
		            <tbody>
						<c:forEach var="mockservice" items="${services}">
											
						<tr>
						    <a name="<c:out value="${mockservice.id}"/>"/>
							<td valign="top">							
	                            <c:url value="/setup" var="setupUrl">
	                                <c:param name="serviceId" value="${mockservice.id}" />
	                             </c:url>
	                             <c:url value="/setup" var="deleteUrl">
	                                <c:param name="serviceId" value="${mockservice.id}" />
	                                <c:param name="delete" value="true" />
	                             </c:url>
	                             <c:url value="/history/list" var="historyUrl">
	                                <c:param name="serviceId" value="${mockservice.id}" />
	                             </c:url>
	                             <a class="tiny" href="<c:out value="${setupUrl}"/>" title="Edit service definition">edit</a> |
	                             <a class="tiny" href="<c:out value="${historyUrl}"/>" title="View request and response history">history</a> | 
	                             <a class="tiny" href="<c:out value="${deleteUrl}"/>" title="Delete this service">delete</a>                     
	                                <br /><br />
								<h2><a href="<c:out value="${setupUrl}"/>" title="Edit Service">
								<c:out value="${mockservice.serviceName}" /></a></h2>							
							</td>
							<td colspan="2">							
								<c:if test="${mode ne 'edit_plan'}">
<%-- LOVELY JQUERY + JSP TAGS + EL --%>									
<script type="text/javascript">
$(document).ready(function() {
	$.prompt.setDefaults({
		opacity:0.2
	}); 
	$("#update_service_<c:out value="${mockservice.id}"/>").click(function(){
		$scenario = $("input[name='scenario_<c:out value="${mockservice.id}"/>']:checked").val();
		$serviceResponseType = $("input[name='serviceResponseType_<c:out value="${mockservice.id}"/>']:checked").val();
		$hangTime = document.getElementById("hangTime_<c:out value="${mockservice.id}"/>").value;
		$serviceId = document.getElementById("serviceId_<c:out value="${mockservice.id}"/>").value;
		$.post("<c:url value="/service_scenario"/>", {serviceResponseType_<c:out value="${mockservice.id}"/>:$serviceResponseType, serviceId:$serviceId,scenario_<c:out value="${mockservice.id}"/>:$scenario, hangTime_<c:out value="${mockservice.id}"/>:$hangTime}, function(xml) {
			$("#responseMessage_<c:out value="${mockservice.id}"/>").html(
					$("report", xml).text()
			);
			$.prompt("Service '<c:out value="${mockservice.serviceName}"/>' updated.");
		});
	});
});
</script>
<%-- EXAMPLE: There is a response from /service_scenario servlet this is how we display it.  --%>
<%-- <div id="responseMessage_<c:out value="${mockservice.id}"/>" class="outputTextArea"></div>  --%>
								<div id="updateStatus_<c:out value="${mockservice.id}"/>" class="outputTextArea"></div>
								<form id="multi_form" action="<c:url value="/service_scenario"/>" method="post">
								<input type="hidden" name="serviceId" id="serviceId_<c:out value="${mockservice.id}"/>" value="${mockservice.id}" />
								</c:if>							
							  <c:set var="mockUrl"><mockey:url value="${mockservice.serviceUrl}"/></c:set> 
							  Mock URL: <a href="<mockey:url value="${mockservice.serviceUrl}"/>"><mockey:url value="${mockservice.serviceUrl}"/></a>
							  <%--   COMMENTING OUT FOR TWO REASONS --%>
							  <%-- 
							  1. Too many flash instances makes Safari crash.
							  2. Flash doesn't play well with transparent overlays. 
							  <mockey:clipboard id="clip-mockservice" text="${mockUrl}" bgcolor="#ffff99"/> 							  
							  --%>
							  <br />
							  <input type="hidden" name="plan_item" value="<c:out value="${mockservice.id}"/>"/>
			                 	                 
							  <p>
			                  <input type="radio" name="serviceResponseType_<c:out value="${mockservice.id}"/>" id="serviceResponseType_<c:out value="${mockservice.id}"/>" value="0" <c:if test='${mockservice.serviceResponseType eq 0}'>checked</c:if> /> 
			                  <b>Proxy</b> to this URL: <span class="highlight">
			                    <c:out value="${mockservice.realServiceUrl}" /></span>
			                    <%--   COMMENTING OUT FOR TWO REASONS --%>
								<%-- 
								  1. Too many flash instances makes Safari crash.
								  2. Flash doesn't play well with transparent overlays. 
				                    <mockey:clipboard id="clip-mockservice" text="${mockservice.realServiceUrl}" bgcolor="#ffff99"/>
			                     --%>
			                    <c:if test="${empty mockservice.realServiceUrl and mockservice.serviceResponseType eq 0}">
			                      <div>
			                        <p class="alert_message">You need to <a href="<c:out value="${setupUrl}"/>" title="edit">define a real URL</a></p>
			                      </div>
			                    </c:if>
			                  </p>
			                  <p>
			                  <input type="radio" name="serviceResponseType_<c:out value="${mockservice.id}"/>" value="2" <c:if test='${mockservice.serviceResponseType eq 2}'>checked</c:if> /> 
			                  <b>Dynamic Scenario</b> 
			                  </p>
                              <p>
                                <input type="radio" name="serviceResponseType_<c:out value="${mockservice.id}"/>" value="1" <c:if test='${mockservice.serviceResponseType eq 1}'>checked</c:if> /> 
                                <b>Static Scenario</b> 
                                <c:if test="${empty mockservice.scenarios and mockservice.serviceResponseType ne 0}">
                                <c:url value="/scenario" var="scenarioUrl">
                                    <c:param name="serviceId" value="${mockservice.id}" />                                    
                                </c:url>
                                    <div>
                                        <p class="alert_message">You need to <a href="<c:out value="${scenarioUrl}"/>" title="Create service scenario" border="0" />create</a>
                                         a scenario before using "Scenario".</p>
                                        <input type="hidden" name="serviceResponseType_<c:out value="${mockservice.id}"/>" value="false" />
                                        
                                    </div>
                                </c:if>
                              </p>                        	  
							  <span>
                                <ul class="group">
                                    <li>
                                    Select a Static Scenario. 
                                    </li>
	                                <c:choose>
	                                  <c:when test="${not empty mockservice.scenarios}">	                                
	                                  <c:forEach var="scenario" begin="0" items="${mockservice.scenarios}">
	                                    <li>
	                                      <input type="radio" name="scenario_<c:out value="${mockservice.id}"/>" id="scenario_<c:out value="${mockservice.id}"/>" value="<c:out value="${scenario.id}"/>"
	                                      <c:if test='${mockservice.defaultScenarioId eq scenario.id}'>checked</c:if> />
	                                      <c:url value="/scenario" var="scenarioEditUrl">
	                                        <c:param name="serviceId" value="${mockservice.id}" />
	                                        <c:param name="scenarioId" value="${scenario.id}" />
	                                      </c:url> 
	                                      <a href="<c:out value="${scenarioEditUrl}"/>" title="Edit service scenario"  ><c:out value="${scenario.scenarioName}" /></a>
	                                      <c:if test="${!empty universalError and universalError.id eq scenario.id and universalError.serviceId eq mockservice.id}">
	                                        (<span class="highlight_pos tiny">Universal Error Response</span>)
	                                      </c:if>
	                                      <c:if test="${scenario.id eq mockservice.errorScenarioId}">
	                                        (<span class="highlight_pos tiny">Service Error Response</span>)
	                                      </c:if>
	                                    </li>
	                                  </c:forEach>
	                                  </c:when>
	                                  <c:otherwise>
	                                    <c:url value="/scenario" var="scenarioUrl">
								            <c:param name="serviceId" value="${mockservice.id}" />
								        </c:url>
	                                  	<li class="alert_message"><span>You need to <a href="<c:out value="${scenarioUrl}"/>" title="Create service scenario" border="0" />create</a>
	                                     a scenario before using "Static or Dynamic Scenario".</span></li>
	                                  </c:otherwise>
	                                </c:choose>
                                </ul>
                              </span>  
                               <p>
			                    <input type="text" name="hangTime_<c:out value="${mockservice.id}"/>" id="hangTime_<c:out value="${mockservice.id}"/>" maxlength="20" size="20" value="<c:out value="${mockservice.hangTime}"/>" /> Hang time (milliseconds)
			                  </p>		                            
                              <c:if test="${mode ne 'edit_plan'}">                              
	                              <input type="button" name="update_service_<c:out value="${mockservice.id}"/>" id="update_service_<c:out value="${mockservice.id}"/>" value="Update" class="button" />
	                              </form>
	                              
                              </c:if>
							</td>
						</tr>    
						</c:forEach>
		            </tbody>
		        </table>	
		        <c:if test="${mode eq 'edit_plan'}">
		        </form>    
		        </c:if>
		         <div>
                  <a name="plan"></a> 
                  <h1>Service Plans</h1>
                  <hr />
                  <c:url value="/plan/setup" var="createPlanUrl">
                    <c:param name="action" value="edit_plan" />
                 </c:url>
                  <p><a href="<c:out value="${createPlanUrl}"/>">Create a Plan</a></p>
                  <c:choose>
                    <c:when test="${!empty plans}">
                    
                           <c:forEach var="planItem" items="${plans}">
                             <c:url value="/plan/setup" var="planUrl">
                                <c:param name="plan_id" value="${planItem.id}" />
                                <c:param name="action" value="edit_plan" />
                             </c:url>
                             <c:url value="/plan/setup" var="deletePlanUrl">
                                <c:param name="plan_id" value="${planItem.id}" />
                                <c:param name="action" value="delete_plan" />
                             </c:url>
                             <c:url value="/plan/setup" var="setPlanUrl">
                                <c:param name="plan_id" value="${planItem.id}" />
                                <c:param name="action" value="set_plan" />
                             </c:url>
                             <p>
                               <b><c:out value="${planItem.name}"/></b> (
                               <a href="<c:out value="${planUrl}"/>">edit</a> | <a href="<c:out value="${deletePlanUrl}"/>">delete</a> | <a href="<c:out value="${setPlanUrl}"/>">set plan</a>)
							</p>
                    
                           </c:forEach>
                    
                    </c:when>
                    <c:otherwise>
                      <p class="highlight">There are no service plans defined. But that's OK. </p>                      
                    </c:otherwise>
                  </c:choose>
                </div>     
    </div>
	        </c:when>
	        <c:otherwise>
			  <p class="alert_message">There are no mock services defined. You can <a href="<c:url value="setup"/>">create one</a> or start <a href="<c:url value="help#record"/>">recording</a>. </p>			  
			  
			  
			</c:otherwise>
        </c:choose>
        
<jsp:include page="/WEB-INF/common/footer.jsp" />