<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>
<%-- 
    Document   : select-avatar
    Created on : 21.11.2013, 20:52:08
    Author     : aachernyshev
--%>


     <div class="form-group ">

                    <form:label cssClass="control-label" path="name">
                        <fmt:message key="avatar.icon"/>:</form:label>                        
                        <div class="btn-group" data-toggle="buttons" style="padding-bottom: 1em;padding-top:1em;">                           
                           
                                         <c:forEach items="${availableAvatarTypes}" var="type">                                         
                                             <c:choose>
                                                 <c:when test="${type eq 'GAVATAR'}">                                                     
                                                      <c:choose>
                                                 <c:when test="${gavatarEnabled}">
                                                     <label class="btn btn-default" >
                                                         <fmt:message key="${type.desc}" />
                                                         <form:radiobutton cssClass="avatarTypeSwitch" path="avatarType" value="${type.code}"    />
                                                     </label>
                                                 </c:when>
                                                          <c:otherwise>
                                                 
                                                              Gavatar is disabled
                                                              
                                                          </c:otherwise>
                                                      </c:choose>
                                                     
                                                 

                                                 </c:when>
                                                 <c:otherwise>

                                                     <label class="btn btn-default" >
                                                         <fmt:message key="${type.desc}" />
                                                         <form:radiobutton cssClass="avatarTypeSwitch" path="avatarType" value="${type.code}"  />
                                                     </label>


                                                 </c:otherwise>
                                             </c:choose>

                                         
                                         </c:forEach>

                                     </div>    
                        
                        <div id="selectAvatarFileBlock" style="display:none;" class="input-group input-group-sm">
                            <span class="input-group-addon">

                                <c:choose>
                                    <c:when test="${usermodel.avatarSet}">
                                        <img src="data:image/png;base64,${usermodel.avatar.picture}" 
                                             alt="<c:out value='${usermodel.name}'/>" />              
                                    </c:when>
                                    <c:otherwise>
                                        <span class="glyphicon glyphicon-picture"></span>
                                    </c:otherwise>
                                </c:choose>
                               
                                
                            </span>
                            <form:input path="file" type="file" cssStyle="width:20em;" 
                                        cssErrorClass="form-control alert alert-danger" 
                                        cssClass="form-control"/>
                             <form:errors path="file" cssClass="help-block alert alert-danger" /> 
                        </div>
                       
                        
                         <div id="previewGavatarBlock" style="display:none;" class="input-group input-group-sm">
                            

                                <c:choose>
                                    <c:when test="${usermodel.emailSet}">
                                        <img  src="<c:out value='${gavatarlUrl}/${usermodel.avatarHash}?s=16'/>"/>
                                        
                                        <img  src="<c:out value='${gavatarlUrl}/${usermodel.avatarHash}?s=48'/>"/>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="glyphicon glyphicon-picture"></span>
                                        Please set & save email first 
                                    </c:otherwise>
                                </c:choose>                           
                                
                           
                            
                        </div>
                       
                        
                </div>    

                        
                        
<script type="text/javascript">
       $(document).ready(function() {
           
                   if ($("#${param.formId} input[type='radio']:checked").val() == 'FILE') {
                      $('#selectAvatarFileBlock').toggle();
                   }
                   
                    $('.avatarTypeSwitch').change(function() {
                           $('#selectAvatarFileBlock').toggle($(this).val() == 'FILE');
                           $('#previewGavatarBlock').toggle($(this).val() == 'GAVATAR');
                       
                });
           
       });
</script>
