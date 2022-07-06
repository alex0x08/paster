<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<%-- 
    This will be injected at bottom of paste edit page

--%>

<script src="<c:url value='/main/resources/${appId}/local_components/ace/src-min-noconflict/ace.js'/>" type="text/javascript"
    charset="utf-8"></script>

<script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/XRegExp.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/shCore.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/shUber.js'/>"></script>

<script src="<c:url value='/main/resources/${appId}/paster/js/paste-edit/word-count.js'/>" type="text/javascript"
    charset="utf-8"></script>
<script src="<c:url value='/main/resources/${appId}/paster/js/paste-edit/paste-edit.js'/>" type="text/javascript"
    charset="utf-8"></script>



<script type="text/javascript">

    // will be substituted from backend
    const EDITOR_TYPE = '${model.codeType}';

    const pasterEdit = new PasterEdit();


    window.addEventListener('load', function () {

        pasterEdit.init(EDITOR_TYPE);

        window.addEventListener('paste', function (e) {
            pasterEdit.onPaste(e);
        });

    });

</script>