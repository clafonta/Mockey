<%@ tag import="com.mockey.model.*" %>
<%@ tag import="java.util.*" %>
<%@ tag import="java.text.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ attribute name="lastVisit" required="true"%>
<%@ attribute name="tag" required="true"%>
<%@ attribute name="serviceName" required="true"%>
<%@ attribute name="serviceId" required="true"%>
<%
Calendar now = Calendar.getInstance();
SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd ', ' hh:mm:ss a zzz");
String time = "";
if(lastVisit!=null && lastVisit.trim().length() > 0){
 time = formatter.format(new Date(new Long(lastVisit)));

}
%>

<table class="tag_word_table status-info">
<tr>
   <td class="tiny" style="text-align:right;"><span class="power-link">Tag(s):</span></td><td class="tiny" style="padding-left:5px;">${tag}</td>
</tr>
<tr>
   <td class="" style="text-align:right; width:58px;">Last visit:</td><td class="tiny" style="padding-left:5px;"><%=time%></td>
</tr>
</table>
