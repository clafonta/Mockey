<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%--@elvariable id="proxyInfo" type="com.mockey.ProxyServer"--%>
<c:set var="actionKey" value="conf_service" scope="request" />
<c:set var="pageTitle" value="Proxy Settings" scope="request" />
<c:set var="currentTab" value="proxy" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>
<div id="main">
    <%@ include file="/WEB-INF/common/message.jsp"%>     
    
    <div>
        <p><h1>Proxy Setup</h1></p> 
	    <p>If Mockey connects to services via your corporate proxy server, here's the place to pipe through it. </p>
	    <div class="conflict_message" >
	    	<h3>Note: Only 'enabled' yes/no and proxy URL are exported when using the <strong>Export Services Definitions</strong> 
	    	feature. We don't export username or password values.</h3>
	    </div>
    </div>
    <div>        
        <form action="<c:url value="/proxy/settings" />" method="POST">
       
            <table class="simple" width="100%">
                <tbody>
                    <tr>
                        <th width="200px"  align="right"><p>Proxy enabled?</p></th>
                        <td>
                            <input type="radio" name="proxyEnabled" value="true"
                                                            <c:if test='${proxyInfo.proxyEnabled}'>checked</c:if> /> Yes <br />
                            <input type="radio" name="proxyEnabled" value="false"
                                                            <c:if test='${!proxyInfo.proxyEnabled}'>checked</c:if> /> No                          
                            
                        </td>
                   </tr>
                    <tr <c:if test='${!proxyInfo.proxyEnabled}'>class="overlabel"</c:if>>
                        <th align="right"><p>Proxy URL </p>
                         <p class="tiny">(and :port if needed)</p></th>
                        <td>
                            <p><input type="text" name="proxyUrl" size="80"  value="<c:out value="${proxyInfo.proxyUrl}"/>" />  </p>
                            
                        </td>
                    </tr>
                    
                    <tr <c:if test='${!proxyInfo.proxyEnabled}'>class="overlabel"</c:if>>
                    
                        <th align="right"><p>Proxy Username</p></th>
                        <td>
                            <p><input type="text" name="proxyUsername" maxlength="20" size="20" value="" /></p>
                            <p class="tiny">We don't show the username after you click Update</p>
                        </td>
                    </tr>
                    <tr <c:if test='${!proxyInfo.proxyEnabled}'>class="overlabel"</c:if>>
                        <th align="right"><p>Proxy Password</p></th>
                        <td>
                            <p><input type="password" name="proxyPassword" maxlength="20" size="20" value="" /></p>
                            <p class="tiny">We don't show the password after you click Update</p>
                        </td>
                    </tr>
                </tbody>
            </table>
            <div align="right">
                <p style="text-align: bottom;"><input type="submit" name="update" value="Update" /></p>
            </div>
        </form>
    </div>    
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
