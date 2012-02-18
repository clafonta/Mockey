<%@ tag import="java.util.*" %>
<%@ tag import="java.net.*" %>
<%@ tag import="com.mockey.storage.*" %>
<%@ tag import="com.mockey.model.*" %>
<%@ tag import="com.mockey.ui.*" %>
<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="pathArg" required="true"%>

<%

URL serverURLObj = new URL(request.getScheme(), // http
		request.getServerName(), // host
		request.getServerPort(), // port
        "");
request.setAttribute("baseUrl",serverURLObj.toString());

%>
<a href="${baseUrl}${pageContext.request.contextPath}${pathArg}">${baseUrl}${pageContext.request.contextPath}${pathArg}</a>