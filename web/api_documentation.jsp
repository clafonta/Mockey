<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey-tag" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="Service Documentation" scope="request" />
<c:set var="currentTab" value="api" scope="request" />

<jsp:include page="/WEB-INF/common/header.jsp" />

<div id="main">
    <h1>Configuration API</h1>
    
    <div>
            <p>This is a list of HTTP request definitions that allows for alternative methods to tweak Mockey's settings.</p>
            <strong>For example:</strong>
            <p>
             Before you run your automated test scripts, you want to pre-set Mockey with a specific 
             <i>Service Plan</i>. Get your build scripts (e.g. Ant, Rake) to call Mockey over HTTP and
             set the appropriate settings. 
            </p>
    </div>
    <c:forEach var="apiservice" items="${apiServices}"  varStatus="status">
    <div class="parentform">
    <h2>${apiservice.name}</h2>
    
    <div>${apiservice.description}</div>
        <h3>Request</h3>
        <p class="code">${apiservice.servicePath}</p>
        <h4>Parameters</h4>
        <div class="api_message">
        <table class="api">
            <tr><th>Field</th><th>Description of possible value(s)</th></tr>
            <c:forEach var="attribute" items="${apiservice.apiRequest.attributes}"  varStatus="status">
                  <tr>
                    <td valign="top">${attribute.fieldName}</td>
                    <td valign="top">
                    <ul>
                        <c:forEach var="fieldValue" items="${attribute.fieldValues}"  varStatus="status">
		                   <li><b>${fieldValue.value}</b> : ${fieldValue.description}</li>
		                </c:forEach>
		            </ul>
		            </td>
                  </tr>
            </c:forEach>
        </table>
        </div>
        <h3>Response</h3>
        <div class="api_message">
	        <table class="api">
	            <tr><th>Field</th><th>Description</th></tr>
	            <c:forEach var="attribute" items="${apiservice.apiResponse.attributes}"  varStatus="status">
	                  <tr>
	                    <td valign="top">${attribute.fieldName}</td>
	                    <td valign="top">${attribute.fieldDescription}</td>
	                  </tr>
	            </c:forEach>
	        </table>
        </div>
        <h4>Example Response</h4>
        <div class="code">${apiservice.apiResponse.example}</div> 
        
        <h3>Available API Calls</h3>
        <mockey-tag:availableConfigurationLinks servicePath="${apiservice.servicePath}" serviceName="${apiservice.name}"/>        
     </div>
    </c:forEach> 
    
    
</div>	
<jsp:include page="/WEB-INF/common/footer.jsp" />