<!Doctype html>

<%@page import="pt.ist.bennu.renderers.actions.DefaultContext"%>
<%
	DefaultContext context = (DefaultContext) request.getAttribute("_CONTEXT_");
%>

<html>
	<head>
		<script src="/bennu-portal/portal.js" type="text/javascript"></script>
	</head>
	<body style="display:none;">
		<jsp:include page="<%=context.getBody()%>" />
	</body>
</html>