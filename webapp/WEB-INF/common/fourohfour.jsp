<<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
	<c:set var="pageTitle" value="404" scope="request"/>         
<%@include file="/WEB-INF/common/header.jsp" %>
<div id="content">
<h1>404 Error</h1>
<p class="info"><b>Page Not Found.</b> 
Maybe your page was moved or you typed something wrong. 
Let's start from the <a href="/home">beginning</a>.</p>
</div>
	<jsp:include page="/WEB-INF/common/footer.jsp" />