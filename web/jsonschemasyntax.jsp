<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<!DOCTYPE html>
<head>
    <title>Draft v4 JSON Schema syntax validation</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="description" content="JSON Schema syntax validation">
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

    <!-- TODO -->
    <p>This page demonstrates the library's syntax validation capabilities. It
    is but <a
    href="https://github.com/fge/json-schema-validator/wiki/Roadmap">one of the
    many things version 2.0.x will be able to do</a>.
    </p>

    <p>You will note that you will get warnings if unknown keywords are spotted.
    Also, messages are located accurately: the pointer into the schema will
    tell the containing object where the error occured, and the <span
    style="font-family: monospace;">keyword</span> member will tell you what
    keyword was at fault.</p>

    <!--
        OK, whatever: some people dislike "onclick" on links, but it Works For
        Me(tm). And it seems to have no side effect, so why should I bother?
    -->
    <a onclick="history.back(); return false;" href="#">Back</a>
</div>

<form id="validate" method="POST">
    <div id="left" class="content">
        <div class="horiz">
            <label for="schema">Schema:</label>
            <span class="error starthidden" id="invalidSchema">Invalid JSON:
                parse error, <a href="#"></a></span>
        </div>
        <textarea name="schema" rows="20" cols="20" id="schema"></textarea>
        <input type="submit" value="Validate">
    </div>
</form>
<div id="right" class="content">
    <div class="horiz">
        <label for="results">Validation result:</label>
        <span class="error starthidden" id="validationFailure">failure</span>
        <span class="success starthidden" id="validationSuccess">success</span>
    </div>
    <textarea name="results" rows="20" cols="20" id="results"
        readonly="readonly"></textarea>
</div>
</body>
</html>
