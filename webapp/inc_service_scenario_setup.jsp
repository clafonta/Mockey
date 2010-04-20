
        <form action="<c:out value="${scenarioUrl}"/>" method="post" style="background-color:#99CCFF;">
            <input type="hidden" name="serviceId" value="<c:out value="${mockservice.id}"/>" />
			<c:if test="${!empty mockscenario.id}">
			    <input type="hidden" name="scenarioId" value="<c:out value="${mockscenario.id}"/>" />
			</c:if>
            <table class="simple" width="100%">
                <tbody>
                    <tr>
                        <th width="20%"><p>Scenario Name:</p></th>
	                    <td>
							<p><input type="text" style="width:100%;"  name="scenarioName" value="<c:out value="${mockscenario.scenarioName}"/>" /></p>
							<p class="tiny">Example: <i>Valid Request</i> or <i>Invalid Request</i></p>
						</td>
                    </tr>
                    <tr>
						<th><p><a href="<c:url value="help#static_dynamic"/>">Match argument</a>: <span style="color:blue;">(optional)</span></p></th>
						<td>
						  <p>
						      <textarea name="matchStringArg" style="width:100%;" rows="2" ><c:out value="${mockscenario.matchStringArg}" /></textarea>
						  </p>
						  
						</td>
                    </tr>      
					<tr>
						<th><p>Scenario response message:</p></th>
						<td>
							<p><textarea class="resizable" name="responseMessage" rows="10" style="width:100%;"><c:out value="${mockscenario.responseMessage}" escapeXml="false"/></textarea>
							
							</p>
						    <p class="tiny">The message you want your mock service to reply with. Feel free to cut and paste XML, free form text, etc.</p>
						</td>
					</tr>
				
                </tbody>
            </table>
	        <p align="right">
				<c:choose>
		            <c:when test="${!empty mockservice.id}">
		                <input type="submit" name="update" value="Update" class="button" />
					</c:when>
					<c:otherwise>
		                <input type="submit" name="create" value="Create" class="button" />
					</c:otherwise>
		        </c:choose>            
		        <c:if test="${!empty mockservice.id}">
		            <button type="submit" name="delete" class="button" onclick="return confirm('Are you sure you want to delete this scenario?');">Delete</button>
		        </c:if>
		        <a href="<c:url value="/setup?serviceId=${mockservice.id}" />">Cancel</a>
	        </p>
	    </form>
	    