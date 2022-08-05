<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

 <c:if test="${previousStep!=null}">
    <button type="submit" class="btn btn-primary">
      <i class="fa fa-arrow-left" aria-hidden="true"></i>
       Previous
    </button>
</c:if>

<c:if test="${nextStep!=null}">
 <button type="submit" class="btn btn-primary">
     <i class="fa fa-arrow-right" aria-hidden="true"></i>
       Next
</button>
</c:if>

<c:if test="${nextStep==null}">
 <button type="submit" class="btn btn-primary">
       <i class="fa fa-sign-in"></i>
        Complete
 </button>
</c:if>
