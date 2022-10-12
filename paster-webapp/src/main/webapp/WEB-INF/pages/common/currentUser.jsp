<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%--

        Curent user (top right) dropdown

--%>
<li class="nav-item dropdown">
    <a href="#" id="userProfileSwitch" role="button" 
        class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
        <c:out value='${currentUser.name}' />
        <b class="caret"></b>
    </a>
    <span class="dropdown-arrow"></span>
    <ul class="dropdown-menu" role="menu" aria-labelledby="userProfileSwitch">      
        <li class="nav-item dropdown" >
            <c:url var="logoutUrl" value='/act/logout' />
            <form:form cssClass="form-inline" 
                        style="padding-left:1em;" 
                        role="form" action="${logoutUrl}" method="POST">
                <button type="submit" class="btn btn-sm">
                    <i class="fa fa-sign-in"></i> <fmt:message key="button.logout" /></a>
                </button>
            </form:form>
        </li>
    </ul>
</li>
