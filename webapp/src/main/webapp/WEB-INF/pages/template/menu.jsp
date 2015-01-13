<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<div class="row">
    <div class="hidden-sm hidden-xs col-md-10 col-lg-10">


       


        <sec:authorize ifAnyGranted="ROLE_ADMIN">



            <a href="<c:url value="/main/paste/list"/>">
                <span class="i">/</span>
                <fmt:message key="paste.list.title"/></a> 

            <a href="<c:url value='/main/user/list'/>">
                <span class="i">x</span>
                <fmt:message key='user.list.title'/></a>

            <a href="<c:url value='/main/admin/settings/edit'/>">
                <span class="i">B</span>
                <fmt:message key='settings.edit.title'/></a>


        </sec:authorize>

    </div>

</div>