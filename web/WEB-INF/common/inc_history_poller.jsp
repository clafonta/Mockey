<script type="text/javascript" src="<c:url value="/javascript/jquery.periodicalupdater.js"  />"></script>
<p>
    <c:url value="/configure" var="flushUrl">
        <c:param name="serviceId" value="${mockservice.id}" />
        <c:param name="clearRequests" value="true" />
    </c:url>
    <a href="<c:out value="${flushUrl}"/>" 
    title="Delete cached scenarios" onclick="return confirm('This will delete all cached scenarios. \nAre you sure you want to delete this cache?');">Clear history</a>
</p>
<p>This is a list of IP addresses who called your service. Click on the IP to see all the messages exchanged between Mockey and this IP.</p>
<p>
    <p id="log"></p>
    <c:url value="/checkforhistory" var="checkforhistoryUrl">
        <c:param name="serviceId" value="${mockservice.id}" />
    </c:url>
    <script language="javascript">
        $.PeriodicalUpdater("<c:out value="${checkforhistoryUrl}"/>",{},
				function(newData) {
					$('#log').append(newData);
				}
			);
    </script>

</p>