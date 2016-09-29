<%@ page import="java.io.*,java.sql.*"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%
	response.setContentType("text/xml");
	out.println("Should be come");
	String sn = request.getParameter("ok");
	//int i = Integer.parseInt(sn);
	out.println("khkjh");

	Class.forName("com.mysql.jdbc.Driver");
	Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bizruntime", "root",
			"malik@123");
	Statement st = con.createStatement();
	st.executeUpdate("insert into emp value(1221,'jay ram','2')");
	ResultSet rs = st.executeQuery("select * from emp where empno=" + 100);
	out.println("rs");
	if (rs.next()) {

		out.println("<emp>");
		out.println("<empno>" + rs.getInt(1) + "</empno>");
		out.println("<empname>" + rs.getString(2) + "</empname>");
		out.println("<empaddr>" + rs.getString(3) + "</empaddr>");
		out.println("</emp>");

	}

	rs.close();
	st.close();
	con.close();
%>