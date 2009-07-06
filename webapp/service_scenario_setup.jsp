<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="actionKey" value="edit_scenario" scope="request" />
<c:set var="pageTitle" value="Scenario Setup" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
    <div id="main">        
        <h1>Edit scenario for service: <span class="highlight"><c:out value="${mockservice.serviceName}"/></span></h1>
        <%@ include file="/WEB-INF/common/inc_action_links.jsp"%>
        <%@ include file="/WEB-INF/common/message.jsp" %>
        <form action="<c:out value="${scenarioUrl}"/>" method="post">
            <input type="hidden" name="serviceId" value="<c:out value="${mockservice.id}"/>" />
			<c:if test="${!empty mockscenario.id}">
			    <input type="hidden" name="scenarioId" value="<c:out value="${mockscenario.id}"/>" />
			</c:if>
            <table class="simple" width="100%">
                <tbody>
                    <tr>
                        <th width="20%"><p>Scenario Name:</p></th>
	                    <td>
							<p><input type="text" style="width:100%;"  name="scenarioName" value="<c:out value="${mockscenario.scenarioName}"/>" /></p>
							<p>Example: <i>Valid Request</i> or <i>Invalid Request</i></p>
						</td>
                    </tr>
                    <tr>
						<th><p>Match argument: <span style="color:blue;">(optional)</span></p></th>
						<td>
						  <p>
						      <textarea name="matchStringArg" style="width:100%;" rows="2" ><c:out value="${mockscenario.matchStringArg}" /></textarea>
						  </p>
						  <p>
						You'll need this for 'dynamic message responses'. The mock service tool will search an
						    incoming request message for this <i>matching argument</i>. If the incoming request message
						    contains this string argument, then this scenario's response message will be the response.
						    If more than one scenario is defined with this matching argument, then the first
						    matched scenario's response message will be returned. If no scenario is found with a 
						    matching argument, a plain text error message will display.
						  </p>
						</td>
                    </tr>      
					<tr>
						<th><p>HTTP response message:</p></th>
						<td>
							<p><textarea name="responseMessage" rows="10" style="width:100%;"><c:out value="${mockscenario.responseMessage}" /></textarea></p>
						    <p>The message you want your mock service to reply with. Feel free to cut and paste XML, free form text, etc.</p>
						</td>
					</tr>
                </tbody>
            </table>
	        <p align="right">
				<c:choose>
		            <c:when test="${!empty mockservice.id}">
		                <input type="submit" name="update" value="Update" class="button" />
					</c:when>
					<c:otherwise>
		                <input type="submit" name="create" value="Create" class="button" />
					</c:otherwise>
		        </c:choose>            
		        <c:if test="${!empty mockservice.id}">
		            <button type="submit" name="delete" class="button" onclick="return confirm('Are you sure you want to delete this scenario?');">Delete</button>
		        </c:if>
		        <a href="<c:url value="/setup?serviceId=${mockservice.id}" />">Cancel</a>
	        </p>
	    </form>
	    <p>
	        <%@ include file="/WEB-INF/common/inc_scenario_list.jsp" %>
	    </p>
	</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
