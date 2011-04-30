<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Console" scope="request" />
<c:set var="currentTab" value="console" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>

<div id="main">

<div>
<h1>Console</h1>
<p>Keep this window open to see Mockey logging output. Useful for debugging Mockey if things go wrong.
</p> 
<textarea id="console" class="consoleTextArea">Not yet implemented</textarea>
</div>

</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
