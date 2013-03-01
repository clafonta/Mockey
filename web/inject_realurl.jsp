<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%--@elvariable id="proxyInfo" type="com.mockey.ProxyServer"--%>
<c:set var="actionKey" value="conf_service" scope="request" />
<c:set var="pageTitle" value="Real URL Injection" scope="request" />
<c:set var="currentTab" value="inject" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>
<script type="text/javascript">
	$(function() {
		$("#inject-button").button().click(function() {
			var replacementValues = new Array();
			$.each($('input:text[name=replacement]'), function() {
				replacementValues.push($(this).val());
			       
			    });	
		    var match = $('#match');
		    $.post('<c:url value="/inject"/>', { match: match.val(), 'replacement[]':  replacementValues} ,function(data){
					if (data.result.success){
						
						 $.prompt(
								 '<div style=\"color:blue;\">Updated:</div> ' + data.result.success,
					                {
					                    callback: function (proceed) {
					                        if(proceed) document.location="<c:url value="/home" />";
					                    },
					                    buttons: {
					                        'OK': true
					                    }
					                });
					}else {
						$.prompt('<div style=\"color:red;\">Not updated:</div> ' + data.result.fail);
					}
				}, 'json' );
		});
	});
</script>
<div id="main2">
    <div>     
        <h1> URL Injection</h1></p> 
		<p>If Mockey connects to many <i>similar</i> testing and development environments (i.e. DEV, STAGE, UAT1 and UAT2), you could 
		be spending a lot of time copying and duplicating real URL(s) defined in your service definitions. In general, many sibling environments 
		have the same file paths but different domains and this is when <strong>injecting</strong> real URLs can come in handy. 
	    When you inject a pattern, you don't replace the original real URL. Instead a new real URL is added to your service(s). 
	    <a href="<c:url value="/help#url_injection"/>">More help</a>
	    </p>
	 </div>
	 
      <div class="centerform" style="padding-top:1em;margin-top:1em;">       
        <label for="match">Match this String pattern in the URL</label> 
        <input type="text" class="text ui-corner-all ui-widget-content" id="match" name="match" size="80"  value="">
        <div class="tinyfieldset">For example: string pattern 'qa1' in urls appqa1.domain.com, qa1test.domain.com, authqa1.domain.com</div>
        <label for="replacement">Replace and Inject with this String pattern</label> 
        <input type="text" class="text ui-corner-all ui-widget-content" id="replacement" name="replacement" size="80"  value="">
        <div class="tinyfieldset">For example: string pattern 'qa2' will add (inject) appqa2.domain.com, qa2test.domain.com, authqa2.domain.com to appropriate services.</div>
        <p align="right"><button id="inject-button">Inject real URLs</button></p>
      </div> 		    
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
