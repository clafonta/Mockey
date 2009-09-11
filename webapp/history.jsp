<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="pageTitle" value="History" scope="request" />
<c:set var="currentTab" value="history" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
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
    </p>
    </form>
    <c:if test="${!empty historyFilter.tokens}">    
    <div class="hint_message">
    <h4>You are filtering your history on:</h4>
    <c:forEach var="token" items="${historyFilter.tokens}">
        <a style="text-decoration: none;" href="<mockey:history value="${token}"/>"><c:out value="${token}"/><img src="<c:url value="/images/bullet_delete.png" />" /></a> 
    </c:forEach>
    <p><a href="<c:url value="/history?action=remove_all_tokens"/>">Remove All Filters</a></p>
    </div>
    </c:if>
    <c:choose>
        <c:when test="${!empty requests}">
            <p>
             <c:url value="/history" var="deleteAllScenarioUrl">
                 <c:param name="action" value="delete_all" />
              </c:url>
             <a id="clear_history" href="#">Clear History</a>  
            </p>
            <c:forEach var="request" items="${requests}" varStatus="status">
                <p><div id="fulfilledRequest_${request.id}">
                    <form action="<c:url value="/scenario"/>" method="post">
	                    <input type="hidden" name="actionTypeGetFlag" value="true" />
	                    <input type="hidden" name="serviceId" value="<c:out value="${request.serviceId}"/>" />
	                    <table class="simple" width="100%">
	                        <tbody>
	                            <tr>
	                                <td>
	                                    <p style="text-align:right;">
	                                    	<a id="deleteFulfilledRequest_${request.id}" class="deleteFulfilledRequestLink"><img src="<c:url value="/images/cross.png"/>"></a>
	                                    </p>
	                                     <c:url value="/history" var="filterByIp">
	                                       <c:param name="token" value="${request.requestorIP}" />	
	                                                                              
	                                    </c:url>
	                                    <c:url value="/history" var="filterByServiceName">
	                                        <c:param name="token" value="${request.serviceName}" />	                                                                              
	                                    </c:url>
	                                    <c:url value="/setup" var="serviceUrl">
                                            <c:param name="serviceId" value="${request.serviceId}" />                                                                               
                                        </c:url>  
	                                    <p><b>Time:</b> <c:out value="${request.time}"/> for client IP: <b><a href="<c:out value="${filterByIp}"/>" title="Filter by IP"><c:out value="${request.requestorIP}"/></a></b> for service <b><a href="<c:out value="${serviceUrl}"/>" title="Service"><c:out value="${request.serviceName}"/></a></b> (<a href="<c:out value="${filterByServiceName}"/>" title="Filter by Service Name">add to filter</a>)</p>
	                                </td>
	                            </tr>
	                            <tr>
	                                <td>
	                                  <div class="conflict_message"/>
	                                    <h3>Request:</h3>
	                                    <p>Header</p>
	                                    <p><textarea name="requestHeader" rows="10" cols="80%"><c:out value="${request.clientRequestHeaders}"/></textarea></p>
	                                    <p>Parameters</p>
	                                    <p><textarea name="requestHeader" rows="10" cols="80%"><c:out value="${request.clientRequestParameters}"/></textarea></p>
	                                    <p>Body</p>
	                                    <p><textarea name="requestMessage" rows="10" cols="80%"><c:out value="${request.clientRequestBody}"/></textarea></p>
	                                   </div>
	                                </td>
	                            </tr>
	                            <tr>
	                                <td >
	                                  <div id="scenario${request.id}" class="addition_message mockeyResponse">
	                                    <h3>Response: </h3>
	                                    <p>Status</p>
	                                    <p>
                                            <textarea name="responseStatus" rows="1" cols="80%"><c:out value="${request.responseMessage.statusLine}"/></textarea>
                                        </p>
	                                    <p>Header</p>
	                                    <p>
                                            <textarea name="responseHeader" rows="10" cols="80%"><c:out value="${request.responseMessage.headerInfo}"/></textarea>
                                        </p>
                                        <p>Body</p>
	                                    <p>
	                                        <button class="formatButton" style="border: 1px solid #006; background: #ccf; margin-left: 60%; border-bottom-width:0;">Format Body</button>
                                            <textarea style="margin-top: 0px;" name="responseMessage" class="responseContent" rows="10" cols="80%"><c:out value="${request.responseMessage.body}"/></textarea>
                                        </p>
                                        <p>
                                        <input type="submit" name="Save" value="Save Response as a Scenario" />
                                        </p>
                                      </div>
	                                </td>
	                            </tr>
	                        </tbody>
	                    </table>
                    </form>
                </div></p>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <div><p>No historical requests. </p></div>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />