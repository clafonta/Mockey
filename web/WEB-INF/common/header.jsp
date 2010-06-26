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
		dropShadows: true                            // disable drop shadows
	});
	// 
	$.getJSON('<c:url value="/proxystatus" />', function(data) {
		if(data.result.proxy_enabled=='true'){
			$("#proxy_unknown").hide();
	       	$("#proxy_on").show();
       	    $("#proxy_off").hide();
	     }else {
	    	 $("#proxy_unknown").hide();
	    	 $("#proxy_on").hide();
	    	 $("#proxy_off").show(); 
	     }
	});
        $("#dialog-flush-confirm").dialog({
            resizable: false,
            height:120,
            modal: false,
            autoOpen: false
        });
            
        $('#flush').each( function() {
            $(this).click( function() {
            	$('#dialog-flush-confirm').show();
                $('#dialog-flush-confirm').dialog('open');
                    $('#dialog-flush-confirm').dialog({
                        resizable: false,
                        buttons: {
                          "Delete everything": function() {
                              document.location="<c:url value="/home?action=deleteAllServices" />";                          
                          }, 
                          Cancel: function(){
                              $(this).dialog('close');
                          }
                        }
                  }); 
                  return false;
                });
               $('#dialog-flush-confirm').dialog("destroy");
            });
});


</script>
</head>
<body>
<div id="container">

<div id="logo">
    <a href="<c:url value="/home" />" class="nav"><img style="vertical-align:middle; height:30px;" src="<c:url value="/images/logo.png" />" /><span style="vertical-align:middle;font-size:20px; text-shadow: 0px 0px 1px #FF0084;" class="nav power-link">Mockey</span></a>
    <span style="float:right;"><img style="height:60px; " src="<c:url value="/images/silhouette.png" />" /></span>
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
	<div id="dialog-flush-confirm" class="hide" title="Flush history">Are you sure? This will delete everything. You may want to <a href="<c:url value="/export" />">export your stuff</a> first.</div>
	<div id="topnav" style="margin-bottom:3em;width:100%;">
	<ul class="sf-menu" >
		<li class="<c:if test="${currentTab == 'home'}">current</c:if>"><a
			href="<c:url value="/home" />">Services  <span class="sf-sub-indicator"> &#187;</span></a>
			<ul>
				<li <c:if test="${currentTab == 'setup'}">class="current"</c:if>><a title="Service Setup - create new service"
					href="<c:url value="/setup" />">Create a Service</a></li>
				<li <c:if test="${currentTab == 'merge'}">class="current"</c:if>>
				<a title="Merge - combine services" href="<c:url value="/merge" />"
					style="">Merge Services</a></li>
				<li <c:if test="${currentTab == 'inject'}">class="current"</c:if>>
				<a title="Real URL injecting" href="<c:url value="/inject" />"
					style="">URL injecting</a></li>
			</ul>
		</li>
		<li <c:if test="${currentTab == 'upload'}">class="current"</c:if>>
			<a href="<c:url value="/upload" />">Import</a></li>
		<li <c:if test="${currentTab == 'export'}">class="current"</c:if>>
			<a href="<c:url value="/export" />">Export</a></li>
		<li <c:if test="${currentTab == 'history'}">class="current"</c:if>>
			<a href="<c:url value="/history" />">History</a></li>
		<li <c:if test="${currentTab == 'proxy'}">class="current"</c:if>>
			<a href="<c:url value="/proxy/settings" />">
			Proxy (<span id="proxy_unknown" class="tiny" >___</span><span id="proxy_on" class="tiny" 
			style="display: none;">ON</span><span id="proxy_off" class="tiny" style="display: none;">OFF</span>)</a></li>
		<li><a id="flush" href="#">Flush</a></li>
		<li <c:if test="${currentTab == 'help'}">class="current"</c:if>><a
			href="<c:url value="/help" />">Help</a></li>
	</ul>
	</div>
	<div style="border-bottom:1px solid #CCCCCC;"></div>
	
</div>

