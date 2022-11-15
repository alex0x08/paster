<%--

    Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
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
<script src="<c:url value='/main/resources/${appId}/paster/js/paste-view/paste-view.js'/>"></script>
<script type="text/javascript">

        SyntaxHighlighter.config.tagName = "pre";
     
        <%--
                EpicEditor(for markdown) global options
        --%>

        <c:url var="epicEditorUrl" value="/main/resources/${appId}/local_components/epiceditor" />

    // edic markdown editor, used in 'add comment' form
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
            autoSave: 50000
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
    // check if we need to render 'edit' buttons
    const ALLOW_EDIT =  "true" === '${not empty currentUser or allowAnonymousCommentsCreate}';
    // instantiate 'view class'
    var pasterView = new PasterView();
    // on page load
    window.addEventListener('load', function () {
        // init view class
        pasterView.init(MODEL_ID,ALLOW_EDIT);
        // attach & show comments
        pasterView.setupCommentsAdd(MODEL_ID);
    // render this block if we have more pastas
    // scroll down will fetch next record
    <c:if test="${availablePrevList.count > 0}">
        pasterView.setupLazy("<c:url value='/main/paste/raw/view'/>",
        "<c:url value='/main/paste'/>",
        ${availablePrevList.count},
        ${model.id},
        [${availablePrevList.itemsAsString}]);
    </c:if>
    });
</script>
<!-- add polling script -->
<jsp:include page="/WEB-INF/pages/common/paste-update-poll.jsp"/>
