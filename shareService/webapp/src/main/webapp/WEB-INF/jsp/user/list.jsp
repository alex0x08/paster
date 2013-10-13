<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<h1><fmt:message key="users.title"/></h1>

<c:url var="url" value='/main/user/list' />



<div class="row" style="margin: 0; padding: 0;">                

<jsp:include
    page="/WEB-INF/jsp/templates/common/pagination.jsp">
    <jsp:param name="modelName" value="user" />
</jsp:include>

<jsp:include
    page="/WEB-INF/jsp/templates/common/page-size.jsp">
    <jsp:param name="modelName" value="user" />
</jsp:include>
    
</div>

<div class="row">
    
    <table class="table table-striped table-bordered" >
    <tr style="">
        <th style="width:5%" >
            <button type="button" class="btn btn-default sortColumnKey">ID  <span class="glyphicon glyphicon-arrow-down"></span></button>
        </th>
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

         <script type="text/javascript">
             
             var currentSort = '${pageItems.sort.property}';
             
                       $(document).ready(function() {
                           
                           
                           
                              $('.sortColumnKey').each(function(index, value) {
                                     alert( index + ": " + value );
                              });
                    });
                      
                  </script>