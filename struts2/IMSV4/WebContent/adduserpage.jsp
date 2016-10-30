<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Struts 2 - Login Application</title>
</head>
<body>
	<h2>User Registration</h2>
	<s:actionerror />
	<s:form action="addemployee.action" method="post">
		<s:textfield name="employee.firstName" key="label.firstName" size="20" />
		<s:textfield name="employee.lastName" key="label.lastName" size="20" />
		<s:textfield name="employee.uname" key="label.uname" size="20" />
		<s:password name="employee.pass" key="label.pass" size="20" />
		<s:textfield name="employee.state" key="label.state" size="20" />
		<s:textfield name="employee.city" key="label.city" size="20" />

		<s:submit method="addEmployee" key="label.register" align="center" />
	</s:form>
	<s:a href="loginpage" action="loginpage">Go to Login Page</s:a>
</body>
</html>