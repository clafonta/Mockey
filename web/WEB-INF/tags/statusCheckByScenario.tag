<%@ tag import="com.mockey.model.*" %>
<%@ tag import="java.util.*" %>
<%@ tag import="java.text.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ attribute name="scenario" required="true" type="com.mockey.model.Scenario" %>
<%@ attribute name="service" required="false" type="com.mockey.model.Service" %>

<div class="tag_word_group">

<span class="tag_word_lead">Last visit:</span>

<c:if test="${not empty scenario.lastVisitSimple}">
<span class="tag_word" id="remove-scenario-last_${scenario.id}_${service.id}"><a href="#" title="Delete last visit" class="scenario-lastvisit-remove remove_grey" id="remove-scenario-last_${scenario.id}_${service.id}">X</a>
${scenario.lastVisitSimple}</span>
</c:if> 

<br />
<p>
<span class="tag_word_lead">Tag(s):</span>
<c:forEach var="tagArg" items="${scenario.tagList}"  varStatus="status">
		<span class="tag_word" id="service-scenario-tag-id_${scenario.id}_${scenario.serviceId}_${status.count}" value="${tagArg}">
			<a href="#" title="Delete tag from this Scenario" class="service-scenario-tag-remove remove_grey" id="remove-scenario-tag_${scenario.id}_${service.id}_${status.count}" >X</a> 
			${tagArg}
		</span>	  
</c:forEach>
</p>
<p class="count-box">Service ID: ${service.id}, Scenario ID: ${scenario.id} </p>
<p class="count-box">Method type: <strong>${scenario.httpMethodType}</strong> </p>
<hr />
<%
String invalidJSONFormatMsg = ""; 
if (scenario.hasMatchArgument() && scenario.isMatchStringArgEvaluationRulesFlag()) {
	// Let's provide a visual highlight to the user if JSON format is invalid.
	try {
		com.mockey.plugin.RequestInspectorDefinedByJson obj = new com.mockey.plugin.RequestInspectorDefinedByJson(scenario.getMatchStringArg());
	} catch (org.json.JSONException jsonException) {
		
		invalidJSONFormatMsg = "<p class='alert_message'>Invalid JSON Format</p>";
	}
}
%>
<p class="tiny">Match arguments: <%= invalidJSONFormatMsg%> 
<pre class="match" > ${scenario.matchStringArg} </pre> 
</p>


<%
if(service.isResponseSchemaFlag()) {
boolean result = com.mockey.ui.JsonSchemaUtil.validData(service.getResponseSchema(), scenario.getMatchStringArg() );
String messageClass = "info_message";
String message = "JSON is valid; satisfies JSON Schema.";
String buttonClass = "hhButtonBlue";
if(!result) {

  messageClass = "conflict_message";
  buttonClass = "hhButtonRed";
  message ="Invalid JSON based on this Service's JSON Schema.";
}
%>
<div class='<%= messageClass%>'> <%= message %> <a class="<%= buttonClass %>" href="<c:url value="jsonschemavalidate"/>?serviceId=${service.id}&scenarioId=${scenario.id}">Inspect JSON</a>
</div> 


<% 
} else {
  %><span class="tiny">No JSON Schema validation.</span><%
}

%>


</div>