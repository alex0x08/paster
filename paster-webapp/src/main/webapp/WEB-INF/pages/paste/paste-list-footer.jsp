<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>



<script src="<c:url value='/main/resources/${appId}/paster/js/paste-list/paste-list.js'/>"></script>

<jsp:include page="/WEB-INF/pages/common/paste-update-poll.jsp"/>

<script type="text/javascript">
    const pasterList = new PasterList();
    window.addEventListener('load', function () {
        pasterList.init('${rawPageUrl}', '${userPageUrl}',
                ${pageItems.pageCount-(pageItems.page+1)},
                ${pageItems.page} + 2);
    });

</script>
