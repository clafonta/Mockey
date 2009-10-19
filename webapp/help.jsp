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
    <a href="#static_dynamic" name="static_dynamic"> </a>
    <h2>Mock Service Scenario</h2>
	<p>
	 A mock service can have 0 or more <i>mock service scenario</i> definitions associated with it. 
	 This is handy if you don't want to proxy to a real service and want to explicitly define 
	 your mock service's response.      
	 </p>
	 <p>
	 <i style="color:red;font-weight:bold;">Match Argument</i>? <i style="color:green;font-weight:bold;">Static</i> or <i style="color:orange;font-weight:bold;">Dynamic</i>? Setting your mock service to 
	 <i>static</i> means your mock service will always return the same mock service scenario, no matter what the request is. Dynamic means, Mockey
	  inspects the incoming request message for a specific <b>match argument</b>. If the incoming request message contains this string argument, then this service 
	  scenario's response message will be the response. If more than one scenario is defined with this matching argument, then the first matched 
	  scenario's response message will be returned. If no scenario is found with a matching argument, a plain text error message will display.
    </p>
    <p><img src="<c:url value="/images/dynamic_response.png" />" /></p>
    <p>
    You only need to define a match argument per service scenario for dynamic scenarios.
    </p>
    <a href="#record" name="record"> </a>
    <h2>Get Started - Record Stuff</h2>
    <p>
	    Mockey can proxy to a desired URL/service, record it, and create a new
	service definition for you. Be sure to check the service defintion's
	history link to inspect the request and response messages.
	</p>
	<div class="hint_message">
	
	<h3>How to Record</h3>
		<p>
		First, place Mockey's URL in front of the service URL you want to
		record, something like this:
		<div class="code">[mockey_ip]:[port]<b>[/Context Root]/service/</b>[whatever_service_you_want_to_record]</div>
		</p>
		<p>
		Second, go back to Mockey's menu and click 'All Services'. You should
		see some auto generated service definitions. 
		</p>
		<p>Third, click on the auto generated 'history' link; you should see your IP address. Click on 
		it to see the request and response transaction(s) you just made.
		
		<h4>Examples</h4>
		<div >
		<ul id="simple">
			<li>1) <a href="<c:out value="${hintRecordUrl1}"/>"><c:out
				value="${hintRecordUrl1}" /></a></li>
			<li>2) <a href="<c:out value="${hintRecordUrl2}"/>"><c:out
				value="${hintRecordUrl2}" /></a></li>
		</ul>
		</div>
		<p>
		After clicking on one of these example links above, click <a href="<c:url value="home"/>">here</a> to see what just happened.  
		</p>
		
	</div>




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
     <p><b>You define this.</b> It is a service scenario flagged as a 'Service Error Response', it will be returned by Mockey if an error occurs 
    when calling the service. An error can be caused by a timeout, from calling a real service, or inability 
    to parse data. </p>
    <h2>Universal Error Response</h2>
    <p><b>You define this.</b> A service scenario flagged as a 'Universal Error Response' will be returned by Mockey if an error occurs 
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
    <h2>URL Mapping Config Recommendations</h2>
    <p>
    If your application points to 1 or more services like this:
    
    <ul>     
      <li>http://someservice.com/catalog/product</li>
      <li>http://someotherservice.com/authentication</li>
      <li>http://anotherservice.com/?wsdl</li>      
    </ul>
    
    ...you probably have this in a configuration file (not in code, right?). You may want to define a <i>base</i> url parameter, 
    like this:
    <div class="code">
    <ul>
      <li>DEV_BASE_URL=http://localhost:8090/Mockey/service/</li>      
      <li>SERVICE_URL_1=http://someservice.com/catalog/product</li>
      <li>SERVICE_URL_2=http://someotherservice.com/authentication</li>
      <li>SERVICE_URL_3=http://anotherservice.com/?wsdl</li>      
    </ul>    
    </div>
    You then can create URLs by pre-pending the DEV_BASE_URL to your SERVICE_URL definitions. When it comes time to go to production, 
    just define DEV_BASE_URL as an empty string. 
    
     
    
    </p>
    <h2>Related Read</h2>
    <p>
    <a href="http://martinfowler.com/bliki/TestDouble.html">http://martinfowler.com/bliki/TestDouble.html</a>
    </p>
</div>	
<jsp:include page="/WEB-INF/common/footer.jsp" />