<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="mockey" uri="/WEB-INF/mockey.tld" %>
<c:set var="actionKey" value="edit_service" scope="request" />
<c:set var="pageTitle" value="Configure" scope="request" />
<c:set var="currentTab" value="create" scope="request" />
<%--@elvariable id="mockservice" type="com.mockey.MockServiceBean"--%>
<%@include file="/WEB-INF/common/header.jsp" %>
<script type="text/javascript">
	$(function() {

		$("#accordion").accordion({
			active: false,
			collapsible: true
		});
	});
</script>	
<script type="text/javascript">
	$(function() {

		// a workaround for a flaw in the demo system (http://dev.jqueryui.com/ticket/4375), ignore!
		$("#dialog").dialog("destroy");
		
		var name = $("#name"),
			email = $("#email"),
			password = $("#password"),
			allFields = $([]).add(name).add(email).add(password),
			tips = $(".validateTips");

		function updateTips(t) {
			tips
				.text(t)
				.addClass('ui-state-highlight');
			setTimeout(function() {
				tips.removeClass('ui-state-highlight', 1500);
			}, 500);
		}

		function checkLength(o,n,min,max) {

			if ( o.val().length > max || o.val().length < min ) {
				o.addClass('ui-state-error');
				updateTips("Length of " + n + " must be between "+min+" and "+max+".");
				return false;
			} else {
				return true;
			}

		}

		function checkRegexp(o,regexp,n) {

			if ( !( regexp.test( o.val() ) ) ) {
				o.addClass('ui-state-error');
				updateTips(n);
				return false;
			} else {
				return true;
			}

		}
		
		$("#dialog-form").dialog({
			autoOpen: false,
			height: 300,
			width: 350,
			modal: true,
			buttons: {
				'Create an account': function() {
					var bValid = true;
					allFields.removeClass('ui-state-error');

					bValid = bValid && checkLength(name,"username",3,16);
					bValid = bValid && checkLength(email,"email",6,80);
					bValid = bValid && checkLength(password,"password",5,16);

					bValid = bValid && checkRegexp(name,/^[a-z]([0-9a-z_])+$/i,"Username may consist of a-z, 0-9, underscores, begin with a letter.");
					// From jquery.validate.js (by joern), contributed by Scott Gonzalez: http://projects.scottsplayground.com/email_address_validation/
					bValid = bValid && checkRegexp(email,/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i,"eg. ui@jquery.com");
					bValid = bValid && checkRegexp(password,/^([0-9a-zA-Z])+$/,"Password field only allow : a-z 0-9");
					
					if (bValid) {
						$('#users tbody').append('<tr>' +
							'<td>' + name.val() + '</td>' + 
							'<td>' + email.val() + '</td>' + 
							'<td>' + password.val() + '</td>' +
							'</tr>');
						$('#accordion').append('<h3><a href=\"#\">'+name.val()+'</a></h3><div>Woot</div>').accordion('destroy').accordion({
							active: false,
							collapsible: true
						});
						
								
						$(this).dialog('close');
					}
				},
				Cancel: function() {
					$(this).dialog('close');
				}
			},
			close: function() {
				allFields.val('').removeClass('ui-state-error');
			}
		});
		
		
		
		$('#create-scenario')
			.button()
			.click(function() {
				$('#dialog-form').dialog('open');
			});

	});
	
</script>


