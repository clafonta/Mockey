<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="pageTitle" value="Image Depot" scope="request" />
<c:set var="currentTab" value="filesysteminfo" scope="request" />
<jsp:include page="/WEB-INF/common/header.jsp" />
<script>
function guidGenerator() {
    var S4 = function() {
       return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
    };
    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
};

$(document).ready( function() {
    $('.delete-file').each( function() {
        $(this).click( function() {
            var listId = this.id.split("_")[1];
            var fileName = this.title;
            var srcDir = this.id.split("_")[2];
            
                      // Post the DELETE call.  
                      $.post('<c:url value="/deletefile"/>', { sourceDir: srcDir, filename: fileName } ,function(data){
                               if(data.status){                                    
                                   $('#item_'+srcDir+'_'+listId).hide();
                                }
                        }, 'json' );
                      
          
          return false;
        });
    });
    $( '#dialog').dialog({ autoOpen: false, modal: true });
    $('.hide-me').each( function() {
        $(this).click( function() {
            $('#downloadlinkcontainer').hide();
          return false;
        });
    });
    $('.display-history').each( function() {
        $(this).click( function() {
            $('#history-list').toggle('slow', function(){
                $('#history-list').css("width","100%");
            });
          return false;
        });
    });
    
 });


</script>
<div class="row">
  <div class="column grid_12">
    
    <div>
    <h2>
	Image Depot
	</h2>
	<p>
    <i>"Why is this here?"</i> The image depot helps serve up images when you're offline or you need to test different image sizes in your app.
    </p> 
    <c:choose>
        
        <c:when test="${not empty imageList}">
        
        <p>
        <c:set var="r" value="${pageContext.request}" />
        <!-- reference: http://stackoverflow.com/questions/3131063/how-can-i-create-an-absolute-url-in-a-jsp -->
            <table class="simple" style="width:100%;">
                <c:forEach var="imageItem" items="${imageList}" varStatus="count">
                
                    <c:url value="/filesysteminfo" var="filesysteminfoUrl">
                        <c:param name="filename" value="${imageItem.filename}" />
                    </c:url>
                    <c:url value="/viewme" var="viewItemUrl">
                        <c:param name="filename" value="${imageItem.filename}" />
                    </c:url>
                    <tr id="item_inDir_${count.index}" class="help_section" style="border: 1px inset #DDDDDD; padding:0px;margin:0px;">
                    
                      <td>
                      <div style="padding: 2px;">                      
                      <ul class="hoverbox">
						<li><a href="#"><img src="${viewItemUrl}" alt="${imageItem.filename}" /><img src="${viewItemUrl}" alt="description" class="preview" /></a></li>
					  </ul>
                      </td>
                      <td>
                      </div> 
                      <div><a href="${viewItemUrl}">${fn:replace(r.requestURL, r.requestURI, '')}${viewItemUrl}</a></div>
                      <div style="padding: 2px; font-size:70%;" class="blur" style="padding-top:5px;">${imageItem.sizeDesc} ${imageItem.lastModifiedDesc}</div> 
                      </td>
                      <td><a id="file_${count.index}_inDir" href="#" title="${imageItem.filename}" style="color: #FF0000;" class="delete-file tiny">delete</a></td>

                    </tr>
                </c:forEach>
            </table> 
        </p>
        
        </c:when>
        <c:otherwise>
        <p class="conflict_message">No images/files here.</p>
        </c:otherwise>
    </c:choose>
	    <div id="file-uploader-yeah">       
		    <noscript>          
		        <p>Please enable JavaScript to use file uploader.</p>
		        <!-- or put a simple form for upload here -->
		    </noscript>         
	    </div>
	    <script>        
	        function createUploader(){            
	            var uploader = new qq.FileUploader({
	                element: document.getElementById('file-uploader-yeah'),
	                action: '<c:url value="/uploadoctetstream"/>',
	                debug: true, 
	                onComplete: function (id, fileName, responseJSON) {
			            if (!responseJSON.success) {
			                alert(responseJSON.message);
			                return;
			            }
			            alert("Success. This page will reload in a second. ");
			            location.reload( true );
			            return;
			        }
	            });           
	        }
	        
	        // in your app create uploader as soon as the DOM is ready
	        // don't wait for the window to load  
	        window.onload = createUploader;     
	    </script>
    </div>
    
    <div id="downloadlinkcontainer" style="display:none;" class="help_info">
        <a style="float:right; color:red;" href="#" class="hide-me">x</a>
        <p id="downloadlinkcontainermsg"></p>
    </div>
    <div id="dialog" title="Basic dialog">
        <p>Processing...<span id="cancel_link_container"></span></p>
    </div>
    </div>
</div>
<jsp:include page="/WEB-INF/common/footer.jsp" />
