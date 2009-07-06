<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%--
			  The block below is for displaying success messages. be sure to remove from session after display
			--%>
<c:if test="${not empty successMessages}">

	<ul class="info_message">
		<c:forEach var="msg" items="${successMessages}">
			<li><p ><c:out value="${msg}" escapeXml="false" /></p></li>
		</c:forEach>
	</ul>
	<c:remove var="successMessages" scope="session" />

</c:if>

<c:if test="${not empty errorMessages}">
	
	<ul class="alert_message">
		<c:forEach var="msg" items="${errorMessages}">
			<li><c:out value="${msg}" escapeXml="false" /></li>
		</c:forEach>
	</ul>
	<c:remove var="errorMessages" scope="session" />
	
</c:if>

