<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<script>
$(document).ready( function() {
	// SCENARIO CREATION JAVASCRIPT
    $("#dialog-create-scenario").dialog({
        resizable: true,
        height:500,
        width:700,
        modal: true,
        autoOpen: false
    });
    var name = $("#scenario_name"),
        match = $("#scenario_match"),
        responsemsg = $("#scenario_response"),
        universal_error_scenario = $('#universal_error_scenario'),
        tag = $('#tag'),
        error_scenario = $('#error_scenario'),
        allFields = $([]).add(name).add(match).add(universal_error_scenario).add(error_scenario).add(responsemsg).add(tag),
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
    $('.createScenarioLink').each( function() {
        $(this).click( function() {
            var serviceId = this.id.split("_")[1];
            // Clear input
            var serviceName = $('#serviceName_'+serviceId).val();
            $('#service-name-for-scenario').text(serviceName);
            $('#scenario_name').val('');
            $('#scenario_match').val('');
            $('#scenario_response').val(''); 
            $('#tag').val(''); 
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
                               $.post('<c:url value="/scenario"/>', { scenarioName: name.val(), serviceId: serviceId, tag: tag.val(), matchStringArg: match.val(),
                                    responseMessage: responsemsg.val(), 
                                    universalErrorScenario: universal_error_scenario.is(':checked'), 
                                    errorScenario: error_scenario.is(':checked')  } ,function(data){
                                        
                                    }, 'json' );  
                               $(this).dialog('close');              
                               document.location="<c:url value="/home" />?serviceId="+ serviceId;
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
                    $('#tag').val(data.tag);
                    $('#scenario_response').val(data.response); 
                    $('#error_scenario').attr('checked', data.scenarioErrorFlag);
                    $('#universal_error_scenario').attr('checked', data.universalScenarioErrorFlag);
                    $('#dialog-create-scenario').dialog('open');
                    $('#dialog-create-scenario').dialog({
                        buttons: {
                          "Update scenario": function() {
                               var bValid = true;  
                               allFields.removeClass('ui-state-error');
                               bValid = bValid && checkLength(name,"scenario name",3,1000);
                               if (bValid) {
                                   $.post('<c:url value="/scenario"/>', { scenarioName: name.val(), serviceId: serviceId, scenarioId: scenarioId,  tag: tag.val(), matchStringArg: match.val(),
                                        responseMessage: responsemsg.val(), universalErrorScenario: universal_error_scenario.is(':checked'), 
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
              tag = data.tag;
              $('#service-name-for-scenario').text(serviceName);
              $('#tag').text(tag);
              $('#scenario_name').val('Give this a name');
              $('#scenario_match').val('');
              $('#scenario_response').val(data.responseBody); 
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
                           $.post('<c:url value="/scenario"/>', { scenarioName: name.val(), serviceId: serviceId, tag: tag.val(), matchStringArg: match.val(),
                                responseMessage: responsemsg.val(), universalErrorScenario: universal_error_scenario.val(), 
                                    errorScenario: error_scenario.val()  } ,function(data){
                                    
                                }, 'json' );  
                           $(this).dialog('close');              
                           document.location="<c:url value="/home" />?serviceId="+ serviceId;
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
    <p class="validateTips"></p>
    <div class="info_message">This scenario belongs to the service called: <strong id="service-name-for-scenario"></strong></div>
    <p>
    <fieldset>
        
        <label for="scenario_name">Scenario name (required)</label>
        <input type="text" name="scenario_name" id="scenario_name" class="text ui-widget-content ui-corner-all" />
        <label for="scenario_match">Match argument</label>
        <input type="text" name="scenario_match" id="scenario_match" class="text ui-widget-content ui-corner-all" />
        <label for="scenario_match">Tag(s)</label> [optional]
        <input type="text" name="tag" id="tag" class="text ui-widget-content ui-corner-all" title="Add 1 or more tags seperated with spaces. (Optional)" />
        <div class="tinyfieldset childform" style="margin-bottom: 1em;">
                <input type="checkbox" name="universal_error_scenario" id="universal_error_scenario" value="true">Universal Error Response</input>
                <br />
                <input type="checkbox" name="error_scenario" id="error_scenario" value="true">Service Scenario Error Response</input>
                <div id="" style="" class="tinyfieldset info_message">
                If these checkboxes are checked, then it tells Mockey how to handle errors. For more information, see  
                <a style="color:blue;" href="<c:url value="/help#error_handling"/>">here</a>
                </div>
        </div>
        <label for="scenario_response">Response content</label>
        <textarea name="scenario_response" id="scenario_response" class="text ui-widget-content ui-corner-all resizable" rows="10"></textarea>
    </fieldset> 
    </p>
</div>
