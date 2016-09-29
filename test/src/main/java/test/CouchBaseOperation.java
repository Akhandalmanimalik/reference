package couchbase;

import helper.GenericHelperClass;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.CouchbaseClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import dto.Opportunity;
//import com.syml.helper.GenericHelperClass;

public class CouchBaseOperation  {
	CouchbaseClient client1 = null;
 	static Logger log = LoggerFactory.getLogger(CouchBaseOperation.class);
 	
 	
 	public static void main(String[] args) {
//	System.out.println(	new CouchBaseOperation().getConnectionToCouchBase().get("Applicant_672"));
 	JSONObject jsonObject = new CouchBaseOperation().getCouchBaseData("Applicant_2877");
 	System.out.println(jsonObject);
//	System.out.println(new CouchBaseOperation().getCouchBaseData("Applicant_2790"));
	
	}
 	
 	//dev connection-----------------------
 	
 	public CouchbaseClient getConnectionToCouchBaseDev() {
 		System.out.println("Comming to getConnectionToCouchBaseDev*****************************");
		String url=null;
		String bucket=null;
		String pass=null;
		//int maximumRetry=0;
		
		Properties prop=new Properties();
		ArrayList<URI> nodes = new ArrayList<URI>();
		try{
		

			// getting connection parameter
			prop.load(CouchBaseOperation.class.getClassLoader().getResourceAsStream("config.properties"));	
			
		}catch(Exception e){
			log.error("Error in getting the property file",e);
		}
		
		try {
			log.info("inside getConnectionToCouchBase method of CouchBaseOperation class");
			url = prop.getProperty("couchBaseUrl");
		//	 url="http://198.72.106.5:8091/pools";
			 bucket ="syml";

					 //prop.getProperty("couchBaseUrl");
			  
					 //prop.getProperty("couchBaseBucketName");
			 pass ="symL@0115";
					 //prop.getProperty("couchBaseBucketPassword");
		//	 maximumRetry=new Integer(prop.getProperty("maximumRetry"));
		
			 // 1. Add one or more nodes of your cluster (exchange the IP with
			// yours)
			nodes.add(URI.create(url));
			log.debug("connecting .....");

			client1 = new CouchbaseClient(nodes, bucket, pass);
		} catch (IOException e) {
			// TODO Please confirm with Shan the config of Production Couchbase instance ... Should there be a failover address in catch block and error in a final block? 
			log.error("error while connecting to couchbase" , e);
			
		}
		return client1;
	}


 	
 	
 	//couchbase production connection ---------------------------------->
	public CouchbaseClient getConnectionToCouchBase() {
		String url=null;
		String bucket=null;
		String pass=null;
		int maximumRetry=0;
		
		Properties prop=new GenericHelperClass().readConfigfile();
		ArrayList<URI> nodes = new ArrayList<URI>();
		
		
		try {
			
			log.info("inside getConnectionToCouchBase method of CouchBaseOperation class");
		//	url = prop.getProperty("couchBaseUrl");
			 //url="http://198.72.106.10:8091/pools";
			 url=prop.getProperty("couchBaseUrl3");
			 bucket =prop.getProperty("couchBaseBucketName");
			 pass =prop.getProperty("couchBaseBucketPassword");
			 try{
			 maximumRetry=new Integer(prop.getProperty("maximumRetry"));
			 System.out.println(url  +" "+bucket+""+  pass +"" +prop.getProperty("couchBaseBucketPassword2"));

			 }catch(Exception e){
				 log.error("Error in connecting to couchbase ",e);
			 }
			 // 1. Add one or more nodes of your cluster (exchange the IP with
			// yours)
			nodes.add(URI.create(url));
			log.debug("connecting .....");
			
		client1 = getCouchbaseConnectionOne(nodes, bucket, pass, maximumRetry);
			if(client1==null){
				 url= prop.getProperty("couchBaseUrl1");
				 bucket = prop.getProperty("couchBaseBucketName2");
				 pass =prop.getProperty("couchBaseBucketPassword2");
				 ArrayList<URI> nodes1 = new ArrayList<URI>();
				nodes1.add(URI.create(url));
				 System.out.println(url  +" "+bucket+""+prop.getProperty("couchBaseBucketPassword2"));

				client1=getCouchbaseConnectionTwo(nodes1,bucket, pass, maximumRetry);
				 if(client1==null) {
					 
					 
					 url= prop.getProperty("couchBaseUrl2");
					 bucket = prop.getProperty("couchBaseBucketName2");
					 pass =prop.getProperty("couchBaseBucketPassword2");
					 nodes1 = new ArrayList<URI>();
					nodes1.add(URI.create(url));
					 client1=getCouchbaseConnectionTwo(nodes1,bucket, pass, maximumRetry);
						
					 
					 if(client1==null){
					 //Send mail error in connecting couchbase
						log.error("Error in Connecting Couhbase");
					}
				 }
			}
		} catch (Exception e) {
			log.error("error while connecting to couchbase " + e.getMessage());
			
			}
	
		
		return client1;
	}

