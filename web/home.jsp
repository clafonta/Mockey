<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="pageTitle" value="Home" scope="request" />
<c:set var="currentTab" value="home" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
<%
    java.util.Map cookieTable = new java.util.HashMap();
    String moodImage = null;
    javax.servlet.http.Cookie[] cookies = request.getCookies();
    
    if(cookies!=null){
	    for (int i=0; i < cookies.length; i++){
	        cookieTable.put(cookies[i].getName(), cookies[i].getValue());
	    }
	    moodImage = request.getParameter("mood");
	    if(moodImage!=null){
	    	javax.servlet.http.Cookie myCookie = new Cookie("mood", moodImage);
		    //myCookie.setMaxAge(0);
		    //myCookie.setDomain(".somedomain.com");      
		    response.addCookie(myCookie);
	    }
	    else if (cookieTable.containsKey("mood")) {
	        moodImage = (String)cookieTable.get("mood");
	    }
    }

    
%>
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
	
	
    $('.tiny_service_delete').each( function() {
        $(this).click( function() {
            var serviceId = this.id.split("_")[1];
            $.prompt(
                'Are you sure you want to delete this Service?',
                {
                    callback: function (proceed) {
                        if(proceed) document.location="<c:url value="/setup" />?deleteService=yes&serviceId="+ serviceId;
                    },
                    buttons: {
                        'Delete Service': true,
                        Cancel: false
                    }
                });
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
                 
			$('#serviceResponseType_0_'+serviceId).addClass("response_not");
			$('#serviceResponseType_1_'+serviceId).addClass("response_not");
			$('#serviceResponseType_2_'+serviceId).addClass("response_not");
			$('#staticScenario_'+serviceId).removeClass("show").addClass("hide");
			$('#proxyScenario_'+serviceId).removeClass("show").addClass("hide");
			$('#dynamicScenario_'+serviceId).removeClass("show").addClass("hide");
			
			if(responseType == 0){
				$('#serviceResponseType_0_'+serviceId).removeClass("response_not").addClass("response_set");
				$('#proxyScenario_'+serviceId).addClass("show");
			}else if(responseType == 1){
				$('#serviceResponseType_1_'+serviceId).removeClass("response_not").addClass("response_set");
				$('#staticScenario_'+serviceId).addClass("show");
			}else if(responseType == 2){
				$('#serviceResponseType_2_'+serviceId).removeClass("response_not").addClass("response_set");
				$('#dynamicScenario_'+serviceId).addClass("show");
			}
			$('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
		});
	});
    
 });
