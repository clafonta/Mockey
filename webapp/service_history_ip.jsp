<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="pageTitle" value="History" scope="request"/>
<jsp:include page="/WEB-INF/common/header.jsp" />
<div id="main">
    <h1>Service History: <span class="highlight"><c:out value="${mockservice.serviceName}"/></span></h1>
    <%@ include file="/WEB-INF/common/inc_action_links.jsp"%>
    <c:choose>
        <c:when test="${!empty scenarioHistoryList}">
            <c:forEach var="scenario" items="${scenarioHistoryList}" varStatus="status">
                <p>
                    <form action="<c:url value="/scenario"/>" method="post">
	                    <input type="hidden" name="actionTypeGetFlag" value="true" />
	                    <input type="hidden" name="serviceId" value="<c:out value="${mockservice.id}"/>" />
	                    <table class="simple" width="100%">
	                        <tbody>
	                            <tr>
	                                <td>
	                                    <p style="text-align:right;">
	                                    <c:url value="/history/detail" var="deleteScenarioUrl">
	                                       <c:param name="serviceId" value="${mockservice.id}" />
	                                       <c:param name="iprequest" value="${iprequest}" />
	                                       <c:param name="scenarioId" value="${scenario.id}" />
	                                       <c:param name="action" value="delete" />
	                                    </c:url>
	                                    <a href="<c:out value="${deleteScenarioUrl}"/>"><img src="<c:url value="/images/cross.png"/>"</a>
	                                    </p>
	                                    <p><b>Time and IP:</b> <c:out value="${scenario.scenarioName}"/> <input type="submit" name="Save" value="Save" /></p>
	                                </td>
	                            </tr>
	                            <tr>
	                                <td>
	                                    <p>Request:</p>
	                                    <p><textarea name="requestMessage" rows="10" cols="80%"><c:out value="${scenario.requestMessage}"/></textarea></p>
	                                </td>
	                            </tr>
	                            <tr>
	                                <td>
	                                    <p>Response:</p>
	                                    <p>
                                            <textarea name="responseMessage" rows="10" cols="80%">${scenario.responseMessage}</textarea>                                            
                                        </p>
	                                </td>
	                            </tr>
	                        </tbody>   
	                    </table>
                    </form>
                </p>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <div><p>No historical requests. </p></div>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" /> 