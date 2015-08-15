<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<tiles:importAttribute name="model" />
<tiles:importAttribute name="modelName" />

<c:if test="${not empty model.commentCount and model.commentCount>0}">
    <span title="has ${model.commentCount} comments ">
    <span style="vertical-align: middle;" class="i">C</span>
    <span style="font-size: 10px;"> x <c:out value="${model.commentCount}"/></span>
    </span>
    ,
</c:if>
