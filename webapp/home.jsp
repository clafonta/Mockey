<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="pageTitle" value="Home" scope="request" />
<c:set var="currentTab" value="home" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
<%
    java.util.Map cookieTable = new java.util.HashMap();
    javax.servlet.http.Cookie[] cookies = request.getCookies();
    for (int i=0; i < cookies.length; i++){
        cookieTable.put(cookies[i].getName(), cookies[i].getValue());
    }
    String moodImage = request.getParameter("mood");
    if(moodImage!=null){
    	javax.servlet.http.Cookie myCookie = new Cookie("mood", moodImage);
	    //myCookie.setMaxAge(0);
	    //myCookie.setDomain(".somedomain.com");      
	    response.addCookie(myCookie);
    }
    else if (cookieTable.containsKey("mood")) {
        moodImage = (String)cookieTable.get("mood");
    }

    
%>
<script>
$(document).ready( function() {
    $('.tiny_service_delete').each( function() {
        $(this).click( function() {
            var serviceId = this.id.split("_")[1];
            $.prompt(
                'Are you sure you want to delete this Service?',
                {
                    callback: function (proceed) {
                        if(proceed) document.location="<c:url value="/setup" />?delete=true&serviceId="+ serviceId;
                    },
                    buttons: {
                        'Delete Service': true,
                        Cancel: false
                    }
                });
            });
        });
    

    $('.gt').each( function() {
        $(this).click(function(){    	
    	  $(".gt_active").removeClass("gt_active");
    	  $(this).addClass("gt_active");
        });
     });
     
    $('.serviceResponseTypeProxyLink').each( function() {
		$(this).click( function() {
			var serviceId = this.id.split("_")[1];
			$.ajax({
				type: "POST",
				url: "<c:url value="service_scenario"/>",
				data:"serviceResponseType_"+serviceId+"=0&serviceId="+serviceId
			});
			$('#serviceResponseTypeProxy_'+serviceId).removeClass("response_not");
			$('#serviceResponseTypeProxy_'+serviceId).addClass("response_proxy");
			$('#serviceResponseTypeStatic_'+serviceId).addClass("response_not");
			$('#serviceResponseTypeDynamic_'+serviceId).addClass("response_not");
			$('#proxyScenario_'+serviceId).attr('checked', true);
			
		});
	});

    $('.serviceResponseTypeStaticLink').each( function() {
		$(this).click( function() {
			var serviceId = this.id.split("_")[1];
			$.ajax({
				type: "POST",
				url: "<c:url value="service_scenario"/>",
				data:"serviceResponseType_"+serviceId+"=1&serviceId="+serviceId
			});
			$('#serviceResponseTypeProxy_'+serviceId).addClass("response_not");
			$('#serviceResponseTypeStatic_'+serviceId).removeClass("response_not");
			$('#serviceResponseTypeStatic_'+serviceId).addClass("response_static");
			$('#serviceResponseTypeDynamic_'+serviceId).addClass("response_not");
			$('#staticScenario_'+serviceId).attr('checked', true);
			
		});
	});
	$('.serviceResponseTypeDynamicLink').each( function() {
		$(this).click( function() {
			var serviceId = this.id.split("_")[1];
			$.ajax({
				type: "POST",
				url: "<c:url value="service_scenario"/>",
				data:"serviceResponseType_"+serviceId+"=2&serviceId="+serviceId
			});
			$('#serviceResponseTypeProxy_'+serviceId).addClass("response_not");
			$('#serviceResponseTypeStatic_'+serviceId).addClass("response_not");
			$('#serviceResponseTypeDynamic_'+serviceId).removeClass("response_not");
			$('#serviceResponseTypeDynamic_'+serviceId).addClass("response_dynamic");
			$('#dynamicScenario_'+serviceId).attr('checked', true);
			
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
							<td valign="top" width="35%">
	                            <c:forEach var="mockservice" items="${services}"  varStatus="status">	  
	                                <div class="parentform" style="margin-bottom:0.2em;padding:0.2em;">
	                                <span style="float:right;"><a class="tiny_service_delete remove_grey" onMouseOver="style.color='#FF0000';" id="deleteServiceLink_<c:out value="${mockservice.id}"/>" title="Delete this service" href="#">x</a></span>
	                                <div class="toggle_button" style="margin:0.2em;">
									      <a class="gt <c:if test="${mockservice.id eq serviceIdToShowByDefault}">gt_active</c:if>" onclick="return true;" href="#" title="<mockey:url value="${mockservice.serviceUrl}"/>" id="togglevalue_<c:out value="${mockservice.id}"/>"><mockey:slug text="${mockservice.serviceName}" maxLength="40"/></a>
									      
									</div>
	                                <mockey:service type="${mockservice.serviceResponseType}" serviceId="${mockservice.id}"/>
	                                <c:if test="${empty mockservice.scenarios}">
	                                  <span style="float:right;font-size:80%;color:yellow;background-color:red;padding:0 0.2em 0 0.2em;">(no scenarios)</span>
	                                </c:if>                 
	                                
									</div>
							    </c:forEach>
							</td>
							<td valign="top">
							<div id='service_list_container'>
							<div class="service_div display" style="<c:if test="${! empty serviceIdToShowByDefault}">display:none;</c:if><c:if test="${serviceIdToShowByDefault==null}">display:block;</c:if>text-align:right;">
							
						  <%
					      if("geometry.jpg".equals(moodImage)){
					        %><img src="<c:url value="/images/geometry.jpg" />" /><%
					      }else if("unicorn.jpg".equals(moodImage)) {
					         %><img src="<c:url value="/images/unicorn.jpg" />" /><%
					      }else {
					         %><img src="<c:url value="/images/silhouette.jpg" />" /><%
					      }
					      %>
							
							</div>
							
							<c:forEach var="mockservice" items="${services}">
							   <div id="div_<c:out value="${mockservice.id}"/>_" class="service_div display" style="<c:if test="${mockservice.id eq serviceIdToShowByDefault}">display: block;</c:if>" > 
                               
								<%-- LOVELY JQUERY + JSP TAGS + EL --%>
								<script type="text/javascript">
								$(document).ready(function() {
								    $.prompt.setDefaults({
								        opacity:0.2
								    });
								    $("#update_service_<c:out value="${mockservice.id}"/>").click(function(){
								        $scenario = $("input[name='scenario_<c:out value="${mockservice.id}"/>']:checked").val();
								        $serviceResponseType = $("input[name='serviceResponseType_<c:out value="${mockservice.id}"/>']:checked").val();
								        $hangTime = document.getElementById("hangTime_<c:out value="${mockservice.id}"/>").value;
								        $serviceId = document.getElementById("serviceId_<c:out value="${mockservice.id}"/>").value;
								        $httpContentType = document.getElementById("httpContentType_<c:out value="${mockservice.id}"/>").value;
								        $.post("<c:url value="/service_scenario"/>", {serviceResponseType_<c:out value="${mockservice.id}"/>:$serviceResponseType, serviceId:$serviceId,scenario_<c:out value="${mockservice.id}"/>:$scenario, hangTime_<c:out value="${mockservice.id}"/>:$hangTime, httpContentType_<c:out value="${mockservice.id}"/>:$httpContentType}, function(xml) {
								            $("#responseMessage_<c:out value="${mockservice.id}"/>").html(
								                    $("report", xml).text()
								            );
								            $.prompt("Service '<c:out value="${mockservice.serviceName}"/>' updated.", { timeout: 2000});
								        });
								        $('#serviceResponseTypeProxy_${mockservice.id}').addClass("response_not");
										$('#serviceResponseTypeStatic_${mockservice.id}').addClass("response_not");
										$('#serviceResponseTypeDynamic_${mockservice.id}').addClass("response_not");
									
										if ($serviceResponseType == '0') {
											
											$('#serviceResponseTypeProxy_${mockservice.id}').removeClass("response_not");
											$('#serviceResponseTypeProxy_${mockservice.id}').addClass("response_proxy");
										  } else if ($serviceResponseType == '1') {
												$('#serviceResponseTypeStatic_${mockservice.id}').removeClass("response_not");
												$('#serviceResponseTypeStatic_${mockservice.id}').addClass("response_static");
										  }else if ($serviceResponseType == '2') {
												$('#serviceResponseTypeDynamic_${mockservice.id}').removeClass("response_not");
												$('#serviceResponseTypeDynamic_${mockservice.id}').addClass("response_dynamic");
											  }
								    });
								});
								</script>
                                <div id="updateStatus_<c:out value="${mockservice.id}"/>" class="outputTextArea"></div>
                                
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

								 <p>Service name: <a class="gt_highlight" href="<c:out value="${setupUrl}"/>" title="Edit Service"><c:out value="${mockservice.serviceName}" /></a></p>
							     <c:set var="mockUrl"><mockey:url value="${mockservice.serviceUrl}"/></c:set>
							
							     <p>Mock URL: <a href="<c:out value="${mockUrl}"/>"><mockey:url value="${mockservice.serviceUrl}"/></a></p>
							     <input type="hidden" name="plan_item" value="<c:out value="${mockservice.id}"/>"/>
							     <p>
			                       <input type="radio" name="serviceResponseType_<c:out value="${mockservice.id}"/>" id="proxyScenario_${mockservice.id}" value="0" <c:if test='${mockservice.serviceResponseType eq 0}'>checked</c:if> />
			                       <b>Proxy</b> to this URL: <span class="highlight"><c:out value="${mockservice.realServiceUrl}" /></span>			                  
			                       <c:if test="${empty mockservice.realServiceUrl and mockservice.serviceResponseType eq 0}">
			                          <div>
			                             <p class="alert_message">You need to <a href="<c:out value="${setupUrl}"/>" title="edit">define a real URL</a></p>
			                          </div>
			                       </c:if>
			                     </p>
			                     <p>
			                        <input type="radio" name="serviceResponseType_<c:out value="${mockservice.id}"/>" id="dynamicScenario_${mockservice.id}" value="2" <c:if test='${mockservice.serviceResponseType eq 2}'>checked</c:if> />
			                        <b>Dynamic Scenario</b>
			                     </p>
                                 <p>
                                   <input type="radio" name="serviceResponseType_<c:out value="${mockservice.id}"/>" id="staticScenario_<c:out value="${mockservice.id}"/>" value="1" <c:if test='${mockservice.serviceResponseType eq 1}'>checked</c:if> />
                                   <b>Static Scenario</b>
                                   <c:if test="${empty mockservice.scenarios and mockservice.serviceResponseType ne 0}">
                                      <c:url value="/scenario" var="scenarioUrl">
                                         <c:param name="serviceId" value="${mockservice.id}" />
                                      </c:url>
                                      <div>
                                        <p class="alert_message">You need to <a href="<c:out value="${scenarioUrl}"/>" title="Create service scenario" border="0" />create</a> a scenario before using "Scenario".</p>
                                        <input type="hidden" name="serviceResponseType_<c:out value="${mockservice.id}"/>" value="false" />
                                      </div>
                                   </c:if>
                                </p>
								  <span>
	                                <ul id="simple" class="group">
	                                    <li>
	                                    Select a Static Scenario.
	                                    </li>
		                                <c:choose>
		                                  <c:when test="${not empty mockservice.scenarios}">
		                                  <c:forEach var="scenario" begin="0" items="${mockservice.scenarios}">
		                                    <li>
		                                      <input type="radio" onclick="$('#staticScenario_<c:out value="${mockservice.id}"/>').attr('checked', true);" name="scenario_<c:out value="${mockservice.id}"/>" id="scenario_<c:out value="${mockservice.id}"/>" value="<c:out value="${scenario.id}"/>"
		                                      <c:if test='${mockservice.defaultScenarioId eq scenario.id}'>checked</c:if> />
		                                      <c:url value="/scenario" var="scenarioEditUrl">
		                                        <c:param name="serviceId" value="${mockservice.id}" />
		                                        <c:param name="scenarioId" value="${scenario.id}" />
		                                      </c:url>
		                                      <a href="<c:out value="${scenarioEditUrl}"/>" title="Edit service scenario"  ><c:out value="${scenario.scenarioName}" /></a>
		                                      <c:if test="${!empty universalError and universalError.id eq scenario.id and universalError.serviceId eq mockservice.id}">
		                                        (<span class="highlight_pos tiny">Universal Error Response</span>)
		                                      </c:if>
		                                      <c:if test="${scenario.id eq mockservice.errorScenarioId}">
		                                        (<span class="highlight_pos tiny">Service Error Response</span>)
		                                      </c:if>
		                                    </li>
		                                  </c:forEach>
		                                  </c:when>
		                                  <c:otherwise>
		                                    <c:url value="/scenario" var="scenarioUrl">
									            <c:param name="serviceId" value="${mockservice.id}" />
									        </c:url>
		                                  	<li class="alert_message"><span>You need to <a href="<c:out value="${scenarioUrl}"/>" title="Create service scenario" border="0" />create</a>
		                                     a scenario before using "Static or Dynamic Scenario".</span></li>
		                                  </c:otherwise>
		                                </c:choose>
	                                </ul>
	                              </span>
                                 <p>
			                       <strong>Hang time (milliseconds):</strong> ${mockservice.hangTime} 
	                             </p>
	                             <p>
			                       <strong>Content type:</strong>   
	                    			<c:choose>
	                    			<c:when test="${!empty mockservice.httpContentType}">${mockservice.httpContentType}</c:when>
	                    			<c:otherwise><span style="color:red;">not set</span></c:otherwise>
	                    			</c:choose>
			                     </p>
                           
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
			  <p class="alert_message">There are no mock services defined. You can <a href="<c:url value="upload"/>">upload one</a>, <a href="<c:url value="setup"/>">create one manually</a> or start <a href="<c:url value="help#record"/>">recording</a>. </p>
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
      .show('fast');    
    return true; 
  
  })

});

</script>
</c:if>
<div style="display:block;text-align:right;">
<p>
mood image <a href="home?mood=silhouette.jpg" class="mood" >A</a>
<a href="home?mood=unicorn.jpg" class="mood" >B</a>
<a href="home?mood=geometry.jpg" class="mood" >C</a>

</p>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />