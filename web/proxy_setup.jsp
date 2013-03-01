<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%--@elvariable id="proxyInfo" type="com.mockey.ProxyServer"--%>
<c:set var="actionKey" value="conf_service" scope="request" />
<c:set var="pageTitle" value="Proxy Settings" scope="request" />
<c:set var="currentTab" value="proxy" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>
<script type="text/javascript">
	$(function() {
		$(".proxy-enable").click(
				function() {
					 var proxyEnabled = this.id.split("_")[1];
			         proxyUrl = $("#proxyUrl"),
			         proxyUsername = $("#proxyUsername"),
			         proxyPassword = $("#proxyPassword");
			         if(proxyEnabled=='true'){
			        	 
			        	 $("#proxy_true").removeClass('response_not').addClass('response_set');
			        	 $("#proxy_false").removeClass('response_set').addClass('response_not');
			        	 // Nav menu items
			        	 $("#proxy_on").show(); 
			        	 $("#proxy_off").hide();
			        	 
				     }else {
				    	 $("#proxy_false").removeClass('response_not').addClass('response_set');
			        	 $("#proxy_true").removeClass('response_set').addClass('response_not');
			        	 // Nav menu items
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
<div id="main2">
    <%@ include file="/WEB-INF/common/message.jsp"%>     
    <p><h1>Proxy Settings</h1></p> 
    <div class="parentform">
       			<p>If Mockey connects to services via some proxy server, here's the place to pipe through it. Enter information and enable.</p>
                <p>
					<c:choose>
					<c:when test='${proxyInfo.proxyEnabled}'>
						<c:set var="off_class" value="response_not" />
						<c:set var="on_class" value="response_set" />
					</c:when>
					<c:otherwise>
						<c:set var="off_class" value="response_set" />
						<c:set var="on_class" value="response_not" />
					</c:otherwise>
					</c:choose> <a href="#" id="proxy_true" class="proxy-enable ${on_class}"
					onclick="return false;">&nbsp;ON&nbsp;</a> <a href="#" id="proxy_false"
					class="proxy-enable ${off_class}" onclick="return false;">OFF</a>
                </p>
        <fieldset>        
          <label for="proxyUrl">Proxy URL</label> 
          <input type="text" placeholder="Your company proxy server." class="text ui-corner-all ui-widget-content" id="proxyUrl" name="proxyUrl" size="80"  value="<c:out value="${proxyInfo.proxyUrl}"/>" />
          <div class="tinyfieldset">Typically, this is your corporate proxy server.</div>       
                <label for="proxyUsername">Proxy username</label>  
                <input type="text" style="width:200px;" placeholder="Username if needed." class="text ui-corner-all ui-widget-content" id="proxyUsername" name="proxyUsername" maxlength="20" size="20" value="" /> 
                <div class="tinyfieldset">Username is <strong>not</strong> shown after a page refresh and not available in Export for security reasons. </div>
                <label for="proxyPassword">Proxy password</label>  
                <input type="password" style="width:200px;"placeholder="Password if needed." class="text ui-corner-all ui-widget-content" id="proxyPassword" name="proxyPassword" maxlength="20" size="20" value="" />
                <div class="tinyfieldset">Password is <strong>not</strong> shown after a page refresh and not available in Export for security reasons.  </div>
                <div id="proxy_message" class="info_message tiny"><strong>Note:</strong> the last time these settings were enabled,
                <c:choose><c:when test='${!empty proxyInfo.proxyUsername}'><strong>proxy name</strong> was provided</c:when><c:otherwise><strong>proxy name</strong> was <strong>not</strong> provided</span></c:otherwise></c:choose> and
                <c:choose><c:when test='${!empty proxyInfo.proxyPassword}'><strong>proxy password</strong> was provided</c:when><c:otherwise><strong>proxy password</strong> was <strong>not</strong> provided</span></c:otherwise></c:choose>.
                </div>
	    </fieldset>
       
    </div>    
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
