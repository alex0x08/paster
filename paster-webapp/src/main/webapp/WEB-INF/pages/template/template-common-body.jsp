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

   
       
  