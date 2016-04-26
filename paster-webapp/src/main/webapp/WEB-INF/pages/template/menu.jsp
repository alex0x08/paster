<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


        <%--

       Admin's menu
        
        --%>    

<sec:authorize access="hasRole('ROLE_ADMIN')">

    <div class="row">
        <div class="hidden-sm hidden-xs col-md-10 col-lg-10">


            <a href="<c:url value="/main/paste/list"/>">
                <span class="i">/</span>
                <fmt:message key="paste.list.title"/></a> 

            <a href="<c:url value='/main/user/list'/>">
                <span class="i">x</span>
                <fmt:message key='user.list.title'/></a>

            <a href="<c:url value='/main/admin/settings/edit'/>">
                <span class="i">B</span>
                <fmt:message key='settings.edit.title'/></a>

            <a href="<c:url value='/main/admin/settings/dbconsole'/>">
                <span class="i">B</span>
                <fmt:message key="settings.dbconsole.title"/>
            </a>


        </div>

    </div>

</sec:authorize>
