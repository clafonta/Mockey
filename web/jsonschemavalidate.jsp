<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Mockey -  JSON Schema Validation</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<c:url value="/images/favicon.ico" />">
    <link href="<c:url value="/css/bootstrap.min.css" />" rel="stylesheet" rel="stylesheet">
    <link href="<c:url value="/css/bootstrap-responsive.min.css" />" rel="stylesheet">
    <link href="<c:url value="/css/jsonschemastyle.css" />" rel="stylesheet">
    <script src="<c:url value="/jquery-ui-1.8.1.custom/js/jquery-1.9.0.min.js" />" type="text/javascript"></script>
    <script src="<c:url value="/javascript/jquery.qtip-1.0.0-rc3.js" />" type="text/javascript"></script>
    <script src="<c:url value="/javascript/jsonschemacommon.js" />" type="text/javascript"></script>
    <script src="<c:url value="/javascript/jsonschema.js" />" type="text/javascript"></script>
    <script src="<c:url value="/javascript/bootstrap.min.js" />" type="text/javascript"></script>
    <script type="text/javascript">
        // "main" is defined in site.js
        $(document).ready(main);
    </script>
</head>
<body>
    <div class="navbar">
      <div class="navbar-inner">
        <div class="container">
          <h4>JSON Schema Validation</h4>
        </div>
      </div>
    </div>
    <div class="container">
      <div class="row">
        <div class="span12">
        <p>
	    <c:choose>
		    <c:when test="${!empty service.id}">
		        <c:url value="/home" var="returnToServiceUrl">
		          <c:param name="serviceId" value="${service.id}" />  
		          <c:param name="scenarioId" value="${scenario.id}" />                                                                               
		    	</c:url> 
		    	<a class="btn btn-primary" href="${returnToServiceUrl}">
	            Return to main page
                </a>
		    </c:when>
		    <c:otherwise>
		    	<c:url value="/home" var="returnToServiceUrl"/>
		    	<a class="btn btn-primary" href="${returnToServiceUrl}">
	            Return to main page
                </a>
		    </c:otherwise>
	    </c:choose>
	    <a href="http://json-schema.org">JSON Schema</a> can validate your JSON
	    data. You can also <a id="loadSamples" href="#">load samples</a> from the
	    <a href="https://github.com/json-schema/JSON-Schema-Test-Suite">JSON Schema
	    test suite</a>.
	    </p>
        </div>
      </div>
      <div class="row">
        <div class="span6">
			<form id="validate" method="POST">
		    <div id="left">
		       <h5 for="schema">Service's JSON Schema:</h5>
		       <span class="error starthidden" id="invalidSchema">Invalid JSON: parse error, <a href="#"></a></span>
		       <textarea name="schema" style="width:100%;" rows="10" id="schema">${service.responseSchema}</textarea>
		       <p>Validation options:</p>
		       <label class="checkbox" for="useV3">
			     <input type="checkbox" name="useV3" id="useV3" value="true"> Use draft v3
			   </label>
		       <label class="checkbox" for="useId">
			     <input type="checkbox" name="useId" id="useId" value="true"> Trust <span style="font-family: monospace">id</span> (inline dereferencing)
			   </label>
		       <p>
		         <h5 for="data">Service Scenario's Response Data:</h5>
		         <span class="error starthidden" id="invalidData">Invalid JSON: parse error, <a href="#"></a></span>
		       </p>
		       <textarea name="data" rows="10" style="width:100%;" id="data">${scenario.responseMessage}</textarea>
		       <input type="submit" value="Validate" class="btn btn-primary">
		    </div>
		   </form>	           
        </div>
        <div class="span6">
          <div id="right">
			    <p>
			        <h5 for="results">Validation result:</h5>
			        <span class="error starthidden" id="validationFailure">failure</span>
			        <span class="success starthidden" id="validationSuccess">success</span>
			    </p>
			    <textarea name="results" rows="25" style="width:100%;"
			        id="results" readonly="readonly"></textarea>
			</div>
      </div>
      </div>
      <hr>
      <footer>
         <p>For more information, see <a href="https://github.com/clafonta/Mockey">https://github.com/clafonta/Mockey</a></p>
      </footer>
    </div>
  </body>
</html>
