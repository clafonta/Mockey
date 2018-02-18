<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Mockey - <c:out value="${requestScope.pageTitle}"/></title>
<meta http-equiv="content-type" content="text/html; charset=iso-8859-1">
<meta http-equiv="cache-control" content="none;">
<link rel="shortcut icon" href="<c:url value="/images/favicon.ico" />">
<%@ include file="/WEB-INF/common/inc_common_files.jsp" %>

<script language="Javascript">
<!---
function decision(message, url){
if(confirm(message)) location.href = url;
}
// --->


$(document).ready(function() {

    $('#poorStartMessage').hide();
    $('#container').show();
    $('input[title!=""]').hint();

	$('textarea.resizable:not(.processed)').TextAreaResizer();
	$('ul.sf-menu').superfish({
		delay:       1000,                            // one second delay on mouseout
		animation:   {opacity:'show',height:'show'},  // fade-in and slide-down animation
		speed:       'fast',                          // faster animation speed
		autoArrows:  false,                           // disable generation of arrow mark-up
		dropShadows: true                            // disable drop shadows
	});
	
   $('.toggle-header-tools').each( function() {
        $(this).click( function() {
          $('#header_tool_wrapper').toggle();
        });
     });
   $('.toggle-header-search').each( function() {
        $(this).click( function() {
          $('#header_search_wrapper').toggle();
        });
     });
	// 
	$.getJSON('<c:url value="/configuration/info" />', function(data) {
		if(data.result.proxy_enabled=='true'){
			$("#proxy_unknown").hide();
	       	$("#proxy_on").show();
       	    $("#proxy_off").hide();
	     }else {
	    	 $("#proxy_unknown").hide();
	    	 $("#proxy_on").hide();
	    	 $("#proxy_off").show(); 
	     }
	    

		if(data.result.twist_enabled==true){
            $("#twisting_on").show();
            $("#twisting_off").hide();
            
         }else {
             $("#twisting_on").hide();
             $("#twisting_off").show(); 
         }
        $('#twist-config').show(); 
        
        if(data.result.transient_state==true){
            $("#transient_unknown").hide();
            $("#transient_true").show();
            $("#transient_false").hide();
            
         }else {
             $("#transient_unknown").hide();
             $("#transient_true").hide();
             $("#transient_false").show(); 
         }
        $('#memory-only-config').show(); 
        
        if(data.result.filter_view_status=='on'){
          $("#filter-tag-field").val(data.result.filter_view_arg);
        }
         
         
        
       
	});

	$('#reset-sticky-session').click( function() {
		$('#reset-session-confirm').dialog({width: 400, height:200, modal: true, resizable:true});
		$('#reset-session-confirm').dialog('open');
		$('#reset-session-confirm').dialog({
            buttons: {
              "Reset Sticky Sessions": function() {
					$.post('<c:url value="/configuration/reset_sticky_cookie_session"/>' ,function(data){
			            if(data.reset){
			               $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast'); 
			             }
			        }, 'json' );      
					$('#reset-session-confirm').dialog('close');                    
              }, 
              Cancel: function(){
                  $('#reset-session-confirm').dialog('close');
                  
              }
            }
      }); 
      return false;
    });
	$("#reset-session-confirm").dialog({
        autoOpen: false
    });
    
     $('.transient-onclick').each( function() {
        $(this).click( function() {
            var tVal = this.id.split("_")[1];
            var isTrueSet = (tVal !== 'true');
           
            $('#transient-confirm').show();
            $('#transient-confirm').dialog('open');
            $('#transient-confirm').dialog({
                modal: true,
                resizable: false,
                buttons: {
                  "Go for it.": function() {
                    
                       $.post('<c:url value="/configuration/info?transient_state="/>' + isTrueSet ,function(data){
                        if(data.result){
                           $('#updated').fadeIn('fast').animate({opacity: 1.0}, 300).fadeOut('fast'); 
                           if(data.result.transient_state){
                              $("#transient_true").show();
                              $("#transient_false").hide();
                            }else {
                              $("#transient_true").hide();
                              $("#transient_false").show();
                            }
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
       $('#dialog-flush-confirm').dialog("destroy");
     });
	
	
    $("#dialog-flush-confirm").dialog({
        resizable: false,
        height:120,
        modal: true,
        autoOpen: false
    });


    
    $("#filter-tag-update-button").click( function() {
           var filterTag = $('#filter-tag-field').val();
           
           $.post('<c:url value="/taghelp"/>', { action: 'filter_tag_on', tag: filterTag } ,function(data){
					   //console.log(data);
					   if(data.success){
						   document.location="<c:url value="/home" />";
					    }else {
					       alert("Hmm...");
					    }
				}, 'json' );
           
        });

    $('.filter-tag-item').each( function() {
        $(this).click( function() {
            var  filterTag = this.id;
            $.post('<c:url value="/taghelp"/>', { action: 'filter_tag_on', tag: filterTag } ,function(data){
                //console.log(data);
                if(data.success){
                    document.location="<c:url value="/home" />";
                }else {
                    alert("Hmm...");
                }
            }, 'json' );
        });
    });
    
    $('.clear-tag-button').each( function() {
       $(this).click( function() {
           $.post('<c:url value="/taghelp"/>', { action: 'filter_tag_off' } ,function(data){
					   //console.log(data);
					   if(data.success){
						   document.location="<c:url value="/home" />";
					    }else {
					       alert("Hmm...");
					    }
				}, 'json' );
        });
      });
    $('#flush').each( function() {
        $(this).click( function() {
        	$('#dialog-flush-confirm').show();
            $('#dialog-flush-confirm').dialog('open');
            $('#dialog-flush-confirm').dialog({
                modal: true,
                resizable: false,
                buttons: {
                  "Delete everything": function() {
                      document.location="<c:url value="/home?action=deleteAllServices" />";                          
                  }, 
                  Cancel: function(){
                      $(this).dialog('close');
                  }
                }
          }); 
          return false;
       });
       $('#dialog-flush-confirm').dialog("destroy");
     });
    
    $('#search_me').each( function() {
        $(this).click( function() {
            var term = $('#search_term').attr('value');
            document.location="<c:url value="/search?term=" />" + term;
          }); 
       
     });
     
     $('#search_term').keypress(function(e) {
        if(e.which == 13) {
            var term = $('#search_term').val();
            document.location="<c:url value="/search?term=" />" + term;
        }
    });
    
    $('.toggle-conflict-link').each( function() {
        $(this).click( function() {
            var tVal = this.id.split("_")[1];
            $('#conflict-info_' + tVal).toggle();
            
          }); 
       
     });
    
     
});


</script>
</head>
<body>
<div id="container" style="display:none;">

<div id="logo">
    <!-- <a href="<c:url value="/home" />" class="nav"><span style="vertical-align:middle;font-size:20px; text-shadow: 0px 0px 1px #FF0084;" class="nav">Mockey</span></a> -->
    <div style="clear:both;"/>
	<%@ include file="/WEB-INF/common/message.jsp"%>
	<%
	String ua = request.getHeader( "User-Agent" );
	boolean isFirefox = ( ua != null && ua.indexOf( "Firefox/" ) != -1 );
	boolean isMSIE = ( ua != null && ua.indexOf( "MSIE 6.0" ) != -1 );
	response.setHeader( "Vary", "User-Agent" );
	%>
	<% if( isMSIE ){ %>
	  <span class="alert_message" style="position:absolute; bottom:0; left:0; width:60px; font-size:0.8em;z-index:100;"><strong>Warning:</strong>This app' isn't designed for <b>Internet Explorer 6.0</b>. You should use another browser.</span>
	<% } %>
	<div id="dialog-flush-confirm" class="hide" title="Flush"><div style="text-align: center;"><img src="<c:url value="/images/flush.png" />"/></div> <br />Are you sure? This will delete everything. You may want to <a href="<c:url value="/export" />">export your stuff</a> first.</div>
	<div id="transient-confirm" class="hide" title="Storage Setting">This will toggle your storage setting. For more information on this, look <a href="<c:url value="/help#storage" />">here</a></div>
	<div id="reset-session-confirm" class="hide" title="Reset Session">Are you sure? This will flush out any sticky cookies that Mockey may have kept due to serving up active-session-based proxy requests.
	<p> <i>Does your brain hurt now?</i></p>
	</div>
	
	<div id="topnav" style="margin-bottom:0.5em;width:100%;">
		<ul class="sf-menu" >
			<li class="<c:if test="${currentTab == 'home'}">current</c:if>"><a
				href="<c:url value="/home" />">Start here...<span class="sf-sub-indicator"> &#187;</span></a>
        <ul>

        <li <c:if test="${currentTab == 'setup'}">class="current"</c:if>><a title="Service Setup - create new service"
        href="<c:url value="/setup" />">Create a Service</a></li>



        <li <c:if test="${currentTab == 'merge'}">class="current"</c:if>>
        <a title="Merge - combine services" href="<c:url value="/merge" />"
        style="">Merge Services</a></li>
        
        <li <c:if test="${currentTab == 'inject'}">class="current"</c:if>>
        <a title="Real URL injecting" href="<c:url value="/inject" />"
        style="">URL Injection</a></li>
        
        <li <c:if test="${currentTab == 'proxy'}">class="current"</c:if>>
          <a href="<c:url value="/proxy/settings"/>" >Proxy Settings <b id="proxy_off" style="display: none;">(OFF)</b><b id="proxy_on" style="display: none;">(ON)</b></a>
        </li>
        
        <li><a id="flush" href="#">Flush</a></li>
        
        <li><span id="reset-sticky-cookie-config">
            <a href="#" id="reset-sticky-session" title="Reset the sticky cookie session that Mockey may be keeping.">Reset Session</a></span></li>

        <li  <c:if test="${currentTab == 'twisting'}">class="current"</c:if>>
            <a href="<c:url value="/twisting/setup"/>">Twisting <b id="twisting_on" style="display:none;">(ON)</b><b id="twisting_off" style="display:none;">(OFF)</b></a>
        </li>

        <li>
          
        </li>
        
        <li <c:if test="${currentTab == 'filesysteminfo'}">class="current"</c:if>>
        <a title="Image Depot" href="<c:url value="/filesysteminfo" />"
        style="">Image Depot</a></li>   
        </ul>
			</li>



			<li <c:if test="${currentTab == 'upload'}">class="current"</c:if>>
        <a href="<c:url value="/upload" />">Import/Export</a></li>

      
			<li <c:if test="${currentTab == 'history'}">class="current"</c:if>>
				<a href="<c:url value="/history" />">History <span class="sf-sub-indicator"> &#187;</span></a>
				<ul>
                <li><a id="chart" href="<c:url value="/servicestatschart" />"> History Stats</a></li>
                </ul>
			</li>
			
	        <li class="<c:if test="${currentTab == 'help'}">current</c:if>">
	        <a href="<c:url value="/help" />">Help  <span class="sf-sub-indicator"> &#187;</span></a>
	            <ul>
                <li <c:if test="${currentTab == 'api'}">class="current"</c:if>><a title="Configuration API"
                href="<c:url value="/service_api" />"><i aria-hidden="true" class="icon-cog"></i>&nbsp;Configuration API</a></li>
                <li <c:if test="${currentTab == 'console'}">class="current"</c:if>><a title="Debug Console"
                href="<c:url value="/console" />"><i aria-hidden="true" class="icon-glasses"></i>&nbsp;Debug Console</a></li>
                <li <c:if test="${currentTab == 'console'}">class="current"</c:if>><a title="View for feature phones"
                href="<c:url value="/mobi" />"><i aria-hidden="true" class="icon-mobile"></i>&nbsp;Mobile</a></li>
                <li ><a title="JSON Schema Validation" href="<c:url value="/jsonschemavalidate" />">
                <i aria-hidden="true" class="icon-power"></i>&nbsp;JSON Schema</a></li>
	            </ul>
	        </li>
          
		</ul>


	<div style="float:right;">
	   <span style="float:right;"><img style="height:30px; " src="<c:url value="/images/logo.png" />" /></span>
	   <a href="<c:url value="/home" />" class="logo_link">Mockey</a>
	</div>

  <div style="display:inline-block; margin-top:10px;">
      <span id="memory-only-config" style="display:none; padding-left:10px; margin-top:20px;">
        Storage set to:
        <a href="#" id="transient_unknown"  style="display: none;">___</a>
        <a href="#" id="transient_true" class="transient-onclick response_not" val="true" style="display: none; color:red;">In Memory Only</a>
        <a href="#" id="transient_false" class="transient-onclick response_not" val="false" style="display: none; color: #10CF33;">Writing to file</a> 
      </span>
  </div>
  
  <div style="clear:both;"/>
  </div>

    

	<div style="clear:both;"/>
		
  </div>

     


