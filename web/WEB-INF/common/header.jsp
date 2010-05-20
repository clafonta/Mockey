<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html lang="en">
<head>
<title>Mockey - <c:out value="${requestScope.pageTitle}"/></title>
<meta http-equiv="content-type" content="text/html; charset=iso-8859-1">
<link rel="shortcut icon" href="<c:url value="/images/favicon.ico" />">
<link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/superfish.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/jquery-ui-1.8.1.custom/css/flick/jquery-ui-1.8.1.custom.css" />" />
<script type="text/javascript" src="<c:url value="/javascript/util.js" />"></script>
<script type="text/javascript" src="<c:url value="/jquery-ui-1.8.1.custom/js/jquery-1.4.2.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/jquery-ui-1.8.1.custom/js/jquery-ui-1.8.1.custom.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery-jeditable-min.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery-impromptu.2.7.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery.textarearesizer.compressed.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/superfish.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/hoverIntent.js" />"></script>

<script LANGUAGE="Javascript">
<!---
function decision(message, url){
if(confirm(message)) location.href = url;
}
// --->


$(document).ready(function() {
	$('textarea.resizable:not(.processed)').TextAreaResizer();
	$('ul.sf-menu').superfish({
		delay:       1000,                            // one second delay on mouseout
		animation:   {opacity:'show',height:'show'},  // fade-in and slide-down animation
		speed:       'fast',                          // faster animation speed
		autoArrows:  false,                           // disable generation of arrow mark-up
		dropShadows: false                            // disable drop shadows
	});
	// 
	$.getJSON('<c:url value="/proxystatus" />', function(data) {
		if(data.result.proxy_enabled=='true'){
       	   $("#proxy_on").show();
       	   $("#proxy_off").hide();
	     }else {
	    	 $("#proxy_on").hide();
	    	 $("#proxy_off").show(); 
	     }
	});
	
    $('#flush').click(
        function () {
             $.prompt(
                    'Are you sure? This will delete everything. You may want to <a href="<c:url value="/export" />">export your stuff</a> first.', {
                        callback: function (proceed) {
                            if(proceed) document.location="<c:url value="/home?action=deleteAllServices" />";
                        },
                        buttons: {
                            'Delete Everything': true,
                            Cancel: false
                        }
                    }
                );
        })
});


</script>
</head>
<body>
<div id="container">

<div id="logo">
    <a href="<c:url value="/home" />"><img src="<c:url value="/images/logo.png" />" /></a>
    
	<%@ include file="/WEB-INF/common/message.jsp"%>
	<%
	String ua = request.getHeader( "User-Agent" );
	boolean isFirefox = ( ua != null && ua.indexOf( "Firefox/" ) != -1 );
	boolean isMSIE = ( ua != null && ua.indexOf( "MSIE 6.0" ) != -1 );
	response.setHeader( "Vary", "User-Agent" );
	%>
	<% if( isMSIE ){ %>
	  <span class="alert_message" style="position:absolute; top:0; right:200; width:500px;">This isn't designed for <b>Internet Explorer 6.0</b>. You should use another browser.</span>
	<% } %>
	<div style="margin-bottom:3em;">
	<ul class="sf-menu ">
		<li class="<c:if test="${currentTab == 'home'}">current</c:if>"><a
			href="<c:url value="/home" />">Services  <img src="<c:url value="/images/nav-arrow-down.png" />" /></a>
			<ul>
				<li <c:if test="${currentTab == 'merge'}">class="current"</c:if>>
				<a title="Merge - combine services" href="<c:url value="/merge" />"
					style="">Merge Services</a></li>
				<li><a title="Service Setup - create new service"
					href="<c:url value="/setup" />">Create a Service</a></li>
			</ul>
		</li>
		<li <c:if test="${currentTab == 'upload'}">class="current"</c:if>>
			<a href="<c:url value="/upload" />">Import</a></li>
		<li <c:if test="${currentTab == 'export'}">class="current"</c:if>>
			<a href="<c:url value="/export" />">Export</a></li>
		<li <c:if test="${currentTab == 'history'}">class="current"</c:if>>
			<a href="<c:url value="/history" />">History</a></li>
		<li <c:if test="${currentTab == 'proxy'}">class="current"</c:if>>
			<a href="<c:url value="/proxy/settings" />">Proxy (<span
			class="tiny" id="proxy_on" style="display: none;">ON</span><span
			id="proxy_off" class="tiny" style="display: none;">OFF</span>)</a></li>
		<li><a id="flush" href="#">Flush</a></li>
		<li <c:if test="${currentTab == 'help'}">class="current"</c:if>><a
			href="<c:url value="/help" />">Help</a></li>
	</ul>
	</div>
	<div style="border-bottom:1px solid #CCCCCC;"></div>
</div>
