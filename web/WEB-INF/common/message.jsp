<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--
			  The block below is for displaying success messages. be sure to remove from session after display
			--%>

<c:if test="${not empty successMessages}">
	<script type="text/javascript">
		    $(document).ready(function() {
			$("#foo").fadeIn(1000).fadeTo(3000, 1).fadeOut(1000);
		});
	</script>
</c:if>
<c:if test="${not empty errorMessages}">
    <script type="text/javascript">
            $(document).ready(function() {
            $("#error-info").fadeIn(1000).fadeTo(3000, 1).fadeOut(1000); 
        });
    </script>
</c:if>
<span id="foo" class="hide">
	<mockey:message/>
</span>
<span id="updated" class="hide">
	Updated
</span>
<span id="deleted" class="hide">
	Deleted
</span>
<c:if test="${not empty errorMessages}">
	<span  id="error-info" class="hide">
		<c:forEach var="msg" items="${errorMessages}">
			<c:out value="${msg}" escapeXml="false" />
		</c:forEach>
	</span>
	<c:remove var="errorMessages" scope="session" />

</c:if>

