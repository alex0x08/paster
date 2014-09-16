<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


    <li class="dropdown">
        <a href="#" role="button" class="dropdown-toggle" data-toggle="dropdown" >

            <c:out value="${currentUser.name}"/>
            <span class="caret" style="padding-bottom:0;padding-top:0.5em;margin:0;"></span></a>
        <ul class="dropdown-menu" role="menu">
            <li>
                Profile
            </li>
            <li><a  title="Logout" href="<c:url value='/act/logout'/>">
                    <i class="fa fa-sign-in"></i>Logout</a></li>

        </ul>
    </li>
