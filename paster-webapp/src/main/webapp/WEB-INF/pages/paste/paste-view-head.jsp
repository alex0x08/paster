<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

   <link type="text/css" rel="stylesheet"
              href="<c:url value="/main/resources/${appId}/paster/css/paste-view/paste-view.css"/>"/>

        <script src="<c:url value='/main/resources/${appId}/local_components/pixastic/canvas2image.js'/>"></script>
        <script src="<c:url value='/main/resources/${appId}/local_components/pixastic/canvas-to-blob.js'/>"></script>
        <script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/XRegExp.js'/>"></script>
        <script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/shCore.js'/>"></script>
        <script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/shLegacy.js'/>"></script>
        <script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/shUber.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/local_components/epiceditor/js/epiceditor.js'/>"></script>
        <script type="text/javascript" src="<c:url value='/main/resources/${appId}/local_components/marked/marked.min.js'/>"></script>

        <script src="<c:url value='/main/resources/${appId}/paster/js/paste-view/sketch.js'/>"></script>
          <script src="<c:url value='/main/resources/${appId}/paster/js/paste-view/paste-view.js'/>"></script>

<script  type="text/javascript">


        <%--

        EpicEditor (for markdown) global options

        --%>


    <c:url var="epicEditorUrl" value="/main/resources/${appId}/local_components/epiceditor"/>


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



</script>