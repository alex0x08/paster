<%-- 
    Document   : template-common-body
    Created on : 20.09.2013, 9:57:48
    Author     : aachernyshev
--%>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

    <script type="text/javascript" src="<c:url value='/main/static/${appVersion}/libs/flowplayer/flowplayer-3.2.12.min.js'/>"></script>

    <script type="text/javascript">
        
        var commentsUrl = '<c:url value="/main/file/raw/comments"/>';

        function showError(message) {
            
         console.log('js error: ' + message);
         $('#jsErrors').append('<div id="jsErrorMessage" class="alert alert-block alert-danger fade in">'+
                        '<button type="button" class="close" data-dismiss="alert"  >&times;</button>'+
                        '<h4>Oh snap! You got an error!</h4>'+
                        '<p>'+message+'</p></div>');         
        }
        
        function initZoombox() {
            
         $('a.zoombox').zoombox({
                theme       : 'simple',        //available themes : zoombox,lightbox, prettyphoto, darkprettyphoto, simple
                opacity     : 0.3,              // Black overlay opacity
                duration    : 800,              // Animation duration
                animation   : true,             // Do we have to animate the box ?
                width       : 1024,              // Default width
                height      : 768,              // Default height
                gallery     : true,             // Allow gallery thumb view
                autoplay : true,                // Autoplay for video
                overflow: true
            });   
        }
        
        function initFlowPlayer() {
      
        flowplayer("a.embedPlayer", "<c:url value='/main/static/${appVersion}/libs/flowplayer/flowplayer-3.2.16.swf'/>", {
      clip:  {
          autoPlay: false,
          autoBuffering: true
      }
           
  });
        }
        
        function initButtons() {
            
              $("[rel='tooltip']").tooltip();
  
               $(".commentsBtn").bind('click', function(e) {
               
                 var thisTab = e.target; 
                  var pageTarget = $(thisTab).attr('href');
                  
                  $(pageTarget).load(commentsUrl+'?id='+$(thisTab).attr('modelId'));
                });
        
              
         $(".pastePreviewBtn").bind('click', function() {
                var link=$(this);
           
           
                $('#targetImg').attr('src',link.attr('targetIcon'));
                $('#targetTitle').html(link.attr('targetTitle'));
           
           
                //${pasteUrl} ${externalUrl}
            
                var clink = '<iframe src="${pasteUrl}/main/paste/loadFrom?url=${externalUrl}/act/download?id=';
                clink+= link.attr('targetId');
                clink+= '" scrolling="auto" frameborder="0" style="width:640px;height:320px; "  allowTransparency="true" > </iframe>';
                
                $('#pasteContent').html(clink);
               $('#paste_preview').modal({backdrop: false}, "show");
              
                return false;

            });
   
   
            $(".fileDeleteBtn").click(function() {
                var link=$(this);           
            
                $('#deleteTargetImg').attr('src',link.attr('targetIcon'));
                $('#deleteTargetTitle').html(link.attr('targetTitle'));
            
                $('#delete_confirmation').modal({backdrop: false}, "show");
                
                $('.confirm_delete_the_item').click(function(e) {
                    $('#delete_confirmation').modal("hide");
                    e.preventDefault();
                    location.href=link.attr('deleteLink');
                    return true;
   
                });

                return false;

            });

            
        }
        
        
           $('#auth-dropdown').click(function(event){
                event.stopPropagation();
                }
            );
        
        $(document).ready(function() {
    
         initZoombox();
         initFlowPlayer();
         initButtons();
     
           /* Bootstrappable btn-group with checkables inside
         - hide inputs and set bootstrap class*/
        $('.btn-group label.btn input[type=radio]')
          .hide()
          .filter(':checked').parent('.btn').addClass('active');
   
         // showError('Fuck!');
          
         // setTimeout("showError('Die')",4000);
        });
    </script>

    