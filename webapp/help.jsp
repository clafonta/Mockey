<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Help" scope="request" />
<c:set var="currentTab" value="help" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
<div id="main">
	<h2>The Big Picture</h2>
	<p><strong>Mockey</strong> is a tool for testing application interactions over http.</p>
	<p><img src="<c:url value="/images/bigpicture.png" />" /></p>
	
	<h2>Mock Service</h2>
	<p>
	 A mock service can be a proxy to a real service, enabling you to inspect request and response messages
	 being exchanged. You can set up Mockey to go through a corporate proxy server (see <a href="<c:url value="/proxy/settings" />">Proxy Settings</a>). 
	 There's support for HTTP and HTTPS interactions (if your proxy server or endpoint service is https). Be sure to check out your service's History
	 to inspect and save a past conversation as a Service Scenario.                       
    </p>
    
    <h2>Get Started - Record Stuff</h2>
    <p>
    You can use Mockey to record things by doing the following:
	    <div class="code">
	    [mockey_ip]:[port]/Mockey/service/[whatever_service_you_want_to_record]
	    </div>
    Let's say you just started Mockey and haven't defined any services. Try this: 
	    <div class="code">
	    http://localhost:8080/Mockey/service/http://www.google.com
	    </div>
	Mockey will proxy to the desired URL/service, record it, and create a new service for you. Be sure to check the
	history link and save scenarios.
	
	
    </p>
   <h2>Flush</h2>
    <p>
    This blows everything away, start with a clean slate. 
    </p>
    <h2>Service Plan</h2>
     <p>
                      A Service Plan is a saved state of all Mock Service configurations. For example, all 
                      happy scenarios per service can be saved as a 'Happy Path' plan and all non-happy scenarios 
                      as an 'Unhappy Path' plan. </p>
   	<h2>Export/Upload - huh?</h2>
    <p>There isn't a database for this web tool, everything is kept in memory. 
    After you get everything set up, <strong>export</strong> your configuration
    to a <strong>mockservice.xml</strong> file. Conversely, upon the next web 
    app restart, <strong>upload</strong> your <strong>mockservice.xml</strong> configuration. 
     </p>
    
    <h2>Service Error Response</h2>
     <p>A service scenario flagged as a 'Service Error Response' will be returned by Mockey if an error occurs 
    when calling the service. An error can be caused by a timeout, from calling a real service, or inability 
    to parse data. </p>
    <h2>Universal Error Response</h2>
    <p>A service scenario flagged as a 'Universal Error Response' will be returned by Mockey if an error occurs 
    when calling a service <i>and</i> a scenario is not defined as a Service Error Response. The purpose of this 
    feature is to provide one place to define a universal error message, without the need to create an error
    scenario for each service.  </p>
    
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