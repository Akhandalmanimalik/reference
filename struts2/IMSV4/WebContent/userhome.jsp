<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<title>Welcome</title>
</head>

<body>
	<h2>Hello,<font color="red"><s:property value="user.uname" /> </font> Welcome to User Home Page!</h2>
	<s:form>
		<table cellspacing="20">
			<tr>
			
				<th><s:a href="addcoursepage" action="addcoursepage">Add Course</s:a></th>
				<th><s:a href="listcoursepage" action="listcoursepage">List All Course</s:a></th>
				<th><s:a href="enrollstudentpage" action="enrollstudentpage">Enroll Student</s:a></th>
				<th><s:a href="loginpage" action="loginpage">Goto Login Page</s:a></th>
			</tr>
		</table>
	</s:form>
</body>
</html>