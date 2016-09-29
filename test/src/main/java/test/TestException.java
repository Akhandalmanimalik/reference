package test;

public class TestException {
public static void main(String[] args) {
	ThrowsException te=new ThrowsException();
	try{
	System.out.println("both should be execute:"+te.method1());
	System.out.println("both should be execute:"+te.method1());
	}catch(Exception npe){
		System.out.println(npe);
	}
}
}
