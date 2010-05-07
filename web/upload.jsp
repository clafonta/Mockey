<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Upload" scope="request" />
<c:set var="currentTab" value="upload" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>
<script type="text/javascript">
	$(function() {
		$("#upload-file").button();
	});
</script>
<div id="main">

<%@ include file="/WEB-INF/common/message.jsp"%>
<div>
<form id="multi_form" style="margin-left: 20%; margin-right: 20%;"
	action="<c:url value="/upload"/>" method="POST"
	enctype="multipart/form-data">
	<fieldset>
		<label>Upload your service definition(s):</label>
	    <p><input class="normal" type="file" name="file" /></p>
	    <button id="upload-file">Update service</button>
	    <a href="<c:url value="/home" />">Cancel</a>
	</fieldset>
</form>
</div>
<c:if test="${not empty conflicts}">
	<h2 class="highlight">Conflicts</h2>
	<ul id="simple" class="conflict_message">
		<c:forEach var="conflict" begin="0" items="${conflicts}">
			<li><c:out value="${conflict}" escapeXml="false" /></li>
		</c:forEach>
	</ul>
</c:if> 
<c:if test="${not empty additions}">
	<h2>Additions</h2>
	<ul id="simple" class="addition_message">
		<c:forEach var="addition" begin="0" items="${additions}">
			<li><c:out value="${addition}" escapeXml="false" /></li>
		</c:forEach>
	</ul>
</c:if>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
