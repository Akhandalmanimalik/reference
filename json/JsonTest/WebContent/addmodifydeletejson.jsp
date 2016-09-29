<!DOCTYPE html>
<html>
<body>

<h2>JSON Object Creation in JavaScript</h2>

<p id="demo"></p>

<script>
var mytext='{"firstname":"Akhandalmani","lastname":"Malik","nickname":null,"address":"Berhampur","MobleNo":9738130495,"Married":false}';
var myobject=JSON.parse(mytext);

document.getElementById("demo").innerHTML =
myobject.firstname+"   "+
myobject.lastname+" " +
"("+myobject.nickname+")<br>" +
myobject.address+"<br>" +
myobject.MobleNo+"<br>" +
myobject.Married;
window.alert("after delete.");

delete myobject.nickname;
document.write(" After Delete Nick Name : <br/>");

document.write("nickname:  "+myobject.nickname+"<br/><br/>");

document.write("firstname:  "+myobject.firstname);
</script>
</body>

</html>
