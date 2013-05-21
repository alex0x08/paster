<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<tiles:importAttribute name="model" />
<tiles:importAttribute name="modelName" />



<c:if test="${model.owner ne null}">
    ,  by
    <a href="http://ru.gravatar.com/site/check/${model.owner.username}" title="GAvatar">
        <img style="vertical-align: middle;padding-bottom: 2px;" src="<c:out value='http://www.gravatar.com/avatar/${model.owner.avatarHash}?s=32'/>"/>
    </a>
                <span style="display: inline;  ">
                        <a title="Contact ${model.owner.name}"  href="mailto:${model.owner.username}?subject=<c:out value='${model.name}' escapeXml="true"/>"><c:out value="${model.owner.name}" /></a>
                </span>
    ,
</c:if>