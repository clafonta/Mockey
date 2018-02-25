<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%@ taglib prefix="mockey-tag" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="Home" scope="request" />
<c:set var="currentTab" value="home" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
<script>
$(document).ready( function() {
	
	// When user clicks browser 'back' or 'forward', we
	// force a reload to ensure we see up-to-date view
	window.onpopstate = function(e){
	   window.location.reload(false); 
	};
	
	$('#tabs').tabs().hide();
	$('#tabs:not(:first)').hide();
    $('#tabs:first').fadeIn('slow');
	
    $('.invisible-focusable').addClass("invisiblefield");  
	$('.invisible-focusable').focus(function() {  
        $(this).removeClass("invisiblefield").addClass("invisiblefiled-in-focus");  
    });  
	$('.invisible-focusable').blur(function() {  
        $(this).removeClass("invisiblefiled-in-focus").addClass("invisiblefield");   
    });
    
    $('.toggle-filter-view').each( function() {
        $(this).click( function() {
        	$('#filter_view_div').toggle();
        	$('.filter-toggle-txt').toggle();
        });
     });
     
     $('.toggle-service-meta-data').each( function() {
        $(this).click( function() {
        	$('.service-meta-data').toggle();
        });
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
            $('#dialog-create-plan').dialog({height: 250, modal: true});       
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
              return false;
            });
        });
	
	$('.delete-plan').each( function() {
		$(this).click( function() {
			var planId = this.id.split("_")[1];
			$('#dialog-delete-service-plan-confirm').dialog({height: 200, modal: true });
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
          return false;
		});
	});

	$('.set-plan').each( function() {
		$(this).click( function() {
			var planId = this.id.split("_")[1];
			$.post('<c:url value="/plan/setup"/>', { action: 'set_plan', plan_id: planId, type: 'redirect' } ,function(data){
				  
				   if(data.result.success){
				       //Send the plan ID to highlight the Services included in the plan.
					   document.location="<c:url value="/home" />?plan_id="+planId; 
				    }
			}, 'json' );
			
		});
	});


	$('.save-plan').each( function() {
        $(this).click( function() {
            var planId = this.id.split("_")[1];
            var servicePlanName = $('input[name=servicePlanName_'+planId+']').val();
			var serviceIds = new Array();
			    $.each($('input:checkbox[name=service_plan_include_checkbox]:checked'), function() {
			    	serviceIds.push($(this).val());
			       
			    });
            $.post('<c:url value="/plan/setup"/>', { action: 'save_plan', plan_id: planId, service_plan_name: servicePlanName, type: 'json', 'service_ids[]': serviceIds} ,function(data){
                  
                   if(data.result.success){
                       $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
                    }
            }, 'json' );
            return false;
        });
    });
    
    $('.save-as-default-plan').each( function() {
        $(this).click( function() {
            
            var planId = this.id.split("_")[1];
            if($(this).hasClass("response_green")) {
            	$.post('<c:url value="/plan/setup"/>', { action: 'set_as_default_plan', plan_id: 'none', type: 'json'} ,function(data){
	                   if(data.result.success){
	                       $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
	                       $('#save-as-default-plan_'+planId).removeClass("response_green").addClass("response_not");
	                       $('#not-service-plan_'+planId).show();
	                       $('#yes-service-plan_'+planId).hide();
	                    }
	            }, 'json' );
	            return false;
            }else {
            
	            $.post('<c:url value="/plan/setup"/>', { action: 'set_as_default_plan', plan_id: planId, type: 'json'} ,function(data){
	            
	                   if(data.result.success){
	                       $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
	                       $('.save-as-default-plan').each( function() {
	                       		$(this).removeClass("response_green").addClass("response_not");
	                       });
	                       $('#save-as-default-plan_'+planId).removeClass("response_not").addClass("response_green");
	                       $('.not-service-plan').each( function() {
	                       		$(this).show();
	                       });
	                        $('.yes-service-plan').each( function() {
	                       		$(this).hide();
	                       });
	                       $('#not-service-plan_'+planId).hide();
	                       $('#yes-service-plan_'+planId).show();
	                    }
	            }, 'json' );
	            return false;
	        }
            
            
        });
    });

	
    $('.tiny_service_delete').each( function() {
        $(this).click( function() {
            var serviceId = this.id.split("_")[1];
            $('#dialog-delete-service-confirm').dialog({modal: true, height: 200 });
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
    
     $('.tiny_service_duplicate').each( function() {
        $(this).click( function() {
            var serviceId = this.id.split("_")[1];
            $('#dialog-duplicate-service-confirm').dialog({height: 200, modal: true });
            $('#dialog-duplicate-service-confirm').dialog('open');
	            $('#dialog-duplicate-service-confirm').dialog({
	                buttons: {
	                  "Duplicate service": function() {
	            	      document.location="<c:url value="/setup" />?duplicateService=yes&serviceId="+ serviceId;
	                  }, 
	                  Cancel: function(){
		                  $(this).dialog('close');
	                  }
	                }
	          });  
	          return false;
            });
        });

    
    
    $('.deleteScenarioLink').each( function() {
        $(this).click( function() {
        	var scenarioId = this.id.split("_")[1];
            var serviceId = this.id.split("_")[2];
            $('#dialog-delete-scenario-confirm').dialog({height: 200, modal: true });
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

    
    $('.service-view-master-link').each( function() {
        $(this).click(function(){   
          var serviceId = this.id.split("_")[1]; 	
          $(".parentform").removeClass("parentformselected");
    	  $("#parentform_"+serviceId).addClass("parentformselected");
    	  
    	  // We push the new URL, to ensure the user returns 
    	  // to this view and is forced a reload (see 'onpopstate')
    	  // when user uses 'back/forward' browser controls. 
    	  window.history.pushState('page2', document.title, window.location.pathname + '?serviceId='+serviceId);
    	  
        });
     });
    
   $('.service-scenario-tag-remove').each( function() {
        $(this).click( function() {
            var scenarioId = this.id.split("_")[1];
			var serviceId = this.id.split("_")[2];
			var tagCount= this.id.split("_")[3];
			var tagId = 'service-scenario-tag-id_'+scenarioId+'_'+serviceId+'_'+tagCount;
			var filterTag = $('#'+tagId).attr('value');
            $.post('<c:url value="/taghelp"/>', { action: 'delete_tag_from_scenario', tag: filterTag, scenarioId: scenarioId, serviceId: serviceId } ,function(data){
					   if(data.success){
						   $('#'+tagId).hide();
        				   $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
					    }else {
					       alert("Hmm...");
					    }
				}, 'json' );
        	
        });
   });
    $('.service-plan-tag-remove').each( function() {
        $(this).click( function() {
            var servicePlanId = this.id.split("_")[1];
			var tagCount= this.id.split("_")[2];
			var tagId = 'service-plan-tag-id_'+servicePlanId+'_'+tagCount;
			var filterTag = $('#'+tagId).attr('value');
            $.post('<c:url value="/taghelp"/>', { action: 'delete_tag_from_service_plan', tag: filterTag, servicePlanId: servicePlanId } ,function(data){
					   if(data.success){
						   $('#'+tagId).hide();
        				   $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
					    }else {
					       alert("Hmm...");
					    }
				}, 'json' );
        	
        });
   });
   $('.service-tag-remove').each( function() {
        $(this).click( function() {
            var serviceId = this.id.split("_")[1];
			var tagCount= this.id.split("_")[2];
			var view= this.id.split("_")[3];
			var tagId = 'service-tag-id_'+serviceId+'_'+tagCount;
			var filterTag = $(this).attr('value');
            $.post('<c:url value="/taghelp"/>', { action: 'delete_tag_from_service', tag: filterTag, serviceId: serviceId } ,function(data){
					   if(data.success){
						   $('#'+tagId+'_detail').hide();
						   $('#'+tagId+'_master').hide();
        				   $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
					    }else {
					       alert("Hmm...");
					    }
				}, 'json' );
        	
        });
   });
       
   $('.service-lastvisit-remove').each( function() {
        $(this).click( function() {
            var serviceId = this.id.split("_")[1];
            $.post('<c:url value="/lastvisithelp"/>', { action: 'clear_last_visit', serviceId: serviceId } ,function(data){
					   if(data.success){
					       $('#remove-service-last_'+serviceId+'_detail').hide();
					       $('#remove-service-last_'+serviceId+'_master').hide();
        				   $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
					    }else {
					       alert("Hmm...");
					    }
				}, 'json' );
        	
        });
   });
   $('.scenario-lastvisit-remove').each( function() {
        $(this).click( function() {
            var scenarioId = this.id.split("_")[1];
            var serviceId = this.id.split("_")[2];
            $.post('<c:url value="/lastvisithelp"/>', { action: 'clear_last_visit', serviceId: serviceId, scenarioId: scenarioId } ,function(data){
					   if(data.success){
					       $('#remove-scenario-last_'+scenarioId+'_'+serviceId).hide();
        				   $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
					    }else {
					       alert("Hmm...");
					    }
				}, 'json' );
        	
        });
   });
   $('.service-plan-lastvisit-remove').each( function() {
        $(this).click( function() {
            var servicePlanId = this.id.split("_")[1];
            $.post('<c:url value="/lastvisithelp"/>', { action: 'clear_last_visit', servicePlanId: servicePlanId } ,function(data){
					   if(data.success){
					       $('#remove-service-plan-last_'+servicePlanId).hide();
        				   $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
					    }else {
					       alert("Hmm...");
					    }
				}, 'json' );
        	
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
    $("#dialog-duplicate-service-confirm").dialog({ resizable: false, height: 120, modal: false, autoOpen: false });
    $("#dialog-tag-manage").dialog({ resizable: false, height: 420, minHeight: 450, modal: true, autoOpen: false });
    $('.hideServiceScenarioLink').each( function() {
        $(this).click( function() {
           
        });
    });
    $("#servicePlanSetMessgeLink").click( function() {
    	$("#servicePlanSetMessge").hide();
    	return false;
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
    
		<span class="basic_label">Filter services with tags</span> (<span class="power-link tiny"><a href="javascript:void(0);" class="manageTagLink ">Tag Helper</a></span>):

		
		<c:forEach var="filterTagItem" items="${filterTagList}"  varStatus="status">
			<c:choose>
				<c:when test='${filterTagItem.state}'>
					<c:set var="toggle_value_class" value="tag_word_selected" />
				</c:when>
				<c:otherwise>
					<c:set var="toggle_value_class" value="" />
				</c:otherwise>
			</c:choose>
			<a href="javascript:void(0);" class="filter-tag-item tiny ${toggle_value_class}" id="${filterTagItem.value}" >${filterTagItem.value}</a>
		</c:forEach>

		<br />
		
		<div style="margin-top:5px;">
			  <input type="text" style="width:80%;" value="${term}" placeholder="Service name, scenario name, or mock url" class="text ui-corner-all ui-widget-content" name="search_term" id="search_term">
			  <button class="hhButton" id="search_me" style="width: 80px;">Search</button>
	    </div> 
	
	</div>

        <%@ include file="/WEB-INF/common/message.jsp" %>
        <!-- SERVICE PLAN CREATE DIALOG -->
        <div id="dialog-tag-manage" title="Tag Helper">
            <p><strong>WARNING:</strong>
            This will remove tag(s) from each Service, Scenario, and Service Plan. 
            <input type="text" name="filter-tag" id="filter-tag" placeholder="Enter tag(s) here" class=" text ui-widget-content ui-corner-all" />
            <ul class="button-list">
            <li><a href="javascript:void(0);" class="hhButtonRed" style="color:#FFFFFF;" id="delete-tag-button">Remove tag(s) from all things.</a></li>
            </ul>
            </p>
        </div>
        
        <!-- SERVICE PLAN CREATE DIALOG -->
        <div id="dialog-create-plan" title="Service Plan">
            <p>
            <fieldset>
                <label for="service_plan_name">Service Plan name</label>
                <input placeholder="Give a descriptive service plan name." type="text" name="service_plan_name" id="service_plan_name" class="text ui-widget-content ui-corner-all" />
                <label for="service_plan_tag" class="blur">Tag(s) - <i>optional</i></label>
                <input type="text" name="service_plan_tag" id="service_plan_tag" placeholder="Optional tags here." class="text ui-widget-content ui-corner-all" />
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

		        <div class="flex_wrapper">
					<div class="flex_item">
							<div id="tabs" style="display:none;">
								<ul>
									<li><a href="#tabs-1" style="font-weight:100;">Services (${fn:length(services)})</a></li>
									<li><a href="#tabs-2" style="font-weight:100;">Plans (${fn:length(plans)})</a></li>
								</ul>
							  	<div id="tabs-1">
				                    <div style="text-align:right;">
										<span class="power-link tiny"><a href="javascript:void(0);" class="toggle-service-meta-data"><span class="service-meta-data">Show Service Details</span></a><span class="service-meta-data" style="display:none;"><a href="<c:url value="/home" />">Hide Service Details</a></span> </span> |
										<span class="power-link tiny"><a href="javascript:void(0);" class="createPlanLink" id="createPlanLink">Create Service Plan</a></span>
				                    </div>
							  	  <c:if test="${!empty servicePlan}">
							  	  <div class="info_message tiny" id="servicePlanSetMessge">
							  	  <span style="float:right;">
							  	  <a href="javascript:void(0);" id="servicePlanSetMessgeLink" class="remove_grey" title="Hide this message." style="text-decoration:none;">Dismiss</a>
							  	  </span>
							  	  Plan that was just set:  
							  	  <h2>
							  	  <strong style="display:block; word-wrap: break-word;" title="${servicePlan.name}">${servicePlan.name}</strong>
							  	  </h2>
							  	  <span class="tiny">Service Plan Id: ${servicePlan.id}</span>
							  	  </div>
							  	  </c:if>
							  	  <div class="info_message tiny service-meta-data" style="display:none;">
							  	  Click one of the following buttons to set 
							  	  response type for <strong>each service</strong>.
								  <p> 
									  <a id="allresponsetype_0" class="allresponsetype response_not" style="text-decoration:none;" href="javascript:void(0);"> Proxy </a>
									  <a id="allresponsetype_1" class="allresponsetype response_not" style="text-decoration:none; margin-left:2px;margin-right:2px;" href="javascript:void(0);"> Static </a>
									  <a id="allresponsetype_2" class="allresponsetype response_not" style="text-decoration:none;" href="javascript:void(0);">Dynamic</a>
								  </p>
								  </div>
								  
								  <div class="">
		                            	<c:forEach var="mockservice" items="${services}"  varStatus="status">	  
			                                <div id="parentform_${mockservice.id}" class="parentform <c:if test="${mockservice.id eq serviceIdToShowByDefault}">parentformselected</c:if>" >
			                                
				                            	<c:url value="/setup" var="setupUrl">
				                                	<c:param name="serviceId" value="${mockservice.id}" />
				                             	</c:url>
				                             	
				                             	
				                                <span style="float:right; display:none;" class="service-meta-data">
				                                
				                                <a class="tiny_service_delete remove_grey" id="deleteServiceLink_<c:out value="${mockservice.id}"/>" title="Delete this service" href="javascript:void(0);"><i aria-hidden="true" class="icon-cancel"></i></a>
				                                </span>
												
												<div class="toggle-buttons" style="margin-bottom:8px;">

				                                  <span class="toggle_button tiny">
													  <c:if test="${empty mockservice.scenarios}">
														  <span title="No scenarios defined for this service." class="icon-info no-scenario-defined-warning"> </span>
													  </c:if>
												      <a class="service-view-master-link" onclick="return true;" href="javascript:void(0);" id="togglevalue_<c:out value="${mockservice.id}"/>" title="${mockservice.serviceName}">${mockservice.serviceName}</a>
												  </span>
												  <mockey-tag:conflictFlag service="${mockservice}" conflictInfo="${conflictInfo}"/>
						                        </div>
						                        <div class="service-meta-data" style="display:none;">
													<mockey:service type="${mockservice.serviceResponseType}" serviceId="${mockservice.id}"/>

													<mockey-tag:statusCheckByService service="${mockservice}" view="master"/>
													<div>
													<a class="tiny_service_duplicate" id="duplicateServiceLink_<c:out value="${mockservice.id}"/>" title="Duplicate this service" href="javascript:void(0);">Clone</a>
													</div>
													<div class="tiny" style="font-size: 12px;">
													Check the box to include this service in a "Save As Plan".  
													<input type="checkbox" name="service_plan_include_checkbox" value="${mockservice.id}" <mockey-tag:serviceInServicePlanFlag service="${mockservice}" servicePlan="${servicePlan}"/> />
													</div>
												</div>
											</div>
								    	</c:forEach>
								    	
								    </div>
							    </div>
							    
							    <div id="tabs-2">
							        
							        <div style="text-align:right;">
							        <span class="power-link tiny"><a href="javascript:void(0);" class="createPlanLink" id="createPlanLink">Create Service Plan</a></span></div>
								    <div class="scroll">
								         <c:if test="${empty plans}">
									      <div class="info_message" id="no-plans-msg"> No plans here - yet! You should make one. </div>
									    </c:if>
									    <div id="plan-list">
									    <c:forEach var="plan" items="${plans}"  varStatus="status">	  
			                                <div id="plan_${plan.id}" class="parentform" >
				                                <span style="float:right;"><a class="delete-plan remove_grey" id="delete-plan_${plan.id}" title="Delete this plan" href="javascript:void(0);"><i aria-hidden="true" class="icon-cancel"></i></a></span>
				                                
				                                <input type="text" style="width:90%;" id="servicePlanName_${plan.id}" class="invisible-focusable invisiblefield" name="servicePlanName_${plan.id}" value="${plan.name}"></input>
				                                <mockey-tag:statusCheckByServicePlan servicePlan="${plan}"/>
				                                <div style="padding-top:0.6em;"> 
					                                  
				                                  <a id="set-plan_${plan.id}" class="set-plan response_not" style="text-decoration:none;" href="javascript:void(0);" title="Enable this plan">Enable Plan</a> &nbsp;
				                                  <a id="save-plan_${plan.id}" class="save-plan response_not" style="text-decoration:none;" href="javascript:void(0);" title="Save settings as this plan.">Save As Plan</a>
				                                  
				                                  <div style="padding-top:1em;font-size: 0.8em; align:right;" class="tiny">
				                                  <hr /> 
				                                  Default plan upon startup?
				                                  <a id="save-as-default-plan_${plan.id}" class="save-as-default-plan <c:choose><c:when test="${plan.id eq defaultServicePlanId}">response_green</c:when><c:otherwise>response_not</c:otherwise></c:choose>" style="text-decoration:none;" href="javascript:void(0);" title="Set as the default plan upon Mockey startup.">
				                                  <span id="not-service-plan_${plan.id}" class="not-service-plan on_off" style="<c:if test="${plan.id eq defaultServicePlanId}">display:none;</c:if>">No</span>
				                                  <span id="yes-service-plan_${plan.id}" class="yes-service-plan on_off" style="<c:choose><c:when test="${plan.id eq defaultServicePlanId}"></c:when><c:otherwise>display:none;</c:otherwise></c:choose>">Yes</span>
				                                  </a>
				                                  </div>
				                                </div>
			                                </div>
									    </c:forEach>
									    <div class="tiny" style="padding-top:1em;" id="no-plans-msg"><a href="<c:url value="help#plan"/>">What's a plan?</a></div>
				                            <div class="info_message tiny">
					                            <p>To <strong>create a plan</strong>, go to the Services tab, make your settings, and
		                                        then tab to here to create (or save). </p>
		                                        <p>To <strong>enable a plan</strong>, click on the <b>Enable Plan</b> link. Note: you 
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
					</div>
					<div class="flex_item">
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
                                    
                                   <div class="service-label"><label>Service name:</label> <mockey-tag:editServiceLink serviceId="${mockservice.id}"/></div>
                                   <div class="service-value big">${mockservice.serviceName}</div>
                                   <div class="service-meta-data" style="display:none;">
                                   <mockey-tag:statusCheckByService service="${mockservice}" view="detail"/>
                                   </div>
                                   <div class="service-label border-top"><label>Mock URL:</label> <mockey-tag:editServiceLink serviceId="${mockservice.id}"/></div>
                                   <div><a class="tiny" href="<mockey:url value="${mockservice.url}"/>"><mockey:url value="${mockservice.url}" /></a></div>
                                   <div class="service-def-spacer"></div>
                                   
                                   
                                   <div class="service-meta-data" style="display:none;">
	                                   <div class="service-label not-top border-top">
	                                   <label>Real URL(s):</label> <mockey-tag:editServiceLink serviceId="${mockservice.id}"/>
	                                   </div>
	                                   <c:forEach var="realUrl" items="${mockservice.realServiceUrls}" varStatus="status" >
									       <p><a class="tiny" href="<mockey:url value="${realUrl}"/>"><mockey:url value="${realUrl}" breakpoint="5"/></a></p>
									   </c:forEach>
									   
	                                   <c:if test="${empty mockservice.realServiceUrls}">
	                                   <div class="info_message">No real URLS defined.</div>
	                                   </c:if>
                                   </div>
                                   <div class="service-label border-top"><label>This service is set to:</label>
                                   	
                                   </div>
                                   <table>
                                   <tr><td>
                                   <mockey:service type="${mockservice.serviceResponseType}" serviceId="${mockservice.id}"/>
                                   </td>
                                   <td>
                                   <div class="service-value" style="diplay:inline-block;text-transform:uppercase;">
	                                   	<span class="hide<c:if test="${mockservice.serviceResponseType eq 2}"> show</c:if>" id="dynamicScenario_${mockservice.id}">Dynamic</span>
								       	<span class="hide<c:if test="${mockservice.serviceResponseType eq 0}"> show</c:if>" id="proxyScenario_${mockservice.id}">Proxy</span>
				                       	<span class="hide<c:if test="${mockservice.serviceResponseType eq 1}"> show</c:if>" id="staticScenario_${mockservice.id}">Static</span>
	                                   </div>
	                               </td>
	                               </tr>
	                               </table>
                                   
                                   
                                   <div class="service-label border-top" style="margin-top:1em;"><label>Select a static scenario (${fn:length(mockservice.scenarios)}):</label>
                                   	<span style="float:right;" class="power-link tiny"><a href="javascript:void(0);" class="createScenarioLink" id="createScenarioLink_${mockservice.id}">Create Scenario</a></span>
                                   </div>
                                   <div id="MAIN_CONTAINER" class="">
                                   <div id="scenario-list_${mockservice.id}">
		                                <c:choose>
		                                  <c:when test="${not empty mockservice.scenarios}">
		                                  <c:forEach var="scenario" begin="0" items="${mockservice.scenarios}" varStatus="status"  >
		                                    <div class="service-detail-scenario-list-item" id="service-scenario-info_${scenario.id}_${mockservice.id}">
			                                    <span class="service-meta-data" style="float:right;padding-top:18px; display:none;"><a href="javascript:void(0);" id="delete-scenario_${scenario.id}_${mockservice.id}" class="deleteScenarioLink remove_grey"><i aria-hidden="true" class="icon-cancel"></i></a> </span>
			                                    <div style="padding-top: 0.5em;padding-bottom:0.5em;">
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
			                                      <a href="javascript:void(0);" id="serviceScenarioON_${scenario.id}_${mockservice.id}" class="scenariosByServiceId-on_${mockservice.id} ${on_class} response_set" onclick="return false;"><p class="on_off">ON</p></a>
			                                      <a href="javascript:void(0);" id="serviceScenarioOFF_${scenario.id}_${mockservice.id}" class="serviceScenarioResponseTypeLink scenariosByServiceId-off_${mockservice.id} ${off_class} response_not" onclick="return false;"><p class="on_off">OFF</p></a>
			                                      <a href="javascript:void(0);" id="view-scenario_${scenario.id}_${mockservice.id}" title="${scenario.scenarioName}" class="viewServiceScenarioLink" style="padding-left:10px;">${scenario.scenarioName}</a>
			                                    </div>
			                                    <div class="service-meta-data" style="display:none;">
			                                    <mockey-tag:statusCheckByScenario scenario="${scenario}" service="${mockservice}"/>
			                                    </div>
		                                    </div>
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
		                                  	<div class="alert_message"><span>You need to create a scenario before using <strong>Static</strong> or <strong>Dynamic</strong> scenario</span></div>
		                                  </c:otherwise>
		                                </c:choose>
	                                </div>
                                   </div>
                                 </div>
                                 <div class="not-top border-top service-meta-data" style="display:none;padding-top:1em;">
			                       <strong>Hang time (milliseconds):</strong> ${mockservice.hangTime} <mockey-tag:editServiceLink serviceId="${mockservice.id}"/>
	                             </div>
                              </div>
                              </div>
                              </c:forEach>
                              </div>
					</div>
		        </div>
		        <jsp:include page="/WEB-INF/common/inc_scenario_create_dialog.jsp" />
		       
		        <div id="dialog" title="Scenerio Preview">
                    <p>Details appended here.</p>
                </div>
                <div id="dialog-delete-service-confirm" title="Delete Service">
                    <p>Are you sure you want to delete this Service?</p>
                </div>
                 <div id="dialog-duplicate-service-confirm" title="Duplicate Service">
                    <p>Are you sure you want to duplicate this Service? You'll have a chance to rename it.</p>
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
			  
			  <c:if test="${not empty filterTag}"><p class="info_message">Hey! You're filtering on '<strong>${filterTag}</strong>' <a style="margin-left:40px;" href="javascript:void(0);" class="clear-tag-button">Clear Filter by Tag(s)</a> </p></c:if>
			  
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