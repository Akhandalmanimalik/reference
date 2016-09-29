function doAjaxCall(){
	
	if(XMLHttpRequest){
		var obj=new XMLHttpRequest();
		var params = "prakash";
		var x=document.getElementById("aa").value;
		
		obj.onreadystatechange=function(){
			
		
		var x=document.getElementById("aa").value;
		
		alert(x);
			if(obj.readyState==4 && obj.status==200){
	
				
				x.innerHTML=obj.responseText;
			}
				
				
			}
	
	}

	

	obj.open('GET','http://localhost:8080/form',true);
	alert("coming");
    obj.send(null);
	

	
}