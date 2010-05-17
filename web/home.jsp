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
			$(".scenariosByServiceId_"+serviceId).removeClass("response_static_big").addClass("response_not_big");
			$("#serviceScenario_"+scenarioId+"_"+serviceId).removeClass("response_not_big").addClass("response_static_big");
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
				$('#serviceResponseType_0_'+serviceId).removeClass("response_not").addClass("response_proxy");
				$('#proxyScenario_'+serviceId).addClass("show");
			}else if(responseType == 1){
				$('#serviceResponseType_1_'+serviceId).removeClass("response_not").addClass("response_static");
				$('#staticScenario_'+serviceId).addClass("show");
			}else if(responseType == 2){
				$('#serviceResponseType_2_'+serviceId).removeClass("response_not").addClass("response_dynamic");
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
									      <a class="gt" onclick="return true;" href="#" id="togglevalue_<c:out value="${mockservice.id}"/>"><mockey:slug text="${mockservice.serviceName}" maxLength="40"/></a>
									      
									</div>
	                                <mockey:service type="${mockservice.serviceResponseType}" serviceId="${mockservice.id}"/>
	                                <c:if test="${empty mockservice.scenarios}">
	                                  <span style="float:right;font-size:80%;color:yellow;background-color:red;padding:0 0.2em 0 0.2em;">(no scenarios)</span>
	                                </c:if>                 
	                                
									</div>
							    </c:forEach>
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
								 <table>
								 <tbody>
								 <tr><th width="50px">Service name:</th><td><span class="h1">${mockservice.serviceName}</span></td></tr>
								 <tr><th>Mock URL(s):</th>
								     <td>
								     <c:forEach var="realUrl" items="${mockservice.realServiceUrls}">
								     <p><a href="<mockey:url value="${realUrl}"/>"><mockey:url value="${realUrl}" breakpoint="5"/></a></p>
								     </c:forEach>
							     </td></tr>
							     <tr><th>This service is set to:</th>
							         <td>
 										<span class="h1 hide<c:if test="${mockservice.serviceResponseType eq 2}"> show</c:if>" id="dynamicScenario_${mockservice.id}">Dynamic</span>
							       		<span class="h1 hide<c:if test="${mockservice.serviceResponseType eq 0}"> show</c:if>" id="proxyScenario_${mockservice.id}">Proxy</span>
			                       		<span class="h1 hide<c:if test="${mockservice.serviceResponseType eq 1}"> show</c:if>" id="staticScenario_${mockservice.id}">Static</span>
							       
							         </td></tr>
							         <tr><th>Select a static scenario:</th>
							         <td>
										<p>
		                                   <c:if test="${empty mockservice.scenarios and mockservice.serviceResponseType ne 0}">
		                                     
		                                      <div>
		                                        <p class="alert_message">You need to <a href="<c:out value="${setupUrl}"/>" title="Edit service definition">create</a> a scenario before using "Scenario".</p>
		                                        <input type="hidden" name="serviceResponseType_<c:out value="${mockservice.id}"/>" value="false" />
		                                      </div>
		                                   </c:if>
		                                </p>
		                                <ul id="simple" class="group">
	                                    
		                                <c:choose>
		                                  <c:when test="${not empty mockservice.scenarios}">
		                                  <c:forEach var="scenario" begin="0" items="${mockservice.scenarios}">
		                                    <li style="padding-top: 0.5em;">
		                                      <c:url value="/scenario" var="scenarioEditUrl">
		                                        <c:param name="serviceId" value="${mockservice.id}" />
		                                        <c:param name="scenarioId" value="${scenario.id}" />
		                                      </c:url>
		                                      <a id="serviceScenario_${scenario.id}_${mockservice.id}" class="serviceScenarioResponseTypeLink scenariosByServiceId_${mockservice.id} <c:choose><c:when test='${mockservice.defaultScenarioId eq scenario.id}'>response_static_big</c:when><c:otherwise>response_not_big</c:otherwise></c:choose>" href="#" title="Edit - ${scenario.scenarioName}"  onclick="return false;"><mockey:slug text="${scenario.scenarioName}" maxLength="40"/></a>
		                                    </li>
		                                  </c:forEach>
		                                  </c:when>
		                                  <c:otherwise>
		                                    <c:url value="/scenario" var="scenarioUrl">
									            <c:param name="serviceId" value="${mockservice.id}" />
									        </c:url>
		                                  	<li class="alert_message"><span>You need to <a href="<c:out value="${setupUrl}"/>" title="Create service scenario" border="0" />create</a>
		                                     a scenario before using "Static or Dynamic Scenario".</span></li>
		                                  </c:otherwise>
		                                </c:choose>
	                                </ul>
							         
							         </td></tr>
								 </tbody>
								 </table>
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