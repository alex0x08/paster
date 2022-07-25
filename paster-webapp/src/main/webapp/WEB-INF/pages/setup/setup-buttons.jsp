<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

 <c:if test="${previousStep!=null}">
    <button type="submit" class="btn btn-primary">
      <i class="fa fa-sign-in"></i>
       Previous
    </button>
</c:if>

<c:if test="${nextStep!=null}">
 <button type="submit" class="btn btn-primary">
      <i class="fa fa-sign-in"></i>
       Next
</button>
</c:if>

<c:if test="${nextStep==null}">
 <button type="submit" class="btn btn-primary">
       <i class="fa fa-sign-in"></i>
        Complete
 </button>
</c:if>
