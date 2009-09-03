<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="actionKey" value="edit_service" scope="request" />
<c:set var="pageTitle" value="Configure" scope="request" />
<c:set var="currentTab" value="create" scope="request" />
<%--@elvariable id="mockservice" type="com.mockey.MockServiceBean"--%>
<jsp:include page="/WEB-INF/common/header.jsp" />
<div id="main">
    <%@ include file="/WEB-INF/common/message.jsp"%>    
    <c:choose>
        <c:when test="${!empty mockservice.id}">
            <h1>Service Setup: <span class="highlight"><c:out value="${mockservice.serviceName}"/></span></h1>
        </c:when>        
        <c:otherwise>
            <h1>Service Setup</h1>            
        </c:otherwise>
    </c:choose>
    <%@ include file="/WEB-INF/common/inc_action_links.jsp"%>
    <form action="<c:url value="/setup"/>" method="POST">
        <input type="hidden" name="serviceId" value="<c:out value="${mockservice.id}"/>" /> 
        <table class="simple" width="100%">
            <tbody>
	            <tr><th><p>Service name:</p></th>
	                <td>
	                    <p><input type="text" name="serviceName" maxlength="100" size="90%" value="<c:out value="${mockservice.serviceName}"/>" /></p>
	                    <p>Use a self descriptive name. For example, if you were to use this for 'authentication' testing, then call it 'Authentication'.</p>
	                </td>
	            </tr>
                <tr>
                    <th><p>Real service URL: </p></th>
                    <td>
                        <p><input type="text" name="realServiceUrl" maxlength="100" size="90%" value="<c:out value="${mockservice.realServiceUrl}"/>" /></p>
                        <p>You'll need this URL if you want Mockey to serve as a proxy to record transactions between your application and the real service.</p>
                    </td>
                </tr>                
	            <tr><th><p>Service description:</p></th>
                    <td>
                        <p><textarea name="description" style="width:90%;" rows="2" ><c:out value="${mockservice.description}" /></textarea></p>
                        <p>Give a short description of this service (max 1000 chars).</p>
                    </td>
                </tr>
	            <tr>
	                <th><p>Mock service URI:</p></th>
	                <td>
	                    <p><c:out value="${mockservice.mockServiceUrl}"/> </p>	                    
	                </td>
	            </tr>
	            <tr>
	                <th><p>HTTP header definition:</p></th>
	                <td>
	                    <p>
	                      <select name="httpHeaderDefinition">
	                        <option value="" <c:if test="${mockservice.httpHeaderDefinition eq ''}">selected="selected"</c:if>>[select]</option>
                            <option value="text/xml;" <c:if test="${mockservice.httpHeaderDefinition eq 'text/xml;'}">selected="selected"</c:if>>text/xml;</option>
                            <option value="text/plain;" <c:if test="${mockservice.httpHeaderDefinition eq 'text/plain;'}">selected="selected"</c:if>>text/plain;</option>
                            <option value="text/css;" <c:if test="${mockservice.httpHeaderDefinition eq 'text/css;'}">selected="selected"</c:if>>text/css;</option>
                            <option value="application/json;" <c:if test="${mockservice.httpHeaderDefinition eq 'application/json;'}">selected="selected"</c:if>>application/json;</option>
                            <option value="text/html;charset=utf-8" <c:if test="${mockservice.httpHeaderDefinition eq 'text/html;charset=utf-8'}">selected="selected"</c:if>>text/html;charset=utf-8</option>
                            <option value="text/html; charset=ISO-8859-1" <c:if test="${mockservice.httpHeaderDefinition eq 'text/html; charset=ISO-8859-1'}">selected="selected"</c:if>>text/html; charset=ISO-8859-1</option>
                            <!-- <option value="other" <c:if test="${mockservice.httpHeaderDefinition eq 'other'}">selected="selected"</c:if>>other</option>  -->
                          </select>	                    
	                    <!--  <input type="text" size="60px" name="httpHeaderDefinition_other" value="<c:out value="${mockservice.httpHeaderDefinition}"/>" /></p>  -->
	                    <p>For example: <span style="font-style: italic;">text/xml; utf-8</span>, <span
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
    <p>
	    <c:if test="${!empty mockservice.scenarios and !empty mockservice.id}">
	        <%@ include file="/WEB-INF/common/inc_scenario_list.jsp"%>
	    </c:if>
    </p>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />