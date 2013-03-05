<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="pageTitle" value="Merge" scope="request" />
<c:set var="currentTab" value="merge" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />

<script>
$(document).ready( function() {

	    // Ensure all is clear.
	    $('input:checkbox[name=souceCheckGroup]:checked').attr("checked", ""); 
	    $('input:checkbox[name=destinationCheckGroup]:checked').attr("checked", ""); 

	    // Accordion initialize
		$("#accordion1").accordion({
			active: false,
			collapsible: true,
			autoHeight: false
		});
		$("#accordion2").accordion({
			active: false,
			collapsible: true,
			autoHeight: false
		});

		// Merge button initialize
		$('.merge-button')
			.button()
			.click(function() {
				var destinationServiceId = $("input:checkbox[name='destinationCheckGroup']:checked"); 
			    var sourceIdValues = new Array();
			    $.each($('input:checkbox[name=souceCheckGroup]:checked'), function() {
			    	sourceIdValues.push($(this).val());
			       
			    });	
			    if(sourceIdValues.length == 0) { 
			    	$.prompt('<div style=\"color:red;\">No source services selected.</div> ');
			    }else if(destinationServiceId.length == 0) { 
			    	$.prompt('<div style=\"color:red;\">No destination service selected.</div> ');
			    }else {
				    
				    //alert("Hey! " + sourceValues + " " + destinationServiceId.val());
	
				    $.post('<c:url value="/merge"/>', { 'serviceIdMergeSource[]': sourceIdValues, serviceIdMergeDestination: destinationServiceId.val() } ,function(data){
						if(data.result.conflicts) {
						   	$.prompt('<div>Service merge results:</div> <div style=\"color:red\">Conflicts</div>' + data.result.conflicts 
								   	+ "<div style=\"color:blue\">Additions</div>"+data.result.additions);
						     } else {
						    	 $.prompt('<div>Services merged and Source services have been deleted.</div>',
						    			 {
						                     callback: function (proceed) {
						                         document.location="<c:url value="/home" />?&serviceId="+ destinationServiceId.val();
						                     },
						                     buttons: {
						                         'Ok': true
						                     }
						                 }
								    	 );
						    	 
						     }
						   } , 'json');	
					    
			    }	
		});

		
		 $("input:checkbox[name='souceCheckGroup']").click ( function(){
			 var serviceId = this.id.split("_")[1];
			 if($('#' + this.id).is(":checked")){
				 $("#source-service_"+serviceId).addClass('selected');
				 $("#destination-service-h3_"+serviceId).hide();
				 $("#destination-service-body_"+serviceId).hide();
				 $('#accordion2').accordion('destroy').accordion({
						active: false,
						collapsible: true
					});
				 // Make sure the Destination side gets reset.
				 $("input:checkbox[name='destinationCheckGroup']").removeAttr('checked');
				 $(".destination-service-name-display").removeClass('destination_selected');
			 }else {
				 $("#source-service_"+serviceId).removeClass('selected');
				 $("#destination-service-h3_"+serviceId).show();
				 $("#destination-service-body_"+serviceId).show();
				 $('#accordion2').accordion('destroy').accordion({
						active: false,
						collapsible: true
					});
		     }
		    });

		 $("input:checkbox[name='destinationCheckGroup']").click ( function(){
			 var serviceId = this.id.split("_")[1];
			 if($('#' + this.id).is(":checked")){
				 $("input:checkbox[id='destinationCheckGroup']").not(this).removeAttr('checked');
				 $(".destination-service-name-display").removeClass('destination_selected');
				 $("#destination-service_"+serviceId).addClass('destination_selected');
			 }else {
				 $("#destination-service_"+serviceId).removeClass('destination_selected');
		     }
		    });
		    

	
 });
</script>
    <div id="main">
        <h1>Merge Services</h1>
        <p>Merging services means putting all scenarios and multiple <i>real urls</i> together, under one service definition. If you're not sure about this, 