	public CouchbaseClient getCouchbaseConnectionOne(ArrayList<URI> nodes,String bucketName,String password,int maximumRetry){
		
		
		log.debug("inside GetcoonectionOne  method of couchbase");
		int retry=1;
		CouchbaseClient client=null;
		while(retry<=maximumRetry && client==null){
			log.debug("inside whilw GetcoonectionOne  method of couchbase");

		try{
		client=new CouchbaseClient(nodes, bucketName, password);
		}catch(Exception e){
			client=null;
			log.error(" getCouchbaseConnectionOne  Retry..    "+retry);
			retry+=1;
			log.error("error in connecting Couchbase one"+e);
		}
		
		}
		return client;
	}
	
public CouchbaseClient getCouchbaseConnectionTwo(ArrayList<URI> nodes,String bucketName,String password,int maximumRetry){
	log.debug("inside GetCouchbaseConectionTwo  method of couchbase");
		int retry=1;
		CouchbaseClient client=null;
		while(retry<=maximumRetry && client==null){
		try{
			System.out.println(nodes +""+ bucketName +""+password );
			client=new CouchbaseClient(nodes, bucketName, password);
			
		}catch(Exception e){
			client=null;
			log.error(" getCouchbaseConnectionTwo Retry..   "+retry);
			retry+=1;
			log.error("error in connecting Couchbase two"+e);
		}
		
		}
		return client;
	}
	

public CouchbaseClient getCouchbaseConnectionThree(ArrayList<URI> nodes,String bucketName,String password,int maximumRetry){
	log.debug("inside GetCouchbaseConectionThree  method of couchbase");
		int retry=1;
		CouchbaseClient client=null;
		while(retry<=maximumRetry && client==null){
		try{
			System.out.println(nodes +""+ bucketName +""+password );
			client=new CouchbaseClient(nodes, bucketName, password);
			
		}catch(Exception e){
			client=null;
			log.error(" getCouchbaseConnectionThree Retry..   "+retry);
			retry+=1;
			log.error("error in connecting Couchbase three"+e);
		}
		
		}
		return client;
	}

