<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Welcome</title>
</head>

<body>
	<h2>Registration Page</h2>
	<s:actionerror />
	<s:form action="addemployee.action" method="post">
		<s:textfield name="firstName" key="label.firstName" size="20" />
		<s:textfield name="lastName" key="label.lastName" size="20" />
		<s:textfield name="userName" key="label.userName" size="20" />
		<%-- <s:textfield name="email" key="label.email" size="20" /> --%>
		<%-- <s:textfield name="address" key="label.address" size="20" /> --%>
		<s:password name="password" key="label.password" size="20" />
		<s:submit method="execute" key="label.register" align="center" />
	</s:form>
</body>
</html>