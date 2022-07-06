<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
 

        <%--

        Common content for all pages (will be attached to bottom of body)

        
        --%>    

<div  class="modal hide" id="deletePopup">

    <div class="modal-dialog">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-bs-dismiss="modal" aria-hidden="true">X</button>
                <h4 class="modal-title" id="dialogTitle"></h4>
            </div>
            <div id="dialogMessage" class="modal-body">
                
            </div>
            <div class="modal-footer">
                <a id="dialogAction" href="" class="btn btn-primary"></a>
                <button type="button" class="btn btn-default" data-bs-dismiss="modal">Cancel</button>
               
            </div>

        </div>
    </div>
</div>


<div  class="modal hide" id="newPasteDialog" >

    <div class="modal-dialog">
        <div class="modal-content" style="padding:0;margin:0;">
           
            <div id="dialogMessage" class="modal-body">                
            </div>          

        </div>
    </div>
</div>
   
       
<script type="text/javascript" src="<c:url value='/main/resources/${appId}/local_components/jquery/jquery.slim.min.js'/>"></script>
<script type="text/javascript"
    src="<c:url value='/main/resources/${appId}/local_components/bootstrap/bootstrap.bundle.min.js'/>"></script>


<script type="text/javascript"
    src="<c:url value='/main/resources/${appId}/local_components/tinyicon/tinycon.js'/>"></script>
    
<script src="<c:url value='/main/resources/${appId}/local_components/pixastic/canvas-to-blob.min.js'/>"></script>

<script src="<c:url value='/main/resources/${appId}/local_components/pixastic/html2canvas.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/pixastic/canvas2image.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/pixastic/pica.min.js'/>"></script>

<script type="text/javascript" src="<c:url value='/main/resources/${appId}/paster/js/all/paster-app.js'/>"></script>


<script type="text/javascript">


    window.pica.prototype.debug = console.log.bind(console);


    class PasterI18nClass {
        text = {
            notify: {
                transmitMessage: '<fmt:message key="action.sending"/>'
            },
            dialog: {
                removal: {
                    title: '<fmt:message key='button.delete'/>',
                    message: '<fmt:message key='dialog.confirm.remove'/>'
                    }

            }
        }
    }

    const PasterI18n = new PasterI18nClass();


    var pasterApp = new PasterApp();

    window.addEventListener('load', function () {
        pasterApp.appInit(document.body);
    });


</script>