you should <a href="">Export</a> your service definitions first. If things go bad, try 
<strong>Flush</strong> and re-import your last saved definitions file. <a href="<c:url value="/help#merge_services"/>">More help</a></p>
        <c:choose>
	        <c:when test="${!empty services}">
	        <div class="parentform">
	            <p align="right"><button id="merge" class="merge-button" name="merge">Merge services</button></p>
	            
		        <table width="100%" cellspacing="0">
		        <tbody>
		        <tr><td><h3>Source</h3><p>First, choose <strong>one or more</strong> services from this column:</p></td><td><h3>Destination</h3><p>Second, select <strong>only one</strong> service in this column (to merge into).</p></td></tr>
		        
		              <tr>                                                                                 
							<td valign="top" width="50%;">
							  <div class="scroll" id="accordion1">
	                            <c:forEach var="mockservice" items="${services}"  varStatus="status">	  
		                            <h3><a href="#" id="source-service_${mockservice.id}" title="${mockservice.serviceName}"><mockey:slug text="${mockservice.serviceName}" maxLength="40"/>
									<c:if test="${!empty mockservice.tagList}">
									<span class="tiny" style="color:black;"><br />tags: <span style="font-weight:100;"><c:forEach var="tag" items="${mockservice.tagList}">${tag} </c:forEach></span></span>
									</c:if>
									</a></h3> 
		                            
		                            
									<div>
									     <div class="info_message"><label for="source-serviceid_${mockservice.id}"><input type="checkbox" name="souceCheckGroup" id="source-serviceid_${mockservice.id}" value="${mockservice.id}" class="source-checkbox" /> Check this box if you want to merge this service into another.</label></div>
										 <div><h4>Mock URL(s)</h4></div>
									     <c:forEach var="alternativeUrl" items="${mockservice.realServiceUrls}">
									     	<div class="tiny"><a href="<mockey:url value="${alternativeUrl}"/>"><mockey:url value="${alternativeUrl}" breakpoint="5"/></a></div>
									     </c:forEach>
									     <c:if test="${empty mockservice.realServiceUrls}"><div class="alert_message">No real URL defined for this service.</div></c:if>
									     <div><h4>Scenarios</h4></div>
		                                 <c:forEach var="scenario" begin="0" items="${mockservice.scenarios}">
		                                   	<div class="tiny"><mockey:slug text="${scenario.scenarioName}" maxLength="40"/></div>
		                                 </c:forEach>
		                                 <c:if test="${not empty mockservice.scenarios}"><div class="alert_message">No scenarios defined for this service.</div></c:if>
		                           
									</div>
							    </c:forEach>
							    </div>
							</td>
							<td valign="top">
							  <div class="scroll" id="accordion2">
	                            <c:forEach var="mockservice" items="${services}"  varStatus="status">	  
		                            <h3 id="destination-service-h3_${mockservice.id}" ><a href="#" id="destination-service_${mockservice.id}" class="destination-service-name-display" title="${mockservice.serviceName}"><mockey:slug text="${mockservice.serviceName}" maxLength="40"/>
		                            <c:if test="${!empty mockservice.tagList}">
									<span class="tiny" style="color:black;"><br />tags: <span style="font-weight:100;"><c:forEach var="tag" items="${mockservice.tagList}">${tag} </c:forEach></span></span>
									</c:if>
									</a></h3> 
									<div id="destination-service-body_${mockservice.id}">
									     <div class="info_message"><label for="destination-serviceid_${mockservice.id}"><input type="checkbox" name="destinationCheckGroup" id="destination-serviceid_${mockservice.id}" value="${mockservice.id}" class="source-checkbox" /> Check this box if you want to merge into this service.</label></div>
										 <div><h4>Mock URL(s)</h4></div>
									     <c:forEach var="alternativeUrl" items="${mockservice.realServiceUrls}">
									     <div class="tiny"><a href="<mockey:url value="${alternativeUrl}"/>"><mockey:url value="${alternativeUrl}" breakpoint="5"/></a></div>
									     </c:forEach>
									     <div><h4>Scenarios</h4></div>
									     <c:choose>
		                                  <c:when test="${not empty mockservice.scenarios}">
		                                  <c:forEach var="scenario" begin="0" items="${mockservice.scenarios}">
		                                    <div class="tiny"><mockey:slug text="${scenario.scenarioName}" maxLength="40"/></div>
		                                  </c:forEach>
		                                  </c:when>
		                                  <c:otherwise>
		                                  	<div class="alert_message">No scenarios defined for this service.</div>
		                                  </c:otherwise>
		                                </c:choose>
									</div>
							    </c:forEach>
							    </div>
							</td>
						</tr>
		            </tbody>
		        </table>
		        <p align="right"><button id="merge" class="merge-button" name="merge">Merge services</button></p>
	        </div>
	        </c:when>
	        <c:otherwise>
			  <p class="alert_message">There are no mock services defined. You can <a href="<c:url value="upload"/>">upload one</a>, <a href="<c:url value="setup"/>">create one manually</a> or start <a href="<c:url value="help#record"/>">recording</a>. </p>
			</c:otherwise>
        </c:choose>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />