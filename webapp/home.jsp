<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="pageTitle" value="Home" scope="request" />
<c:set var="currentTab" value="home" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
    <div id="main">
        <p><strong>Mockey</strong> is a tool for testing application interactions over http.</p>
        <%@ include file="/WEB-INF/common/message.jsp" %>        
        <c:choose>
	        <c:when test="${!empty services}">
	           
	            <form action="<c:url value="/plan/setup"/>" method="post">
	            <div>
                  <a name="plan"></a> 
                  <h1>Service Plans</h1>
                   <c:url value="/plan/setup" var="createPlanUrl">
                                <c:param name="action" value="edit" />
                             </c:url>
                             <a href="<c:out value="${createPlanUrl}"/>">Create a plan</a>
                  <c:choose>
                    <c:when test="${!empty plans}">
                    
                           <c:forEach var="planItem" items="${plans}">
                             <c:url value="/plan/setup" var="planUrl">
                                <c:param name="plan_id" value="${planItem.id}" />
                                <c:param name="action" value="edit" />
                             </c:url>
                             <c:url value="/plan/setup" var="deletePlanUrl">
                                <c:param name="plan_id" value="${planItem.id}" />
                                <c:param name="action" value="delete" />
                             </c:url>
                             <c:url value="/plan/setup" var="setPlanUrl">
                                <c:param name="plan_id" value="${planItem.id}" />
                                <c:param name="action" value="set" />
                             </c:url>
                             <p>
                               <b><c:out value="${planItem.name}"/></b> (
                               <a href="<c:out value="${planUrl}"/>">edit</a> | <a href="<c:out value="${deletePlanUrl}"/>">delete</a> | <a href="<c:out value="${setPlanUrl}"/>">set plan</a>):
                               <c:out value="${planItem.description}"/></p>
                    
                           </c:forEach>
                    
                    </c:when>
                    <c:otherwise>
                      <p class="alert_message">There are no service plans defined. </p>
                      <p>
                      A Service Plan is a saved state of all service configurations. For example, all 
                      happy scenarios for each service can be the 'Happy Path' plan, and all non-happy scenarios 
                      as an 'Unhappy Path' plan.
                      </p>
                    </c:otherwise>
                  </c:choose>
                </div>     
		<c:if test="${mode eq 'edit'}">
			
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
						<th width="20%">
						<p>Plan Description:</p>
						</th>
						<td>
						<p><textarea name="plan_description" style="width: 80%;"
							rows="3"><c:out value="${plan.description}" /></textarea></p>
						</td>
					</tr>
					<tr>
						<td></td>
						<td>
						<p align="right"><c:choose>
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
                <h1>Mock Services</h1>
		        <table class="simple" width="100%" cellspacing="0">
			        <thead>
			            <tr>
						 <th width="15%" style="text-align:left;">Mock Service Name</th>
						 <th style="text-align:left;">Mock Service Settings</th>
			            </tr>
			        </thead>
		            <tbody>
						<c:forEach var="mockservice" items="${services}">
						<c:url value="/configure" var="configureUrl">
							<c:param name="serviceId" value="${mockservice.id}" />
						</c:url>
						<c:url value="/setup" var="setupUrl">
							<c:param name="serviceId" value="${mockservice.id}" />
						</c:url>
						<c:url value="/history/list" var="historyUrl">
                            <c:param name="serviceId" value="${mockservice.id}" />
                        </c:url>
						<tr>
						    <a name="<c:out value="${mockservice.id}"/>"/>
							<td class="contact">
							<a href="<c:out value="${configureUrl}"/>" title="Configure service response">
							<c:out value="${mockservice.serviceName}" /></a> <br /><br />
							<a class="tiny" href="<c:out value="${setupUrl}"/>" title="Edit service definition">edit</a><br /> 
                                <a class="tiny" href="<c:out value="${historyUrl}"/>" title="View request and response history">history</a>
							</td>
							<td>
							  <c:set var="mockUrl"><mockey:url value="${mockservice.serviceUrl}"/></c:set> 
							  Mock URL: <a href="<mockey:url value="${mockservice.serviceUrl}"/>"><mockey:url value="${mockservice.serviceUrl}"/></a><mockey:clipboard id="clip-mockservice" text="${mockUrl}" bgcolor="#FFFFF0"/>
							  <br />
							  <input type="hidden" name="plan_item" value="<c:out value="${mockservice.id}"/>"/>
							  <p <c:if test='${!mockservice.proxyOn}'>class="overlabel"</c:if>>
			                  <input type="radio" name="proxyOn_<c:out value="${mockservice.id}"/>" value="true" <c:if test='${mockservice.proxyOn}'>checked</c:if> /> 
			                  <b>Proxy</b> to this URL: <span class="highlight">
			                    <c:out value="${mockservice.realServiceUrl}" /></span><mockey:clipboard id="clip-mockservice" text="${mockservice.realServiceUrl}" bgcolor="#FFFFF0"/>
			                    <c:if test="${empty mockservice.realServiceUrl and mockservice.proxyOn}">
			                      <div>
			                        <p class="alert_message">You need to <a href="<c:out value="${setupUrl}"/>" title="edit">define a real URL</a></p>
			                      </div>
			                    </c:if>
			                  </p>
                              <p <c:if test='${mockservice.proxyOn}'>class="overlabel"</c:if>>
                                <input type="radio" name="proxyOn_<c:out value="${mockservice.id}"/>" value="false" <c:if test='${!mockservice.proxyOn}'>checked</c:if> /> 
                                <b>Scenario -</b> Pick the type of scenario. 
                                <c:if test="${empty mockservice.scenarios and !mockservice.proxyOn}">
                                    <div>
                                        <p class="alert_message">You need to <a href="<c:out value="${scenarioUrl}"/>" title="Create service scenario" border="0" />create</a>
                                         a scenario before using "Scenario".</p>
                                        <input type="hidden" name="proxyOn_<c:out value="${mockservice.id}"/>" value="false" />
                                    </div>
                                </c:if>
                             </p>
                        	  
							  <span>
                                <ul class="group">
                                  <c:forEach var="scenario" begin="0" items="${mockservice.scenarios}">
                                    <li>
                                      <c:if test='${!mockservice.replyWithMatchingRequest}'>
                                        <input type="radio" name="plan_item_scenario_<c:out value="${mockservice.id}"/>" value="<c:out value="${scenario.id}"/>"
                                        <c:if test='${mockservice.defaultScenarioId eq scenario.id}'>checked</c:if> />
                                      </c:if> 
                                      <c:url value="/scenario" var="scenarioEditUrl">
                                        <c:param name="serviceId" value="${mockservice.id}" />
                                        <c:param name="scenarioId" value="${scenario.id}" />
                                      </c:url> 
                                      <a href="<c:out value="${scenarioEditUrl}"/>" title="Edit service scenario"  <c:if test='${mockservice.proxyOn}'>class="overlabel"</c:if>><c:out value="${scenario.scenarioName}" /></a>
                                    </li>
                                  </c:forEach>
                                </ul>
                              </span>
                              <c:choose>
                              <c:when test="${mode eq 'edit'}">
                              </c:when>
                              <c:otherwise>
                              <input type="submit" name="update_service_<c:out value="${mockservice.id}"/>" value="Update" class="button" />
                              </c:otherwise>
                              </c:choose>
							</td>
						</tr>    
						</c:forEach>
		            </tbody>
		        </table>	
		        
		        </form>    
	        </c:when>
	        <c:otherwise>
			  <p class="alert_message">There are no mock services defined. <a href="<c:url value="setup"/>">Create one.</a></p>
			</c:otherwise>
        </c:choose>
    </div>
<jsp:include page="/WEB-INF/common/footer.jsp" />