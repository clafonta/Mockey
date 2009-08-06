<c:if test="${!empty mockservice.id}">
	<c:url value="/setup" var="setupUrl">
	    <c:param name="serviceId" value="${mockservice.id}" />
	</c:url>
	    <c:url value="/scenariolist" var="scenarioListUrl">
	    <c:param name="serviceId" value="${mockservice.id}" />
	</c:url>
	<c:url value="/scenario" var="scenarioCreateUrl">
        <c:param name="serviceId" value="${mockservice.id}" />
    </c:url>
	<c:url value="/configure" var="configureUrl">
	    <c:param name="serviceId" value="${mockservice.id}" />
	</c:url>
	<c:url value="/history/list" var="historyUrl">
        <c:param name="serviceId" value="${mockservice.id}" />
    </c:url>
	<p style="text-align:center;" id="action_links"> 	    
	    <a href="<c:out value="${setupUrl}"/>" title="Edit service settings" <c:if test="${actionKey == 'edit_service'}"> class="nav" </c:if>>Edit Service Definition</a> |
	    <a href="<c:out value="${scenarioCreateUrl}"/>" title="Edit service settings">Create Scenario</a> |
	    <a href="<c:out value="${scenarioListUrl}"/>" title="Scenarios" <c:if test="${actionKey == 'scenario_list'}"> class="nav" </c:if>>Edit Scenarios</a> |
	    <a href="<c:out value="${configureUrl}"/>" title="Configure service response scenario" <c:if test="${actionKey == 'conf_service'}"> class="nav" </c:if>>Service Response Style</a> |  
	    <a href="<c:out value="${historyUrl}"/>" title="History of request made to this service" <c:if test="${actionKey == 'service_history'}"> class="nav" </c:if>>History</a>
	
		<table class="basic" width="100%">
	        <tbody>
	            <tr><th colspan=2" style="text-align:center;">Service Definition</th></tr>
	            <tr><th width="180px"><p>Service Name:</p></th>
	            <td>
	                <p>
	                    <c:url value="/setup" var="setupUrl">
	                        <c:param name="serviceId" value="${mockservice.id}" />
	                    </c:url> 
	                    <c:out value="${mockservice.serviceName}" />
	                </p>
	            </td>
	            </tr>
	            <tr>
	                <th><p>Mock URL:</p></th>
                    <c:set var="mockUrl"><mockey:url value="${mockservice.serviceUrl}"/></c:set> 
	                <td>
	                    <p><a href="<mockey:url value="${mockservice.serviceUrl}"/>"><mockey:url value="${mockservice.serviceUrl}"/></a><mockey:clipboard id="clip-mockservice" text="${mockUrl}" bgcolor="#F1F1F0"/></p>
	                </td>
	            </tr>
	            <tr>
	                <th><p>Real URL:</p></th>
	                <td>
	                    <p>
	                        <c:choose>
	                            <c:when test="${empty mockservice.realServiceUrl}">
	                                <span <c:if test="${mockservice.serviceResponseType eq 0}">style="color:red;" </c:if>>(undefined)</span>
	                            </c:when>
	                            <c:otherwise>
	                                <c:out value="${mockservice.realServiceUrl}" /><mockey:clipboard id="clip-realservice" text="${mockservice.realServiceUrl}" bgcolor="#F1F1F0" /> 
	                            </c:otherwise>
	                        </c:choose>
	                    </p>
	                </td>
	            </tr>
	            <tr>
	                <th><p>HTTP header definition:</p></th>
	                <td><p><c:out value="${mockservice.httpHeaderDefinition}" /></p></td>
	            </tr>
	        </tbody>
	    </table>
	</p>
	<br />
</c:if> 