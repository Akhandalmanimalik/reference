package test;

public class Search {

	public static Result mortgagePage11Properties() {
		Logger.info("Inside mortgagePage11Properties");
		
		HashMap dataStoreValue=new HashMap();
		CouchBaseOperation storeData=null;
		DynamicForm dynamicForm = form().bindFromRequest();
		try{
			String formType ="Mortgage Application";
			String subForm ="Mortgage Application 11";
			Session session = Http.Context.current().session();
			String applicantID=null;
			try{
//				applicantID=(String)session.get("applicantID");
				applicantID="2753";
				Logger.debug("applicantID "+applicantID);
			}catch(Exception e){
				Logger.error("Error when reading applicantID from session "+e);
			}
			String ip=request().remoteAddress();
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			//get current date time with Calendar()
			Calendar cal = Calendar.getInstance();
			String currentDateTime=(dateFormat.format(cal.getTime()));
			
			CreateApplicant createLead = new CreateApplicant();
			
			String	howManyProperties = dynamicForm.get("howManyProperties");
			/*if(howManyProperties.equalsIgnoreCase("none") && howManyProperties != null){}
			else*/ 
			Logger.debug("howManyProperties---> "+ howManyProperties);
			dataStoreValue.put("Applicant-howManyProperties",howManyProperties);
			
			if(howManyProperties.equalsIgnoreCase("one") && howManyProperties != null){
				
				Logger.info(" Inside if's one ");
				String address1=dynamicForm.get("address0");
				String appxValue1=dynamicForm.get("appxValue1");
				String mortgageValue1=dynamicForm.get("mortgage1");
				String rentMo1=dynamicForm.get("rentMo1");
				String condoFees1=dynamicForm.get("condoFees1");
				String ownership1=dynamicForm.get("ownership1");
				String selling1=dynamicForm.get("agree1");
				
				Logger.debug("address1= "+ address1);
				Logger.debug("appxValue1= "+ appxValue1);
				Logger.debug("mortgageValue1= "+ mortgageValue1);
				Logger.debug("rentMo1= "+ rentMo1);
				Logger.debug("condoFees1= "+ condoFees1);
				Logger.debug("ownership1= "+ ownership1);
				Logger.debug("selling1= "+ selling1);
				int monthlyRent=0;
				if(rentMo1 != null){
				try{
					monthlyRent = Integer.parseInt(rentMo1);
				}catch(Exception e){
					Logger.debug("Error in parsing rentMo1");
				}
				}
				
				dataStoreValue.put("Applicant-address1", address1);
				dataStoreValue.put("Applicant-appx_value1", appxValue1);
				dataStoreValue.put("Applicant-mortgage_value1", mortgageValue1);
				dataStoreValue.put("Applicant-rent_mo1", rentMo1);
				dataStoreValue.put("Applicant-condoFees1", condoFees1);
				dataStoreValue.put("Applicant-ownership1", ownership1);
				dataStoreValue.put("Applicant-sellingYesNo1", selling1);
				
				Logger.debug(!mortgageValue1.equalsIgnoreCase("")+"!mortgageValue1.equalsIgnoreCase() ");
				Logger.debug(mortgageValue1.equalsIgnoreCase("")+"!mortgageValue1.equalsIgnoreCase() ");
				
				if (mortgageValue1 != null && !mortgageValue1.equalsIgnoreCase("")
						&& selling1 != null && selling1.equalsIgnoreCase("on")) {
					Logger.debug("both mortgagae and selling values are comming.");
					
					if(rentMo1 != null && !rentMo1.equalsIgnoreCase(""))
					 createLead.createApplicantMortgage(applicantID, address1,1, selling1);
					else
						createLead.createApplicantMortgage(applicantID, address1,1, selling1);
					
					 Logger.debug("Record is updated in applicant mortgage of openERP..");
					if (condoFees1 != null && !condoFees1.equalsIgnoreCase("")) {
						Logger.debug("condo values comming");
						createLead.createApplicantProperties(applicantID, address1, condoFees1, 1, selling1);
						Logger.debug("Record is updated in applicant properties of openERP with condo value");
					} else {
						Logger.debug("condo values not coming");
						createLead.createApplicantProperties(applicantID, address1, "-1", 1, selling1);
						Logger.debug("Record is updated in applicant properties of openERP with condo value");
					}
					
				} else if (mortgageValue1 != null && mortgageValue1.equalsIgnoreCase("")
						&& selling1 != null && selling1.equalsIgnoreCase("")) {
					Logger.debug("when mortgage value is comming but selling value is not comming.");
					
					if(rentMo1 != null && !rentMo1.equalsIgnoreCase(""))
						 createLead.createApplicantMortgage(applicantID, address1,1, selling1);
						else
							createLead.createApplicantMortgage(applicantID, address1,1, selling1);
						
						Logger.debug("Record is updated in applicant mortgage of openERP with selling false and with rentMo1");
						if (condoFees1 != null	&& !condoFees1.equalsIgnoreCase("")) {
							Logger.debug("condo values comming");
							createLead.createApplicantProperties(applicantID, address1, condoFees1, 1, selling1);
							Logger.debug("Record is updated in applicant properties of openERP with condo fees");
						} else {
							Logger.debug("condo values not comming");
							createLead.createApplicantProperties(applicantID, address1, "-1", 1, selling1);
							Logger.debug("Record is updated in applicant properties of openERP without condo value");
						}
				}else if(mortgageValue1 != null && mortgageValue1.equalsIgnoreCase("") && selling1 != null && selling1.equalsIgnoreCase("on")){

					Logger.debug("when mortgage is not comming but selling value is comming");

					//createLead.createApplicantMortgage(applicantID, address1,1, selling1);
					if(condoFees1 != null && !condoFees1.equalsIgnoreCase("")){
						Logger.debug("condo values set to yes");
						createLead.createApplicantProperties(applicantID, address1, condoFees1,1,selling1);
						Logger.debug("Record updated in applicant properties of  openERP with condoFees");
						}else{
							Logger.debug("condo values set to no");
						createLead.createApplicantProperties(applicantID, address1, "-1",1,selling1);
						Logger.debug("Record updated in applicant properties of  openERP but condoFees is empty ");
					}
				
				} else {
					Logger.debug("both mortgage and selling value not comming ");
					
						if (condoFees1 != null	&& !condoFees1.equalsIgnoreCase("")) {
							Logger.debug("condo values set to yes");
							createLead.createApplicantProperties(applicantID,	address1, condoFees1, 1, selling1);
							Logger.debug("Updated record in openERP with condo values");
						} else {
							Logger.debug("condo values set to no");
							createLead.createApplicantProperties(applicantID, address1, "-1", 1, selling1);
							Logger.debug("Updated record in openERP with out condo values");
						}
					 // end of if else rentalyesno1
				}
				
			}else if(howManyProperties.equalsIgnoreCase("two") && howManyProperties != null){
				
			}else if(howManyProperties.equalsIgnoreCase("three") && howManyProperties != null){
				
			}else if(howManyProperties.equalsIgnoreCase("four") && howManyProperties != null){
				
			}else if(howManyProperties.equalsIgnoreCase("more") && howManyProperties != null){
				
			}
			CouchBaseOperation appendData=new CouchBaseOperation();
			
			dataStoreValue.put("Applicant-subForm-11", subForm);
			
			appendData.appendDataInCouchBase("Applicant_"+applicantID, dataStoreValue);
		
			return ok(mortgagePage11Properties.render(""));
		}catch(Exception e){
			Logger.error("Error in mortgagePage11Properties"+e);
			return ok("Something went wrong in mortgagePage11Properties..."+e);
		}
		
	}
}
