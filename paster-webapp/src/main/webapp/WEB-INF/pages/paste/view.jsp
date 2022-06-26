<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>



<jsp:include page="/WEB-INF/pages/common/paste-update-poll.jsp"/>



<div class="row">
    <c:if test="${not empty availableNext and not empty availableNext.thumbImage}">
        <div class="col-md-2 hidden-sm hidden-xs">
            <a href="<c:url value="/${availableNext.id}"/>"  title="<fmt:message key="button.next"/>">
                <img width="300" height="200" class="img-thumbnail img-responsive p-comment" style="alignment-adjust: middle; width: 200px; height: 100px;"
                     src="<c:url value='/main/resources/${appId}/t/${availableNext.lastModified.time}/paste_content/${availableNext.thumbImage}' >
                     </c:url>" />
            </a>
        </div>

    </c:if>


    <div  class="col-md-8"  >
        <jsp:include page="/WEB-INF/pages/paste/common/paste-view-top.jsp"/>
    </div>



    <c:if test="${availablePrev!=null and not empty availablePrev.thumbImage}">
        <div class="col-md-2 hidden-sm hidden-xs">
            <a href="<c:url value="/${availablePrev.id}"/>"  title="<fmt:message key="button.prev"/>">
                <img width="300" height="200" class="img-thumbnail img-responsive p-comment" style="alignment-adjust: middle; width: 200px; height: 100px;" 
                     src="<c:url value='/main/resources/${appId}/t/${availablePrev.lastModified.time}/paste_content/${availablePrev.thumbImage}' >
                     </c:url>"/>
            </a>

        </div>
    </c:if>


</div>



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


    window.pica.debug = console.log.bind(console);
    window.pica.WEBGL = true;


    var pasterView = new PasterView();

    window.addEvent('load', function () {

        pasterView.init(${model.id});

    <c:if test="${not empty currentUser or allowAnonymousCommentsCreate}">

        pasterView.setupCommentsAdd(${model.id});

    </c:if>



    <c:if test="${availablePrevList.count > 0}">
        $('pageLoadSpinner').toggle();

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

