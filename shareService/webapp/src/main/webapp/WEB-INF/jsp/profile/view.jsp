
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>



<c:if test="${not empty statusMessageKey}">
    <p><fmt:message key="${statusMessageKey}"/></p>
</c:if>


        <div class="form-row">
            <label for="name"><fmt:message key="user.name"/>:</label>
            <span class="input">
                <c:out value="${user.name}"/>
             </span>
        </div>

            
            
        <div class="form-row">
            <label for="login"><fmt:message key="user.login"/>:</label>
            <span class="input">
                <c:out value="${user.login}"/>
            </span>
        </div>

        <div class="form-row">
            <label for="roles"><fmt:message key="user.roles"/>:</label>
            <span class="input">
                    
                    <c:forEach items="${user.roles}" var="role">
                        <fmt:message key="${role.desc}"/>
                     </c:forEach>
               
            </span>
        </div>

          <div class="form-row">
            <label for="email"><fmt:message key="user.email"/>:</label>
            <span class="input">
                         <c:out value="${user.email}"/>
            </span>
        </div>   
            
        <div class="form-buttons">
            <div class="button">   
                <a title="<fmt:message key="button.edit"/>" href="<c:url value="/main/profile/edit">
                       <c:param name="id" value="${user.id}"/>
                   </c:url>"><fmt:message key="button.edit"/></a> 
            </div>
        </div>
