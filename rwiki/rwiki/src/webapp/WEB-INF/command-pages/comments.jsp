<!-- To Turn Comments on, uncomment this
<a href="#" onclick="ajaxRefPopup(this,'<c:out value="${renderBean.newCommentURL}" />',0); return false;" >Comment</a>
<div id="rwiki_comments" >
<c:forEach var="comment"
		  items="${renderBean.comments}" >
	    <div class="rwikiCommentsHeader">
	        Comment by: <c:out value="${comment.rwikiObject.user}" /> on <c:out value="${comment.rwikiObject.version}" /> 
        		<a href="#" onclick="ajaxRefPopup(this,'<c:out value="${comment.newCommentURL}" />',0); return false;" >Comment</a>
        		<c:if test="${comment.canEdit}" >
        			<a href="#" onclick="ajaxRefPopup(this,'<c:out value="${comment.editCommentURL}" />',0); return false;" >Edit</a>
        		</c:if>
		 <div class="rwikiCommentBody_<c:out value="${comment.commentLevel}" />">
				<c:out value="${comment.renderedPage}" escapeXml="false"/><br/>	    
	      </div>
	    </div>
</c:forEach>
</div>
-->