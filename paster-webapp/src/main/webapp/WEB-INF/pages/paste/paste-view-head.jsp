<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<link type="text/css" rel="stylesheet"
      href="<c:url value="/main/resources/${appId}/paste-view/view.css"/>"/>

<script src="<c:url value='/main/resources/${appId}/paste-view/xregexp.js'/>"></script>

<script src="<c:url value='/main/resources/${appId}/paste-view/shCore.js'/>"></script>

<script src="<c:url value='/main/resources/${appId}/paste-view/jquery-latest.js'/>"></script>

<script src="<c:url value='/main/resources/${appId}/paste-view/sketch.js'/>"></script>

<script src="<c:url value='/main/assets/${appId}/paster/minified/paster-view/js/paste-view-script.js'/>"></script>



<script type="text/javascript" src="<c:url value="/main/resources/${appId}/paste-view/temp/base64.js"/>"></script>
<script type="text/javascript" src="<c:url value="/main/resources/${appId}/paste-view/temp/html2canvas.js"/>"></script>
<script type="text/javascript" src="<c:url value="/main/resources/${appId}/paste-view/temp/canvas2image.js"/>"></script>
<script type="text/javascript" src="<c:url value="/main/resources/${appId}/paste-view/temp/canvas-to-blob.js"/>"></script>
<script type="text/javascript" src="<c:url value="/main/resources/${appId}/paste-view/temp/pixastic.core.js"/>"></script>
<script type="text/javascript" src="<c:url value="/main/resources/${appId}/paste-view/temp/crop.js"/>"></script>

<style>
*.html2canvasreset{
    overflow: visible !important;
    width: auto !important;
    height: auto !important;
    max-height: auto !important;
}
</style>