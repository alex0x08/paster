<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<fmt:message key="action.success"/>,
   
<a href="javascript: window.parent.createNewPasteDlg.hide();window.parent.location.reload()"><fmt:message key="button.close"/></a>


<script type="text/javascript">
    
    window.addEvent('domready', function() {
        //window.parent.createNewPasteDlg.hide();
        
        window.parent.location.reload();
    });
</script>