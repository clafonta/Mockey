<%@ tag import="com.mockey.model.*" %>
<%@ tag import="java.util.*" %>
<%@ tag import="java.text.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="view" required="true" %> 
<%@ attribute name="service" required="true" type="com.mockey.model.Service" %>

<%
// There are TWO views, DETAIL or MASTER
//
Calendar now = Calendar.getInstance();
SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
String time = "";

if(service.getLastVisit()!=null && service.getLastVisit() > 0 ){
 time = formatter.format(new Date(new Long(service.getLastVisit())));

}
%>
<span class="tag_word_lead">Last visit: ${time}</span>
<br />
<span class="tag_word_lead">Tag(s):</span>
<c:forEach var="tagArg" items="${service.tagList}"  varStatus="status">
		<span class="tag_word" id="service-tag-id_${service.id}_${status.count}_${view}">
			<a href="#" title="Delete tag from this Service" class="service-tag-remove remove_grey" id="remove-service-tag_${service.id}_${status.count}" value="${tagArg}">X</a> 
			${tagArg}
		</span>	  
</c:forEach>
