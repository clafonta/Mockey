<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Mockey - Mobi</title>
    <meta http-equiv="content-type" content="text/html; charset=iso-8859-1">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/mobi.css" />" />
  </head>
  <body>
    <div id="container">
      <h1>Mockey</h1>	
      &nbsp;&nbsp;
      <%@ include file="/WEB-INF/common/message.jsp" %>
      <c:choose>
        <c:when test="${!empty services}">
          <form action="<c:url value="/mobi"/>" method="get">
            <input type="hidden" name="filter" value="yes"/>  
            <select name="serviceId" >
              <c:forEach var="mockservice" items="${allservices}">
                <option value="<c:out value="${mockservice.id}" />"><c:out value="${mockservice.serviceName}" /></option>	 
		      </c:forEach>
		     </select>
		     <input type="submit" value="Go" /> <a href="<c:url value="/mobi"/>">Show All</a>
		   </form>
          <c:forEach var="mockservice" items="${services}">
           <div>
             <form action="<c:url value="/mobi"/>" method="post">
               <input type="hidden" name="filter" value="${filter}" />   
               <input type="hidden" name="serviceId" value="${mockservice.id}" />               
               <h2>Service name: <c:out value="${mockservice.serviceName}" /></h2>               
               Mock URL: <mockey:url value="${mockservice.url}" />
               <br />
               <p>
			     <input type="radio" name="serviceResponseType" value="0" <c:if test='${mockservice.serviceResponseType eq 0}'>checked</c:if> />
			     <b>Proxy</b> to this URL: <span class="highlight"><c:out value="${mockservice.realServiceUrl}" /></span>
			   </p>
			   <p>
			     <input type="radio" name="serviceResponseType" value="2" <c:if test='${mockservice.serviceResponseType eq 2}'>checked</c:if> />
			     <b>Dynamic Scenario</b>
			   </p>
               <p>
                 <input type="radio" name="serviceResponseType" value="1" <c:if test='${mockservice.serviceResponseType eq 1}'>checked</c:if> />
                 <b>Static Scenario</b>                
               </p>
               <p>
                 <ul>
                   <li>Select a Static Scenario.</li>
                   <c:choose>
                      <c:when test="${not empty mockservice.scenarios}">
                        <c:forEach var="scenario" begin="0" items="${mockservice.scenarios}">
                          <li>
                            <input type="radio" name="scenario" value="<c:out value="${scenario.id}"/>"
                              <c:if test='${mockservice.defaultScenarioId eq scenario.id}'>checked</c:if> />
                            <c:url value="/scenario" var="scenarioEditUrl">
                              <c:param name="serviceId" value="${mockservice.id}" />
                              <c:param name="scenarioId" value="${scenario.id}" />
                            </c:url>
                            <a href="<c:out value="${scenarioEditUrl}"/>" title="Edit service scenario"  ><c:out value="${scenario.scenarioName}" /></a>
                          </li>
                        </c:forEach>
                      </c:when>
                      <c:otherwise>
                        <c:url value="/scenario" var="scenarioUrl">
                          <c:param name="serviceId" value="${mockservice.id}" />
                        </c:url>
                      	<li>You need to <a href="<c:out value="${scenarioUrl}"/>" title="Create service scenario" border="0" />create</a>
                         a scenario before using "Static or Dynamic Scenario".</li>
                      </c:otherwise>
                    </c:choose>
                   </ul>
                </p>
                <p>
                  <input type="text" name="hangTime" maxlength="20" size="20" value="<c:out value="${mockservice.hangTime}"/>" /> Hang time (milliseconds)
                  &nbsp;&nbsp;
                  Content Type             
                </p>
                <input type="submit" value="Update" />
	            </form>
              </div>
              &nbsp;&nbsp;
            </c:forEach>          
        </c:when>
        <c:otherwise>
          <p class="alert_message">There are no mock services defined. </p>
        </c:otherwise>
      </c:choose>
    </div>    
  </body>
</html>