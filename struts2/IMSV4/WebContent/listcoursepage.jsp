
<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>All Courses</title>
</head>
<body>
	<table cellspacing="10" border="2"  bordercolor="red"><tr><th>Courses</th></tr>
	<tr><td><s:iterator value="listCourses"></td></tr>
			<tr><td><s:property /></td></tr>
	<tr><td></s:iterator></td></tr>
	</table>
	<table cellspacing="20">
		<tr>
			<th><s:a href="enrollstudentpage" action="enrollstudentpage">Add Student</s:a></th>
			<th><s:a href="userhomepage" action="userhomepage">Goto User Home</s:a></th>
			<th><s:a href="loginpage" action="loginpage">Go to Login Page</s:a></th>
			<th><s:a href="addcoursepage" action="addcoursepage">Add Course </s:a></th>
	</table>

</body>
</html>
