<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<link rel="apple-touch-icon" sizes="57x57" href="${ctx}/favicon/apple-touch-icon-57x57.png">
<link rel="apple-touch-icon" sizes="60x60" href="${ctx}/favicon/apple-touch-icon-60x60.png">
<link rel="apple-touch-icon" sizes="72x72" href="${ctx}/favicon/apple-touch-icon-72x72.png">
<link rel="apple-touch-icon" sizes="76x76" href="${ctx}/favicon/apple-touch-icon-76x76.png">
<link rel="apple-touch-icon" sizes="114x114" href="${ctx}/favicon/apple-touch-icon-114x114.png">
<link rel="apple-touch-icon" sizes="120x120" href="${ctx}/favicon/apple-touch-icon-120x120.png">
<link rel="apple-touch-icon" sizes="144x144" href="${ctx}/favicon/apple-touch-icon-144x144.png">
<link rel="apple-touch-icon" sizes="152x152" href="${ctx}/favicon/apple-touch-icon-152x152.png">
<link rel="apple-touch-icon" sizes="180x180" href="${ctx}/favicon/apple-touch-icon-180x180.png">
<link rel="icon" type="image/png" href="${ctx}/favicon/favicon-32x32.png" sizes="32x32">
<link rel="icon" type="image/png" href="${ctx}/favicon/android-chrome-192x192.png" sizes="192x192">
<link rel="icon" type="image/png" href="${ctx}/favicon/favicon-96x96.png" sizes="96x96">
<link rel="icon" type="image/png" href="${ctx}/favicon/favicon-16x16.png" sizes="16x16">
<link rel="manifest" href="${ctx}/favicon/manifest.json">
<link rel="mask-icon" href="${ctx}/favicon/safari-pinned-tab.svg" color="#5bbad5">
<meta name="msapplication-TileColor" content="#00aba9">
<meta name="msapplication-TileImage" content="${ctx}/favicon/mstile-144x144.png">
<meta name="theme-color" content="#ffffff">

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<!-- HTTP 1.1 -->
<meta http-equiv="Cache-Control" content="no-store"/>
<!-- HTTP 1.0 -->
<meta http-equiv="Pragma" content="no-cache"/>
<!-- Prevents caching at the Proxy Server -->
<meta http-equiv="Expires" content="0"/>


<link href="<c:url value='/main/assets/${appId}/paster/minified/all/css/all-style.min.css'/>" rel="stylesheet" type="text/css">

<style>
    @media screen and (min-width: 768px) {

        #newPasteDialog .modal-dialog  {width:1140px;}

    }
</style>
<script src="<c:url value='/main/assets/${appId}/paster/minified/all/js/all-script.js'/>"></script>

<script  type="text/javascript">


    var transmitText = '<fmt:message key="action.sending"/>';
    var growl = null;
    window.addEvent('domready', function () {
        growl = new Growler.init();
    });

</script>
