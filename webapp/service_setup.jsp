<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="actionKey" value="edit_service" scope="request" />
<c:set var="pageTitle" value="Configure" scope="request" />
<c:set var="currentTab" value="create" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>
<script type="text/javascript">
	$(function() {

		$("#accordion").accordion({
			active: false,
			collapsible: true
		});
	});
</script>	
<script type="text/javascript">
	$(function() {

		// a workaround for a flaw in the demo system (http://dev.jqueryui.com/ticket/4375), ignore!
		$("#dialog").dialog("destroy");

		var name = $("#scenario_name"),
		      match = $("#scenario_match"),
		      responsemsg = $("#scenario_response"),
		allFields = $([]).add(name).add(match).add(responsemsg),
		tips = $(".validateTips");	

		function updateTips(t) {
			tips
				.text(t)
				.addClass('ui-state-highlight');
			setTimeout(function() {
				tips.removeClass('ui-state-highlight', 1500);
			}, 500);
		}

		function checkLength(o,n,min,max) {

			if ( o.val().length > max || o.val().length < min ) {
				o.addClass('ui-state-error');
				updateTips("Length of " + n + " must be between "+min+" and "+max+".");
				return false;
			} else {
				return true;
			}

		}

		function checkRegexp(o,regexp,n) {

			if ( !( regexp.test( o.val() ) ) ) {
				o.addClass('ui-state-error');
				updateTips(n);
				return false;
			} else {
				return true;
			}

		}
		
		$("#dialog-form").dialog({
			autoOpen: false,
			height: 600,
			width: 650,
			modal: true,
			buttons: {
				'Create a scenario': function() {
					var bValid = true;
					allFields.removeClass('ui-state-error');
					bValid = bValid && checkLength(name,"scenario name",3,250);
					if (bValid) {
						
						$('#accordion').append('<h3><a href=\"#\">'+name.val()+'</a></h3><div><form action=\"\" method=\"post\" style=\"background-color:#99CCFF;\">' +
								'<input type=\"hidden\" name=\"serviceId\" value=\"\" />' +
								'<input type=\"hidden\" name=\"scenarioId\" value=\"\" />' +
								'<table class=\"simple\" width=\"100%\"><tbody><tr><th width=\"20%\"><p>Scenario Name:</p></th><td>' +
								'<p><input type=\"text\" style=\"width:100%;\"  name=\"scenarioName\" value=\"'+name.val()+'\" /></p>' +
								'<p class=\"tiny\">Example: <i>Valid Request</i> or <i>Invalid Request</i></p></td></tr>' +
								'<tr><th><p><a href=\"\">Match argument</a>: <span class=\"tiny\">(use for Dynamic response)</span></p></th>' + 
								'<td><p><textarea name=\"matchStringArg\" style=\"width:100%;\" rows=\"2\" >'+ match.val()+'</textarea></p></td></tr>' +
								'<tr><th><p>Scenario response message:</p></th><td>'+
								'<p><textarea class=\"resizable\" name=\"responseMessage\" rows=\"10\" style=\"width:100%;\">'+responsemsg.val() +
								'</textarea></p><p class=\"tiny\">The message you want your mock service to reply with. Feel free to cut and paste XML, free form text, etc.</p>' +
								'</td></tr></tbody></table><button id=\"update-scenario\">Update scenario</button><button id=\"delete-scenario\">Delete scenario</button></form></div>').accordion('destroy').accordion({
							active: false,
							collapsible: true
						});
						
								
						$(this).dialog('close');
					}
				},
				Cancel: function() {
					$(this).dialog('close');
				}
			},
			close: function() {
				allFields.val('').removeClass('ui-state-error');
			}
		});
		
		$('#create-scenario')
			.button()
			.click(function() {
				$('#dialog-form').dialog('open');
			});
		$('#update-service')
		    .button()
		    .click(function() {
			    
		    	$.prompt.setDefaults({
			        opacity:0.2
			    });
			    var serviceId = $("#service_id"),
			        realUrl = $("#service_real_url"),
			        serviceName = $("#service_name"),
			        hangtime = $("#hang_time"),
			        serviceContentType = $("#service_http_content_type");
			    if (serviceId == null) {   alert('serviceId is not set.'); } 
			 
			   $.post('<c:url value="/setup"/>', { serviceName: serviceName.val(), serviceId: serviceId.val(),
				   realServiceUrl:  realUrl.val(),  httpContentType: serviceContentType.val(),
				   hangTime: hangtime.val() } ,function(data){
					   if (data.data.redirect){
						   window.location.replace(data.data.redirect);
						   
					   }else {   
					   	$.prompt('<span style=\"color:red;\">Not updated:</span> ' + data.data.info );
					   }

					   }, 'json' );
				
		});

		
	
		$('#delete-service')
		    .button()
		    .click(function() {
			    alert("delete");
		});
		$('#delete-scenario')
		    .button()
		    .click(function() {
			    alert("delete scenario");
		});	
		$('.update-scenario')
		    .button()
		    .click(function() {
		    	var serviceId = $("#service_id");
		    	var scenearioId = this.id.split("_")[1];
			    alert("update scenario with service_id "+ serviceId.val() + " scenearioId: " + scenearioId);
		});		

	});
	
