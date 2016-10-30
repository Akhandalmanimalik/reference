<%@ taglib uri="/struts-tags" prefix="s"%>
<h1>Student Registration Form</h1>
<s:form action="regForm" theme="xhtml">
	<s:textfield name="studentId" label="Student Id" />
	<s:textfield name="studentName" label="Student Name" />
	<s:textfield name="email" label="Email" />
	<s:textarea name="address" label="Address" />
	<s:submit value="Register" />
	<%-- <s:radio name="gender" label="Male" list="mylist" />
	<s:radio name="gender" label="Female" list="mylist" />
	<s:checkbox name="hobies" label="Reading Books" />
	<s:checkbox name="hobies" label="Searching Technology" />
	<s:checkbox name="hobies" label="Playing Cricket" /> --%>
</s:form>