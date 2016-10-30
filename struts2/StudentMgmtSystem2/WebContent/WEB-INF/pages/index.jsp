<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head></head>
<body>
	<h1>Struts 2 Hello World Example</h1>

	<s:form action="regForm">
		<s:textfield name="userName" label="Student Name" />
		<s:password name="password" label="Password" />
		<s:submit value="login" />
	</s:form>
	 <%-- <s:a href="addStudent.jsp">Click Here For New Register</s:a> --%>

</body>
</html>