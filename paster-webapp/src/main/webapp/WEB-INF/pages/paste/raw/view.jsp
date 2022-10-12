<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<%--

    Renders view page for selected paste without template (raw)

--%>


<div id="${model.id}_pagedPaste">
    
    <jsp:include page="/WEB-INF/pages/paste/common/paste-view-top.jsp"/>
    <jsp:include page="/WEB-INF/pages/paste/common/paste-view-common.jsp"/>
    
</div>
