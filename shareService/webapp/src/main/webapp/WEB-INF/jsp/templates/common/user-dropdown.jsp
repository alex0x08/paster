<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>



  <div class="btn-group box">
      <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
        <jsp:include page="/WEB-INF/jsp/templates/common/user-avatar.jsp" >
            <jsp:param name="size" value="small" />
        </jsp:include>

          <c:out value="${usermodel.login}"/>
          <span class="caret"></span>
         </a>

          <ul class="dropdown-menu ">             
              
              <li role="presentation" class="dropdown-header">
              
                   <c:out value="${usermodel.name}"/>
                   (
                   <c:url var="imgUrl" 
                          value='/main/static/${appVersion}/images/flags/flag_${usermodel.prefferedLocale.language}_${usermodel.prefferedLocale.country}.png'/>
                   <img src="${imgUrl}" title="<c:out value="${usermodel.prefferedLocale.displayName}"/>"/> 
                   )
                   <small>
            <c:forEach items="${usermodel.roles}" var="role"  >
                <fmt:message key="${role.desc}"/>
            </c:forEach>           
                   </small>
               
            <p>
                <c:if test="${usermodel.relatedProject.avatarSet}">
                                      <img src="data:image/png;base64,${usermodel.relatedProject.avatar.icon}" 
                                           alt="<c:out value='${usermodel.relatedProject.name}'/>" />                
                                  </c:if>
                <c:out value="${usermodel.relatedProject.name}"/>
            </p> 
                  
              </li>
              
              <li role="presentation" class="divider"></li>
              
              <c:if test="${param.mode eq 'PROFILE'}">
              <li>
                  <a class="profile" href="<c:url value="/main/profile"/>">
                      <span class="glyphicon glyphicon-user"></span>
                      <fmt:message key="button.profile"/>                      
                  </a> 
              </li>
               </c:if>
              
              <li>
                <a href="<c:url value='/main/file/list/search?query=ownerName:${usermodel.name}'/>" 
                target="${not empty param.integrationMode ? '_blank' : 'target'}">
                    <span class="glyphicon glyphicon-search" title="<fmt:message key="button.search"/>"></span>
                    <fmt:message key="button.search"/>
                </a>
          
              </li>
              
              <c:if test="${param.mode eq 'PROFILE'}">
                  <li>


                      <c:url var="logoutUrl" value="/act/doLogout"/>
                      <form:form role="form" cssStyle="padding-left:1.5em;" action="${logoutUrl}" 
                                 method="POST" >
                          <button class="btn btn-danger btn-sm" type="submit" >
                              <span class="glyphicon glyphicon-log-out"></span>
                              <fmt:message key="button.logout"/>
                          </button>

                      </form:form>


                  </li>   
              </c:if>
               

              <c:if test="${param.mode eq 'USER'}">
                  <c:if test="${usermodel.skypeSet}">
                      <li>
                          
                          <a href="<c:url value='skype:${usermodel.skype}?chat&topic=${model.name}'/>">
                              <img src="<c:url value='/main/static/${appVersion}/images/skype.png'/>"/>                    
                            <fmt:message key="button.call"/>
                        </a>
                        
                      </li>
                  </c:if>
                      
                   <c:if test="${usermodel.emailSet}">
                      <li>
                          
                          <a href="<c:url value='mailto:${usermodel.email}'><c:param name="subject" value="${model.name}"/></c:url>">
                             <span class="glyphicon glyphicon-envelope"></span>                   
                            <fmt:message key="button.mail"/>
                        </a>
                        
                      </li>
                  </c:if>    
                  
              </c:if>
               
             
           
           
          </ul>
  </div>  


