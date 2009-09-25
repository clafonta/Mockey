<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="500" scope="request"/>         
<%@include file="/WEB-INF/common/header.jsp" %>  
<div id="content">
<h2>500 Error</h2>
<p class="info"><b>System Error.</b> Whoa! That's not supposed to happen. Want to try <a href="<c:url value="/home" />">again</a>?</p>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
