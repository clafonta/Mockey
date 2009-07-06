<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="actionKey" value="conf_service" scope="request" />
<c:set var="pageTitle" value="Configure" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
<div id="main">
    <%@ include file="/WEB-INF/common/message.jsp"%>   
     
    <h1>Service Configuration: <span class="highlight"><c:out value="${mockservice.serviceName}"/></span></h1>
    <%@ include file="/WEB-INF/common/inc_action_links.jsp"%>
    <div>
        <c:url value="/scenario" var="scenarioUrl">
            <c:param name="serviceId" value="${mockservice.id}" />
        </c:url>
        <form action="<c:url value="/configure" />">
        <input type="hidden" name="serviceId" value="<c:out value="${mockservice.id}"/>" />
            <table class="simple" width="100%">
                <tbody>
                    <tr>
                        <th width="180px"><p>Hang time:</p></th>
                        <td>
                            <p><input type="text" name="hangTime" maxlength="5" size="5" value="<c:out value="${mockservice.hangTime}"/>" /> milliseconds</p>
                        </td>
                    </tr>
                    <tr>
                        <th><p>Proxy or scenario?</p></th>
                        <td>
	                        <p <c:if test='${!mockservice.proxyOn}'>class="overlabel"</c:if>>
	                            <input type="radio" name="proxyOn" value="true" <c:if test='${mockservice.proxyOn}'>checked</c:if> /> 
	                            <b>Proxy response -</b> Pass your request to the <i>real</i> service URL, reply
	                                with the real service's response message, and record the event. 
	                                <c:if test="${empty mockservice.realServiceUrl and mockservice.proxyOn}">
	                                    <div>
	                                        <p class="alert_message">You need to <a href="<c:out value="${setupUrl}"/>" title="edit">define a real URL</a></p>
	                                    </div>
	                                </c:if>
	                        </p>
	                        <p <c:if test='${mockservice.proxyOn}'>class="overlabel"</c:if>>
	                            <input type="radio" name="proxyOn" value="false" <c:if test='${!mockservice.proxyOn}'>checked</c:if> /> 
	                            <b>Scenario -</b> Pick the type of scenario. 
	                            <c:if test="${empty mockservice.scenarios and !mockservice.proxyOn}">
	                                <div>
	                                    <p class="alert_message">You need to <a href="<c:out value="${scenarioUrl}"/>" title="Create service scenario" border="0" />create</a>
	                                     a scenario before using "Scenario".</p>
	                                    <input type="hidden" name="proxyOn" value="true" />
	                                </div>
	                            </c:if>
	                        </p>
                        </td>
                   </tr>
                    <c:if test="${!mockservice.proxyOn}">
                        <c:if test="${!empty mockservice.scenarios}">
                            <tr>
                                <th><p>Choose dynamic matching or specific scenario</p></th>
                                <td>
									<p <c:if test='${!mockservice.replyWithMatchingRequest}'>class="overlabel"</c:if>>
									    <input type="radio" name="replyWithMatchingRequest" value="true" <c:if test='${mockservice.replyWithMatchingRequest}'>checked</c:if>></input>
									    <b>Matching scenario:</b> This option will return the response from the scenario with a matching request message.
									</p>
                                    <p <c:if test='${mockservice.replyWithMatchingRequest}'>class="overlabel"</c:if>>
                                        <input type="radio" name="replyWithMatchingRequest" value="false" <c:if test='${!mockservice.replyWithMatchingRequest}'>checked</c:if> />
                                        <b>From scenario:</b> 
                                        <span>
                                            <ul class="group">
                                                <c:forEach var="scenario" begin="0" items="${mockservice.scenarios}">
                                                    <li>
                                                        <c:if test='${!mockservice.replyWithMatchingRequest}'>
                                                            <input type="radio" name="defaultScenarioId" value="<c:out value="${scenario.id}"/>"
                                                            <c:if test='${mockservice.defaultScenarioId eq scenario.id}'>checked</c:if> />
                                                        </c:if> 
                                                        <c:url value="/scenario" var="scenarioEditUrl">
                                                            <c:param name="serviceId" value="${mockservice.id}" />
                                                            <c:param name="scenarioId" value="${scenario.id}" />
                                                        </c:url> 
                                                        <a href="<c:out value="${scenarioEditUrl}"/>" title="Edit service scenario"><c:out value="${scenario.scenarioName}" /></a>
                                                    </li>

                                                </c:forEach>
                                            </ul>
                                        </span>
                                   </p>
                                </td>
                            </tr>
                        </c:if>
                    </c:if>
                </tbody>
            </table>
            <div align="right">
                <p style="text-align: bottom;"><input type="submit" name="update" value="Update" /></p>
            </div>
        </form>
    </div>    
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
