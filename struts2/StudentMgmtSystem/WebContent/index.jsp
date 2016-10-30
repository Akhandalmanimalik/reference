<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<h1>Student Registration Form</h1>
<html:form action="regForm" method="post">
	<table>
		<tr>
			<td>ID:</td>
			<td><html:text property="studentId" /></td>
			<td><html:errors property="id_e" /></td>
		</tr>
		<tr>
			<td>NAME:</td>
			<td><html:text property="studentName" /></td>
			<td><html:errors property="name_e" /></td>
		</tr>
		<tr>
			<td>EMAIL:</td>
			<td><html:text property="email" /></td>
			<td><html:errors property="email_e" /></td>
		</tr>
		<tr>
			<td>ADDRESS:</td>
			<td><html:textarea property="address" /></td>
			<td><html:errors property="address_e" /></td>
		</tr>
		<tr>
			<td>GENDER: MALE</td>
			<td><html:radio property="gender" value="Male"/></td>
			<td><html:errors property="gender_e" /></td>
		</tr>
		<tr>
			<td>GENDER: FEMALE</td>
			<td><html:radio property="gender" value="Female"/></td>
			<td></td>
		</tr>
		<tr>
			<td>HOBIES:</td>
			<td><html:checkbox property="hobies" value="hobie1" />Playing
				Cricket</td>
			<td><html:errors property="hobies_e" /></td>
		</tr>
		<tr>
			<td></td>
			<td><html:checkbox property="hobies" value="hobie2" />Reading Novels</td>
			<td></td>
		</tr>
		<tr>
			<td></td>
			<td><html:checkbox property="hobies" value="hobie3" />Chatting</td>
			<td></td>
		</tr>
		<tr>
			<td><html:submit value="Register" />
			<td>
		</tr>
	</table>
</html:form>