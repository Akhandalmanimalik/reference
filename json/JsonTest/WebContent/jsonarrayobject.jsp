<!DOCTYPE html>
<html>
<body>

<h2>Create Object from JSON String</h2>

<p id="demo"></p>

<script>
var mytext = '{"employees":[' +
'{"firstName":"Akhandalmani","lastName":"Malik" },' +
'{"firstName":"Laxmi","lastName":"Narayan" },' +
'{"firstName":"Rudra","lastName":"Narayan" }]}';

obj = JSON.parse(mytext);

document.getElementById("demo").innerHTML =
	
obj.employees[0].firstName + " " + obj.employees[0].lastName;

</script>

</body>
</html>