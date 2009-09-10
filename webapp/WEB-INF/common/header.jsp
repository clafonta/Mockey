<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Mockey - <c:out value="${requestScope.pageTitle}"/></title>
<meta http-equiv="content-type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="<c:url value="/style.css" />" />
<link rel="shortcut icon" href="<c:url value="/images/favicon.ico" />">
<script type="text/javascript" src="<c:url value="/javascript/util.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery-jeditable-min.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery-impromptu.2.7.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/xmlTidy.js" />"></script>

 <script LANGUAGE="Javascript">
<!---
function decision(message, url){
if(confirm(message)) location.href = url;
}
// --->

$(document).ready(function() {

	var makeExactlyAsTallAsItNeedsToBe = function(textArea) {
		var content = $(textArea).val() == undefined ? "" : $(textArea).val();
		var numOfRowsOfContent = 1;
		try { numOfRowsOfContent = content.match(/[^\n]*\n[^\n]*/gi).length; } catch(e) {}
		var maxSize = 40;
		textArea.rows = numOfRowsOfContent<maxSize?numOfRowsOfContent+1:maxSize;
	}
	$('textarea').each( function() {
		makeExactlyAsTallAsItNeedsToBe(this);
		$(this).keyup( function(e) {
			makeExactlyAsTallAsItNeedsToBe(this);
		});
		$(this).change( function(e) {
			makeExactlyAsTallAsItNeedsToBe(this);
		});
		$(this).bind( "reformatted", function(e) {
			makeExactlyAsTallAsItNeedsToBe(this);
		});
	});

});

$(document).ready(function() {
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

<ul id="minitabs">
    <li><a <c:if test="${currentTab == 'home'}">id ="current"</c:if> href="<c:url value="/home" />">All Services</a> | </li>
    <li><a href="<c:url value="/setup" />">Create Service</a> | </li>
    <li><a <c:if test="${currentTab == 'upload'}">id ="current"</c:if> href="<c:url value="/upload" />">Upload Service Definitions</a> | </li>
    <li><a <c:if test="${currentTab == 'export'}">id ="current"</c:if> href="<c:url value="/export" />">Export Service Definitions</a> | </li>
    <li><a <c:if test="${currentTab == 'history'}">id ="current"</c:if> href="<c:url value="/history" />">History</a> | </li>
    <li><a <c:if test="${currentTab == 'proxy'}">id ="current"</c:if> href="<c:url value="/proxy/settings" />">Proxy Settings</a> | </li>     
    <li><a id="flush" href="#">Flush</a> | </li>
    <li><a <c:if test="${currentTab == 'help'}">id ="current"</c:if> href="<c:url value="/help" />">Help</a></li></ul>
</div>