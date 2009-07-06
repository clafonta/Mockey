<%@ taglib prefix="c"  uri="http://java.sun.com/jstl/core" %> 
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="actionKey" value="service_history" scope="request" />
<c:set var="pageTitle" value="History" scope="request"/>         
<jsp:include page="/WEB-INF/common/header.jsp" />   
<div id="main">
    <h1>Service History: <span class="highlight"><c:out value="${mockservice.serviceName}"/></span></h1>    
    <%@ include file="/WEB-INF/common/inc_action_links.jsp"%>    
    <div>
        <%@ include file="/WEB-INF/common/inc_history_poller.jsp"%>
    </div>   
<jsp:include page="/WEB-INF/common/footer.jsp" />