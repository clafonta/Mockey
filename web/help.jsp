<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Help" scope="request" />
<c:set var="currentTab" value="help" scope="request" />

<jsp:include page="/WEB-INF/common/header.jsp" />

<div id="main2">
    <div id="helpTop" style="position:relative;">
	    <div class="table_of_contents" >  
		    <h3 style="float:right;padding-right:1em;">Table of Contents</h3>
		    <div style="clear:both;"></div>
		    <ul>
		        <li>&#187; <a href="#bigpicture">Big Picture</a></li>
		        <li>&#187; <a href="#storage">Storage</a></li> 
		        <li>&#187; <a href="#mockservice">Mock Service</a></li>
		        <li>&#187; <a href="#plan">Service Plan</a></li>
		        <li>&#187; <a href="#scenario">Mock Service Scenario</a></li>
		        <li>&#187; <a href="#twisting">Twisting</a></li>
		        <li>&#187; <a href="#record">Get Started - Record Stuff</a></li>
		        <li>&#187; <a href="#flush_feature">Flush</a></li>
		        <li>&#187; <a href="#export_upload">Export/Upload</a></li>
		        <li>&#187; <a href="#url_injection">URL Injection</a></li>
		        <li>&#187; <a href="#merge_services">Merge Services</a></li>
		        <li>&#187; <a href="#initialization">Initialization</a></li>
		        <li>&#187; <a href="#good_things_to_test">Good Things to Test</a></li>
		        <li>&#187; <a href="#url_recommendations">URL Config Recommendations</a></li>	        
		        <li>&#187; <a href="#robots">Robots</a></li>  
		        <li>&#187; <a href="#service_eval_rules">Service Request Validation Rules</a></li>
		        <li>&#187; <a href="#getting_the_right_scenario">Getting the Right Scenario</a></li>
		        <li>&#187; <a href="#response_schema">Response JSON Schema</a></li> 
		        <li>&#187; <a href="#status">Status</a></li> 
		        <li>&#187; <a href="#startup">Start Up Configurations</a></li> 
		    </ul>
	    </div>
	    <div style="">
		<a href="#bigpicture" name="bigpicture"></a> 
		<h2>The Big Picture</h2>
		   <strong>Mockey</strong> is a tool for testing application interactions over http.</p>
		   <p><img src="<c:url value="/images/bigpicture.png" />" /></p>
		</div>
	</div>
	<a href="#storage" name="storage"></a>
	<div class="help_section">
        <h2>Storage</h2>
        <p>
        Mockey can run as <strong>In Memory Only</strong> or <strong>Writing to File</strong>. If in-memory only, all definitions and settings will be lost upon restart of Mockey. If writing-to-file, then all changes will persist upon a restart. 
    	</p>
    	<p>
         Storage state is managed by Mockey's 'transient' configuration flag. If transient is 'true' (<a href="<c:url value="/configuration/info"/>">transient_state=true</a>), then configuration changes are in-memory <b>only</b> and not persisted to the file system. This is good for people 
        or robots who want to play with Mockey settings and not infect any source files that were used to initialized 
        Mockey. If the transient flag is 'false', (<a href="<c:url value="/configuration/info"/>">transient_state=false</a>), 
        then all configuration definitions and changes will be written to the file system.  <b>Note:</b> right after the transient 
        setting is turned off (<i>set to false</i>), everything in-memory is written to the file system. 
        </p>
        <p class="alert_message" style="position:relative;">
          <img style="float:right;" height="30px" src="<c:url value="/images/skull_and_crossbones.png"/>" />
          <b>Warning:</b> misunderstanding and misuse of this storage configuration (aka <b>transient</b> flag) can result in a huge painful thing. If you're creating services and making changes with the transient setting set to true, then all your work will be lost once you restart Mockey.   
        </p>
        <p>
        Why is <i>transient</i> needed, and why is it a good thing? Creation of data within Mockey is a good thing, and it's especially
        good if you save this data to a source code repository like CVS, Peforce, Git, SVN, etc. But it may not be a great thing to have
        your source code repo flag Mockey files as dirty when all you did was toggle things on or off or changed a desired 
        scenario setting. If you are happy with your new data and configuration changes, and you want your source code repot tool to 
        flag/find your changes, then be sure the transient setting 
        is OFF (set to false). 
        </p>
    </div>
    <a href="#mockservice" name="mockservice"></a>
	<div class="help_section_zebra">
		<h2>Mock Service</h2>
		<p>
		 A mock service can be a proxy to a real service, enabling you to inspect request and response messages
		 being exchanged. You can set up Mockey to go through a corporate proxy server (see <a href="<c:url value="/proxy/settings" />">Proxy Settings</a>). 
		 There's support for HTTP and HTTPS interactions (if your proxy server or endpoint service is https). Be sure to check out your service's History
		 to inspect and save a past conversation as a Service Scenario.                       
	    </p>
    </div>
    <a href="#plan" name="plan"></a> 
    <div class="help_section">
	    <h2>Service Plan</h2>
	    <p>
	    A service plan is a snap shot of your service and scenario settings. For example, you toggle your service
	    and scenario settings to represent a Gold Member. Then you toggle your service and scenario settings to 
	    represent a Silver Member. Instead of spending your time toggling back an forth, you would save your settings
	    for the Gold Member as a "Gold Member" plan and Silver Member settings as "Silver Member" plan. 
	    <div class="info_message sidebarme"><strong>Note:</strong> Service Plans are not the most reliable things. If you 
	    frequently add and remove scenarios or create and delete services, the service plans get stale and start to
	    smell bad. </div>
	    </p>
	</div>
	<a href="#scenario" name="scenario"></a>
    <div class="help_section_zebra">
		<h2>Mock Service Scenario</h2>
		<p>
	    A mock service can have 0 or more <i>mock service scenario</i> definitions associated with it. 
	    This is handy if you don't want to proxy to a real service and want to explicitly define 
		your mock service's response.      
	    </p>
	    <p>
	    <h3>Match Argument</h3>
	     Setting your mock service to 
		 <strong>Static</strong> means your mock service will always return the same mock service scenario, no matter what the request is. <strong>Dynamic</strong> means, Mockey
		  inspects the incoming request message for a specific <b>match argument</b>. If the incoming request message contains this match argument 
		  (either <i>string</i> or satisfies the <i>rules</i> for <a href="#getting_the_right_scenario">Getting the Right Scenario</a>), 
		  then this service 
		  scenario's response message will be the response. If more than one scenario is defined with this matching argument, then the first matched 
		  scenario's response message will be returned. If no scenario is found with a matching argument, a plain text error message will display.
		  You only need to define a match argument per service scenario for <u>dynamic</u> scenarios, and does not apply to static or proxy.
	    </p>	    
	    <p style="text-align:center;"><img src="<c:url value="/images/dynamic_response.png" />" /></p>
	    <a href="#beware_of_match" name="beware_of_match"></a>
	    <p class="alert_message" style="position:relative;">
	      <img style="float:right;" height="30px" src="<c:url value="/images/skull_and_crossbones.png"/>" />
          <b>Beware:</b> Let's say you have <i>Scenario A</i> with match argument '123' and <i>Scenario B</i> with match argument 'ABC', and 
          an incoming request with value 'ABC123'. Which scenario will be returned, A or B? There's no guarantee on what Mockey will respond 
          with. Let's say in addition, you have a <i>Scenario C</i> with match argument 'ABC123' and the incoming request includes the argument
          'ABC123'. Which scenario will be returned, A, B, or C, since all match-arguments are in the request? In this case, <i>Scenario C</i> 
          will always be returned because it has the greatest length match argument, 6 characters versus 3 characters. 
          <br /><br />
          If you are looking for an exact value, then use rules (see <a href="#getting_the_right_scenario">Getting the Right Scenario</a>) with type 'regex_required' and do not depend
          on a simple text search. 
        </p>
	    <p>
	    <h3>Tags</h3>
	    You can add 0 or more tags to a Scenario. Why is this good? Tags can be used for many things, some include but not limited to:
	    <ul>
	    <li>Tag scenarios to let people know what they are designed for, e.g.Android or iOS, mobile or non-mobile, July's Release, etc.</li>
	    <li>Filter your view, e.g. "<i>I only want to see Scenarios and Services tagged for testing the August release.</i>"</li>
	    </ul>
	    </p>
	    <p>
	    <h3>HTTP Response Status</h3>
	    By default, all things are set to HTTP 200 (OK) but feel free to change this 
	    if you need to test how your application handles 500, 404, 303s, etc. 
	    </p>
	    <a href="#error_handling" name="error_handling"></a>
        <h2>Error Handling</h2>
        When you create a <strong>Scenario</strong>, you'll see two checkboxes. Here's what they do:
        <h4>&#187; Service Scenario Error Response</h4>
        <p>
        If a service scenario is flagged as a 'Service Error Response', it will be returned by Mockey if an error occurs 
        when calling the service. An error can be caused by a timeout from calling a real service or inability 
        to parse data. 
        </p>
        
        <h4>&#187; Universal Error Response</h4>
        <p>
        If a service scenario is flagged as a 'Universal Error Response', it will be returned by Mockey if an error occurs 
        when calling a service <i>and</i> a scenario is not defined as a <i>Service Scenario Error Response</i>. The purpose of this 
        feature is to provide one place to define a universal error message, without the need to create an error
        scenario for each service. <b>Note:</b> Only one scenario out of all services can be flagged as universal. 
        </p>
        <p class="alert_message" style="position:relative;">
          <img style="float:right;" height="30px" src="<c:url value="/images/skull_and_crossbones.png"/>" />
          <b>Warning:</b> use with caution. If you flag a valid looking Scenario as your universal error or 
          service error, you'll see valid data, unknowing of the mysteries and real errors 
        that are working against you. This can lead to trouble. 
        </p>
	    
    </div>
    <a href="#twisting" name="twisting"></a>
    <div class="help_section">
	    <h2>Twisting</h2>
	    <p>
	    Twisting refers to taking incoming requests from URL/Domain X and mapping them to URL/Domain Y. 
	    <ul>
	      <li>Twisting is used for services set to <strong>Proxy</strong>, <strong>Static</strong> or <strong>Dynamic</strong></li>
	    </ul>
	    <strong>When would I want to use Twisting?</strong> When your client application doesn't
	        easily allow you to point to different environments or when some requests should be answered by the real
	        service but other requests need to be answered by your sandbox. 
	        <div class="info_message">
	            <h4>How Twisting Works</h4>
	            <p>
	            Let's say Mockey receives an incoming request: 
	            <div class="code">http://127.0.0.1:8080/service/http://<b>uat1</b>.mystartup.com/catalog/list</div>
	            </p>
	            <p>
	            And let's say Twisting is on with the following twist configuration:
	            <div class="code"><strong>"Make sure everything hits the QA Environment, not UAT"</strong>
		            <ul style="list-style: none;">
			            <li>Here are the find and replace patterns:</li>
			            <li>  
							<table class="api">
				            <tr><th>Find...</th><th>Replace with...</th></tr>
				            <tr><td>uat1.mystartup.com</td><td>qa1.mystartup.com</td></tr>
				            <tr><td>uat2.mystartup.com</td><td>qa1.mystartup.com</td></tr>
				            <tr><td>qa3.mystartup.com</td><td>qa1.mystartup.com</td></tr>
				            </table>
				       </li>
		            </ul>
	            </div>
	            <p>
	            Based on the Twisting configuration above, Mockey will take the incoming request and <i>twist</i> the 
	            request to the following <b>new</b> URL:
	            <div class="code">http://127.0.0.1:8080/service/http://<b>qa1</b>.mystartup.com/catalog/list</div>
	            ..and then proxy that request. 
	            </p>
	            
	        </div>
	    </p>
	</div>
    <a href="#record" name="record"></a> 
	<div class="help_section_zebra">
	    <h2>Get Started - Record Stuff</h2>
	    <p>
	    Mockey can proxy to a desired URL/service, record it, and create a new
		service definition for you. Be sure to check the service defintion's
		history link to inspect the request and response messages.
			<div class="info_message">
			    <h4>How to Record</h4>
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
		</p>
	</div>
	<a href="#flush_feature" name="flush_feature"></a>
	<div class="help_section" style="position:relative;">
	    <h2>Flush</h2>
	    <div style="position:absolute; top: 5px; right: 5px;"><img src="<c:url value="/images/flush.png"/>"></div>
	    <p>
	    Clicking on the <strong>Flush</strong> menu button will clear out everything, allowing you to start with a clean slate. 
	    </p>
	</div>
	<a href="#export_upload" name="export_upload"></a>
	<div class="help_section_zebra">
	    <h2>Export/Upload - huh?</h2>
	    <p>
	    There isn't a database for this web tool, everything is kept in memory and written to a local file. 
	    After you get everything set up, <strong>export</strong> your configuration
	    to a <strong>mockservice.xml</strong> file; you can share this file with others if you like.  
	     </p>
	</div>
	<a href="#url_injection" name="url_injection"></a>
	<div class="help_section">
        <h2>URL Injection</h2>
        <p>
        <strong>When would URL injection be important?</strong> Mockey tries to be smart about things. With an incoming HTTP URL request, 
        Mockey will try to see if any Services are defined (or associated) to the incoming HTTP URL request, whether its the <strong>Mock URL</strong>
        or one of the <strong>Real URL(s)</strong>. If no Service definition is associated with the incoming HTTP URL, then Mockey will do its best
        to proxy the requests.
        <br /><br />
        <strong style="color:red;">TODO: Need logic flow diagram here.</strong>
        <br /><br />
        If you defined many Services and Scenarios to work with a specific environment (e.g. <i>https://<b>qa-3</b>.environment.domains.com/cataglog/product</i>), 
        then you're kind of hosed if all requests start coming from another URL (e.g. <i>https://<b>sandbox</b>.environment.domains.com/cataglog/product</i>).
        
        <br /><br />
        URL Injection allows you to quickly tell Mockey how to associate Service definitions associated with one environment
        (e.g. qa3) to another environment (e.g. sandbox).  
        <br />
        <div class="info_message">
       <h3>Example</h3>
       Before injection:
       <p><strong>Service XYZ</strong> has the following real URLs:
      
                <ul>
                  <li>http://qa1.domain.com/authentication</li>
                  <li>http://qa2.domain.com/authentication</li>
                </ul>
                </p>
                After injecting with match pattern <i>qa1.domain.com</i> and 
                replace pattern <i>qa8.domain.com</i>, we get:
                <p>
                <strong>Service XYZ</strong> has the following real URLs:
      
                <ul>
                  <li>http://qa1.domain.com/authentication</li>
                  <li>http://qa2.domain.com/authentication</li>
                  <li>http://qa8.domain.com/authentication</li>
                </ul>
                </p>
        </div>
      
         
        </p>
    </div>
    <a href="#merge_services" name="merge_services"></a>
    <div class="help_section_zebra">
        <h2>Merge Services</h2>
        <p>
         <strong>When would I want to Merge Services?</strong> Whenever you see duplication of Service and/or Scenario definitions. 
         This can happen if you have Scenarios tied to duplicate Service definition (e.g. Service X: <i>http://mystartup.com/authentication</i> and 
         Service Y: <i>http://mystartup.com/login</i> - both referring to the same thing). 
         <br /><br />
         Merge them! 
         <br /><br />Merging will create a new Service definition
         associated to multiple real URLs (e.g. Service X: <i>http://mystartup.com/authentication</i> and 
         <i>http://mystartup.com/login</i> ) and combine all unique Scenario definitions into the new Service.
         
         </p>
    </div>
    <a href="#initialization" name="initialization"></a>
    <div class="help_section">
      <h2>Initialization</h2>
      <p>
      There are few ways to initialize Mockey. Here they are:
      <ul>
        <li><b>File upload:</b> use the <a href="<c:url value="/upload" />">Import</a> feature.</li>
        <li><b>File location:</b> file is located on the same server where Mockey is running.</li>
        </ul>
        If you know the path to the file and Mockey has access to it, then you can tell Mockey
        to initialize itself, either pre or post start-up. Here's how:
      
       <div class="info_message">
       <h3>At Startup</h3>
       Let's say this is what you have:
       <div class="code code_text">
       &gt; ls <br />
       &gt; Mockey.jar some_file.xml <b>mock_service_definitions.xml</b><br />
       &gt; java -jar Mockey.jar<br />
       </div>
       By default, Mockey will initialize itself with <b>mock_service_definitions.xml</b>. If the 
       file isn't there, it will create a new one and write it out. 

       <!-- TODO: Bring this back.
       Alternatively, you could pass
       it an argument:
       <div class="code code_text">
       &gt; ls <br />
       &gt; Mockey.jar <b>some_file.xml</b> mock_service_definitions.xml<br />
       &gt; java -jar Mockey.jar -f some_file.xml<br />
       </div>
       Now, Mockey will initialize itself with <b>some_file.xml</b> upon startup <b>but</b> it will
       continue to write itself out to <b>mock_service_definitions.xml</b> after initialization. 
		-->

       For more options,
       try the <span class="code_text">--help</span> argument.
       <div class="code code_text">
       &gt; java -jar Mockey.jar --help
       </div>
       <h3>Post Startup</h3>
       Pass the <span class="code_text">init</span> and <span class="code_text">file</span> arguments to the Home service. <i>File</i> refers
       to a file relative to where the Mockey.jar is located (and Mockey is allowed to read it). 
       <div class="code code_text">
       &gt; http://localhost:8080/Mockey/home&amp;action=init&file=some_file.xml <br /><br />
       or <br /><br />
       &gt; http://localhost:8080/Mockey/home?action=init&file=/Users/someuser/Work/some_file.xml <br /><br />
       or (to get a JSON response back instead of HTML)<br /><br />
       &gt; http://localhost:8080/Mockey/home?action=init<b>&type=json</b>&file=/Users/someuser/Work/some_file.xml
       </div>
       If <span class="code_text">some_file.xml</span> does exist, then Mockey will <a href="#flush_feature">Flush</a>
        it's configurations and initialize itself with <span class="code_text">some_file.xml</span>. For more 
        information, see <a href="<c:url value="/service_api" />">Configuration API</a>.
        </div>
      </p>
    </div>
    <a href="#good_things_to_test" name="good_things_to_test"></a>
    <div class="help_section_zebra">
	    <h2>Good Things to Test</h2>
	    <p>
		    Here's a short list of things Mockey is good for. 
		    <ul>
		    <li><i>Connection smarts:</i> Try setting the hang time for 2 minutes, then see if your application's timeout connection setting works. Remember, 
		    sometimes the service your application interacts with is slow and may receive a connection but not let go.</li>
		    <li><i>Garbage handling:</i> Be sure to create bad responses (e.g. Mockey responds with the word 'GARBAGE') and see if your application handles this gracefully. </li>
		    </ul>      
	    </p>
    </div>
    <a href="#url_recommendations" name="url_recommendations"></a>
    <div class="help_section">
	    <h2>URL Config Recommendations</h2>
		<p>
		    If your application points to 1 or more services like this:
		    <ul>     
		      <li>http://someservice.com/catalog/product</li>
		      <li>http://someotherservice.com/authentication</li>
		      <li>http://anotherservice.com/?wsdl</li>      
		    </ul>
		    ...you probably have this in a configuration file (not in code, right?). You may want to define a <i>base</i> url parameter, 
		    like this:
		    <div class="code code_text">
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
	</div>
	<a href="#robots" name="robots"></a>
	<div class="help_section_zebra">
        <h2>Robots</h2>
        <p>
        Mockey is not only for Humans. Robots can use it too. See <a href="service_api">here</a>.
        </p>
    </div>
    
    <a href="#service_eval_rules" name="service_eval_rules"></a>
    <div class="help_section">
        <h2>Service Request Validation Rules</h2>
        <p class="quote">"Hey, am I sending garbage over the wall, or am I meeting the intended service API?"</p>
		<p>Mockey has some hooks for you to evaluate incoming requests defined in a JSON formatted rules. These rules can be applied to incoming 
			request for the validation. </p>
		
		<p>You can apply evaluation rules at a Service level to help flag potential errors ("<i>Hey, you're missing a required request attribute!</i>") and it works as follows:
			<ul>
				<li>A request is made to Mockey. </li>
				<li>Mockey finds the appropriate mock Service. If request validation is <b>enabled</b>, then Mockey will scan
				the incoming request with your defined rules. </li>
				<li>If evaluation errors/issues are found, then they will be logged and viewable in the <a href="<c:url value="/history"/>">History</a> page.</li>	
			</ul>
		</p>
		<p>Here's an example definition: </pre>
<pre class="code" style="font-size:0.9em;">
	// EXAMPLE
{
    "parameters": [
        {
            "key": "ticker",
            "desc": "A value must be provided with the 'ticker' parameter, and it must contain the letter 'g'. Providing 'GOOG' is valid, but 'FB' will flag an error.",
            "value_rule_arg": "g",
            "value_rule_type": "string_required"
        },
        {
            "key": "date",
            "desc": "Optional date value, but if provided, must satisfy mm/DD/yyyy format.",
            "value_rule_arg": "^((1[0-2]|0?[1-9])/(3[01]|[12][0-9]|0?[1-9])/(?:[0-9]{2})?[0-9]{2})?$",
            "value_rule_type": "regex_optional"
        }
    ],
    "headers": [
        {
            "key": "page_id",
            "desc": "A page_id value MUST be provided, any non-empty string value.",
            "value_rule_arg": "",
            "value_rule_type": "string_required"
        }
    ],
    "body": [
        {
            "desc": "The text 'username' is required to be present in the POST body.",
            "value_rule_arg": "username",
            "value_rule_type": "string_required"
        }
    ],
    "url": [
        {
            "desc": "The value '123' is required to be present in the RESTful URL. For example 'http://127.0.0.1/service/customer/123/invoice'",
            "value_rule_arg": "\\b123\\b",
            "value_rule_type": "regex_required"
        }
    ]
}
</pre>
	</div>
		
    <a href="#getting_the_right_scenario" name="getting_the_right_scenario"></a>
    <div class="help_section_zebra">
		<h2>Getting the Right Scenario (for Dynamic Services)</h2>
		<p class="quote">"I am writing to a RESTful API, where <b>PUT</b> should get scenario 'A' and <b>DELETE</b> should get scenario 'B', but how do I do this?"</p>
		<p>
		When evaluation rules are applied to a Scenario, they are processd only when the Scenario's parent Service is set to <strong>Dynamic</strong>. 
		If the Service is set to Dynamic, and the Scenario has a simple <i>match</i> argument or evaluation <i>rules</i> defined, then the incoming 
		request will be evaluated. 	If the request satisfies the Scenario's matching argument or rules, then that Scenario is returned.     
		<br /><br />For example, if you want your request 
		<span class="code_text"> http://127.0.0.1:8080/service/customer?customer=111&invoice=222</span> to return Scenario B, 
		then here's an example setup: 
		
        <pre class="code" style="font-size:0.9em;">
// EXAMPLE Service A with 'Scenario A' 
{
    "parameters": [
        {
            "key": "customer",
            "value_rule_arg": "33",
            "value_rule_type": "string_required"
        },
        {
            "key": "invoice",
            "value_rule_arg": "44",
            "value_rule_type": "string_required"
        }
    ]
}
</pre>
<pre class="code" style="font-size:0.9em;">
// EXAMPLE Service A with 'Scenario B'
{
    "parameters": [
        {
            "key": "customer",
            "value_rule_arg": "111",
            "value_rule_type": "string_required"
        },
        {
            "key": "invoice",
            "value_rule_arg": "222",
            "value_rule_type": "string_required"
        }
    ]
}
</pre>
<pre class="code" style="font-size:0.9em;">
// EXAMPLE Service A with 'Scenario C'
{
    "url": [
        {
            "desc": "If the value '123' is in the RESTful URL. For example 'http://127.0.0.1/service/customer/123'",
            "value_rule_arg": "\\b123\\b",
            "value_rule_type": "regex_required"
        }
    ]
}
</pre>

<h3>Wild card</h3>
There is limited wildcard support for those who need to evaluate values not necessarily tied to a specific parameter key. 
For example: 

<pre class="code" style="font-size:0.9em;">
// EXAMPLE wild card
{
    "parameters": [{
        "key": "*",
        "value_rule_arg": "^(aaa)$",
        "value_rule_type": "regex_required"
    },{
        "key": "*",
        "value_rule_arg": "^(bbb)$",
        "value_rule_type": "regex_required"
    }]
}
</pre>
This definition will work for either <span class="code_text">http://127.0.0.1:8080/service/customer?ticker=aaa&customer=bbb</span> or
<span class="code_text">http://127.0.0.1:8080/service/customer?invoice=bbb&channel=aaa</span>. 

		</p>
		<h3>Rules API</h3>
        <p>
		    The JSON rules API supports an array of evaluation rules per group type. The group type supported are:
		    <ul>
				<li><strong>parameters</strong>: for the purpose of evaluating key value pairs.</li>
				<li><strong>headers</strong>: for the purpose of evaluating key value pairs.</li>
				<li><strong>body</strong>: for the purpose of evaluating the existence of specific content contained in a POST body payload.</li>
				<li><strong>url</strong>: for the purpose of evaluating the existence of specific content related to the incoming request URL, i.e. RESTful URLs containing token identifiers.</li>
		    </ul>
	    </p>
	    <h4>Are rules handled with AND or OR?<h4>
	    <ul>
		    <li>All rules per TYPE will be treated as 'AND'. For example, all key/value pairs in 'parameters' must exist AND be valid.</li>
			<li>All rules between TYPEs will be treated as 'OR'. For example, all key/value pair rules must be TRUE in 'parameters' OR all key/value rules must be true for 'headers'. </li>
	    </ul>
	    <p>
	    Each evaluation rule includes the following:
		
        <table class="api">
        <thead>
        <tr><th>KEY</th><th>DESCRIPTION</th></tr>
          </thead>
        <tbody>
        <tr><td>key</td><td><strong>Required</strong> for rule group type 'headers' and 'parameters'. 
        The name of the parameter-key or header-key that needs a non-empty string value. Use <strong>&#42;</strong> for wildcard 
        support, meaning you want to apply evaluation rules to values only.   
        
        <br /></br /><strong>Ignored</strong> for rule group type 'body' and 'url'.</td></tr>
        <tr><td>desc</td><td><strong>Optional.</strong> A short description of what you're trying to accomplish. Note: 
        This message will display in the History page if an evaluation issue occurs with a Service (not Scenario) to inform the user that they may be missing request parameters. </td></tr>
        <tr><td>value_rule_arg</td><td>Can be an empty string, character, non-empty string or string representing a regex' value. </td></tr>
        <tr><td>value_rule_type</td><td>Tells Mockey <i>how</i> to evaluate the key-value pair. Valid values are
	<ul>
		<li><b>string_required</b>: Case insensitive. The string or character must be present in the non-empty-VALUE associated to the parameter (or header) KEY. 
			You could use a REGEX here, like <span class="code_text">^(?!\s*$).+</span>, but for those who just want a simple character or text search, you can use this instead
			of dealing with the complexities of regular-<i>confusing?</i>-expressions. 
			Please read the <a href="#beware_of_match">beware</a> section before using 'string_required'.
			</li>
		<li><b>regex_required</b>: A non-null value must be provided and satisfy the regex' definition ('value_rule_arg')</li>
		<li><b>regex_optional</b>: Optional, but if non-null, then it must satisfy the regex' definition ('value_rule_arg')</li>
		</ul>
		
		</td></tr>
          </tbody>
        
        </table>
        </p>
        <p>Here's another example. When working with REST API based web services, you may encounter access limitations on the server and/or client side. 
        	Here is one way to map a PUT request to a scenario with a header rule and looking for a specific TOKEN. 
<pre class="code" style="font-size:0.9em;">
// EXAMPLE  
{
    "headers": [
        {
            "key": "x-http-method-override",
            "value_rule_arg": "PUT",
            "value_rule_type": "string_required"
        }
    ],
    "url": [
        {
            "key": "*",
            "value_rule_arg": "44",
            "value_rule_type": "string_required"
        }
    ]
}
</pre>
	<p>This should work for a Service defined as (/service/customer/{token}), and an incoming request:
	curl -X POST -d@text.txt http://localhost:8080/service/customer/44 --header "x-http-method-override:POST" 
	</p>
    <p>
    Need a sample file? Start with a clean Mockey and Import this <a href="sample.xml">file</a>. 
    </p>
        
    </div>
    <div class="help_section">
	    <a href="#response_schema" name="response_schema"></a>
        <h2>Response JSON Schema</h2>
        <p>
        How do you know that your Service Scenarios (those defined in JSON) are in a valid data format? By providing a JSON Schema, 
        Mockey will inform you if your Scenarios are invalid and give you insight on why. For more information on JSON Schema, see
        <a href="http://json-schema.org/">http://json-schema.org/</a>. 
        
        </p>
    </div>
    <div class="help_section_zebra">
	    <a href="#status" name="status"></a>
	    <h2>Status</h2>
	    <p>
	    There's a 'status' URL that will give you information on Mockey. See <a href="<c:url value="/status" />">status</a>. You should 
	    use it if you need to check if Mockey is running, time of start, and location of where it's writing information to. This is 
	    helpful for your continuous integration/testing server.
	    
	    </p>
	</div>
	<div class="help_section_zebra">
	    <a href="#startup" name="startup"></a>
	    <h2>Start Up Configuration</h2>
	    <p>
	    There are a few options to configure Mockey on startup. 
	    <h3>Executable Jar</h3>
	    If you are running the executable jar, check the Help command to see your options:
		    <div class="code">
		    java -jar Mockey.jar --help
		    </div>
	    For example, if you want your configuration files to be located in a specific directory, then do the following:
		    <div class="code">
		    java -jar Mockey.jar -l /Users/clafonta/Work/Mockey/dist/testme
		    </div>
	    ..and Mockey will save your files in the 'testme' directory, e.g.:
	    
	    <pre>
>tree /Users/clafonta/Work/Mockey/dist/testme/
/Users/clafonta/Work/Mockey/dist/testme/
|---mock_service_definitions.xml
|___mockey_def_depot
    |____Feeling
         |---Feeling.xml
         |___scenarios
             |--- happy.txt
             |___ happy.xml
	    </pre>
	    <h3>Using Tomcat</h3>
	    If you are using the WAR file, running in a Tomcat instance, then you can set your preferred Mockey repository location, via command line (Unix) as follows:
	    <div class="code">
	    export JAVA_OPTS="-DmockeyDefinitionsRepoHome=/Users/johnsmith/work_repo"
	    </div>
	    The variable name is '<i>mockeyDefinitionsRepoHome</i>'. Search the web for more information on JAVA_OPS and Tomcat and how to set it in Windows if necessary.
	    </p>
	</div>
	
	
	
</div>	
<jsp:include page="/WEB-INF/common/footer.jsp" />