</script>
    <div id="main">
        <%@ include file="/WEB-INF/common/message.jsp" %>
        <c:choose>
	        <c:when test="${!empty services}">
	        
	            <c:set var="serviceIdToShowByDefault" value="<%= request.getParameter("serviceId") %>" scope="request"/>
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
								  <p> Make all 
									  <a id="allresponsetype_0" class="allresponsetype response_proxy" href="#">Proxy</a>
									  <a id="allresponsetype_1" class="allresponsetype response_static" href="#">Static</a>
									  <a id="allresponsetype_2" class="allresponsetype response_dynamic" href="#">Dynamic</a>
								  </p>
								  <div class="scroll">
		                            	<c:forEach var="mockservice" items="${services}"  varStatus="status">	  
			                                <div id="parentform_${mockservice.id}" class="parentform <c:if test="${mockservice.id eq serviceIdToShowByDefault}">parentformselected</c:if>" >
				                                <span style="float:right;"><a class="tiny_service_delete remove_grey" id="deleteServiceLink_<c:out value="${mockservice.id}"/>" title="Delete this service" href="#">x</a></span>
				                                <div class="toggle_button" style="margin:0.2em;">
												      <a class="gt" onclick="return true;" href="#" id="togglevalue_<c:out value="${mockservice.id}"/>"><mockey:slug text="${mockservice.serviceName}" maxLength="30"/></a>
												</div>
				                                <mockey:service type="${mockservice.serviceResponseType}" serviceId="${mockservice.id}"/>
				                                <c:if test="${empty mockservice.scenarios}">
				                                  <span style="float:right;font-size:80%;color:yellow;background-color:red;padding:0 0.2em 0 0.2em;">(no scenarios)</span>
				                                </c:if>
											</div>
								    	</c:forEach>
								    </div>
							    </div>
							    <div id="tabs-2">
								    <div class="scroll">
								        <div>To <strong>create a plan</strong>, go to the Services tab, make your settings, and
								        then tab to here to create (or save). 
								        </div>
								        <div style="padding:1em 0em;">
								        <input type="text" id="servicePlanName" name="servicePlanName"></input>
								        <button id="create-plan">Create plan</button>
								        
								        </div>
								        <div style="padding-bottom:1em;">To <strong>set a plan</strong>, click on the plan name below. You will be redirected to the Services tab.  
								        </div>
								         <c:if test="${empty plans}">
									      <div class="info_message" id="no-plans-msg"> No plans here. </div>
									    </c:if>
									    <div id="plan-list">
									    <c:forEach var="plan" items="${plans}"  varStatus="status">	  
			                                <div id="plan_${plan.id}" class="parentform" ><a href="#" id="set-plan_${plan.id}" class="set-plan">${plan.name}</a>
			                                <span style="float:right;"><a class="delete-plan remove_grey" id="delete-plan_<c:out value="${plan.id}"/>" title="Delete this plan" href="#">x</a></span>
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
							
						  <%
					      if("geometry.jpg".equals(moodImage)){
					        %><img style="vertical-align:middle" src="<c:url value="/images/geometry.jpg" />" /><%
					      }else if("unicorn.jpg".equals(moodImage)) {
					         %><img style="vertical-align:middle" src="<c:url value="/images/unicorn.jpg" />" /><%
					      }else if("lebowski.png".equals(moodImage)) {
					         %><img style="vertical-align:middle" src="<c:url value="/images/lebowski.png" />" /><%
					      }else {
					         %><img style="vertical-align:middle" src="<c:url value="/images/silhouette.jpg" />" /><%
					      }
					      %>
							
							</div>
							
							<c:forEach var="mockservice" items="${services}">
							   <div id="div_<c:out value="${mockservice.id}"/>_" class="service_div display" style="<c:if test="${mockservice.id eq serviceIdToShowByDefault}">display: block;</c:if>" > 
                               
								
                                <div id="updateStatus_<c:out value="${mockservice.id}"/>" class="outputTextArea"></div>
                                <div class="parentformselected">
                                <input type="hidden" name="serviceId" id="serviceId_<c:out value="${mockservice.id}"/>" value="${mockservice.id}" />
                                
							    <div class="service_edit_links">
	                            <c:url value="/setup" var="setupUrl">
	                                <c:param name="serviceId" value="${mockservice.id}" />
	                             </c:url>
	                             <c:url value="/setup" var="deleteUrl">
	                                <c:param name="serviceId" value="${mockservice.id}" />
	                                <c:param name="delete" value="true" />
	                             </c:url>
	                             <a class="tiny" href="<c:out value="${setupUrl}"/>" title="Edit service definition">edit</a> |
	                             <a class="tiny_service_delete" id="deleteServiceLink_<c:out value="${mockservice.id}"/>" title="Delete this service" href="#">delete</a> |
	                             <a class="tiny" href="<c:url value="/home"/>" title="hide me">hide</a> 
                                 </div>
                                 
                                 <div class="service">
                                   <div class="service-label">Service name:</div>
                                   <div class="service-value">${mockservice.serviceName}</div>
                                   <div class="service-label not-top">Mock URL:</div>
                                   <div><a class="tiny" href="<mockey:url value="${mockservice.url}"/>"><mockey:url value="${mockservice.url}" /></a></div>
                                   <div class="service-label not-top">Real URL(s):</div>
                                   <table class="simple">
                                   <c:forEach var="realUrl" items="${mockservice.realServiceUrls}" varStatus="status" >
								     
								       <tr>
								       <td width="10px;"> 
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
								       <td>
								       <a href="<mockey:url value="${realUrl}"/>"><mockey:url value="${realUrl}" breakpoint="5"/></a></td>
								       </tr>
								     
								     </c:forEach>
								   </table>
                                   <c:if test="${empty mockservice.realServiceUrls}">
                                   <div class="info_message">No real URLS defined.</div>
                                   </c:if>
                                   <div class="service-label not-top">This service is set to:</div>
                                   <div class="service-value">
                                   	<span class="hide<c:if test="${mockservice.serviceResponseType eq 2}"> show</c:if>" id="dynamicScenario_${mockservice.id}">Dynamic</span>
							       	<span class="hide<c:if test="${mockservice.serviceResponseType eq 0}"> show</c:if>" id="proxyScenario_${mockservice.id}">Proxy</span>
			                       	<span class="hide<c:if test="${mockservice.serviceResponseType eq 1}"> show</c:if>" id="staticScenario_${mockservice.id}">Static</span>
                                   </div>
                                   <div class="service-label not-top">Select a static scenario:</div>
                                   <div>
                                   <ul id="simple" class="group">
	                                    
		                                <c:choose>
		                                  <c:when test="${not empty mockservice.scenarios}">
		                                  <c:forEach var="scenario" begin="0" items="${mockservice.scenarios}">
		                                    <li style="padding-top: 0.5em;">
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
		                                      <mockey:slug text="${scenario.scenarioName}" maxLength="40"/>
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
		                                  	<li class="alert_message"><span>You need to <a href="<c:out value="${setupScenarioUrl}"/>" title="Create service scenario" border="0" />create</a>
		                                     a scenario before using "Static or Dynamic Scenario".</span></li>
		                                  </c:otherwise>
		                                </c:choose>
	                                </ul>
                                   </div>
                                 </div>
                                 
								
                                 <div>
			                       <strong>Hang time (milliseconds):</strong> ${mockservice.hangTime} 
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
    </div>
	        </c:when>
	        <c:otherwise>
			  <p class="info_message">There are no mock services defined. You can <a href="<c:url value="upload"/>">upload one</a>, <a href="<c:url value="setup"/>">create one manually</a> or start <a href="<c:url value="help#record"/>">recording</a>. </p>
			</c:otherwise>
        </c:choose>
<c:if test="${mode ne 'edit_plan'}">
<script type="text/javascript">$('html').addClass('js');

$(function() {

  $('a','div.toggle_button').click(function() {
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
<c:if test="${!empty services}">
<div style="display:block;text-align:right;">
<p>
mood image <a href="home?mood=silhouette.jpg" class="mood" >A</a>
<a href="home?mood=unicorn.jpg" class="mood" >B</a>
<a href="home?mood=geometry.jpg" class="mood" >C</a>
<a href="home?mood=lebowski.png" class="mood" >D</a>
</p>
</div>
</c:if>

<jsp:include page="/WEB-INF/common/footer.jsp" />