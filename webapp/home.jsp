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
	           
	            <c:if test="${mode eq 'edit'}">
	            <form action="<c:url value="/plan/setup"/>" method="post">
	            </c:if>
	            <div>
                  <a name="plan"></a> 
                  <h1>Service Plans</h1>
                  <hr />
                  <c:url value="/plan/setup" var="createPlanUrl">
                    <c:param name="action" value="edit" />
                 </c:url>
                  <p>
                      A Service Plan is a saved state of all Mock Service configurations. For example, all 
                      happy scenarios per service can be saved as a 'Happy Path' plan and all non-happy scenarios 
                      as an 'Unhappy Path' plan. <a href="<c:out value="${createPlanUrl}"/>">Create a Plan</a> </p>
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
                      <p class="highlight">There are no service plans defined. But that's OK. </p>                      
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
		        <div>
                <h1>Mock Services</h1>
                <hr />
                <p>
                      Here's a list of all the mock services. A mock service can be a proxy to a real service, enabling you to inspect request and response messages
                      being exchanged. You can set up Mockey to go through a corporate proxy server (see <a href="<c:url value="/proxy/settings" />">Proxy Settings</a>). 
                      There's support for HTTP and HTTPS interactions (if your proxy server or endpoint service is https). Be sure to check out your service's History
                      to inspect and save a past conversation as a Service Scenario.                       
                      </p>
                      </div>
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
						
						<tr>
						    <a name="<c:out value="${mockservice.id}"/>"/>
							<td valign="top">
							
                            <c:url value="/setup" var="setupUrl">
                                <c:param name="serviceId" value="${mockservice.id}" />
                             </c:url>
                             <c:url value="/history/list" var="historyUrl">
                                <c:param name="serviceId" value="${mockservice.id}" />
                             </c:url>
                             <a class="tiny" href="<c:out value="${setupUrl}"/>" title="Edit service definition">edit</a> |
                             <a class="tiny" href="<c:out value="${historyUrl}"/>" title="View request and response history">history</a>                         
                                <br /><br />
							<h2><a href="<c:out value="${configureUrl}"/>" title="Configure service response">
							
							
							<c:out value="${mockservice.serviceName}" /></a></h2>
							
							</td>
							<td colspan="2">
							<c:choose>
							<c:when test="${mode ne 'edit'}">
							<form action="<c:url value="/service_scenario"/>" method="post">
							<input type="hidden" name="serviceId" value="${mockservice.id}" />
							</c:when>
							<c:otherwise>
							
							</c:otherwise>
							</c:when>
							  <c:set var="mockUrl"><mockey:url value="${mockservice.serviceUrl}"/></c:set> 
							  Mock URL: <a href="<mockey:url value="${mockservice.serviceUrl}"/>"><mockey:url value="${mockservice.serviceUrl}"/></a><mockey:clipboard id="clip-mockservice" text="${mockUrl}" bgcolor="#FFFFF0"/>
							  <br />
							  <input type="hidden" name="plan_item" value="<c:out value="${mockservice.id}"/>"/>
							  <p>
			                  <input type="radio" name="proxyOn_<c:out value="${mockservice.id}"/>" value="true" <c:if test='${mockservice.proxyOn}'>checked</c:if> /> 
			                  <b>Proxy</b> to this URL: <span class="highlight">
			                    <c:out value="${mockservice.realServiceUrl}" /></span><mockey:clipboard id="clip-mockservice" text="${mockservice.realServiceUrl}" bgcolor="#FFFFF0"/>
			                    <c:if test="${empty mockservice.realServiceUrl and mockservice.proxyOn}">
			                      <div>
			                        <p class="alert_message">You need to <a href="<c:out value="${setupUrl}"/>" title="edit">define a real URL</a></p>
			                      </div>
			                    </c:if>
			                  </p>
                              <p>
                                <input type="radio" name="proxyOn_<c:out value="${mockservice.id}"/>" value="false" <c:if test='${!mockservice.proxyOn}'>checked</c:if> /> 
                                <b>Scenario -</b> Pick the type of scenario. 
                                <c:if test="${empty mockservice.scenarios and !mockservice.proxyOn}">
                                <c:url value="/scenario" var="scenarioUrl">
                                    <c:param name="serviceId" value="${mockservice.id}" />                                    
                                </c:url>
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
                              </form>
                              </c:otherwise>
                              </c:choose>
							</td>
						</tr>    
						</c:forEach>
		            </tbody>
		        </table>	
		        <c:if test="${mode eq 'edit'}">
		        </form>    
		        </c:if>
	        </c:when>
	        <c:otherwise>
			  <p class="alert_message">There are no mock services defined. <a href="<c:url value="setup"/>">Create one.</a></p>
			</c:otherwise>
        </c:choose>
    </div>
<jsp:include page="/WEB-INF/common/footer.jsp" />