<div id="main">
    <%@ include file="/WEB-INF/common/message.jsp"%>
    <c:choose>
        <c:when test="${!empty mockservice.id}">
            <c:url value="/home" var="serviceUrl">
              <c:param name="serviceId" value="${mockservice.id}" />                                                                               
        	</c:url> 
            <h1>Service Setup: <span class="highlight"><a href="${serviceUrl}"><c:out value="${mockservice.serviceName}"/></a></span></h1>
        </c:when>
        <c:otherwise>
            <h1>Service Setup</h1>
        </c:otherwise>
    </c:choose>
   
    <form action="<c:url value="/setup"/>" method="POST">
        <input type="hidden" name="serviceId" value="<c:out value="${mockservice.id}"/>" />
        <table class="simple" width="100%">
            <tbody>
	            <tr><th><p>Service name:</p></th>
	                <td>
	                    <p><input type="text" name="serviceName" maxlength="100" size="90%" value="<c:out value="${mockservice.serviceName}"/>" /></p>
	                    <p class="tiny">Use a self descriptive name. For example, if you were to use this for 'authentication' testing, then call it 'Authentication'.</p>
	                </td>
	            </tr>
                <tr>
                    <th><p>Real service URL: </p></th>
                    <td>
                        <p><input type="text" name="realServiceUrl" maxlength="100" size="90%" value="<c:out value="${mockservice.realServiceUrl}"/>" /></p>
                        <p class="tiny">You'll need this URL if you want Mockey to serve as a proxy to record transactions between your application and the real service.</p>
                    </td>
                </tr>
	          
	            <tr>
	                <th><p>HTTP header definition:</p></th>
	                <td>
	                    <p>
	                      <select name="httpContentType">
	                        <option value="" <c:if test="${mockservice.httpContentType eq ''}">selected="selected"</c:if>>[select]</option>
                            <option value="text/xml;" <c:if test="${mockservice.httpContentType eq 'text/xml;'}">selected="selected"</c:if>>text/xml;</option>
                            <option value="text/plain;" <c:if test="${mockservice.httpContentType eq 'text/plain;'}">selected="selected"</c:if>>text/plain;</option>
                            <option value="text/css;" <c:if test="${mockservice.httpContentType eq 'text/css;'}">selected="selected"</c:if>>text/css;</option>
                            <option value="application/json;" <c:if test="${mockservice.httpContentType eq 'application/json;'}">selected="selected"</c:if>>application/json;</option>
                            <option value="text/html;charset=utf-8" <c:if test="${mockservice.httpContentType eq 'text/html;charset=utf-8'}">selected="selected"</c:if>>text/html;charset=utf-8</option>
                            <option value="text/html; charset=ISO-8859-1" <c:if test="${mockservice.httpContentType eq 'text/html; charset=ISO-8859-1'}">selected="selected"</c:if>>text/html; charset=ISO-8859-1</option>
                            <!-- <option value="other" <c:if test="${mockservice.httpContentType eq 'other'}">selected="selected"</c:if>>other</option>  -->
                          </select>
	                    </p>
	                    <p class="tiny">For example: <span style="font-style: italic;">text/xml; utf-8</span>, <span
                                style="font-style: italic;">application/json;</span>, etc. </p>
	                </td>
	            </tr>
            </tbody>
        </table>
        <p align="right">
	        <c:choose>
	            <c:when test="${!empty mockservice.id}">
	                <input type="submit" name="update" value="Update" />
	            </c:when>
	            <c:otherwise>
	                <input type="submit" name="create" value="Create" />
	            </c:otherwise>
	        </c:choose>
	        <c:if test="${!empty mockservice.id}">
	            <input type="submit" name="delete" value="Delete" onclick="return confirm('Deleting this service will delete all scenarios associated with it. \nAre you sure you want to delete this service?');" />
	        </c:if>
	        <a href="<c:url value="/"/>">Cancel</a>
	    </p>
    </form>
    <h3>Existing Scenarios</h3>
	<!--  xxx -->
	<div class="demo">
		<div id="accordion">
			
			<c:forEach var="mockscenario" begin="0" items="${mockservice.scenarios}" varStatus="status">   
				<h3><a href="#">${mockscenario.scenarioName}</a></h3>
				<div>
				  <p>
				  <%@ include file="/inc_service_scenario_setup.jsp" %>
				 
				  </p>
				</div>
			</c:forEach>
		</div>
   </div><!-- End demo -->
   <!-- xxxxxxxx -->
   <div class="demo">

<div id="dialog-form" title="Create new scenario">
	<p class="validateTips">All form fields are required.</p>

	<form>
	<fieldset>
		<label for="name">Name</label>
		<input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all" />
		<label for="email">Email</label>
		<input type="text" name="email" id="email" value="" class="text ui-widget-content ui-corner-all" />
		<label for="password">Password</label>
		<input type="password" name="password" id="password" value="" class="text ui-widget-content ui-corner-all" />
		<label for="scenarioName">Name</label>
		<input type="text" name="scenarioName" id="scenarioName" class="text ui-widget-content ui-corner-all" />
		<label for="scenarioName">Match Argument</label>
		<input type="text" name="matchStringArg" id="matchStringArg" class="text ui-widget-content ui-corner-all" />
	</fieldset> 
	</form>
</div>


<div id="users-contain" class="ui-widget">
	<table id="users" class="ui-widget ui-widget-content">
		<thead>
			<tr class="ui-widget-header ">
				<th>Name</th>
				<th>Email</th>
				<th>Password</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>John Doe</td>
				<td>john.doe@example.com</td>
				<td>johndoe1</td>
			</tr>
		</tbody>
	</table>
</div>
<button id="create-scenario">Create new scenario</button>

</div><!-- End demo -->

<div class="demo-description">

<p>Use a modal dialog to require that the user enter data during a multi-step process.  Embed form markup in the content area, set the <code>modal</code> option to true, and specify primary and secondary user actions with the <code>buttons</code> option.</p>

</div><!-- End demo-description -->
   <!-- xxxxxxxx -->
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />