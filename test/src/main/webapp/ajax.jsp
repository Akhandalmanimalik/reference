<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Employee Registration Form</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<p></p>
<!-- <script> -->

<script type="text/javascript">
	function getCities(value){
		alert("in getcities");
		$.ajax({
			url: "root/getCities"+value,
			type: "GET",
			async: true, 
			data:{country_id:val},
			success:function(data){
				 alert('SUCCESS');
				 $("#test").html(data); 
				},
			error:function(data){alert(JSON.stringify(data));}
			});
	}	 
</script>
</head>
<body>
 <form method="post" action="root/addEmployee" ><pre>
	FirstName:	<input type="text" name="firstName"/><br/>
	LastName:	<input type="text" name="lastName"/><br/>
	UserName:	<input type="text" name="userName"/><br/>
	Password:	<input type="password" name="password"/><br/>
	<!-- State:		<input type="text" name="state"/><br/> -->
	
	State:		<select id="state" name="state" onChange="getCities(this.value)">
					<option value="">Select Your State:</option>
					<option value="Odisha">Odisha</option>
					<option value="Karnataka">Karnataka</option>
					<option value="AndhraPradesh">AndhraPradesh</option>
	</select>
	<!-- City:		<input type="text" name="City"/><br/> -->
	City:		<select id="city" name="City">
					<option value="">Choose Your City:</option>
					<option></option>
				</select>
				
	<input type="submit" name="Register"/>
	</pre>
</form>
<p id="test"> </p>
</body>
</html>