<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

  
   
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
  <script  type="text/javascript">
      function showModal(redirectUrl,action,title,message) {
        new SimpleModal({"btn_ok": action,"btn_cancel": "<fmt:message key='button.cancel'/>"}).show({
                    "model": "confirm",
                    "callback": function() {
                       window.location.href = redirectUrl;
                     },
                    "title": title,
                    "contents": message
                });
      }
  </script>
  
  