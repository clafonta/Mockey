<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="actionKey" value="edit_service" scope="request" />
<c:set var="pageTitle" value="Configure" scope="request" />
<c:set var="currentTab" value="create" scope="request" />
<%--@elvariable id="mockservice" type="com.mockey.MockServiceBean"--%>
<%@include file="/WEB-INF/common/header.jsp" %>
<script type="text/javascript">
	$(function() {
		$("#accordion").accordion({
			active: false,
			collapsible: true
		});
	});
	</script>


<div id="main">
    <%@ include file="/WEB-INF/common/message.jsp"%>
    <c:choose>
        <c:when test="${!empty mockservice.id}">
            <c:url value="/home" var="serviceUrl">
              <c:param name="serviceId" value="${mockservice.id}" />                                                                               
        	</c:url> 
            <h1>Service Setup: <span class="highlight"><a href="${serviceUrl}"><c:out value="${mockservice.serviceName}"/></a></span></h1>
        </c:when>
        <c:otherwise>
            <h1>Service Setup</h1>
        </c:otherwise>
    </c:choose>
   
    <form action="<c:url value="/setup"/>" method="POST">
        <input type="hidden" name="serviceId" value="<c:out value="${mockservice.id}"/>" />
        <table class="simple" width="100%">
            <tbody>
	            <tr><th><p>Service name:</p></th>
	                <td>
	                    <p><input type="text" name="serviceName" maxlength="100" size="90%" value="<c:out value="${mockservice.serviceName}"/>" /></p>
	                    <p class="tiny">Use a self descriptive name. For example, if you were to use this for 'authentication' testing, then call it 'Authentication'.</p>
	                </td>
	            </tr>
                <tr>
                    <th><p>Real service URL: </p></th>
                    <td>
                        <p><input type="text" name="realServiceUrl" maxlength="100" size="90%" value="<c:out value="${mockservice.realServiceUrl}"/>" /></p>
                        <p class="tiny">You'll need this URL if you want Mockey to serve as a proxy to record transactions between your application and the real service.</p>
                    </td>
                </tr>
	          
	            <tr>
	                <th><p>HTTP header definition:</p></th>
	                <td>
	                    <p>
	                      <select name="httpContentType">
	                        <option value="" <c:if test="${mockservice.httpContentType eq ''}">selected="selected"</c:if>>[select]</option>
                            <option value="text/xml;" <c:if test="${mockservice.httpContentType eq 'text/xml;'}">selected="selected"</c:if>>text/xml;</option>
                            <option value="text/plain;" <c:if test="${mockservice.httpContentType eq 'text/plain;'}">selected="selected"</c:if>>text/plain;</option>
                            <option value="text/css;" <c:if test="${mockservice.httpContentType eq 'text/css;'}">selected="selected"</c:if>>text/css;</option>
                            <option value="application/json;" <c:if test="${mockservice.httpContentType eq 'application/json;'}">selected="selected"</c:if>>application/json;</option>
                            <option value="text/html;charset=utf-8" <c:if test="${mockservice.httpContentType eq 'text/html;charset=utf-8'}">selected="selected"</c:if>>text/html;charset=utf-8</option>
                            <option value="text/html; charset=ISO-8859-1" <c:if test="${mockservice.httpContentType eq 'text/html; charset=ISO-8859-1'}">selected="selected"</c:if>>text/html; charset=ISO-8859-1</option>
                            <!-- <option value="other" <c:if test="${mockservice.httpContentType eq 'other'}">selected="selected"</c:if>>other</option>  -->
                          </select>
	                    </p>
	                    <p class="tiny">For example: <span style="font-style: italic;">text/xml; utf-8</span>, <span
                                style="font-style: italic;">application/json;</span>, etc. </p>
	                </td>
	            </tr>
            </tbody>
        </table>
        <p align="right">
	        <c:choose>
	            <c:when test="${!empty mockservice.id}">
	                <input type="submit" name="update" value="Update" />
	            </c:when>
	            <c:otherwise>
	                <input type="submit" name="create" value="Create" />
	            </c:otherwise>
	        </c:choose>
	        <c:if test="${!empty mockservice.id}">
	            <input type="submit" name="delete" value="Delete" onclick="return confirm('Deleting this service will delete all scenarios associated with it. \nAre you sure you want to delete this service?');" />
	        </c:if>
	        <a href="<c:url value="/"/>">Cancel</a>
	    </p>
    </form>
    <h3>Service Scenarios</h3>
	<!--  xxx -->
	<div class="demo">
		<div id="accordion">
			
			<c:forEach var="mockscenario" begin="0" items="${mockservice.scenarios}" varStatus="status">   
				<h3><a href="#">${mockscenario.scenarioName}</a></h3>
				<div>
				  <p>
				  <%@ include file="/inc_service_scenario_setup.jsp" %>
				 
				  </p>
				</div>
			</c:forEach>
			<h3 style="color:red; text-decoration:none;"><a href="#" style="color:red;text-size:2em;">CREATE A SCENARIO</a></h3>
			<div>
				<p><%@ include file="/service_scenario_setup.jsp" %></p>
			</div>
		</div>
   </div><!-- End demo -->

</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />