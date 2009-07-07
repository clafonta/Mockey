<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="actionKey" value="edit_service" scope="request" />
<c:set var="pageTitle" value="Configure" scope="request" />
<c:set var="currentTab" value="create" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
<div id="main">
    <%@ include file="/WEB-INF/common/message.jsp"%>    
    <c:choose>
        <c:when test="${!empty mockservice.scenarios and !empty mockservice.id}">
            <h1>Service Setup: <span class="highlight"><c:out value="${mockservice.serviceName}"/></span></h1>
        </c:when>
        <c:when test="${!empty mockservice.id and !empty mockservice.scenarios }">
            <c:url value="/scenario" var="scenarioUrl">
                <c:param name="serviceId" value="${mockservice.id}" />
            </c:url>
            <h1>Service Setup</h1>
            <div style="margin-left:20%; margin-right:20%">
                <h2 class="overlabel">Step 1 - Create a service name and mock URI <span style="color: green;">- Done!</span></h2>
                <h2>Step 2 - Create a service scenario <a style="color: red;" href="<c:out value="${scenarioUrl}"/>" title="Create service scenario" border="0" />Create Now</a></h2>
            </div>
        </c:when>
        <c:otherwise>
            <h1>Service Setup</h1>
            <div style="margin-left:20%; margin-right:20%">
                <h2>Step 1 - Create a service name and mock URI</h2>
                <h2 class="overlabel">Step 2 - Create a service scenario</h2>
            </div>
        </c:otherwise>
    </c:choose>
    <%@ include file="/WEB-INF/common/inc_action_links.jsp"%>
    <form action="<c:url value="/setup"/>" method="POST">
        <input type="hidden" name="serviceId" value="<c:out value="${mockservice.id}"/>" /> 
     
        <br />
        <table class="simple" width="100%">
            <tbody>
	            <tr><th><p>Service name:</p></th>
	                <td>
	                    <p><input type="text" name="serviceName" maxlength="100" size="90%" value="<c:out value="${mockservice.serviceName}"/>" /></p>
	                    <p>Use a self descriptive name. For example, if you were to use this for 'authentication' testing, then call it 'Authentication'.</p>
	                </td>
	            </tr>
                <tr>
                    <th><p>Real service URL: <br /><span style="color:blue;">(optional)</span></p></th>
                    <td>
                        <p><input type="text" name="realServiceUrl" maxlength="100" size="90%" value="<c:out value="${mockservice.realServiceUrlWithScheme}"/>" /></p>
                        <p>You'll need this URL if you want Mockey to serve as a proxy to record transactions between your application and the real service.</p>
                    </td>
                </tr>                
	            <tr><th><p>Service description:</p></th>
                    <td>
                        <p><textarea name="description" style="width:90%;" rows="2" ><c:out value="${mockservice.description}" /></textarea></p>
                        <p>Give a short description of this service (max 1000 chars).</p>
                    </td>
                </tr>
	            <tr>
	                <th><p>Mock service URI:</p></th>
	                <td>
	                    <p><input type="text" name="mockServiceUrl" maxlength="100" size="90%" value="<c:out value="${mockservice.mockServiceUrl}"/>" /></p>
	                    <p>Use something like: <i>/some_service_path/here/more</i> </p>
	                </td>
	            </tr>
	            <tr>
	                <th><p>HTTP header definition:</p></th>
	                <td>
	                    <p><input type="text" size="40px" name="httpHeaderDefinition" value="<c:out value="${mockservice.httpHeaderDefinition}"/>" /></p>
	                    <p>For example: <i>text/xml; utf-8</i>, <i>application/json;</i>, etc. </p>
	                </td>
	            </tr>
            </tbody>
        </table>
        <p align="right">
	        <c:choose>
	            <c:when test="${!empty mockservice.id}">
	                <input type="submit" name="update" value="Update" />
	            </c:when>
	            <c:otherwise>
	                <input type="submit" name="create" value="Create" />
	            </c:otherwise>
	        </c:choose> 
	        <c:if test="${!empty mockservice.id}">
	            <input type="submit" name="delete" value="Delete" onclick="return confirm('Deleting this service will delete all scenarios associated with it. \nAre you sure you want to delete this service?');" />
	        </c:if>
	        <a href="<c:url value="/"/>">Cancel</a>
	    </p>
    </form>
    <p>
	    <c:if test="${!empty mockservice.scenarios and !empty mockservice.id}">
	        <%@ include file="/WEB-INF/common/inc_scenario_list.jsp"%>
	    </c:if>
    </p>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />