package helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openerp.CreateApplicant;

import org.json.JSONException;
import org.json.JSONObject;

import play.Logger;
import controllers.ApplicantProperties;
import couchbase.CouchBaseOperation;

public class PropertiesDBOperation {
	HashMap dataStoreValue = new HashMap();
	CouchBaseOperation storeData = null;
	String formType = "Mortgage Application";
	String subForm = "Mortgage Application 11";
	String applicantID = null;

	public void updateCBOpenErpProperties(List<ApplicantProperties> propertiesList,
		String howManyProperties,String		applicantID ) {
		Logger.info("Inside updateCBOpenERpProperties of PropertiesDBOperation class");
		Logger.debug("howManyProperties "+howManyProperties);
		dataStoreValue.put("Applicant-howManyProperties",howManyProperties);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// get current date time with Calendar()
		Calendar cal = Calendar.getInstance();
		String currentDateTime = (dateFormat.format(cal.getTime()));
		CreateApplicant createLead = new CreateApplicant();
		int howManyProp = 0;
		if (howManyProperties != null
				&& howManyProperties.equalsIgnoreCase("one"))
			howManyProp = 1;
		else if (howManyProperties != null
				&& howManyProperties.equalsIgnoreCase("two"))
			howManyProp = 2;
		else if (howManyProperties != null
				&& howManyProperties.equalsIgnoreCase("three"))
			howManyProp = 3;
		else if (howManyProperties != null
				&& howManyProperties.equalsIgnoreCase("four"))
			howManyProp = 4;
		else
			howManyProp = 7;
		ApplicantProperties properties = null;

		Logger.debug("how many property " + howManyProp);
		try {
			for (int i = 0; i < howManyProp; i++) {
				Logger.debug("inside for loop");
				properties = propertiesList.get(i);
				String address = properties.getAddress();
				String appxValue = properties.getAppxValue();
				String mortgageValue = properties.getMortgage();
				int rentMo = properties.getRentMo();
				String condoFees = properties.getCondoFees();
				String ownership = properties.getOwnership();
				boolean selling = properties.isSelling();
				dataStoreValue.put("Applicant-address" + i, address);
				dataStoreValue.put("Applicant-appx_value" + i, appxValue);
				dataStoreValue.put("Applicant-mortgage_value" + i,	mortgageValue);
				dataStoreValue.put("Applicant-rent_mo" + i, rentMo);
				dataStoreValue.put("Applicant-condoFees" + i, condoFees);
				dataStoreValue.put("Applicant-ownership" + i, ownership);
				dataStoreValue.put("Applicant-sellingYesNo" + i, selling);
				Logger.debug("address["+i+"] "+address+"\n appxValue["+i+"] "+appxValue+"\n mortgageValue["+i+"]"+mortgageValue+"\n rentMO["+i+"]"+rentMo+"\n CondoFees["+i+"]"+condoFees+"\n ownership["+i+"]"+ownership+"\n selling ["+i+"]"+selling);
				Logger.debug(!mortgageValue.equalsIgnoreCase("")
						+ "!mortgageValue1.equalsIgnoreCase() ");
				Logger.debug(mortgageValue.equalsIgnoreCase("")
						+ "!mortgageValue1.equalsIgnoreCase() ");

				if (mortgageValue != null
						&& !mortgageValue.equalsIgnoreCase("")
						&& selling == true) {
					Logger.debug("both mortgagae and selling values are comming.");

					if (rentMo != 0)
						createLead.createApplicantMortgage(applicantID,
								address, 1, selling);
					else
						createLead.createApplicantMortgage(applicantID,
								address, 1, selling);

					Logger.debug("Record is updated in applicant mortgage of openERP..");
					if (condoFees != null && !condoFees.equalsIgnoreCase("")) {
						Logger.debug("condo values comming");
						createLead.createApplicantProperties(applicantID,
								address, condoFees, 1, selling);
						Logger.debug("Record is updated in applicant properties of openERP with condo value");
					} else {
						Logger.debug("condo values not coming");
						createLead.createApplicantProperties(applicantID,
								properties.getAddress(), "-1", 1, selling);
						Logger.debug("Record is updated in applicant properties of openERP with condo value");
					}

				} else if (properties.getMortgage() != null
						&& properties.getMortgage().equalsIgnoreCase("")
						&& selling == false) {

					Logger.debug("when mortgage value is comming but selling value is not comming.");

					if (properties.getRentMo() != 0)
						createLead.createApplicantMortgage(applicantID,
								properties.getAddress(), 1, selling);
					else
						createLead.createApplicantMortgage(applicantID,
								properties.getAddress(), 1, selling);

					Logger.debug("Record is updated in applicant mortgage of openERP with selling false and with rentMo1");
					if (properties.getCondoFees() != null
							&& !properties.getCondoFees().equalsIgnoreCase("")) {
						Logger.debug("condo values comming");
						createLead.createApplicantProperties(applicantID,
								properties.getAddress(),
								properties.getCondoFees(), 1,
								properties.isSelling());
						Logger.debug("Record is updated in applicant properties of openERP with condo fees");
					} else {
						Logger.debug("condo values not comming");
						createLead.createApplicantProperties(applicantID,
								properties.getAddress(), "-1", 1, selling);
						Logger.debug("Record is updated in applicant properties of openERP without condo value");
					}
				} else if (properties.getMortgage() != null
						&& properties.getMortgage().equalsIgnoreCase("")
						&& selling == true) {
					Logger.debug("when mortgage is not comming but selling value is comming");

					// createLead.createApplicantMortgage(applicantID,
					// address1,1, selling1);
					if (properties.getCondoFees() != null
							&& !properties.getCondoFees().equalsIgnoreCase("")) {
						Logger.debug("condo values set to yes");
						createLead.createApplicantProperties(applicantID,
								address, condoFees, 1, selling);
						Logger.debug("Record updated in applicant properties of  openERP with condoFees");
					} else {
						Logger.debug("condo values set to no");
						createLead.createApplicantProperties(applicantID,
								properties.getAddress(), "-1", 1,
								properties.isSelling());
						Logger.debug("Record updated in applicant properties of  openERP but condoFees is empty ");
					}
				} else {
					Logger.debug("both mortgage and selling value not comming ");
					if (condoFees != null && !condoFees.equalsIgnoreCase("")) {
						Logger.debug("condo values set to yes");
						createLead.createApplicantProperties(applicantID,
								address, condoFees, 1, selling);
						Logger.debug("Updated record in openERP with condo values");
					} else {
						Logger.debug("condo values set to no");
						createLead.createApplicantProperties(applicantID,
								address, "-1", 1, selling);
						Logger.debug("Updated record in openERP with out condo values");
					}
					// end of if else rentalyesno1
				}
			}
			CouchBaseOperation appendData = new CouchBaseOperation();
			dataStoreValue.put("Applicant-subForm-11", subForm);
			appendData.appendDataInCouchBase("Applicant_" + applicantID,
					dataStoreValue);
		} catch (Exception e) {
			Logger.error("Error when inserting property record into openerp and couchbase.");
		}
	}
	public Map readPropertiesFromCB(){
		Logger.info("Inside readPropertiesFromCB of PorpertiesDBOperation");
		List<ApplicantProperties> propertiesList = new ArrayList<ApplicantProperties>();
		CouchBaseOperation  couchbaseObject=null;
		ApplicantProperties properties=null;
		String howManyProperties="";
		JSONObject jsonObject=null;
		String applicantID = "2753";
		String applicantIDCB="Applicant_"+applicantID;
		int howManyProp=0;
		Map propertyMap=new HashMap();
		try{
			couchbaseObject =new CouchBaseOperation();
			jsonObject = couchbaseObject.getCouchBaseData(applicantIDCB);
			howManyProperties = jsonObject.getString("Applicant-howManyProperties");
			Logger.debug("howManyProperties "+howManyProperties);
			if(howManyProperties != null){
				if(howManyProperties.equalsIgnoreCase("one"))
					howManyProp=1;
				if(howManyProperties.equalsIgnoreCase("two"))
					howManyProp=2;
				if(howManyProperties.equalsIgnoreCase("three"))
					howManyProp=3;
				if(howManyProperties.equalsIgnoreCase("four"))
					howManyProp=4;
				if(howManyProperties.equalsIgnoreCase("more"))
					howManyProp=7;
				for(int i=0;i<howManyProp;i++){
					properties=new ApplicantProperties();
					properties.setAddress(jsonObject.getString("Applicant-address"+i));
					properties.setAppxValue(jsonObject.getString("Applicant-appx_value"+i));
					properties.setMortgage(jsonObject.getString("Applicant-mortgage_value"+i));
					properties.setRentMo(jsonObject.getInt("Applicant-rent_mo"+i));
					properties.setCondoFees(jsonObject.getString("Applicant-condoFees"+i));
					properties.setOwnership(jsonObject.getString("Applicant-ownership"+i));
					properties.setSelling(jsonObject.getBoolean("Applicant-sellingYesNo"+i));
					propertiesList.add(properties);
				}
				
			}
		}catch(JSONException | NullPointerException excp){
			Logger.debug("Exception when reading howManyProperties record from couchbase.",excp);
		}
		Logger.debug("howManyProperties >>>"+howManyProperties);
		propertyMap.put("propertyList", propertiesList);
		propertyMap.put("howManyProperty",howManyProperties);
		//Need to return howManyProperties(put in a hashmap and return hashmap)
		return propertyMap;
	}
}
