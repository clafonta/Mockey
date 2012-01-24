<%@ tag import="com.mockey.model.*" %>
<%@ tag import="java.util.*" %>
<%@ tag import="java.text.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ attribute name="scenario" required="true" type="com.mockey.model.Scenario" %>
<%@ attribute name="serviceId" required="true" %>

<span class="tag_word_lead">Tag(s):</span>
<c:forEach var="tagArg" items="${scenario.tagList}"  varStatus="status">

		<span class="tag_word" id="service-scenario-tag-id_${scenario.id}_${scenario.serviceId}_${status.count}" value="${tagArg}">
			<a href="#" title="Delete tag from this Scenario" class="service-scenario-tag-remove remove_grey" id="remove-scenario-tag_${scenario.id}_${serviceId}_${status.count}" >X</a> 
			${tagArg}
		</span>	  
</c:forEach>

