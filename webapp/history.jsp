<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="pageTitle" value="History" scope="request" />
<c:set var="currentTab" value="history" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>
<script>
$(document).ready(function() {

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

	$('.viewFulfilledRequestLink').each( function() {
		$(this).click( function() {
			var requestId = this.id.split("_")[1];			
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

	$('.hideFulfilledRequestLink').each( function() {
		$(this).click( function() {
			var requestId = this.id.split("_")[1];			
			$('#letmesee_'+requestId).fadeOut();
		});
	});

	
});

$(document).ready(function() {
    $('#clear_history').click(
        function () {
             $.prompt(
                    'Are you sure? This will delete all fulfilled request for all requesting IPs.', {
                        callback: function (proceed) {
                            if(proceed) document.location="<c:url value="/history?action=delete_all" />";
                        },
                        buttons: {
                            'Delete All History': true,
                            Cancel: false
                        }
                    }
                );
        })
});
</script>
<div id="main">
    <h1>Service History: <span class="highlight"><c:out value="${mockservice.serviceName}"/></span></h1>
    <form action="<c:url value="/history"/>" method="get">
    <p>
    <input type="text" name="token" size="80"/>
    <input type="submit" name="Filter" value="Add Filter" />
    
    <c:if test="${!empty requests}">
      <c:url value="/history" var="deleteAllScenarioUrl">
         <c:param name="action" value="delete_all" />
      </c:url>
      <a  class="spread" id="clear_history" href="#">Clear History</a>  
    </c:if>
    <c:if test="${!empty historyFilter.tokens}">  
       <a class="spread" href="<c:url value="/history?action=remove_all_tokens"/>">Clear Filters</a>
    </c:if>
    </p>
    </form>
    <c:if test="${!empty historyFilter.tokens}">    
    <div class="hint_message">
    <h4>You are filtering your history on:</h4>
    <c:forEach var="token" items="${historyFilter.tokens}">
        <a style="text-decoration: none;" title="Remove filter token" href="<mockey:history value="${token}"/>"><c:out value="${token}"/><img src="<c:url value="/images/bullet_delete.png" />" /></a> 
    </c:forEach>
    <p></p>
    </div>
    </c:if>
    <c:choose>
        <c:when test="${!empty requests}">
            <c:forEach var="request" items="${requests}" varStatus="status">            
                <div id="fulfilledRequest_${request.id}" class="parentform" style="padding: 0.2em 0.5em; 0.2em 0.5em;">
                
                 
                   <c:url value="/history" var="filterByIp">
                      <c:param name="token" value="${request.requestorIP}" />
                   </c:url>
                   <c:url value="/history" var="filterByServiceName">
                       <c:param name="token" value="${request.serviceName}" />	                                                                              
                   </c:url>
                   <c:url value="/setup" var="serviceUrl">
                          <c:param name="serviceId" value="${request.serviceId}" />                                                                               
                      </c:url>  
                   
                   <div style="text-align:right;  position: relative;">
                   <a href="<c:out value="${filterByServiceName}"/>" title="Filter by Service Name">add to filter</a> |
                   <a href="#" id="viewFulfilledRequest_${request.id}" class="viewFulfilledRequestLink">view</a> |
                   <a href="#" id="hideFulfilledRequest_${request.id}" class="hideFulfilledRequestLink">hide</a> |                  
                   <a href="#" id="deleteFulfilledRequest_${request.id}" class="deleteFulfilledRequestLink"><img style="margin-bottom:-0.2em;" src="<c:url value="/images/cross.png"/>"></a>
                   </div>
                   <div style="width:600px; position:relative; margin-top:-1em;">
                   <b>Time:</b> <c:out value="${request.time}"/> for client IP: <b><a href="<c:out value="${filterByIp}"/>" title="Filter by IP"><c:out value="${request.requestorIP}"/></a></b> for service <b><a href="<c:out value="${serviceUrl}"/>" title="Service"><c:out value="${request.serviceName}"/></a></b>
                   
                                     
                <div id="letmesee_${request.id}">
                </div>
                </div>
                    
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <div><p>No historical requests. </p></div>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />