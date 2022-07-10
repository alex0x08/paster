<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>



<script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/XRegExp.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/shCore.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/shAll.js'/>"></script>
<script type="text/javascript"
    src="<c:url value='/main/resources/${appId}/local_components/epiceditor/js/epiceditor.js'/>"></script>
<script type="text/javascript"
    src="<c:url value='/main/resources/${appId}/local_components/marked/marked.min.js'/>"></script>
<script type="text/javascript"
    src="<c:url value='/main/resources/${appId}/local_components/sketchjs/sketch.js'/>"></script>
<%--
<script src="<c:url value='/main/resources/${appId}/paster/js/paste-view/sketch.js'/>"></script>
--%>

<script src="<c:url value='/main/resources/${appId}/paster/js/paste-view/paste-view.js'/>"></script>

<script type="text/javascript">


        SyntaxHighlighter.config.tagName = "pre";
     
        <%--

        EpicEditor(for markdown) global options

        --%>


        <c:url var="epicEditorUrl" value="/main/resources/${appId}/local_components/epiceditor" />


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

        Marked(markdown render) global options

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

<script src="<c:url value='/main/resources/${appId}/paster/js/all/LazyPagination.js'/>"></script>



<script type="text/javascript">

    const MODEL_ID = '${model.id}';

    const ALLOW_EDIT =  "true" === '${not empty currentUser or allowAnonymousCommentsCreate}';

    var pasterView = new PasterView();

    window.addEventListener('load', function () {

        pasterView.init(MODEL_ID,ALLOW_EDIT);

        pasterView.setupCommentsAdd(MODEL_ID);

  
        document.getElementById('pageLoadSpinner').style.display='';
        pasterView.setupLazy("<c:url value='/main/paste/raw/view'/>",
                "<c:url value='/main/paste'/>",
        ${availablePrevList.count},
        ${model.id},
                [${availablePrevList.itemsAsString}]);

        <%--
    <c:if test="${availablePrevList.count > 0}">
  
    </c:if> --%>

    });

</script>



<jsp:include page="/WEB-INF/pages/common/paste-update-poll.jsp"/>