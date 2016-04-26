<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<c:choose>
    <c:when test="${systemInfo.appProfile.code =='PRODUCTION'}">



    </c:when>

    <c:otherwise>

        <link type="text/css" rel="stylesheet"
              href="<c:url value="/main/resources/${appId}/paster/css/paste-view/paste-view.css"/>"/>



        <script src="<c:url value='/main/resources/${appId}/local_components/pixastic/canvas2image.js'/>"></script>

        <script src="<c:url value='/main/resources/${appId}/local_components/pixastic/canvas-to-blob.js'/>"></script>
        <script src="<c:url value='/main/resources/${appId}/local_components/pixastic/pixastic.core.js'/>"></script>
        <script src="<c:url value='/main/resources/${appId}/local_components/pixastic/crop.js'/>"></script>


        <script src="<c:url value='/main/resources/${appId}/bower_components/SyntaxHighlighter/scripts/XRegExp.js'/>"></script>

        <script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/shCore.js'/>"></script>
        <script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/shLegacy.js'/>"></script>
        <script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/shUber.js'/>"></script>


        <script src="<c:url value='/main/resources/${appId}/paster/js/paste-view/sketch.js'/>"></script>


    </c:otherwise>

</c:choose>
