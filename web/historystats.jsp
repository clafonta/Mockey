<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="pageTitle" value="History Stats" scope="request" />
<c:set var="currentTab" value="history" scope="request" />
<%@include file="/WEB-INF/common/header.jsp" %>
<script>
$(document).ready(function(){

		var dt_from = "${rangeStartDate}";
    	var dt_to = "${rangeEndDate}";
    	var filter_from = "${filterStartDate}";
    	var filter_to = "${filterEndDate}";

    	$('.slider-time').html(filter_from);
    	$('.slider-time2').html(filter_to);
    	var min_val = Date.parse(dt_from)/1000;
    	var max_val = Date.parse(dt_to)/1000;
    	var min_filter_val = Date.parse(filter_from)/1000;
    	var max_filter_val = Date.parse(filter_to)/1000;

    	function zeroPad(num, places) {
    	  var zero = places - num.toString().length + 1;
    	  return Array(+(zero > 0 && zero)).join("0") + num;
    	}
    	function formatDT(__dt) {
    	    var year = __dt.getFullYear();
    	    var month = zeroPad(__dt.getMonth()+1, 2);
    	    var date = zeroPad(__dt.getDate(), 2);
    	    var hours = zeroPad(__dt.getHours(), 2);
    	    var minutes = zeroPad(__dt.getMinutes(), 2);
    	    var seconds = zeroPad(__dt.getSeconds(), 2);
    	    return year + '/' + month + '/' + date + ' ' + hours + ':' + minutes + ':' + seconds;
    	};


    	$("#slider-range").slider({
    	    range: true,
    	    min: min_val,
    	    max: max_val,
    	    step: 1,
    	    values: [min_filter_val, max_filter_val],
    	    slide: function (e, ui) {
    	        var dt_cur_from = new Date(ui.values[0]*1000); //.format("yyyy/mm/dd hh:mm:ss");
    	        $('.slider-time').html(formatDT(dt_cur_from));

    	        var dt_cur_to = new Date(ui.values[1]*1000); //.format("yyyy/mm/dd hh:mm:ss");                
    	        $('.slider-time2').html(formatDT(dt_cur_to));
    	        
    	    }
    	});
    	
    	$("#filter").click( function() {
 			
    		var filterStartDate = $('.slider-time').html();
    		var filterEndDate = $('.slider-time2').html();
 			console.log('From: ' + filterStartDate + ' to ' + filterEndDate );
 			document.location="<c:url value="/servicestatschart"/>?filterStartDate="+filterStartDate+"&filterEndDate="+filterEndDate;
		 	
 		});
    	
		var ctx = document.getElementById("myChart");
		var myChart = new Chart(ctx, {
		    type: 'bar',
		    data: {
		    	scaleOverride: true,
		    	scaleStepWidth: 1,
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
<div id="main">
    <h1>History Stats</h1>
    
   
    <p>
    This chart shows the number of Service calls, per Service made during a span of time.  
    Not what you need? Then here's a <a href="<c:url value="/servicestats"/>" >JSON format</a> 
    or a <a href="<c:url value="/servicestats?format=csv"/>" >.csv format</a> you can use for further analysis.
    </p>
   
    <c:if test="${not empty statFlag}">
    <p class="alert_message">The history of requests is empty. </p>
    </c:if>
    
    <c:if test="${empty statFlag}">
    	<p>
		    <table class="stat_table" style="min-width:400px;">
		    	<thead>
		    	<tr><td>Service</td><td>Count</td><td>Earliest Start Time</td></tr>
		    	</thead>
		    	<tbody>
		    	<c:forEach var="stat" items="${statList}"  varStatus="status">
							<tr><td>${stat.serviceName}</td><td>${stat.count}</td><td>${stat.timeAsString}</td></tr>
				</c:forEach>
		    	
		    	</tbody>
		    </table>
		</p>
	    <div id="time-range" style="max-width:600px;">		   
		    <div class="sliders_step1">
		        
		        <div style="float:right;"><strong>End:</strong> ${rangeEndDate}</div>
		        <div><strong>Start:</strong> ${rangeStartDate}</div>
		        <div id="slider-range"></div> 
		    </div>
		    <p><strong>Filter:</strong> <span class="slider-time"></span> - <span class="slider-time2"></span> <a href="#" id="filter">Filter</a> | <a href="<c:url value="/servicestatschart"/>" >Reset</a></p>		    		    
		</div>
	</c:if>
    <canvas id="myChart" height="100%" ></canvas>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />