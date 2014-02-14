<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

  
    <script type="text/javascript">
        var growl= null;
        window.addEvent('domready',function() {
            growl = new Growler.init();

        });
    </script>

    <%--
    <!--
  Under development, this section allows you to preview your changes
  immediately after saving them on the {app.less} file. However, you
  must remove it once it enters the production stage or it will open
  a bunch of requests to your server to retrieve new changes.
-->
    <script charset="utf-8">
        //less.env = "development";
        //less.watch();
    </script>
--%>
  