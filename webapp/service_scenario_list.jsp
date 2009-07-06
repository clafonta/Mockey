<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="actionKey" value="scenario_list" scope="request" />
<c:set var="pageTitle" value="Scenario Setup" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
    <div id="main">
        <h1>Scenarios for service: <span class="highlight"><c:out value="${mockservice.serviceName}"/></span></h1>
        <%@ include file="/WEB-INF/common/inc_action_links.jsp"%>
        <p>
            <c:url value="/scenario" var="scenarioUrl">
                <c:param name="serviceId" value="${mockservice.id}" />
            </c:url>
            <a href="<c:out value="${scenarioUrl}"/>" title="Create scenario">Create scenario</a>
        </p>
	    <p>
	        <%@ include file="/WEB-INF/common/inc_scenario_list.jsp" %>
	    </p>
	    
	</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
