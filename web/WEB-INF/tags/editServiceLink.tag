<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ attribute name="serviceId" required="true"%>

<span style="float:right;" class="tiny power-link"><a href="<c:url value="/setup?serviceId=${serviceId}"/>" title="Edit service definition">Edit</a></span>
