<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:url var="url" value='/main/paste/list/search' />
    <form:form cssClass="d-flex" role="search" action="${url}" id="searchForm" modelAttribute="query" method="POST">
        <fmt:message key='paste.search.placeholder' var="searchPlaceHolder" />
        <form:input path="query" name="query" id="pquery" cssClass="form-control form-control-sm me-2"
            placeholder="${searchPlaceHolder}" autocomplete="true" />
        <button class="btn btn-primary btn-sm" id="doSearchBtn" type="submit">
            <span id="btnCaption">
                <fmt:message key="button.search" /></span>
            <i id="btnIcon" class="fa fa-spinner" style="display:none;"></i>
        </button>
        <form:errors path="query" cssClass="error" element="div" />
    </form:form>
<%--
<div class="row">
    <div class="column grid-10">

            <span style="font-size: 12px;display:block;padding-bottom: 10px;" >
            <fmt:message key="search.query.samples"/>:
         "<a href="<c:url value='/main/paste/list/search?query=id:1'/>">id:1</a>",
         "<a href="<c:url value='/main/paste/list/search?query=name:test'/>">name:test</a>" ,

        <c:url  var="lm_sample_url" value="/main/paste/list/search">
            <c:param name='query' value='lastModified:["30.01.2013" TO "31.01.2013"]'/>
        </c:url>
        "<a href='<c:out value="${lm_sample_url}"/>'> lastModified:["30.01.2013" TO "31.01.2013"]</a>"
 </span>
    </div>
</div>
--%>
<script type="text/javascript">
    window.addEventListener('load', function () {
        const el = document.getElementById('doSearchBtn');
        el.addEventListener('click', function () {
            const el2 = this.querySelector('#btnCaption');
            el2.text = transmitText;
            el2.disabled = true;
            this.querySelector('#btnIcon').style.display = '';
            el.submit();
        });
    });
</script>
