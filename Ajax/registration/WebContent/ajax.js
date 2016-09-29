function doAjaxCall(){
	
	if(XMLHttpRequest){
		var obj=new XMLHttpRequest();
		
		var x=document.getElementById("aa").value;
		alert("x"+x);
		var y=document.getElementById("ss").value;
		alert("y"+y);
		obj.onreadystatechange=function(){
			
	
			if(obj.readyState==4 && obj.status==200){
				x.innerHTML=obj.responseText;
			}
				
				
			}
	
	}
	
	

	obj.open("GET","http://localhost:8080/registration/form?email="+x+"&name="+y,true);
	alert("coming"+x);
    obj.send(null);
	

	
}