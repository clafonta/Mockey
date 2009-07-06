<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Mockey - <c:out value="${requestScope.pageTitle}"/></title>
<meta http-equiv="content-type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="<c:url value="/style.css" />" />
<link rel="shortcut icon" href="<c:url value="/images/favicon.ico" />">
<script type="text/javascript" src="<c:url value="/javascript/util.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/prototype.js" />"></script>
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
    <li><a <c:if test="${currentTab == 'proxy'}">id ="current"</c:if> href="<c:url value="/proxy/settings" />">Proxy Settings</a> | </li>    
    <li><a <c:if test="${currentTab == 'help'}">id ="current"</c:if> href="<c:url value="/help.jsp" />">Help</a></li>
</ul>
</div>