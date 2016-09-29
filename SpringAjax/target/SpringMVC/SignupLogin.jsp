<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
 	<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<script type="text/javascript">

function loadXMLDoc(){
	var xmlhttp;
	//xmlhttp Object
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
		xmlhttp.onreadystatechange=function() {
			
	  		if (xmlhttp.readyState==4 && xmlhttp.status==200){
	    		document.getElementByName("sele").innerHTML=xmlhttp.responseText;
	    	}
	  	}
	}
xmlhttp.open("GET","state.spring/cities?ka",true);
xmlhttp.send();
</script>

</head>
<body>

<form name="dummy">

<select name="state" onchange="loadXMLDoc()">
<option>Select State</option>
<option value="ka">ka</option>
<option value="od">101</option>
</select>

<select name="city" onchange="loadXMLDoc()">
<option>Select State</option>
<option value="ka">ka</option>
<option value="od">101</option>
</select>
</form>
</body>
</html>