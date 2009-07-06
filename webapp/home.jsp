<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="pageTitle" value="Home" scope="request" />
<c:set var="currentTab" value="home" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
    <div id="main">
        <p><strong>Mockey</strong> is a tool for testing application interactions over http.</p>        
        <c:choose>
	        <c:when test="${!empty services}">
		        <table class="simple" width="100%">
			        <thead>
			            <tr>
						 <th width="20%" style="text-align:left;">Mock Service</th>
						 <th width="60%" colspan="2" style="text-align:left;">Mock Service URL</th>
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
							<td><a href="<c:out value="${configureUrl}"/>" title="Configure service response"><c:out value="${mockservice.serviceName}" /></a></td>
							<td><a href="<mockey:url value="${mockservice.serviceUrl}"/>" title="Mock service URL"><mockey:url value="${mockservice.serviceUrl}"/></a></td>
							<td>								  
								<a class="tiny" href="<c:out value="${setupUrl}"/>" title="Edit service definition">edit</a> | 
								<a class="tiny" href="<c:out value="${historyUrl}"/>" title="View request and response history">history</a>
							</td>
						</tr>    
						</c:forEach>
		            </tbody>
		        </table>		       
	        </c:when>
	        <c:otherwise>
			  <p class="alert_message">There are no mock services defined. <a href="<c:url value="setup"/>">Create one.</a></p>
			</c:otherwise>
        </c:choose>
    </div>
<jsp:include page="/WEB-INF/common/footer.jsp" />