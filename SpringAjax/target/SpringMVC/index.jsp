<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>WelComePage</title>
<META NAME="Generator" CONTENT="EditPlus">
<META NAME="Author" CONTENT="">
<META NAME="Keywords" CONTENT="">
<META NAME="Description" CONTENT="">
	 <script type="text/javascript">

function CheckFormat () {
            var uname = document.getElementById ("uname");
            if (uname.value.length < 3) {
                alert ("The name of the user must be at least 3 characters long!");
                return false;
            }

            var pass = document.getElementById ("pass");
            var repassword = document.getElementById ("repassword");
            if (pass.value.length < 3) {
                alert ("The password must be at least 3 characters long!");
                return false;
            }
            /* if (repassword.value != pass.value) {
                alert ("The password & Repassword mismatch!");
                return false;
           }
                        */         
         return true;
      	}
</script>
</head>
<body>


<form id="regForm" method="POST" action="root/login" onsubmit="return CheckFormat ();" name="form"><pre>
        User Name	: <input type="text" name="uname" id="uname" />
        <br />
        Password	: <input type="password" name="pass" id="pass" />
        <br />
        			<input type="submit" value="login" />
    </pre></form>
    
    <a href="AddEmployee.jsp">Click Here For New Register</a>
    
</body>
</html>