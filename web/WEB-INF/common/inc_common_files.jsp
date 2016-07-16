<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%@ taglib prefix="mockey-tag" tagdir="/WEB-INF/tags" %>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/hoverbox.css" />" media="screen, projection" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/superfish.css" />" />
<!--  We version the style, just to be sure users don't see the cached version. -->
<link rel="stylesheet" type="text/css" href="<c:url value="/css/style.css" />?version=<mockey-tag:version/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/jquery-ui-1.8.1.custom/css/flick/jquery-ui-1.8.1.custom.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/javascript/fileuploader/fileuploader.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/icomoon/style.css" />" />
<%
// Only for DEV. This polls every few seconds and will refressh this page's CSS
//<script type="text/javascript" src="<c:url value="/javascript/cssrefresh.js" />"></script>
%>
<script type="text/javascript" src="<c:url value="/javascript/util.js" />"></script>
<script type="text/javascript" src="<c:url value="/jquery-ui-1.8.1.custom/js/jquery-1.4.2.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/jquery-ui-1.8.1.custom/js/jquery-ui-1.8.1.custom.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery-jeditable-min.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery-impromptu.2.7.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery.textarearesizer.compressed.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/superfish.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/hoverIntent.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/jquery.hint.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/fileuploader/fileuploader.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/beautify.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/beautify-html.js" />"></script>
<script type="text/javascript" src="<c:url value="/javascript/chart.min.js" />"></script>