<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html lang="en">
<head>
<title>Mockey - <c:out value="${requestScope.pageTitle}"/></title>
<meta http-equiv="content-type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="<c:url value="/style.css" />" />
<link rel="shortcut icon" href="<c:url value="/images/favicon.ico" />">
<script type="text/javascript" src="<c:url value="/javascript/util.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery-jeditable-min.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery-impromptu.2.7.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery.textarearesizer.compressed.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/xmlTidy.js" />"></script>

<script LANGUAGE="Javascript">
<!---
function decision(message, url){
if(confirm(message)) location.href = url;
}
// --->


$(document).ready(function() {
	$('textarea.resizable:not(.processed)').TextAreaResizer();
	
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
    <img src="<c:url value="/images/logo.png" />" />
    

<%
String ua = request.getHeader( "User-Agent" );
boolean isFirefox = ( ua != null && ua.indexOf( "Firefox/" ) != -1 );
boolean isMSIE = ( ua != null && ua.indexOf( "MSIE 6.0" ) != -1 );
response.setHeader( "Vary", "User-Agent" );
%>
<% if( isMSIE ){ %>
  <span class="alert_message" style="position:absolute; top:0; right:200; width:500px;">This isn't designed for <b>Internet Explorer 6.0</b>. You should use another browser.</span>
<% } %>

<ul id="minitabs">
    <li><a <c:if test="${currentTab == 'home'}">id ="current"</c:if> href="<c:url value="/home" />">Services</a></li>
    <li>(<a href="<c:url value="/setup" />" class="tiny" style="color:red;">Create</a>) | </li>
    <li><a <c:if test="${currentTab == 'upload'}">id ="current"</c:if> href="<c:url value="/upload" />">Upload</a> | </li>
    <li><a <c:if test="${currentTab == 'export'}">id ="current"</c:if> href="<c:url value="/export" />">Export</a> | </li>
    <li><a <c:if test="${currentTab == 'history'}">id ="current"</c:if> href="<c:url value="/history" />">History</a> | </li>
    <li><a <c:if test="${currentTab == 'proxy'}">id ="current"</c:if> href="<c:url value="/proxy/settings" />">Proxy Settings</a> | </li>     
    <li><a id="flush" href="#">Flush</a> | </li>
    <li><a <c:if test="${currentTab == 'help'}">id ="current"</c:if> href="<c:url value="/help" />">Help</a></li></ul>
</div>
