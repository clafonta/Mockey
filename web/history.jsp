<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="pageTitle" value="History" scope="request" />
<c:set var="currentTab" value="history" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>
<script>
$(document).ready(function() {


		var makeExactlyAsTallAsItNeedsToBe = function(textArea) {
			var content = $(textArea).val() == undefined ? "" : $(textArea).val();
			var numOfRowsOfContent = 1;
			try { numOfRowsOfContent = content.match(/[^\n]*\n[^\n]*/gi).length; } catch(e) {}
			var maxSize = 40;
			textArea.rows = numOfRowsOfContent<maxSize?numOfRowsOfContent+1:maxSize;
		}
		$('textarea').each( function() {
			makeExactlyAsTallAsItNeedsToBe(this);
			$(this).keyup( function(e) {
				makeExactlyAsTallAsItNeedsToBe(this);
			});
			$(this).change( function(e) {
				makeExactlyAsTallAsItNeedsToBe(this);
			});
			$(this).bind( "reformatted", function(e) {
				makeExactlyAsTallAsItNeedsToBe(this);
			});
		});

    $(".tabs").tabs();
			
	$('#filter-button').button();
	
	$('.mockeyResponse').each( function() {
		var formatButton = $(this).find(".formatButton")[0];
		$(formatButton).click( function() {
			var contentTextArea = $(this).parent().parent().find(".responseContent")[0];
			var theId = this.id;
			formatXmlInTextArea(contentTextArea);
			return false;
		});
		var formatXmlInTextArea = function(textArea) {
			var unFormatted = $(textArea).val();
			var formatted = format_xml(unFormatted);
			$(textArea).val(formatted);
			$(textArea).trigger("reformatted");
		}
	});

	$('.deleteFulfilledRequestLink').each( function() {
		$(this).click( function() {
			var requestId = this.id.split("_")[1];
			var unusedServiceId = -1;
			$.ajax({
				type: "GET",
				url: "<c:url value="history"/>?action=delete&serviceId="+unusedServiceId+"&fulfilledRequestId="+requestId
			});
			$('#fulfilledRequest_'+requestId).fadeOut(500, function() {
				$('#fulfilledRequest_'+requestId).remove();
			});
		});
	});
	
	$('.tagFulfilledRequestLink').each( function() {
		$(this).click( function() {
			var requestId = this.id.split("_")[1];
			var unusedServiceId = -1;
			$.ajax({
				type: "POST",
				url: "<c:url value="history"/>?action=tag&fulfilledRequestId="+requestId
			});
		});
	});
	
    $('a.tagFulfilledRequestLink').click(function () {
      $(this).parent().toggleClass("selected");
      var tagspan= $(this).find(".tag")[0];
      $(tagspan).toggle();
      var untagspan= $(this).find(".untag")[0];
      $(untagspan).toggle();
    });

	$('.viewFulfilledRequestLink_orig').each( function() {
		$(this).click( function() {
			var requestId = this.id.split("_")[1];	
		    $(this).toggle();		
		    $('#hideFulfilledRequest_'+requestId).toggle();
			$.ajax({
				type: "GET",
				url: "<c:url value="fulfilledrequest"/>?&fulfilledRequestId="+requestId,
				success: function(html) {
                  //i want to fade result into these 2 divs...
                  $('#letmesee_'+requestId).hide().html(html).fadeIn();
                }
			});
		});
	});

    $('.viewRequestInspectionLink').each( function() {
        $(this).click( function() {
            var requestId = this.id.split("_")[1];  
            $('#viewRequestInspectionBlock_'+requestId).toggle();
            
        });
    });
    
	$('.viewFulfilledRequestLink').each( function() {
        $(this).click( function() {
            var requestId = this.id.split("_")[1];  
            var element = this;
            var timeoutValue = 50000;
            $('#spinner_'+requestId).toggle();
            $(element).toggle(); 
            $.ajax({
                type: 'GET',
                dataType: 'json',
                timeout: timeoutValue, 
                url: '<c:url value="/conversation/record"/>?&conversationRecordId='+requestId,
                success: function(data) {
                	
                        
                  $('#hideFulfilledRequest_'+requestId).toggle();
                  $('#spinner_'+requestId).hide();
                  $('#requestUrl_'+requestId).val(data.requestUrl);
                  $('#requestParameters_'+requestId).val(data.requestParameters);
                  $('#requestHeaders_'+requestId).val(data.requestHeaders);
                  $('#requestCookies_'+requestId).val(data.requestCookies);
                  $('#requestBody_'+requestId).val(data.requestBody);
                  $('#responseScenarioName_'+requestId).append(data.responseScenarioName);
                  $('#responseScenarioTags_'+requestId).append(data.responseScenarioTags);
                  $('#responseStatus_'+requestId).val(data.responseStatus);
                  $('#responseHeader_'+requestId).val(data.responseHeader);
                  $('#responseBody_'+requestId).val(data.responseBody);
                  $('#responseCookies_'+requestId).val(data.responseCookies);
                  $('#letmesee_'+requestId).show(); 
                  
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) { 
                	$('#spinner_'+requestId).hide();
                	$(element).show(); 
                    alert("Status: " + textStatus + ". System error or possibly taking longer than " +timeoutValue/1000 + " seconds. Check logs for insight." ); 
                } 
            });
        });
    });



	$('.hideFulfilledRequestLink').each( function() {
		$(this).click( function() {
			var requestId = this.id.split("_")[1];			
			$('#letmesee_'+requestId).fadeOut();
			$('#hideFulfilledRequest_'+requestId).toggle();
			$('#viewFulfilledRequest_'+requestId).toggle();
		});
	});

   
    $("#dialog-clear-history-confirm").dialog({
        resizable: false,
        height:120,
        modal: true,
        autoOpen: false
    });
        
    $('.clear_history').each( function() {
        $(this).click( function() {
            
            $('#dialog-clear-history-confirm').dialog('open');
                $('#dialog-clear-history-confirm').dialog({
                    buttons: {
                      "Delete history": function() {
                         document.location="<c:url value="/history?action=delete_all" />";                              
                      }, 
                      Cancel: function(){
                          $(this).dialog('close');
                      }
                    }
              }); 
              return false;
            });
        });
        
});

</script>
<div id="main">
    <jsp:include page="/WEB-INF/common/inc_scenario_create_dialog.jsp" />

    <h1>History</h1>
    <p>
    This is the list of service calls made to Mockey.
    </p>
    <div id="dialog-clear-history-confirm" title="Delete history">Are you sure? This will delete all fulfilled requests for all requesting IPs.</div>
    <form action="<c:url value="/history"/>" method="get">
    <p>
    
    <input type="text" name="token" size="80" placeholder="Enter 1 or more space seperated tags to filter the list to only the things with a matching tag." class="text ui-corner-all ui-widget-content"/>
    <button id="filter-button" >Add Filter</button>
    
    <c:if test="${!empty requests}">
      <c:url value="/history" var="deleteAllScenarioUrl">
         <c:param name="action" value="delete_all" />
      </c:url>
      <a  class="spread clear_history" href="#">Clear History</a>  
    </c:if>
    <c:if test="${!empty historyFilter.tokens}">  
       <a class="spread" href="<c:url value="/history?action=remove_all_tokens"/>">Clear Filters</a>
    </c:if>
    </p>
    </form>
    <c:if test="${!empty historyFilter.tokens}">    
    <p class="tiny">You are filtering your history on:<span style="float:right;"><strong>Hint:</strong> Try filtering with <i>bang + term</i>, example: <span class="code_text"><b>!term</b></span></span></p>
    <div class="hint_message">
   
    <p>
    <c:forEach var="token" items="${historyFilter.tokens}">
        ${token}<a id="token" class="remove_grey" title="Remove filter token" href="<mockey:history value="${token}"/>"><i aria-hidden="true" class="icon-cancel"></i></a> 
    </c:forEach>
    </p>
    
    </div>
    </c:if>
    <c:choose>
        <c:when test="${!empty requests}">
        
        
        	<c:if test="${fn:length(requests) > 100}">
                <p style="font-size:1.2em; color: red;"><span>Whoa!</span> There are too many records to display them all (<strong>${fn:length(requests)} records</strong>). Here are a few... </p>
            </c:if>
            <c:forEach var="request" items="${requests}" varStatus="status"> 
            	
                <c:if test="${status.count < 101}">
                <div id="fulfilledRequest_${request.id}" class="parentform" style="padding: 0.2em 0.5em; 0.2em 0.5em;">
                   <c:url value="/home" var="serviceUrl">
                          <c:param name="serviceId" value="${request.serviceId}" />                                                                               
                   </c:url>  
                   <div style="text-align:right;  position: relative;font-size:80%;" class="<c:if test="${request.comment ne null}">selected</c:if>">
                     
                     <c:if test="${!empty request.requestInspectionResult.resultMessageList}">
                     <div id="viewRequestInspectionBlock_${request.id}" class="alert_message" style="display:none;">
                     <ul>
                      <c:forEach var="reqResultMsg" items="${request.requestInspectionResult.resultMessageList}"> 
                        <li>${reqResultMsg}</li>
                      </c:forEach>
                      </ul>
                     </div>
                     <a href="#" id="viewRequestInspection_${request.id}" class="viewRequestInspectionLink hhButtonRed" onclick="return false;">info</a>
                     </c:if>
                     <span id="spinner_${request.id}" style="margin-right:5px; display:none;"><img src="<c:url value="/images/ajax-loader.gif" />"/> </span>
                     <a href="#" id="viewFulfilledRequest_${request.id}" class="viewFulfilledRequestLink hhButton" onclick="return false;">view</a>
                     
                     <a href="#" id="hideFulfilledRequest_${request.id}" class="hideFulfilledRequestLink hhButton" onclick="return false;" style="display:none;">hide</a>   
                     <a href="#" id="tagFulfilledRequestLink_${request.id}" class="tagFulfilledRequestLink hhButton" onclick="return false;"><span class="tag" style="<c:if test="${request.comment ne null}">display:none;</c:if>">flag</span><span class="untag" style="<c:if test="${request.comment eq null}">display:none;</c:if>">unflag</span></a>
                     <a href="#" id="deleteFulfilledRequest_${request.id}" class="deleteFulfilledRequestLink remove_grey" style="margin-left:2em;"><i aria-hidden="true" class="icon-cancel"></i></a>	              
	               </div>
	               <div style="width:95%; position:relative; margin-top:-1em;">
	                 
	                 <b>When:</b> <mockey:fdate date="${request.time}"/> <b>From:</b> <a id="finfo" title="<c:out value="${request.requestorIP}"/>"><mockey:slug text="${request.requestorIP}" maxLength="25"/></a>
						 (<mockey:service style="1" type="${request.serviceResponseType}"/>)
	                 <b><a href="<c:out value="${serviceUrl}"/>" title="<c:out value="${request.serviceName}"/>"><mockey:slug text="${request.serviceName}" maxLength="20"/></a></b>
	                 <div style="padding-top:0.2em;">
	                 <c:if test="${!empty request.originalUrlBeforeTwisting }">
                     <a href="<c:url value="/twisting/setup"/>"><b style="color:red;">TWISTED!</b></a> <b>Original request:</b> <i>${request.originalUrlBeforeTwisting} </i> 
                     </c:if>
	                 <b>Raw Request:</b> <mockey:slug text="${request.rawRequest}" maxLength="180"/> </div>                    
	                 <div id="letmesee_orig${request.id}">
	                 
                     </div>
                     <div id="letmesee_${request.id}" style="display:none;">
	                    
                        <div style="padding: 0.2em 0.4em; margin: 0.2em 0.0em;">
	                        <h2>Request</h2>
	                        <div class="tabs">
	                                    <ul>
	                                        <li><a href="#tabs-2_${request.id}">Parameters</a></li>
	                                        <li><a href="#tabs-3_${request.id}">Headers</a></li>
	                                        <li><a href="#tabs-5_${request.id}">Cookies</a></li>
	                                        <li><a href="#tabs-4_${request.id}">Body</a></li>
	                                    </ul>
	                                    <div id="tabs-2_${request.id}" class="historyTextArea">
	                                    <textarea class="noborder_textarea resizable" id="requestParameters_${request.id}" name="requestParameters" rows="25" cols="50"></textarea>
	                                    </div>
	                                    <div id="tabs-3_${request.id}" class="historyTextArea">
	                                    <textarea class="noborder_textarea resizable" id="requestHeaders_${request.id}"  name="requestHeaders" rows="25" cols="50"></textarea>
	                                    </div>
	                                    <div id="tabs-5_${request.id}" class="historyTextArea">
                                        <textarea class="noborder_textarea resizable" id="requestCookies_${request.id}"  name="requestCookies" rows="25" cols="50"></textarea>
                                        </div>
	                                    <div id="tabs-4_${request.id}" class="historyTextArea">
	                                    <textarea class="noborder_textarea resizable" id="requestBody_${request.id}" name="requestBody" rows="25" cols="50"></textarea>
	                                    </div>
	                        </div>
                        </div>
                        <div style="padding: 0.2em 0.4em; margin: 0.2em 0.0em;">
                        <h2>Response:</h2> 
                        <p>Response scenario name: <span id="responseScenarioName_${request.id}"> </span> </p>
                        <p>Response scenario tags: <span id="responseScenarioTags_${request.id}"> </span> </p>
                            <div class="tabs">
                                        <ul>
                                            <li><a href="#resp-tabs-2_${request.id}">Headers</a></li>
                                            <li><a href="#resp-tabs-5_${request.id}">Cookies</a></li>
                                            <li><a href="#resp-tabs-3_${request.id}">Body</a></li>
                                            <li><a href="#resp-tabs-1_${request.id}">Status</a></li>
                                        </ul>
                                        <div id="resp-tabs-2_${request.id}" class="historyTextArea">
                                           <p style="color:red;">Note: header key values are pipe ("|") delimited by Mockey for readability. </p>
                                           <textarea class="noborder_textarea resizable" id="responseHeader_${request.id}"  name="requestHeader" rows="5" cols="50"></textarea>
                                        </div>
                                        <div id="resp-tabs-5_${request.id}" class="historyTextArea">
                                           <textarea class="noborder_textarea resizable" id="responseCookies_${request.id}" name="responseCookies" rows="5" cols="50"></textarea>
                                        </div>
                                        <div id="resp-tabs-3_${request.id}" class="historyTextArea">
                                           <textarea class="noborder_textarea resizable" id="responseBody_${request.id}" name="responseBody" rows="5" cols="50"></textarea>
                                        </div>
                                        <div id="resp-tabs-1_${request.id}" class="historyTextArea">
                                           <textarea class="noborder_textarea resizable" id="responseStatus_${request.id}" name="responseStatus" rows="5" cols="50"></textarea>
                                        </div>
                                        
                                        
                            </div>
                            <div>
                            <button id="save-as-a-service-scenario_${request.id}" class="save-as-a-service-scenario">Save me as a scenario</button>
                            </div>
                            
                        </div>
                     </div>
                   </div>                   
                </div>
                </c:if>
               
            </c:forEach>
        </c:when>
        <c:otherwise>
            <p class="info_message">No history here. It's because no one talks to Mockey or someone just cleared the history. Mockey is feeling unwanted.</p>
        </c:otherwise>
    </c:choose>
</div>



<jsp:include page="/WEB-INF/common/footer.jsp" />