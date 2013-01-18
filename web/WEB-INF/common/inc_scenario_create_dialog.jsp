<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%@ taglib prefix="mockey-tag" tagdir="/WEB-INF/tags" %>
<script>
$(document).ready( function() {
	// SCENARIO CREATION JAVASCRIPT
    $("#dialog-create-scenario").dialog({
        resizable: true,
        height:700,
        width:700,
        modal: true,
        autoOpen: false
    });
    var name = $("#scenario_name"),
        match = $("#scenario_match"),
        match_regex_flag = $('#scenario_match_regex_flag'),
        responsemsg = $("#scenario_response"),
        universal_error_scenario = $('#universal_error_scenario'),
        scenario_response_header = $('#scenario_response_header'),
        tag = $('#tag'),
        http_response_status_code = $('#http_response_status_code');
        error_scenario = $('#error_scenario'),
        allFields = $([]).add(name).add(match).add(match_regex_flag).add(universal_error_scenario).add(error_scenario).add(responsemsg).add(tag).add(http_response_status_code).add(scenario_response_header),
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
    $('#toggleMoreOptionsDisplay').click ( function() {
    	$('#moreOptionsDisplay').toggle();
    	$('#showOptionsText').toggle();
        $('#hideOptionsText').toggle();    	
        return false;
    });
    
    $('#prettyPrintMyJson').click ( function() {
        var txt = $('#scenario_response').val();
    	var pTxt = js_beautify(txt);
    	$('#scenario_response').val(pTxt);
        return false;
    });
    
    $('#prettyPrintMyHtml').click ( function() {
        var txt = $('#scenario_response').val();
    	var pTxt = style_html(txt);
    	$('#scenario_response').val(pTxt);
        return false;
    });
    
    $('.createScenarioLink').each( function() {
        $(this).click( function() {
            var serviceId = this.id.split("_")[1];
            // Clear input
            var serviceName = $('#serviceName_'+serviceId).val();
            $('#service-name-for-scenario').text(serviceName);
            $('#scenario_name').val('');
            $('#scenario_match').val('');
            $('#scenario_match_regex_flag').attr('checked', false),
            $('#scenario_response').val(''); 
            $('#scenario_response_header').val('');
            $('#tag').val(''); 
            $('#http_response_status_code').val('200');
            $('#universal_error_scenario').attr('checked', false);
            $('#error_scenario').attr('checked', false);
            $('#dialog-create-scenario').dialog('open');
                $('#dialog-create-scenario').dialog({
                    buttons: {
                      "Create scenario": function() {
                           var bValid = true;  
                           allFields.removeClass('ui-state-error');
                           bValid = bValid && checkLength(name,"scenario name",3,1000);
                           if (bValid) {
                               $.post('<c:url value="/scenario"/>', { scenarioName: name.val(), serviceId: serviceId, tag: tag.val(), 
                                    matchStringArg: match.val(), matchStringArgRegexFlag: match_regex_flag.is(':checked'),
                                    responseMessage: responsemsg.val(), responseHeader: scenario_response_header.val(),
                                    httpResponseStatusCode: http_response_status_code.val(),
                                    universalErrorScenario: universal_error_scenario.is(':checked'), 
                                    errorScenario: error_scenario.is(':checked')  } ,
                                    function(data){
                                        $(this).dialog('close');
                                        document.location="<c:url value="/home" />?serviceId="+ serviceId;
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
    
    $('.viewServiceScenarioLink').each( function() {
        $(this).click( function() {
            var scenarioId = this.id.split("_")[1];
            var serviceId = this.id.split("_")[2];
            
            $.ajax({
                type: "GET",
                dataType: 'json',
                url: "<c:url value="/view/scenario"/>?serviceId="+serviceId+"&scenarioId="+scenarioId,
                success: function(data) {
                    $('#service-name-for-scenario').text(data.serviceName);
                    $('#scenario_name').val(data.name);
                    $('#scenario_match').val(data.match);
                    $('#scenario_match_regex_flag').attr('checked', data.matchRegexFlag);
                    $('#tag').val(data.tag);
                    $('#scenario_response_header').val(data.responseHeader);
                    $('#scenario_response').val(data.response); 
                    $('#error_scenario').attr('checked', data.scenarioErrorFlag);
                    $('#universal_error_scenario').attr('checked', data.universalScenarioErrorFlag);
                    $('#http_response_status_code').val(data.httpResponseStatusCode);
                    $('#dialog-create-scenario').dialog('open');
                    $('#dialog-create-scenario').dialog({
                        buttons: {
                          "Update scenario": function() {
                               var bValid = true;  
                               allFields.removeClass('ui-state-error');
                               bValid = bValid && checkLength(name,"scenario name",3,1000);
                               if (bValid) {
                                   
                                   $.post('<c:url value="/scenario"/>', { scenarioName: name.val(), serviceId: serviceId, scenarioId: scenarioId,  
                                        tag: tag.val(), matchStringArg: match.val(), matchStringArgRegexFlag: match_regex_flag.is(':checked'), 
                                        responseHeader: scenario_response_header.val(), responseMessage: responsemsg.val(), 
                                        universalErrorScenario: universal_error_scenario.is(':checked'), httpResponseStatusCode: http_response_status_code.val(),
                                        errorScenario: error_scenario.is(':checked')  } ,function(data){
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
    
    $('.save-as-a-service-scenario').button().click(function() {
        var requestId = this.id.split("_")[1];
        // 1. Get the recorded conversation.
        // 2. Populate the form with the data.
        // 3. Save the scenario.
        var serviceId; 
        var serviceName;
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: '<c:url value="/conversation/record"/>?&conversationRecordId='+requestId,
            success: function(data) {
              serviceId = data.serviceId;
              serviceName = data.serviceName;
              $('#service-name-for-scenario').text(serviceName);
              $('#tag').val('');
              $('#scenario_name').val('Give this a name');
              $('#scenario_match').val('');
              $('#scenario_response').val(data.responseBody); 
              $('#scenario_response_header').val(data.responseHeader);
            }
        });
        
        $('#dialog-create-scenario').dialog('open');
            $('#dialog-create-scenario').dialog({
                buttons: {
                  "Create scenario": function() {
                       var bValid = true;  
                       
                       allFields.removeClass('ui-state-error');
                       bValid = bValid && checkLength(name,"scenario name",3,1000);
                       if (bValid) {
                           $.post('<c:url value="/scenario"/>', { 
                                scenarioName: name.val(), 
                                serviceId: serviceId, 
                                tag: tag.val(), 
                                matchStringArg: match.val(), 
                                matchStringArgRegexFlag: match_regex_flag.val(), 
                                responseHeader: scenario_response_header.val(), 
                                responseMessage: responsemsg.val(), 
                                universalErrorScenario: universal_error_scenario.val(), 
                                httpResponseStatusCode: http_response_status_code.val(),
                                errorScenario: error_scenario.val()  } ,
                                function(data){
                                    $(this).dialog('close');
                                    document.location="<c:url value="/home" />?serviceId="+ serviceId;
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
</script>

<div id="dialog-create-scenario" title="Service Scenario">
    <div class="childform">
    <p class="validateTips"></p>
    This scenario belongs to the service called: <br />
    <h2 class="big" id="service-name-for-scenario"></h2>
    <p>
    <fieldset>
        <hr />
        <label for="scenario_name"><strong>Scenario Name:</strong></label>
        <input type="text" name="scenario_name" id="scenario_name" class="text ui-widget-content ui-corner-all" />
        <br />
        Required. Note: Scenarios will be listed alphabetically, so if you're going to use it often, label it accordingly. 
        <hr />
        <p style"padding-bottom:25px;">
        <span style="float:right;">Pretty format: 
        <a href="#" class="blue" id="prettyPrintMyHtml">HTML</a>
        <a href="#" class="blue" id="prettyPrintMyJson">JSON</a></span>
        <label for="scenario_response"><strong>Response Content:</strong></label>
        </p>
        <textarea name="scenario_response" id="scenario_response" class="text ui-widget-content ui-corner-all resizable" rows="20"></textarea>
        <p><a href="#" id="toggleMoreOptionsDisplay"><span id="showOptionsText">Show More Options</span> <span id="hideOptionsText" style="display:none;">Hide Options</span></a></p>
        <div id="moreOptionsDisplay"  style="display:none;">
            <p class="tinyfieldset info_message">For information on these input fields, please read the <a href="<c:url value="/help#scenario"/>"><strong>Help</strong></a> section.</p>
	        <hr />
	        <label for="scenario_match"><strong>Match Argument:</strong></label>
	        <input type="text" name="scenario_match" id="scenario_match" class="text ui-widget-content ui-corner-all" />
	        <input type="checkbox" name="scenario_match_regex_flag" id="scenario_match_regex_flag"  value="true" ></input> 
	        Check the box if you want the match argument to be treated as a regular expression. 
	        <hr />
	        <label for="tag"><strong>Tag(s):</strong></label> 
	        <input type="text" name="tag" id="tag" class="text ui-widget-content ui-corner-all" />
	        <br />
	        (Optional) Comma seperated tags. Examples 'release-123', 'qa', 'iphone', 'android'
	        <hr />
	        <label for="http_response_status_code"><strong>HTTP Response Status:</strong></label>
	        <div style="padding:1em 0 1em 0">
	        <select class="text ui-widget-content ui-corner-all" id="http_response_status_code" name="http_response_status_code" >
	        <c:forEach var="httpRespCode" items="${httpRespCodeList}"  varStatus="status">
	         <option class="text ui-widget-content" value="<c:out value="${httpRespCode.code}" />"><mockey:slug text="${httpRespCode.text}" maxLength="90"/></option>
	        </c:forEach>
	        </select>
	        </div>	 
	        By default, this will be set to 200. This is helpful when you need to validate how your code handles 500, 302, 404, and more.
	        <hr />     
		    <div>
			    <p style"padding-bottom:25px;">
			    <label for="scenario_header_name"><strong>HTTP Header Fields:</strong></label>
			    <p>
			    <textarea name="scenario_response_header" id="scenario_response_header" class="text ui-widget-content ui-corner-all resizable" rows="5"></textarea>
			    <p>Optional. Feel free to leave this empty, but just in case your app needs to parse some header information, this is where you hook it in. 
			    </p>
			    <p>
			    	<strong>Note:</strong> 
			    	Field name and value pairs are pipe '|' delimited. Mockey will parse this input and set things accordingly. Example:
			    	<br />
				    <span style="margin-left:50px;" class="code_text">Content-Type: text/html; charset=utf-8 <strong>|</strong> Cache-Control: max-age=3600</span><br />... will result in:
				    <br />
				    <span style="margin-left:50px;" class="code_text">
				    Content-Type: text/html;
				    </span>
				    <span style="margin-left:50px;" class="code_text">
				    <br />
				    Cache-Control:  max-age=3600
				   	</span>
				   	<br /><br />
			   		For more information, see: <a href="http://en.wikipedia.org/wiki/List_of_HTTP_header_fields">http://en.wikipedia.org/wiki/List_of_HTTP_header_fields</a>
			   	</p>
	        </div>
	        <hr />
	        <div>
	        <label><strong>Error Handling: </strong></label>
	        <p style"padding-top:5px;">
	                <input type="checkbox" name="universal_error_scenario" id="universal_error_scenario" value="true">Universal Error Response</input>
	                <br />
	                <input type="checkbox" name="error_scenario" id="error_scenario" value="true">Service Scenario Error Response</input>
			</p>	       
			<p>
			This let's Mockey know what to do when things go wrong. See the <a href="<c:url value="/help#scenario"/>">Help</a> section for more insight.
			</p>         
	        </div>
        </div>
    </fieldset> 
    </p>
    </div>
</div>
