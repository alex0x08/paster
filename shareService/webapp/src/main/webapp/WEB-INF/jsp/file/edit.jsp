<<<<<<< HEAD
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<jsp:include
    page="/WEB-INF/jsp/templates/common/breadcrumb.jsp">
    <jsp:param name="model" value="${model}" />
    <jsp:param name="modelName" value="file" />
</jsp:include>


<c:url var="blockUrl" value='/main/file/raw/view' />


<link href="<c:url value='/main/assets/${appVersion}/jquery-file-upload/8.4.2/css/jquery.fileupload-ui.css'/>" rel="stylesheet"/>
<link href="<c:url value='/main/assets/${appVersion}/bootstrap-datepicker/1.1.3/css/datepicker.css'/>" rel="stylesheet"/>
<link href="<c:url value='/main/static/${appVersion}/libs/bootstrap-switch/stylesheets/bootstrap-switch.css'/>" rel="stylesheet"/>

 <script type="text/javascript">
       $(document).ready(function() {
           
                   if ($("#form_file_upload input[type='radio']:checked").val() == 'PROJECT') {
                      $('#selectProjectsBlock').toggle();
                   }
                   
                    $('.accessLevelSwitch').change(function() {
                           $('#selectProjectsBlock').toggle($(this).val() == 'PROJECT');
                           $('#selectUsersBlock').toggle($(this).val() == 'USERS');                       
                });           
       });