</script>


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
	            <input type="text" id="service_name" class="text ui-corner-all ui-widget-content" name="service_name" maxlength="100" size="100%" value="<c:out value="${mockservice.serviceName}"/>" />
	            <div class="tinyfieldset">Use a self descriptive name. For example, if you were to use this for 'authentication' testing, then call it 'Authentication'.</div>
	            <label for="service_url">Real service URL: </label>
                <input type="text" id="service_real_url" class="text ui-corner-all ui-widget-content" name="realServiceUrl" maxlength="100" size="100%" value="<c:out value="${mockservice.realServiceUrl}"/>" />
                <div class="tinyfieldset">You'll need this URL if you want Mockey to serve as a proxy to record transactions between your application and the real service.</div>
                <label for="service_url">Hang time: </label>
                <input type="text" id="hang_time" class="text ui-corner-all ui-widget-content" name="hangtime" maxlength="20" size="30px" value="<c:out value="${mockservice.hangTime}"/>" />
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
    <c:if test="${!empty mockservice.id}">
	    <div class="create-scenario-form">
			<div id="dialog-form" title="Create new scenario">
				<p class="validateTips">Scenario name is required.</p>
				<form>
				<fieldset>
					<label for="scenario_name">Scenario name</label>
					<input type="text" name="scenario_name" id="scenario_name" class="text ui-widget-content ui-corner-all" />
					<label for="scenario_match">Match argument (for Dynamic response type)</label>
					<input type="text" name="scenario_match" id="scenario_match" class="text ui-widget-content ui-corner-all" />
					<label for="scenario_response">Response content</label>
					<textarea name="scenario_response" id="scenario_response" class="text ui-widget-content ui-corner-all resizable" rows="10"></textarea>
				</fieldset> 
				</form>
			</div>
			<p style="float:right;"><button id="create-scenario">Create new scenario</button></p>
		</div>
	
	    <h3>Existing Scenarios</h3>
		
		<div class="demo">
			<div id="accordion">
				
				<c:forEach var="mockscenario" begin="0" items="${mockservice.scenarios}" varStatus="status">   
					<h3><a href="#">${mockscenario.scenarioName}</a></h3>
					<div>
					<div class="parentformselected" >
					
				            <input type="hidden" name="serviceId" value="<c:out value="${mockservice.id}"/>" />
							<c:if test="${!empty mockscenario.id}">
							    <input type="hidden" name="scenarioId" value="<c:out value="${mockscenario.id}"/>" />
							</c:if>
				            <table class="simple" width="100%">
				                <tbody>
				                    <tr>
				                        <th width="20%"><p>Scenario Name:</p></th>
					                    <td>
											<p><input type="text" style="width:100%;" id="scenarioName_${mockscenario.id}" name="scenarioName" value="<c:out value="${mockscenario.scenarioName}"/>" /></p>
											<p class="tiny">Example: <i>Valid Request</i> or <i>Invalid Request</i></p>
										</td>
				                    </tr>
				                    <tr>
										<th><p><a href="<c:url value="help#static_dynamic"/>">Match argument</a>: <span style="color:blue;">(optional)</span></p></th>
										<td>
										  <p>
										      <textarea name="matchStringArg" id="matchStringArg_${mockscenario.id}" style="width:100%;" rows="2" ><c:out value="${mockscenario.matchStringArg}" /></textarea>
										  </p>
										  
										</td>
				                    </tr>      
									<tr>
										<th><p>Scenario response message:</p></th>
										<td>
											<p><textarea class="resizable" id="responseMessage_${mockscenario.id}" name="responseMessage" rows="10" style="width:100%;"><c:out value="${mockscenario.responseMessage}" escapeXml="false"/></textarea>
											
											</p>
										    <p class="tiny">The message you want your mock service to reply with. Feel free to cut and paste XML, free form text, etc.</p>
										</td>
									</tr>
								
				                </tbody>
				            </table>
					        <p align="right">
						        <button id="update-scenario_${mockscenario.id}" class="update-scenario" name="">Update scenario</button>
								<button id="delete-scenario" name="delete" onclick="return confirm('Are you sure you want to delete this scenario?');">Delete</button>
					        </p>
						</div>
					</div>
				</c:forEach>
			</div>
	   </div>
   </c:if>
   
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />