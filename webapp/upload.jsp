<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Upload" scope="request"/>         
<c:set var="currentTab" value="upload" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />   
<div id="main">
    <h3>Upload your service definition(s):</h3>
    <%@ include file="/WEB-INF/common/message.jsp"%>
    <form style="margin-left:20%; margin-right:20%;" action="<c:url value="/upload"/>" method="POST" enctype="multipart/form-data">
        <p><input class="normal" type="file" name="file"/></p>
        <input type="submit" name="upload" value="Upload" class="button"/>
        <input type="submit" name="cancel" value="Cancel" class="button"/>
    </form>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