	@SuppressWarnings("unchecked")
	public void storeDataInCouchBase(String key,String formType,@SuppressWarnings("rawtypes") HashMap map) {
		try {
			log.info("inside storeDataInCouchBase method of CouchBaseOperation class");
			client1 = getConnectionToCouchBase();
		
			// convert data into json
			JSONObject jsonData = new JSONObject();
			jsonData.put("FormType",formType);
			Set<Map.Entry<String, String>> set = map.entrySet();
			for (Map.Entry<String, String> entry : set) {
				jsonData.put(entry.getKey(), entry.getValue());
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");            //get current date time with Calendar()
            Calendar cal = Calendar.getInstance();
            String currentDateTime=(dateFormat.format(cal.getTime())); 
			jsonData.put("Submission_Date_Time1b",currentDateTime);

		

			log.debug("sending data...");
		
			
			client1.set(key, jsonData.toString());
			log.debug("sending data... done with id :"+key);
		closeCouchBaseConnection();

		} catch (Exception e) {
			log.error("error while storing data into couchbase : " + e);
		}

	}
	
	
	@SuppressWarnings("unchecked")
	public void storeDataInCouchBase(String key,String formType,Opportunity opportunity) {
		try {
			log.info("inside storeDataInCouchBase method of CouchBaseOperation class");
			client1 = getConnectionToCouchBase();
		
		
			ObjectMapper objectMapper=new ObjectMapper();
			
			
			
		String opportunityData=	objectMapper.writeValueAsString(opportunity);
			
		org.codehaus.jettison.json.JSONObject jsonObject=new org.codehaus.jettison.json.JSONObject(opportunityData);
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");            //get current date time with Calendar()
            Calendar cal = Calendar.getInstance();
            String currentDateTime=(dateFormat.format(cal.getTime())); 
            jsonObject.put(formType+"_submissionTime",currentDateTime);

		

			log.debug("sending data...");
		
			
			client1.set(key, jsonObject.toString());
			log.debug("sending data... done with id :"+key);
		closeCouchBaseConnection();

		} catch (Exception e) {
			log.error("error while storing data into couchbase : " + e);
		}

	}
	
	public void storeJsonDataInCouchBase(String key,JSONObject jsonData) {
		try {
			log.info("inside storeDataInCouchBase method of CouchBaseOperation class");
			client1 = getConnectionToCouchBase();
		
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");            //get current date time with Calendar()
            Calendar cal = Calendar.getInstance();
            String currentDateTime=(dateFormat.format(cal.getTime())); 
            jsonData.put("Submission_Date_Time1b",currentDateTime);

		

			log.debug("sending data...");
		
			
			client1.set(key, jsonData.toString());
			log.debug("sending data... done with id :"+key);
		closeCouchBaseConnection();

		} catch (Exception e) {
			log.error("error while storing data into couchbase : " + e);
		}

	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void appendDataInCouchBase(String key,HashMap appendData){
		try {
			
			log.info("inside appendDataInCouchBase method of CouchBaseOperation class");
			client1 = getConnectionToCouchBase();
			
			String object=(String)client1.get(key);
			// convert data into json
			log.debug("old json  data...  "+object);
			JSONObject jsonData =  new JSONObject(object);
			Set<Map.Entry<String, String>> set = appendData.entrySet();
			for (Map.Entry<String, String> entry : set) {
				jsonData.put(entry.getKey(), entry.getValue());
			}
			log.debug("new json  data...  "+jsonData.toString());
			client1.replace(key, jsonData.toString());
		//	client1.replace(key, o1.toString());
			log.debug("replacing data... done");

		closeCouchBaseConnection();

		} catch (Exception e) {
			log.error("error while apending data into couchbase inside CouchbaseOperation class : " + e);
		}

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void editDataInCouchBase(String key,String formType,HashMap editData){
		try {
			log.info("inside editDataInCouchBase method of CouchBaseOperation class");

			client1 = getConnectionToCouchBase();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");            //get current date time with Calendar()
            Calendar cal = Calendar.getInstance();
            String currentDateTime=(dateFormat.format(cal.getTime())); 

			// convert data into json
			JSONObject jsonData = new JSONObject();
			jsonData.put("FormType",formType);
			Set<Map.Entry<String, String>> set = editData.entrySet();
			for (Map.Entry<String, String> entry : set) {
				jsonData.put(entry.getKey(), entry.getValue());
			}

			
			jsonData.put("Submission_Date_Time1b",currentDateTime);

			log.debug("editing data...");
			client1.replace(key, jsonData.toString());

		closeCouchBaseConnection();

		} catch (Exception e) {
			log.error("error while editing data into couchbase : " + e);
		}
	}

	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void storeMortgageFormData3InCouchBase(HashMap map) {
		try {

			client1 = getConnectionToCouchBase();
			String currentlyownanyrealestate = (String) map.get("doyoucurrentlyownanyrealestate");

			String mortgageform3UniqueId = (String) map.get("mortgageform3UniqueId");
			

			// convert data into json
			JSONObject jsonData = new JSONObject();

			Set<Map.Entry<String, String>> set = map.entrySet();
			for (Map.Entry<String, String> entry : set) {
				jsonData.put(entry.getKey(), entry.getValue());
			}

			// create a unqiue id
			String uid = mortgageform3UniqueId + "_" + currentlyownanyrealestate;

			log.info("sending data...");
			client1.set(uid, jsonData.toString());

			log.info("closing connection");
		closeCouchBaseConnection();

		} catch (Exception e) {
			log.error("error while storing data into couchbase : " + e);
		}

	}
	
	public JSONObject getData(String key){
		String object="";
		 JSONObject jsonData =null;
		try{
client1 = getConnectionToCouchBase();
		
			
			
		 object=(String)client1.get(key);
	jsonData =  new JSONObject(object);
	closeCouchBaseConnection();

		}catch(Exception e){
			
		}
		return jsonData;
	}
	
	
	
	public void closeCouchBaseConnection() {
		log.debug("closing connection");
		client1.shutdown(900l, TimeUnit.MILLISECONDS);
	

	}
	
	public void deleteCouchBaseData(String key){
		log.debug("Deleting couchbase data based on key");
		client1=getConnectionToCouchBase();
		client1.delete(key);
		closeCouchBaseConnection();
	}
	
	
	
	public JSONObject getCouchBaseData(String key){
		
		log.debug("Inside Get couchbase data based on key");
		System.out.println("key>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<"+key);
		client1=getConnectionToCouchBase();
		
		String object=(String)client1.get(key);
		
		JSONObject jsonObject=null;
		try {
			jsonObject = new JSONObject(object);
			
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error("error in getting data from couchbase "+e);
		}
		closeCouchBaseConnection();
		return jsonObject;
	}
	
	public ArrayList<String> getReferralData(
			String referralID) {
		ArrayList<String> list=new ArrayList<String>();
		log.info("inside findReferralSourceCode method of CheckReferalSource class");

	String email="";
	String firstName="";
	String LastName="";
		String phoneNumber = "";
		try {

JSONObject json=getCouchBaseData("ReferralTrigerData_"+referralID);
log.info("referal ID >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+"ReferralTrigerData_"+referralID);
			
		phoneNumber=(String) json.get("partner_mobile");
		email=(String) json.get("email_from");
		String name=(String) json.get("name");
		try{
			String arString[]=name.split("_");
			firstName=arString[0];
			LastName=arString[1];
		}catch(Exception e){
			
		}
		list.add(phoneNumber);
		list.add(email);
		list.add(firstName);
		list.add(LastName);
		} catch (Exception e) {
			log.error("Error while reading data from server:\n\n"
					+ e.getMessage());
		}

	
		return list;
	}
	

}
