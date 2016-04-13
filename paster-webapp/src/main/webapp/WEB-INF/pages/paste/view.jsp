<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<jsp:include page="/WEB-INF/pages/common/paste-update-poll.jsp"/>



<div class="row">


    <c:if test="${not empty availableNext and not empty availableNext.thumbImage}">
        <div class="col-md-2 hidden-sm hidden-xs">

            <a href="<c:url value="/${availableNext.id}"/>"  title="<fmt:message key="button.next"/>">
                <img width="300" height="200" class="img-thumbnail img-responsive p-comment" style="alignment-adjust: middle; width: 200px; height: 100px;"
                     src="<c:url value='/main/resources/${appId}/t/${availableNext.lastModified.time}/${availableNext.thumbImage}' >
                     </c:url>" />
            </a>
        </div>

    </c:if>
        

    <div  class="col-md-8"  >
        <jsp:include page="/WEB-INF/pages/paste/common/paste-view-top.jsp"/>
    </div>


      
    <c:if test="${availablePrev!=null and not empty availablePrev.thumbImage}">

        <div class="col-md-2 hidden-sm hidden-xs">

            <a href="<c:url value="/${availablePrev.id}"/>"  title="<fmt:message key="button.prev"/>">
                <img width="300" height="200" class="img-thumbnail img-responsive p-comment" style="alignment-adjust: middle; width: 200px; height: 100px;" 
                     src="<c:url value='/main/resources/${appId}/t/${availablePrev.lastModified.time}/${availablePrev.thumbImage}' >
                     </c:url>"/>
            </a>

        </div>
    </c:if>
        

</div>



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


   

    SyntaxHighlighter.config.tagName = "pre";

    window.addEvent('load', function () {

    

    <c:if test="${not empty currentUser or allowAnonymousCommentsCreate}">

        $('${model.id}_addCommentBtn').addEvent('click', function (event) {
            event.stop();

            this.getElementById('btnCaption').set('text', transmitText);
            this.set('disabled', true);

            $('${model.id}_addCommentForm').getElements('.disableOnSubmit').each(function (el, i) {
                el.toggle();
            });

            this.getElementById('btnIcon').toggle();
            onSaveComment('${model.id}');

        });


    </c:if>

     //   ZeroClipboard.config({swfPath: "<c:url value='/main/resources/${appId}/static/ZeroClipboard.swf'/>"});

        var clip = new ZeroClipboard(document.id("ctrlc_link"));

        clip.on('aftercopy', function (event) {
            growl.notify(event.data["text/plain"].length + ' symbols copied to clipboard.');
        });

        var clipLine = new ZeroClipboard(document.id("ctrlc_line"));

        clipLine.on('aftercopy', function (event) {
            growl.notify(event.data["text/plain"].length + ' symbols copied to clipboard.');
        });

        SyntaxHighlighter.highlight(${model.id}, {}, $('${model.id}_pasteText'), true, true);

    <c:if test="${availablePrevList.count > 0}">
        $('pageLoadSpinner').toggle();
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
                }, beforeLoad: function (page) {
                    //alert(page);
                    //SyntaxHighlighter.hideEditForm(this.);
                    $('pageLoadSpinner').setStyle('display', '');
                }, afterAppend: function (block, page) {

                    var ptext = document.getElementById(page + '_pasteText');

                    SyntaxHighlighter.highlight(page, {}, ptext, false, false);

                    //ptext.setStyle('display', 'none');

                    $(page + '_addCommentBtn').addEvent('click', function () {
                        this.getElementById('btnCaption').set('text', transmitText).disabled = true;
                        this.getElementById('btnIcon').setStyle('display', '');
                        onSaveComment(page);
                    });

                    try {
                        history.pushState({id: page}, "Page " + page, userPageUrl + "/" + page);
                    } catch (e) {
                    }
                    bindDeleteDlg(block);

                    initDraw(page);
                    showAll(page);

                    $('pageLoadSpinner').setStyle('display', 'none');
                    $(page + '_pasteText').grab($('pageLoadSpinner'), "after");
                }

            });
        };

    </script>

    <div id="pageLoadSpinner" >
        <i class="fa fa-spinner"></i>

        <fmt:message key="action.loading"/>
    </div>
    <div id="morePages"></div>
</c:if>



<c:url var="drawImg" 
       value='/main/resources/${appId}/r/${model.lastModified.time}/${model.reviewImgData}'/>

