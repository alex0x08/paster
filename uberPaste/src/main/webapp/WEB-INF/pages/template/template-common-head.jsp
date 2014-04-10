<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:choose>
                <c:when test="${not empty systemInfo.project.iconImage}">
<link id="favicon" rel="icon" type=​"image/​jpeg"  href="${systemInfo.project.iconImage}"/>
                </c:when>
    <c:otherwise>
<link rel="icon" href="<c:url value='/main/static/${appVersion}/images/ninja.png'/>"/>
    </c:otherwise>
</c:choose>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9" />
<link href="<c:url value='/main/static/${appVersion}/css/app.css'/>" rel="stylesheet" type="text/css">
<script src="<c:url value='/main/static/${appVersion}/js/all.js'/>"></script>

<script  type="text/javascript">
            var transmitText = '<fmt:message key="action.sending"/>';
            var growl= null;
        window.addEvent('domready',function() {
            growl = new Growler.init();

        });
</script>
 