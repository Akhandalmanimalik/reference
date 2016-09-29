<!DOCTYPE html>
<html>
<body>

<h2>Create Object from JSON String</h2>

<p id="demo"></p>

<script>
var txt = '{"employees":[' +
'{"firstName":"Akhandalmani","lastName":"Malik" },' +
'{"firstName":"Nayaz","lastName":"pasha" },' +
'{"firstName":"Bibek","lastName":"Patnayak" }]}';

var obj = eval ("(" + txt + ")");

document.getElementById("demo").innerHTML =
obj.employees[1].firstName + " " + obj.employees[1].lastName;
</script>

</body>
</html>
