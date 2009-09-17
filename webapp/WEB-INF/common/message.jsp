<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--
			  The block below is for displaying success messages. be sure to remove from session after display
			--%>

<c:if test="${not empty successMessages}">
<script type="text/javascript">
	    $(document).ready(function() {
		$("#foo").fadeIn(2000).fadeTo(5000, 1).fadeOut(2000);
	});
</script>
<p id="foo" class="info_message">
	<c:forEach var="msg" items="${successMessages}">
		<c:out value="${msg}" escapeXml="false" /><br />
	</c:forEach>
	</p>
	<c:remove var="successMessages" scope="session" />
</c:if>

<c:if test="${not empty errorMessages}">

	<p id="bar" class="alert_message">
	<c:forEach var="msg" items="${errorMessages}">
		<c:out value="${msg}" escapeXml="false" /> <br />
	</c:forEach>
	</p>
	<c:remove var="errorMessages" scope="session" />

</c:if>

