<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

 <c:if test="${previousStep!=null}">
    <c:url var="prevUrl" value='/main/setup/prev/${previousStep.stepKey}' />

    <button type="submit" formaction="${prevUrl}" class="btn btn-primary">
         <fmt:message key="button.prev" />
    </button>
</c:if>

<c:if test="${nextStep!=null}">
 <button type="submit" class="btn btn-primary">
         <fmt:message key="button.next" />
</button>
</c:if>

<c:if test="${nextStep==null}">

 <button type="submit" class="btn btn-primary">
         <fmt:message key="button.complete" />
</button>


</c:if>