<script type="text/javascript">
    var $j = jQuery.noConflict();


    
    function getTextSizes(el) {
        var obj = el.getComputedSize();

        var h = parseInt(obj["totalHeight"]),
                w = parseInt(obj["totalWidth"]);
        return [h, w];
    }

    function showAll(modelId) {

        $(modelId + "_drawBlock").setStyle("display", "none");

        var sizes = getTextSizes($(modelId + "_pasteText"));

        var sketch = $(modelId + "_sketch_ro");

        sketch.set({
            'height': sizes[0],
            'width': sizes[1]
        });

        $(modelId + "_all").show();
        
        $(modelId + "_drawBlock").hide();

    }

    function showComments(modelId) {


        $(modelId + "_drawBlock").hide();
        
        $(modelId + "_all").hide();

    }

    function showDrawArea(modelId) {

        var sizes = getTextSizes($(modelId + "_pasteText"));

        var area = $(modelId + "_drawArea"),
                sketch = $(modelId + "_sketch");

        area.set({
            styles: {
                'height': sizes[0],
                'width': sizes[1]
            }
        });

        sketch.set({
            'height': sizes[0],
            'width': sizes[1]
        });

        canvas = document.getElementById(modelId + '_sketch');
        ctx = canvas.getContext('2d');

    <c:if test="${model.reviewImgData!=null}">
        
    img = new Image();
        img.src = "${drawImg}";
        img.onload = function () {
            sk = $j('#' + modelId + '_sketch').sketch();
            ctx.drawImage(img, 0, 0, img.width, img.height, 0, 0, sizes[1], sizes[0]);
        }
        
    
    </c:if>
        
        
        $(modelId + "_drawBlock").show();
        $(modelId + "_all").hide();
    }


    function initDraw(modelId) {

        $j.each(['#ff1101', '#ff0', '#0f0', '#0ff', '#00f', '#6d47e7', '#000', '#fff'], function () {
            $j('#' + modelId + '_centerPanel .tools')
                    .append("<a href='#" + modelId + "_sketch' class='btn btn-xs' data-color='" + this + "' style='border:1px solid black; background: " + this + ";'>&nbsp;&nbsp;</a> ");
        });
        $j.each([1,3, 5, 10, 15], function () {
            $j('#' + modelId + '_centerPanel .tools')
                    .append("<a href='#" + modelId + "_sketch' data-size='" + this + "' style='background: #ccc'>" + this + "</a> ");
        });

        $j('#' + modelId + '_sketch').sketch();
        $(modelId + '_saveReviewBtn').addEvent('click', function (event) {

            event.stop();
            onSaveReviewDraw(modelId);
        });

    }    
   

    function onSaveComment(modelId) {
        console.log('_on save comment ' + modelId);

        var thumbImg = document.getElementById(modelId + '_thumbImgComment');

     
        html2canvas(document.getElementById(modelId + '_centerPanel'), {
           logging:true,
            onrendered: function (canvas) {


                var img = Pixastic.process(canvas, "crop", {
                    rect: {
                        left: 350, top: 100, width: 600, height: 800
                    }
                });

                img = Canvas2Image.saveAsPNG(img, true, 300, 200);
                // document.body.appendChild(img);
                thumbImg.set('value', img.src);

                //     console.log(img.src);
                   $(modelId + "_addCommentForm").submit();

            }

        });



    }

    function onSaveReviewDraw(modelId) {

        console.log('saving review..');

        var reviewImg = document.getElementById(modelId + '_reviewDrawImg');

        var imgData = $j('#' + modelId + '_sketch').sketch().getData();
        reviewImg.set('value', imgData);

        var thumbImg = document.getElementById(modelId + '_thumbImg');

        showAll(modelId);

        var sketch = $(modelId + "_sketch_ro");

         sketch.setStyle('background-image','url('+imgData+')');
         

        html2canvas($(modelId + '_centerPanel'), {
            allowTaint: true,
            taintTest: false,
            onrendered: function (canvas) {
                var img = Pixastic.process(canvas, "crop", {
                    rect: {
                        left: 350, top: 150, width: 600, height: 800
                    }
                });
                
                img2 = Canvas2Image.saveAsPNG(img, true, 300, 200);
                thumbImg.set('value', img2.src);

                $(modelId + "_saveReviewDraw").submit();
            }
        });


    }


 

    window.addEvent('load', function () {
        initDraw(${model.id});
        showAll(${model.id});
      
    });




</script>