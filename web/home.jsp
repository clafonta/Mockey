<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%@ taglib prefix="mockey-tag" tagdir="/WEB-INF/tags" %>

<c:set var="pageTitle" value="Home" scope="request" />
<c:set var="currentTab" value="home" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />

<script>
$(document).ready( function() {
	$('#tabs').tabs().hide();
	$('#tabs:not(:first)').hide();
    $('#tabs:first').fadeIn('fast');
	
    $('.invisible-focusable').addClass("invisiblefield");  
	$('.invisible-focusable').focus(function() {  
        $(this).removeClass("invisiblefield").addClass("invisiblefiled-in-focus");  
    });  
	$('.invisible-focusable').blur(function() {  
        $(this).removeClass("invisiblefiled-in-focus").addClass("invisiblefield");   
    }); 
    
    $('.manageTagLink').each( function() {
        $(this).click( function() {             
            $('#dialog-tag-manage').dialog('open');
                $('#dialog-tag-manage').dialog({ 
                    buttons: {                      
                      Cancel: function(){
                          $(this).dialog('close');
                      }
                    }
              }); 
              // Reset the size.
              $('#dialog-tag-manage').dialog({height: 350 });
                
              return false;
            });
        });
    
	$('.createPlanLink').each( function() {
        $(this).click( function() {             
            $('#dialog-create-plan').dialog('open');
                $('#dialog-create-plan').dialog({ 
                    buttons: {
                      "Create plan": function() {
                           var bValid = true;  
                           if (bValid) {
                        	   var servicePlanName = $('input[name=service_plan_name]').val();  
                        	   var servicePlanTag = $('input[name=service_plan_tag]').val();  
                        	   $.post('<c:url value="/plan/setup"/>', { action: 'save_plan', service_plan_name: servicePlanName, service_plan_tag:servicePlanTag } ,function(data){
                                   if(data.result.success && data.result.planid){
                                       //$('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast'); 
                                       // We redirect here. Because appending HTML would require we append this 
                                       // click function, (which appends to itself, not good). Looping here.
                                       // We redirect for now. 
                                       $(this).dialog('close');              
                                       document.location="<c:url value="/home" />"; 
                                       
                                    }
                            }, 'json' );
                               
                           }
                      }, 
                      Cancel: function(){
                          $(this).dialog('close');
                      }
                    }
              }); 
              // Reset the size.
              $('#dialog-create-plan').dialog({height: 350 });
                
              return false;
            });
        });
	
	$('.delete-plan').each( function() {
		$(this).click( function() {
			var planId = this.id.split("_")[1];
			$('#dialog-delete-service-plan-confirm').dialog('open');
            $('#dialog-delete-service-plan-confirm').dialog({
                buttons: {
                  "Delete Plan": function() {
                      // Post the DELETE call.  
                      $.post('<c:url value="/plan/setup"/>', { action: 'delete_plan', plan_id: planId } ,function(data){
			                   if(data.result.success){
			                       $('#deleted').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast'); 
			                       $('#plan_'+planId).hide();
			                    }
			            }, 'json' );
                      $(this).dialog('close');                      
                  }, 
                  Cancel: function(){
                      $(this).dialog('close');
                  }
                }
          }); 
          $('#dialog-delete-service-plan-confirm').dialog({height: 200 });
            
          return false;
		});
	});

	$('.set-plan').each( function() {
		$(this).click( function() {
			var planId = this.id.split("_")[1];
			$.post('<c:url value="/plan/setup"/>', { action: 'set_plan', plan_id: planId, type: 'redirect' } ,function(data){
				  
				   if(data.result.success){
					   document.location="<c:url value="/home" />"; 
				    }
			}, 'json' );
			
		});
	});


	$('.save-plan').each( function() {
        $(this).click( function() {
            var planId = this.id.split("_")[1];
            var servicePlanName = $('input[name=servicePlanName_'+planId+']').val()
            
            $.post('<c:url value="/plan/setup"/>', { action: 'save_plan', plan_id: planId, service_plan_name: servicePlanName, type: 'json' } ,function(data){
                  
                   if(data.result.success){
                       $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
                    }
            }, 'json' );
            
        });
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
	          $('#dialog-delete-service-confirm').dialog({height: 200 });
			      
	          return false;
            });
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
                          $('#deleted').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast'); 
                          
                      }, 
                      Cancel: function(){
                          $(this).dialog('close');
                      }
                    }
              }); 
              $('#dialog-delete-scenario-confirm').dialog({height: 200 });
                
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

    $('#dialog').dialog({ autoOpen: false, minHeight: 300, width:700, height:500, modal: true });
    $('#dialog-delete-service-plan-confirm').dialog({autoOpen: false, height: 150, resizable: false });
    $('#dialog-delete-scenario-confirm').dialog({autoOpen: false, minHeight: 250, width: 300, height: 120, modal: false, resizable: false });
    $("#dialog-create-plan").dialog({minHeight: 350, height:350, width: 500,  modal: false, autoOpen: false, resizable: true });
    $("#dialog-delete-service-confirm").dialog({ resizable: false, height: 120, modal: false, autoOpen: false });
    $("#dialog-tag-manage").dialog({ resizable: false, height: 420, minHeight: 450, modal: true, autoOpen: false });
    $('.hideServiceScenarioLink').each( function() {
        $(this).click( function() {
           
        });
    });
    $("#filter-tag-button").click( function() {
           var filterTag = $('#filter-tag').val();
           $.post('<c:url value="/taghelp"/>', { action: 'filter_tag_on', tag: filterTag } ,function(data){
					   //console.log(data);
					   if(data.success){
						   document.location="<c:url value="/home" />";
					    }else {
					       alert("Hmm...");
					    }
				}, 'json' );
           
        });
   
    $("#delete-tag-button").click( function() {
    	   var filterTag = $('#filter-tag').val();
           $.post('<c:url value="/taghelp"/>', { action: 'delete_tag_from_store', tag: filterTag } ,function(data){
					   //console.log(data);
					   if(data.success){
						   document.location="<c:url value="/home" />";
					    }else {
					       alert("Hmm...");
					    }
				}, 'json' );
        });
  
 });
