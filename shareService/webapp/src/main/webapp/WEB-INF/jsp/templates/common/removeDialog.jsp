<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>



<div class="container">
    
    <button id="removeBtn" class="btn">
       <fmt:message key="button.delete"/> 
    </button>
    
    
    <div class="delete_dosar">delete</div>
</div>

<!-- Boostrap modal dialog -->
<div id="delete_confirmation" class="modal hide fade" style="display: none; ">
    <div class="modal-header">
        <a href="#" class="close" data-dismiss="modal">x</a>
        <h3>Are you sure?</h3>
    </div>
    <div class="modal-body">
        <div class="paddingT15 paddingB15" id="modal_text">    
            Are you sure with this?
        </div>

    </div>
    <div class="modal-footer">
        <a href="#" class="btn confirm_delete_the_item no_return">yes</a>
        <a href="#" class="btn btn-secondary " data-dismiss="modal">no</a>

    </div>
</div>

<script type="text/javascript">

    $(document).ready(function() {

        $("#removeBtn").live('click', function() {

            $('#delete_confirmation').modal({backdrop: false}, "show");
            $('.confirm_delete_the_item').live('click', function(e) {
                $('#delete_confirmation').modal("hide");
                e.preventDefault();
                alert('x');
            });
            return false;

        });

    });
</script>
