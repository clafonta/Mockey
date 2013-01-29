<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%@ taglib prefix="mockey-tag" tagdir="/WEB-INF/tags" %>
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
		$('#prettyPrintMyServiceJsonRules').click ( function() {
	        var txt = $('#request_inspector_json_rules').val();
	    	var pTxt = js_beautify(txt);
	    	$('#request_inspector_json_rules').val(pTxt);
	        return false;
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
			        requestInspectorName = $("#request_inspector_name"),
			        requestInspectorJsonRules = $("#request_inspector_json_rules"),
			        requestInspectorJsonRulesEnableFlag = $("#request_inspector_json_rules_enable_flag"),
			        tag = $('#tag'),
			        lastVisit = $("#last_visit");
			        
			 
			   $.post('<c:url value="/setup"/>', { serviceName: serviceName.val(), serviceId: serviceId.val(), tag: tag.val(),
				   'realServiceUrl[]':  realServiceUrlValues, url: url.val(), lastVisit: lastVisit.val(), 
				   requestInspectorName: requestInspectorName.val(), requestInspectorJsonRules: requestInspectorJsonRules.val(), 
				   requestInspectorJsonRulesEnableFlag: requestInspectorJsonRulesEnableFlag.is(':checked'), hangTime: hangtime.val() } ,function(data){
					   
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
						if(data.result.requestInspectorJsonRules){
							$("#request_inspector_json_rules").addClass('ui-state-error');
							message = message + '<div>' + data.result.requestInspectorJsonRules +'</div>';
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
    <span style="" class="hhButton"><a href="${returnToServiceUrl}">
   
        <i aria-hidden="true" class="icon-arrow-left"></i>
        &nbsp;Return to main page
   
    </a></span>
    <h1>Service Setup</h1>  
    <div class="parentform">
        <c:if test="${!empty mockservice.id}">
            <input type="hidden" id="service_id" name="serviceId" value="<c:out value="${mockservice.id}"/>" />
        </c:if>
        <fieldset>
				<label for="service_name">Service Name:</label>
	            <input type="text" id="service_name" placeholder="For example 'Customer Invoice Service'" class="text ui-corner-all ui-widget-content" name="service_name" maxlength="1000" size="90%" value="<c:out value="${mockservice.serviceName}"/>" />
	            <div class="tinyfieldset">Use a self descriptive name. For example, if you were to use this for 'authentication' testing, then call it 'Authentication'.</div>
	            <hr>
	            <label for="service_url">Mock Service URL: </label>
	            <input type="text" id="service_url" placeholder="For example: 'customer/{token}/invoice' or 'customer/list/invoice'" class="text ui-corner-all ui-widget-content" name="service_url" maxlength="1000" size="90%" value="<c:out value="${mockservice.url}"/>" />
	            <div class="tinyfieldset">You can make up a new but unique <i>mock</i> URL to map to the real URL(s). Your mock URL will look like this: 
	               <div><input id="mock-url-init" class="invisiblefield" value="<mockey:url value="${mockservice.url}" />"/><input id="mock-url" class="invisiblefield hide"  value="<mockey:url value="" />"/>
	               </div>
	               <p class="info_message">
	               <b>NOTE:</b> This has some <strong>RESTful</strong> support. If you define your mock URL as <span class="code_text">http://www.someservice.com/customer/{TOKEN ID}</span> (with curly braces for token identifiers), 
	               then an application requesting <span class="code_text">http://www.someservice.com/customer/123</span> will be mapped to this service. In fact, if you create
	               a Scenario match argument with the appropriate token (in this example, '123'), then Scenario with match argument 123 will be returned. 
	               <br />
	               <br />
	               See <a href="#evaluation_rules_api">Evaluation Rules in JSON</a> in the Help page for directions. 
	               </p>
	            </div>
	            <div id="invalidUrl" style="display:none;color:red;"><span>Note:</span> </div>
	            <hr>
	            <label for="service_url" id="real_service_url_label">Real service URLs</label>
	            <div id="real_service_url_list">
					<c:forEach var="realServiceUrl" items="${mockservice.realServiceUrls}">
						<input type="text" id="service_real_url" class="text ui-corner-all ui-widget-content" name="realServiceUrl" maxlength="1000" size="90%" value="${realServiceUrl}" />
					</c:forEach>
				</div>
				<input type="text" id="service_real_url" placeholder="For example: 'http://www.reddit.com'" class="text ui-corner-all ui-widget-content" name="realServiceUrl" maxlength="1000" size="90%" value="" />
					<a title="Add row" id="add-row" href="#" style="color:red;text-decoration:none;font-size:1em;">+</a>
                <div class="tinyfieldset">You'll need a real service URL if you want Mockey to serve as a proxy to record transactions between your application and the real service.</div>
                <hr>
                <label for="service_url">Hang Time: </label>
                <div>
                <input type="text" id="hang_time" class="text ui-corner-all ui-widget-content" style="width:250px;" name="hangtime" maxlength="20" size="30px" value="<c:out value="${mockservice.hangTime}"/>" />
                </div>
                <div class="tinyfieldset">The delay time in milliseconds. Mockey will wait for this long before returning a response.</div>
                <hr>
                <label for="service_url">Tag(s):</label> 
                <input type="text" id="tag" class="text ui-corner-all ui-widget-content" name="tag" placeholder="For example: stage iosMay"  maxlength="1000" size="90%" value="<c:out value="${mockservice.tag}"/>" />
                <div class="tinyfieldset"><strong>Optional.</strong> Add 1 or more tags seperated with spaces. Tags can be useful for all kinds of things. Use it as meta-data for your services, plans, scenarios, etc.</div>
                <hr>
                <label for="service_url" class="strong">Last Visit:</label>
                <div>
                <input type="text" id="last_visit" placeholder="mm/dd/yyyy" class="text ui-corner-all ui-widget-content" style="width:250px;" name="lastvisit" maxlength="20" size="30px" value="<mockey-tag:prettyDate lastVisit="${mockservice.lastVisit}"/>" />
                </div>                                
                <div class="tinyfieldset"><i>Purely informational.</i> The last time this service was called.</div>                
               <hr>
               <label>Evaluation Rules in JSON</label>
               <span style="float:right;" >Pretty format:  <a href="#" class="blue" id="prettyPrintMyServiceJsonRules">JSON</a></span>
               <div style="margin-bottom:5px; margin-top:10px;">
               
               <textarea name="request_inspector_json_rules" placeholder="See the Help section for directions. " id="request_inspector_json_rules" class="text ui-widget-content ui-corner-all resizable" rows="8"><c:out value="${mockservice.requestInspectorJsonRules}"/></textarea>
               </div>
               <input type="checkbox" name="request_inspector_json_rules_enable_flag" id="request_inspector_json_rules_enable_flag" value="true" <c:if test="${mockservice.requestInspectorJsonRulesEnableFlag}">checked</c:if> />
			   Check this box to run the Evaluation Rules.  Uncheck this box if you want to ignore Evaluation Rules. 
			   <div class="tinyfieldset" style="margin-top:5px;"><strong>Optional.</strong> For more on this, read the <a href="<c:url value="/help#evaluation_rules_api"/>">help</a> section.
	           </div>
               <hr>
               <!-- 
               <label>Request Inspector Java Class Name</label>
               <div>
	           <select style="width:400px;" id="request_inspector_name" name="requestInspectorName">
	           		<option value="" <c:if test="${mockservice.requestInspectorName eq ''}">selected="selected"</c:if>>[select]</option>
	           		<c:forEach var="riItem" items="${requestInspectorList}">
	                        <option value="${riItem}" <c:if test="${mockservice.requestInspectorName eq riItem}">selected="selected"</c:if>>${riItem}</option>
	                </c:forEach>
	           </select>
	           </div>
	           <div class="tinyfieldset" style="margin-top:5px;"><strong>Optional.</strong> For those really complicated rules that are not easy to define in JSON, you can implement 
	           your validation in Java. Assign your custom request inspector Java implementation to this service. For more on this, read the <a href="<c:url value="/help#inspector_in_java"/>">help</a> section.
	           </div>
	            -->
                                
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