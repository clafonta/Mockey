<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="pageTitle" value="History Stats" scope="request" />
<c:set var="currentTab" value="history" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>

<div id="main">
    

    <h1>History Stats</h1>
    <p>
    Here are some statistics about the calls made to Mockey.
    </p>
    <p>
    Between <b>${startDate}</b> and <b>${endDate}</b>
    </p>
    <canvas id="myChart" height="100%" ></canvas>
    <script>
    $(function() {
		var ctx = document.getElementById("myChart");
		var myChart = new Chart(ctx, {
		    type: 'bar',
		    data: {
		        labels: [
				<c:forEach var="stat" items="${statList}"  varStatus="status">
					"${stat.serviceName}"<c:if test="${!status.last}">,</c:if>
				</c:forEach>
				],
		        datasets: [{
		            label: '# of Service Calls',
		            data: [
		            <c:forEach var="stat" items="${statList}"  varStatus="status">
					${stat.count}<c:if test="${!status.last}">,</c:if>
					</c:forEach>],
		            borderWidth: 1
		        }]
		    },
		    options: {
		    	responsive: true,
		        scales: {
		            yAxes: [{
		                ticks: {
		                    beginAtZero:true
		                }
		            }]
		        }
		    }
		});
    });
</script>
    
</div>



<jsp:include page="/WEB-INF/common/footer.jsp" />