</script>

 <c:choose>
            <c:when test="${model.blank}">

                <script src="<c:url value='/main/assets/${appVersion}/jquery-file-upload/8.4.2/js/jquery.fileupload.js'/>"></script>
                <script src="<c:url value='/main/assets/${appVersion}/jquery-file-upload/8.4.2/js/jquery.fileupload-process.js'/>"></script>
                <script src="<c:url value='/main/assets/${appVersion}/jquery-file-upload/8.4.2/js/jquery.fileupload-ui.js'/>"></script>
                <script src="<c:url value='/main/static/${appVersion}/libs/bootstrap-switch/js/bootstrap-switch.js'/>"></script>
                <script src="<c:url value='/main/assets/${appVersion}/bootstrap-datepicker/1.1.3/js/bootstrap-datepicker.js'/>"></script>
                <script src="<c:url value='/main/assets/${appVersion}/jquery-knob/1.2.2/jquery.knob.js'/>"></script>

                <c:url var="url" value='/main/file/upload-xdr' />
                <c:set var="mainBlockStyle" value="col-md-4"/>
                
                <script type="text/javascript">

                function formatFileSize(bytes) {
                    if (typeof bytes !== 'number') {
                    return '';
                }

        if (bytes >= 1000000000) {
            return (bytes / 1000000000).toFixed(2) + ' GB';
        }

        if (bytes >= 1000000) {
            return (bytes / 1000000).toFixed(2) + ' MB';
        }

        return (bytes / 1000).toFixed(2) + ' KB';
        }
   


       $(document).ready(function() {
     
        var ul = $('#upload_block');
                         
       $('#file_upload').fileupload({
            
                            url: '${url}',
                            dataType: 'json',
                            autoUpload: true,
                            dropZone: $('#dropzone'),                    
        add: function (e, data) {
        
            var tpl = $('<li class="working"><input type="text" value="0" data-width="48" data-height="48"'+
                ' data-fgColor="#0788a5" data-readOnly="1" data-bgColor="#3e4043" /><p></p><span></span></li>');

            tpl.find('p').text(data.files[0].name)
                         .append('<i>' + formatFileSize(data.files[0].size) + '</i>');

            data.context = tpl.appendTo(ul);

            tpl.find('input').knob();

            tpl.find('span').click(function(){

                if(tpl.hasClass('working')){
                    jqXHR.abort();
                }

                tpl.fadeOut(function(){
                    tpl.remove();
                });

            });

            var jqXHR = data.submit();
        },

        progress: function(e, data){

            var progress = parseInt(data.loaded / data.total * 100, 10);
            data.context.find('input').val(progress).change();            
        },

        fail:function(e, data){
            data.context.addClass('error');
        },
        done: function(e, data) {
             
         try {  data.context.removeClass('working');
                data.context.fadeOut(function(){
                   data.context.remove();
                });
                                                
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
                            },
                    error: function(data) {
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
     <div class="${mainBlockStyle} col-md-offset-1" >     

                 <form:form action="${url}" modelAttribute="model" id="form_file_upload"
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
                     
                     <c:if test="${not empty model.integrationCode}">
                         <div class="form-group">
                             <div class="controls">
                                 <form:errors cssClass="errorblock" element="div"/>
                                 <c:out value="Used integration code: ${model.integrationCode}" escapeXml="true" />
                                 <form:hidden path="integrationCode" />
                             </div>
                         </div> 
                     </c:if>


                     <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN,ROLE_MANAGER">

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
                                                 <form:radiobutton cssClass="accessLevelSwitch" path="accessLevel" value="${level.code}"  />
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


                         <div class="form-group" id="selectProjectsBlock" style="display:none;">
                             <form:label  path="relatedProjects"><fmt:message key="projects.title"/>:</form:label>
                                 <div class="controls">

                                 <fmt:message key='placeholder.select.projects' var="placeholderSelectText"/>
                                 <form:select  path="relatedProjects" cssClass="chosen_select_box_multiple" data-placeholder="${placeholderSelectText}" cssStyle="width:15em;"   >
                                     <form:options  items="${availableProjects}" itemValue="id" itemLabel="name"  />
                                 </form:select>
                                 <form:errors path="relatedProjects" cssClass="error" />
                             </div>
                         </div>         

                         <div class="form-group" id="selectUsersBlock" style="display:none;">
                             <form:label  path="relatedUsers"><fmt:message key="users.title"/>:</form:label>
                                 <div class="controls">

                                 <fmt:message key='placeholder.select.users' var="placeholderSelectText"/>

                                 <form:select  path="relatedUsers" cssClass="btn chosen_select_box_multiple"
                                               data-placeholder="${placeholderSelectText}" cssStyle="width:15em;" >

                                     <c:forEach items="${availableProjectsWithUsers}" var="projectUsers">

                                         <optgroup label="<c:out value='${projectUsers.project.name}'/>">
                                             <form:options  items="${projectUsers.users}" itemValue="id" itemLabel="name"  />
                                         </optgroup>

                                     </c:forEach>

                                 </form:select>

                                 <form:errors path="relatedUsers" cssClass="error" />

                             </div>

                         </div>                         

                     </div>

                     <c:if test="${model.blank}">
                         <div class="col-lg-6" >                                    
                             <div id="dropzone" class="box well" style="width:30em;height:10em;border:1px solid black;"><fmt:message key='file.upload.dropzone'/></div>
                         </div>     
                     </c:if>                                    

                 </div> 
                        <%--         
                 <div class="row">
                     <div class="col-md-8 col-md-offset-1" >    
                      
                         
                         <table class="table table-bordered table-hover" id="customFieldsTable">
				<thead>
					<tr  >
						
                                            <th class="text-center" style="width:20%;">
							Name
						</th>
						<th class="text-center">
							Value
						</th>
                                                <th style="width:5%;"> X </th>
						
					</tr>
				</thead>
				<tbody>
                                    
                                      <c:forEach items="${model.xml.fields}" var="field" varStatus="status">
      <tr id="row_${status.index}">
          <form:hidden path="xml.fields[${field.key}].uuid" />
          <td>
              <form:input path="xml.fields[${field.key}].name" cssClass="form-control" />
              <form:errors path="xml.fields[${field.key}].name" cssClass="alert alert-danger"   />
          </td>
          
          <td>
              <form:input path="xml.fields[${field.key}].value" cssClass="form-control"/>
              <form:errors path="xml.fields[${field.key}].value" cssClass="alert alert-danger"  />
          </td>
          <td><a href="#" class="removeBtn">remove</a></td>
      </tr>
      
      <c:if test="${status.last}">
          <tr id='row_${status.index+1}'></tr>
          
           <script type="text/javascript">
               var totalRows = ${status.index+1};
           </script>
        
      </c:if>

  </c:forEach>
	
				</tbody>
			</table>
               

                        <input name="xml.fields[1].name" >

  <input name="xml.fields[1].value" > 
                         
  <a href="#" id="addRow" class="addBtn">add</a>
  <a href="#" id="deleteRow" class="addBtn">delete</a>
                         
                         <div class="form-group">
                         
         

  <form:errors path="removeAfter" cssClass="error" />

                             
                             <script type="text/javascript">
                                         
    
     $(document).ready(function(){
     
        $("#addRow").click(function(){
       
                $('#row_'+totalRows).html('<td><input name="xml.fields['+totalRows+'].xname" value="" /> </td><td><input  name="xml.fields['+totalRows+'].xvalue" value="" /></td>');
        alert($('#row_'+totalRows).html());
      $('#customFieldsTable').append('<tr id="row_'+(totalRows+1)+'"></tr>');
      totalRows++; 
        });
     $("#deleteRow").click(function(){
    	 if(totalRows>1){
		 $("#row_"+(totalRows-1)).html('');
		 totalRows--;
		 }
	 });

});
    
                                                             
                             </script>     

                         </div>       
                         --%>
                         <c:if test="${model.blank}">

                             <div class="form-group">
                                 <form:label  path="file"><fmt:message key="file.removeAfter"/>:</form:label>
                                     <div class="controls">
                                         
                                         <div class="make-switch" data-on-label="<i class='glyphicon glyphicon-ok'></i>" 
                                              data-off-label="<i class='glyphicon glyphicon-minus'></i>">
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
                                         <form:input path="file" name="file" multiple="true" id="file_upload"
                                                     type="file" /> 
                                     </span>

                                     <form:errors path="file" cssClass="error" />

                                     <div style="padding-top:1em;">
                                        
                                     <ul id='upload_block'>
                                         <!-- The file uploads will be shown here -->
                                     </ul>    
                                     </div>
                                     

                                     <noscript>
                                     <input name="submit" type="submit" class="btn btn-large btn-primary" value="Send" />
                                     </noscript>
                                 </div>

                             </c:when>
                             <c:otherwise>

                                 <div class="form-group">
                                     <div class="col-lg-10">
                                         <jsp:include
                                             page="/WEB-INF/jsp/templates/common/preview.jsp">
                                             <jsp:param name="detail" value="1" />
                                             <jsp:param name="downloadLink" value="download" />
                                             <jsp:param name="model" value="${model}" />
                                             
                                         </jsp:include>
                                     </div>
                                 </div>

                                 <div class="form-group">
                                     <div class="col-lg-10">

                                         <div class="fileupload fileupload-new" data-provides="fileupload">

                                             <span class="btn btn-success fileinput-button">
                                                 <i class="glyphicon glyphicon-repeat"></i>

                                                 <span id="fileSelectLabel"><fmt:message key="button.change"/></span>
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
             </div>

<div class="row">
    <div id="messages"  class="col-xs-4 col-md-10 col-md-offset-1">
    </div>

    <div id="gallery" class="col-xs-4 col-md-10 col-md-offset-1">
    </div>

</div>

=======
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<jsp:include
    page="/WEB-INF/jsp/templates/common/breadcrumb.jsp">
    <jsp:param name="model" value="${model}" />
    <jsp:param name="modelName" value="file" />
</jsp:include>


<c:url var="blockUrl" value='/main/file/raw/view' />


<link href="<c:url value='/main/assets/${appVersion}/jquery-file-upload/8.4.2/css/jquery.fileupload-ui.css'/>" rel="stylesheet"/>
<link href="<c:url value='/main/assets/${appVersion}/bootstrap-datepicker/1.1.3/css/datepicker.css'/>" rel="stylesheet"/>
<link href="<c:url value='/main/static/${appVersion}/libs/bootstrap-switch/stylesheets/bootstrap-switch.css'/>" rel="stylesheet"/>

 <script type="text/javascript">
       $(document).ready(function() {
           
                   if ($("#form_file_upload input[type='radio']:checked").val() == 'PROJECT') {
                      $('#selectProjectsBlock').toggle();
                   }
                   
                    $('.accessLevelSwitch').change(function() {
                           $('#selectProjectsBlock').toggle($(this).val() == 'PROJECT');
                           $('#selectUsersBlock').toggle($(this).val() == 'USERS');                       
                });           
       });
</script>

 <c:choose>
            <c:when test="${model.blank}">

                <script src="<c:url value='/main/assets/${appVersion}/jquery-file-upload/8.4.2/js/jquery.fileupload.js'/>"></script>
                <script src="<c:url value='/main/assets/${appVersion}/jquery-file-upload/8.4.2/js/jquery.fileupload-process.js'/>"></script>
                <script src="<c:url value='/main/assets/${appVersion}/jquery-file-upload/8.4.2/js/jquery.fileupload-ui.js'/>"></script>
                <script src="<c:url value='/main/static/${appVersion}/libs/bootstrap-switch/js/bootstrap-switch.js'/>"></script>
                <script src="<c:url value='/main/assets/${appVersion}/bootstrap-datepicker/1.1.3/js/bootstrap-datepicker.js'/>"></script>
                <script src="<c:url value='/main/assets/${appVersion}/jquery-knob/1.2.2/jquery.knob.js'/>"></script>

                <c:url var="url" value='/main/file/upload-xdr?${_csrf.parameterName}=${_csrf.token}' />
                <c:set var="mainBlockStyle" value="col-md-4"/>
                
                <script type="text/javascript">

                function formatFileSize(bytes) {
                    if (typeof bytes !== 'number') {
                    return '';
                }

        if (bytes >= 1000000000) {
            return (bytes / 1000000000).toFixed(2) + ' GB';
        }

        if (bytes >= 1000000) {
            return (bytes / 1000000).toFixed(2) + ' MB';
        }

        return (bytes / 1000).toFixed(2) + ' KB';
        }
   


       $(document).ready(function() {
     
        var ul = $('#upload_block');
                         
       $('#file_upload').fileupload({
            
                            url: '${url}',
                            dataType: 'json',
                            autoUpload: true,
                            dropZone: $('#dropzone'),                    
        add: function (e, data) {
        
       
            var tpl = $('<li class="working"><input type="text" value="0" data-width="48" data-height="48"'+
                ' data-fgColor="#0788a5" data-readOnly="1" data-bgColor="#3e4043" /><p></p><span></span></li>');

            tpl.find('p').text(data.files[0].name)
                         .append('<i>' + formatFileSize(data.files[0].size) + '</i>');

            data.context = tpl.appendTo(ul);

            tpl.find('input').knob();

            tpl.find('span').click(function(){

                if(tpl.hasClass('working')){
                    jqXHR.abort();
                }

                tpl.fadeOut(function(){
                    tpl.remove();
                });

            });

            var jqXHR = data.submit();
        },

        progress: function(e, data){

            var progress = parseInt(data.loaded / data.total * 100, 10);
            data.context.find('input').val(progress).change();            
        },

        fail:function(e, data){
            data.context.addClass('error');
        },
        done: function(e, data) {
             
         try {  data.context.removeClass('working');
                data.context.fadeOut(function(){
                   data.context.remove();
                });
                                                
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
                            },
                    error: function(data) {
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
     <div class="${mainBlockStyle} col-md-offset-1" >     

                 <form:form action="${url}" modelAttribute="model" id="form_file_upload"
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
                     
                     <c:if test="${not empty model.integrationCode}">
                         <div class="form-group">
                             <div class="controls">
                                 <form:errors cssClass="errorblock" element="div"/>
                                 <c:out value="Used integration code: ${model.integrationCode}" escapeXml="true" />
                                 <form:hidden path="integrationCode" />
                             </div>
                         </div> 
                     </c:if>


                     <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN,ROLE_MANAGER">

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
                                                 <form:radiobutton cssClass="accessLevelSwitch" path="accessLevel" value="${level.code}"  />
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


                         <div class="form-group" id="selectProjectsBlock" style="display:none;">
                             <form:label  path="relatedProjects"><fmt:message key="projects.title"/>:</form:label>
                                 <div class="controls">

                                 <fmt:message key='placeholder.select.projects' var="placeholderSelectText"/>
                                 <form:select  path="relatedProjects" cssClass="chosen_select_box_multiple" data-placeholder="${placeholderSelectText}" cssStyle="width:15em;"   >
                                     <form:options  items="${availableProjects}" itemValue="id" itemLabel="name"  />
                                 </form:select>
                                 <form:errors path="relatedProjects" cssClass="error" />
                             </div>
                         </div>         

                         <div class="form-group" id="selectUsersBlock" style="display:none;">
                             <form:label  path="relatedUsers"><fmt:message key="users.title"/>:</form:label>
                                 <div class="controls">

                                 <fmt:message key='placeholder.select.users' var="placeholderSelectText"/>

                                 <form:select  path="relatedUsers" cssClass="btn chosen_select_box_multiple"
                                               data-placeholder="${placeholderSelectText}" cssStyle="width:15em;" >

                                     <c:forEach items="${availableProjectsWithUsers}" var="projectUsers">

                                         <optgroup label="<c:out value='${projectUsers.project.name}'/>">
                                             <form:options  items="${projectUsers.users}" itemValue="id" itemLabel="name"  />
                                         </optgroup>

                                     </c:forEach>

                                 </form:select>

                                 <form:errors path="relatedUsers" cssClass="error" />

                             </div>

                         </div>                         

                     </div>

                     <c:if test="${model.blank}">
                         <div class="col-lg-6" >                                    
                             <div id="dropzone" class="box well" style="width:30em;height:10em;border:1px solid black;"><fmt:message key='file.upload.dropzone'/></div>
                         </div>     
                     </c:if>                                    

                 </div> 
                        <%--         
                 <div class="row">
                     <div class="col-md-8 col-md-offset-1" >    
                      
                         
                         <table class="table table-bordered table-hover" id="customFieldsTable">
				<thead>
					<tr  >
						
                                            <th class="text-center" style="width:20%;">
							Name
						</th>
						<th class="text-center">
							Value
						</th>
                                                <th style="width:5%;"> X </th>
						
					</tr>
				</thead>
				<tbody>
                                    
                                      <c:forEach items="${model.xml.fields}" var="field" varStatus="status">
      <tr id="row_${status.index}">
          <form:hidden path="xml.fields[${field.key}].uuid" />
          <td>
              <form:input path="xml.fields[${field.key}].name" cssClass="form-control" />
              <form:errors path="xml.fields[${field.key}].name" cssClass="alert alert-danger"   />
          </td>
          
          <td>
              <form:input path="xml.fields[${field.key}].value" cssClass="form-control"/>
              <form:errors path="xml.fields[${field.key}].value" cssClass="alert alert-danger"  />
          </td>
          <td><a href="#" class="removeBtn">remove</a></td>
      </tr>
      
      <c:if test="${status.last}">
          <tr id='row_${status.index+1}'></tr>
          
           <script type="text/javascript">
               var totalRows = ${status.index+1};
           </script>
        
      </c:if>

  </c:forEach>
	
				</tbody>
			</table>
               

                        <input name="xml.fields[1].name" >

  <input name="xml.fields[1].value" > 
                         
  <a href="#" id="addRow" class="addBtn">add</a>
  <a href="#" id="deleteRow" class="addBtn">delete</a>
                         
                         <div class="form-group">
                         
         

  <form:errors path="removeAfter" cssClass="error" />

                             
                             <script type="text/javascript">
                                         
    
     $(document).ready(function(){
     
        $("#addRow").click(function(){
       
                $('#row_'+totalRows).html('<td><input name="xml.fields['+totalRows+'].xname" value="" /> </td><td><input  name="xml.fields['+totalRows+'].xvalue" value="" /></td>');
        alert($('#row_'+totalRows).html());
      $('#customFieldsTable').append('<tr id="row_'+(totalRows+1)+'"></tr>');
      totalRows++; 
        });
     $("#deleteRow").click(function(){
    	 if(totalRows>1){
		 $("#row_"+(totalRows-1)).html('');
		 totalRows--;
		 }
	 });

});
    
                                                             
                             </script>     

                         </div>       
                         --%>
                         <c:if test="${model.blank}">

                             <div class="form-group">
                                 <form:label  path="file"><fmt:message key="file.removeAfter"/>:</form:label>
                                     <div class="controls">
                                         
                                         <div class="make-switch" data-on-label="<i class='glyphicon glyphicon-ok'></i>" 
                                              data-off-label="<i class='glyphicon glyphicon-minus'></i>">
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
                                         <form:input path="file" name="file" multiple="true" id="file_upload"
                                                     type="file" /> 
                                     </span>

                                     <form:errors path="file" cssClass="error" />

                                     <div style="padding-top:1em;">
                                        
                                     <ul id='upload_block'>
                                         <!-- The file uploads will be shown here -->
                                     </ul>    
                                     </div>
                                     

                                     <noscript>
                                     <input name="submit" type="submit" class="btn btn-large btn-primary" value="Send" />
                                     </noscript>
                                 </div>

                             </c:when>
                             <c:otherwise>

                                 <div class="form-group">
                                     <div class="col-lg-10">
                                         <jsp:include
                                             page="/WEB-INF/jsp/templates/common/preview.jsp">
                                             <jsp:param name="detail" value="1" />
                                             <jsp:param name="downloadLink" value="download" />
                                             <jsp:param name="model" value="${model}" />
                                             
                                         </jsp:include>
                                     </div>
                                 </div>

                                 <div class="form-group">
                                     <div class="col-lg-10">

                                         <div class="fileupload fileupload-new" data-provides="fileupload">

                                             <span class="btn btn-success fileinput-button">
                                                 <i class="glyphicon glyphicon-repeat"></i>

                                                 <span id="fileSelectLabel"><fmt:message key="button.change"/></span>
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
             </div>

<div class="row">
    <div id="messages"  class="col-xs-4 col-md-10 col-md-offset-1">
    </div>

    <div id="gallery" class="col-xs-4 col-md-10 col-md-offset-1">
    </div>

</div>

>>>>>>> dbf52a094d477965568a60d39b920438f36ce077
