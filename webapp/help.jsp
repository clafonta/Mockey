<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:set var="pageTitle" value="Help" scope="request" />
<c:set var="currentTab" value="help" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
<div id="main">
	<h2>The Big Picture</h2>
    <p><img src="<c:url value="/images/bigpicture.png" />" /></p>
   	<h2>Export/Upload - huh?</h2>
    <p>There isn't a database for this web tool, everything is kept in memory. 
    After you get everything set up, <strong>export</strong> your configuration
    to a <strong>mockservice.xml</strong> file. Conversely, upon the next web 
    app restart, <strong>upload</strong> your <strong>mockservice.xml</strong> configuration. 
     </p>
    <h2>Good Things to Test</h2>
    <p>Here's a short list of things Mockey is good for. 
    <ul>
    <li><i>Connection smarts:</i> Try setting the hang time for 2 minutes, then see if your application's timeout connection setting works. Remember, 
    sometimes the service your application interacts with is slow and may receive a connection but not let go.</li>
    <li><i>Garbage handling:</i> Be sure to create bad responses (e.g. Mockey responds with the word 'GARBAGE') and see if your application handles this gracefully. </li>
      
    
    </ul> 
     
    </p>
</div>	
<jsp:include page="/WEB-INF/common/footer.jsp" />