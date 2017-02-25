<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%@ taglib prefix="mockey-tag" tagdir="/WEB-INF/tags" %>
<script>
$(document).ready( function() {
	// SCENARIO CREATION JAVASCRIPT
    $("#dialog-create-scenario").dialog({
        resizable: true,
        width:800,
        modal: true,
        autoOpen: false
    });
    var name = $("#scenario_name"),
        match = $("#scenario_match"),
        match_evaluation_rules_flag = $('#scenario_match_evaluation_rules_flag'),
        responsemsg = $("#scenario_response"),
        universal_error_scenario = $('#universal_error_scenario'),
        scenario_response_header = $('#scenario_response_header'),
        tag = $('#tag'),
        hang_time = $('#hangtime'),
        http_response_status_code = $('#http_response_status_code');
        http_method_type = $('#http_method_type');
        error_scenario = $('#error_scenario'),
        allFields = $([]).add(name).add(match).add(match_evaluation_rules_flag).add(http_method_type).add(universal_error_scenario).add(error_scenario).add(responsemsg).add(tag).add(hang_time).add(http_response_status_code).add(scenario_response_header),
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
    $('#toggleAdvancedScenarioCreateDetails').click ( function() {
        $.each($('.js-showhide-scenario-create-details'), function() {
			$(this).toggle();	

		});	
        return false;
    });
    
    $('#prettyPrintMyJsonRules').click ( function() {
        var txt = $('#scenario_match').val();
    	var pTxt = js_beautify(txt);
    	$('#scenario_match').val(pTxt);
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
        	// Clear tips
        	$('#errorInfo').html('');
            var serviceId = this.id.split("_")[1];
            // Clear input
            var serviceName = $('#serviceName_'+serviceId).val();
            $('#service-name-for-scenario').text(serviceName);
            $('#scenario_name').val('');
            $('#scenario_match').val('');
            $('#scenario_match_evaluation_rules_flag').attr('checked', false),
            $('#scenario_response').val(''); 
            $('#scenario_response_header').val('');
            $('#tag').val(''); 
            $('#hangtime').val(''); 
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
                               $.post('<c:url value="/scenario"/>', { scenarioName: name.val(), serviceId: serviceId, tag: $('input[name=tag]').val(), hangTime: $('input[name=hangtime]').val(), 
                                    matchStringArg: match.val(), matchStringArgEvaluationRulesFlag: match_evaluation_rules_flag.is(':checked'),
                                    responseMessage: responsemsg.val(), responseHeader: scenario_response_header.val(),
                                    httpResponseStatusCode: http_response_status_code.val(),
                                    httpMethodType: http_method_type.val(),
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
                    $('#scenario_match_evaluation_rules_flag').attr('checked', data.matchRegexFlag);
                    $('#tag').val(data.tag);
                    $('#hangtime').val(data.hangtime);
                    $('#scenario_response_header').val(data.responseHeader);
                    $('#scenario_response').val(data.response); 
                    $('#error_scenario').attr('checked', data.scenarioErrorFlag);
                    $('#universal_error_scenario').attr('checked', data.universalScenarioErrorFlag);
                    $('#http_response_status_code').val(data.httpResponseStatusCode);
                    $('#http_method_type').val(data.httpMethodType);
                    $('#dialog-create-scenario').dialog('open');
                    $('#dialog-create-scenario').dialog({
                        buttons: {
                          "Update scenario": function() {
                          	   $('#errorInfo').html('');
                               var bValid = true;  
                               allFields.removeClass('ui-state-error');
                               bValid = bValid && checkLength(name,"scenario name",3,1000);
                               if (bValid) {
                                   
                                   $.post('<c:url value="/scenario"/>', { scenarioName: name.val(), serviceId: serviceId, scenarioId: scenarioId, hangTime: $('input[name=hangtime]').val(), 
                                        tag: $('input[name=tag]').val(), matchStringArg: match.val(), matchStringArgEvaluationRulesFlag: match_evaluation_rules_flag.is(':checked'), 
                                        responseHeader: scenario_response_header.val(), responseMessage: responsemsg.val(),
                                        universalErrorScenario: universal_error_scenario.is(':checked'), httpResponseStatusCode: http_response_status_code.val(),
                                        httpMethodType: http_method_type.val(),
                                        errorScenario: error_scenario.is(':checked')  } ,function(data){
                                               console.log("Saving data: "+data);
                                               $('#view-scenario_'+scenarioId+'_' +serviceId).fadeOut(function(){ $(this).text(name.val()).fadeIn() });
                                               $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast');
                                        }, 'json' );  
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
    	// Clear tips
        $('#errorInfo').html('');
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
              $('#hangtime').val('');
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
                                tag: $('input[name=tag]').val(), 
                                hangTime: $('input[name=hangtime]').val(), 
                                matchStringArg: match.val(), 
                                matchStringArgEvaluationRulesFlag: match_evaluation_rules_flag.val(), 
                                responseHeader: scenario_response_header.val(), 
                                responseMessage: responsemsg.val(), 
                                universalErrorScenario: universal_error_scenario.val(), 
                                httpResponseStatusCode: http_response_status_code.val(),
                                httpMethodType: http_method_type.val(),
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
    <p id="errorInfo" class="validateTips"></p>
    This scenario belongs to the service called: 
    <p class="big help_optional" id="service-name-for-scenario" style="text-align:center; padding: 0; margin: 0;"></p>
     
    
    <fieldset>
        <hr />
        <label for="scenario_name" class="field-label-title">Scenario Name*:</label>
        <input type="text" name="scenario_name" placeholder="Provide a unique and descriptive name." id="scenario_name" class="text ui-widget-content ui-corner-all" />
        <p class="field-label-helptxt">
        <strong>Required</strong>. Note: Scenarios will be listed alphabetically, so if you're going to use it often, label it accordingly. 
        </p>
        <hr />
        <p>
        <span style="float:right;">Pretty format: 
        <a href="#" class="blue" id="prettyPrintMyHtml">HTML</a>
        <a href="#" class="blue" id="prettyPrintMyJson">JSON</a></span>
        <label for="scenario_response" class="field-label-title">Response Content*:</label>
        </p>
        <textarea placeholder="Copy/paste your desired response, be it HTML, JSON, XML, or whatever." name="scenario_response" id="scenario_response" class="text ui-widget-content ui-corner-all resizable" rows="10"></textarea>
			<p class="tinyfieldset info_message">For information on these input fields, please read the <a href="<c:url value="/help#scenario"/>"><strong>Help</strong></a> section.</p>
			<p class="power-link tiny"><a id="toggleAdvancedScenarioCreateDetails" href="javascript:void(0)"><span class="js-showhide-scenario-create-details">Show Advanced</span><span style="display:none;" class="js-showhide-scenario-create-details" >Hide Advanced</span></a></p>
			<div style="display:none;" class="js-showhide-scenario-create-details">  

        
	        <hr />
	        <p style="padding-bottom:25px;">
			<span style="float:right;">Pretty format: 
			<a href="#" class="blue" id="prettyPrintMyJsonRules">JSON</a></span>
			<label for="scenario_match" class="field-label-title">Match Argument:</label>
			</p>
	        <textarea  name="scenario_match" id="scenario_match" placeholder="Enter a text term for basic search e.g. 'abc' or use evaluation rules. See the help section for more information. " class="text ui-widget-content ui-corner-all resizable" rows="8">
	        </textarea> 
	        <p class="field-label-helptxt">
	        	<strong class="help_optional">Optional.</strong>
	        	<input type="checkbox" name="scenario_match_evaluation_rules_flag" id="scenario_match_evaluation_rules_flag"  value="true" ></input> Enable evaluation rules. <br />
	        	Check the box if you want the match argument to be treated as <a href="<c:url value="/help#evaluation_rules_api"/>">evaluation</a> rules. Leaving the box unchecked will
	        result in a very basic text search, but please read the <a href="<c:url value="/help#beware_of_match"/>">beware</a> section before moving forward.
	        </p>
	        
	        
	        <hr />
	        <label for="tag" class="field-label-title">Tag(s):</label> 
	        <input type="text" name="tag" id="tag" class="text ui-widget-content ui-corner-all" placeholder="Enter tags here." />
	        <p class="field-label-helptxt">
	        <strong class="help_optional">Optional.</strong> Comma seperated tags. Examples 'release-123', 'qa', 'iphone', 'android'
	        </p>
	        
	        <hr />
	        <label for="hangtime" class="field-label-title">Hangtime:</label> 
	        <input type="number" name="hangtime" id="hangtime" class="text ui-widget-content ui-corner-all" placeholder="Enter hangtime here." />
	        <p class="field-label-helptxt">
	        <strong class="help_optional">Optional.</strong> The delay time in milliseconds. Mockey will wait for this long before returning this scenario. If a valid value is provided, then it will override the hang time value
	        defined in this Scenario's Service definition. 
	        </p>
	        
	        
	        <hr />
	        <label for="http_response_status_code" class="field-label-title">HTTP Method Type</label>
	        <div style="padding:1em 0 1em 0">
	        <select class="text ui-widget-content ui-corner-all" id="http_method_type" name="http_method_type" >
	         <option class="text ui-widget-content" value="">&#42;</option>
	         <option class="text ui-widget-content" value="GET">GET</option>
	         <option class="text ui-widget-content" value="PUT">PUT</option>
	         <option class="text ui-widget-content" value="POST">POST</option>
	         <option class="text ui-widget-content" value="DELETE">DELETE</option>
	        </select>
	        </div>
	        <p class="field-label-helptxt"> 
	        <strong class="help_optional">Optional.</strong> This is helpful when you want to ensure your RESTful request (e.g. GET, POST, PUT, DELETE) 
	        is tied to the right scenario. By default, set to &#42;, which means any HTTP method type will do.
	        </p>
	        
	        
	        <hr />
	        <label for="http_response_status_code" class="field-label-title">HTTP Response Status:</label>
	        <div style="padding:1em 0 1em 0">
	        <select class="text ui-widget-content ui-corner-all" id="http_response_status_code" name="http_response_status_code" >
	        <c:forEach var="httpRespCode" items="${httpRespCodeList}"  varStatus="status">
	         <option class="text ui-widget-content" value="<c:out value="${httpRespCode.code}" />"><mockey:slug text="${httpRespCode.text}" maxLength="90"/></option>
	        </c:forEach>
	        </select>
	        </div>	 
	        <p class="field-label-helptxt">
	        <strong class="help_optional">Optional.</strong> By default, this will be set to 200. This is helpful when you need to validate how your code handles 500, 302, 404, and more.
	        </p>
	        
	        
	        <hr />     
		    <div>
			    <p style="padding-bottom:25px;">
			    <label for="scenario_header_name" class="field-label-title">HTTP Header Fields:</label>
			    <p>
			    <textarea name="scenario_response_header" id="scenario_response_header" class="text ui-widget-content ui-corner-all resizable" rows="5"></textarea>
			    <p><strong class="help_optional">Optional.</strong> Feel free to leave this empty, but just in case your app needs to parse some header information, this is where you hook it in. 
			    </p>
			    <p class="field-label-helptxt">
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
	        <label class="field-label-title">Error Handling:</label>
	        <p style="padding-top:5px;">
	                <input type="checkbox" name="universal_error_scenario" id="universal_error_scenario" value="true">Universal Error Response</input>
	                <br />
	                <input type="checkbox" name="error_scenario" id="error_scenario" value="true">Service Scenario Error Response</input>
			</p>	       
			<p class="field-label-helptxt">
			<strong class="help_optional">Optional.</strong> This let's Mockey know what to do when things go wrong. See the <a href="<c:url value="/help#scenario"/>">Help</a> section for more insight.
			</p>         
	        </div>
        </div>
    </fieldset> 
    
    </div>
</div>
