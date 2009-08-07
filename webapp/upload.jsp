<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Upload" scope="request" />
<c:set var="currentTab" value="upload" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
<div id="main">
<h3>Upload your service definition(s):</h3>
<%@ include file="/WEB-INF/common/message.jsp"%>
<form id="multi_form" style="margin-left: 20%; margin-right: 20%;"
	action="<c:url value="/upload"/>" method="POST"
	enctype="multipart/form-data">
<p><input class="normal" type="file" name="file" /></p>
<input type="submit" name="upload" value="Upload" class="button" /> <a
	href="<c:url value="/home" />">Cancel</a></form>
<c:if test="${not empty conflicts}">
	<h2 class="highlight">Conflicts</h2>
	<ul class="conflict_message">
		<c:forEach var="conflict" begin="0" items="${conflicts}">
			<li><c:out value="${conflict}" escapeXml="false" /></li>
		</c:forEach>
	</ul>
</c:if> 
<c:if test="${not empty additions}">
	<h2>Additions</h2>
	<ul>
		<c:forEach var="addition" begin="0" items="${additions}">
			<li><c:out value="${addition}" escapeXml="false" /></li>
		</c:forEach>
	</ul>
</c:if>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
