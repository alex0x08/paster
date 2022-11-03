<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class='row justify-content-md-center'>
    <div class='col-auto'>
        <p>
            <fmt:message key='paster.setup.step.completed.title'/>
        </p>
                <c:url var="finalizeInstallUrl" value='/main/setup/doFinalizeInstall' />

                 <form:form cssClass="form-inline"
                        style="padding-left:1em;"
                        role="form"
                        action="${finalizeInstallUrl}" method="POST">
                     <button type="submit" class="btn btn-danger">
                         <fmt:message key="button.restart" />
                         </a>
                     </button>
                 </form:form>
    </div>
</div>


