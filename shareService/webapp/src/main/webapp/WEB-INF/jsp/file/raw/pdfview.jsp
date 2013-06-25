<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>

<c:url value="/act/download" var="downloadUrl">
  <c:param name="id" value="${model.uuid}"/>
</c:url>

<html>
    <head>
          <link href="<c:url value='/css/metro-bootstrap.css'/>" rel="stylesheet"/>
    
    </head>
    
<body>
  <div>
    <button id="prev" class="btn" onclick="goPrevious()">&larr;<fmt:message key="button.prev"/></button>
    <button id="next" class="btn" onclick="goNext()"><fmt:message key="button.next"/> &rarr;</button>
    &nbsp; &nbsp;
    
    <span><fmt:message key="list.page"/>: <span id="page_num"></span> / <span id="page_count"></span></span>
  </div>
  
  <div>
    <canvas id="the-canvas" style="border:1px solid black"></canvas>
  </div>

  <!-- Use latest PDF.js build from Github -->
  <script type="text/javascript" src="<c:url value='/js/pdf.js'/>"></script>
  
  <script type="text/javascript">
    //
    // NOTE: 
    // Modifying the URL below to another server will likely *NOT* work. Because of browser
    // security restrictions, we have to use a file server with special headers
    // (CORS) - most servers don't support cross-origin browser requests.
    //
    var url = '<c:out value="${downloadUrl}"/>';
    
            //'http://cdn.mozilla.net/pdfjs/tracemonkey.pdf';

    //
    // Disable workers to avoid yet another cross-origin issue (workers need the URL of
    // the script to be loaded, and currently do not allow cross-origin scripts)
    //
    PDFJS.disableWorker = true;

    var pdfDoc = null,
        pageNum = 1,
        scale = 1.0,
        canvas = document.getElementById('the-canvas'),
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
        page.render(renderContext);
      });

      // Update page counters
      document.getElementById('page_num').textContent = pageNum;
      document.getElementById('page_count').textContent = pdfDoc.numPages;
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

    //
    // Asynchronously download PDF as an ArrayBuffer
    //
    PDFJS.getDocument(url).then(function getPdfHelloWorld(_pdfDoc) {
      pdfDoc = _pdfDoc;
      renderPage(pageNum);
    });
  </script>  
</body>
</html>
