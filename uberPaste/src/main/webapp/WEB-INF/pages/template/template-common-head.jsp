<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:choose>
                <c:when test="${not empty systemInfo.project.iconImage}">
<link id="favicon" rel="icon" type="image/jpeg"  href="${systemInfo.project.iconImage}"/>
                </c:when>
    <c:otherwise>
<link rel="icon" href="<c:url value='/main/static/${appVersion}/images/ninja.png'/>"/>
    </c:otherwise>
</c:choose>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

   <!-- HTTP 1.1 -->
        <meta http-equiv="Cache-Control" content="no-store"/>
        <!-- HTTP 1.0 -->
        <meta http-equiv="Pragma" content="no-cache"/>
        <!-- Prevents caching at the Proxy Server -->
        <meta http-equiv="Expires" content="0"/>
     

<link href="<c:url value='/main/assets/${appVersion}/paster/minified/all/css/all-style.min.css'/>" rel="stylesheet" type="text/css">
<script src="<c:url value='/main/assets/${appVersion}/paster/minified/all/js/all-script.min.js'/>"></script>

<script  type="text/javascript">
            var transmitText = '<fmt:message key="action.sending"/>';
            var growl= null;
        window.addEvent('domready',function() {
            growl = new Growler.init();            
        });
        
</script>
 