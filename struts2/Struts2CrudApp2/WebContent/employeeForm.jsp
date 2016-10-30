<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
    <link href="<s:url value="/resources/main.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>
<center>
<div class="titleDiv"><s:text name="application.title"/></div>
<h1><s:if test="employee==null || employee.employeeId == null">
	 <s:text name="label.employee.add"/>
   </s:if>
   <s:else>
	 <s:text name="label.employee.edit"/>
   </s:else></h1>
   
	<table width=600 align=center>
	    <tr><td><a href="getAllEmployees.action">Click Here to View Employees</a></td>
	     </tr>
	</table>	 
	 <table>
		<tr><td align="left" style="font:bold;color:red">
	          <s:fielderror/>
                  <s:actionerror/>
                  <s:actionmessage/></td></tr>
        </table>
     		 	
    <s:form>
     <table align="center" class="borderAll">
     	 
         <tr><td class="tdLabel"><s:text name="label.firstName"/></td>
         	        <td><s:textfield name="employee.firstName" size="30"/></td>
         </tr>
        <tr>
            <td class="tdLabel"><s:text name="label.lastName"/></td>
                            <td><s:textfield name="employee.lastName" size="30"/></td>
        </tr>
        <tr><td class="tdLabel"><s:text name="label.age"/></td>
        	                <td><s:textfield name="employee.age" size="20"/></td>
        </tr>
         <tr>
            <td class="tdLabel"><s:text name="label.department"/></td>
            <td><s:select name="employee.department.departmentId" 
                list="#session.departments" 
                listKey="departmentId" 
                listValue="name"
                />
            </td>
             <s:hidden name="employee.employeeId"/>
        </tr>
    </table>

    <table> 
    	<tr>
    	  <td><s:submit action="insertOrUpdate" key="button.label.submit" cssClass="butStnd"/></td>
    	  <td><s:reset key="button.label.cancel" cssClass="butStnd"/></td>
    	<tr>
    </table> 		  		 
   </s:form>
</center>		
</body>
</html>