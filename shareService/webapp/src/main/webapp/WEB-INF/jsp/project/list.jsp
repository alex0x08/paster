<%-- 
    Document   : list
    Created on : 25.09.2013, 18:10:20
    Author     : aachernyshev
--%>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>



<h1><fmt:message key="projects.title"/></h1>

<c:url var="url" value='/main/project/list' />
<div id="notice"></div>
<div>

    <table class="table table-striped table-bordered" >
        <tr style="">
            <th style="width:5%" >
                 <button type="button" class="btn btn-default sortColumnKey" sortColumn="id">ID  
                <span class="glyphicon"></span></button>
            </th>
            <th style="width:10%" >
                 <button type="button" class="btn btn-default sortColumnKey" sortColumn="name">
                <fmt:message key="struct.name"/>
                <span class="glyphicon"></span></button>
            </th>
            <th style="width:20%;">
             <button type="button" class="btn btn-default sortColumnKey" sortColumn="description">
                <fmt:message key="struct.desc"/>
                <span class="glyphicon"></span></button>
            </th>
            <th style="width:10%;">
                 <button type="button" class="btn btn-default sortColumnKey" sortColumn="lastModified">
                <fmt:message key="struct.lastModified"/>
                <span class="glyphicon"></span></button>
            </th>
            <th style="width:1%;"></th>
        </tr>
        <c:forEach var="project" items="${items}" varStatus="status">           
            
            <c:url value="/main/project/edit" var="editUrl">                
                        <c:param name="id" value="${project.id}"/>                  
            </c:url>
            
            <tr >
                <td>
                    <a href="<c:out value='${editUrl}'/>">${project.id}</a> 
                </td> 

                <td>
                    
                    <c:if test="${project.avatarSet}">
                        <img src="data:image/png;base64,${project.avatar.icon}" 
                             alt="<c:out value='${project.name}'/>" />                
                    </c:if>
                    
                    <a href="<c:out value='${editUrl}'/>">${project.name}</a> 
                </td> 

                <td>
                    <a href="<c:out value='${editUrl}'/>">${project.description}</a> 
                </td>
               
                <td>
                    <fmt:formatDate value="${project.lastModified}" pattern="${dateTimePattern}"/>
                </td>

                <td>
                  
                    <a class="fileDeleteBtn"
                   targetTitle="<c:out value='${project.name}'/>
                   &nbsp;                     
                   <fmt:formatDate value="${project.lastModified}" pattern="${dateTimePattern}"/>"
                   title="<fmt:message key="button.delete"/>"  
                   deleteLink="<c:url value="/main/project/delete">
                       <c:param name="id" value="${project.id}"/>
                   </c:url>">
                    <span class="glyphicon glyphicon-remove" title="<fmt:message key="button.delete"/>"></span>
                </a>   

                </td>
            </tr>
        </c:forEach>
    </table>

</div>

                <jsp:include
            page="/WEB-INF/jsp/templates/common/sort-table.jsp">
            <jsp:param name="modelName" value="project" />
        </jsp:include>          
                
      