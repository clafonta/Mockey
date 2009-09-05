<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<%--@elvariable id="proxyInfo" type="com.mockey.ProxyServer"--%>
<c:set var="actionKey" value="conf_service" scope="request" />
<c:set var="pageTitle" value="Proxy Settings" scope="request" />
<c:set var="currentTab" value="proxy" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
<div id="main">
    <%@ include file="/WEB-INF/common/message.jsp"%>     
    <h1>Proxy Setup</h1> 
    <div>
    <p>If Mockey connects to services via your corporate proxy server, here's the place to pipe through it. </p>
    <p >
    <h3><span style="color:red;" >Note: These   settings will not be exported when using the <strong>Export Services Definitions</strong> feature.</span></h3>
    </div>
    <div>        
        <form action="<c:url value="/proxy/settings" />" method="POST">
       
            <table class="simple" width="100%">
                <tbody>
                    <tr>
                        <th><p>Proxy enabled?</p></th>
                        <td>
                            <input type="radio" name="proxyEnabled" value="true"
                                                            <c:if test='${proxyInfo.proxyEnabled}'>checked</c:if> /> Yes <br />
                            <input type="radio" name="proxyEnabled" value="false"
                                                            <c:if test='${!proxyInfo.proxyEnabled}'>checked</c:if> /> No                          
                            
                        </td>
                   </tr>
                    <tr <c:if test='${!proxyInfo.proxyEnabled}'>class="overlabel"</c:if>>
                        <th width="180px"><p>Proxy URL (and :port if needed)</p></th>
                        <td>
                            <p><input type="text" name="proxyUrl" size="80"  value="<c:out value="${proxyInfo.proxyUrl}"/>" /> </p>
                        </td>
                    </tr>
                    
                    <tr <c:if test='${!proxyInfo.proxyEnabled}'>class="overlabel"</c:if>>
                    
                        <th width="180px"><p>Proxy Username</p></th>
                        <td>
                            <p><input type="text" name="proxyUsername" maxlength="20" size="20" value="<c:out value="${proxyInfo.proxyUsername}"/>" /></p>
                        </td>
                    </tr>
                    <tr <c:if test='${!proxyInfo.proxyEnabled}'>class="overlabel"</c:if>>
                        <th width="180px"><p>Proxy Password</p></th>
                        <td>
                            <p><input type="password" name="proxyPassword" maxlength="20" size="20" value="<c:out value="${proxyInfo.proxyPassword}"/>" /></p>
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
