<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="actionKey" value="edit_service" scope="request" />
<c:set var="pageTitle" value="Configure" scope="request" />
<c:set var="currentTab" value="setup" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>
<script type="text/javascript"><!--
   

	$(function() {
		
		$('#add-row').click(function() {
			$('#real_service_url_list').append('<input type=\"text\" id=\"service_real_url\" class=\"text ui-corner-all ui-widget-content\" name=\"realServiceUrl\" maxlength=\"100\" size=\"90%\" value=\"\" />');
			
		});

		$(function() {

			  var inputTxt = $('#mock-url').val();  

			  $('#service_url').bind('keyup keypress', function() { 
				  
			      $('#mock-url')[0].value = inputTxt + $(this)[0].value;
			      $('#mock-url-init').hide();
			      $('#mock-url').show();
			      
			  });
			});
							
		$('#update-service')
		    .button()
		    .click(function() {
			    
		    	$.prompt.setDefaults({
			        opacity:0.2
			    });
		    	var realServiceUrlValues = new Array();
				$.each($('input:text[name=realServiceUrl]'), function() {
					realServiceUrlValues.push($(this).val());
				       
				    });	
			    var serviceId = $('#service_id'),
			        url = $('#service_url');
			        realUrl = $("#service_real_url"),
			        serviceName = $("#service_name"),
			        hangtime = $("#hang_time"),
			        serviceContentType = $("#service_http_content_type");
			 
			   $.post('<c:url value="/setup"/>', { serviceName: serviceName.val(), serviceId: serviceId.val(),
				   'realServiceUrl[]':  realServiceUrlValues, url: url.val(), httpContentType: serviceContentType.val(),
				   hangTime: hangtime.val() } ,function(data){
					   
					   if (data.result.redirect){
						   window.location.replace(data.result.redirect);
						   
					   }else {   
						var message = "";
						if(data.result.serviceName){
							$("#service_name").addClass('ui-state-error');
							  message = message + '<div>' + data.result.serviceName +'</div>';
							}
						if(data.result.urlMsg){
							$("#service_url").addClass('ui-state-error');
							message = message + '<div>' + data.result.urlMsg+'</div>';
						}
						if(data.result.serviceUrlMsg){
							$('input[name=realServiceUrl]').each( function(){$(this).addClass('ui-state-error')});
							message = message + '<div>' + data.result.serviceUrlMsg+'</div>' 
							                  + '<div style="color:red;" >' + data.result.serviceUrl + '</div>';
						}
						
						
						
					   	$.prompt('<div style=\"color:red;\">Not updated:</div> ' + message);
					   }

					   }, 'json' );
				
		});

		
	
		$('#delete-service')
		    .button()
		    .click(function() {
		    	 var serviceId = "${mockservice.id}";
			    $.prompt(
		                'Are you sure you want to delete this Service?',
		                {
		                    callback: function (proceed) {
		                        if(proceed) document.location="<c:url value="/setup" />?deleteService=true&serviceId="+ serviceId;
		                    },
		                    buttons: {
		                        'Delete Service': true,
		                        Cancel: false
		                    }
		                });
		});

	});
	
--></script>


