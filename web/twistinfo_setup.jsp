<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="actionKey" value="conf_service" scope="request" />
<c:set var="pageTitle" value="Twisting" scope="request" />
<c:set var="currentTab" value="twisting" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>
<script type="text/javascript">
$(document).ready( function() {


   $("#dialog-create-twist").dialog({minHeight: 450, height:250, width: 500,  modal: true, autoOpen: false, resizable: true });
   $('.createTwistLink').each( function() {
       $(this).click( function() {             
           $('#dialog-create-twist').dialog('open');
               $('#dialog-create-twist').dialog({
                   buttons: {
                     "Create Twist": function() {
		            	  var originationValues = new Array();
		                  $.each($('input:text[name=origination]'), function() {
		                	  originationValues.push($(this).val());
		                      });
		                  var destinationValues = new Array();
                          $.each($('input:text[name=destination]'), function() {
                        	  destinationValues.push($(this).val());
                              }); 
                          var bValid = true;  
                          // 'valid' check here for those who want input validation.
                          if (bValid) {
                              var twistConfigName = $('input[id=twist_config_name_new]').val();  
                              $.post('<c:url value="/twisting/setup"/>', { 'response-type': 'json', 'twist-name': twistConfigName, 'twist-origination-values[]':  originationValues,'twist-destination-values[]':  destinationValues } ,function(data){
                                  if(data.result.success && data.result.id){
                                      $(this).dialog('close');              
                                      document.location="<c:url value='/twisting/setup' />"; 
                                      
                                   }
                           }, 'json' );
                              
                          }
                     }, 
                     Cancel: function(){
                         $(this).dialog('destroy');
                     }
                   }
             }); 
             // Reset the size.
             $('#dialog-create-twist').dialog({height: 450 });
               
             return false;
           });
       });

   $('.editTwistLink').each( function() {
       $(this).click( function() {  
    	   var twistId = this.id.split("_")[1];  
    	            
           $('#dialog-edit-twist_' + twistId).dialog('open');
               $('#dialog-edit-twist_' + twistId).dialog({
                   buttons: {
                     "Update Twist": function() {
                          var originationValues = new Array();
                          $.each($('input:text[name=origination_'+twistId+']'), function() {
                              originationValues.push($(this).val());
                              });
                          var destinationValues = new Array();
                          $.each($('input:text[name=destination_'+twistId+']'), function() {
                              destinationValues.push($(this).val());
                              }); 
                          var bValid = true;  
                          // 'valid' check here for those who want input validation.
                          if (bValid) {
                              var twistConfigName = $('input[id=twist_config_name_'+twistId+']').val();  
                              $.post('<c:url value="/twisting/setup"/>', { 'twist-id': twistId, 'response-type': 'json', 'twist-name': twistConfigName, 'twist-origination-values[]':  originationValues,'twist-destination-values[]':  destinationValues } ,function(data){
                                  if(data.result.success && data.result.id){
                                      $(this).dialog('close');              
                                      document.location="<c:url value='/twisting/setup' />"; 
                                      
                                   }
                           }, 'json' );
                              
                          }
                     }, 
                     Cancel: function(){
                         $(this).dialog('destroy');
                     }
                   }
             }); 
             // Reset the size.
             $('#dialog-edit-twist_' + twistId).dialog({height: 450, width:400 });
               
             return false;
           });
       });
   
   $('#add-row').click(function() {
       $('#create-twist-pattern-list').append(
    		   '<div class=\"parentform\" >' +
    		   '<span style=\"float:right;\"><a class=\"remove-row remove_grey\" onclick=\"$(this).parent().parent().remove()\" title=\"Delete this service\" href=\"#\">x</a></span>' +
    		   '<label for=\"twist-name\">Find this pattern...</label>' +
    	       '<input type=\"text\" id=\"service_real_url\" class=\"text ui-corner-all ui-widget-content\" name=\"origination\" maxlength=\"100\" size=\"50%\" value=\"\" />' +
    	       '<label for=\"twist-name\">Replace it with this one...</label>' +
    	       '<input type=\"text\" id=\"service_real_url\" class=\"text ui-corner-all ui-widget-content\" name=\"destination\" maxlength=\"100\" size=\"50%\" value=\"\" />' +
    	       '</div>' );
   });
   $('.add-edit-row').each( function() {
       $(this).click( function() {  
    	   var twistId = this.id.split("_")[1];
    	   $('#twist-edit-pattern-list_' + twistId).append(
                   '<div class=\"parentform\" >' +
                   '<span style=\"float:right;\"><a class=\"remove-row remove_grey\" onclick=\"$(this).parent().parent().remove()\" title=\"Delete this service\" href=\"#\">x</a></span>' +
                   '<label for=\"twist-name\">Find this pattern...</label>' +
                   '<input type=\"text\" id=\"service_real_url\" class=\"text ui-corner-all ui-widget-content\" name=\"origination_'+twistId+'\" maxlength=\"100\" size=\"50%\" value=\"\" />' +
                   '<label for=\"twist-name\">Replace it with this one...</label>' +
                   '<input type=\"text\" id=\"service_real_url\" class=\"text ui-corner-all ui-widget-content\" name=\"destination_'+twistId+'\" maxlength=\"100\" size=\"50%\" value=\"\" />' +
                   '</div>' );
            
       })
   });

   $('.delete-twist-link').each( function() {
       $(this).click( function() {
           var twistId = this.id.split("_")[1];
           $('#dialog-delete-twist-config-confirm').dialog('open');
           $('#dialog-delete-twist-config-confirm').dialog({
               buttons: {
                 "Delete Twist Config": function() {
                     // Post the DELETE call.  
                     $.post('<c:url value="/twisting/delete"/>', { 'response-type': 'json', 'twist-id': twistId } ,function(data){
                              if(data.result.success){
                            	  $(this).dialog('close');   
                                  $('#deleted').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast', 
                                          function() {document.location="<c:url value="/twisting/setup" />"; }); 
                                  
                                        
                                  
                               }
                       }, 'json' );
                     $(this).dialog('close');                      
                     
                 }, 
                 Cancel: function(){
                     $(this).dialog('close');
                 }
               }
         }); 
         $('#dialog-delete-twist-config-confirm').dialog({height: 300 });
           
         return false;
       });
   });
   $('#dialog-delete-twist-config-confirm').dialog({autoOpen: false, minHeight: 300, width: 300, height: 300, modal: false, resizable: false });

   <c:forEach var="twistInfo" items="${twistInfoList}"  varStatus="status">
     $('#dialog-edit-twist_${twistInfo.id}').dialog({autoOpen: false, minHeight: 300, width: 450, height: 400, modal: false, resizable: true });
   </c:forEach>
   $('.toggletwist').each( function() {
       $(this).click(function(){   
         var twistId = this.id.split("_")[1];    
         var enable = this.id.split("_")[2];    
         
         //console.log("response type: "+response_type); response_set
         $.post('<c:url value="/twisting/toggle"/>', { 'response-type': 'json', 'twist-id': twistId, 'twist-enable': enable } ,function(data){
                      //console.log(data);
                      if(data.result.success){
                          // R
                          $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast', 
                                  function() {document.location="<c:url value="/twisting/setup" />"; });
                          return false;
                          
                       }else {
                    	   $("#tmp-error").remove();
                    	   $("#foo").append("<span id=\"tmp-error\">Unable to toggle</span>").fadeIn(1000).fadeTo(3000, 1).fadeOut(1000);
                       }
               }, 'json' );
       });
    });

});
</script>
<div id="main2">     
    <h1>Twisting</h1> 
    <div>
		<p><strong>Twisting</strong> refers to re-mapping incoming URL requests to other URLs. This is useful if 
		your code is pointing to DOMAIN-X and/or URL-PATH-X, and you need to point to DOMAIN-Y and/or URL-PATH-Y. <a id="more-help" class="more-help" href="<c:url value="/help#twisting" />">More help.</a>
		</p>
		
	    <div style="text-align:right;"><span class="power-link tiny"><a href="#" class="createTwistLink" id="createTwistLink">Create Twist Configuration</a></span></div>
	    <c:choose>
           <c:when test="${empty twistInfoList}">
              <p class="info_message"><strong>No twisting here.</strong></p>
           </c:when>
           <c:otherwise>
             <h2>Twisting Settings</h2>
             <c:forEach var="twistInfo" items="${twistInfoList}"  varStatus="status">
             <div class="parentform" id="twist-config_${twistInfo.id}">    
               
               <span style="float:right;"> <a href="#" id="delete-twist-link_${twistInfo.id}" class="delete-twist-link remove_grey">x</a> </span>
               <h3>${twistInfo.name}</h3>
                 <p> 
                     <a id="toggle-twist-on_${twistInfo.id}_true" class="toggletwist toggle-twist-on <c:if test="${twistInfoIdEnabled eq twistInfo.id}">response_set</c:if> <c:if test="${twistInfoIdEnabled ne twistInfo.id}">response_not</c:if>" style="text-decoration:none;" href="#"> On </a>
                     <a id="toggle-twist-off_${twistInfo.id}_false" class="toggletwist toggle-twist-off <c:if test="${twistInfoIdEnabled eq twistInfo.id}">response_not</c:if> <c:if test="${twistInfoIdEnabled ne twistInfo.id}">response_set</c:if>" style="text-decoration:none; margin-left:2px;margin-right:2px;" href="#"> Off </a>
                 </p>
                 <table class="api">
                   <tr><th>Find this pattern...</th><th>Replace with this...</th></tr>
	                 <c:forEach var="patternPair" items="${twistInfo.patternPairList}" >  
	                   <tr>
	                     <td>${patternPair.origination } </td><td> ${patternPair.destination } </td>
	                   </tr> 
	                 </c:forEach>
                  </table>
                  <p class="power-link tiny" style="text-align:right;"> <a href="#" id="editTwistLink_${twistInfo.id}" class="editTwistLink">Edit</a> </p>
             </div>      
             <div id="dialog-edit-twist_${twistInfo.id}" title="Edit Twist Configuration">
	            <p>
	                <label for="twist-name">Twist Configuration name</label>
	                <input type="text" name="twist_config_name" id="twist_config_name_${twistInfo.id}" value="${twistInfo.name}" class="text ui-widget-content ui-corner-all" />
	                <div name="" style="text-align:right;"><a title="Add row" id="add-edit-row_${twistInfo.id}" class="add-edit-row" href="#" style="color:red;text-decoration:none;font-size:1em;">Add pattern twist</a></div>
	                <c:forEach var="patternPair" items="${twistInfo.patternPairList}" >  
                     <div class="parentform">
                     <span style="float:right;"><a class="remove-row remove_grey" onclick="$(this).parent().parent().remove()" title="Delete this service" href="#">x</a></span>
                     
                     <label for="twist-name">Find this pattern...</label>
                     <input type="text" class="text ui-corner-all ui-widget-content" name="origination_${twistInfo.id}" maxlength="100" value="${patternPair.origination }" />
                     <label for="twist-name">Replace it with this one...</label>
                     <input type="text" class="text ui-corner-all ui-widget-content" name="destination_${twistInfo.id}" maxlength="100" value="${patternPair.destination }" />
                     </div>
                     </c:forEach>
	                <div id="twist-edit-pattern-list_${twistInfo.id}">
	                    <!-- JQUERY Append content here -->
	                </div>
	            </p>
	        </div>         
             </c:forEach>
           </c:otherwise>
        </c:choose>
        <!-- TWISTING DIALOG -->
        <div id="dialog-create-twist" title="Create Twist Configuration">
            <p>
                <label for="twist-name">Twist Configuration name</label>
                <input type="text" name="twist_config_name" id="twist_config_name_new" class="text ui-widget-content ui-corner-all" />
                <div style="text-align:right;"><a title="Add row" id="add-row" href="#" style="color:red;text-decoration:none;font-size:1em;">Add pattern twist</a></div>
                <div id="create-twist-pattern-list">
                    <!-- JQUERY Append content here -->
                </div>
            </p>
        </div>
         
    </div>
    <div id="dialog-delete-twist-config-confirm" title="Delete Twisting Configuration">
      <p>Are you sure you want to delete this Twisting configuration?</p>
    </div>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
