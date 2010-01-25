<<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
	<c:set var="pageTitle" value="403" scope="request"/>         
<%@include file="/WEB-INF/common/header.jsp" %>
<div id="content">
<h1>403 FORBIDDEN</h1>
<p class="info">You are not authorized to access the page that you requested.</p>
</div>
	<jsp:include page="/WEB-INF/common/footer.jsp" />
