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


 <c:url value="/act/download" var="fullDownloadUrl">

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
                <c:url value="/${model.id}" var="detailUrl"/>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>


<kc:prettyTime var="modelLastModified" date="${model.lastModified}" locale="${pageContext.response.locale}"/>

 <c:url value="${externalUrl}/act/download" var="selfExternalUrl">
            <!--${model.uuid} -->
            <c:param name="id" value="${model.uuid}"/>
        </c:url>

            <c:choose>
                <c:when test="${model.willBeRemoved}">
                    <c:set var="boxClass" value="panel-danger"/>
                </c:when>
                 <c:when test="${model.accessLevel == 'OWNER'}">
                    <c:set var="boxClass" value="panel-info"/>
                </c:when>
                <c:otherwise>
                    <c:set var="boxClass" value=""/>
                    
                </c:otherwise>
            </c:choose>          
          
            
<div class="box panel ${boxClass}" >
 
    <div class="panel-heading" style="margin:0;padding:0;border:0;">   <ul class="nav nav-tabs" >
            <li class="active">
                    <a href="#file_${model.id}" data-toggle="tab" ><span class="glyphicon glyphicon-file"></span> <fmt:message key="file.tab.file"/></a></li>
                <li>                        
        
                    <a href="#comments_${model.id}" data-toggle="tab" class="commentsBtn" modelId="${model.id}">
                        <span class="glyphicon glyphicon-comment"></span> <fmt:message key="file.tab.comments"/>
                        <c:if test="${model.commentsCount>0}">
                          (  <c:out value="${model.commentsCount}"/> )
                        </c:if>
                    </a>
                </li>
                <li><a href="#api_${model.id}" data-toggle="tab" >
                        <span class="glyphicon glyphicon-cog"></span> 
                        <fmt:message key="file.api.file"/></a></li>
                
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
                                    <span class="glyphicon glyphicon-edit"></span>
                                    <fmt:message key="button.edit"/></a>    
                            </li>

                            <c:if test="${empty param.integrationMode and ( model.owner eq currentUser or currentUser.admin )}">

                                <li>
                                    <a class="fileDeleteBtn"
                                       targetIcon="<c:url value='/main/static/${appVersion}/images/mime/${model.icon}'/>"
                                       targetTitle="${model.name} &nbsp; ${model.formattedFileSize} &nbsp; ${modelLastModified}  &nbsp; ${model.owner.name}"
                                       title="<fmt:message key="button.delete"/>"  
                                       deleteLink="<c:url value="/main/file/delete">
                                           <c:param name="id" value="${model.id}"/>
                                       </c:url>">
                                             <span class="glyphicon glyphicon-remove"></span>
                                <fmt:message key="button.delete"/></a>    
                                </li>

                            </c:if>

                        </sec:authorize>

                        <c:if test="${pasteEnable}">
                        
                              <li>
                                  
                                  <c:choose>
                                      <c:when test="${empty param.integrationMode}">
                                          
                                          <a class="pastePreviewBtn"
                                       targetIcon="<c:url value='/main/static/${appVersion}/images/mime/${model.icon}'/>"
                                       targetTitle="${model.name} &nbsp; ${model.formattedFileSize} &nbsp; ${modelLastModified}  &nbsp; ${model.owner.name}"
                                       targetId="${model.uuid}"
                                       title="<fmt:message key="paste.preview.title"/>"  
                                       >
                                        <img style="display: inline;"
                                           src="<c:url value='/main/static/${appVersion}/images/ninja.png'/>"/>
                                        <fmt:message key="paste.preview.title"/></a>    
                                          
                                      </c:when>
                                      <c:otherwise>
                                          
                                          <a href="${pasteUrl}/main/paste/loadFrom?url=${selfExternalUrl}"
                                            target="_blank" class="btn"
                                             title="<fmt:message key="paste.preview.title"/>" >
                                        <img style="display: inline;"
                                           src="<c:url value='/main/static/${appVersion}/images/ninja.png'/>"/>
                                        <fmt:message key="paste.preview.title"/></a>    
                                          
                                      </c:otherwise>
                                  </c:choose>
                                  
                                    
                                </li>
                        </c:if>
                                
                        <c:if test="${gdocsEnable}">

                            <li>
                                
                                 <c:choose>
                                     <c:when test="${empty param.integrationMode}">
                                
                                         <a title="<fmt:message key="file.gdocs.preview"/>"
                                   data-target="#gdocsPreviewModal_${model.id}" 
                                   class="btn" data-toggle="modal"
                                   >
                                    <img style="display: inline;" 
                                         src="<c:url value='/main/static/${appVersion}/images/mime/${model.icon}'/>"/> 
                                    <fmt:message key="file.gdocs.preview"/></a>    
                                         
                                     </c:when>
                                     <c:otherwise>
                                         
                                         <c:url value="http://docs.google.com/viewer" var="gdocsUrl">
                                             <c:param name="url" value="${selfExternalUrl}"/>
                                             <c:param name="embedded" value="true"/>        
                                         </c:url>

                                         <a title="<fmt:message key="file.gdocs.preview"/>"
                                            href="${gdocsUrl}" target="_blank"
                                            class="btn" 
                                            >
                                             <img style="display: inline;" 
                                                  src="<c:url value='/main/static/${appVersion}/images/mime/${model.icon}'/>"/> 
                                             <fmt:message key="file.gdocs.preview"/></a>    
                                         
                                     </c:otherwise>
                                 </c:choose>
                                
                            </li>
                        </c:if>        

                        <c:if test="${model.type eq 'PDF'}">
                            <li>
                                
                                 <c:choose>
                                     <c:when test="${empty param.integrationMode}">
                                
                                         <a title="<fmt:message key="file.pdf.preview"/>"
                                            data-target="#pdfPreviewModal_${model.id}" 
                                            class="btn" data-toggle="modal"
                                            >
                                             <img style="display: inline;" 
                                                  src="<c:url value='/main/static/${appVersion}/images/mime/pdf.gif'/>"/> 
                                             <fmt:message key="file.pdf.preview"/></a>    
                                         
                                     </c:when>
                                     <c:otherwise>
                                         
                                         <a title="<fmt:message key="file.pdf.preview"/>"
                                            target="_blank"
                                            class="btn" 
                                            href="<c:url value='/main/file/raw/pdfview'><c:param name='id' value='${model.id}'/></c:url>"
                                                >
                                                <img style="display: inline;" 
                                                     src="<c:url value='/main/static/${appVersion}/images/mime/pdf.gif'/>"/> 
                                            <fmt:message key="file.pdf.preview"/>
                                         </a>    
                                         
                                     </c:otherwise>
                                 </c:choose>
                                
                                
                            </li>

                        </c:if>        

                        <li>
                            <a title="<fmt:message key="button.download"/>" 
                               target="${menuTarget}" href="<c:out value='${fullDownloadUrl}'/>">
                                <span class="glyphicon glyphicon-download"></span>
                                <fmt:message key="file.download"/></a>    
                        </li>
                    </ul>
                </div>
            </c:if>

        </div>
                
            </ul></div>
    
    <div class="panel-body" style="margin:0;padding:0;border:0;">
   
        <div  class="tab-content" style="padding:1em;">
                <div class="tab-pane active" id="file_${model.id}">
                    
                       <div class="caption" style="vertical-align: top;" >
        
        
        <a href="<c:url value='/main/file/list/search?query=type:${model.type}'/>" target="${not empty param.integrationMode ? "_blank" : target}">
            <img style="text-align: left; display: inline; " src="<c:url value='/main/static/${appVersion}/images/mime/${model.icon}'/>"/>
            
        </a>
        
        <a href="<c:out value='${detailUrl}'/>" target="${target}">${model.name}</a>

       

        <div style="padding-left:10px;" >${model.formattedFileSize} 
            &nbsp; ${modelLastModified} 
            
             <a href="<c:url value='/main/file/list/search?query=ownerName:${model.owner.name}'/>"
                target="${not empty param.integrationMode ? "_blank" : target}">
                     <c:out value="${model.owner.name}"/>   
                </a>
            &nbsp;  
            <span style="display: block;font-size:smaller;"> 
            
                <a href="<c:url value='/main/file/list/search?query=accessLevel:${model.accessLevel.code}'/>" 
                   target="${not empty param.integrationMode ? "_blank" : target}">
                    <fmt:message key="${model.accessLevel.desc}"/>
                </a>
                
            </span> 
                
                    <c:if test="${model.willBeRemoved}">
                        <fmt:message key="list.removal.days">
                            <fmt:param value="${model.daysBeforeRemoval}"/>
                        </fmt:message> 
                        (<fmt:formatDate value="${model.removeAfter}" pattern="${dateTimePattern}"/> )
                    </c:if>   
                
                
        </div>
    </div>
        <div >
  
            <c:if test="${model.type eq 'VIDEO' and model.mime eq 'video/x-flv'}">
              
                <a class="embedPlayer" 
			 href="<c:url value="/act/download">
                                   <c:param name="id" value="${model.uuid}"/>                                     
                               </c:url>"> 
		</a> 
               
            </c:if>
            
            
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
                      <c:param name="inline" value="1"/>
                      <c:param name="fname" value="${model.name}"/>
            </c:url>

            
            <c:choose>

                <%--
                
                 min_height = 300;
                 min_width = 100;
                
                from ImageBuilder
                
                --%>

               
                
                <c:when test="${empty param.integrationMode and  model.previewWidth >100 and model.previewHeight>300}">
                    <c:set var="previewClass" value="zoombox w${model.previewWidth} h${model.previewHeight} zgallery1"/>
                </c:when>
                <c:otherwise>
                    <c:set var="previewClass" value=""/>        
                </c:otherwise>
            </c:choose>

            
            
            <a title="<fmt:message key="button.edit"/>" class="${previewClass}" target="${not empty param.integrationMode ? "_blank" : target}" href="<c:out value='${fullImgUrl}'/>">
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
                
                 <c:if test="${model.previewWidth >0 and model.previewHeight>0}">
                    <div>
                        <c:out value="${model.previewWidth} x ${model.previewHeight}"/>
                    </div>
                
                </c:if>
        </c:if>
    </div>

                    
                </div>
                    <div class="tab-pane" id="comments_${model.id}" >
                </div>
                
                
                   <div class="tab-pane" id="api_${model.id}" >
                    
                       <c:choose>
                           <c:when test="${not empty param.revision}">          
                               <c:url value="/main/file.json" var="jsonUrl">
                                   <c:param name="id" value="${model.id}"/>
                                   <c:param name="revision" value="${param.revision}"/>          
                               </c:url>
                               <c:url value="/main/file.xml" var="xmlUrl">
                                   <c:param name="id" value="${model.id}"/>
                                   <c:param name="revision" value="${param.revision}"/>          
                               </c:url>

                           </c:when>
                           <c:otherwise>
                               <c:url value="/main/file/${model.id}.xml" var="xmlUrl"/>
                               <c:url value="/main/file/${model.id}.json" var="jsonUrl"/>
                               
                           </c:otherwise>
                       </c:choose>
                       <p>

                           <a title="<fmt:message key="model.view.json"/>" target="_blank" 
                              href="<c:out value='${jsonUrl}'/>">
                               <img style="display: inline;" src="<c:url value='/main/static/${appVersion}/images/mime/json.png'/>"/>
                           </a>    

                           <a title="<fmt:message key="model.view.xml"/>" target="_blank" 
                              href="<c:out value='${xmlUrl}'/>">
                               <img style="display: inline;" src="<c:url value='/main/static/${appVersion}/images/mime/xml.gif'/>"/>
                           </a>    
                       </p>
                       <textarea  class="form-control" cols="40" rows="6"  ><iframe src="${externalUrl}/main/file/integrated/view?id=${model.id}" width="400" height="400" frameborder="0"></iframe>
                       </textarea>
                       
                </div>

            </div>
  </div>
    
</div>


<c:if test="${gdocsEnable and empty param.integrationMode}">

   
            <div id="gdocsPreviewModal_${model.id}" class="modal hide fade" tabindex="-1" style="width:650px;"
         role="dialog" aria-labelledby="googledocModalLabel" aria-hidden="true">
        <div class="modal-header">
            <img style="text-align: left; display: inline; " src="<c:url value='/main/static/${appVersion}/images/mime/${model.icon}'/>"/>
            <a href="<c:out value='${detailUrl}'/>" target="${target}">${model.name}</a>
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        </div>

      

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


<c:if test="${model.type eq 'PDF' and empty param.integrationMode}">

    <div id="pdfPreviewModal_${model.id}" class="modal  fade" tabindex="-1" style="width:680px;"
         role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-header">
            <img style="text-align: left; display: inline; " src="<c:url value='/main/static/${appVersion}/images/mime/${model.icon}'/>"/>
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
