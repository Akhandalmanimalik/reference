<!DOCTYPE html>
<html>
<body>

<h2>How to delete an array in runtime.</h2>

<p id="demo"></p>

<script>
var countries = {};
countries.results = [
    {id:'AF',name:'Afghanistan'},
    {id:'AL',name:'Albania'},
    {id:'Aj',name:'Argentina'},
    {id:'IA',name:'India'},
    {id:'PA',name:'Pakistan'},
    {id:'SA',name:'SouthAfrica'}
];
//document.write(countries.results[0].id+"   "+countries.results[0].name+"<br/><br/>");
//document.write(countries.results[1].id+"   "+countries.results[1].name+"<br/><br/>");
//document.write(countries.results[2].id+"   "+countries.results[2].name+"<br/><br/>");

//solution:
//function to remove a value from the json array
function removeItem(obj, prop, val) {
var c, found=false;
for(c in obj) {
    if(obj[c][prop] == val) {
        found=true;
        break;
    }
}
if(found){
    delete obj[c];
}
}
//example: call the 'remove' function to remove an item by id.
removeItem(countries.results,'id','AF');

//example2: call the 'remove' function to remove an item by name.
removeItem(countries.results,'name','Albania');

//print our result to console to check it works !
for(c in countries.results) {
	document.write(countries.results[c].id+"   "+countries.results[c].name+"<br/><br/>");
}
</script>
</body>

</html>