</script>
    <div id="main">
    <div id="filter_view_div">
	<span>Filter Services by Tag(s): </span> 
	<input type="text" id="filter-tag-field" style="width:350px;" title="Enter tags here. View services by matching tags."  name="filter-tag-field" class="blur text ui-corner-all ui-widget-content" /> 
	<a href="#" id="filter-tag-update-button" class="hhButton">Apply Filter</a> <a href="#" class="hhButtonRed clear-tag-button" id="">Clear Filter View</a>
	<a href="#" class="manageTagLink">Tag Helper</a>
	</div>
        <%@ include file="/WEB-INF/common/message.jsp" %>
        <!-- SERVICE PLAN CREATE DIALOG -->
        <div id="dialog-tag-manage" title="Tag Helper">
            <p><strong>WARNING:</strong>
            This will remove tag(s) from each Service, Scenario, and Service Plan. 
            <input type="text" name="filter-tag" id="filter-tag" title="Enter tag(s) here" class="text ui-widget-content ui-corner-all" />
            <ul class="button-list">
            <li><a href="#" class="hhButtonRed" style="color:#FFFFFF;" id="delete-tag-button">Remove tag(s) from all things.</a></li>
            </ul>
            </p>
        </div>
        
        <!-- SERVICE PLAN CREATE DIALOG -->
        <div id="dialog-create-plan" title="Service Plan">
            <p>
            <fieldset>
                <label for="service_plan_name">Service Plan name</label>
                <input type="text" name="service_plan_name" id="service_plan_name" class="text ui-widget-content ui-corner-all" />
                <label for="service_plan_tag" class="blur">Tag(s) - <i>optional</i></label>
                <input type="text" name="service_plan_tag" id="service_plan_tag" title="Optional tags here" class="text ui-widget-content ui-corner-all" />
            </fieldset> 
            </p>
        </div>
        
        <c:choose>
	        <c:when test="${!empty services || !empty plans}">    
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
							<td valign="top" width="220px;" style="padding-left:0;">
							<div id="tabs" style="display:none;">
								<ul>
									<li><a href="#tabs-1">Services</a></li>
									<li><a href="#tabs-2">Plans</a></li>
								</ul>
							  	<div id="tabs-1">
							  	  <div style="text-align:right;"><span class="power-link tiny"><a href="#" class="createPlanLink" id="createPlanLink">Create Service Plan</a></span></div>
							  	  <div class="info_message tiny">
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
				                                <div class="count-box">${status.count}</div>
												<div style="margin-bottom:0.5em;">
												 <mockey:slug text="${mockservice.serviceName}" maxLength="30"/>
												</div>
												
												<div class="toggle-buttons">
				                                  <mockey:service type="${mockservice.serviceResponseType}" serviceId="${mockservice.id}"/>
				                                  <span class="toggle_button tiny">
												      <a class="gt" onclick="return true;" href="#" id="togglevalue_<c:out value="${mockservice.id}"/>">view</a> |
												      <a href="<c:out value="${setupUrl}"/>" title="Edit service definition">edit</a>
												  </span>
												  <c:if test="${empty mockservice.scenarios}">
						                           <div class="warning_no_scenario">No scenarios defined for this service.</div>
						                          </c:if>
						                        </div>
			                                    
												<div style="padding-top:10px;">
												<mockey-tag:statusCheck lastVisit="${mockservice.lastVisit}" tag="${mockservice.tag}" serviceName="${mockservice.serviceName}" serviceId="${mockservice.id}"/>					
												</div>
											</div>
								    	</c:forEach>
								    	
								    </div>
							    </div>
							    
							    <div id="tabs-2">
							        <div style="text-align:right;"><span class="power-link tiny"><a href="#" class="createPlanLink" id="createPlanLink">Create Service Plan</a></span></div>
								    <div class="scroll">
								         <c:if test="${empty plans}">
									      <div class="info_message" id="no-plans-msg"> No plans here - yet! You should make one. </div>
									    </c:if>
									    <div id="plan-list">
									    <c:forEach var="plan" items="${plans}"  varStatus="status">	  
			                                <div id="plan_${plan.id}" class="parentform" >
			                                <span style="float:right;"><a class="delete-plan remove_grey" id="delete-plan_<c:out value="${plan.id}"/>" title="Delete this plan" href="#">x</a></span>
			                                
			                                <input type="text" style="width:90%;" id="servicePlanName_${plan.id}" class="invisible-focusable invisiblefield" name="servicePlanName_${plan.id}" value="${plan.name}"></input>
			                                <span class="tiny">Tag(s): ${plan.tag}</span>
			                                <div style="padding-top:0.6em;">  
			                                  <a id="set-plan_${plan.id}" class="set-plan response_not" style="text-decoration:none;" href="#"> Set This Plan </a> &nbsp;
			                                  <a id="save-plan_${plan.id}" class="save-plan response_not" style="text-decoration:none;" href="#">Save As Plan</a></div>
			                                </div>
									    </c:forEach>
									    <div class="tiny" style="padding-top:1em;" id="no-plans-msg"><a href="<c:url value="help#plan"/>">What's a plan?</a></div>
			                            <div class="info_message tiny">
			                            <p>To <strong>create a plan</strong>, go to the Services tab, make your settings, and
                                        then tab to here to create (or save). </p>
                                        <p>To <strong>set a plan</strong>, click on the <b>Set As Plan</b> link. Note: you 
                                        will be redirected to the Services tab. </p>
                                        <p>
                                        To <strong>update a plan</strong>, go to the <b>Services</b> tab, make the 
                                        necessary updates you need and return here. Clicking <b>Save/Update</b> will 
                                        update the service plan name and settings. </p>
                                        </div>
									    </div>
									   
							    	</div>
							    </div>
							</div>
							</td>
							<td valign="top" width="380px;">
							<div id='service_list_container'>
							<div class="service_div display" style="<c:if test="${! empty serviceIdToShowByDefault}">display:none;</c:if><c:if test="${serviceIdToShowByDefault==null}">display:block;</c:if>text-align:center;">
							
						     Nothing here to display. Move along. 
							
							</div>
							
							<c:forEach var="mockservice" items="${services}">
							   <div id="div_<c:out value="${mockservice.id}"/>_" class="service_div display" style="<c:if test="${mockservice.id eq serviceIdToShowByDefault}">display: block;</c:if>" > 
                               
								
                                <div id="updateStatus_<c:out value="${mockservice.id}"/>" class="outputTextArea"></div>
                                <div class="parentformselected">
                                <input type="hidden" name="serviceId" id="serviceId_<c:out value="${mockservice.id}"/>" value="${mockservice.id}" />
                                <input type="hidden" name="serviceName" id="serviceName_${mockservice.id}" value="${mockservice.serviceName}" />
                                
                                 <div class="service" width="350px;">
                                    
                                   <div class="service-label">Service name: <mockey-tag:editServiceLink serviceId="${mockservice.id}"/></div>
                                   <div class="service-value big"><mockey:slug text="${mockservice.serviceName}" maxLength="40"/></div>
                                   <mockey-tag:statusCheck lastVisit="${mockservice.lastVisit}" tag="${mockservice.tag}" serviceName="${mockservice.serviceName}" serviceId="${mockservice.id}"/>
                                   <div class="service-label border-top">Mock URL: <mockey-tag:editServiceLink serviceId="${mockservice.id}"/></div>
                                   <div><a class="tiny" href="<mockey:url value="${mockservice.url}"/>"><mockey:url value="${mockservice.url}" /></a></div>
                                   <div class="service-def-spacer"></div>
                                   
                                   <div class="service-label not-top border-top">Real URL(s): <mockey-tag:editServiceLink serviceId="${mockservice.id}"/></div>
                                   <c:forEach var="realUrl" items="${mockservice.realServiceUrls}" varStatus="status" >
								       <p><a class="tiny" href="<mockey:url value="${realUrl}"/>"><mockey:url value="${realUrl}" breakpoint="5"/></a></p>
								   </c:forEach>
								   
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
                                   <span style="float:right;" class="power-link tiny"><a href="#" class="createScenarioLink" id="createScenarioLink_${mockservice.id}">Create Scenario</a></span>
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
		                                      
		                                      
											       <span class="tag_word status-info">${scenario.tag}</span> 
											  
											  <span>
											  
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
                                 
								
                                 <div class="not-top border-top" style="padding-top:1em;">
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
		        <jsp:include page="/WEB-INF/common/inc_scenario_create_dialog.jsp" />
		       
		        <div id="dialog" title="Scenerio Preview">
                    <p>Details appended here.</p>
                </div>
                <div id="dialog-delete-service-confirm" title="Delete Service">
                    <p>Are you sure you want to delete this Service?</p>
                </div>
                <div id="dialog-delete-scenario-confirm" title="Delete Service Scenario">
                    <p>Are you sure you want to delete this Scenario?</p>
                </div>
                <div id="dialog-delete-service-plan-confirm" title="Delete Service Plan">
                    <p>Are you sure you want to delete this Service Plan?</p>
                </div>
                
    </div>
	        </c:when>
	        <c:otherwise>
	          <p class="intro_txt">Hello, this is Mockey. It looks like this is the first time you are using Mockey because 
	          it's in a clean state. To learn more about Mockey, see the <a href="<c:url value="/help"/>">help</a> section. </p> 
			  <p class="info_message">There are no mock services defined. You can <a href="<c:url value="upload"/>">upload one</a>, <a href="<c:url value="setup"/>">create one manually</a> or start <a href="<c:url value="help#record"/>">recording</a>. 
			  If you were expecting to see services, then maybe filtering is on
			  
			   or something went wrong. Checkout the <a href="<c:url value="/console"/>">debug output</a>.
			   
			    
			  </p>
			  
			  <c:if test="${not empty sessionScope.FILTER_SESSION_TAG}"><p class="info_message">Hey! You're filtering on '<strong>${sessionScope.FILTER_SESSION_TAG}</strong>' <a style="margin-left:40px;" href="#" class="clear-tag-button">Clear Filter by Tag(s)</a> </p></c:if>
			  
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