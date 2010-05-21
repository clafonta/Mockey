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
<h1>Import</h1>
<p>Import your service definition(s) here. Import will try to <i>merge</i> services already defined with services defined in your import file. If you're not sure about this, 
you should <a href="">Export</a> your service definitions first. If things go bad for you, then you can 
<strong>Flush</strong> and re-import your saved definitions file.</p>
<form class="centerform" action="<c:url value="/upload"/>" method="POST" enctype="multipart/form-data">
	<fieldset>
		
	    <p><input class="normal" type="file" name="file" size="50"/></p>
	    <p align="right"><button id="upload-file">Import service definitions</button></p>
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
