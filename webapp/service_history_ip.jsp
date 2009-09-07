<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="pageTitle" value="History" scope="request"/>
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
				url: "/history/detail?action=delete&serviceId="+unusedServiceId+"&fulfilledRequestId="+requestId
			});
			$('#fulfilledRequest_'+requestId).fadeOut(500, function() {
				$('#fulfilledRequest_'+requestId).remove();
			});
		});
	});
});
</script>
<div id="main">
    <h1>Service History: <span class="highlight"><c:out value="${mockservice.serviceName}"/></span></h1>
    <%@ include file="/WEB-INF/common/inc_action_links.jsp"%>
    <c:choose>
        <c:when test="${!empty scenarioHistoryList}">
            <p>
             <c:url value="/history/detail" var="deleteAllScenarioUrl">
	                                       <c:param name="serviceId" value="${mockservice.id}" />
	                                       <c:param name="iprequest" value="${iprequest}" />
	                                       <c:param name="action" value="delete_all" />
	                                    </c:url>
	                                   <a href="<c:out value="${deleteAllScenarioUrl}"/>">Clear All</a>
            </p>
            <c:forEach var="scenario" items="${scenarioHistoryList}" varStatus="status">
                <p><div id="fulfilledRequest_${scenario.id}">
                    <form action="<c:url value="/scenario"/>" method="post">
	                    <input type="hidden" name="actionTypeGetFlag" value="true" />
	                    <input type="hidden" name="serviceId" value="<c:out value="${mockservice.id}"/>" />
	                    <table class="simple" width="100%">
	                        <tbody>
	                            <tr>
	                                <td>
	                                    <p style="text-align:right;">
	                                    <c url value="/history/detail" var="deleteScenarioUrl">
	                                       <c param name="serviceId" value="${mockservice.id}" />
	                                       <c param name="iprequest" value="${iprequest}" />
	                                       <c param name="scenarioId" value="${scenario.id}" />
	                                       <c param name="action" value="delete" />
	                                    </c url>
	                                    <a id="deleteFulfilledRequest_${scenario.id}" class="deleteFulfilledRequestLink"><img src="<c:url value="/images/cross.png"/>"></a>
	                                    </p>
	                                    <p><b>Time and IP:</b> <c:out value="${scenario.serviceInfo.scenarioName}"/> </p>
	                                </td>
	                            </tr>
	                            <tr>
	                                <td>
	                                  <div class="conflict_message"/>
	                                    <h3>Request:</h3>
	                                    <p>Header</p>
	                                    <p><textarea name="requestHeader" rows="10" cols="80%"><c:out value="${scenario.clientRequestHeaders}"/></textarea></p>
	                                    <p>Parameters</p>
	                                    <p><textarea name="requestHeader" rows="10" cols="80%"><c:out value="${scenario.clientRequestParameters}"/></textarea></p>
	                                    <p>Body</p>
	                                    <p><textarea name="requestMessage" rows="10" cols="80%"><c:out value="${scenario.clientRequestBody}"/></textarea></p>
	                                   </div>
	                                </td>
	                            </tr>
	                            <tr>
	                                <td >
	                                  <div id="scenario${scenario.id}" class="addition_message mockeyResponse">
	                                    <h3>Response: </h3>
	                                    <p>Status</p>
	                                    <p>
                                            <textarea name="responseStatus" rows="1" cols="80%"><c:out value="${scenario.responseMessage.statusLine}"/></textarea>
                                        </p>
	                                    <p>Header</p>
	                                    <p>
                                            <textarea name="responseHeader" rows="10" cols="80%"><c:out value="${scenario.responseMessage.headerInfo}"/></textarea>
                                        </p>
                                        <p>Body</p>
	                                    <p>
	                                        <button class="formatButton" style="border: 1px solid #006; background: #ccf; margin-left: 60%; border-bottom-width:0;">Format Body</button>
                                            <textarea style="margin-top: 0px;" name="responseMessage" class="responseContent" rows="10" cols="80%"><c:out value="${scenario.responseMessage.body}"/></textarea>
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