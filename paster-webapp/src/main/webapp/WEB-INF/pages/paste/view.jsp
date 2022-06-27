<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>



<jsp:include page="/WEB-INF/pages/common/paste-update-poll.jsp"/>

<jsp:include page="/WEB-INF/pages/paste/common/paste-view-top.jsp"/>
   



<jsp:include page="/WEB-INF/pages/paste/common/paste-view-common.jsp"/>


<div id="numSpace" class="line" >
</div>

<span id="pasteLineCopyBtn" style="display:none; white-space: normal;">
    <a id="ctrlc_line" 
       data-clipboard-target="pasteLineToCopy" href="javascript:void(0);" 
       style="float:left;" title="Copy to clipboard" >
        <span class="img-map img-clip"></span>
    </a> 
</span>

<span id="pasteLineToCopy" style="display:none;">
    NONE
</span>




<script type="text/javascript">


    var pasterView = new PasterView();

    window.addEventListener('load', function () {

        pasterView.init(${model.id});

    <c:if test="${not empty currentUser or allowAnonymousCommentsCreate}">

        pasterView.setupCommentsAdd(${model.id});

    </c:if>



    <c:if test="${availablePrevList.count > 0}">
        document.getElementById('pageLoadSpinner').style.display='';
        pasterView.setupLazy('<c:url value="/main/paste/raw/view"/>',
                '<c:url value="/main/paste"/>',
        ${availablePrevList.count},
        ${model.id},
                [${availablePrevList.itemsAsString}]);

    </c:if>

    });

</script>


<c:if test="${availablePrevList.count > 0}">
    <div id="pageLoadSpinner" >
        <i class="fa fa-spinner"></i>
        <fmt:message key="action.loading"/>
    </div>
    <div id="morePages"></div>
</c:if>

