<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%@ taglib prefix="mockey-tag" tagdir="/WEB-INF/tags" %>

<c:set var="pageTitle" value="Home" scope="request" />
<c:set var="currentTab" value="home" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />

<script>
$(document).ready( function() {
	$("#tabs").tabs();
	$('#create-plan')
		.button()
		.click(function() {
			
			var servicePlanName = $('input[name=servicePlanName]').val()
			$.post('<c:url value="/plan/setup"/>', { action: 'save_plan', servicePlanName: servicePlanName } ,function(data){
				   //console.log(data);
				   if(data.result.success && data.result.planid){
					   $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast'); 
					   // We redirect here. Because appending HTML would require we append this 
					   // click function, (which appends to itself, not good). Looping here.
					   // We redirect for now. 
					   document.location="<c:url value="/home" />"; 
					   
				    }
			}, 'json' );
		});

	
	$('.delete-plan').each( function() {
		$(this).click( function() {
			var planId = this.id.split("_")[1];
			$.post('<c:url value="/plan/setup"/>', { action: 'delete_plan', plan_id: planId } ,function(data){
				  
				   if(data.result.success){
					   $('#deleted').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast'); 
					   $('#plan_'+planId).hide();
				    }
			}, 'json' );
			
		});
	});

	$('.set-plan').each( function() {
		$(this).click( function() {
			var planId = this.id.split("_")[1];
			$.post('<c:url value="/plan/setup"/>', { action: 'set_plan', plan_id: planId } ,function(data){
				  
				   if(data.result.success){
					   $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
					   document.location="<c:url value="/home" />"; 
				    }
			}, 'json' );
			
		});
	});

    $("#dialog-delete-service-confirm").dialog({
        resizable: false,
        height:120,
        modal: true,
        autoOpen: false
    });
	
    $('.tiny_service_delete').each( function() {
        $(this).click( function() {
            var serviceId = this.id.split("_")[1];
            $('#dialog-delete-service-confirm').dialog('open');
	            $('#dialog-delete-service-confirm').dialog({
	                buttons: {
	                  "Delete service": function() {
	            	      document.location="<c:url value="/setup" />?deleteService=yes&serviceId="+ serviceId;
	                  }, 
	                  Cancel: function(){
		                  $(this).dialog('close');
	                  }
	                }
	          }); 
	          return false;
            });
        });

    // SCENARIO CREATION JAVASCRIPT
    $("#dialog-create-scenario").dialog({
        resizable: true,
        height:500,
        width:700,
        modal: false,
        autoOpen: false
    });
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
    $('.createScenarioLink').each( function() {
        $(this).click( function() {
        	$.ajaxSetup({ cache: false });
            var serviceId = this.id.split("_")[1];
            // Clear input
            $('#scenario_name').val('');
            $('#scenario_match').val('');
            $('#scenario_response').val(''); 
            $('#dialog-create-scenario').dialog('open');
                $('#dialog-create-scenario').dialog({
                    buttons: {
                      "Create scenario": function() {
                	       var bValid = true;  
                	       allFields.removeClass('ui-state-error');
                	       bValid = bValid && checkLength(name,"scenario name",3,250);
                	       if (bValid) {
			                   $.post('<c:url value="/scenario"/>', { scenarioName: name.val(), serviceId: serviceId, matchStringArg: match.val(),
			                        responseMessage: responsemsg.val() } ,function(data){
			                        	
			                        }, 'json' );  
	                           $(this).dialog('close');              
	                           document.location="<c:url value="/home" />?serviceId="+ serviceId;
                	       }
                      }, 
                      Cancel: function(){
                          $(this).dialog('close');
                      }
                    }
              }); 
              
              return false;
            });
        });

    $("#dialog-delete-scenario-confirm").dialog({
        resizable: false,
        height:120,
        modal: false,
        autoOpen: false
    });
    
    $('.deleteScenarioLink').each( function() {
        $(this).click( function() {
        	var scenarioId = this.id.split("_")[1];
            var serviceId = this.id.split("_")[2];
            $('#dialog-delete-scenario-confirm').dialog('open');
                $('#dialog-delete-scenario-confirm').dialog({
                    buttons: {
                      "Delete scenario": function() {
                          // Post the DELETE call.  
                    	  $.post('<c:url value="/scenario"/>', {serviceId: serviceId, deleteScenario: 'yes', scenarioId: scenarioId},
                                  function(data) {}, 'json');
                          // Hide the info. 
                          $('#service-scenario-info_'+scenarioId + '_'+ serviceId ).hide();
                          $(this).dialog('close');
                          $(this).dialog( "destroy" )
                          $('#deleted').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast'); 
                          
                      }, 
                      Cancel: function(){
                          $(this).dialog('close');
                      }
                    }
              }); 
              return false;
            });
        });
    
    $('.allresponsetype').each( function() {
        $(this).click(function(){   
          var response_type = this.id.split("_")[1]; 	
          //console.log("response type: "+response_type);
          $.post('<c:url value="/setup"/>', { responseType: response_type, all: true } ,function(data){
					   //console.log(data);
					   if(data.result.success){
						   document.location="<c:url value="/home" />";
					    }
				}, 'json' );
        });
        
        
     });
    
    $('.gt').each( function() {
        $(this).click(function(){   
          var serviceId = this.id.split("_")[1]; 	
          $(".parentform").removeClass("parentformselected");
    	  $("#parentform_"+serviceId).addClass("parentformselected");
        });
     });
    
   
    $('.serviceScenarioResponseTypeLink').each( function() {
		$(this).click( function() {
			var scenarioId = this.id.split("_")[1];
			var serviceId = this.id.split("_")[2];
			
			$.ajax({
				type: "POST",
				url: "<c:url value="service_scenario"/>",
				data:"scenarioId="+scenarioId+"&serviceId="+serviceId
			});
			$(".scenariosByServiceId-on_"+serviceId).hide();
			$(".scenariosByServiceId-off_"+serviceId).show();
			$("#serviceScenarioOFF_"+scenarioId+"_"+serviceId).hide();
			$("#serviceScenarioON_"+scenarioId+"_"+serviceId).show();
			$('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
		});
	});

    $('.setRealUrlLink').each( function() {
		$(this).click( function() {
			var row = this.id.split("_")[1];
			var serviceId = this.id.split("_")[2];
			$.ajax({
				type: "POST",
				url: "<c:url value="service_scenario"/>",
				data:"defaultUrlIndex="+row+"&serviceId="+serviceId
			});
			$(".realUrl-on_"+serviceId).hide();
			$(".realUrl-off_"+serviceId).show();
			$("#realUrlOFF_"+row+"_"+serviceId).hide();
			$("#realUrlON_"+row+"_"+serviceId).show();
			$('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
		});
	});

    
    $('.serviceResponseTypeLink').each( function() {
		$(this).click( function() {
			var responseType = this.id.split("_")[1];
			var serviceId = this.id.split("_")[2];
			$.ajax({
				type: "POST",
				url: "<c:url value="service_scenario"/>",
				data:"serviceResponseType="+responseType+"&serviceId="+serviceId
			});
                 
			$('.serviceResponseType_0_'+serviceId).removeClass("response_set").addClass("response_not");
			$('.serviceResponseType_1_'+serviceId).removeClass("response_set").addClass("response_not");
			$('.serviceResponseType_2_'+serviceId).removeClass("response_set").addClass("response_not");
			
			$('#staticScenario_'+serviceId).removeClass("show").addClass("hide");
			$('#proxyScenario_'+serviceId).removeClass("show").addClass("hide");
			$('#dynamicScenario_'+serviceId).removeClass("show").addClass("hide");
			
			if(responseType == 0){
				$('.serviceResponseType_0_'+serviceId).removeClass("response_not").addClass("response_set");
				$('#proxyScenario_'+serviceId).addClass("show");
			}else if(responseType == 1){
				$('.serviceResponseType_1_'+serviceId).removeClass("response_not").addClass("response_set");
				$('#staticScenario_'+serviceId).addClass("show");
			}else if(responseType == 2){
				$('.serviceResponseType_2_'+serviceId).removeClass("response_not").addClass("response_set");
				$('#dynamicScenario_'+serviceId).addClass("show");
			}
			$('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
		});
	});

    $('#dialog').dialog({ autoOpen: false, minHeight: 300, width:700, height:500 });
    
    $('.viewServiceScenarioLink').each( function() {
        $(this).click( function() {
        	var scenarioId = this.id.split("_")[1];
            var serviceId = this.id.split("_")[2];
            $.ajax({
                type: "GET",
                dataType: 'json',
                url: "<c:url value="/view/scenario"/>?serviceId="+serviceId+"&scenarioId="+scenarioId,
                success: function(data) {
                	$('#scenario_name').val(data.name);
                	$('#scenario_match').val(data.match);
                	$('#scenario_response').val(data.response); 
                	$('#dialog-create-scenario').dialog('open');
                    $('#dialog-create-scenario').dialog({
                        buttons: {
                          "Update scenario": function() {
                               var bValid = true;  
                               allFields.removeClass('ui-state-error');
                               bValid = bValid && checkLength(name,"scenario name",3,250);
                               if (bValid) {
                                   $.post('<c:url value="/scenario"/>', { scenarioName: name.val(), serviceId: serviceId, scenarioId: scenarioId, matchStringArg: match.val(),
                                        responseMessage: responsemsg.val() } ,function(data){
                                               console.log(data);
                                              
                                        }, 'json' );  
                                   $('#view-scenario_'+scenarioId+'_' +serviceId).fadeOut(function(){ $(this).text(name.val()).fadeIn() });
                                   $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
                                   return false;
                                   
                               }
                          }, 
                          "Close": function(){
                              $(this).dialog('close');
                          }
                        }
                  });       
                }
            });
            return false;
        });
    });

    $('.hideServiceScenarioLink').each( function() {
        $(this).click( function() {
           
        });
    });
    
 });
</script>
    <div id="main">
        <%@ include file="/WEB-INF/common/message.jsp" %>
        <c:choose>
	        <c:when test="${!empty services}">    
	          <c:choose>
				    <c:when test="${empty param.serviceId}">
				        
				        <c:set var="serviceIdToShowByDefault" value="${services[0].id}" scope="request"/>
				    </c:when>
				    <c:otherwise>
				        <c:set var="serviceIdToShowByDefault" value="${param.serviceId}" scope="request"/>
				    </c:otherwise>
				</c:choose>

		        <table class="simple" width="100%" cellspacing="0">
	            <tbody>
		              <tr>                                                                                 
							<td valign="top" width="40%">
							<div id="tabs">
								<ul>
									<li><a href="#tabs-1">Services</a></li>
									<li><a href="#tabs-2">Plans</a></li>
								</ul>
							  	<div id="tabs-1">
							  	  <div class="info_message tiny"/>
							  	  Click one of the following buttons to set 
							  	  response type for <strong>each service</strong>.
								  <p> 
									  <a id="allresponsetype_0" class="allresponsetype response_not" style="text-decoration:none;" href="#"> Proxy </a>
									  <a id="allresponsetype_1" class="allresponsetype response_not" style="text-decoration:none; margin-left:2px;margin-right:2px;" href="#"> Static </a>
									  <a id="allresponsetype_2" class="allresponsetype response_not" style="text-decoration:none;" href="#">Dynamic</a>
								  </p>
								  </div>
								  <div class="scroll">
		                            	<c:forEach var="mockservice" items="${services}"  varStatus="status">	  
			                                <div id="parentform_${mockservice.id}" class="parentform <c:if test="${mockservice.id eq serviceIdToShowByDefault}">parentformselected</c:if>" >
			                                
				                            	<c:url value="/setup" var="setupUrl">
				                                	<c:param name="serviceId" value="${mockservice.id}" />
				                             	</c:url>
				                                <span style="float:right;"><a class="tiny_service_delete remove_grey" id="deleteServiceLink_<c:out value="${mockservice.id}"/>" title="Delete this service" href="#">x</a></span>
				                                
												<div style="margin-bottom:0.5em;">
												<mockey:slug text="${mockservice.serviceName}" maxLength="30"/>
												</div>
				                                <mockey:service type="${mockservice.serviceResponseType}" serviceId="${mockservice.id}"/>
				                                <span class="toggle_button tiny">
												      <a class="gt" onclick="return true;" href="#" id="togglevalue_<c:out value="${mockservice.id}"/>">view</a> |
												      <a href="<c:out value="${setupUrl}"/>" title="Edit service definition">edit</a>
												</span>
												 <c:if test="${empty mockservice.scenarios}">
						                           <div class="warning_no_scenario">No scenarios defined for this service.</div>
						                         </c:if>
				                               
											</div>
								    	</c:forEach>
								    </div>
							    </div>
							    <div id="tabs-2">
								    <div class="scroll">
								        <div class="parentform">
								        <fieldset>
								        To <strong>create a plan</strong>, go to the Services tab, make your settings, and
								        then tab to here to create (or save). 
								        <p><input type="text" style="width:90%;" id="servicePlanName" class="text ui-corner-all ui-widget-content" name="servicePlanName"></input></p>
								        <p><button id="create-plan">Create plan</button></p>
								        </fieldset>
								        
								        </div>
								        <p>To <strong>set a plan</strong>, click on the plan below. You will be redirected to the Services tab.  
								        </p>
								         <c:if test="${empty plans}">
									      <div class="info_message" id="no-plans-msg"> No plans here - yet! You should make one. </div>
									    </c:if>
									    <div id="plan-list">
									    <c:forEach var="plan" items="${plans}"  varStatus="status">	  
			                                <div id="plan_${plan.id}" class="parentform" >
			                                <mockey:slug text="${plan.name}" maxLength="40"/>
			                                <span style="float:right;"><a class="delete-plan remove_grey" id="delete-plan_<c:out value="${plan.id}"/>" title="Delete this plan" href="#">x</a></span>
			                                <div><a href="#" id="set-plan_${plan.id}" class="set-plan tiny">set me as the plan</a></div>
			                                </div>
									    </c:forEach>
									    <div class="tiny" style="padding-top:1em;" id="no-plans-msg"><a href="<c:url value="help#plan"/>">What's a plan?</a></div>
			                            
									    </div>
									   
							    	</div>
							    </div>
							</div>
							</td>
							<td valign="top">
							<div id='service_list_container'>
							<div class="service_div display" style="<c:if test="${! empty serviceIdToShowByDefault}">display:none;</c:if><c:if test="${serviceIdToShowByDefault==null}">display:block;</c:if>text-align:center;">
							
						     Nothing here to display. Move along. 
							
							</div>
							
							<c:forEach var="mockservice" items="${services}">
							   <div id="div_<c:out value="${mockservice.id}"/>_" class="service_div display" style="<c:if test="${mockservice.id eq serviceIdToShowByDefault}">display: block;</c:if>" > 
                               
								
                                <div id="updateStatus_<c:out value="${mockservice.id}"/>" class="outputTextArea"></div>
                                <div class="parentformselected">
                                <input type="hidden" name="serviceId" id="serviceId_<c:out value="${mockservice.id}"/>" value="${mockservice.id}" />
                                 <div class="service">
                                    
                                   <div class="service-label">Service name: <mockey-tag:editServiceLink serviceId="${mockservice.id}"/></div>
                                   <div class="service-value big">${mockservice.serviceName} </div>
                                   <div class="service-def-spacer"></div>
                                   <div class="service-label border-top">Mock URL: <mockey-tag:editServiceLink serviceId="${mockservice.id}"/></div>
                                   <div><a class="tiny" href="<mockey:url value="${mockservice.url}"/>"><mockey:url value="${mockservice.url}" /></a></div>
                                   <div class="service-def-spacer"></div>
                                   
                                   <div class="service-label not-top border-top">Real URL(s): <mockey-tag:editServiceLink serviceId="${mockservice.id}"/></div>
                                   <table class="simple">
                                   <c:forEach var="realUrl" items="${mockservice.realServiceUrls}" varStatus="status" >
								     
								       <tr>
								       <td width="70"> 
								       		 <c:choose>
		                                        <c:when test='${mockservice.defaultRealUrlIndex+1 == status.count}'>
		                                          <c:set var="off_class" value="hide" />
		                                          <c:set var="on_class" value="" />
		                                        </c:when>
		                                        <c:otherwise>
		                                          <c:set var="off_class" value="" />
		                                          <c:set var="on_class" value="hide" />
		                                        </c:otherwise>
		                                      </c:choose>
		                                     
		                                      <a href="#" id="realUrlON_${status.count}_${mockservice.id}" class="realUrl-on_${mockservice.id} ${on_class} response_set" onclick="return false;">&nbsp;ON&nbsp;</a>
		                                      <a href="#" id="realUrlOFF_${status.count}_${mockservice.id}" class="setRealUrlLink realUrl-off_${mockservice.id} ${off_class} response_not" onclick="return false;">OFF</a>
								       
								       </td>
								       <td style="text-align:left;">
								       <a class="tiny" href="<mockey:url value="${realUrl}"/>"><mockey:url value="${realUrl}" breakpoint="5"/></a></td>
								       </tr>
								     
								     </c:forEach>
								   </table>
                                   <c:if test="${empty mockservice.realServiceUrls}">
                                   <div class="info_message">No real URLS defined.</div>
                                   </c:if>
                                   
                                   <div class="service-label border-top">This service is set to:</div>
                                   <div class="service-value big">
                                   	<span class="hide<c:if test="${mockservice.serviceResponseType eq 2}"> show</c:if>" id="dynamicScenario_${mockservice.id}">Dynamic</span>
							       	<span class="hide<c:if test="${mockservice.serviceResponseType eq 0}"> show</c:if>" id="proxyScenario_${mockservice.id}">Proxy</span>
			                       	<span class="hide<c:if test="${mockservice.serviceResponseType eq 1}"> show</c:if>" id="staticScenario_${mockservice.id}">Static</span>
                                   </div>
                                   <mockey:service type="${mockservice.serviceResponseType}" serviceId="${mockservice.id}"/>
                                   
                                   <div class="service-label border-top" style="margin-top:1em;">Select a static scenario:
                                   <span style="float:right;"><a href="#" class="createScenarioLink power-link" id="createScenarioLink_${mockservice.id}">Create Scenario</a></span>
                                   </div>
                                   <div>
                                   <ul id="scenario-list_${mockservice.id}" class="simple group">
	                                    <div id="result1" class="jTemplatesTest"></div>
	                                    
		                                <c:choose>
		                                  <c:when test="${not empty mockservice.scenarios}">
		                                  <c:forEach var="scenario" begin="0" items="${mockservice.scenarios}">
		                                    <li style="padding-top: 0.5em;" id="service-scenario-info_${scenario.id}_${mockservice.id}">
		                                      <c:choose>
		                                        <c:when test='${mockservice.defaultScenarioId eq scenario.id}'>
		                                          <c:set var="off_class" value="hide" />
		                                          <c:set var="on_class" value="" />
		                                        </c:when>
		                                        <c:otherwise>
		                                          <c:set var="off_class" value="" />
		                                          <c:set var="on_class" value="hide" />
		                                        </c:otherwise>
		                                      </c:choose>
		                                     
		                                      <a href="#" id="serviceScenarioON_${scenario.id}_${mockservice.id}" class="scenariosByServiceId-on_${mockservice.id} ${on_class} response_set" onclick="return false;">&nbsp;ON&nbsp;</a>
		                                      <a href="#" id="serviceScenarioOFF_${scenario.id}_${mockservice.id}" class="serviceScenarioResponseTypeLink scenariosByServiceId-off_${mockservice.id} ${off_class} response_not" onclick="return false;">OFF</a>
		                                      <a href="#" id="view-scenario_${scenario.id}_${mockservice.id}" class="viewServiceScenarioLink"><mockey:slug text="${scenario.scenarioName}" maxLength="40"/></a>
		                                      <span> <a href="#" id="delete-scenario_${scenario.id}_${mockservice.id}" class="deleteScenarioLink remove_grey">x</a> </span>
		                                    </li>
		                                  </c:forEach>
		                                  </c:when>
		                                  <c:otherwise>
		                                    <c:url value="/scenario" var="scenarioUrl">
									            <c:param name="serviceId" value="${mockservice.id}" />
									        </c:url>
									         <c:url value="/setup" var="setupScenarioUrl">
					                                <c:param name="serviceId" value="${mockservice.id}" />
					                                <c:param name="createScenario" value="yes" />
					                             </c:url>
		                                  	<li class="alert_message"><span>You need to create a scenario before using <strong>Static</strong> or <strong>Dynamic</strong> scenario</span></li>
		                                  </c:otherwise>
		                                </c:choose>
	                                </ul>
                                   </div>
                                 </div>
                                 
								
                                 <div class="not-top">
			                       <strong>Hang time (milliseconds):</strong> ${mockservice.hangTime} <mockey-tag:editServiceLink serviceId="${mockservice.id}"/>
	                             </div>
	                             <div>
			                       <strong>Content type:</strong>   
	                    			<c:choose>
	                    			<c:when test="${!empty mockservice.httpContentType}">${mockservice.httpContentType}</c:when>
	                    			<c:otherwise><span style="color:red;">not set</span></c:otherwise>
	                    			</c:choose>
			                     </div>
                              </div>
                              </div>
                              </c:forEach>
                              </div>
                              
							</td>							
						</tr>
						
		            </tbody>
		        </table>
		      
		        <div id="dialog" title="Scenerio Preview">
                    <p>Details appended here.</p>
                </div>
                <div id="dialog-delete-service-confirm" title="Delete Service">
                    <p>Are you sure you want to delete this Service?</p>
                </div>
                <div id="dialog-delete-scenario-confirm" title="Delete Service Scenario">
                    <p>Are you sure you want to delete this Scenario?</p>
                </div>
                <div id="dialog-create-scenario" title="Service Scenario">
                    <p class="validateTips">Scenario name is required.</p>
                    <p>
                    <form>
                    <fieldset>
                        <label for="scenario_name">Scenario name</label>
                        <input type="text" name="scenario_name" id="scenario_name" class="text ui-widget-content ui-corner-all" />
                        <label for="scenario_match">Match argument</label>
                        <input type="text" name="scenario_match" id="scenario_match" class="text ui-widget-content ui-corner-all" />
                        <div class="tinyfieldset">Used for Dynamic response type. Case sensitive.</div>
                        <label for="scenario_response">Response content</label>
                        <textarea name="scenario_response" id="scenario_response" class="text ui-widget-content ui-corner-all resizable" rows="10"></textarea>
                    </fieldset> 
                    </form>
                    </p>
                </div>
    </div>
	        </c:when>
	        <c:otherwise>
			  <p class="info_message">There are no mock services defined. You can <a href="<c:url value="upload"/>">upload one</a>, <a href="<c:url value="setup"/>">create one manually</a> or start <a href="<c:url value="help#record"/>">recording</a>. </p>
			</c:otherwise>
        </c:choose>
<c:if test="${mode ne 'edit_plan'}">
<script type="text/javascript">$('html').addClass('js');

$(function() {

  $('a','span.toggle_button').click(function() {
    var serviceId = this.id.split("_")[1];
    $('div.display','#service_list_container')
      .stop()
      .hide()
      .filter( function() { return this.id.match('div_' + serviceId+'_'); })   
      .show();    
    return true; 
  
  })

});

</script>
</c:if>

<jsp:include page="/WEB-INF/common/footer.jsp" />