<div id="main">
    
    <div class="result"></div>
    <c:choose>
	    <c:when test="${!empty mockservice.id}">
	        <c:url value="/home" var="returnToServiceUrl">
	          <c:param name="serviceId" value="${mockservice.id}" />                                                                               
	    	</c:url> 
	    </c:when>
	    <c:otherwise>
	    	<c:url value="/home" var="returnToServiceUrl"/>
	    </c:otherwise>
    </c:choose>
    <span style="float:right;"><a href="${returnToServiceUrl}">Return to main page</a></span>
    <h1>Service Setup</h1>  
    <div class="parentform">
        <c:if test="${!empty mockservice.id}">
            <input type="hidden" id="service_id" name="serviceId" value="<c:out value="${mockservice.id}"/>" />
        </c:if>
        <fieldset>
				<label for="service_name">Service name:</label>
	            <input type="text" id="service_name" class="text ui-corner-all ui-widget-content" name="service_name" maxlength="100" size="90%" value="<c:out value="${mockservice.serviceName}"/>" />
	            <div class="tinyfieldset">Use a self descriptive name. For example, if you were to use this for 'authentication' testing, then call it 'Authentication'.</div>
	            <label for="service_url">Mock service URL: </label>
	            <input type="text" id="service_url" class="text ui-corner-all ui-widget-content" name="service_url" maxlength="100" size="90%" value="<c:out value="${mockservice.url}"/>" />
	            <div class="tinyfieldset">You can make up a new but unique <i>mock</i> URL to map to the real URL(s). Your mock URL will look like this: 
	               <div><input id="mock-url-init" class="invisiblefield" value="<mockey:url value="${mockservice.url}" />"/><input id="mock-url" class="invisiblefield hide"  value="<mockey:url value="" />">
	               </div>
	            </div>
	            <label for="service_url" id="real_service_url_label">Real service URLs</label>
	            <div id="real_service_url_list">
					<c:forEach var="realServiceUrl" items="${mockservice.realServiceUrls}">
						<input type="text" id="service_real_url" class="text ui-corner-all ui-widget-content" name="realServiceUrl" maxlength="100" size="90%" value="${realServiceUrl}" />
					</c:forEach>
				</div>
				<input type="text" id="service_real_url" class="text ui-corner-all ui-widget-content" name="realServiceUrl" maxlength="100" size="90%" value="" />
					<a title="Add row" id="add-row" href="#" style="color:red;text-decoration:none;font-size:1em;">+</a>
                <div class="tinyfieldset">You'll need a real service URL if you want Mockey to serve as a proxy to record transactions between your application and the real service.</div>
                <label for="service_url">Hang time: </label>
                <input type="text" id="hang_time" class="text ui-corner-all ui-widget-content" style="width:100px;" name="hangtime" maxlength="20" size="30px" value="<c:out value="${mockservice.hangTime}"/>" />
                <div class="tinyfieldset">The delay time in milliseconds.</div>
                <label>HTTP header definition:</label>
	            <select id="service_http_content_type" name="httpContentType">
	                        <option value="" <c:if test="${mockservice.httpContentType eq ''}">selected="selected"</c:if>>[select]</option>
                            <option value="text/xml;" <c:if test="${mockservice.httpContentType eq 'text/xml;'}">selected="selected"</c:if>>text/xml;</option>
                            <option value="text/plain;" <c:if test="${mockservice.httpContentType eq 'text/plain;'}">selected="selected"</c:if>>text/plain;</option>
                            <option value="text/css;" <c:if test="${mockservice.httpContentType eq 'text/css;'}">selected="selected"</c:if>>text/css;</option>
                            <option value="application/json;" <c:if test="${mockservice.httpContentType eq 'application/json;'}">selected="selected"</c:if>>application/json;</option>
                            <option value="text/html;charset=utf-8" <c:if test="${mockservice.httpContentType eq 'text/html;charset=utf-8'}">selected="selected"</c:if>>text/html;charset=utf-8</option>
                            <option value="text/html; charset=ISO-8859-1" <c:if test="${mockservice.httpContentType eq 'text/html; charset=ISO-8859-1'}">selected="selected"</c:if>>text/html; charset=ISO-8859-1</option>
                            <!-- <option value="other" <c:if test="${mockservice.httpContentType eq 'other'}">selected="selected"</c:if>>other</option>  -->
                          </select>
	           <div class="tinyfieldset">For example: <span style="font-style: italic;">text/xml; utf-8</span>, <span
                                style="font-style: italic;">application/json;</span>, etc. </div>
	    </fieldset>
        <p align="right">
	        <c:choose>
	            <c:when test="${!empty mockservice.id}">
	                <button id="update-service">Update service</button>
	            </c:when>
	            <c:otherwise>
	                <button id="update-service">Create new service</button>
	            </c:otherwise>
	        </c:choose>
	        <c:if test="${!empty mockservice.id}">
	            <button id="delete-service">Delete</button>
	        </c:if>
	    </p>
    </div>
   
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />