<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<jsp:include
    page="/WEB-INF/jsp/templates/common/breadcrumb.jsp">
    <jsp:param name="model" value="${model}" />
    <jsp:param name="modelName" value="file" />
</jsp:include>


<c:url var="blockUrl" value='/main/file/raw/view' />

<script src="<c:url value='/js/vendor/jquery.ui.widget.js'/>"></script>

<div class="row">
    <div class="span10">     



        <c:if test="${not empty param.statusMessageKey}">
            <p>
                <fmt:message key="${param.statusMessageKey}" />
            </p>
        </c:if>


        <c:choose>
            <c:when test="${model.blank}">


                <script src="<c:url value='/js/jquery.fileupload.js'/>"></script>
                <script src="<c:url value='/js/jquery.fileupload-ui.js'/>"></script>
                <script src="<c:url value='/js/jquery.fileupload-fp.js'/>"></script>


                <c:url var="url" value='/main/file/upload-xdr' />

                <script type="text/javascript">

                    $(document).ready(function() {

                        $('#file_upload').fileupload({
                            dataType: 'json',
                            progressall: function(e, data) {

                                var progress = parseInt(data.loaded / data.total * 100, 10);
                                $('.progress .bar').css(
                                'width',
                                progress + '%'
                            );
                                console.log(progress + '%');
           
                                            
                            }, done: function(e, data) {
                                try {

                                    console.log(data);

                                    if (typeof data.result['error'] == "undefined") {
                                        /*
                                        $("#messages").append('<div class="alert alert-success"><button type="button" class="close" data-dismiss="alert">&times;</button>File ' + data.result['name'] + ' uploaded sucessfully</div>');
                                         */
                                        $.get("${blockUrl}?id=" + data.result['id'], function(data) {
                                            $("#gallery").append(data);
                                        });
                                    } else {
                                        $("#messages").append('<div class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button>File ' + data.result['name'] + ' failed to upload!</div>');
                                    }
                                } catch (e) {
                                    alert(e.message);

                                }
                            }, error: function(data) {
                                console.log(data);

                            }


                        });
                    });

                </script>       

            </c:when>
            <c:otherwise>

                <script src="<c:url value='/libs/jasny-bootstrap/js/jasny-bootstrap.js'/>"></script>


                <c:url var="url" value='/main/file/save' />
            </c:otherwise>
        </c:choose>



        <form:form action="${url}" modelAttribute="model" 
                   method="POST" id="file_upload" cssClass="form-horizontal"
                   enctype="multipart/form-data">


            <fieldset>
                <form:errors path="*" cssClass="errorblock" element="div"/>


                <c:if test="${not empty model.integrationCode}">

                    <c:out value="Used integration code: ${model.integrationCode}" escapeXml="true" />
                    <form:hidden path="integrationCode" />

                </c:if>

                <c:choose>
                    <c:when test="${model.blank}">

                        <form:hidden path="url" />
                        <form:hidden path="name" />

                    </c:when>
                    <c:otherwise>
                        <form:hidden path="id" />

                    </c:otherwise>
                </c:choose>

                  <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">

                    <c:if test="${model.blank or (model.owner eq currentUser or currentUser.admin)}">

                        <form:label cssClass="control-label" path="accessLevel"><fmt:message key="file.accessLevel"/>:</form:label>

                        <div class="controls">
                            <c:choose>
                                <c:when test="${not empty model.integrationCode}">
                                    <c:set var="accessLevelDisabled" value="true"/>

                                </c:when>
                                <c:otherwise>
                                    <c:set var="accessLevelDisabled" value="false"/>                                    
                                </c:otherwise>

                            </c:choose>

                            <form:select id="accessLevel" path="accessLevel" disabled="${accessLevelDisabled}">

                                <c:forEach items="${model.accessLevel.levels}" var="level">
                                    <form:option value="${level.code}">
                                        <fmt:message key="${level.desc}" />
                                    </form:option>
                                </c:forEach>
                            </form:select>

                            <c:if test="${not empty model.integrationCode}">
                                File integrated, cannot set access level.
                            </c:if>

                            <form:errors path="accessLevel" cssClass="error" />
                        </div>
                        </c:if>
                         </sec:authorize>

                <c:choose>
                    <c:when test="${model.blank}">
                        <div class="control-group">
                            <form:label cssClass="control-label" path="file"><fmt:message key="file.file.new"/>:</form:label>
                            <div class="controls">
                                <form:input path="file" name="file" 
                                            type="file" /> 
                                <form:errors path="file" cssClass="error" />
                                <div class="fileupload-progress" style="height:2em;">
                                    <div class="progress progress-success progress-striped active" 
                                         role="progressbar" aria-valuemin="0" aria-valuemax="100">
                                        <div class="bar" style="width:0%;"></div>
                                    </div>
                                </div>
                            </div>      
                        </div>

                    </c:when>
                    <c:otherwise>
                        <div class="control-group">
                            <form:label cssClass="control-label" path="file"><fmt:message key="file.file.uploaded"/>:</form:label>
                            <div class="controls">
                                <jsp:include
                                    page="/WEB-INF/jsp/templates/common/preview.jsp">
                                    <jsp:param name="detail" value="1" />
                                    <jsp:param name="downloadLink" value="download" />
                                </jsp:include>
                            </div>
                        </div>

                        <div class="control-group">
                            <form:label cssClass="control-label" path="file">
                                <fmt:message key="file.file.replace"/>:</form:label>
                                <div class="controls">
                                    <div class="fileupload fileupload-new" data-provides="fileupload">
                                        <span class="btn btn-file">
                                            <span class="fileupload-new"><fmt:message key="button.select"/></span>
                                        <form:input path="file" name="file" cssClass="input-file"
                                                    type="file" /> 
                                        <span class="fileupload-exists"><fmt:message key="button.change"/></span>
                                    </span>
                                    <span class="fileupload-preview"></span>
                                    <a href="#" class="close fileupload-exists" data-dismiss="fileupload" style="float: none">×</a>
                                </div>
                                <form:errors path="file" cssClass="error" />
                            </div>      
                        </div>

                    </c:otherwise>
                </c:choose>


              

                        <c:if test="${!model.blank and not empty availableRevisions}">

                            <form:label cssClass="control-label" path="accessLevel">
                                <fmt:message key="struct.versions"/>:</form:label>

                                <div class="controls">
                                <jsp:include
                                    page="/WEB-INF/jsp/templates/common/revisions.jsp">
                                    <jsp:param name="modelName" value="file" />
                                </jsp:include>
                            </div>
                        </c:if>
                        <c:if test="${!model.blank}">
                            <div class="form-actions">
                                <c:choose>
                                    <c:when test="${model.blank}">
                                        <fmt:message var="submit_button_text" key="button.upload" />
                                        <input name="submit" class="btn btn-primary" 
                                               type="submit" value="${submit_button_text}" />
                                    </c:when>
                                    <c:otherwise>
                                        <a href="<c:url value='/main/file/${model.id}'/>" class="btn" >&larr; <fmt:message key="button.back"/></a>
                                        
                                        <fmt:message var="submit_button_text" key="button.save" />

                                        
                                          <c:if test="${model.accessLevel eq 'OWNER' or model.owner eq currentUser or currentUser.admin}">
                                     
                                               <input name="delete" type="submit" class="btn btn-danger fileDeleteBtn"
                                               targetIcon="<c:url value='/images/mime/${model.icon}'/>"
                                               targetTitle="${model.name} &nbsp; ${model.formattedFileSize} &nbsp; ${modelLastModified}  &nbsp; ${model.owner.name}"

                                               deleteLink="<c:url value="/main/file/delete">
                                                   <c:param name="id" value="${model.id}"/>
                                               </c:url>"
                                               value="<fmt:message key="button.delete"/>" />
                                              
                                          </c:if>
                                       
                                        
                                        <input name="submit" type="submit" class="btn btn-large btn-primary" value="${submit_button_text}" />
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>
                        <jsp:include
                            page="/WEB-INF/jsp/templates/common/comments.jsp">
                            <jsp:param name="model" value="${model}" />
                            <jsp:param name="modelName" value="file" />
                        </jsp:include>
                    
               
            </fieldset>
        </form:form>

    </div>

</div>

<div class="row">
    <div id="messages" style="well">
    </div>

    <div id="gallery">
    </div>

</div>

