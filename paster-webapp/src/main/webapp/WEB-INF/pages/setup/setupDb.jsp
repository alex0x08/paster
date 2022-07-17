<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class='row'>
    <div class='col-md-offset-3 col-xs-4 col-md-5'>

     YO! Here will be installation setup.

                <c:url var="finalizeInstallUrl" value='/main/setup/finalizeInstall' />

                 <form:form cssClass="form-inline" style="padding-left:1em;" role="form" action="${finalizeInstallUrl}" method="POST">
                     <button type="submit" class="btn btn-sm">
                         <i class="fa fa-sign-in"></i>Finish</a>
                     </button>
                 </form:form>


    </div>
</div>


