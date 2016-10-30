<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Add course Page</title>
</head>
<body>
	<h2>Add New Course</h2>
	<s:actionerror />
	<s:form action="addcourse.action" method="post">
		<s:textfield name="course.courseId" key="label.courseid" size="20" />
		<s:textfield name="course.courseName" key="label.coursename" size="20" />
		<s:textfield name="course.fee" key="label.fee" size="20" />
		<s:textfield name="course.duration" key="label.duration" size="20" />
		<s:submit method="addCourse" key="label.create" align="center" />
	</s:form>
	<table cellspacing="20">
	<tr><th><s:a href="loginpage" action="loginpage">Go to Login Page</s:a></th>
	<th><s:a href="userhomepage" action="userhomepage">Goto User Home</s:a></th>
	</tr>
	</table>
</body>
</html>