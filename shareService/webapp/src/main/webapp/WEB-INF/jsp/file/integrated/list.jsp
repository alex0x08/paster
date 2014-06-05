<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<script src="<c:url value='/main/assets/${appVersion}/jquery-ui/1.10.2/ui/minified/jquery.ui.widget.min.js'/>"></script>
<link href="<c:url value='/main/assets/${appVersion}/jquery-file-upload/8.4.2/css/jquery.fileupload-ui.css'/>" rel="stylesheet"/>

<script type="text/javascript">
      $(document).ready(function() {
               $('#fileSelectBtn').bind('change', function() {
                       $('#fileSelectLabel').text($(this).val());
               });
      });
</script>

<c:url var="url" value='/main/file/integrated/list' />
<c:url var="uploadUrl" value='/main/file/save/integrated' />


    <div class="row">
        <div class="col-md-2 col-xs-10" >
            <jsp:include page="/WEB-INF/jsp/templates/common/lang-select.jsp"/>          
        </div>
    </div>

    <div class="row">
        <div class="col-lg-10 ">
 
               <div class="tabbable">
        <ul class="nav nav-tabs" id="integratedTab">
            <li ><a href="#uploadFile" data-toggle="tab"><fmt:message key="file.upload.title"/></a></li>
            <li class="active"><a href="#listFiles" data-toggle="tab"><fmt:message key="files.title"/></a></li>
        </ul>
    </div>

            
            
        </div>
        
    </div>      
        
 

<div class="tab-content" style="padding-top:0.5em;">
  <div class="tab-pane" id="uploadFile">
      
         <sec:authorize access="isAuthenticated()">   
         
            <form:form action="${uploadUrl}" modelAttribute="model" 
                       method="POST" id="file_upload" cssClass="form-horizontal"
                       enctype="multipart/form-data">

                   <form:errors path="*" cssClass="errorblock" element="div"/>

                    <c:out value="Using integration code: ${model.integrationCode}" escapeXml="true" />

                    <form:hidden path="url" />
                    <form:hidden path="name" />
                    <form:hidden path="integrationCode" />
                    <form:hidden path="accessLevel" />
                   
                      <div class="form-group">
                                     <div class="col-lg-10">
                                         <div class="fileupload fileupload-new" data-provides="fileupload">
                                             <span class="btn btn-success fileinput-button">
                                                 <i class="glyphicon glyphicon-repeat"></i>
                                                 <span id="fileSelectLabel"><fmt:message key="button.select"/></span>
                                                 <form:input id="fileSelectBtn" path="file" name="file"   
                                                             type="file" /> 
                                             </span>
                                         </div>
                                         <form:errors path="file" cssClass="error" />
                                     </div>
                                 </div>                   
                    <div class="form-actions">
                        <fmt:message var="submit_button_text" key="button.upload" />
                        <input name="submit" class="btn btn-primary" type="submit" value="${submit_button_text}" />
                    </div>               
            </form:form>

        </sec:authorize>

       <sec:authorize access="isAnonymous()">
            
            <jsp:include page="/WEB-INF/jsp/templates/common/auth-dropdown.jsp" >
                <jsp:param name="integrationCode" value="${model.integrationCode}"/>
            </jsp:include>
     
        </sec:authorize>
      
  </div>
  <div class="tab-pane active" id="listFiles">
      
         
        <c:if test="${pageItems.nrOfElements > 0}">
<div class="row" >                
    <div class="col-md-6"  > 
        <c:if test="${pageItems.pageCount > 1}">  
            <jsp:include
                page="/WEB-INF/jsp/templates/common/pagination.jsp">
                <jsp:param name="modelName" value="file" />
                <jsp:param name="integrationCode" value="${model.integrationCode}" />                
            </jsp:include>
        </c:if>
    </div>
</div>
<div class="row" >        
    
   
    <div class="col-lg-10">
        <div class="panel-group" id="accordion">
  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne">
           <fmt:message key="settings.title"/>
        </a>
      </h4>
    </div>
    <div id="collapseOne" class="panel-collapse collapse">
      <div class="panel-body">
          <jsp:include
            page="/WEB-INF/jsp/templates/common/page-size.jsp">
            <jsp:param name="modelName" value="file" />
            <jsp:param name="integrationCode" value="${model.integrationCode}" />                
        </jsp:include>    
        
        <jsp:include
            page="/WEB-INF/jsp/templates/common/sort.jsp">
            <jsp:param name="modelName" value="file" />
             <jsp:param name="integrationCode" value="${model.integrationCode}" />                
        </jsp:include>
      
      </div>
    </div>
  </div>
    
</div>
  
    </div>
</div>

            
</c:if>

   <div class="row">
    <div class="col-md-6">
 
        <div id="gallery">
            <c:forEach var="obj" items="${pageItems.pageList}" varStatus="status">

                <c:set var="model" value="${obj}" scope="request"></c:set>

                <jsp:include page="/WEB-INF/jsp/templates/common/preview.jsp" >
                    <jsp:param name="integrationMode" value="integrated"/>
                    <jsp:param name="menuTargetNew" value="1"/>

                </jsp:include>
                <br/>
            </c:forEach>

        </div>


        <c:if test="${pageItems.nrOfElements == 0}">

            <jsp:include page="/WEB-INF/jsp/templates/common/noFiles.jsp" >
                <jsp:param name="integrationMode" value="1"/>
            </jsp:include>

        </c:if>

      
  </div> 
</div>
    
  </div>
</div>
    

     