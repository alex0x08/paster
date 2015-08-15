<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


    <li class="dropdown" data-behavior="BS.Dropdown">
        <a href="#" id="userProfileSwitch" role="button" 
           class="dropdown-toggle" data-toggle="dropdown" >

            <c:out value="${currentUser.name}"/>
            <b class="caret"></b>
        </a>
             <span class="dropdown-arrow"></span>
        <ul class="dropdown-menu" role="menu"  aria-labelledby="userProfileSwitch">
            <li>
                Profile
            </li>
            <li>
                <c:url var="logoutUrl" value='/act/logout'/>
                 <form:form  role="form" action="${logoutUrl}" method="POST" >
                       <button type="submit" class="btn btn-primary" >
                          <i class="fa fa-sign-in"></i>Logout</a>
                       </button>

                 </form:form>
              
                    </li>

        </ul>
    </li>
