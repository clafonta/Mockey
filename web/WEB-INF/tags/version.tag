<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ tag body-content="empty" %> 
<%@ tag trimDirectiveWhitespaces="true" %>
<%
  String ver = com.mockey.runner.JettyRunner.class.getPackage().getImplementationVersion();
  if(ver==null) {
     ver = "debug-mode";
  }
%><%= ver %> 