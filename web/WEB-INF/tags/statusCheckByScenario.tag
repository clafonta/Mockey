<%@ tag import="com.mockey.model.*" %>
<%@ tag import="java.util.*" %>
<%@ tag import="java.text.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ attribute name="scenario" required="true" type="com.mockey.model.Scenario" %>
<%@ attribute name="serviceId" required="true" %>
<div class="tag_word_group">

<span class="tag_word_lead">Last visit:</span>

<c:if test="${not empty scenario.lastVisitSimple}">
<span class="tag_word" id="remove-scenario-last_${scenario.id}_${serviceId}"><a href="#" title="Delete last visit" class="scenario-lastvisit-remove remove_grey" id="remove-scenario-last_${scenario.id}_${serviceId}">X</a>
${scenario.lastVisitSimple}</span>
</c:if> 

<br />
<span class="tag_word_lead">Tag(s):</span>
<c:forEach var="tagArg" items="${scenario.tagList}"  varStatus="status">
		<span class="tag_word" id="service-scenario-tag-id_${scenario.id}_${scenario.serviceId}_${status.count}" value="${tagArg}">
			<a href="#" title="Delete tag from this Scenario" class="service-scenario-tag-remove remove_grey" id="remove-scenario-tag_${scenario.id}_${serviceId}_${status.count}" >X</a> 
			${tagArg}
		</span>	  
</c:forEach>
</div>