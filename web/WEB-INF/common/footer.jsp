<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<div id="footer">
  <p class="tiny" style="padding-bottom:2em;" >
  <%
  String ver = com.mockey.runner.JettyRunner.class.getPackage().getImplementationVersion();
  if(ver==null) {
     ver = "debug-mode";
  }
  %>
  
  <span class="code_text"> Version: <%= ver %> |  </span>  
  For more information, see <a href="https://github.com/clafonta/Mockey">https://github.com/clafonta/Mockey</a></p>

</div>
<div id="poorStartMessage" style="font-size:0.8em;position:fixed; bottom:10px; right:5px; color:green;">Oh dang. You need to click the browser's refresh button or enable JavaScript.</div>

</div>
</body>
</html>