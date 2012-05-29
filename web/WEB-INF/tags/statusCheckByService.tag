<%@ tag import="com.mockey.model.*" %>
<%@ tag import="java.util.*" %>
<%@ tag import="java.text.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="view" required="true" %> 
<%@ attribute name="service" required="true" type="com.mockey.model.Service" %>

<div class="tag_word_group">
<span class="tag_word_lead">Last visit:</span>
<c:if test="${not empty service.lastVisitSimple}">
<span class="tag_word" id="remove-service-last_${service.id}_${view}"><a href="#" title="Delete last visit" class="service-lastvisit-remove remove_grey" id="remove-service-last_${service.id}">X</a>
${service.lastVisitSimple}</span>
</c:if> 
<br />
<span class="tag_word_lead">Tag(s):</span>
<c:forEach var="tagArg" items="${service.tagList}"  varStatus="status">
		<span class="tag_word" id="service-tag-id_${service.id}_${status.count}_${view}">
			<a href="#" title="Delete tag from this Service" class="service-tag-remove remove_grey" id="remove-service-tag_${service.id}_${status.count}" value="${tagArg}">X</a> 
			${tagArg}
		</span>	  
</c:forEach>
<p class="count-box">Service ID: ${service.id}</p>  
</div>
