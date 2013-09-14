<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<div class="btn-group box">
    
     <a class="btn dropdown-toggle btn-primary" data-toggle="dropdown" href="#">
          <span class="glyphicon glyphicon-folder-close"></span>
                                    <span class="caret"></span>
                                </a>
    
<ul class="dropdown-menu">

  
    <sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_USER">    

        <li >
            <a  href="<c:url value="/main/file/new"/>">
                <span class="glyphicon glyphicon-upload"></span><fmt:message key="file.upload.title"/></a> 
        </li>
    </sec:authorize>
    <li >
        <a  href="<c:url value="/main/file/list"/>"><span class="glyphicon glyphicon-folder-close"></span><fmt:message key="files.title"/></a>

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

</div>