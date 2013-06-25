<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<c:choose>
    <c:when test="${googleDocsEnabled and model.accessLevel eq 'ALL' and (model.type eq 'MSWORD' or model.type eq 'MSXLS')}">
        <c:set var="gdocsEnable" value="${true}"/>
    </c:when>
    <c:otherwise>
        <c:set var="gdocsEnable" value="${false}"/>
    </c:otherwise>
</c:choose>


<c:choose>
    <c:when test="${pasteIntegrationEnabled and model.accessLevel eq 'ALL' and (model.type eq 'CODE' or model.type eq 'TEXT')}">
        <c:set var="pasteEnable" value="${true}"/>
    </c:when>
    <c:otherwise>
        <c:set var="pasteEnable" value="${false}"/>
    </c:otherwise>
</c:choose>


<c:choose>
    <c:when test="${not empty param.targetNew}">
        <c:set var="target" value="_blank"/>
    </c:when>
    <c:otherwise>
        <c:set var="target" value="_self"/>        
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${not empty param.menuTargetNew}">
        <c:set var="menuTarget" value="_blank"/>
    </c:when>
    <c:otherwise>
        <c:set var="menuTarget" value="_self"/>        
    </c:otherwise>
</c:choose>


<c:choose>
    <c:when test="${not empty param.downloadLink}">

        <c:if test="${param.downloadLink eq 'download'}">

            <c:url value="/act/download" var="detailUrl">

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


        </c:if>
        <c:if test="${param.downloadLink eq 'edit'}">

            <c:url value="/main/file/edit/${model.id}" var="detailUrl"/>

        </c:if>


    </c:when>
    <c:otherwise>

        <c:choose>
            <c:when test="${not empty param.integrationMode}">
                <c:url value="/main/file/integrated/view" var="detailUrl">
                    <c:param name="id" value="${model.id}"/>
                    <c:param name="integrationMode" value="${param.integrationMode}"/>
                </c:url>
            </c:when>
            <c:otherwise>
                <c:url value="/main/file/${model.id}" var="detailUrl"/>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>


<kc:prettyTime var="modelLastModified" date="${model.lastModified}" locale="${pageContext.response.locale}"/>


