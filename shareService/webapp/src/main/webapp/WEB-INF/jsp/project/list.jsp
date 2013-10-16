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
            <th style="width:5%" >ID</th>
            <th style="width:10%" ><fmt:message key="struct.name"/></th>
            <th style="width:20%;"><fmt:message key="struct.desc"/></th>
            <th style="width:10%;"><fmt:message key="struct.lastModified"/></th>
            <th style="width:5%;"></th>
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
                    <a href="<c:out value='${editUrl}'/>">${project.name}</a> 
                </td> 

                <td>
                    <a href="<c:out value='${editUrl}'/>">${project.description}</a> 
                </td>
               
                <td>
                    <fmt:formatDate value="${project.lastModified}" pattern="${dateTimePattern}"/>
                </td>

                <td>
                    <a title="<fmt:message key="button.delete"/>" href="<c:url value="/main/project/delete">
                           <c:param name="id" value="${project.id}"/>
                       </c:url>"><img src="<c:url value='/images/delete.png'/>"/></a> 

                </td>
            </tr>
        </c:forEach>
    </table>

</div>
