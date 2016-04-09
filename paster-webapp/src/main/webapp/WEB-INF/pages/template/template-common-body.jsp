<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
 

<div data-behavior="BS.Popup" class="modal hide" id="deletePopup">

    <div class="modal-dialog">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">X</button>
                <h4 class="modal-title" id="dialogTitle"></h4>
            </div>
            <div id="dialogMessage" class="modal-body">
                
            </div>
            <div class="modal-footer">
                <a id="dialogAction" href="" class="btn btn-primary"></a>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
               
            </div>

        </div>
    </div>
</div>


<div data-behavior="BS.Popup" class="modal hide" id="newPasteDialog" >

    <div class="modal-dialog">
        <div class="modal-content" style="padding:0;margin:0;">
           
            <div id="dialogMessage" class="modal-body">                
            </div>          

        </div>
    </div>
</div>

   

  <script  type="text/javascript">
      
       window.addEvent('load', function() {
           
         
            bindDeleteDlg(document.body);
        });

      
      function showModal(dlg,redirectUrl,action,title,message) {
                           
       
         if (title!==null) {
               dlg.getElementById('dialogTitle').set('text',title);
         }
         
        if (action!==null) {
             dlg.getElementById('dialogAction').set('text',action).set('href',redirectUrl);
         }
         
         dlg.getElementById('dialogMessage').set('html',message);
         
          new Bootstrap.Popup(dlg, {animate:false,closeOnEsc:true}).show();
      
      };
      
      function bindDeleteDlg(parent) {

        parent.getElements('.deleteBtn').each(function(el, i) {
                el.addEvent("click", function(e) {
                e.stop();
                        var source = e.target || e.srcElement;
                        showModal($('deletePopup'),source.parentElement.href,
                                  '<fmt:message key='button.delete'/>',
                                  '<fmt:message key='dialog.confirm.remove'/>',
                                        source.parentElement.getElementById('dialogMsg').innerHTML);
                });
            });    

          
      };     
    
      
  </script>
  
       
  