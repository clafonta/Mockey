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
<p class="tiny">
<strong>Why Tags?</strong>
Each tag will be applied to each service and scenario. This can be useful when merging different files and you want to keep track on the when and where things came from.
</p>
<form class="centerform" action="<c:url value="/upload"/>" method="POST" enctype="multipart/form-data">
	<fieldset>
		
	    <h4>Your Definition File</h4>
	    <p><input class="text ui-corner-all ui-widget-content" type="file" name="file" size="50"/></p>
	    
	    
	    <p>Tag it (Optional) <input title="Add tags here, seperated with spaces. Optional." class="blur text ui-corner-all ui-widget-content" type="test" name="taglist" size="50"/></p>
	    <p align="right"><button id="upload-file">Import service definitions</button></p>
	</fieldset>
</form>

</div>
<c:if test="${not empty conflicts}">
	<h2 class="highlight">Conflicts</h2>
	<ul class="conflict_message upload-result">
		<c:forEach var="conflict" begin="0" items="${conflicts}">
			<li><c:out value="${conflict}" escapeXml="false" /></li>
		</c:forEach>
	</ul>
</c:if> 
<c:if test="${not empty additions}">
	<h2>Additions</h2>
	<ul class="addition_message upload-result">
		<c:forEach var="addition" begin="0" items="${additions}">
			<li><c:out value="${addition}" escapeXml="false" /></li>
		</c:forEach>
	</ul>
</c:if>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
