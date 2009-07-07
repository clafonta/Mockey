<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="MuWa - RMS Client - Home" scope="request" />
<c:set var="currentTab" value="home" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />

<div>
<p><strong>Oops...</strong>something bad happened. You should return to the homepage. <a href="<c:url value="/" />">Click here</a>.</p>
</div>

<jsp:include page="/WEB-INF/common/footer.jsp" />
