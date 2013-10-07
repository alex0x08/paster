<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<jsp:include
    page="/WEB-INF/jsp/templates/common/breadcrumb.jsp">
    <jsp:param name="model" value="${model}" />
    <jsp:param name="modelName" value="file" />
</jsp:include>

<div class="row">  
    <div class="col-xs-4 col-md-10">

        <c:if test="${not empty statusMessageKey}">
            <p><fmt:message key="${statusMessageKey}"/></p>
        </c:if>

            <c:url value="/act/download" var="downloadUrl">

                <c:choose>
                    <c:when test="${not empty param.revision}">          
                        <c:param name="id" value="${model.id}"/>
                        <c:param name="revision" value="${param.revision}"/>          
                    </c:when>
                    <c:otherwise>
                        <c:param name="id" value="${model.uuid}"/>
                    </c:otherwise>
                </c:choose>
            </c:url>


        <div class="form-row pull-left">
            <label for="file"><fmt:message key="file.file.uploaded"/>:</label>

            <span class="input">
                <jsp:include page="/WEB-INF/jsp/templates/common/preview.jsp" >
                    <jsp:param name="downloadLink" value="edit" />
                </jsp:include>
            </span>
        </div>        

         <c:if test="${!model.blank and not empty availableRevisions}">

         <div class="form-row pull-left">         
             <label for="file"><fmt:message key="struct.versions"/>:</label>
             <span class="input" style="padding-left:1em;">
                      <jsp:include
                        page="/WEB-INF/jsp/templates/common/revisions.jsp">
                        <jsp:param name="modelName" value="file" />
                    </jsp:include>
            </span>             
         </div>  

         </c:if>

          
         
         <c:if test="${!model.blank and pasteIntegrationEnabled and model.pasterIntegrated}">

         <div class="form-row pull-left">
             <label for="file">Integrated with paste :</label>
               <span class="input" style="padding-left:1em;">
                   <iframe  src="${pasteUrl}/main/paste/integrated/preview/${model.pasterId}"
                     scrolling="auto" frameborder="0"
                     style="width:630px;height: 400px;"  allowTransparency="true"   >
                    </iframe>  
            </span>
             
         </div>   

         </c:if>
     
            
        <div class="form-buttons pull-left">

            <c:if test="${not empty availablePrev}">
                <a class="btn" href="<c:url value='/${model.id-1}'/>">&#8592;</a>
            </c:if>

            <a class="btn btn-primary btn-large" href="<c:out value='${downloadUrl}'/>">
                <img style="display: inline;" src="<c:url value='/main/static/${appVersion}/images/download.png'/>"/>
                <fmt:message key="file.download"/></a>

            <c:if test="${not empty availableNext}">
                <a class="btn" href="<c:url value='/${model.id+1}'/>">&#8594;</a>
            </c:if>

        </div>

    </div>

</div>       


<jsp:include
    page="/WEB-INF/jsp/templates/common/comments.jsp">
    <jsp:param name="model" value="${model}" />
    <jsp:param name="modelName" value="file" />
    <jsp:param name="addComment" value="1" />
<jsp:param name="currentUser" value="${currentUser}" />
  
</jsp:include>



