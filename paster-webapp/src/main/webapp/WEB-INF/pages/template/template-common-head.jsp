<%--

    Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<%--

    Headers for all pages

--%>
<!-- favicons -->
<link rel="apple-touch-icon" sizes="57x57" href="${ctx}/main/resources/${appId}/static/favicon/apple-touch-icon-57x57.png">
<link rel="apple-touch-icon" sizes="60x60" href="${ctx}/main/resources/${appId}/static/favicon/apple-touch-icon-60x60.png">
<link rel="apple-touch-icon" sizes="72x72" href="${ctx}/main/resources/${appId}/static/favicon/apple-touch-icon-72x72.png">
<link rel="apple-touch-icon" sizes="76x76" href="${ctx}/main/resources/${appId}/static/favicon/apple-touch-icon-76x76.png">
<link rel="apple-touch-icon" sizes="114x114" href="${ctx}/main/resources/${appId}/static/favicon/apple-touch-icon-114x114.png">
<link rel="apple-touch-icon" sizes="120x120" href="${ctx}/main/resources/${appId}/static/favicon/apple-touch-icon-120x120.png">
<link rel="apple-touch-icon" sizes="144x144" href="${ctx}/main/resources/${appId}/static/favicon/apple-touch-icon-144x144.png">
<link rel="apple-touch-icon" sizes="152x152" href="${ctx}/main/resources/${appId}/static/favicon/apple-touch-icon-152x152.png">
<link rel="apple-touch-icon" sizes="180x180" href="${ctx}/main/resources/${appId}/static/favicon/apple-touch-icon-180x180.png">
<link rel="icon" type="image/png" href="${ctx}/main/resources/${appId}/static/favicon/favicon-32x32.png" sizes="32x32">
<link rel="icon" type="image/png" href="${ctx}/main/resources/${appId}/static/favicon/android-chrome-192x192.png" sizes="192x192">
<link rel="icon" type="image/png" href="${ctx}/main/resources/${appId}/static/favicon/favicon-96x96.png" sizes="96x96">
<link rel="icon" type="image/png" href="${ctx}/main/resources/${appId}/static/favicon/favicon-16x16.png" sizes="16x16">
<link rel="manifest" href="${ctx}/main/resources/${appId}/static/favicon/manifest.json">
<link rel="mask-icon" href="${ctx}/main/resources/${appId}/static/favicon/safari-pinned-tab.svg" color="#5bbad5">
<!--  metadata tags -->
<meta name="msapplication-TileColor" content="#00aba9">
<meta name="msapplication-TileImage" content="${ctx}/main/resources/${appId}/static/favicon/mstile-144x144.png">
<meta name="theme-color" content="#ffffff">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!-- HTTP 1.1 -->
<meta http-equiv="Cache-Control" content="no-store" />
<!-- HTTP 1.0 -->
<meta http-equiv="Pragma" content="no-cache" />
<!-- Prevents caching at the Proxy Server -->
<meta http-equiv="Expires" content="0" />
<!-- Bootstrap css -->
<link href="<c:url value='/main/resources/${appId}/paster/css/all/bootstrap.min.css'/>" rel="stylesheet"
    type="text/css" />
<!-- additional icons -->
<link href="<c:url value='/main/resources/${appId}/paster/css/all/mnmlicons.css'/>" rel="stylesheet" type="text/css" />
<!-- Font Awersome icons -->
<link type="text/css" href="<c:url value='/main/resources/${appId}/local_components/font-awesome/css/font-awesome.min.css'/>"
    rel="stylesheet" />
<!-- main Paster styles -->
<link href="<c:url value='/main/resources/${appId}/paster/css/all/app.css'/>" rel="stylesheet" type="text/css" />
