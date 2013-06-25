<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<c:url var="blockUrl" value='/main/file/raw/view' />

<script src="<c:url value='/js/vendor/jquery.ui.widget.js'/>"></script>

<script src="<c:url value='/js/jquery.fileupload.js'/>"></script>
<script src="<c:url value='/js/jquery.fileupload-ui.js'/>"></script>
<script src="<c:url value='/js/jquery.fileupload-fp.js'/>"></script>

<div class="row">
    <div class="span10">     



        <c:if test="${not empty param.statusMessageKey}">
            <p>
                <fmt:message key="${param.statusMessageKey}" />
            </p>
        </c:if>


        <c:choose>
            <c:when test="${model.blank}">
                <c:url var="url" value='/main/file/upload-xdr' />

                <script type="text/javascript">

                    $(document).ready(function(){
                            
                        $('#file_upload').fileupload({
                            dataType: 'json',
                            progressall: function (e, data) {
         
                                var progress = parseInt(data.loaded / data.total * 100, 10);
                                $('.progress .bar').css(
                                'width',
                                progress + '%'
                            );
                            }, done: function (e, data) {
                                try {
        
                                    console.log(data);
            
                                    if(typeof data.result['error'] == "undefined") {
                                        $("#messages").append('<div class="alert alert-success"><button type="button" class="close" data-dismiss="alert">&times;</button>File '+data.result['name']+' uploaded suce        ssfully</div>');
                                        $.get("${blockUrl}?id="+data.result['id'], function (data) {
                                            $("#gallery").append(data);
                                        });
                                    } else {
                                        $("#messages").append('<div class="alert alert-error"><button type="button" class="close" data-dismiss="alert">&times;</button>File '+data.result['name']+' failed to upload!</div>');
                                    }
                                } catch (e) {
                                    alert(e.message);
        
                                }
                            }, error: function (data) {
                                console.log(data);

                            }


                        });
                    });

                </script>       

            </c:when>
            <c:otherwise>
                <c:url var="url" value='/main/file/save' />
            </c:otherwise>
        </c:choose>



        <form:form action="${url}" modelAttribute="model" 
                   method="POST" id="file_upload" cssClass="form-horizontal"
                   enctype="multipart/form-data">


            <fieldset>
                <form:errors path="*" cssClass="errorblock" element="div"/>

               
                <c:choose>
                    <c:when test="${not empty param.integrationCode}">
                        
                        <c:out value="Using integration code: ${param.integrationCode}" escapeXml="true" />
                
                        
                         <input name="integrationCode" type="hidden" 
                                           value="${param.integrationCode}" />

                        
                    </c:when>
                        <c:otherwise>
                        <form:hidden path="integrationCode" />
                            
                        </c:otherwise>
                </c:choose>
                
                <c:choose>
                    <c:when test="${model.blank}">

                        <form:hidden path="url" />
                        <form:hidden path="name" />

                    </c:when>
                    <c:otherwise>
                        <form:hidden path="id" />
                    </c:otherwise>
                </c:choose>


                <c:choose>
                    <c:when test="${model.blank}">

                        <div class="control-group">

                            <form:label cssClass="control-label" path="file"><fmt:message key="file.file.new"/>:</form:label>

                                <div class="controls">
                                <form:input path="file" name="file" cssClass="input-file"
                                            type="file" /> 
                                <form:errors path="file" cssClass="error" />
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
                                   
                                </jsp:include>

                            </div>
                        </div>

                        <div class="control-group">

                            <form:label cssClass="control-label" path="file"><fmt:message key="file.file.new"/>:</form:label>

                                <div class="controls">
                                <form:input path="file" name="file" cssClass="input-file"
                                            type="file" /> 
                                <form:errors path="file" cssClass="error" />
                            </div>      
                        </div>

                    </c:otherwise>
                </c:choose>


                <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">

                    <c:if test="${model.blank or (model.owner eq currentUser or currentUser.admin)}">

                        <form:label cssClass="control-label" path="accessLevel"><fmt:message key="file.accessLevel"/>:</form:label>

                            <div class="controls">
                            <form:select id="accessLevel"path="accessLevel">

                                <c:forEach items="${model.accessLevel.levels}" var="level">
                                    <form:option value="${level.code}">
                                        <fmt:message key="${level.desc}" />
                                    </form:option>
                                </c:forEach>
                            </form:select>
                            <form:errors path="accessLevel" cssClass="error" />
                        </div>


                        <span class="span8">

                            <c:if test="${!model.blank}">

                                <jsp:include
                                    page="/WEB-INF/jsp/templates/common/revisions.jsp">
                                    <jsp:param name="modelName" value="file" />
                                </jsp:include>
                              

                            </c:if>

                        </span>

                        <div class="form-actions">

                            <c:choose>
                                <c:when test="${model.blank}">
                                    <fmt:message var="submit_button_text" key="button.upload" />

                                    <noscript>    
                                    <input name="submit" class="btn btn-primary" type="submit" value="${submit_button_text}" />
                                    </noscript>

                                </c:when>
                                <c:otherwise>
                                    <input name="cancel" type="submit" class="btn"
                                           value="<fmt:message key="button.back"/>" />

                                    <fmt:message var="submit_button_text" key="button.save" />
                                    <input name="delete" type="submit" class="btn"
                                           value="<fmt:message key="button.delete"/>" />

                                    <input name="submit" type="submit" class="btn btn-primary" value="${submit_button_text}" />

                                </c:otherwise>
                            </c:choose>

                        </div>


                    </c:if>

                </sec:authorize>


            </fieldset>


        </form:form>

    </div>

</div>

<div class="row">

    <div class="span5 fileupload-progress fade">
        <!-- The global progress bar -->
        <div class="progress progress-success progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100">
            <div class="bar" style="width:0%;"></div>
        </div>
        <!-- The extended global progress information -->
        <div class="progress-extended">&nbsp;</div>
    </div>

    <div class="fileupload-loading"></div>


    <div id="messages" style="well">
    </div>


    <div id="gallery">
    </div>


</div>

