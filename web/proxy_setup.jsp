<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%--@elvariable id="proxyInfo" type="com.mockey.ProxyServer"--%>
<c:set var="actionKey" value="conf_service" scope="request" />
<c:set var="pageTitle" value="Proxy Settings" scope="request" />
<c:set var="currentTab" value="proxy" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>
<script type="text/javascript">
	$(function() {
		$("#radio-set").buttonset();
		$(".boo").click(
				function() {
					 var proxyEnabled = this.id.split("_")[1];
			         proxyUrl = $("#proxyUrl"),
			         proxyUsername = $("#proxyUsername"),
			         proxyPassword = $("#proxyPassword");
			         if(proxyEnabled=='true'){
			        	 
			        	 $("#proxy_on").show();
			        	 $("#proxy_off").hide();
			        	 
				     }else {
				    	 $("#proxy_on").hide();
				    	 $("#proxy_off").show();
			        	 
				     }
				     $('#proxy_message').hide();
					 $.post('<c:url value="/proxy/settings"/>', { proxyPassword: proxyPassword.val(), proxyUsername: proxyUsername.val(),
							proxyUrl:  proxyUrl.val(),  proxyEnabled: proxyEnabled} ,function(data){
								if (data.result.success){
									$('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
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
				<div id="radio-set" style="margin-bottom: 1em;">
		            <input type="radio" id="radio_true" name="proxyEnabled" class="boo"  <c:if test='${proxyInfo.proxyEnabled}'>checked</c:if> /><label for="radio_true">Proxy enabled</label> 
	                <input type="radio" id="radio_false" name="proxyEnabled" class="boo" <c:if test='${!proxyInfo.proxyEnabled}'>checked</c:if> /> <label for="radio_false">Proxy not enabled</label>
                </div>
                <div class="tinyfieldset">If Mockey connects to services via some proxy server, here's the place to pipe through it. Enter information and enable.</div>
                <label for="proxyUrl">Proxy URL</label> 
                <input type="text" class="text ui-corner-all ui-widget-content" id="proxyUrl" name="proxyUrl" size="80"  value="<c:out value="${proxyInfo.proxyUrl}"/>" />
                <div class="tinyfieldset">Typically, this is your corporate proxy server.</div>       
                <label for="proxyUsername">Proxy username</label>  
                <input type="text" style="width:200px;"  class="text ui-corner-all ui-widget-content" id="proxyUsername" name="proxyUsername" maxlength="20" size="20" value="" /> 
                <div class="tinyfieldset">Username is <strong>not</strong> shown after a page refresh and not available in Export for security reasons. </div>
                <label for="proxyPassword">Proxy password</label>  
                <input type="password" style="width:200px;" class="text ui-corner-all ui-widget-content" id="proxyPassword" name="proxyPassword" maxlength="20" size="20" value="" />
                <div class="tinyfieldset">Password is <strong>not</strong> shown after a page refresh and not available in Export for security reasons.  </div>
                <div id="proxy_message" class="info_message tiny"><strong>Note:</strong> the last time these settings were enabled,
                <c:choose><c:when test='${!empty proxyInfo.proxyUsername}'><span style="color:blue;"><strong>proxy name</strong> was provided</span></c:when><c:otherwise><span style="color:yellow;"><strong>proxy name</strong> was <strong>not</strong> provided</span></c:otherwise></c:choose> and
                <c:choose><c:when test='${!empty proxyInfo.proxyPassword}'><span style="color:blue;"><strong>proxy password</strong> was provided</span></c:when><c:otherwise><span style="color:yellow;"><strong>proxy password</strong> was <strong>not</strong> provided</span></c:otherwise></c:choose>.
                </div>
	    </fieldset>
       
    </div>    
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
