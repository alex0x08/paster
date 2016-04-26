<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


        <%--

        Headers for all pages
        
        --%>    

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


<c:choose>
    <c:when test="${systemInfo.appProfile.code =='PRODUCTION'}">


    </c:when>
    <c:otherwise>



        <link href="<c:url value='/main/resources/${appId}/local_components/behavior_ui/dev/bootstrap/css/behavior-ui-bootstrap.css'/>" 
              rel="stylesheet" type="text/css"/>

        <link href="<c:url value='/main/resources/${appId}/paster/css/all/mnmlicons.css'/>" 
              rel="stylesheet" type="text/css"/>


        <link href="<c:url value='/main/resources/${appId}/paster/css/all/app.css'/>" 
              rel="stylesheet" type="text/css"/>

        <!-- build:css assets/styles/vendor.css -->
        <!-- bower:css -->
        <link type="text/css" href="<c:url value='/main/resources/${appId}/bower_components/components-font-awesome/css/font-awesome.css'/>" rel="stylesheet"/>
        <!-- endbower -->
        <!-- endbuild -->
        <!-- build:css assets/styles/main.css -->
        <!-- endbuild -->




        <!-- build:js scripts/vendor.js -->
        <!-- bower:js -->
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/bower_components/mootools/dist/mootools-core.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/bower_components/mootools/dist/mootools-core-compat.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/bower_components/zeroclipboard/dist/ZeroClipboard.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/bower_components/tinycon/tinycon.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/bower_components/jquery-no-conflict-1.9/jquery.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/bower_components/EpicEditor/epiceditor/js/epiceditor.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/bower_components/marked/lib/marked.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/bower_components/pdfmake/build/pdfmake.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/bower_components/pdfmake/build/vfs_fonts.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/bower_components/html2canvas/build/html2canvas.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/bower_components/canvas2image/canvas2image/canvas2image.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/bower_components/base64/base64.js'/>"></script>
        <!-- endbower -->
        <!-- endbuild -->


        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/local_components/behavior_ui/dev/js/behavior-ui.js'/>"></script>

        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/paster/js/all/LazyPagination.js'/>"></script>

        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/paster/js/all/growler.js'/>"></script>

        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/paster/js/all/paster-app.js'/>"></script>


    </c:otherwise>
</c:choose>


<script  type="text/javascript">



        <%--

        EpicEditor (for markdown) global options
        
        --%>    


    <c:url var="epicEditorUrl" value="/main/resources/${appId}/bower_components/EpicEditor/epiceditor"/>


    var globalEpicEditorOpts = {
        container: 'SET-IN-EDITOR',
        textarea: null,
        basePath: '${epicEditorUrl}',
        clientSideStorage: false,
        localStorageName: 'epiceditor',
        useNativeFullsreen: true,
        parser: marked,
        file: {
            name: 'epiceditor',
            defaultContent: '',
            autoSave: 100
        },
        theme: {
            base: '/themes/base/epiceditor.css',
            preview: '/themes/preview/bartik.css',
            editor: '/themes/editor/epic-light.css'
        },
        button: {
            preview: true,
            fullscreen: true
        },
        focusOnLoad: true,
        shortcut: {
            modifier: 18,
            fullscreen: 70,
            preview: 80
        },
        string: {
            togglePreview: 'Toggle Preview Mode',
            toggleEdit: 'Toggle Edit Mode',
            toggleFullscreen: 'Enter Fullscreen'
        }
    };


    
        <%--

        Marked (markdown render) global options
        
        --%>    

    
    marked.setOptions({
        renderer: new marked.Renderer(),
        gfm: true,
        tables: true,
        breaks: false,
        pedantic: false,
        sanitize: true,
        smartLists: true,
        smartypants: false
    });


    
        <%--

        Paster js initialization

        --%>    

    // This will create a shortcut for `extend()`.
    Class.Mutators.Static = function (members) {
        this.extend(members);
    };


    var PasterI18n = new Class({
        Static: {
            text: {
                notify: {
                    transmitMessage: '<fmt:message key="action.sending"/>'
                },
                dialog: {
                    removal: {
                        title: '<fmt:message key='button.delete'/>',
                        message: '<fmt:message key='dialog.confirm.remove'/>'
                    }

                }
            }

        }
    });




    var pasterApp = new PasterApp();

    window.addEvent('load', function () {

        pasterApp.appInit(document.body);
    });


</script>
