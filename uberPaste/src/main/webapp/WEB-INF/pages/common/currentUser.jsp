<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


    <li class="dropdown" data-behavior="BS.Dropdown">
        <a href="#" id="userProfileSwitch" role="button" class="dropdown-toggle" data-toggle="dropdown" >

            <c:out value="${currentUser.name}"/>
            <b class="caret"></b>
        </a>
             <span class="dropdown-arrow"></span>
        <ul class="dropdown-menu" role="menu"  aria-labelledby="userProfileSwitch">
            <li>
                Profile
            </li>
            <li><a  title="Logout" href="<c:url value='/act/logout'/>">
                    <i class="fa fa-sign-in"></i>Logout</a></li>

        </ul>
    </li>
