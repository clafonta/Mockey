<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey-tag" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="Upload" scope="request" />
<c:set var="currentTab" value="upload" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>
<script>
$(document).ready( function() {
    $('#tabs').tabs().hide();
	$('#tabs:not(:first)').hide();
    $('#tabs:first').fadeIn('fast');
    $("#upload-file").button();
    $("#upload-via-url").button();
});
</script>
<div id="main2">

<%@ include file="/WEB-INF/common/message.jsp"%>
<div>
	<h1>Export</h1>
	<p>
	<a href="<c:url value="/export?download"/>">Download</a> this Mockey's definition or view it here: <mockey-tag:baseAbsolutePath pathArg="/export"/>  
	
	</div>
	</p>
	<h1>Import</h1>
	<p>Import your Mockey service definition XML file from a local file directory or from a URL. Import will try to <i>merge</i> services already defined with services defined in your import file. If you're not sure about this, 
	you should <a href="">Export</a> your service definitions first. If things go bad for you, then you can 
	<strong>Flush</strong> and re-import your saved definitions file.</p>
	<p class="tiny">
		<strong>Why Tags?</strong>
		Each tag will be applied to each Service, Scenario, and Plan. This can be useful when merging different files and you want to keep track on the when and where things came from.
	</p>

	<div id="tabs" class="tiny" style="margin-left: 10%; margin-right: 10%; margin-top:30px;">
		<ul>
			<li><a href="#tabs-1">File upload</a></li>
			<li><a href="#tabs-2">URL</a></li>
		</ul>
	  	<div id="tabs-1">
		  	<form style="border:none;"  action="<c:url value="/upload"/>" method="POST" enctype="multipart/form-data">
		  	<p>Local file<input class="text ui-corner-all ui-widget-content" type="file" name="file" size="50"/></p>
		  	<p>Tag it (Optional) <input placeholder="Add tags here, seperated with spaces. Optional." class="text ui-corner-all ui-widget-content" type="test" name="taglist" size="50"/></p>
		  	<p align="right"><button id="upload-file" name="viaLocal">Upload local definitions file</button></p>
		  	</form>
	  	</div>
	  	<div id="tabs-2">
		  	<form style="border:none;" action="<c:url value="/upload"/>" method="POST">
		  	URL<input class="text ui-corner-all ui-widget-content" type="text" name="url" placeholder="Paste your URL here: http://localhost:8888/Mockey/export" size="50"/>
		  	Tag it (Optional) <input placeholder="Add tags here, seperated with spaces. Optional." class="text ui-corner-all ui-widget-content" type="test" name="taglist" size="50"/>
		  	<p align="right"><button id="upload-via-url" name="viaUrl">Upload definitions via URL</button></p>
		  	</form>
	  	</div>
	</div>							  	
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
