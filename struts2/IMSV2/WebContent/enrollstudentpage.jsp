<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Student Enrollment Form</title>
</head>
<body>
	<h2>Student Enrollment.</h2>
	<s:actionerror />
	<s:form action="enrollstudent.action" method="post">
		<s:textfield name="student.id" key="label.id" size="20" />
		<s:textfield name="student.studentName" key="label.studentName"
			size="20" />
		<s:textfield name="student.courseName" key="label.courseName"
			size="20" />
		<s:textfield name="student.fee" key="label.fee" size="20" />
		<s:textfield name="student.duration" key="label.duration" size="20" />
		<s:submit method="enrollStudent" key="label.enroll" align="center" />
	</s:form>
	<table cellspacing="20">
		<tr>
			<th><s:a href="loginpage" action="loginpage">Go to Login Page</s:a></th>
			<th><s:a href="userhomepage" action="userhomepage">Goto User Home</s:a></th>
		</tr>
	</table>
</body>
</html>