<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<c:url value="/act/download" var="downloadUrl">
  <c:param name="id" value="${model.uuid}"/>
</c:url>

<html>
    <head>
        <link href="<c:url value='/main/assets/${appVersion}/bootstrap/3.0.0/css/bootstrap.min.css'/>" rel="stylesheet"/>
        <link href="<c:url value='/main/assets/${appVersion}/bootstrap/3.0.0/css/bootstrap-theme.min.css'/>" rel="stylesheet"/>
    </head>
    
<body>
    
    <c:if test="${param.viewMode eq 'full'}">
  
        <div id="controlPanel" style="display:none;">
            <button id="prev" class="btn" onclick="goPrevious()">&larr;<fmt:message key="button.prev"/></button>
            <button id="next" class="btn" onclick="goNext()"><fmt:message key="button.next"/> &rarr;</button>
            &nbsp; &nbsp;

            <span><fmt:message key="list.page"/>: <span id="page_num"></span> / <span id="page_count"></span></span>
        </div>

        
    </c:if>
    
    <div id="loadingPanel">
        <img src="<c:url value='/main/static/${appVersion}/images/icon-loader.gif'/>"/>
        <fmt:message key="action.loading"/>
    </div>
  <div>
    <canvas id="pdf-canvas" style="border:1px solid black;display:none;"></canvas>
  </div>

  <script src="<c:url value='/main/assets/${appVersion}/jquery/2.0.3/jquery.js'/>"></script>
      
     <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/external/webL10n/l10n.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/core.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/util.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/api.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/metadata.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/canvas.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/obj.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/function.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/charsets.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/cidmaps.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/colorspace.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/crypto.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/evaluator.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/fonts.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/glyphlist.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/image.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/metrics.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/parser.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/pattern.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/stream.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/worker.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/external/jpgjs/jpg.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/jpx.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/jbig2.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/bidi.js'/>"></script>
    <script type="text/javascript">PDFJS.workerSrc = '<c:url value='/main/assets/${appVersion}/pdf-js/0.5.5/src/worker_loader.js'/>';</script>

    <c:choose>
        <c:when test="${param.viewMode eq 'full'}">
            <c:set var="scaleRate" value="1.0"/>
        </c:when>
        <c:otherwise>
            <c:set var="scaleRate" value="0.3"/>
            
        </c:otherwise>
    </c:choose>
    
  
  <script type="text/javascript">
    //
    // NOTE: 
    // Modifying the URL below to another server will likely *NOT* work. Because of browser
    // security restrictions, we have to use a file server with special headers
    // (CORS) - most servers don't support cross-origin browser requests.
    //
    var url = '<c:out value="${downloadUrl}"/>';
    
            //'http://cdn.mozilla.net/pdfjs/tracemonkey.pdf';

    var viewMode = '<c:out value="${param.viewMode}"/>';        

    //
    // Disable workers to avoid yet another cross-origin issue (workers need the URL of
    // the script to be loaded, and currently do not allow cross-origin scripts)
    //
    //PDFJS.disableWorker = true;

    var pdfDoc = null,
        pageNum = 1,
        scale = ${scaleRate},
        canvas = document.getElementById('pdf-canvas'),
        ctx = canvas.getContext('2d');

    //
    // Get page info from document, resize canvas accordingly, and render page
    //
    function renderPage(num) {
      // Using promise to fetch the page
      pdfDoc.getPage(num).then(function(page) {
        var viewport = page.getViewport(scale);
        canvas.height = viewport.height;
        canvas.width = viewport.width;

      
        // Render PDF page into canvas context
        var renderContext = {
          canvasContext: ctx,
          viewport: viewport
        };
        page.render(renderContext).then(function() {
         
        $('#loadingPanel').css('display','none');
        
            $('#pdf-canvas').css('display','');
        
        if (viewMode == 'full'){
            $('#controlPanel').css('display','');
        }
      
        
        });
      
        if (viewMode == 'preview'){
        // alert('canvas h '+canvas.height);
         parent.autoResize(parent.document.getElementById('${model.id}_pdf_preview'),canvas);
        } 
        
         
        
      });

      if (document.getElementById('page_num')) {
      // Update page counters
      document.getElementById('page_num').textContent = pageNum;
      document.getElementById('page_count').textContent = pdfDoc.numPages;
      }
    }

    //
    // Go to previous page
    //
    function goPrevious() {
      if (pageNum <= 1)
        return;
      pageNum--;
      renderPage(pageNum);
    }

    //
    // Go to next page
    //
    function goNext() {
      if (pageNum >= pdfDoc.numPages)
        return;
      pageNum++;
      renderPage(pageNum);
    }

    $(document).ready(function() {
    
    //
    // Asynchronously download PDF as an ArrayBuffer
    //
    PDFJS.getDocument(url).then(function (pdf) {
      pdfDoc = pdf;
      renderPage(pageNum);
      
           
      });
    
     });
  </script>
  
    
  
</body>
</html>
