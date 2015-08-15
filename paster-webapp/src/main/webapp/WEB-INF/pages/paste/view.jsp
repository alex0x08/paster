<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<jsp:include page="/WEB-INF/pages/common/paste-update-poll.jsp"/>


<jsp:include page="/WEB-INF/pages/paste/common/paste-view-common.jsp"/>


<div id="numSpace" class="line" >
</div>

<span id="pasteLineCopyBtn" style="display:none; white-space: normal;">
    <a id="ctrlc_line" 
       data-clipboard-target="pasteLineToCopy" href="javascript:void(0);" 
        style="float:left;" title="Copy to clipboard" >
        <span class="img-map img-clip"></span>
    </a> 
</span>

<span id="pasteLineToCopy" style="display:none;">
    NONE
</span>




<script type="text/javascript">


    function toggleRight(modelId) {

                            var rightPanel = document.getElementById(modelId + '_rightPanel'),
                                    centerPanel = document.getElementById(modelId + '_centerPanel'),
                                    rightPanelCtrl = document.getElementById(modelId + '_rightPanelCtrl');

                            if (rightPanel.getStyle('display') != 'none') {
                                rightPanel.setStyle('display', 'none');
                                centerPanel.set('class', 'col-md-10');
                                rightPanelCtrl.getElement('span').set('text', '+');
                            } else {
                                centerPanel.set('class', 'col-md-8');
                                rightPanel.setStyle('display', 'inline');
                                rightPanelCtrl.getElement('span').set('text', '-');
                            }
                        };

    SyntaxHighlighter.config.tagName = "pre";

    window.addEvent('domready', function() {

  <c:if test="${shareIntegration}">
          
  $(${model.id} + '_shareFrame').setStyle('height', document.body.scrollHeight< 1024 ?  '1024px' :  document.body.scrollHeight + 'px');
  
  </c:if>

    <c:if test="${not empty currentUser or allowAnonymousCommentsCreate}">
      
      $('${model.id}_addCommentBtn').addEvent('click', function(event) {
             event.stop();

            this.getElementById('btnCaption').set('text', transmitText);
            this.set('disabled', true);
            
             $('${model.id}_addCommentForm').getElements('.disableOnSubmit').each(function(el, i) {
                 el.setStyle('display', 'none');
             });
        
            this.getElementById('btnIcon').setStyle('display', '');
            $('${model.id}_addCommentForm').submit();
            
        });

    
    </c:if>
      
        ZeroClipboard.setDefaults({moviePath: "<c:url value='/main/assets/${appId}/paster/static/ZeroClipboard.swf'/>"});

        var clip = new ZeroClipboard(document.id("ctrlc_link"));

        clip.on('complete', function(client, args) {
            growl.notify(args.text.length + ' symbols copied to clipboard.');
        });

        var clipLine = new ZeroClipboard(document.id("ctrlc_line"));

        clipLine.on('complete', function(client, args) {
            growl.notify(args.text.length + ' symbols copied to clipboard.');
        });

        SyntaxHighlighter.highlight(${model.id}, {}, $('${model.id}_pasteText'), true,true);
        
    <c:if test="${availablePrevList.count > 0}">
        $('pageLoadSpinner').setStyle('display', 'none');
        initLazy();
    </c:if>

         });

</script>


<c:if test="${availablePrevList.count > 0}">


    <c:url var="rawPageUrl" value="/main/paste/raw/view"/>
    <c:url var="userPageUrl" value="/main/paste"/>

    <script type="text/javascript">

            var userPageUrl = '${userPageUrl}', pageUrl = '${rawPageUrl}';

            function initLazy() {
                new LazyPagination(document, {
                    url: pageUrl,
                    method: 'get',
                    maxRequests: ${availablePrevList.count},
                    buffer: 100,
                    pageDataIndex: 'page',
                    idMode: true,
                    data: {
                        page: 0,
                        id: ${model.id}
                    },
                    idSet: [${availablePrevList.itemsAsString}],
                    inject: {
                        element: 'morePages',
                        where: 'before'
                    }, beforeLoad: function(page) {
                        //alert(page);
                         //SyntaxHighlighter.hideEditForm(this.);
                        $('pageLoadSpinner').setStyle('display', '');
                    }, afterAppend: function(block, page) {

                        var ptext = document.getElementById(page + '_pasteText');

                        SyntaxHighlighter.highlight(page, {}, ptext, false,false);

                        //ptext.setStyle('display', 'none');

                        $(page + '_addCommentBtn').addEvent('click', function() {
                            this.getElementById('btnCaption').set('text', transmitText).disabled = true;
                            this.getElementById('btnIcon').setStyle('display', '');
                            $(page + "_addCommentForm").submit();
                        });

                        try {
                            history.pushState({id: page}, "Page " + page, userPageUrl + "/" + page);
                        } catch (e) {
                        }
                        bindDeleteDlg(block);

                      
                        $('pageLoadSpinner').setStyle('display', 'none');
                        $(page + '_pasteText').grab($('pageLoadSpinner'),"after");
                        }

                });
            }
            ;

    </script>
    
    <div id="pageLoadSpinner" >
        <i class="fa fa-spinner"></i>
        
        <fmt:message key="action.loading"/>
    </div>
    <div id="morePages"></div>
</c:if>
