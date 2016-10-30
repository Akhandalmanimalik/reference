<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Struts 2 - Login Application </title>
</head>
 
<body>
<h2>Login Application</h2>
<s:actionerror />
<s:form action="login.action" method="post">
    <s:textfield name="user.uname" key="label.username" size="20" />
    <s:password name="user.pass" key="label.password" size="20" />
    <s:submit method="execute" key="label.login" align="center" />
</s:form>
<a href="add_student.jsp">Click Here For New Register</a>
</body>
</html>