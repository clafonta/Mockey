<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%--@elvariable id="proxyInfo" type="com.mockey.ProxyServer"--%>
<c:set var="actionKey" value="conf_service" scope="request" />
<c:set var="pageTitle" value="Proxy Settings" scope="request" />
<c:set var="currentTab" value="proxy" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>
<script type="text/javascript">
	$(function() {
		
		$("#radio1").buttonset().click(
				function() {
					
					 var proxyEnabled = $("input[name=proxyEnabled]:checked").index(), 
			         proxyUrl = $("#proxyUrl"),
			         proxyUsername = $("#proxyUsername"),
			         proxyPassword = $("#proxyPassword");
			         if(proxyEnabled==1){
			        	 proxyEnabled = true;
				     }
					 $.post('<c:url value="/proxy/settings"/>', { proxyPassword: proxyPassword.val(), proxyUsername: proxyUsername.val(),
							proxyUrl:  proxyUrl.val(),  proxyEnabled: proxyEnabled} ,function(data){
								if (data.result.success){
									$.prompt('<div style=\"color:red;\">Updated:</div> ' + data.result.success, { timeout: 2000});
								}else {
									$.prompt('<div style=\"color:red;\">Not updated:</div> ' + data.result.message);
								}
							}, 'json' );

					});
		
	});
	//<form action="<c:url value="/proxy/settings" />" method="POST">
</script>
<div id="main">
    <%@ include file="/WEB-INF/common/message.jsp"%>     
    <p><h1>Proxy Settings</h1></p> 
    <div class="parentform">
        <fieldset>
				<div id="radio1">
		            <input type="radio" id="radio1" name="proxyEnabled" value="true"  <c:if test='${proxyInfo.proxyEnabled}'>checked</c:if> /><label for="radio1">Proxy enabled</label> 
	                <input type="radio" id="radio2" name="proxyEnabled" value="false" <c:if test='${!proxyInfo.proxyEnabled}'>checked</c:if> /> <label for="radio2">Proxy not enabled</label>
                </div>
                <div class="tinyfieldset">If Mockey connects to services via some proxy server, here's the place to pipe through it. Enter information and enable.</div>
                <label for="proxyUrl">Proxy URL</label> 
                <input type="text" class="text ui-corner-all ui-widget-content" id="proxyUrl" name="proxyUrl" size="80"  value="<c:out value="${proxyInfo.proxyUrl}"/>" />
                <div class="tinyfieldset">Typically, this is your corporate proxy server.</div>       
                <label for="proxyUsername">Proxy username</label>  
                <input type="text" style="width:200px;"  class="text ui-corner-all ui-widget-content" id="proxyUsername" name="proxyUsername" maxlength="20" size="20" value="" />
                 <div class="tinyfieldset">We don't store the username on file. </div>
                <label for="proxyPassword">Proxy password</label>  
                <input type="password" style="width:200px;" class="text ui-corner-all ui-widget-content" id="proxyPassword" name="proxyPassword" maxlength="20" size="20" value="" />
                <div class="tinyfieldset">We don't store the password on file. </div>
          
	    </fieldset>
       
    </div>    
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
