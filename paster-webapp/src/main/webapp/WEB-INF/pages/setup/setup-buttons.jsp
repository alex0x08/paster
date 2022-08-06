<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

 <c:if test="${previousStep!=null}">
    <c:url var="prevUrl" value='/main/setup/prev/${previousStep.stepKey}' />

    <button type="submit" formaction="${prevUrl}" class="btn btn-primary">
      <i class="fa fa-arrow-left" aria-hidden="true"></i>
         <fmt:message key="button.prev" /></span>
    </button>
</c:if>

<c:if test="${nextStep!=null}">
 <button type="submit" class="btn btn-primary">
     <i class="fa fa-arrow-right" aria-hidden="true"></i>
         <fmt:message key="button.next" /></span>
</button>
</c:if>

<c:if test="${nextStep==null}">
 <button type="submit" class="btn btn-primary">
       <i class="fa fa-sign-in"></i>
        Complete
 </button>
</c:if>
