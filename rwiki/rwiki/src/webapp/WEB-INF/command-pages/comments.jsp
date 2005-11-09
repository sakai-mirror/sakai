<!-- To Turn Comments on, uncomment this 
<div class="rwiki_comments" >
<a href="#" onclick="ajaxRefPopup(this,'<c:out value="${renderBean.newCommentURL}" />',0); return false;" >Comment</a>
<c:forEach var="comment"
		  items="${renderBean.comments}" >
		 <div class="rwikicommentbody_<c:out value="${comment.commentLevel}" />">
		
	    <div class="rwikicommentheader">
	        Comment by: <c:out value="${comment.rwikiObject.user}" /> on <c:out value="${comment.rwikiObject.version}" /> 
        		<a href="#" onclick="ajaxRefPopup(this,'<c:out value="${comment.newCommentURL}" />',0); return false;" >Comment</a>
        		<c:if test="${comment.canEdit}" >
        			<a href="#" onclick="ajaxRefPopup(this,'<c:out value="${comment.editCommentURL}" />',0); return false;" >Edit</a>
        		</c:if>
        	</div>
		 <div class="rwikicomenttext" />
				<c:out value="${comment.renderedPage}" escapeXml="false"/><br/>	    
	      </div>
	    </div>
</c:forEach>
</div>
-->