<div class="box well" >
    <div class="caption" style="vertical-align: top;" >
        <img style="text-align: left; display: inline; " src="<c:url value='/images/mime/${model.icon}'/>"/>
        <a href="<c:out value='${detailUrl}'/>" target="${target}">${model.name}</a>

        <div class="pull-right">

            <c:if test="${empty param.detail}">

                <div class="btn-group pull-right">
                    <a class="btn dropdown-toggle " data-toggle="dropdown" href="#">
                        <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu">

                        <sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_MANAGER,ROLE_USER">

                            <li>
                                <a title="<fmt:message key="button.edit"/>" target="${menuTarget}" 
                                   href="<c:url value="/main/file/edit/${model.id}"/>">
                                    <img style="display: inline;" 
                                         src="<c:url value='/images/edit.png'/>"/>
                                    <fmt:message key="button.edit"/></a>    
                            </li>

                            <c:if test="${model.owner eq currentUser or currentUser.admin}">

                                <li>
                                    <a class="fileDeleteBtn"
                                       targetIcon="<c:url value='/images/mime/${model.icon}'/>"
                                       targetTitle="${model.name} &nbsp; ${model.formattedFileSize} &nbsp; ${modelLastModified}  &nbsp; ${model.owner.name}"
                                       title="<fmt:message key="button.delete"/>"  
                                       deleteLink="<c:url value="/main/file/delete">
                                           <c:param name="id" value="${model.id}"/>
                                       </c:url>"><img style="display: inline;"
                                           src="<c:url value='/images/delete.png'/>"/> <fmt:message key="button.delete"/></a>    
                                </li>

                            </c:if>

                        </sec:authorize>

                        <c:if test="${pasteEnable}">
                        
                              <li>
                                    <a class="pastePreviewBtn"
                                        targetIcon="<c:url value='/images/mime/${model.icon}'/>"
                                       targetTitle="${model.name} &nbsp; ${model.formattedFileSize} &nbsp; ${modelLastModified}  &nbsp; ${model.owner.name}"
                                      targetId="${model.uuid}"
                                       title="<fmt:message key="paste.preview.title"/>"  
                                       >
                                        <img style="display: inline;"
                                           src="<c:url value='/images/ninja.png'/>"/>
                                        <fmt:message key="paste.preview.title"/></a>    
                                </li>
                        </c:if>
                                
                        <c:if test="${gdocsEnable}">

                            <li>
                                <a title="<fmt:message key="file.gdocs.preview"/>"
                                   data-target="#gdocsPreviewModal_${model.id}" 
                                   class="btn" data-toggle="modal"
                                   >
                                    <img style="display: inline;" 
                                         src="<c:url value='/images/mime/${model.icon}'/>"/> 
                                    <fmt:message key="file.gdocs.preview"/></a>    
                            </li>
                        </c:if>        

                        <c:if test="${model.type eq 'PDF'}">
                            <li>
                                <a title="<fmt:message key="file.pdf.preview"/>"
                                   data-target="#pdfPreviewModal_${model.id}" 
                                   class="btn" data-toggle="modal"
                                   >
                                    <img style="display: inline;" 
                                         src="<c:url value='/images/mime/pdf.gif'/>"/> 
                                    <fmt:message key="file.pdf.preview"/></a>    
                            </li>

                        </c:if>        

                        <li>
                            <a title="<fmt:message key="button.download"/>" 
                               target="${menuTarget}" href=" <c:url value="/act/download">
                                   <c:param name="id" value="${model.uuid}"/>
                               </c:url>">

                                <img style="display: inline;" src="<c:url value='/images/download.png'/>"/> <fmt:message key="file.download"/></a>    
                        </li>
                    </ul>
                </div>
            </c:if>

        </div>

        <div style="padding-left:10px;" >${model.formattedFileSize} 
            &nbsp; ${modelLastModified} 
            &nbsp;   ${model.owner.name}
            <span style="display: block;font-size:smaller;"> 
                <fmt:message key="${model.accessLevel.desc}"/></span> 
        </div>
    </div>
        <div >

        <c:if test="${model.containsPreview}">

            <c:url value='/act/download' var="fullImgUrl">
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

            <c:choose>

                <%--
                
                 min_height = 300;
                 min_width = 100;
                
                from ImageBuilder
                
                --%>

                <c:when test="${model.previewWidth>100 and model.previewHeight>300}">
                    <c:set var="previewClass" value="zoombox w${model.previewWidth} h${model.previewHeight}"/>
                </c:when>
                <c:otherwise>
                    <c:set var="previewClass" value=""/>        
                </c:otherwise>
            </c:choose>


            <a title="<fmt:message key="button.edit"/>" class="${previewClass}" target="${target}" href="<c:out value='${fullImgUrl}'/>">
                <img style="border: none;"   src="<c:url value='/act/download'>
                         <c:choose>
                             <c:when test="${not empty param.revision}">          
                                 <c:param name="id" value="${model.id}"/>
                                 <c:param name="revision" value="${param.revision}"/>          
                             </c:when>
                             <c:otherwise>
                                 <c:param name="id" value="${model.uuid}"/>

                             </c:otherwise>
                         </c:choose>
                         <c:param name="preview" value="1"/>

                     </c:url>"/>
            </a>
        </c:if>
    </div>

</div>


<c:if test="${gdocsEnable}">

    <div id="gdocsPreviewModal_${model.id}" class="modal hide fade" tabindex="-1" style="width:650px;"
         role="dialog" aria-labelledby="googledocModalLabel" aria-hidden="true">
        <div class="modal-header">
            <img style="text-align: left; display: inline; " src="<c:url value='/images/mime/${model.icon}'/>"/>
            <a href="<c:out value='${detailUrl}'/>" target="${target}">${model.name}</a>
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        </div>

        <c:url value="${externalUrl}/act/download" var="selfExternalUrl">
            <!--${model.uuid} -->
            <c:param name="id" value="${model.uuid}"/>
        </c:url>

        <c:url value="http://docs.google.com/viewer" var="gdocsUrl">
            <c:param name="url" value="${selfExternalUrl}"/>
            <c:param name="embedded" value="true"/>        
        </c:url>

        <div class="modal-body" style="height:460px;overflow: hidden;" >
            <iframe  src="${gdocsUrl}"
                     scrolling="auto" frameborder="0"
                     style="width:630px;height: 400px;"  allowTransparency="true"   >
            </iframe>     
        </div>
    </div>

</c:if>


<c:if test="${model.type eq 'PDF'}">

    <div id="pdfPreviewModal_${model.id}" class="modal hide fade" tabindex="-1" style="width:680px;"
         role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-header">
            <img style="text-align: left; display: inline; " src="<c:url value='/images/mime/${model.icon}'/>"/>
            <a href="<c:out value='${detailUrl}'/>" target="${target}">${model.name}</a>
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        </div>
        <div class="modal-body" style="height:460px;overflow: hidden;" >
            <iframe  src="<c:url value='/main/file/raw/pdfview'><c:param name='id' value='${model.id}'/></c:url>"
                     scrolling="auto" frameborder="0"
                     style="width:660px;height: 400px;"  allowTransparency="true"   >
                </iframe>     
            </div>
        </div>

</c:if>
