<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<h1><fmt:message key="users.title"/></h1>

<c:url var="url" value='/main/user/list' />
<div id="notice"></div>
<div>
    
    <table class="table table-striped table-bordered" >
    <tr style="">
        <th style="width:5%" >ID</th>
        <th style="width:10%" ><fmt:message key="user.login"/></th>
        <th style="width:20%;"><fmt:message key="user.name"/></th>
        <th style="width:10%;"><fmt:message key="user.roles"/></th>
        <th style="width:10%;"><fmt:message key="struct.lastModified"/></th>
        <th style="width:5%;"></th>
    </tr>
    <c:forEach var="user" items="${items}" varStatus="status">
        <tr >
            <td>
                <a href="<c:url value="/main/user/edit">
                       <c:param name="id" value="${user.id}"/>
                   </c:url>">${user.id}</a> 
            </td> 

            <td>
                <a href="<c:url value="/main/user/edit">
                       <c:param name="id" value="${user.id}"/>
                   </c:url>">${user.login}</a> 
            </td> 

            <td>
                <a href="<c:url value="/main/user/edit">
                       <c:param name="id" value="${user.id}"/>
                   </c:url>">${user.name}</a> 
            </td>
            <td>
                <c:forEach items="${user.roles}" var="role">
                    <fmt:message key="${role.desc}"/>

                </c:forEach>
            </td>
            <td>
                <fmt:formatDate value="${user.lastModified}" pattern="${dateTimePattern}"/>
            </td>

            <td>
                <a title="<fmt:message key="button.delete"/>" href="<c:url value="/main/user/delete">
                       <c:param name="id" value="${user.id}"/>
                   </c:url>"><img src="<c:url value='/images/delete.png'/>"/></a> 

            </td>
        </tr>
    </c:forEach>
</table>
        
        </div>