<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>JSON Schema validation online</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8">
    <meta name="description" content="Validate your JSON Schema online">
    <link href="<c:url value="/css/style.css" />" rel="stylesheet" type="text/css">
    <link href="<c:url value="/css/jsonschemastyle.css" />" rel="stylesheet" type="text/css">
    <script src="<c:url value="/jquery-ui-1.8.1.custom/js/jquery-1.9.0.min.js" />" type="text/javascript"></script>
    <script src="<c:url value="/javascript/jquery.qtip-1.0.0-rc3.js" />" type="text/javascript"></script>
    <script src="<c:url value="/javascript/jsonschemacommon.js" />" type="text/javascript"></script>
    <script src="<c:url value="/javascript/jsonschema.js" />" type="text/javascript"></script>
    
    <script type="text/javascript">
        // "main" is defined in site.js
        $(document).ready(main);
    </script>
</head>
<body>
<div id="top">
    <div class="noscript">
        <p>
            <span style="font-weight: bold">This site requires Javascript to run
            correctly</span>
        </p>
    </div>

    
    <p>
    <a href="<c:url value="home" />" class="hhButton">Home</a>
    Service ID: ${service.id} Scenario ID: ${scenario.id}
    </p>
    <p><a href="http://json-schema.org">JSON Schema</a> can validate your JSON
    data. You can also <a id="loadSamples" href="#">load samples</a> from the
    <a href="https://github.com/json-schema/JSON-Schema-Test-Suite">JSON Schema
    test suite</a>.
    </p>
</div>

<form id="validate" method="POST">
    <div id="left" class="content" style="padding-bottom:3em;">
        <div class="horiz">
            <h3 for="schema">Service's JSON Schema:</h3>
            <span class="error starthidden" id="invalidSchema">Invalid JSON:
                parse error, <a href="#"></a></span>
        </div>
        <textarea name="schema" rows="20" cols="20" class="half"
            id="schema">${service.responseSchema}</textarea>
        <div class="options">
            <p>Validation options:</p>
            <div>
                <input type="checkbox" name="useV3" id="useV3" value="true">
                <label for="useV3">Use draft v3</label>
            </div>
            <div>
                <input type="checkbox" name="useId" id="useId" value="true">
                <label for="useId">Trust <span style="font-family: monospace">id
                </span> (inline dereferencing)</label>
            </div>
        </div>
        <div class="horiz">
            <h3 for="data">Service Scenario's Response Data:</h3>
            <span class="error starthidden" id="invalidData">Invalid JSON: parse
                error, <a href="#"></a></span>
        </div>
        <textarea name="data" rows="20" cols="20" class="half"
            id="data">${scenario.responseMessage}</textarea>
        <input type="submit" value="Validate" class="hhButtonRed">
    </div>
</form>
<div id="right" class="content" >
    <div class="horiz">
        <label for="results">Validation result:</label>
        <span class="error starthidden" id="validationFailure">failure</span>
        <span class="success starthidden" id="validationSuccess">success</span>
    </div>
    <textarea name="results" rows="20" cols="20"
        id="results" readonly="readonly"></textarea>
</div>
</body>
</html>
