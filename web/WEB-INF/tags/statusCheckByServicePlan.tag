<%@ tag import="com.mockey.model.*" %>
<%@ tag import="java.util.*" %>
<%@ tag import="java.text.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ attribute name="servicePlan" required="true" type="com.mockey.model.ServicePlan" %>
<div class="tag_word_group">
<span class="tag_word_lead">Last visit:</span>
<c:if test="${not empty servicePlan.lastVisitSimple}">
<span class="tag_word" id="remove-service-plan-last_${servicePlan.id}"><a href="#" title="Delete last visit" class="service-plan-lastvisit-remove remove_grey" id="remove-service-plan-last_${servicePlan.id}">X</a>
${servicePlan.lastVisitSimple}</span>
</c:if> 

<br />
<span class="tag_word_lead">Tag(s):</span>
<c:forEach var="tagArg" items="${servicePlan.tagList}"  varStatus="status">
		<span class="tag_word" id="service-plan-tag-id_${servicePlan.id}_${status.count}" value="${tagArg}">
			<a href="#" title="Delete tag from this Service Plan" class="service-plan-tag-remove remove_grey" id="remove-service-plan-tag_${servicePlan.id}_${status.count}" >X</a> 
			${tagArg}
		</span>	  
</c:forEach>
</div>