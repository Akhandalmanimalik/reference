<!DOCTYPE html>
<html>
<body>

<h2>How to Add new Item in json array runtime.</h2>

<p id="demo"></p>

<script>
//Adding type 1:
var items = {"theTeam":[
                        {"teamId":"1","status":"pending"},
                        {"teamId":"2","status":"member"},
                        {"teamId":"3","status":"member"}
                        ]
                        };
items.theTeam.push({"teamId":"4","status":"pending"});
document.write("After added teamId 4 :<br/><br/>");
document.write(items.theTeam[3].teamId+"   "+items.theTeam[3].status+"<br/><br/>");

</script>
</body>

</html>
