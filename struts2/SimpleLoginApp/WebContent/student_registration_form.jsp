<%@ taglib uri="/struts-tags"  prefix="s" %>
<s:form action="registration" method="post">
Student Name:	<s:text property="studentName" name="studentName"/>
Email:	<s:text property="studentName" name="studentName"/>
Address:	<s:textarea property ="address"/>
<%-- Gender:	<s:radio property="gender" value="male">Male
		<s:radio property="gender" value="female"/>Female
Hobies:	<s:checkbox --%>
</s:form>
