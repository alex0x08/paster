<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<jsp:include
    page="/WEB-INF/jsp/templates/common/breadcrumb.jsp">
    <jsp:param name="model" value="${model}" />
    <jsp:param name="modelName" value="file" />
</jsp:include>


<c:url var="blockUrl" value='/main/file/raw/view' />


<script src="<c:url value='/main/assets/${appVersion}/jquery-ui/1.10.2/ui/minified/jquery.ui.widget.min.js'/>"></script>

<link href="<c:url value='/main/assets/${appVersion}/jquery-file-upload/8.4.2/css/jquery.fileupload-ui.css'/>" rel="stylesheet"/>
<link href="<c:url value='/main/assets/${appVersion}/bootstrap-datepicker/1.1.3/css/datepicker.css'/>" rel="stylesheet"/>
<link href="<c:url value='/main/static/${appVersion}/libs/bootstrap-switch/stylesheets/bootstrap-switch.css'/>" rel="stylesheet"/>

 

 <c:choose>
            <c:when test="${model.blank}">

                <script src="<c:url value='/main/assets/${appVersion}/jquery-file-upload/8.4.2/js/jquery.fileupload.js'/>"></script>
                <script src="<c:url value='/main/assets/${appVersion}/jquery-file-upload/8.4.2/js/jquery.fileupload-process.js'/>"></script>
                <script src="<c:url value='/main/assets/${appVersion}/jquery-file-upload/8.4.2/js/jquery.fileupload-ui.js'/>"></script>
              
                <script src="<c:url value='/main/static/${appVersion}/libs/bootstrap-switch/js/bootstrap-switch.js'/>"></script>
                <script src="<c:url value='/main/assets/${appVersion}/bootstrap-datepicker/1.1.3/js/bootstrap-datepicker.js'/>"></script>

                <c:url var="url" value='/main/file/upload-xdr' />

                <c:set var="mainBlockStyle" value="col-md-3"/>
                
                <script type="text/javascript">

                    $(document).ready(function() {
                              
                  $('#file_upload').fileupload({
                            dataType: 'json',
                            autoUpload: true,
                            dropZone: $('#dropzone'),
                            
                             drop: function (e, data) {
                                $.each(data.files, function (index, file) {
        
                                console.log('Selected file: ' + file.name);
                               /* $('#progress').css('display','');*/    
                                });
                            },
                            change: function (e, data) {
                                $.each(data.files, function (index, file) {
                                    console.log('Selected file: ' + file.name);
                                  /*  $('#progress').css('display','');*/
                                });
                    },progressall: function(e, data) {
                                var progress = parseInt(data.loaded / data.total * 100, 10);
                              /*  $('#progress .progress-bar').css('width',progress + '%');*/
                                console.log(progress + '%');
                    }, done: function(e, data) {
                            /*$('#progress').css('display','none'); 
                            $('#progress .progress-bar').css('width', '0');
                              */  
                                try {
                                    /*console.log(data);*/
                                    
                                    if (typeof data.result['error'] == "undefined") {
                                       
                                        $.get("${blockUrl}?id=" + data.result['id'], function(data) {
                                            $("#gallery").prepend(data);
                                            
                                            
                                       initZoombox();
                                       initFlowPlayer();
                                       initButtons();
        
                                        });
                                    } else {
                                        showError('File ' + data.result['name'] + ' failed to upload!');
                                    }
                                } catch (e) {
                                    showError(e.message);
                                }
                            }, error: function(data) {
                                console.log(data);
                                try {
                                showError(data.responseText); } catch (e) {showError(data);}
                            }
                        });              
                   
                     $('#enableRemovalSwitch').on('change', function (e) {
                           
                           if (this.checked) {
                                $('#inputRemovalDateBlock').css('display','');
                           } else {
                                $('#inputRemovalDateBlock').css('display','none'); 
                                $('#removeAfter').val('');
                            }                             
                            
                        });


                        var nowTemp = new Date();
                        var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

                        var removalPicker =$('#inputRemovalDateBlock').datepicker({
                          
                          startDate: now,  
                          
                        }).on('changeDate', function(ev) {
                            removalPicker.hide();
                        }).data('datepicker');


                       
     

                    });

                </script>       

            </c:when>
            <c:otherwise>
                <c:set var="mainBlockStyle" value="col-md-12"/>
                
                <c:url var="url" value='/main/file/save' />
                
                  <script type="text/javascript">
                      
                      
                       $(document).ready(function() {
                              
                              $('#fileSelectBtn').bind('change', function() {
                                  
                                  $('#fileSelectLabel').text($(this).val());
                              });
                       
                    });
                      
                  </script>
                
            </c:otherwise>
        </c:choose>

 <div class="row">
    <div class="${mainBlockStyle} col-md-offset-1">     

                 <form:form action="${url}" modelAttribute="model" id="file_upload"
                   method="POST"  cssClass="form-horizontal"
                   enctype="multipart/form-data">
                    
                     <c:choose>
                         <c:when test="${model.blank}">
                             <form:hidden path="url" />
                             <form:hidden path="name" />
                         </c:when>
                         <c:otherwise>
                             <form:hidden path="id" />
                         </c:otherwise>
                     </c:choose>  
                     
                     
                     <div class="form-group">
                         <div class="controls">
                             <form:errors cssClass="errorblock" element="div"/>

                             <c:if test="${not empty model.integrationCode}">
                                 <c:out value="Used integration code: ${model.integrationCode}" escapeXml="true" />
                                 <form:hidden path="integrationCode" />
                             </c:if>
                         </div>
                     </div> 


                             <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">

                                 <c:if test="${model.blank or (model.owner eq currentUser or currentUser.admin)}">


                                     <div class="form-group">
                                         <form:label  path="accessLevel"><fmt:message key="file.accessLevel"/>:</form:label>
                                             <div >

                                             <c:choose>
                                                 <c:when test="${not empty model.integrationCode}">
                                                     <c:set var="accessLevelDisabled" value="true"/>
                                                 </c:when>
                                                 <c:otherwise>
                                                     <c:set var="accessLevelDisabled" value="false"/>                                    
                                                 </c:otherwise>

                                             </c:choose>

                                       
                                                 
                                                 <div class="btn-group" data-toggle="buttons">

                                                 <c:forEach items="${model.accessLevel.levels}" var="level">
                                                     <label class="btn btn-default" >
                                                         <fmt:message key="${level.desc}" />
                                                         <form:radiobutton path="accessLevel" value="${level.code}"  />
                                                     </label>

                                                 </c:forEach>


                                             </div>    


                                             <c:if test="${not empty model.integrationCode}">
                                                 File integrated, cannot set access level.
                                             </c:if>

                                             <form:errors path="accessLevel" cssClass="error" />

                                         </div>
                                     </div>


                                          

                                 </c:if>
                                 
                                    <c:if test="${model.blank}">


                                     <div class="form-group">
                                         <form:label  path="file"><fmt:message key="file.removeAfter"/>:</form:label>
                                             <div class="controls">
                                                   
                                             <div class="switch" >
                                                 <input id='enableRemovalSwitch' type="checkbox"  >
                                             </div>
                                             
                                            
                                                 <div id='inputRemovalDateBlock'  class="date" data-date="" data-date-format="${datePatternPicker}" 
                                                  style="display:none;padding-top: 0.5em;max-width: 7em;">
                                                 <form:input id="removeAfter" path="removeAfter" name="file" cssClass="form-control" size="10"  /> 
                                                 <span class="add-on"><i class="icon-th"></i></span>
                                             </div>      

                                              
                                                
                                               <form:errors path="removeAfter" cssClass="error" />

                                             </div>

                                     </div>   
                                             </c:if>
                                 
                             </sec:authorize>  
          
                             
                <c:choose>
                    <c:when test="${model.blank}">

                        <div class="form-group">
                           
                                
                                 <!-- The fileinput-button span is used to style the file input field as button -->
                                    <span class="btn btn-success fileinput-button">
                                        <i class="glyphicon glyphicon-plus"></i>
                                        <span><fmt:message key="button.select-upload"/></span>
                                        <form:input path="file" name="file" 
                                                type="file" /> 
                                </span>

                                <form:errors path="file" cssClass="error" />

                                <div id="progress" class="progress" style="display:none;">
                                    <div class="progress-bar progress-bar-success"></div>
                                </div>
                                                           
                            
                             
                            </div>
                       


                    </c:when>
                    <c:otherwise>
                        
                        <div class="form-group">
                            <div class="col-lg-10">
                                <jsp:include
                                    page="/WEB-INF/jsp/templates/common/preview.jsp">
                                    <jsp:param name="detail" value="1" />
                                    <jsp:param name="downloadLink" value="download" />
                                </jsp:include>
                            </div>
                        </div>
                        
                            <div class="form-group">
                                <div class="col-lg-10">

                                    <div class="fileupload fileupload-new" data-provides="fileupload">

                                        <span class="btn btn-success fileinput-button">
                                            <i class="glyphicon glyphicon-repeat"></i>
                                            
                                            <span id="fileSelectLabel">Replace file</span>
                                            <form:input id="fileSelectBtn" path="file" name="file"   
                                                        type="file" /> 
                                        </span>
                                    </div>
                                    <form:errors path="file" cssClass="error" />
                                </div>
                            </div>
                    </c:otherwise>
                </c:choose>
              
                             
                        <c:if test="${!model.blank and not empty availableRevisions}">

                             <form:label cssClass="control-label" path="accessLevel">
                                    <fmt:message key="struct.versions"/>:</form:label>
                               
                            <div class="form-group">
                                    <div class="col-lg-10">

                                    <jsp:include
                                        page="/WEB-INF/jsp/templates/common/revisions.jsp">
                                        <jsp:param name="modelName" value="file" />
                                    </jsp:include>

                                </div>
                            </div>
                        </c:if>      
                             
                     
                             <c:if test="${!model.blank}">

                                 <div class="form-group">
                                     <div class="col-lg-10">
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
                                 </div>
                             </c:if>
                     
                 </form:form>

    </div>

    <c:if test="${model.blank}">
        <div class="col-lg-4">                                    
            <div id="dropzone" class="box well" style="width:30em;height:10em;border:1px solid black;"><fmt:message key='file.upload.dropzone'/></div>
         </div>
     
    </c:if>
        
</div>

<div class="row">
    <div id="messages"  class="col-xs-4 col-md-10 col-md-offset-1">
    </div>

    <div id="gallery" class="col-xs-4 col-md-10 col-md-offset-1">
    </div>

</div>

