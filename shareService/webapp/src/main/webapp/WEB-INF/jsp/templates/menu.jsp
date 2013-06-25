<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<ul class="nav">

    <li class="nav-header"><fmt:message key="files.title"/></li>

    <sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_USER">    


        <li class="">
            <a class="new-file" href="<c:url value="/main/file/new"/>"><fmt:message key="file.upload.title"/></a> 
        </li>
    </sec:authorize>
    <li class="">
        <a class="files" href="<c:url value="/main/file/list"/>"><fmt:message key="files.title"/></a>

    </li>



    <sec:authorize ifAnyGranted="ROLE_ADMIN"> 
        <li class="nav-header"><fmt:message key="users.title"/></li>

        <li class="">
            <a class="new-user" href="<c:url value="/main/user/new"/>"><fmt:message key="user.new.title"/></a> 
        </li>

        <li class="">

            <a class="users" href="<c:url value="/main/user/list"/>"><fmt:message key="users.title"/></a>

        </li>

    </sec:authorize>


</ul>
