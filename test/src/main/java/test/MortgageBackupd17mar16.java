package controllers;

import static play.data.Form.form;
import helper.AddressDBOperation;
import helper.ApplicantDBOperation;
import helper.CouchbaseDAO;
import helper.FormatDateString;
import helper.GenericHelperClass;
import helper.LendingTermDBOperation;
import helper.PersonalInfoDBOperation;
import helper.PropertiesDBOperation;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import net.iharder.Base64;
import openerp.CreateApplicant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pdfGeneration.MortgageApplicationPdfGeneration;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Session;
import play.mvc.Result;
import views.html.MortgageApplicationSucess;
import views.html.completed_application;
import views.html.expired_page;
import views.html.mortgagePage1;
import views.html.mortgagePage10Assets;
import views.html.mortgagePage11Properties;
import views.html.mortgagePage12Disclose;
import views.html.mortgagePage1a;
import views.html.mortgagePage1b;
import views.html.mortgagePage2Pre;
import views.html.mortgagePage2Pur;
import views.html.mortgagePage2Ref;
import views.html.mortgagePage3;
import views.html.mortgagePage4;
import views.html.mortgagePage5a;
import views.html.mortgagePage5b;
import views.html.mortgagePage6;
import views.html.mortgagePage6a;
import views.html.mortgagePage6b;
import views.html.mortgagePage7Address;
import views.html.mortgagePage7a;
import views.html.mortgagePage7b;
import views.html.mortgagePage8;
import views.html.mortgagePage9;
import views.html.privacypolicy;
import address.splitAddress.Address;
import addressgroup.pojo.AddressGroup;
import addressgroup.pojo.Incomes;

import com.debortoliwines.openerp.api.ObjectAdapter;
import com.debortoliwines.openerp.api.Row;
import com.sendwithus.SendWithUsExample;
import com.syml.postgres.service.PostGresDaoService;

import couchbase.CouchBaseOperation;

class Mortgage1 extends Controller {

	public static Result mortgageApplication() {

		try {
			Logger.info("Inside mortgageApplication");
			Session session = Http.Context.current().session();

			String crmLeadId = request().getQueryString("crmLeadId");
			String referralEmail = request().getQueryString("referrerEmail");
			String referralName = request().getQueryString("referralName");

			String role = request().getQueryString("role");
			String expiryDate = request().getQueryString("expireDate");
			boolean isExpired = true;
			int id = 0;
			try {
				id = Integer.parseInt(crmLeadId);
				String pattern = "dd/MM/yyyy";
				String today = new SimpleDateFormat(pattern).format(new Date());
				Date expDate = new SimpleDateFormat("dd/MM/yyyy",
						Locale.ENGLISH).parse(expiryDate);
				Date end = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
						.parse(today);
				isExpired = expDate.compareTo(end) >= 0;
				Logger.debug("is Expired   " + isExpired);
			} catch (Exception e) {
				Logger.error("Error when calculating expiration of date ", e);
			}

			Opportunity opportunity = new Opportunity();

			opportunity.setId(id);
			opportunity.setReferralSourceName(referralName);
			opportunity.setReferralSourceEmail(referralEmail);

			if (opportunity.getId() != 0
					&& opportunity.getReferralSourceEmail() != null
					&& opportunity.getReferralSourceName() != null) {
				session.put("crmLeadId", opportunity.getId() + "");
				session.put("referralEmail",
						opportunity.getReferralSourceEmail());
				session.put("referralName", opportunity.getReferralSourceName());
			}
			Logger.debug("crmLead id is  ------ " + crmLeadId
					+ "\n Referral Email id is  ------  " + referralEmail
					+ "\n referralName " + referralName);
			boolean isMobile = false;

			opportunity.getForms().setDeviceType("Desktop");
			session.put("isMobile", "no");
			String browserDetails = request().getHeader("User-Agent");
			if (browserDetails.contains("mobile")
					|| browserDetails.contains("Mobile")) {
				isMobile = true;
				session.put("isMobile", "isMobile");
				opportunity.getForms().setDeviceType("Mobile");

			}
			Opportunity oldOpportunity = new CouchBaseOperation()
					.getOpporunityData(id + "");
			Applicant applicant = new Applicant();

			Logger.debug("opportunity>>>>>>>" + opportunity);
			int pogressStatus = 0;
			if (oldOpportunity != null
					&& oldOpportunity.getPogressStatus() != 0) {
				pogressStatus = oldOpportunity.getPogressStatus();
			}
			if (oldOpportunity == null
					&& pogressStatus < 10
					&& (role.equalsIgnoreCase("B") || role
							.equalsIgnoreCase("C") && isExpired)) {
				Logger.debug("Applicant id does not exist");

				try {

				} catch (Exception e) {

				}
				if (!isMobile)
					return ok(mortgagePage1.render(opportunity, applicant,
							applicant));
				else
					return ok(mortgagePage1a.render(opportunity, applicant));
			} else {
				Logger.debug("Exsting Applicant ");
				Logger.debug("pogressStatus " + pogressStatus);
				if (role.equalsIgnoreCase("B") || role.equalsIgnoreCase("C")
						&& isExpired) {
					Applicant applicantDetails = null;
					if (pogressStatus < 99) {
						applicantDetails = oldOpportunity.getApplicants()
								.get(0);
						session.put("leadingGoal",
								oldOpportunity.getWhat_is_your_lending_goal());
						session.put("applicantID", applicantDetails.getId()
								+ "");
						session.put("additionalApplicants",
								oldOpportunity.getIsAdditionalApplicantExist());

						session.put("applicantFirstName",
								applicantDetails.getApplicant_name());
						session.put("applicantLasttName",
								applicantDetails.getApplicant_last_name());
						session.put("applicantEmail",
								applicantDetails.getEmail_personal());

						if (oldOpportunity.getApplicants() != null
								&& oldOpportunity.getApplicants().size() > 1
								&& oldOpportunity
										.getIsAdditionalApplicantExist()
										.equalsIgnoreCase("Yes")) {
							applicantDetails = oldOpportunity.getApplicants()
									.get(1);
							session.put("co_applicantFirstName",
									applicantDetails.getApplicant_name());
							session.put("co_applicantLastName",
									applicantDetails.getApplicant_last_name());
							session.put("co_applicantEmail",
									applicantDetails.getEmail_personal());
							session.put("applicantID2",
									applicantDetails.getId() + "");
						}
						String mlsListed = oldOpportunity.getMls();
						return existingUser(pogressStatus, isMobile, mlsListed);
					} else
						return ok(completed_application
								.render("Your Application is expired. Please email Visdom at support@visdom.ca if you requirement continued access."));

				} else
					return ok(expired_page
							.render("Your Application is expired. Try Again"));
			}
		} catch (Exception e) {
			Logger.error(
					"Error in mortgageApplication method of Mortgage class : ",
					e);
			return ok("Some thing went wrong when rendering mortgage First page");
		}
	}

	public static Result existingUser(int progressStatus, boolean isMobile,
			String mlsListed) {

		Session session = Http.Context.current().session();
		ApplicantAddressParameter7 appAddressParam = null;
		PersonalInfo personalInfo = null;
		TotalAssets totalAssets = new TotalAssets();
		totalAssets.setVehicle(new ArrayList<AssetsParam>());
		totalAssets.setBankAccount(new ArrayList<AssetsParam>());
		totalAssets.setRrsp(new ArrayList<AssetsParam>());
		totalAssets.setInvestments(new ArrayList<AssetsParam>());
		totalAssets.setOthers(new ArrayList<AssetsParam>());
		CoApplicantAddressParameter7 coAppAddressParam = null;
		String lendingGoal = "";
		String applicantName = "";
		String coApplicantName = "";
		String additionalApplicant = "";

		try {
			lendingGoal = (String) session.get("leadingGoal");
			applicantName = (String) session.get("applicantFirstName");
			coApplicantName = (String) session.get("co_applicantFirstName");
			additionalApplicant = (String) session.get("additionalApplicants");
		} catch (Exception e) {
			Logger.error(
					"Error when reading applicantName and coApplicant name from couchbase ",
					e);
		}
		switch (progressStatus) {
		case 20:

			if (mlsListed != null && mlsListed.equalsIgnoreCase("PrivateSale"))
				return ok(mortgagePage3.render("", "", "", "", "", "", "", "",
						""));
			else
				return ok(mortgagePage4.render("", "", "", "", ""));
		case 30:
			return ok(mortgagePage4.render("", "", "", "", ""));
		case 35:
			return ok(mortgagePage5a.render("", "", "", "", "", ""));
		case 40:
			return ok(mortgagePage5b.render("", "", "", "", ""));
		case 45:
			if (!isMobile)
				return ok(mortgagePage6.render(additionalApplicant,
						applicantName, coApplicantName, "", "", "", "", "", "",
						"", "", "", "", "", "", "", "", "", "", "", ""));
			else {
				personalInfo = new PersonalInfo();
				personalInfo.setAdditionalApplicant(additionalApplicant);
				personalInfo.setApplicantName(applicantName);
				return ok(mortgagePage6a.render(personalInfo));
			}
		case 50:
			if (!isMobile)
				return ok(mortgagePage6.render(additionalApplicant,
						applicantName, coApplicantName, "", "", "", "", "", "",
						"", "", "", "", "", "", "", "", "", "", "", ""));
			else {
				personalInfo = new PersonalInfo();
				personalInfo.setAdditionalApplicant(additionalApplicant);
				personalInfo.setCoApplicantName(coApplicantName);
				return ok(mortgagePage6b.render(new PersonalInfo()));
			}
		case 55:
			if (!isMobile)
				return ok(mortgagePage7Address.render(additionalApplicant,
						applicantName, coApplicantName,
						new ApplicantAddressParameter7(),
						new CoApplicantAddressParameter7()));
			else {
				appAddressParam = new ApplicantAddressParameter7();
				appAddressParam.setAdditionalApplicant(additionalApplicant);
				appAddressParam.setApplicantName(applicantName);
				return ok(mortgagePage7a.render(appAddressParam));
			}
		case 60:
			if (!isMobile)
				return ok(mortgagePage7Address.render(additionalApplicant,
						applicantName, coApplicantName,
						new ApplicantAddressParameter7(),
						new CoApplicantAddressParameter7()));
			else {
				coAppAddressParam = new CoApplicantAddressParameter7();
				coAppAddressParam.setCoApplicantName(coApplicantName);
				return ok(mortgagePage7b.render(coAppAddressParam));
			}
		case 65:
			return ok(mortgagePage8.render("", new EmployeIncomeTypeParam(),
					new SelfEmployeIncomeTypeParam(),
					new PensionIncomeTypeParam(),
					new InvestmentsIncomeTypeParam(),
					new MaternityIncomeTypeParam(),
					new VehicleAllowIncomeTypeParam(),
					new LivingAllowIncomeTypeParam(),
					new CommissionIncomeTypeParam(),
					new BonusIncomeTypeParam(), new OtherIncomeTypeParam(),
					applicantName));
		case 70:
			if (additionalApplicant != null
					&& additionalApplicant.equalsIgnoreCase("yes"))
				return ok(mortgagePage9.render(additionalApplicant,
						new EmployeIncomeTypeParam(),
						new SelfEmployeIncomeTypeParam(),
						new PensionIncomeTypeParam(),
						new InvestmentsIncomeTypeParam(),
						new MaternityIncomeTypeParam(),
						new VehicleAllowIncomeTypeParam(),
						new LivingAllowIncomeTypeParam(),
						new CommissionIncomeTypeParam(),
						new BonusIncomeTypeParam(), new OtherIncomeTypeParam(),
						coApplicantName));
			else
				return ok(mortgagePage10Assets.render("", applicantName,
						coApplicantName, totalAssets));
		case 75:

			return ok(mortgagePage10Assets.render("", applicantName,
					coApplicantName, totalAssets));
		case 85:
			return ok(mortgagePage11Properties.render("", applicantName,
					coApplicantName, new ArrayList<ApplicantProperties>()));
		case 95:
			return ok(mortgagePage12Disclose.render("", additionalApplicant,
					new Integer(4), applicantName, coApplicantName));
		default:
			return ok("Previous Data not found please fill newly application .");
		}
	}

	public static Result mortgagePage1a() {

		Logger.info("Inside mortgagePage1a");
		DynamicForm dynamicForm = form().bindFromRequest();
		ApplicantDBOperation appDBOperation = null;
		try {
			String applicantFirstName = dynamicForm.get("firstName");
			String applicantLastName = dynamicForm.get("lastName");
			String applicantEmail = dynamicForm.get("email");
			String term = dynamicForm.get("term");

			Session session = Http.Context.current().session();
			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = new Opportunity();
			opportunity.setId(crm_LeadId);
			Applicant applicant = new Applicant();
			String additionalApplicants = dynamicForm
					.get("additionalApplicants");

			applicant.setApplicant_name(applicantFirstName);
			applicant.setApplicant_last_name(applicantLastName);
			applicant.setEmail_personal(applicantEmail);
			opportunity.setIsAdditionalApplicantExist(additionalApplicants);
			opportunity.setWhat_is_your_lending_goal(term);
			opportunity.getApplicants().add(applicant);
			opportunity.setPogressStatus(5);

			String ip = request().remoteAddress();
			appDBOperation = new ApplicantDBOperation();
			appDBOperation.createApplicant(opportunity, ip);

			if (additionalApplicants != null
					&& additionalApplicants.equalsIgnoreCase("yes"))
				return ok(mortgagePage1b.render(opportunity,new Applicant()));
			else if (term != null && term.equalsIgnoreCase("PreApproval"))
				return ok(mortgagePage2Pre.render("uniid",
						additionalApplicants, new LendingTerm()));
			else if (term != null && term.equalsIgnoreCase("Purchase"))
				return ok(mortgagePage2Pur.render(new LendingTerm(),
						additionalApplicants));
			else if (term != null && term.equalsIgnoreCase("Refinance"))
				return ok(mortgagePage2Ref.render(new LendingTerm(),
						additionalApplicants));
			else
				// req.setAttribute("message",
				// " We are sorry, but it seems the security and reliability of your internet connection may have been weakened.  To protect your identity and the security of your information, can you please submit this application again");
				// req.getRequestDispatcher("MortgageApplicationSucess.jsp").forward(req,
				// res);
				return ok("Demo return, it should be MortgageApplicationSucess.jsp");

		} catch (Exception e) {
			Logger.error("Error in mortgagePage1b : ", e);
			return ok("Something went wrong when rendering mortgagePage1b or mortgagePage2Pre/Pur/Ref ");
		}

	}

	public static Result mortgagePage1b() {
		Logger.info("Inside mortgagePage1b");
		Applicant appBasicDetails = new Applicant();
		DynamicForm dynamicForm = form().bindFromRequest();
		CouchBaseOperation storeData = new CouchBaseOperation()
		;
		ApplicantDBOperation appDBOperation = null;
		try {

			Session session = Http.Context.current().session();
			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			String term = dynamicForm.get("term");
			String additionalApplicants = dynamicForm
					.get("additionalApplicant");
			String applicantId = dynamicForm.get("applicantId");

			String coApplicantFirstName = dynamicForm.get("adFirstName");
			String coApplicantLastName = dynamicForm.get("adLastName");
			String coApplicantEmail = dynamicForm.get("adEmail");
			Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
					+ "");
			appBasicDetails.setApplicant_name(coApplicantFirstName);
			appBasicDetails.setApplicant_last_name(coApplicantLastName);
			appBasicDetails.setEmail_personal(coApplicantEmail);
			appBasicDetails.setApplicantId(applicantId);
			if (opportunity != null) {
				opportunity.getApplicants().add(appBasicDetails);
			}
			opportunity.setPogressStatus(10);

			// get ip of latest form sumitted
			String ip = request().remoteAddress();
			appDBOperation = new ApplicantDBOperation();
			appDBOperation.createCoApplicant(opportunity, ip);

			if (term != null && term.equalsIgnoreCase("PreApproval"))
				return ok(mortgagePage2Pre.render("uniid",
						additionalApplicants, new LendingTerm()));
			else if (term != null && term.equalsIgnoreCase("Purchase"))
				return ok(mortgagePage2Pur.render(new LendingTerm(),
						additionalApplicants));
			else if (term != null && term.equalsIgnoreCase("Refinance"))
				return ok(mortgagePage2Ref.render(new LendingTerm(),
						additionalApplicants));
			else
				// req.setAttribute("message",
				// " We are sorry, but it seems the security and reliability of your internet connection may have been weakened.  To protect your identity and the security of your information, can you please submit this application again");
				// req.getRequestDispatcher("MortgageApplicationSucess.jsp").forward(req,
				// res);
				return ok("Demo return, it should be MortgageApplicationSucess.jsp");

		} catch (Exception e) {
			Logger.error("error in mortgagePage1b : ", e);
			return ok("Something went wrong when rendering mortgagePage2Pre/Pur/Ref");
		}
	}

	public static Result mortgagePage2() {

		Logger.info("Inside mortgagePage2 ");

		Opportunity opportunity = new Opportunity();
		Applicant applicant = null;
		Applicant co_applicant=null;
		PostGresDaoService postGresDaoService = new PostGresDaoService();

		DynamicForm dynamicForm = form().bindFromRequest();

		String coApplicantFirstName = null;
		String coApplicantLastName = null;
		String coApplicantEmail = null;
		try {
			Session session = Http.Context.current().session();
			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			String leadingGoal = dynamicForm.get("term");
			String additionalApplicants = dynamicForm
					.get("additionalApplicants");
			opportunity.setWhat_is_your_lending_goal(leadingGoal);
			opportunity.setIsAdditionalApplicantExist(additionalApplicants);
			opportunity.setId(crm_LeadId);
			String applicantFirstName = dynamicForm.get("firstName");
			String applicantLastName = dynamicForm.get("lastName");
			String applicantEmail = dynamicForm.get("email");

			applicant = new Applicant();
			applicant.setApplicant_name(applicantFirstName);
			applicant.setApplicant_last_name(applicantLastName);
			applicant.setEmail_personal(applicantEmail);
			opportunity.getApplicants().add(applicant);
			// Let's make the formType Mortgage Application which allows us to
			// "Group"
			// the application in couchbase.
			String formType = "Mortgage Application";

			// Let's create a subform field which allows to identify this
			// particular "subform" submission.
			String subForm = "Mortgage Application 1";

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			// get current date time with Calendar()
			Calendar cal = Calendar.getInstance();
			String currentDateTime = (dateFormat.format(cal.getTime()));

			// get ip of latest form sumitted
			String ip = request().remoteAddress();

			Logger.info("input from form1 mortgage application  : firstname "
					+ applicantFirstName + "\t lastname : " + applicantLastName
					+ "\t email : " + applicantEmail + "\t current time : "
					+ currentDateTime + "\t ip : " + ip + "\t term: "
					+ leadingGoal);

			// Logger.debug("old unique id is  "+uniid);
			session.put("leadingGoal", leadingGoal);

			session.put("additionalApplicants", additionalApplicants);
			Logger.debug("additionalApplicants" + additionalApplicants);
			CouchBaseOperation storeData = new CouchBaseOperation();

			if (additionalApplicants.equalsIgnoreCase("yes")) {

				coApplicantFirstName = dynamicForm.get("adFirstName");
				coApplicantLastName = dynamicForm.get("adLastName");
				coApplicantEmail = dynamicForm.get("adEmail");
				co_applicant = new Applicant();
				co_applicant.setApplicant_name(coApplicantFirstName);
				co_applicant.setApplicant_last_name(coApplicantLastName);
				co_applicant.setEmail_personal(coApplicantEmail);

				// applicantCreate.createApplicant(opportunity);
				session.put("co_applicantFirstName", coApplicantFirstName);
				session.put("co_applicantLastName", coApplicantLastName);
				session.put("co_applicantEmail", coApplicantEmail);
			}
			opportunity = postGresDaoService.createApplicant(opportunity);
			int appId =opportunity.getApplicants().get(0).getId();
			String applicantId = appId + "";
			Logger.debug("applicantID >>>" + applicantId);
			session.put("applicantID", applicantId);

			applicant.setId(appId);
			if (additionalApplicants.equalsIgnoreCase("yes")) {
				int appId2 =opportunity.getApplicants().get(1).getId();
				String applicantId2 = appId2 + "";
				session.put("applicantID2", applicantId2);
				co_applicant.setId(appId2);
				opportunity.getApplicants().add(co_applicant);

			}
			opportunity.getApplicants().add(applicant);
			opportunity.setPogressStatus(10);

			storeData.storeDataInCouchBase(formType, opportunity);

			session.put("applicantFirstName", applicantFirstName);
			session.put("applicantLasttName", applicantLastName);
			session.put("applicantEmail", applicantEmail);

			if (leadingGoal.equalsIgnoreCase("PreApproval")
					&& leadingGoal != null)
				return ok(mortgagePage2Pre.render("uniid",
						additionalApplicants, new LendingTerm()));
			else if (leadingGoal.equalsIgnoreCase("Purchase")
					&& leadingGoal != null)
				return ok(mortgagePage2Pur.render(new LendingTerm(),
						additionalApplicants));
			else if (leadingGoal.equalsIgnoreCase("Refinance")
					&& leadingGoal != null)
				return ok(mortgagePage2Ref.render(new LendingTerm(),
						additionalApplicants));
			else
				// req.setAttribute("message",
				// " We are sorry, but it seems the security and reliability of your internet connection may have been weakened.  To protect your identity and the security of your information, can you please submit this application again");
				// req.getRequestDispatcher("MortgageApplicationSucess.jsp").forward(req,
				// res);
				return ok("Demo return, it should be MortgageApplicationSucess.jsp");
		} catch (Exception e) {
			Logger.error("error in try:" + e.getMessage() + "  " + e.getCause());

			// req.setAttribute("message",
			// " We are sorry, but it seems the security and reliability of your internet connection may have been weakened.  To protect your identity and the security of your information, can you please submit this application again");
			// req.getRequestDispatcher("MortgageApplicationSucess.jsp").forward(req,
			// res);
			return ok("Demo return, it should be MortgageApplicationSucess.jsp");
		}
	}

	public static Result mortgagePage2Pre() {

		Logger.debug("Inside mortgagePage2Pre");
		DynamicForm dynamicForm = form().bindFromRequest();
		CreateApplicant oppertunityUpdate = new CreateApplicant();
		CouchBaseOperation storeData = new CouchBaseOperation();
		
		Session session = Http.Context.current().session();

		int crm_LeadId = 0;
		try {
			crm_LeadId = Integer.parseInt(session.get("crmLeadId").toString());
		} catch (Exception e) {
			Logger.error("error in getting CRMLEADID from session id " + crm_LeadId);
		}
		Opportunity opportunity = storeData.getOpporunityData(crm_LeadId + "");
		Logger.info("------------------opporunity data ----------------------------");
		Logger.info(" data "+opportunity);
		String leadingGoal = (String) session.get("leadingGoal");

		String province = dynamicForm.get("provience");
		String purchaseprice = dynamicForm.get("purchaseprice");
		String downpayment = dynamicForm.get("downpayment");

		String bankAccount = dynamicForm.get("bankAccount");
		String rrsps = dynamicForm.get("rrsps");
		String investments = dynamicForm.get("investments");
		String borrowed = dynamicForm.get("borrowed");
		String saleofProperty = dynamicForm.get("saleofProperty");
		String gift = dynamicForm.get("gift");
		String personalCash = dynamicForm.get("personalCash");
		String existingEquity = dynamicForm.get("existingEquity");
		String sweatEnquity = dynamicForm.get("sweatEnquity");
		double down_payment_amount = 0;
		double purchase_price = 0;

		try {
			down_payment_amount = Double.parseDouble(downpayment);
		} catch (Exception e) {
			Logger.error("Error when parsing downpayment to double value " + e);

		}
		try {
			purchase_price = Double.parseDouble(purchaseprice);
		} catch (Exception e) {
			Logger.error("Error when parsing purschase price to double value "
					+ e);

		}

		opportunity.setProvince(province);
		opportunity.setDown_payment_amount(down_payment_amount);
		opportunity.setPurchase_price(purchase_price);
		Set<String> paymentsource1 = new HashSet<String>();
		if (bankAccount.equalsIgnoreCase("true")) {
			bankAccount = "Bank Account";
			paymentsource1.add(bankAccount);
			opportunity.setBank_account(1.0);
		}
		if (rrsps.equalsIgnoreCase("true")) {
			rrsps = "RRSPS";
			paymentsource1.add(rrsps);
			opportunity.setRrsp_amount(1.0);
		}
		if (investments.equalsIgnoreCase("true")) {
			investments = "Investment";
			paymentsource1.add(investments);
			opportunity.setInvestment(1.0);
		}

		if (borrowed.equalsIgnoreCase("true")) {
			borrowed = "Borrowed";
			paymentsource1.add(borrowed);
			opportunity.setBorrowed_amount(1.0);
		}

		if (saleofProperty.equalsIgnoreCase("true")) {
			saleofProperty = "Sale Of Property";
			paymentsource1.add(saleofProperty);
			opportunity.setSaleOfExistingAmount(1.0);
		}

		if (gift.equalsIgnoreCase("true")) {
			gift = "Gift";
			paymentsource1.add(gift);
			opportunity.setGifted_amount(1.0);
		}
		if (personalCash.equalsIgnoreCase("true")) {
			personalCash = "Personal Cash";
			paymentsource1.add(personalCash);
			opportunity.setPersonalCashAmount(1.0);
		}
		if (existingEquity.equalsIgnoreCase("true")) {
			existingEquity = "Existing Equity";
			paymentsource1.add(existingEquity);
			opportunity.setExistingEquityAmount(1.0);
		}
		if (sweatEnquity.equalsIgnoreCase("true")) {
			sweatEnquity = "Sweat Enquity";
			paymentsource1.add(sweatEnquity);
			opportunity.setSweat_equity_amount(1.0);
		}

		Logger.debug("payment resource " + paymentsource1);

		String living4Financing = dynamicForm.get("living4Financing");
		String additionalApplicants = dynamicForm.get("additionalApplicants");
		String formType = "Mortgage Application";
		String subForm = "mortgagePage2pre";
		opportunity.setLivingInProperty(living4Financing);

		String applicantID = (String) session.get("applicantID");
		String applicantID2 = null;
		if (additionalApplicants.equalsIgnoreCase("yes")) {
			applicantID2 = (String) session.get("applicantID2");
		}

		int leadId = 0;
		int applicantIDInt = 0;
		int applicantID2Int = 0;
		try {

			leadId = Integer.parseInt((String) session.get("crmLeadId"));
			Logger.debug("Lead id >>>>" + leadId + "<<<<<");
			applicantIDInt = Integer.parseInt(applicantID);
			Logger.debug("applicantIDInt one>>>>" + applicantIDInt + "<<<<<");
			if (applicantID2 != null)
				applicantID2Int = Integer.parseInt(applicantID2);
			Logger.debug("applicantIDInt two >>>>" + applicantID2Int + "<<<<<");
			Logger.debug("leadId " + leadId);
		} catch (Exception e) {
			Logger.error("Some value not found from session :", e);
		}

		int liveDetails = 0;
		double monthlyRentalIncome = 0;
		if (living4Financing.equalsIgnoreCase("Renter")
				&& living4Financing != null) {
			String rentalAmount = dynamicForm.get("rentalAmount");
			liveDetails = 2;
			monthlyRentalIncome = Double.parseDouble(rentalAmount);
			opportunity.setMonthlyRentalIncome(monthlyRentalIncome);
			Logger.debug("Renter");
		} else if (living4Financing.equalsIgnoreCase("OwnerAndRenter")
				&& living4Financing != null) {

			String rentalAmount = dynamicForm.get("rentalAmount");

			liveDetails = 3;
			monthlyRentalIncome = Double.parseDouble(rentalAmount);
			Logger.debug("Owner and Renter");
			opportunity.setMonthlyRentalIncome(monthlyRentalIncome);

		} else if (living4Financing.equalsIgnoreCase("Owner (Myself)")
				&& living4Financing != null) {
			liveDetails = 1;
			Logger.debug("Owner");
		}

		opportunity.setPogressStatus(20);

		oppertunityUpdate.assignOppertunityPreApproval(applicantIDInt, leadId,
				downpayment + "", liveDetails + "", monthlyRentalIncome + "",
				purchaseprice, province);

		if (applicantID2Int != 0) {
			oppertunityUpdate.assignOppertunityPreApproval(applicantID2Int,
					leadId, downpayment + "", liveDetails + "",
					monthlyRentalIncome + "", purchaseprice, province);
		}
		storeData.storeDataInCouchBase(formType, opportunity);
		Logger.debug("provience " + province + "\n purchaseprice :"
				+ purchaseprice + "\n downpayment :" + downpayment
				+ "\n bankAccount: " + bankAccount + "\n investments :"
				+ investments + "\n borrowed: " + borrowed
				+ "\n saleofProperty :" + saleofProperty + "\n gift :" + gift
				+ "\n personalCash :" + personalCash + "\n existingEquity:"
				+ existingEquity + "\n sweatEnquity " + sweatEnquity
				+ "\n living4Financing  " + living4Financing
				+ "\n leadin goal " + leadingGoal + "\n additionalApplicants  "
				+ additionalApplicants);

		return ok(mortgagePage4.render(additionalApplicants, "", "", "", ""));

	}

	public static Result mortgagePage2Pur() {

		Logger.info("Inside mortgagePage2 Purchase");
		DynamicForm dynamicForm = form().bindFromRequest();
		CreateApplicant oppertunityUpdate = null;
		CouchBaseOperation storeData = new CouchBaseOperation();

		try {

			Session session = Http.Context.current().session();

			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
					+ "");

			String formattedAddress = dynamicForm.get("formatted_address");
			String downpayment30 = dynamicForm.get("downpayment30");
			// String downpayment = dynamicForm.get("downpayment");

			String bankAccount = dynamicForm.get("bankAccount");
			String rrsps = dynamicForm.get("rrsps");
			String investments = dynamicForm.get("investments");
			String borrowed = dynamicForm.get("borrowed");
			String saleofProperty = dynamicForm.get("saleofProperty");
			String gift = dynamicForm.get("gift");
			String personalCash = dynamicForm.get("personalCash");
			String existingEquity = dynamicForm.get("existingEquity");
			String sweatEnquity = dynamicForm.get("sweatEnquity");
			String mlsList = dynamicForm.get("mlsList");
			String additionalApplicants = dynamicForm
					.get("additionalApplicants");
			String living4Financing = dynamicForm.get("living4Financing");

			String formType = "Mortgage Application";
			String subForm = "Mortgage Application 2 Purchase";

			String leadingGoal = null;
			String applicantID = null;
			try {
				leadingGoal = (String) session.get("leadingGoal");
				applicantID = (String) session.get("applicantID");

				Logger.debug("leadingGoal " + leadingGoal);
				Logger.debug("applicantID " + applicantID);
				Logger.debug("leadid " + crm_LeadId);
			} catch (Exception e) {
				Logger.error("Error when reading from session" + e);
			}
			double down_payment_amount = 0;

			try {
				down_payment_amount = Double.parseDouble(downpayment30);
			} catch (Exception e) {
				Logger.error("Error when parsing downpayment to double value "
						+ e);

			}
			opportunity.setDown_payment_amount(down_payment_amount);

			Logger.debug("additionalApplicants pur " + additionalApplicants
					+ "\n formattedAddress  " + formattedAddress
					+ "\n downpayment30  :" + downpayment30
					+ "\n bankAccount: " + bankAccount + "\n investments :"
					+ investments + "\n borrowed: " + borrowed
					+ "\n saleofProperty :" + saleofProperty + "\n gift :"
					+ gift + "\n personalCash :" + personalCash
					+ "\n existingEquity: " + existingEquity
					+ "\n sweatEnquity " + sweatEnquity
					+ "\n living4Financing  " + living4Financing + "\n mlsList"
					+ mlsList);
			String rentalAmount = null;

			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>");
			int liveDetails = 0;
			Double monthlyRentalIncome = 0.0;
			opportunity.setLivingInProperty(living4Financing);
			if ((living4Financing.equalsIgnoreCase("OwnerMyself") && living4Financing != null)) {
				liveDetails = 1;
				Logger.debug("Living for financing Owner myself selected.");
			} else if (living4Financing.equalsIgnoreCase("Renter")
					&& living4Financing != null) {
				liveDetails = 2;
				rentalAmount = dynamicForm.get("rentalAmount");
				try {
					monthlyRentalIncome = Double.parseDouble(rentalAmount);
				} catch (Exception e) {
					Logger.debug("Error when parsing Rental Amount" + e);
				}
				opportunity.setMonthlyRentalIncome(monthlyRentalIncome);
			} else if (living4Financing.equalsIgnoreCase("OwnerAndRenter")
					&& living4Financing != null) {
				liveDetails = 3;
				rentalAmount = dynamicForm.get("rentalAmount");
				try {
					monthlyRentalIncome = Double.parseDouble(rentalAmount);
				} catch (Exception e) {
					Logger.debug("Error when parsing Rental Amount" + e);
				}
				Logger.debug("rentalAmount " + monthlyRentalIncome);
				opportunity.setMonthlyRentalIncome(monthlyRentalIncome);
			}
			Set<String> paymentsource1 = new HashSet<String>();
			if (bankAccount.equalsIgnoreCase("true")) {
				bankAccount = "Bank Account";
				opportunity.setBank_account(1.0);
			}
			if (rrsps.equalsIgnoreCase("true")) {
				rrsps = "RRSPS";
				paymentsource1.add(rrsps);
				opportunity.setRrsp_amount(1.0);
			}
			if (investments.equalsIgnoreCase("true")) {
				investments = "Investment";
				paymentsource1.add(investments);
				opportunity.setInvestment(1.0);
			}
			if (borrowed.equalsIgnoreCase("true")) {
				borrowed = "Borrowed";
				paymentsource1.add(borrowed);
				opportunity.setBorrowed_amount(1.0);
			}
			if (saleofProperty.equalsIgnoreCase("true")) {
				saleofProperty = "Sale Of Property";
				paymentsource1.add(saleofProperty);
				opportunity.setSaleOfExistingAmount(1.0);
			}

			if (gift.equalsIgnoreCase("true")) {
				gift = "Gift";
				paymentsource1.add(gift);
				opportunity.setGifted_amount(1.0);
			}
			if (personalCash.equalsIgnoreCase("true")) {
				personalCash = "Personal Cash";
				paymentsource1.add(personalCash);
				opportunity.setPersonalCashAmount(1.0);
			}
			if (existingEquity.equalsIgnoreCase("true")) {
				existingEquity = "Existing Equity";
				paymentsource1.add(existingEquity);
				opportunity.setExistingEquityAmount(1.0);
			}
			if (sweatEnquity.equalsIgnoreCase("true")) {
				sweatEnquity = "Sweat Enquity";
				paymentsource1.add(sweatEnquity);
				opportunity.setSweat_equity_amount(1.0);
			}
			@SuppressWarnings("unchecked")
			HashMap<String, String> addressMap = new Address()
					.getProperAddressTwo(formattedAddress);

			if (mlsList == null) {
				mlsList = "";
			}

			opportunity.setMls(mlsList);
			opportunity.setPogressStatus(20);
			opportunity.setAddress(addressMap.get("address1"));
			opportunity.setCity(addressMap.get("city"));
			opportunity.setProvince(addressMap.get("Province"));
			opportunity.setPostalCode(addressMap.get("postalcode"));
			Logger.debug("payment resource " + paymentsource1);
			oppertunityUpdate = new CreateApplicant();
			Logger.debug("applicantID= " + applicantID + "\n leadId= "
					+ crm_LeadId + "\n formattedAddress= " + formattedAddress
					+ "\n downpayment30= " + downpayment30
					+ "\n paymentsource1= " + paymentsource1
					+ "\n liveDetails=" + liveDetails
					+ "\n monthlyRentalIncome= " + monthlyRentalIncome);
			oppertunityUpdate.assignOppertunityPurchase(applicantID,
					crm_LeadId, "2", formattedAddress, downpayment30,
					paymentsource1 + "", liveDetails + "", monthlyRentalIncome
							+ "");

			int applicantID2Int = 0;
			try {
				applicantID2Int = Integer.parseInt(session.get("applicantID2"));
			} catch (Exception e) {
				Logger.error("Error in getting sessionvalue of coApplicant" + e);
			}

			if (applicantID2Int != 0) {
				oppertunityUpdate.assignOppertunityPurchase(applicantID2Int
						+ "", crm_LeadId, "2", formattedAddress, downpayment30,
						paymentsource1 + "", liveDetails + "",
						monthlyRentalIncome + "");
			}
			Logger.debug("Record is updated in oppurtunity of openERP ");
			storeData.storeDataInCouchBase(formType, opportunity);
			Logger.debug("Record is updated in couchbase");
			if (mlsList != null && mlsList.equalsIgnoreCase("MLSListed")
					|| mlsList.equalsIgnoreCase("NewBuild"))
				return ok(mortgagePage4.render(additionalApplicants, "", "",
						"", ""));
			else
				return ok(mortgagePage3.render(additionalApplicants, "", "",
						"", "", "", "", "", ""));
		} catch (Exception e) {
			Logger.error("Error in mortgagePage2Pur " + e);
			return ok("Some thing went wrong in mortgagePage2 purchage ");
		}
	}

	public static Result mortgagePage2Ref() {
		CouchBaseOperation storeData = new CouchBaseOperation();

		Logger.info("Inside mortgagePage2Ref Refinance Selected.");
		DynamicForm dynamicForm = form().bindFromRequest();
		CreateApplicant oppertunityUpdate = null;
		try {
			Session session = Http.Context.current().session();

			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
					+ "");

			oppertunityUpdate = new CreateApplicant();
			String propertyaddress1 = dynamicForm.get("formatted_address");
			String refinancing1 = dynamicForm.get("refivalue");
			String refiAdditionalAmount = dynamicForm.get("additionalFunds");
			String additionalApplicants = dynamicForm
					.get("additionalApplicants");

			String buyProperty = dynamicForm.get("buyProperty");
			String payOffDebt = dynamicForm.get("payOffDebt");
			String buyInvestment = dynamicForm.get("buyInvestment");
			String buyVehicle = dynamicForm.get("buyVehicle");
			String renovate = dynamicForm.get("renovate");
			String refurnish = dynamicForm.get("refurnish");
			String vacation = dynamicForm.get("vacation");
			String recVehicle = dynamicForm.get("recVehicle");
			String other = dynamicForm.get("other");

			String applicantID = (String) session.get("applicantID");
			String applicantID2 = null;

			if (additionalApplicants.equalsIgnoreCase("yes")) {
				applicantID2 = (String) session.get("applicantID2");
			}
			Set<String> generaldescription1 = new HashSet<String>();
			if (buyProperty.equalsIgnoreCase("true")) {
				buyProperty = "Buy Property";
				generaldescription1.add(buyProperty);

			}
			if (payOffDebt.equalsIgnoreCase("true")) {
				payOffDebt = "Pay Off Debt";
				generaldescription1.add(payOffDebt);
			}
			if (buyInvestment.equalsIgnoreCase("true")) {
				buyInvestment = "Buy Investment";
				generaldescription1.add(buyInvestment);

			}
			if (buyVehicle.equalsIgnoreCase("true")) {
				buyVehicle = "Buy Vehicle";
				generaldescription1.add(buyVehicle);
				opportunity.setBuy_new_vehicle(buyVehicle);

			}
			if (renovate.equalsIgnoreCase("true")) {
				renovate = "Renovate";
				generaldescription1.add(renovate);
			}

			if (refurnish.equalsIgnoreCase("true")) {
				refurnish = "Refurnish";
				generaldescription1.add(refurnish);
			}
			if (vacation.equalsIgnoreCase("true")) {
				vacation = "Vacation";
				generaldescription1.add(vacation);
			}
			if (recVehicle.equalsIgnoreCase("true")) {
				recVehicle = "Rec Vehicle";
				generaldescription1.add(recVehicle);
			}
			if (other.equalsIgnoreCase("true")) {
				other = "Other";
				generaldescription1.add(other);
			}

			int liveDetails = 0;
			Double monthlyRentalIncome = 0.0;
			String living4Financing = dynamicForm.get("living4Financing");
			if (living4Financing.equalsIgnoreCase("Renter")
					&& living4Financing != null) {
				String rentalAmount = dynamicForm.get("rentalAmount");
				liveDetails = 2;
				monthlyRentalIncome = Double.parseDouble(rentalAmount);
				opportunity.setMonthlyRentalIncome(monthlyRentalIncome);
				Logger.debug("Renter");
			} else if (living4Financing.equalsIgnoreCase("OwnerAndRenter")
					&& living4Financing != null) {

				String rentalAmount = dynamicForm.get("rentalAmount");

				liveDetails = 3;
				monthlyRentalIncome = Double.parseDouble(rentalAmount);
				opportunity.setMonthlyRentalIncome(monthlyRentalIncome);

				Logger.debug("Owner and Renter");
			} else if (living4Financing.equalsIgnoreCase("Owner (Myself)")
					&& living4Financing != null) {
				liveDetails = 1;
				Logger.debug("Owner");
			}

			double refinancing_Property_Value = 0.0;

			double addtional_amount = 0.0;
			try {
				addtional_amount = Double.parseDouble(refiAdditionalAmount);
			} catch (Exception e) {
				Logger.error("Error in parsing opportunityReaddtional amount "
						+ e);
			}

			try {
				refinancing_Property_Value = Double.parseDouble(refinancing1);
			} catch (Exception e) {
				Logger.error("Error in parsing opportunityReaddtional amount "
						+ e);
			}

			@SuppressWarnings("unchecked")
			HashMap<String, String> addressMap = new Address()
					.getProperAddressTwo(propertyaddress1);

			opportunity.setAddress(addressMap.get("address1"));
			opportunity.setCity(addressMap.get("city"));
			opportunity.setProvince(addressMap.get("Province"));
			opportunity.setPostalCode(addressMap.get("postalcode"));
			opportunity.setProperty_value(refinancing_Property_Value);
			opportunity.setAdditional_amount(addtional_amount);
			opportunity.setLivingInProperty(living4Financing);
			opportunity.setPogressStatus(20);

			int applicantIDInt = 0;
			int applicantID2Int = 0;
			try {

				Logger.debug("Lead id >>>>" + crm_LeadId + "<<<<<");
				applicantIDInt = Integer.parseInt(applicantID);
				Logger.debug("applicantIDInt one>>>>" + applicantIDInt
						+ "<<<<<");
				if (applicantID2 != null)
					applicantID2Int = Integer.parseInt(applicantID2);
				Logger.debug("applicantIDInt two >>>>" + applicantID2Int
						+ "<<<<<");
				Logger.debug("leadId " + crm_LeadId);
			} catch (Exception e) {
				Logger.error("Some value not found from session :", e);
			}

			oppertunityUpdate.assignOppertunityRefinance(applicantIDInt,
					crm_LeadId, "3", propertyaddress1, liveDetails + "",
					monthlyRentalIncome + "", refiAdditionalAmount + "",
					refinancing1);

			if (applicantID2Int != 0) {
				oppertunityUpdate.assignOppertunityRefinance(applicantIDInt,
						crm_LeadId, "3", propertyaddress1, liveDetails + "",
						monthlyRentalIncome + "", refiAdditionalAmount + "",
						refinancing1);
			}
			// String in couchBase
			String formType = "Mortgage Application";

			Logger.debug("applicantID " + applicantID);
			storeData.storeDataInCouchBase(formType, opportunity);
			Logger.debug("additionalApplicants " + additionalApplicants
					+ "\n formattedAddress  " + propertyaddress1
					+ "\n refivalue  :" + refinancing1 + "\n additionalFunds :"
					+ refiAdditionalAmount + "\n buyProperty: " + buyProperty
					+ "\nbuyInvestment  :" + buyInvestment + "\n buyVehicle: "
					+ buyVehicle + "\n renovate :" + renovate
					+ "\n refurnish :" + refurnish + "\nvacation :" + vacation
					+ "\n recVehicle: " + recVehicle + "\n other " + other
					+ "\n living4Financing  " + living4Financing
					+ "\n selected check box value " + generaldescription1);
			// String referralId=request().getQueryString("referralId");
			return ok(mortgagePage3.render(additionalApplicants, "", "", "",
					"", "", "", "", ""));
		} catch (Exception e) {

			Logger.error("error in the mortgagePage2Pre method" + e);
			return ok("Error in");
		}
	}

	public static Result mortgagePage3() {

		Logger.info("Inside mortgagePage3");
		DynamicForm dynamicForm = form().bindFromRequest();
		try {

			String formType = "Mortgage Application";
			String subForm = "Mortgage Application 3";

			CouchBaseOperation couchBaseOperation = new CouchBaseOperation();

			String applicantId = "";
			Session session = Http.Context.current().session();

			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = couchBaseOperation
					.getOpporunityData(crm_LeadId + "");

			try {

				applicantId = (String) session.get("applicantID");
			} catch (Exception e) {
				Logger.error("Error in gettin session applicant Value" + e);
			}
			// get ip of latest form sumitted

			String typeofbuilding = dynamicForm.get("buildingType");
			String propertystyle = dynamicForm.get("propertyStyle");

			String propertyheated = dynamicForm.get("propertyHeat");
			String getwater = dynamicForm.get("propertyGetWater");
			String propertydispose = dynamicForm.get("propertyDisposeWater");
			String garagetype = dynamicForm.get("typeGarage");
			String sqaurefootage = dynamicForm.get("squareFt");
			String garabageSize = dynamicForm.get("garageSize");

			Logger.debug(" garabage size " + garabageSize);
			Logger.debug("Garagetype:" + garagetype);
			int squreFootAgeInt = 0;
			try {
				squreFootAgeInt = Integer.parseInt(sqaurefootage);
			} catch (Exception e) {
				Logger.error("error in parsing squre footageValue  to integer"
						+ sqaurefootage);
			}
			opportunity.setHeating(propertyheated);
			opportunity.setWater(getwater);
			opportunity.setPropertyType(typeofbuilding);
			opportunity.setPropertyStyle(propertystyle);
			opportunity.setGarageType(garagetype);
			opportunity.setGarageSize(garabageSize);
			opportunity.setSquareFootage(squreFootAgeInt);
			opportunity.setPogressStatus(30);

			CreateApplicant createApplicant = new CreateApplicant();

			createApplicant.updateOpportunity(crm_LeadId, propertystyle,
					typeofbuilding, propertyheated, getwater, propertydispose,
					garagetype, garabageSize, sqaurefootage);

			Logger.debug("Propertystyle:" + propertystyle
					+ "\n Typeofbuilding:" + typeofbuilding
					+ "\n Propertyheated:" + propertyheated + "\n Getwater:"
					+ getwater + "\n Propertydispose:" + propertydispose
					+ "\n Squarefootage:" + sqaurefootage);

			// Storing to couchBase
			Logger.debug("inside Couchbase operation");

			couchBaseOperation.storeDataInCouchBase(formType, opportunity);

			Logger.debug(" Couchbase operation sucessfully");

			String additionalApplicants = dynamicForm
					.get("additionalApplicants");
			Logger.debug("additionalApplicants  " + additionalApplicants);
			Logger.debug("term **********************:"
					+ dynamicForm.get("term"));
			return ok(mortgagePage4
					.render(additionalApplicants, "", "", "", ""));
		} catch (Exception e) {
			Logger.error("Error in mortgagePage3 " + e);
			return ok("should be render MortgageApplicationSucess.jsp");
		}
	}

	public static Result mortgagePage4() {
		Logger.info("Inside mortgagePage4");
		DynamicForm dynamicForm = form().bindFromRequest();
		try {
			CouchBaseOperation storeData = new CouchBaseOperation();

			String additionalApplicants = "yes";
			String formType = "Mortgage Application";
			String subForm = "Mortgage Application 4";
			CreateApplicant oppertunityUpdate = null;
			oppertunityUpdate = new CreateApplicant();
			Session session = Http.Context.current().session();

			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
					+ "");
		
			String applicantID = "";
			String co_ApplicantID = "";

			try {
				applicantID = (String) session.get("applicantID");

				co_ApplicantID = session.get("applicantID2");
			} catch (Exception e) {
				Logger.error("Error in getting values from session " + e);
			}

			int applicantIDInt = 0;
			try {
				applicantIDInt = Integer.parseInt(applicantID);

			} catch (Exception e) {
				Logger.error("Error in Reading data from  session", e);
			}

			String mortgageinmind1 = dynamicForm.get("mortgageInMind");
			Logger.debug("Mortgage in mind " + mortgageinmind1);
			
			int desiredType = 0;
			opportunity.setDesiredMortgageType(mortgageinmind1);

			if (mortgageinmind1.equalsIgnoreCase("Variable")) {
				desiredType = 2;

			} else if (mortgageinmind1.equalsIgnoreCase("Fixed")) {
				desiredType = 1;

			} else if (mortgageinmind1.equalsIgnoreCase("Line of Credit")) {
				desiredType = 0;

			} else if (mortgageinmind1.equalsIgnoreCase("Cashback")) {
				desiredType = 3;

			} else if (mortgageinmind1.equalsIgnoreCase("Combination")) {
				desiredType = 5;

			} else if (mortgageinmind1.equalsIgnoreCase("Best Option")) {
				desiredType = 4;

			}

			String currentMortgageTerm = dynamicForm.get("mortgageTerm");
		
			opportunity.setTerm(currentMortgageTerm);
			int mortgagemind = 0;
			if (currentMortgageTerm.equalsIgnoreCase("6 Month")) {
				mortgagemind = 2;

			} else if (currentMortgageTerm.equalsIgnoreCase("1 Year")) {
				mortgagemind = 3;

			} else if (currentMortgageTerm.equalsIgnoreCase("2 Year")) {
				mortgagemind = 4;

			} else if (currentMortgageTerm.equalsIgnoreCase("3 Year")) {
				mortgagemind = 5;

			} else if (currentMortgageTerm.equalsIgnoreCase("4 Year")) {
				mortgagemind = 6;

			} else if (currentMortgageTerm.equalsIgnoreCase("5 Year")) {
				mortgagemind = 7;

			} else if (currentMortgageTerm.equalsIgnoreCase("7 Year")) {
				mortgagemind = 8;

			} else if (currentMortgageTerm.equalsIgnoreCase("10 Year")) {
				mortgagemind = 9;

			} else if (currentMortgageTerm.equalsIgnoreCase("None")) {
				mortgagemind = 1;

			}

			String lookingfor11 = dynamicForm.get("lookingForAmortize");
			String likeToAmortize = "";
			Logger.debug("Looking for Ammortize how many years " + lookingfor11);
			int desiredMortgitionValue = 0;

			if (lookingfor11.equalsIgnoreCase("10 Year"))
				desiredMortgitionValue = 10;
			else if (lookingfor11.equalsIgnoreCase("15 Year"))
				desiredMortgitionValue = 15;
			else if (lookingfor11.equalsIgnoreCase("20 Year"))
				desiredMortgitionValue = 20;
			else if (lookingfor11.equalsIgnoreCase("25 Year"))
				desiredMortgitionValue = 25;
			else if (lookingfor11.equalsIgnoreCase("30 Year"))
				desiredMortgitionValue = 30;
			else if (lookingfor11.equalsIgnoreCase("Other")) {
				likeToAmortize = dynamicForm.get("amortizeYear");
				try {
					desiredMortgitionValue = Integer.parseInt(likeToAmortize);
				} catch (Exception e) {
					Logger.error("Error in parsing String to number :" + e);
				}
			}
			opportunity.setAmortization(Double.parseDouble(desiredMortgitionValue+""));
			opportunity.setPogressStatus(35);
			String lendingGoal = dynamicForm.get("leadingGoal");
			Logger.debug("Lending Goal " + lendingGoal);
			String lendingId = "";
			if (lendingGoal != null && !lendingGoal.isEmpty()) {
				if (lendingGoal.equalsIgnoreCase("PreApproval"))
					lendingId = "1";
				else if (lendingGoal.equalsIgnoreCase("Purchase"))
					lendingId = "2";
				else if (lendingGoal.equalsIgnoreCase("Refinance"))
					lendingId = "3";
			}
			Logger.debug("Applicant id " + applicantID + "\n Lead id :  "
					+ crm_LeadId + "\n Lending goal in number " + lendingId
					+ "\n desiredType " + desiredType + "\n mortgage mind "
					+ mortgagemind + "\n desiredMortgitionValue "
					+ desiredMortgitionValue);

			oppertunityUpdate.assignOppertunityPurchase(applicantID, crm_LeadId,
					lendingId, desiredType + "", mortgagemind + "",
					desiredMortgitionValue);
			storeData.storeDataInCouchBase(formType, opportunity);

			return ok(mortgagePage5a.render("", "", "", "", "", ""));
		} catch (Exception e) {
			Logger.error("Error in mortgagePage4 ", e);
			return ok("Failure in mortgagePager");
		}
	}

	public static Result mortgagePage5a() {
		Logger.info(" Inside mortgagePage5a ");
		DynamicForm dynamicForm = form().bindFromRequest();
		CouchBaseOperation storeData = new CouchBaseOperation();
String formType="Mortgage5a"	;		


		try {

			Session session = Http.Context.current().session();

			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
					+ "");
			
			CreateApplicant oppertunityUpdate = new CreateApplicant();
			String incomedown1 = dynamicForm.get("mybills");
			int incomeDcreased = 0;
			if (incomedown1.equalsIgnoreCase("Yes") && incomedown1 != null) {
				incomeDcreased = 5;
				Logger.debug("updating Oppertunity");
				opportunity.setIncomeDecreasedWorried(incomedown1);

			} else if (incomedown1.equalsIgnoreCase("No")
					&& incomedown1 != null) {
				Logger.debug("inside incomedown1 NO");
				incomeDcreased = 1;
				opportunity.setIncomeDecreasedWorried(incomedown1);


			} else if (incomedown1.equalsIgnoreCase("Maybe")
					&& incomedown1 != null) {
				Logger.debug("inside incomedown1 Maybe");
				incomeDcreased = 3;
				Logger.debug("updating Oppertunity");
				opportunity.setIncomeDecreasedWorried(incomedown1);

			}
			String largerfamily1 = dynamicForm.get("largerFamily");
			Logger.debug("largerfamily1" + largerfamily1);

			int futurfamilly = 0;
			if (largerfamily1.equalsIgnoreCase("Yes") && largerfamily1 != null) {
				Logger.debug("inside largerfamily1 Yes");

				futurfamilly = 5;
				opportunity.setFuture_family(largerfamily1);
				Logger.debug("updating Oppertunity");

			} else if (largerfamily1.equalsIgnoreCase("No")
					&& largerfamily1 != null) {
				Logger.debug("inside largerfamily1 NO");

				futurfamilly = 1;

				Logger.debug("updating Oppertunity");
				opportunity.setFuture_family(largerfamily1);

			} else if (largerfamily1.equalsIgnoreCase("Maybe")
					&& largerfamily1 != null) {
				Logger.debug("inside largerfamily1 Maybe");
				futurfamilly = 3;
				opportunity.setFuture_family(largerfamily1);

				Logger.debug("updating Oppertunity");
			}

			String buyingnewvechile1 = dynamicForm.get("buyNewVehicle");
				int buyNewVehical = 0;
			if (buyingnewvechile1.equalsIgnoreCase("Yes")
					&& buyingnewvechile1 != null) {
				Logger.debug("inside buyingnewvechile1 Yes");

				buyNewVehical = 5;
				opportunity.setBuy_new_vehicle(buyingnewvechile1);

				Logger.debug("updating Oppertunity");

			} else if (buyingnewvechile1.equalsIgnoreCase("No")
					&& buyingnewvechile1 != null) {
				Logger.debug("inside buyingnewvechile1 NO");
				buyNewVehical = 1;
				opportunity.setBuy_new_vehicle(buyingnewvechile1);

				Logger.debug("updating Oppertunity");

			} else if (buyingnewvechile1.equalsIgnoreCase("Maybe")
					&& buyingnewvechile1 != null) {
				Logger.debug("inside buyingnewvechile1 Maybe");
				buyNewVehical = 3;
				opportunity.setBuy_new_vehicle(buyingnewvechile1);

				Logger.debug("updating Oppertunity");
			}

			String Planninglifestyle1 = dynamicForm.get("recreatStoreHome");
		
			int lifeStyleChnage = 0;
			if (Planninglifestyle1.equalsIgnoreCase("Yes")
					&& Planninglifestyle1 != null) {
				Logger.debug("inside Planninglifestyle1 Yes");

				lifeStyleChnage = 5;
				opportunity.setLifestyleChange(Planninglifestyle1);

				Logger.debug("updating Oppertunity");

			} else if (Planninglifestyle1.equalsIgnoreCase("No")
					&& Planninglifestyle1 != null) {
				Logger.debug("inside Planninglifestyle1 NO");

				lifeStyleChnage = 1;
				opportunity.setLifestyleChange(Planninglifestyle1);

				Logger.debug("updating Oppertunity");

			} else if (Planninglifestyle1.equalsIgnoreCase("Maybe")
					&& Planninglifestyle1 != null) {

				lifeStyleChnage = 3;
				opportunity.setLifestyleChange(Planninglifestyle1);

				Logger.debug("updating Oppertunity");
			}
			String applicantID = "";
			String co_ApplicantID = "";

			String additionalApplicants = "";
			Logger.debug("additionalApplicants  " + additionalApplicants);

			int leadId = 0;

			try {
				applicantID = (String) session.get("applicantID");
				additionalApplicants = (String) session
						.get("additionalApplicants");

			} catch (Exception e) {
				Logger.error("Error in getting values from session " + e);
			}

			Logger.debug("insisde mortgagePage5a  leadIdfrom session = "+ leadId + " applicantID from session =" + applicantID);
			String subForm = "Mortgage Application 5a";
			opportunity.setPogressStatus(40);
			int financialRisktaker = 0;
			String financialrisk1 = dynamicForm.get("riskTaker");
			if (financialrisk1.equalsIgnoreCase("Yes")
					&& financialrisk1 != null) {

				financialRisktaker = 5;
				opportunity.setFinancial_risk_taker(financialrisk1);
				Logger.debug("updating Oppertunity");

			} else if (financialrisk1.equalsIgnoreCase("No")
					&& Planninglifestyle1 != null) {

				financialRisktaker = 1;
				opportunity.setFinancial_risk_taker(financialrisk1);

				Logger.debug("updating Oppertunity");

			} else if (financialrisk1.equalsIgnoreCase("Maybe")
					&& financialrisk1 != null) {
				financialRisktaker = 3;
				opportunity.setFinancial_risk_taker(financialrisk1);

				Logger.debug("updating Oppertunity");
			}

			int applicantIDInt = 0;
			try {
				applicantIDInt = Integer.parseInt(applicantID);

			} catch (Exception e) {
				Logger.error("Applicant id comming in wrong format ", e);
			}

			oppertunityUpdate.assignOppertunity(applicantIDInt, incomeDcreased,
					futurfamilly, buyNewVehical, lifeStyleChnage,
					financialRisktaker, leadId);
			storeData.storeDataInCouchBase(formType, opportunity);
			return ok(mortgagePage5b.render(additionalApplicants, "", "", "",
					""));
		} catch (Exception e) {
			Logger.error("Error in mortgage5a method." + e);
			return ok("some thing went wrong in method 5a ");
		}
	}

	public static Result mortgagePage5b() {
		Logger.info("Inside method mortgagePage5b ");
		try {
			DynamicForm dynamicForm = form().bindFromRequest();
			CreateApplicant oppertunityUpdate = new CreateApplicant();
			CouchBaseOperation storeData = new CouchBaseOperation();
			String formType="Mortgage 5b";
			Session session = Http.Context.current().session();

			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
					+ "");
			
			
			
			String thinkproperty1 = dynamicForm.get("likelyProperty");
			int proprtyLessThaniveyears = 0;
			if (thinkproperty1.equalsIgnoreCase("Yes")
					&& thinkproperty1 != null) {
				proprtyLessThaniveyears = 5;
				opportunity.setPropertyLessThen5Years(thinkproperty1);
				Logger.debug("updating Oppertunity");

			} else if (thinkproperty1.equalsIgnoreCase("No")
					&& thinkproperty1 != null) {

				proprtyLessThaniveyears = 1;
				opportunity.setPropertyLessThen5Years(thinkproperty1);

				Logger.debug("updating Oppertunity");

			} else if (thinkproperty1.equalsIgnoreCase("Maybe")
					&& thinkproperty1 != null) {

				proprtyLessThaniveyears = 3;
				opportunity.setPropertyLessThen5Years(thinkproperty1);

				Logger.debug("updating Oppertunity");
			}

			String imaginesamejob1 = dynamicForm.get("sameJob");
			int jobForFiveYears = 0;
			if (imaginesamejob1.equalsIgnoreCase("Yes")
					&& imaginesamejob1 != null) {
				Logger.debug("inside imaginesamejob1 YES");
				jobForFiveYears = 5;
				opportunity.setJob5Years(imaginesamejob1);

				Logger.debug("updating Oppertunity");

			} else if (imaginesamejob1.equalsIgnoreCase("No")
					&& imaginesamejob1 != null) {
				Logger.debug("inside imaginesamejob1 NO");
				jobForFiveYears = 1;
				opportunity.setJob5Years(imaginesamejob1);

				Logger.debug("updating Oppertunity");

			} else if (imaginesamejob1.equalsIgnoreCase("Maybe")
					&& imaginesamejob1 != null) {
				Logger.debug("inside imaginesamejob1 Maybe");
				jobForFiveYears = 3;
				opportunity.setJob5Years(imaginesamejob1);

				Logger.debug("updating Oppertunity");
			}
			String applicantID = "";
			int applicantIDInt = 0;

			String additionalApplicants = "";
			String isMobileValue = "";
			try {
				applicantID = (String) session.get("applicantID");

				isMobileValue = session.get("isMobile");
				additionalApplicants = session.get("additionalApplicants");
				Logger.debug("additionalApplicants " + additionalApplicants);

			} catch (Exception e) {
				Logger.error("Error in getting values from session " + e);
			}

			try {
				applicantIDInt = Integer.parseInt(applicantID);
			} catch (Exception e) {
				Logger.error("Error reading from session ", e);
			}

			String incomeraise1 = dynamicForm.get("incomeGoing");
			String rentalproperty1 = dynamicForm.get("OneMoreProp");
		
			opportunity.setPogressStatus(45);
			oppertunityUpdate.assignOppertunity(applicantIDInt,
					jobForFiveYears, proprtyLessThaniveyears, crm_LeadId);
			storeData.storeDataInCouchBase(formType, opportunity);
			String applicantName = session.get("applicantFirstName");
			String coApplicantName = session.get("co_applicantFirstName");
			PersonalInfo personalInfo = new PersonalInfo();
			personalInfo.setAdditionalApplicant(additionalApplicants);
			personalInfo.setApplicantName(applicantName);
			personalInfo.setCoApplicantName(coApplicantName);
			boolean isMobile = false;

			if (isMobileValue != null && !isMobileValue.isEmpty()) {
				if (isMobileValue.equalsIgnoreCase("isMobile"))
					isMobile = true;
			}
			if (!isMobile)
				return ok(mortgagePage6.render(additionalApplicants,
						applicantName, coApplicantName, "", "", "", "", "", "",
						"", "", "", "", "", "", "", "", "", "", "", ""));
			else
				return ok(mortgagePage6a.render(personalInfo));
		} catch (Exception e) {
			Logger.error("Error in mortgagePage5b" + e);
			return ok("Some thing went wrong in method mortgagePage5b  ");
		}

	}

	public static Result mortgagePage6() {
		Logger.info(" Inside mortgagePage6 ");
		try {
			DynamicForm dynamicForm = form().bindFromRequest();
		
			CouchBaseOperation storeData = new CouchBaseOperation();

			Session session = Http.Context.current().session();
			String leadingGoal = (String) session.get("leadingGoal");
			String applicantID = "";
			String additionalApplicants = "";
			int crm_LeadId = 0;
			Applicant applicant=null;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
					+ "");
			
			try {
				applicantID = (String) session.get("applicantID");
				additionalApplicants = session.get("additionalApplicants");
			} catch (Exception e) {
				Logger.error("Error in reading data from session ", e);
			}

			Logger.debug("additionalApplicants>>>>>>>>>>>>>>>>>>>>>>"
					+ additionalApplicants);
			Logger.debug("applicantID " + applicantID);
			String formType = "Mortgage Application";
			String subForm = "Mortgage Application 6";
			Logger.debug("leadingGoal " + leadingGoal);
			// get ip of latest form sumitted
			String ip = request().remoteAddress();

			String mobilePhone = dynamicForm.get("applMobPhone");
			String workPhone = dynamicForm.get("applWorkPhone");
			String homePhone = dynamicForm.get("applHomePhone");
			String inputBirthDay = dynamicForm.get("applBirthday");
			String insurance = dynamicForm.get("applInsurNum");
			String relationshipStatus = dynamicForm.get("appRelStatus");
			String dependant = dynamicForm.get("applDependants");
			String refareYouCanada = dynamicForm.get("areYouCanadianRess");
			String movedCanadas = dynamicForm.get("movedCanadas");

			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			Date birthday = null;
			try {
				birthday = df.parse(inputBirthDay);
			} catch (ParseException e) {
				Logger.error("Error in parsing string to date");
			}
			Logger.debug("birthday>>>>>>>>>>>>>>>>>>" + birthday
					+ "<<<<<<<<<<<<<<<<<");

			Logger.debug("mobilePhone " + mobilePhone + "\n workPhone"
					+ workPhone + "\n homePhone " + homePhone + "\n birthday"
					+ birthday + "\n insurance" + insurance
					+ "\n relationshipStatus" + relationshipStatus
					+ "\n dependant" + dependant + "\n refareYouCanada "
					+ refareYouCanada + "\n movedCanadas " + movedCanadas);

				CreateApplicant updateApplicant = new CreateApplicant();
			boolean non_ResidentBool;
			if (refareYouCanada.equalsIgnoreCase("Yes")
					&& refareYouCanada != null) {
				non_ResidentBool = true;
			} else {
				non_ResidentBool = false;
			}

			if (relationshipStatus.equalsIgnoreCase("Common-Law")
					&& relationshipStatus != null) {
				relationshipStatus = "Common_Law";
			}
			applicant=opportunity.getApplicants().get(0);
			applicant.setWorkPhone(workPhone);
			applicant.setHomePhone(homePhone);
			applicant.setCell(mobilePhone);
			applicant.setDob(birthday);
			applicant.setRelationship_status(relationshipStatus);
			applicant.setMovedToCannada(movedCanadas);
			applicant.setInsurenceNumber(insurance);
		
			applicant.setNon_resident(non_ResidentBool);

			// FOR ADDITIONAL APPLICANT
			if (additionalApplicants.equalsIgnoreCase("yes")
					&& additionalApplicants != null) {
				String CoMobilePhone = dynamicForm.get("coApplMobPhone");
				String CoAppWorkPhone = dynamicForm.get("coApplWorkPhone");
				String CoAppHomePhone = dynamicForm.get("coApplHomePhone");
				String inputCoAppBirthday = dynamicForm.get("coApplBirthday");
				String CoAppinsurance = dynamicForm.get("coApplInsurNum");
				String CoApprelationshipStatus = dynamicForm
						.get("coAppRelStatus");
				String CoAppdependant = dynamicForm.get("coAppDependants");
				String CoApprefareYouCanada = dynamicForm.get("coApplicantss");
				String CoAppmovedCanadas = dynamicForm.get("coAppMovedCanadae");
				DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
				Date CoAppBirthday = null;
				try {
					CoAppBirthday = df2.parse(inputCoAppBirthday);
				} catch (ParseException e) {
					Logger.error("Error in parsing string to date");
				}
				Logger.debug("CoAppBirthday >>>>>>>>>>>>>>>>>" + CoAppBirthday
						+ "<<<<<<<<<<<<<<");
				Logger.debug("CoMobilePhone " + CoMobilePhone
						+ "\n CoAppWorkPhone " + CoAppWorkPhone
						+ "\n CoAppHomePhone " + CoAppHomePhone
						+ "\n CoAppBirthday " + CoAppBirthday
						+ "\n CoAppinsurance " + CoAppinsurance
						+ "\n CoApprelationshipStatus "
						+ CoApprelationshipStatus + "\n CoAppdependant "
						+ CoAppdependant + "\n CoApprefareYouCanada "
						+ CoApprefareYouCanada + "\n CoAppmovedCanadas "
						+ CoAppmovedCanadas);

				boolean Co_non_ResidentBool;
				if (CoApprefareYouCanada.equalsIgnoreCase("Yes")) {
					Co_non_ResidentBool = true;
				} else {
					Co_non_ResidentBool = false;
				}

				if (CoApprelationshipStatus != null
						&& CoApprelationshipStatus
								.equalsIgnoreCase("Common-Law")) {
					CoApprelationshipStatus = "Common_Law";
				}
				
				applicant=opportunity.getApplicants().get(1);
				applicant.setWorkPhone(CoAppWorkPhone);
				applicant.setHomePhone(CoAppHomePhone);
				applicant.setCell(CoMobilePhone);
				applicant.setDob(CoAppBirthday);
				applicant.setRelationship_status(CoApprelationshipStatus);
				applicant.setMovedToCannada(CoAppmovedCanadas);
				applicant.setInsurenceNumber(CoAppinsurance);
			
				applicant.setNon_resident(non_ResidentBool);

				String applicantID2 = "";
				try {
					applicantID2 = (String) session.get("applicantID2");
					Logger.debug("applicantID2 " + applicantID2);
				} catch (Exception e) {
					Logger.error("Error reading data from session " + e);
				}
				
				updateApplicant.updateApplicant(applicantID2, CoMobilePhone,
						CoAppWorkPhone, CoAppHomePhone, CoAppinsurance,
						CoApprelationshipStatus, Co_non_ResidentBool,
						CoAppBirthday);
			}
			
			opportunity.setPogressStatus(55);
			updateApplicant.updateApplicant(applicantID, mobilePhone,
					workPhone, homePhone, insurance, relationshipStatus,
					non_ResidentBool, birthday);
			Logger.debug("Record is updated in openERP applicant module");
			storeData.storeDataInCouchBase(formType, opportunity);
			Logger.debug("Record is updated in couchbase.");
			ApplicantAddressParameter7 appAddressObj = new ApplicantAddressParameter7();
			CoApplicantAddressParameter7 coAppAddressObj = new CoApplicantAddressParameter7();
			String applicantName = session.get("applicantFirstName");
			String coApplicantName = session.get("co_applicantFirstName");
			return ok(mortgagePage7Address.render(additionalApplicants,
					applicantName, coApplicantName, appAddressObj,
					coAppAddressObj));
			// return ok(mortgagePage7Address.render(""));
		} catch (Exception e) {
			Logger.error("Error in mortgagePage6", e);
			return ok("Some thing went wrong in mortgagePage6 ");
		}
	}

	public static Result mortgagePage7() {
		Logger.info("Inside mortgagePage7");
		try {
			DynamicForm dynamicForm = form().bindFromRequest();
			CouchBaseOperation storeData = new CouchBaseOperation();

			String additionalApplicants = dynamicForm
					.get("additionalApplicants");
			Logger.info("additionalApplicants   " + additionalApplicants);
			Session session = Http.Context.current().session();
			
			int crm_LeadId = 0;
			Applicant applicant=null;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
					+ "");
			String applicantID = "";
			try {
				applicantID = (String) session.get("applicantID");

			} catch (Exception e) {
				Logger.error("Error in  reading data from session " + e);
			}

			
			applicant=opportunity.getApplicants().get(0);
			controllers.Address address=null;
			AddressGroup currentaddressObj = null;
			AddressGroup prioraddress1Obj = null;
			AddressGroup prioraddress2Obj = null;
			Address addressSplit = new Address();
			String subForm = "Mortgage Application 7";

			CreateApplicant cLead;
			ArrayList<AddressGroup> listOfAddresses = new ArrayList<AddressGroup>();
			String currentAddress = dynamicForm.get("currentAddress1");
			String inputMovedIn1 = dynamicForm.get("movedIn1");

			String currentSumMonth = dynamicForm.get("currentaddressmonthsum");
			String totalcurrentMonths = dynamicForm.get("totalcurrentmonths");

			DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
			Date movedIn1 = null;
			try {
				movedIn1 = df2.parse(inputMovedIn1);
			} catch (ParseException e) {
				Logger.error("Error in parsing string to date");
			}
			Logger.debug("birthday>>>>>>>>>>>>>>>>>>" + movedIn1
					+ "<<<<<<<<<<<<<<<<<");

			int totalcurrentMonthsInt = 0;
			Logger.debug("*********inside  currentAddress************ ");
			Logger.debug("currentAddress" + currentAddress
					+ "\n input MovedIn1 " + inputMovedIn1
					+ "\n After string-to-date movedIn1 " + movedIn1
					+ "\n currentSumMonth" + currentSumMonth
					+ "\n totalcurrentMonths" + totalcurrentMonths);

			int totalSumMonths = 0;
			try {
				totalSumMonths = Integer.parseInt(totalcurrentMonths);
			} catch (Exception e) {
				Logger.error("Error in parsing totalcurrentMontsh " + e);

			}

			if (currentAddress != null) {
				currentaddressObj = new AddressGroup(currentAddress,
						currentSumMonth, totalcurrentMonths);
				listOfAddresses.add(currentaddressObj);
				HashMap currentAddressSplit = addressSplit
						.getProperAddress(currentAddress);

				String name = null;
				String city = null;
				String province = null;
				String postalcode = null;
				if (currentAddressSplit != null) {
					name = (String) currentAddressSplit.get("address1");
					city = (String) currentAddressSplit.get("city");
					province = (String) currentAddressSplit.get("Province");
					postalcode = (String) currentAddressSplit.get("postalcode");
					// creating Applicant
					cLead = new CreateApplicant();

					cLead.createApplicantAddress(applicantID, name, city,
							province, postalcode, movedIn1);
					Logger.debug("Applicant  created with currentAddressSplit ");
				}
				
			address=new controllers.Address();
				address.setName(name);
				address.setCity(city);
				address.setPostalCode(postalcode);
				address.setMovedIn(movedIn1);
				address.setProvience(province);
				applicant.getListOfAddress().add(address);
			}

			if ((totalcurrentMonths != null && totalcurrentMonths.length() != 0 && !totalcurrentMonths
					.equals("")) && totalcurrentMonthsInt <= 36) {

				Logger.debug("**********inside 1st priorAddress1 *******************");

				String priorAddress1 = dynamicForm.get("currentAddress2");
				String inputMovedIn2 = dynamicForm.get("movedIn2");

				String priorSumMonth1 = dynamicForm.get("priormonthsum1");
				String totalpriorcurrentmonths1 = dynamicForm
						.get("totalpriormonths1");
				int totalpriorcurrentmonths1Int = 0;
				try {
					totalpriorcurrentmonths1Int = Integer
							.parseInt(totalpriorcurrentmonths1);
				} catch (Exception e) {
					Logger.error(
							"exception in converting totalpriorcurrentmonths to int : ",
							e);
				}
				Date movedIn2 = null;
				try {
					movedIn2 = df2.parse(inputMovedIn2);
				} catch (ParseException e) {
					Logger.error("Error in parsing string to date");
				}

				Logger.debug("priorAddress1" + priorAddress1
						+ "\n inputMovedIn1 " + inputMovedIn1
						+ "\n after string-2-date movedIn2" + movedIn2
						+ "movedIn2>>>" + movedIn2 + "\n priorSumMonth1"
						+ priorSumMonth1 + "\n totalpriorcurrentmonths1"
						+ totalpriorcurrentmonths1 + "\n totalMonths:"
						+ totalpriorcurrentmonths1 + "\n TotalDate is:"
						+ totalpriorcurrentmonths1);

				if (priorAddress1 != null) {
					// currentaddressObj = new
					// AddressGroup(coAppcurrentAddress,currentYear,currentMonths,currentSumMonth,totalcurrentMonths);
					prioraddress1Obj = new AddressGroup(priorAddress1,
							priorSumMonth1, totalpriorcurrentmonths1);
					// listOfAddresses.add(currentaddressObj);
					listOfAddresses.add(prioraddress1Obj);
					HashMap currentAddressSplit = addressSplit
							.getProperAddress(priorAddress1);

					String name = null;
					String city = null;
					String province = null;
					String postalcode = null;
					if (currentAddressSplit != null) {
						name = (String) currentAddressSplit.get("address1");
						city = (String) currentAddressSplit.get("city");
						province = (String) currentAddressSplit.get("Province");
						postalcode = (String) currentAddressSplit
								.get("postalcode");
						// Creating Applicant
						cLead = new CreateApplicant();

						// Loggeric for date
						cLead.createApplicantAddress(applicantID, name, city,
								province, postalcode, movedIn2);

						
						
						address=new controllers.Address();
						address.setName(name);
						address.setCity(city);
						address.setPostalCode(postalcode);
						address.setMovedIn(movedIn2);
						address.setProvience(province);
						applicant.getListOfAddress().add(address);
						Logger.debug("Going to OpenERP Create Applicant address ");

						

					}// spliting address

				}// checking prior address is not null
				
				
				
				
				String totalpriorcurrentmonths2 = dynamicForm
						.get("totalpriormonths2");
				int totalpriorcurrentmonths2Int = 0;
				try {
					totalpriorcurrentmonths2Int = Integer
							.parseInt(totalpriorcurrentmonths2);
				} catch (Exception e) {
					Logger.error("Error when parsing totalpriorcurrentmonths from string to int"
							+ e);
				}
				if (totalpriorcurrentmonths2Int <= 36
						&& (totalpriorcurrentmonths2 != null && !totalpriorcurrentmonths2
								.equals(""))) {

					Logger.debug("taking 2nd prior address");
					String priorAddress2 = dynamicForm.get("currentAddress3");
					String inputMovedIn3 = dynamicForm.get("movedIn3");
					String priorSumMonth2 = dynamicForm.get("priormonthsum2");

					Date movedIn3 = null;
					try {
						movedIn3 = df2.parse(inputMovedIn3);
					} catch (ParseException e) {
						Logger.error("Error in parsing string to date");
					}
					Logger.debug("********** inside 1st priorAddress2 *******************");
					Logger.debug("priorAddress2" + priorAddress2
							+ "\n inputMovedIn3 " + inputMovedIn3
							+ "\n Affter string-2-date movedIn3 " + movedIn3
							+ "\n priorSumMonth2" + priorSumMonth2
							+ "\n totalpriorcurrentmonths2:"
							+ totalpriorcurrentmonths2
							+ "totalpriorcurrentmonths2 is:"
							+ totalpriorcurrentmonths2Int);

					Calendar prical2 = Calendar.getInstance();
					prical2.add(Calendar.MONTH, -totalpriorcurrentmonths2Int);
					String currentDateTimenew2 = (df2.format(prical2.getTime()));

					Logger.debug("CurrentDate for Prior Address 2 is:"
							+ currentDateTimenew2);

					if (priorAddress2 != null) {

						prioraddress2Obj = new AddressGroup(priorAddress2,
								priorSumMonth2, totalpriorcurrentmonths2);
						listOfAddresses.add(prioraddress2Obj);

						HashMap currentAddressSplit = addressSplit
								.getProperAddress(priorAddress2);

						String name = null;
						String city = null;
						String province = null;
						String postalcode = null;
						if (currentAddressSplit != null) {
							name = (String) currentAddressSplit.get("address1");
							city = (String) currentAddressSplit.get("city");
							province = (String) currentAddressSplit
									.get("Province");
							postalcode = (String) currentAddressSplit
									.get("postalcode");
							cLead = new CreateApplicant();

							cLead.createApplicantAddress(applicantID, name,
									city, province, postalcode, movedIn3);
							Logger.debug("Applicant created with priorAddress2 ");

							Logger.debug("CouchBase data is appending...");
							address=new controllers.Address();
							address.setName(name);
							address.setCity(city);
							address.setPostalCode(postalcode);
							address.setMovedIn(movedIn3);
							address.setProvience(province);
							applicant.getListOfAddress().add(address);
						}// spliting prior address 2
					}// checking prior address 2 is not null
				}// end of Loggeric of prior address 2

			}

			if (additionalApplicants != null
					&& additionalApplicants.equalsIgnoreCase("yes")) {
				
				
				applicant=opportunity.getApplicants().get(1);
				AddressGroup currentaddress4Obj = null;
				AddressGroup prioraddress5Obj = null;
				AddressGroup prioraddress6Obj = null;

				String applicantID2 = "";
				try {
					applicantID2 = (String) session.get("applicantID2");

				} catch (Exception e) {
					Logger.error("Error in  reading data from session " + e);
				}

				String coAppcurrentAddress = dynamicForm
						.get("CoCurrentAddress1");
				String coAppInputMovedIn1 = dynamicForm.get("CoMovedIn1");

				String coAppCurrentSumMonth = dynamicForm
						.get("coAppcurrentaddressmonthsum");
				String coAppTotalcurrentMonths = dynamicForm
						.get("coAppTotalcurrentMonths");

				df2 = new SimpleDateFormat("MM/dd/yyyy");
				Date coMovedIn1 = null;
				try {
					coMovedIn1 = df2.parse(coAppInputMovedIn1);
				} catch (ParseException e) {
					Logger.error("Error in parsing string to date");
				}
				Logger.debug("birthday>>>>>>>>>>>>>>>>>>" + coMovedIn1
						+ "<<<<<<<<<<<<<<<<<");

				int coApptotalcurrentMonthsInt = 0;
				Logger.debug("*********inside  coAppcurrentAddress************ ");
				Logger.debug("coAppcurrentAddress" + coAppcurrentAddress
						+ "\n input MovedIn1 " + coAppInputMovedIn1
						+ "\n After string-to-date movedIn1 " + coMovedIn1
						+ "\n coAppCurrentSumMonth" + coAppCurrentSumMonth
						+ "\n coAppTotalcurrentMonths"
						+ coAppTotalcurrentMonths);

				int coApptotalSumMonths = 0;
				try {
					coApptotalSumMonths = Integer
							.parseInt(coAppTotalcurrentMonths);
				} catch (Exception e) {
					Logger.error("Error in parsing string to int coAppTotalcurrentMonths"
							+ e);
				}

				if (coAppcurrentAddress != null) {

					currentaddress4Obj = new AddressGroup(coAppcurrentAddress,
							coAppCurrentSumMonth, coAppTotalcurrentMonths);
					listOfAddresses.add(currentaddress4Obj);
					HashMap currentAddressSplit = addressSplit
							.getProperAddress(coAppcurrentAddress);

					String name = null;
					String city = null;
					String province = null;
					String postalcode = null;
					if (currentAddressSplit != null) {
						name = (String) currentAddressSplit.get("address1");
						city = (String) currentAddressSplit.get("city");
						province = (String) currentAddressSplit.get("Province");
						postalcode = (String) currentAddressSplit
								.get("postalcode");
						// creating Applicant
						cLead = new CreateApplicant();

						cLead.createApplicantAddress(applicantID2, name, city,
								province, postalcode, coMovedIn1);
						Logger.debug("Applicant  created with currentAddressSplit ");
					}
					address=new controllers.Address();
					address.setName(name);
					address.setCity(city);
					address.setPostalCode(postalcode);
					address.setMovedIn(coMovedIn1);
					address.setProvience(province);
					applicant.getListOfAddress().add(address);
				}

				if ((coAppTotalcurrentMonths != null
						&& coAppTotalcurrentMonths.length() != 0 && !coAppTotalcurrentMonths
							.equals("")) && coApptotalcurrentMonthsInt <= 36) {

					Logger.debug("**********inside 1st coAppPriorAddress1 *******************");

					String coAppPriorAddress1 = dynamicForm
							.get("CoCurrentAddress2");
					String coAppInputMovedIn2 = dynamicForm.get("CoMovedIn2");

					String coAppPriorSumMonth1 = dynamicForm
							.get("coAppPriorSumMonth1");
					String coAppTotalpriorcurrentmonths1 = dynamicForm
							.get("coAppTotalpriorcurrentmonths1");
					int totalpriorcurrentmonths1Int = 0;
					try {
						totalpriorcurrentmonths1Int = Integer
								.parseInt(coAppTotalpriorcurrentmonths1);
					} catch (Exception e) {
						Logger.error(
								"exception in converting totalpriorcurrentmonths to int : ",
								e);
					}
					Date coMovedIn2 = null;
					try {
						coMovedIn2 = df2.parse(coAppInputMovedIn2);
					} catch (ParseException e) {
						Logger.error("Error in parsing string to date");
					}

					Logger.debug("coAppPriorAddress1" + coAppPriorAddress1
							+ "\n coAppInputMovedIn1 " + coAppInputMovedIn2
							+ "\n after string-2-date movedIn2" + coMovedIn2
							+ "\n comovedIn2>>" + coMovedIn2
							+ "\n coAppPriorSumMonth1" + coAppPriorSumMonth1
							+ "\n coAppTotalpriorcurrentmonths1"
							+ coAppTotalpriorcurrentmonths1
							+ "\n coAppTotalpriorcurrentmonths1:"
							+ coAppTotalpriorcurrentmonths1
							+ "\n TotalDate is:"
							+ coAppTotalpriorcurrentmonths1);

					if (coAppPriorAddress1 != null) {
						// currentaddressObj = new
						// AddressGroup(coAppcurrentAddress,currentYear,currentMonths,coAppCurrentSumMonth,coAppTotalcurrentMonths);
						prioraddress5Obj = new AddressGroup(coAppPriorAddress1,
								coAppPriorSumMonth1,
								coAppTotalpriorcurrentmonths1);
						// listOfAddresses.add(currentaddressObj);
						listOfAddresses.add(prioraddress5Obj);
						HashMap currentAddressSplit = addressSplit
								.getProperAddress(coAppPriorAddress1);

						String name = null;
						String city = null;
						String province = null;
						String postalcode = null;
						if (currentAddressSplit != null) {
							name = (String) currentAddressSplit.get("address1");
							city = (String) currentAddressSplit.get("city");
							province = (String) currentAddressSplit
									.get("Province");
							postalcode = (String) currentAddressSplit
									.get("postalcode");
							// Creating Applicant
							cLead = new CreateApplicant();
							// Loggeric for date
							cLead.createApplicantAddress(applicantID2, name,
									city, province, postalcode, coMovedIn2);
							Logger.debug("Going to OpenERP Create Applicant address ");
							address=new controllers.Address();
							address.setName(name);
							address.setCity(city);
							address.setPostalCode(postalcode);
							address.setMovedIn(coMovedIn2);
							address.setProvience(province);
							applicant.getListOfAddress().add(address);

						}// spliting address

					}// checking prior address is not null
					String coApptotalpriorcurrentmonths2 = dynamicForm
							.get("coApptotalpriorcurrentmonths2");
					int totalpriorcurrentmonths2Int = 0;
					try {
						totalpriorcurrentmonths2Int = Integer
								.parseInt(coApptotalpriorcurrentmonths2);
					} catch (Exception e) {
						Logger.error(
								"Error when parsing totalpriorcurrentmonths from string to int",
								e);
					}
					if (totalpriorcurrentmonths2Int <= 36
							&& (coApptotalpriorcurrentmonths2 != null && !coApptotalpriorcurrentmonths2
									.equals(""))) {

						Logger.debug("taking 2nd prior address");
						String coApppriorAddress2 = dynamicForm
								.get("CoCurrentAddress4");
						String coAppInputMovedIn3 = dynamicForm
								.get("CoMovedIn3");
						String coApppriorSumMonth2 = dynamicForm
								.get("coApppriorSumMonth2");

						Date coMovedIn3 = null;
						try {
							coMovedIn3 = df2.parse(coAppInputMovedIn3);
						} catch (ParseException e) {
							Logger.error("Error in parsing string to date");
						}
						Logger.debug("********** inside 1st co priorAddress2 *******************");
						Logger.debug("coApppriorAddress2" + coApppriorAddress2);
						Logger.debug("coAppInputMovedIn3 " + coAppInputMovedIn3);
						Logger.debug("Affter string-2-date coMovedIn3 "
								+ coMovedIn3);
						Logger.debug("coApppriorSumMonth2"
								+ coApppriorSumMonth2);
						// New Loggeric For prior Date 2
						Logger.debug("coApptotalpriorcurrentmonths2:"
								+ coApptotalpriorcurrentmonths2);

						Logger.debug("TotalDate is:"
								+ totalpriorcurrentmonths2Int);

						Calendar prical2 = Calendar.getInstance();
						prical2.add(Calendar.MONTH,
								-totalpriorcurrentmonths2Int);
						String currentDateTimenew2 = (df2.format(prical2
								.getTime()));

						Logger.debug("CurrentDate for co app Prior Address 2 is:"
								+ currentDateTimenew2);

						if (coApppriorAddress2 != null) {

							prioraddress6Obj = new AddressGroup(
									coApppriorAddress2, coApppriorSumMonth2,
									coApptotalpriorcurrentmonths2);
							listOfAddresses.add(prioraddress6Obj);

							HashMap currentAddressSplit = addressSplit
									.getProperAddress(coApppriorSumMonth2);

							String name = null;
							String city = null;
							String province = null;
							String postalcode = null;
							if (currentAddressSplit != null) {
								name = (String) currentAddressSplit
										.get("address1");
								city = (String) currentAddressSplit.get("city");
								province = (String) currentAddressSplit
										.get("Province");
								postalcode = (String) currentAddressSplit
										.get("postalcode");
								cLead = new CreateApplicant();

								cLead.createApplicantAddress(applicantID2,
										name, city, province, postalcode,
										coMovedIn3);
								Logger.debug("Applicant created with priorAddress2 ");

								Logger.debug("CouchBase data is appending...");
								address=new controllers.Address();
								address.setName(name);
								address.setCity(city);
								address.setPostalCode(postalcode);
								address.setMovedIn(coMovedIn3);
								address.setProvience(province);
								applicant.getListOfAddress().add(address);
							}// spliting prior address 2
						}// checking prior address 2 is not null
					}// end of Loggeric of prior address 2

				}

			}// end of additionalApplicant
			
			opportunity.setPogressStatus(65);
			
		 String formType="Applicant-SubForm-7";
			storeData.storeDataInCouchBase(formType, opportunity);
			
			Logger.debug("Data created in coucbase for MortgageForm7");

			// req.setAttribute("uniid",uniid);
			// req.setAttribute("applicantId",applicantId);
			// additionalApplicants
			EmployeIncomeTypeParam employeIncome = new EmployeIncomeTypeParam();
			SelfEmployeIncomeTypeParam selfEmpIncome = new SelfEmployeIncomeTypeParam();
			PensionIncomeTypeParam pension = new PensionIncomeTypeParam();
			InvestmentsIncomeTypeParam investIncType = new InvestmentsIncomeTypeParam();
			MaternityIncomeTypeParam maternityIncType = new MaternityIncomeTypeParam();
			VehicleAllowIncomeTypeParam vehicleIncType = new VehicleAllowIncomeTypeParam();
			LivingAllowIncomeTypeParam livingIncType = new LivingAllowIncomeTypeParam();
			CommissionIncomeTypeParam commIncType = new CommissionIncomeTypeParam();
			BonusIncomeTypeParam bonusIncType = new BonusIncomeTypeParam();
			OtherIncomeTypeParam otherIncType = new OtherIncomeTypeParam();

			String applicantName = (String) session.get("applicantFirstName");
			return ok(mortgagePage8.render("", employeIncome, selfEmpIncome,
					pension, investIncType, maternityIncType, vehicleIncType,
					livingIncType, commIncType, bonusIncType, otherIncType,
					applicantName));
		} catch (Exception e) {
			Logger.error("Error in mortgagePage7" + e);
			return ok("Something went wrong in mortgagePage6");
		}

	}

	public static Result mortgagePage8() {
		Logger.info("inside mortgagePage8");
		DynamicForm dynamicForm = form().bindFromRequest();
		FormatDateString formatDateString = new FormatDateString();
		try {
			// String incomeType = dynamicForm.get("pleaseselect"); //
			// incomeType
			String Employed = dynamicForm.get("employee");
			String Self_Employed = dynamicForm.get("selfEmployed");
			String Pension = dynamicForm.get("pension");
			String Investments = dynamicForm.get("investments");
			String Maternity = dynamicForm.get("maternity");
			String Vehicle_Allowance = dynamicForm.get("vehicleAllow");
			String LivingAllow = dynamicForm.get("livingAllow");
			String Commission = dynamicForm.get("commission");
			String Bonus = dynamicForm.get("bonus");
			String Other = dynamicForm.get("other");
			TotalAssets totalAssets = new TotalAssets();
			List<String> incomeType = new ArrayList<String>();
			String additionalApplicant = "";
			Session session = Http.Context.current().session();
			CouchBaseOperation storeData = new CouchBaseOperation();

			
			
			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
					+ "");
			

			try {
				additionalApplicant = session.get("additionalApplicants");
			} catch (Exception e) {
				Logger.error("Error in getting session value" + e);
			}

			Logger.debug("Employed =" + Employed + "\n Self_Employed ="
					+ Self_Employed + "\n Pension = " + Pension
					+ "Investments =" + Investments + "\n Maternity ="
					+ Maternity + "\n Vehicle_Allowance =" + Vehicle_Allowance
					+ "\n LivingAllow =" + LivingAllow + "\n Commission ="
					+ Commission + "\n Bonus =" + Bonus + "\n Other =" + Other);

			if (Employed != null && Employed.equalsIgnoreCase("true")) {
				Employed = "Employed";
				incomeType.add(Employed);
			}

			if (Self_Employed != null && Self_Employed.equalsIgnoreCase("true")) {
				Self_Employed = "SelfEmployed";
				incomeType.add(Self_Employed);
			}
			if (Pension != null && Pension.equalsIgnoreCase("true")) {
				Pension = "Pension";
				incomeType.add(Pension);
			}

			// CHANGEEEE
			if (Investments != null && Investments.equalsIgnoreCase("true")) {
				Investments = "Investments";
				incomeType.add(Investments);
			}

			if (Maternity.equalsIgnoreCase("true") && Maternity != null) {
				Maternity = "Maternity";
				incomeType.add(Maternity);
			}

			if (Vehicle_Allowance != null
					&& Vehicle_Allowance.equalsIgnoreCase("true")) {
				Vehicle_Allowance = "VehicleAllowance";
				incomeType.add(Vehicle_Allowance);
			}

			if (LivingAllow != null && LivingAllow.equalsIgnoreCase("true")) {
				LivingAllow = "LivingAllowance";
				incomeType.add(LivingAllow);
			}

			if (Commission != null && Commission.equalsIgnoreCase("true")) {
				Commission = "Commission";
				incomeType.add(Commission);
			}

			if (Bonus != null && Bonus.equalsIgnoreCase("true")) {
				Bonus = "Bonus";
				incomeType.add(Bonus);
			}
			if (Other != null && Other.equalsIgnoreCase("true")) {
				Other = "Other";
				incomeType.add(Other);
			}
			Logger.debug("incomeType =" + incomeType);

			String formType = "Mortgage Application";
			String subForm = "Mortgage Application 8";
			String applicantID = "";
			String co_ApplicantID = "";

			int leadId = 0;

			try {
				applicantID = (String) session.get("applicantID");

				leadId = Integer.parseInt((String) session.get("crmLeadId"));
				co_ApplicantID = session.get("applicantID2");
			} catch (Exception e) {
				Logger.error("Error in getting values from session " + e);
			}

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			// get current date time with Calendar()
			Calendar cal = Calendar.getInstance();
			String currentDateTime = (dateFormat.format(cal.getTime()));

			// get ip of latest form sumitted

			String ip = request().remoteAddress();

			CreateApplicant applicantEmployee = new CreateApplicant();

			Incomes income = null;
			ArrayList<Incomes> incomeListForOdoo = new ArrayList<Incomes>();

			Logger.debug("after incomeoddlist");
			String[] stringIncome = new String[10];
			List<String> selectedValues = new ArrayList<String>();

			for (int i = 0; i <= incomeType.size() - 1; i++) {
				selectedValues.add(incomeType.get(i));
			}
			Logger.debug("selectedValues=" + selectedValues);
			Logger.debug("before for");

			Applicant applicant=opportunity.getApplicants().get(0);
			Income incometype=null;
			
			for (String selectvalue : selectedValues) {

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Employed")) {
					Logger.debug("inside employed");
					income = new Incomes();
					// String currentEmployee1 =
					// dynamicForm.get("empIncomeTypes"); // currentEmployee
					String business1 = dynamicForm.get("empBusinessName");
					String startMonth1 = dynamicForm.get("empStartMonth");
					String monthsWorked1 = dynamicForm.get("totalMonths"); // monthsWorked
					String currentPosition1 = dynamicForm.get("empPosition"); // currentPosition
					String employeeTotalMonths1 = dynamicForm
							.get("totalMonths"); // employeeTotalMonths
					
					
					
					// Logger.debug("currentEmployee1= " + currentEmployee1);
					Logger.debug("currentPosition1= " + currentPosition1);
					Logger.debug("monthsWorked1= " + monthsWorked1);
					Date startMont100 = formatDateString
							.getFormattedDate(startMonth1);
					
					
				

					income.Type = "Employed";
					income.Business = business1;
					income.JobTitle = currentPosition1;
					income.Supplementary = true;
					income.Months = monthsWorked1;
					incomeListForOdoo.add(income);
					Logger.debug("incomeListForOdoo " + incomeListForOdoo);

					int monthsWorked = 0;
					try {
						monthsWorked = Integer.parseInt(monthsWorked1);
						Logger.debug("monthsWorked " + monthsWorked);
					} catch (Exception e) {
						Logger.error("Error in parsing monthsWorked1 in mortgagePage8 method"
								+ e);
					}
					incometype=new Income();
					incometype.setTypeOfIncome("Employed");
					incometype.setBusiness(business1);
					incometype.setPosition(currentPosition1);
					incometype.setMonth(monthsWorked);
					incometype.setHistorical(false);
					applicant.getIncomes().add(incometype);
					int employeeTotal = 0;
					try {
						employeeTotal = Integer.parseInt(employeeTotalMonths1);
					} catch (Exception e) {
						Logger.error("Error in parsing employee Total Months1"
								+ e);
					}
					Logger.debug("++++++++++++++++++ " + "applicantID="
							+ applicantID + "\n business = " + business1
							+ "\n currentPosition1 =" + currentPosition1
							+ "\n employeeTotal =" + employeeTotal
							+ "+++++++++++++++");
					applicantEmployee.createIncomeApplicant(applicantID, 1,
							business1, currentPosition1, employeeTotal, true);
					if (employeeTotal < 36 && employeeTotalMonths1 != null) {

						Logger.debug("inside employed leass then 36 ------------>1");

						income = new Incomes();

						String priorBusiness11 = dynamicForm
								.get("empBusinessName2"); // priorEmployee1
						String startMonth11 = dynamicForm.get("empStartMonth2"); // priorEmployee1
						String monthsum11 = dynamicForm.get("monthsum11");// monthsum1
						String position11 = dynamicForm.get("empPosition2");// position1

						String monthsTotal11 = dynamicForm.get("totalMonths2");// monthsTotal1
						Date startMont101 = formatDateString
								.getFormattedDate(startMonth11);

						Logger.debug("priorEmployee11" + "Employed"
								+ "\n Applicant-business11 " + priorBusiness11
								+ "\n monthsum11" + monthsum11
								+ "\n monthsTotal11" + monthsTotal11
								+ "\n position11" + position11);

					
						income.Type = "Employed";
						income.Business = priorBusiness11;
						income.JobTitle = position11;
						income.Supplementary = false;
						income.Historical = true;
						income.Months = monthsum11;
						incomeListForOdoo.add(income);
						// Logger.debug("list size : "+incomeListForOdoo.size());
						int monthsWorked2 = Integer.parseInt(monthsum11);

						Logger.debug("++++++++++++++++++" + "applicantID="
								+ applicantID + "\n priorBusiness11= "
								+ priorBusiness11 + "\n position11 ="
								+ position11 + "\n monthsWorked2 ="
								+ monthsWorked2 + "+++++++++++++++");
						applicantEmployee.createIncomeApplicant(applicantID, 1,
								priorBusiness11, position11, monthsWorked2,
								false, true);

						int employeeTotal1 = 0;
						try {
							employeeTotal1 = Integer.parseInt(monthsTotal11);
						} catch (Exception e) {
							Logger.error("Error in parsing monthsTotal11 " + e);
						}
						incometype=new Income();
						incometype.setTypeOfIncome("Employed");
						incometype.setBusiness(priorBusiness11);
						incometype.setPosition(position11);
						incometype.setMonth(monthsWorked2);
						incometype.setHistorical(true);
					
						applicant.getIncomes().add(incometype);
						if (employeeTotal1 < 36 && monthsTotal11 != null) {
							Logger.debug("inside employed leass then 36 ------------>2");

							String business21 = dynamicForm
									.get("empBusinessName3"); // priorEmployee2
							String startMonth21 = dynamicForm
									.get("empStartMonth3"); // months2
							String position21 = dynamicForm.get("empPosition3"); // position2
							String monthsum21 = dynamicForm.get("monthsum33"); // monthsum2
							String monthsTotal21 = dynamicForm
									.get("totalMonths3"); // monthsTotal2
							// Logger.debug("business21 "+business21+"\n startMonth21 "
							// +startMonth21+"\n position21 "+position21);
							Date startMont102 = formatDateString
									.getFormattedDate(startMonth21);
						

							income.Type = "Employed";
							income.Business = business21;
							income.JobTitle = position21;
							income.Supplementary = false;
							income.Historical = true;
							income.Months = monthsum21;
							incomeListForOdoo.add(income);
							Logger.debug("priorEmployee21" + "\t" + business21);
							Logger.debug("position21" + "\t" + position21);
							Logger.debug("monthsTotal21" + "\t" + monthsTotal21);
							Logger.debug("monthsum21" + "\t" + monthsum21);

							int monthsInteger = 0;

							try {
								monthsInteger = Integer.parseInt(monthsum21);
							} catch (Exception e) {
								Logger.error("Error in parsing monthsum21 in mortgagePage8 method"
										+ e);
							}
							incometype=new Income();
							incometype.setTypeOfIncome("Employed");
							incometype.setBusiness(business21);
							incometype.setPosition(position21);
							incometype.setMonth(monthsInteger);
							incometype.setHistorical(true);
							applicant.getIncomes().add(incometype);
							
							Logger.debug("afetr int");
							Logger.debug("++++++++++++++++++" + "applicantID="
									+ applicantID + "\n business21= "
									+ business21 + "\n position11 ="
									+ position11 + "\n monthsWorked2 ="
									+ monthsWorked2 + "+++++++++++++++");
							applicantEmployee.createIncomeApplicant(
									applicantID, 1, business21, position21,
									monthsInteger, false, true);
							int employeeTotal2 = 0;
							try {
								employeeTotal2 = Integer
										.parseInt(monthsTotal21);
							} catch (Exception e) {
								Logger.error("Error in parsing monthsTotal21 in mortgagePage8 method"
										+ e);
							}

							Logger.debug("Employed data entry is successfully.");
						}
					}
				} // End of employed data entry

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("SelfEmployed")) {
					Logger.debug("inside Self Employed");
					String business11 = dynamicForm.get("selfBusinessName")
							+ "(Current Year NOA)"; // priorEmployee1
					String selfEmpStartMonth11 = dynamicForm
							.get("selfStartMon");// selfEmployee Start month
					String position11 = dynamicForm.get("selfPosition");// position1
					String monthsum11 = dynamicForm.get("monthsum1");// monthsum1
					String monthsTotal11 = dynamicForm.get("monthsum1");// monthsTotal1
					Logger.debug("business21 " + business11
							+ "\n startMonth21 " + selfEmpStartMonth11
							+ "\n position21 " + position11);
					Date selfEmpStartMonth110 = formatDateString
							.getFormattedDate(selfEmpStartMonth11);
					// 1st Record
					

					income = new Incomes();
					income.Type = "Self-Employed";
					income.Business = business11 + "(Current Year NOA)";
					income.JobTitle = position11;
					income.Historical = false;
					income.Supplementary = false;
					income.Months = monthsum11;

					boolean history = false;
					boolean supplementri = false;

					Logger.debug("after Icnome Type");
					int monthsInteger = 0;
					try {
						monthsInteger = Integer.parseInt(monthsum11);
					} catch (Exception e) {
						Logger.error("Error in parsing monthsum1 in mortgagePage8"
								+ e);
					}

					Logger.debug("Inside Months");
					Logger.debug("Created Incomes type Self employee below 3 years ");
					Logger.debug("Going to CouchBAse");

					Logger.debug("storing data in couchBase");
					int totalmonthsum1Integer = 0;
					try {
						totalmonthsum1Integer = Integer.parseInt(monthsTotal11);

						Logger.debug("totalmonthsum1Integer"
								+ totalmonthsum1Integer);

					} catch (Exception e) {
						Logger.error("Error in parsing monthsTotal1 in mortgagePage8"
								+ e);
					}
					Logger.debug("-------------------------------"
							+ "\n business = " + business11 + "\n position1= "
							+ position11 + "\n totalmonthsum1Integer= "
							+ totalmonthsum1Integer + "\n history= " + history
							+ "\n supplementri= " + supplementri);
					applicantEmployee.createIncomeApplicant(applicantID, 2,
							business11, position11, totalmonthsum1Integer,
							history, supplementri);
					incometype=new Income();
					incometype.setTypeOfIncome("SelfEmployed");
					incometype.setBusiness(business11);
					incometype.setPosition(position11);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(false);
					applicant.getIncomes().add(incometype);
					

					if (totalmonthsum1Integer < 36) {
						Logger.debug("Privious Self employ Details");

						String business22 = dynamicForm
								.get("selfBusinessName2") + "(Prior Year NOA)";
						String positionself22 = dynamicForm
								.get("selfPosition2");
						String selfEmpStartMonth22 = dynamicForm
								.get("selfStartMon2");
						String selfemployemonthssum22 = dynamicForm
								.get("selfemployemonthssum2");
						String selfemployemonthsumTotal22 = dynamicForm
								.get("selfemployemonthsumTotal2");
						Date selfEmpStartMonth111 = formatDateString
								.getFormattedDate(selfEmpStartMonth22);
						// 2nd Record
						
						Logger.debug("selfemployed2:\t" + business22);
						Logger.debug("positionself2:\t" + positionself22);
						// Logger.debug("selfemployedyears2:\t" +
						// selfemployedyears2);
						// Logger.debug("selfemployedmonths170:\t"+
						// selfemployedmonths170);
						Logger.debug("selfemployemonthssum2:\t"
								+ selfemployemonthssum22);
						income = new Incomes();
						income.Type = "Self-Employed";
						income.Business = business22 + "(Prior Year NOA)";
						income.JobTitle = positionself22;
						income.Historical = true;
						income.Supplementary = false;
						income.Months = selfemployemonthssum22;

						int selfemployemonthssum2Integer = Integer
								.parseInt(selfemployemonthssum22);

						Logger.debug("-------------------------------"
								+ "\n priorEmployee1 = " + business22
								+ "\n positionself21= " + positionself22
								+ "\n totalmonthsum1Integer= "
								+ selfemployemonthssum2Integer);
						// Logic to inser data in OpenERP
						Logger.debug("Going to OpenERP........");
						applicantEmployee.createIncomeApplicant(applicantID, 2,
								business22, positionself22,
								selfemployemonthssum2Integer, false, true);
						int totalMonthSelf = Integer
								.parseInt(selfemployemonthsumTotal22);
						
						
						incometype=new Income();
						incometype.setTypeOfIncome("SelfEmployed");
						incometype.setBusiness(business22);
						incometype.setPosition(positionself22);
						incometype.setMonth(selfemployemonthssum2Integer);
						incometype.setHistorical(true);
						applicant.getIncomes().add(incometype);

						if (totalMonthSelf < 36) {
							Logger.debug("Privious Self employ Details");

							String business33 = dynamicForm
									.get("selfBusinessName3")
									+ "(Prior Year NOA)";
							String selfStartMon333 = dynamicForm
									.get("selfStartMon3");
							String positionself33 = dynamicForm
									.get("selfPosition3");
							String selfemployemonthssum33 = dynamicForm
									.get("selfemployemonthssum2");
							String selfemployemonthsumTotal33 = dynamicForm
									.get("selfemployemonthsumTotal2");

							Date selfEmpStartMonth112 = formatDateString
									.getFormattedDate(selfStartMon333);
							// 3rd Record
				

							Logger.debug("selfemployed2:\t" + business33);
							Logger.debug("positionself2:\t" + positionself33);
							// Logger.debug("selfemployedyears2:\t" +
							// selfemployedyears2);
							// Logger.debug("selfemployedmonths170:\t"+
							// selfemployedmonths170);
							Logger.debug("selfemployemonthssum2:\t"
									+ selfemployemonthssum33);
							income = new Incomes();
							income.Type = "Self-Employed";
							income.Business = business33 + "(Prior Year NOA)";
							income.JobTitle = positionself33;
							income.Historical = true;
							income.Supplementary = false;
							income.Months = selfemployemonthssum33;

							Logger.debug("-------------------------------"
									+ "\n priorEmployee1 = " + business33
									+ "\n positionself21= " + positionself33);
							int selfEmpMonthSum33 = Integer
									.parseInt(selfemployemonthssum33);
							// Logic to inser data in OpenERP
							Logger.debug("Going to OpenERP........");
							applicantEmployee.createIncomeApplicant(
									applicantID, 2, business33, positionself33,
									selfEmpMonthSum33, false, true);
							
							incometype=new Income();
							incometype.setTypeOfIncome("SelfEmployed");
							incometype.setBusiness(business33);
							incometype.setPosition(positionself33);
							incometype.setMonth(selfEmpMonthSum33);
							incometype.setHistorical(true);
							applicant.getIncomes().add(incometype);

						}
					}
				}// End of self-employee

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Pension")) {
					Logger.debug("inside Pension");

					String pensionBusiness = dynamicForm
							.get("pensionBusiness1");
					String pensionStartMonth = dynamicForm.get("pensionMY1");
					String pensionPosition = dynamicForm
							.get("pensionPosition1");
					String pensionTotal = dynamicForm.get("pensionTotal");
					Date pensionStartMonth1 = formatDateString
							.getFormattedDate(pensionStartMonth);
					income = new Incomes();
					Logger.debug("inside pension");
					income.Type = "Pension";
					income.Business = pensionBusiness;
					income.JobTitle = pensionPosition;
					income.Supplementary = true;
					income.Months = pensionTotal;
					income.Position = "";
					int monthsInteger = 0;
					try {
						monthsInteger = Integer.parseInt(pensionTotal);
					} catch (Exception p) {
						Logger.error(
								"Error when parsing pension monthsum total", p);
					}

					applicantEmployee.createIncomeApplicant(applicantID, 6,
							pensionBusiness, pensionPosition, monthsInteger,
							true);

					Logger.debug("created Applicant Income for Pension");

					Iterator iterate = incomeListForOdoo.iterator();
					
					incometype=new Income();
					incometype.setTypeOfIncome("pension");
					incometype.setBusiness(pensionBusiness);
					incometype.setPosition(pensionPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);
					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);

				}// End of Pension

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Investments")) {

					Logger.debug("inside Investments");
					String investmentsBusiness = dynamicForm
							.get("investmentBusiness1");
					String invsestMentStartMonth = dynamicForm
							.get("investmentsMY1");
					String investmentsPosition = dynamicForm
							.get("investmentPosition1");
					String investmentsTotal = dynamicForm
							.get("investmentTotal");

					TotalAssets totalAssetss = new TotalAssets();
					Date invsestMentStartMonth1 = formatDateString
							.getFormattedDate(invsestMentStartMonth);
					income = new Incomes();
					income.Type = "Investments";
					income.Business = investmentsBusiness;
					income.JobTitle = investmentsPosition;
					income.Supplementary = true;
					income.Months = investmentsTotal;

					Logger.debug("Going to create Applicantbased Incomes for Investments");
					int monthsInteger = 0;
					try {
						monthsInteger = Integer.parseInt(investmentsTotal);
					} catch (Exception e) {
						Logger.error(
								"Error when parsing investments monthsum total ",
								e);
					}
					// Bussiness created with MonthSelection

					applicantEmployee.createIncomeApplicant(applicantID, 13,
							investmentsBusiness, investmentsPosition,
							monthsInteger, true);
					Logger.debug("created with the Investments");
					// CouchBase

					incometype=new Income();
					incometype.setTypeOfIncome("Investments");
					incometype.setBusiness(investmentsBusiness);
					incometype.setPosition(investmentsPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);
					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				}// End of Investments

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Maternity")) {
					Logger.debug("inside Maternity");

					String maternityBusiness = dynamicForm
							.get("maternityBusiness1");
					String maternityStartMonth = dynamicForm
							.get("maternityMY1");
					String maternityPosition = dynamicForm
							.get("maternityPosition1");
					String maternityMonthTotal = dynamicForm
							.get("maternityTotal");
					Logger.debug("maternityMonthTotal >>>>>>>>>>>>>>"
							+ maternityMonthTotal);
					Date maternityStartMonth1 = formatDateString
							.getFormattedDate(maternityStartMonth);
					income = new Incomes();
					income.Type = "Maternity";

					income.Business = maternityBusiness;
					income.JobTitle = maternityPosition;
					income.Supplementary = true;
					income.Months = maternityMonthTotal;

					int monthsInteger = Integer.parseInt(maternityMonthTotal);

					applicantEmployee.createIncomeApplicant(applicantID, 14,
							maternityBusiness, maternityPosition,
							monthsInteger, true);

					Logger.debug("created Applicant Income for Pension");

					incometype=new Income();
					incometype.setTypeOfIncome("Maternity");
					incometype.setBusiness(maternityBusiness);
					incometype.setPosition(maternityPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);

					Iterator iterate = incomeListForOdoo.iterator();

					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				}

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("VehicleAllowance")) {
					Logger.debug("inside vehical allownace");

					String vehicAllowBusiness = dynamicForm
							.get("vehicleAllowBusiness1");
					String vehicAllowPosition = dynamicForm
							.get("vehicleAllowPosition1");
					String vehicleStartMonth = dynamicForm
							.get("vehicleAllowMY1");
					String vehicAllowMonthTotal = dynamicForm
							.get("vehicleAllowTotal");

					Date vehicleStartMonth1 = formatDateString
							.getFormattedDate(vehicleStartMonth);
					income = new Incomes();
					income.Type = "Vehicle Allowance";
					income.Business = vehicAllowBusiness;
					income.JobTitle = vehicAllowPosition;
					income.Supplementary = true;
					income.Months = vehicAllowMonthTotal;

					// put Logger here

					Logger.debug("Going to create Applicantbased Incomes for Vehicle allowance");

					int monthsInteger = Integer.parseInt(vehicAllowMonthTotal);
					// Bussiness created with MonthSelection

					applicantEmployee.createIncomeApplicant(applicantID, 12,
							vehicAllowBusiness, vehicAllowPosition,
							monthsInteger, true);
					Logger.debug("created with the VehicleAllowance");

					incometype=new Income();
					incometype.setTypeOfIncome("VehicleAllowance");
					incometype.setBusiness(vehicAllowBusiness);
					incometype.setPosition(vehicAllowPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);

					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				} // End of Vehicle allowance

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("LivingAllowance")) {
					Logger.debug("inside Living Allowance");

					String livingAllowBusiness = dynamicForm
							.get("livingAllowBusiness1");
					String livingAllowPosition = dynamicForm
							.get("livingAllowPosition1");
					String livingStartMonth = dynamicForm.get("livingAllowMY1");
					String livingAllowMonthTotal = dynamicForm
							.get("livingAllowTotal");
					Date livingStartMonth1 = formatDateString
							.getFormattedDate(livingStartMonth);

					income = new Incomes();
					income.Type = "Living Allowance";
					income.Business = livingAllowBusiness;
					income.JobTitle = livingAllowPosition;
					income.Supplementary = true;
					income.Months = livingAllowMonthTotal;

					// put Logger here

					Logger.debug("Going to create Applicantbased Incomes for Vehicle allowance");

					int monthsInteger = Integer.parseInt(livingAllowMonthTotal);
					// Bussiness created with MonthSelection

					applicantEmployee.createIncomeApplicant(applicantID, 11,
							livingAllowBusiness, livingAllowPosition,
							monthsInteger, true);
					Logger.debug("created with the Living Allowance");

					incometype=new Income();
					incometype.setTypeOfIncome("LivingAllowance");
					incometype.setBusiness(livingAllowBusiness);
					incometype.setPosition(livingAllowPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);


					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				} // End of Living allowance

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Commission")) {
					Logger.debug("inside Commission ");

					String commissionBusiness = dynamicForm
							.get("commissionBusiness1");
					String commissionStartMonth = dynamicForm
							.get("commissionMY1");
					String commissionPosition = dynamicForm
							.get("commissionPosition1");
					String commissionMonthTotal = dynamicForm
							.get("commissionTotal");
					Date commissionStartMonth1 = formatDateString
							.getFormattedDate(commissionStartMonth);
					income = new Incomes();
					income.Type = "Commission";
					income.Business = commissionBusiness;
					income.JobTitle = commissionPosition;
					income.Supplementary = true;
					income.Months = commissionMonthTotal;
					// put Logger here
					Logger.debug("Going to create Applicantbased Incomes for Commission ");

					int monthsInteger = Integer.parseInt(commissionMonthTotal);
					// Bussiness created with MonthSelection

					applicantEmployee.createIncomeApplicant(applicantID, 4,
							commissionBusiness, commissionPosition,
							monthsInteger, true);
					Logger.debug("created with the Commission");

					incometype=new Income();
					incometype.setTypeOfIncome("Commission");
					incometype.setBusiness(commissionBusiness);
					incometype.setPosition(commissionPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);

					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				} // End of Commission

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Bonus")) {
					Logger.debug("inside Bonus ");

					String bonusBusiness = dynamicForm.get("bonusBusiness1");
					String bonusStartMonth = dynamicForm.get("bonusMY1");
					String bonusPosition = dynamicForm.get("bonusPosition1");
					String bonusMonthTotal = dynamicForm.get("bonusTotal");

					Date bonusStartMonth1 = formatDateString
							.getFormattedDate(bonusStartMonth);
					income = new Incomes();
					income.Type = "Bonus";
					income.Business = bonusBusiness;
					income.JobTitle = bonusPosition;
					income.Supplementary = true;
					income.Months = bonusMonthTotal;

					// put Logger here

					Logger.debug("Going to create Applicantbased Incomes for Bonus ");

					int monthsInteger = Integer.parseInt(bonusMonthTotal);
					// Bussiness created with MonthSelection

					applicantEmployee.createIncomeApplicant(applicantID, 8,
							bonusBusiness, bonusPosition, monthsInteger, true);
					Logger.debug("created with the Bonus");

					incometype=new Income();
					incometype.setTypeOfIncome("Bonus");
					incometype.setBusiness(bonusBusiness);
					incometype.setPosition(bonusPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);

					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				} // End of Bonus allowance

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Other")) {
					Logger.debug("inside Other ");

					String otherBusiness = dynamicForm.get("otherBusiness1");
					String otherStartMonth = dynamicForm.get("otherMY1");
					String otherPosition = dynamicForm.get("otherPosition1");
					String otherMonthTotal = dynamicForm.get("otherTotal");
					Date otherStartMonth1 = formatDateString
							.getFormattedDate(otherStartMonth);

					income = new Incomes();
					income.Type = "Other";
					income.Business = otherBusiness;
					income.JobTitle = otherPosition;
					income.Supplementary = true;
					income.Months = otherMonthTotal;

					// put Logger here

					Logger.debug("Going to create Applicantbased Incomes for O");

					int monthsInteger = Integer.parseInt(otherMonthTotal);
					// Bussiness created with MonthSelection

					applicantEmployee.createIncomeApplicant(applicantID, 13,
							otherBusiness, otherPosition, monthsInteger, true);
					Logger.debug("created with the Other");


					incometype=new Income();
					incometype.setTypeOfIncome("Other");
					incometype.setBusiness(otherBusiness);
					incometype.setPosition(otherPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);


					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				} // End of Other

			}// end of for loop\
			
			opportunity.setPogressStatus(70);
			 formType="Applicant-subForm-8";
		storeData.storeDataInCouchBase(formType, opportunity);
			// HARD CODE SHOULD BE REMOVE
			String applicantName = (String) session
					.get("co_applicantFirstName");
			String coApplicantName = (String) session
					.get("co_applicantFirstName");
			if (additionalApplicant != null
					&& additionalApplicant.equalsIgnoreCase("yes")) {
				return ok(mortgagePage9.render(additionalApplicant,
						new EmployeIncomeTypeParam(),
						new SelfEmployeIncomeTypeParam(),
						new PensionIncomeTypeParam(),
						new InvestmentsIncomeTypeParam(),
						new MaternityIncomeTypeParam(),
						new VehicleAllowIncomeTypeParam(),
						new LivingAllowIncomeTypeParam(),
						new CommissionIncomeTypeParam(),
						new BonusIncomeTypeParam(), new OtherIncomeTypeParam(),
						coApplicantName));
			} else {

				return ok(mortgagePage10Assets.render("", applicantName,
						coApplicantName, totalAssets));
			}
		} catch (Exception e) {

			Logger.error("Error in mortgagePage8 ", e);
			return ok("some thing went wrong in mortgagePage8");
		}
	}

	public static Result mortgagePage9() {
		Logger.info("***inside mortgagePage9***");
		DynamicForm dynamicForm = form().bindFromRequest();
		try {
			// String incomeType = dynamicForm.get("pleaseselect"); //
			// incomeType
			String Employed = dynamicForm.get("employee");
			String Self_Employed = dynamicForm.get("selfEmployed");
			String Pension = dynamicForm.get("pension");
			String Investments = dynamicForm.get("investments");
			String Maternity = dynamicForm.get("maternity");
			String Vehicle_Allowance = dynamicForm.get("vehicleAllow");
			String LivingAllow = dynamicForm.get("livingAllow");
			String Commission = dynamicForm.get("commission");
			String Bonus = dynamicForm.get("bonus");
			String Other = dynamicForm.get("other");
			TotalAssets totalAssets = new TotalAssets();
			totalAssets.setVehicle(new ArrayList<AssetsParam>());
			totalAssets.setBankAccount(new ArrayList<AssetsParam>());
			totalAssets.setRrsp(new ArrayList<AssetsParam>());
			totalAssets.setInvestments(new ArrayList<AssetsParam>());
			totalAssets.setOthers(new ArrayList<AssetsParam>());
			List<String> incomeType = new ArrayList<String>();
			String additionalApplicant = "";
			Session session = Http.Context.current().session();
			CouchBaseOperation storeData = new CouchBaseOperation();

			FormatDateString formatDateString = new FormatDateString();
			
			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
					+ "");
			
			
			
			String applicantName = "";
			String coApplicantName = "";
			try {

				additionalApplicant = session.get("additionalApplicants");

				applicantName = session.get("applicantFirstName");
				coApplicantName = session.get("co_applicantFirstName");
			} catch (Exception e) {
				Logger.error("Error in getting session value ", e);
			}

			Logger.debug("Employed =" + Employed + "\n Self_Employed ="
					+ Self_Employed + "\n Pension =" + Pension
					+ "\n Investments =" + Investments + "\n Maternity ="
					+ Maternity + "\n Vehicle_Allowance = " + Vehicle_Allowance
					+ "\n LivingAllow =" + LivingAllow + "\n Commission ="
					+ Commission + "\n Bonus =" + Bonus + "\n Other =" + Other);

			if (Employed != null && Employed.equalsIgnoreCase("true")) {
				Employed = "Employed";
				incomeType.add(Employed);
			}

			if (Self_Employed != null && Self_Employed.equalsIgnoreCase("true")) {
				Self_Employed = "SelfEmployed";
				incomeType.add(Self_Employed);
			}
			if (Pension != null && Pension.equalsIgnoreCase("true")) {
				Pension = "Pension";
				incomeType.add(Pension);
			}

			if (Investments != null && Investments.equalsIgnoreCase("true")) {
				Investments = "Investments";
				incomeType.add(Investments);
			}

			if (Maternity.equalsIgnoreCase("true") && Maternity != null) {
				Maternity = "Maternity";
				incomeType.add(Maternity);
			}

			if (Vehicle_Allowance != null
					&& Vehicle_Allowance.equalsIgnoreCase("true")) {
				Vehicle_Allowance = "VehicleAllowance";
				incomeType.add(Vehicle_Allowance);
			}

			if (LivingAllow != null && LivingAllow.equalsIgnoreCase("true")) {
				LivingAllow = "LivingAllowance";
				incomeType.add(LivingAllow);
			}

			if (Commission != null && Commission.equalsIgnoreCase("true")) {
				Commission = "Commission";
				incomeType.add(Commission);
			}

			if (Bonus != null && Bonus.equalsIgnoreCase("true")) {
				Bonus = "Bonus";
				incomeType.add(Bonus);
			}
			if (Other != null && Other.equalsIgnoreCase("true")) {
				Other = "Other";
				incomeType.add(Other);
			}
			Logger.debug("incomeType =" + incomeType);

			String formType = "Mortgage Application";
			String subForm = "Mortgage Co-Application 3";
			String applicantID = "";
			String co_ApplicantID = "";

			int leadId = 0;
			try {
				applicantID = (String) session.get("applicantID");
				leadId = Integer.parseInt((String) session.get("crmLeadId"));
				co_ApplicantID = session.get("applicantID2");
			} catch (Exception e) {
				Logger.error("Error in getting values from session " + e);
			}

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			// get current date time with Calendar()
			Calendar cal = Calendar.getInstance();
			String currentDateTime = (dateFormat.format(cal.getTime()));

			// get ip of latest form sumitted

			String ip = request().remoteAddress();

			CreateApplicant applicantEmployee = new CreateApplicant();

			Incomes income = null;
			ArrayList<Incomes> incomeListForOdoo = new ArrayList<Incomes>();

			Logger.debug("after incomeoddlist");
			String[] stringIncome = new String[10];
			List<String> selectedValues = new ArrayList<String>();

			for (int i = 0; i <= incomeType.size() - 1; i++) {
				selectedValues.add(incomeType.get(i));
			}
			Logger.debug("selectedValues=" + selectedValues);
			Logger.debug("before for");
			
			
			Applicant applicant=opportunity.getApplicants().get(1);
			Income incometype=null;

			for (String selectvalue : selectedValues) {
				Logger.debug("applicantID>>>>>>>>>>>>>>> " + applicantID);
				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Employed")) {
					Logger.debug("inside employed");
					income = new Incomes();
					// String currentEmployee1 =
					// dynamicForm.get("empIncomeTypes"); // currentEmployee
					String business1 = dynamicForm.get("empBusinessName");
					String startMonth1 = dynamicForm.get("empStartMonth");
					String monthsWorked1 = dynamicForm.get("totalMonths"); // monthsWorked
					String currentPosition1 = dynamicForm.get("empPosition"); // currentPosition
					String employeeTotalMonths1 = dynamicForm
							.get("totalMonths"); // employeeTotalMonths

					Date coEmpStartDate1 = formatDateString
							.getFormattedDate(startMonth1);
					// Logger.debug("currentEmployee1= " + currentEmployee1);
					Logger.debug("currentPosition1= " + currentPosition1);

					Logger.debug("monthsWorked1= " + monthsWorked1);
				
				

					income.Type = "Employed";
					income.Business = business1;
					income.JobTitle = currentPosition1;
					income.Supplementary = true;
					income.Months = monthsWorked1;
					incomeListForOdoo.add(income);
					Logger.debug("incomeListForOdoo " + incomeListForOdoo);

					int monthsWorked = 0;
					try {
						monthsWorked = Integer.parseInt(monthsWorked1);
						Logger.debug("monthsWorked " + monthsWorked);
					} catch (Exception e) {
						Logger.error("Error in parsing monthsWorked1 in mortgagePage8 method"
								+ e);
					}

					int employeeTotal = 0;
					try {
						employeeTotal = Integer.parseInt(employeeTotalMonths1);
					} catch (Exception e) {
						Logger.error("Error in parsing employee Total Months1"
								+ e);
					}
					Logger.debug("++++++++++++++++++ " + "applicantID="
							+ applicantID + "\n business = " + business1
							+ "\n currentPosition1 =" + currentPosition1
							+ "\n employeeTotal =" + employeeTotal
							+ "+++++++++++++++");
					applicantEmployee.createIncomeApplicant(applicantID, 1,
							business1, currentPosition1, employeeTotal, true);
					
					
					incometype=new Income();
					incometype.setTypeOfIncome("Employed");
					incometype.setBusiness(business1);
					incometype.setPosition(currentPosition1);
					incometype.setMonth(monthsWorked);
					incometype.setHistorical(false);
					applicant.getIncomes().add(incometype);
					if (employeeTotal < 36 && employeeTotalMonths1 != null) {

						Logger.debug("inside employed leass then 36 ------------>1");

						income = new Incomes();

						String priorBusiness11 = dynamicForm
								.get("empBusinessName2"); // priorEmployee1
						String startMonth11 = dynamicForm.get("empStartMonth2"); // priorEmployee1
						String monthsum11 = dynamicForm.get("monthsum11");// monthsum1
						String position11 = dynamicForm.get("empPosition2");// position1

						String monthsTotal11 = dynamicForm.get("totalMonths2");// monthsTotal1

						Logger.debug("priorEmployee11" + "Employed"
								+ "\n Applicant-business11 " + priorBusiness11
								+ "\n monthsum11" + monthsum11
								+ "\n monthsTotal11" + monthsTotal11
								+ "\n position11" + position11);

						Date coEmpStartDate2 = formatDateString
								.getFormattedDate(startMonth11);
						

						income.Type = "Employed";
						income.Business = priorBusiness11;
						income.JobTitle = position11;
						income.Supplementary = false;
						income.Historical = true;
						income.Months = monthsum11;
						incomeListForOdoo.add(income);
						// Logger.debug("list size : "+incomeListForOdoo.size());
						int monthsWorked2 = Integer.parseInt(monthsum11);

						Logger.debug("++++++++++++++++++" + "applicantID="
								+ applicantID + "\n priorBusiness11= "
								+ priorBusiness11 + "\n position11 ="
								+ position11 + "\n monthsWorked2 ="
								+ monthsWorked2 + "+++++++++++++++");
						applicantEmployee.createIncomeApplicant(applicantID, 1,
								priorBusiness11, position11, monthsWorked2,
								false, true);

						int employeeTotal1 = 0;
						try {
							employeeTotal1 = Integer.parseInt(monthsTotal11);
						} catch (Exception e) {
							Logger.error("Error in parsing monthsTotal11 " + e);
						}
						
						incometype=new Income();
						incometype.setTypeOfIncome("Employed");
						incometype.setBusiness(priorBusiness11);
						incometype.setPosition(position11);
						incometype.setMonth(monthsWorked2);
						incometype.setHistorical(true);
						applicant.getIncomes().add(incometype);

						if (employeeTotal1 < 36 && monthsTotal11 != null) {
							Logger.debug("inside employed leass then 36 ------------>2");

							String business21 = dynamicForm
									.get("empBusinessName3"); // priorEmployee2
							String startMonth21 = dynamicForm
									.get("empStartMonth3"); // months2
							String position21 = dynamicForm.get("empPosition3"); // position2
							String monthsum21 = dynamicForm.get("monthsum33"); // monthsum2
							String monthsTotal21 = dynamicForm
									.get("totalMonths3"); // monthsTotal2

							Date coEmpStartDate3 = formatDateString
									.getFormattedDate(startMonth21);

						

							income.Type = "Employed";
							income.Business = business21;
							income.JobTitle = position21;
							income.Supplementary = false;
							income.Historical = true;
							income.Months = monthsum21;
							incomeListForOdoo.add(income);
							Logger.debug("priorEmployee21" + "\t" + business21);
							Logger.debug("position21" + "\t" + position21);
							Logger.debug("monthsTotal21" + "\t" + monthsTotal21);
							Logger.debug("monthsum21" + "\t" + monthsum21);

							int monthsInteger = 0;

							try {
								monthsInteger = Integer.parseInt(monthsum21);
							} catch (Exception e) {
								Logger.error("Error in parsing monthsum21 in mortgagePage8 method"
										+ e);
							}
							Logger.debug("afetr int");
							Logger.debug("++++++++++++++++++" + "applicantID="
									+ applicantID + "\n business21= "
									+ business21 + "\n position11 ="
									+ position11 + "\n monthsWorked2 ="
									+ monthsWorked2 + "+++++++++++++++");
							applicantEmployee.createIncomeApplicant(
									applicantID, 1, business21, position21,
									monthsInteger, false, true);
							int employeeTotal2 = 0;
							try {
								employeeTotal2 = Integer
										.parseInt(monthsTotal21);
							} catch (Exception e) {
								Logger.error(
										"Error in parsing monthsTotal21 in mortgagePage8 method",
										e);
							}
							incometype=new Income();
							incometype.setTypeOfIncome("Employed");
							incometype.setBusiness(business21);
							incometype.setPosition(position21);
							incometype.setMonth(monthsInteger);
							incometype.setHistorical(true);
							applicant.getIncomes().add(incometype);
							Logger.debug("Employed data entry is successfully.");
						}
					}
				} // End of employed data entry

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Self-Employ")) {
					Logger.debug("inside Self Employed");

					String business11 = dynamicForm.get("selfBusinessName")
							+ "(Current Year NOA)"; // priorEmployee1
					String selfEmpStartMonth11 = dynamicForm
							.get("selfStartMon");// selfEmployee Start month
					String position11 = dynamicForm.get("selfPosition");// position1
					String monthsum11 = dynamicForm.get("monthsum1");// monthsum1
					String monthsTotal11 = dynamicForm.get("monthsum1");// monthsTotal1

					Date coSlfEmpStartDate1 = formatDateString
							.getFormattedDate(selfEmpStartMonth11);

					Logger.debug(business11);
					Logger.debug(position11);
					Logger.debug(monthsum11);
					Logger.debug(monthsTotal11);
					// 1st Record
				

					income = new Incomes();
					income.Type = "Self-Employed";
					income.Business = business11 + "(Current Year NOA)";
					income.JobTitle = position11;
					income.Historical = false;
					income.Supplementary = false;
					income.Months = monthsum11;

					boolean history = false;
					boolean supplementri = false;

					Logger.debug("after Icnome Type");
					int monthsInteger = 0;
					try {
						monthsInteger = Integer.parseInt(monthsum11);
					} catch (Exception e) {
						Logger.error("Error in parsing monthsum1 in mortgagePage8"
								+ e);
					}

					Logger.debug("Inside Months");
					Logger.debug("Created Incomes type Self employee below 3 years ");
					Logger.debug("Going to CouchBAse");

					Logger.debug("storing data in couchBase");
					int totalmonthsum1Integer = 0;
					try {
						totalmonthsum1Integer = Integer.parseInt(monthsTotal11);

						Logger.debug("totalmonthsum1Integer"
								+ totalmonthsum1Integer);

					} catch (Exception e) {
						Logger.error("Error in parsing monthsTotal1 in mortgagePage8"
								+ e);
					}
					Logger.debug("-------------------------------"
							+ "\n business = " + business11 + "\n position1= "
							+ position11 + "\n totalmonthsum1Integer= "
							+ totalmonthsum1Integer + "\n history= " + history
							+ "\n supplementri= " + supplementri);
					applicantEmployee.createIncomeApplicant(applicantID, 2,
							business11, position11, totalmonthsum1Integer,
							history, supplementri);
					incometype=new Income();
					incometype.setTypeOfIncome("Self-Employ");
					incometype.setBusiness(business11);
					incometype.setPosition(position11);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);
					if (totalmonthsum1Integer < 36) {
						Logger.debug("Privious Self employ Details");

						String business22 = dynamicForm
								.get("selfBusinessName2") + "(Prior Year NOA)";
						String positionself22 = dynamicForm
								.get("selfPosition2");
						String selfEmpStartMonth22 = dynamicForm
								.get("selfStartMon2");
						String selfemployemonthssum22 = dynamicForm
								.get("selfemployemonthssum2");
						String selfemployemonthsumTotal22 = dynamicForm
								.get("selfemployemonthsumTotal2");

						Date coSlfEmpStartDate2 = formatDateString
								.getFormattedDate(selfEmpStartMonth22);

						// 2nd Record
					

						Logger.debug("selfemployed2:\t" + business22);
						Logger.debug("positionself2:\t" + positionself22);
						// Logger.debug("selfemployedyears2:\t" +
						// selfemployedyears2);
						// Logger.debug("selfemployedmonths170:\t"+
						// selfemployedmonths170);
						Logger.debug("selfemployemonthssum2:\t"
								+ selfemployemonthssum22);
						income = new Incomes();
						income.Type = "Self-Employed";
						income.Business = business22 + "(Prior Year NOA)";
						income.JobTitle = positionself22;
						income.Historical = true;
						income.Supplementary = false;
						income.Months = selfemployemonthssum22;

						int selfemployemonthssum2Integer = Integer
								.parseInt(selfemployemonthssum22);

						Logger.debug("-------------------------------"
								+ "\n priorEmployee1 = " + business22
								+ "\n positionself21= " + positionself22
								+ "\n totalmonthsum1Integer= "
								+ selfemployemonthssum2Integer);
						// Logic to inser data in OpenERP
						Logger.debug("Going to OpenERP........");
						applicantEmployee.createIncomeApplicant(applicantID, 2,
								business22, positionself22,
								selfemployemonthssum2Integer, false, true);
						int totalMonthSelf = Integer
								.parseInt(selfemployemonthsumTotal22);
						
						incometype=new Income();
						incometype.setTypeOfIncome("Self-Employ");
						incometype.setBusiness(business22);
						incometype.setPosition(positionself22);
						incometype.setMonth(monthsInteger);
						incometype.setHistorical(true);
						applicant.getIncomes().add(incometype);
						if (totalMonthSelf < 36) {
							Logger.debug("Privious Self employ Details");

							String business33 = dynamicForm
									.get("selfBusinessName3")
									+ "(Prior Year NOA)";
							String selfStartMon333 = dynamicForm
									.get("selfStartMon3");
							String positionself33 = dynamicForm
									.get("selfPosition3");
							String selfemployemonthssum33 = dynamicForm
									.get("selfemployemonthssum2");
							String selfemployemonthsumTotal33 = dynamicForm
									.get("selfemployemonthsumTotal2");

							Date coSlfEmpStartDate3 = formatDateString
									.getFormattedDate(selfStartMon333);

							// 3rd Record
							

							Logger.debug("selfemployed2:\t" + business33);
							Logger.debug("positionself2:\t" + positionself33);
							// Logger.debug("selfemployedyears2:\t" +
							// selfemployedyears2);
							// Logger.debug("selfemployedmonths170:\t"+
							// selfemployedmonths170);
							Logger.debug("selfemployemonthssum2:\t"
									+ selfemployemonthssum33);
							income = new Incomes();
							income.Type = "Self-Employed";
							income.Business = business33 + "(Prior Year NOA)";
							income.JobTitle = positionself33;
							income.Historical = true;
							income.Supplementary = false;
							income.Months = selfemployemonthssum33;

							Logger.debug("-------------------------------"
									+ "\n priorEmployee1 = " + business33
									+ "\n positionself21= " + positionself33);
							int selfEmpMonthSum33 = Integer
									.parseInt(selfemployemonthssum33);
							// Logic to inser data in OpenERP
							Logger.debug("Going to OpenERP........");
							applicantEmployee.createIncomeApplicant(
									applicantID, 2, business33, positionself33,
									selfEmpMonthSum33, false, true);
							
							incometype=new Income();
							incometype.setTypeOfIncome("Self-Employ");
							incometype.setBusiness(business33);
							incometype.setPosition(positionself33);
							incometype.setMonth(monthsInteger);
							incometype.setHistorical(true);
							applicant.getIncomes().add(incometype);
						}
					}
				}// End of self-employee

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Pension")) {
					Logger.debug("inside Pension");

					String pensionBusiness = dynamicForm
							.get("pensionBusiness1");
					String pensionStartMonth = dynamicForm.get("pensionMY1");
					String pensionPosition = dynamicForm
							.get("pensionPosition1");
					String pensionTotal = dynamicForm.get("pensionTotal");
					Logger.debug("Form data for pension "
							+ "\n pensionBusiness " + pensionBusiness
							+ "\n pensionStartMonth " + pensionStartMonth
							+ "\n pensionPosition " + pensionPosition
							+ "\n pensionTotal " + pensionTotal);
					income = new Incomes();
					Logger.debug("inside pension");
					income.Type = "Pension";
					income.Business = pensionBusiness;
					income.JobTitle = pensionPosition;
					income.Supplementary = true;
					income.Months = pensionTotal;
					income.Position = "";
					int monthsInteger = 0;
					try {
						monthsInteger = Integer.parseInt(pensionTotal);
					} catch (Exception e) {
						Logger.error(
								"Error when converting pensionTotal String to int ",
								e);
					}

					applicantEmployee.createIncomeApplicant(applicantID, 6,
							pensionBusiness, pensionPosition, monthsInteger,
							true);

					Logger.debug("created Applicant Income for Pension");

					Date pensionStartMonth1 = formatDateString
							.getFormattedDate(pensionStartMonth);

					
					incometype=new Income();
					incometype.setTypeOfIncome("Pension");
					incometype.setBusiness(pensionBusiness);
					incometype.setPosition(pensionPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);

					Iterator iterate = incomeListForOdoo.iterator();

					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);

				}// End of Pension

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Investments")) {

					Logger.debug("inside Investments");
					String investmentsBusiness = dynamicForm
							.get("investmentBusiness1");
					String invsestMentStartMonth = dynamicForm
							.get("investmentsMY1");
					String investmentsPosition = dynamicForm
							.get("investmentPosition1");
					String investmentsTotal = dynamicForm
							.get("investmentTotal");
					Date invsestMentStartMonth1 = formatDateString
							.getFormattedDate(invsestMentStartMonth);
					TotalAssets totalAssetss = new TotalAssets();

					income = new Incomes();
					income.Type = "Investments";
					income.Business = investmentsBusiness;
					income.JobTitle = investmentsPosition;
					income.Supplementary = true;
					income.Months = investmentsTotal;

					Logger.debug("Going to create Applicantbased Incomes for Investments");

					int monthsInteger = Integer.parseInt(investmentsTotal);
					// Bussiness created with MonthSelection

					applicantEmployee.createIncomeApplicant(applicantID, 13,
							investmentsBusiness, investmentsPosition,
							monthsInteger, true);
					Logger.debug("created with the Investments");
					// CouchBase

					incometype=new Income();
					incometype.setTypeOfIncome("Investments");
					incometype.setBusiness(investmentsBusiness);
					incometype.setPosition(investmentsPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);
					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				}// End of Investments

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Maternity")) {
					Logger.debug("inside Maternity");

					String maternityBusiness = dynamicForm
							.get("maternityBusiness1");
					String maternityStartMonth = dynamicForm
							.get("maternityMY1");
					String maternityPosition = dynamicForm
							.get("maternityPosition1");
					String maternityMonthTotal = dynamicForm
							.get("maternityTotal");

					Date maternityStartMonth1 = formatDateString
							.getFormattedDate(maternityStartMonth);
					Logger.debug("maternityMonthTotal >>>>>>>>>>>>>>"
							+ maternityMonthTotal);
					income = new Incomes();
					income.Type = "Maternity";

					income.Business = maternityBusiness;
					income.JobTitle = maternityPosition;
					income.Supplementary = true;
					income.Months = maternityMonthTotal;

					int monthsInteger = Integer.parseInt(maternityMonthTotal);

					applicantEmployee.createIncomeApplicant(applicantID, 14,
							maternityBusiness, maternityPosition,
							monthsInteger, true);

					Logger.debug("created Applicant Income for Pension");

					incometype=new Income();
					incometype.setTypeOfIncome("Maternity");
					incometype.setBusiness(maternityBusiness);
					incometype.setPosition(maternityPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);

					Iterator iterate = incomeListForOdoo.iterator();

					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				}

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("VehicleAllowance")) {
					Logger.debug("inside vehical allownace");

					String vehicAllowBusiness = dynamicForm
							.get("vehicleAllowBusiness1");
					String vehicAllowPosition = dynamicForm
							.get("vehicleAllowPosition1");
					String vehicleStartMonth = dynamicForm
							.get("vehicleAllowMY1");
					String vehicAllowMonthTotal = dynamicForm
							.get("vehicleAllowTotal");

					Date vehicleStartMonth1 = formatDateString
							.getFormattedDate(vehicleStartMonth);
					income = new Incomes();
					income.Type = "Vehicle Allowance";
					income.Business = vehicAllowBusiness;
					income.JobTitle = vehicAllowPosition;
					income.Supplementary = true;
					income.Months = vehicAllowMonthTotal;

					// put Logger here

					Logger.debug("Going to create Applicantbased Incomes for Vehicle allowance");

					int monthsInteger = Integer.parseInt(vehicAllowMonthTotal);
					// Bussiness created with MonthSelection

					applicantEmployee.createIncomeApplicant(applicantID, 12,
							vehicAllowBusiness, vehicAllowPosition,
							monthsInteger, true);
					Logger.debug("created with the VehicleAllowance");

					incometype=new Income();
					incometype.setTypeOfIncome("VehicleAllowance");
					incometype.setBusiness(vehicAllowBusiness);
					incometype.setPosition(vehicAllowPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);
					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				} // End of Vehicle allowance

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("LivingAllowance")) {
					Logger.debug("inside Living Allowance");

					String livingAllowBusiness = dynamicForm
							.get("livingAllowBusiness1");
					String livingAllowPosition = dynamicForm
							.get("livingAllowPosition1");
					String livingStartMonth = dynamicForm.get("livingAllowMY1");
					String livingAllowMonthTotal = dynamicForm
							.get("livingAllowTotal");
					Date livingStartMonth1 = formatDateString
							.getFormattedDate(livingStartMonth);

					income = new Incomes();
					income.Type = "Living Allowance";
					income.Business = livingAllowBusiness;
					income.JobTitle = livingAllowPosition;
					income.Supplementary = true;
					income.Months = livingAllowMonthTotal;

					// put Logger here

					Logger.debug("Going to create Applicantbased Incomes for Vehicle allowance");

					int monthsInteger = Integer.parseInt(livingAllowMonthTotal);
					// Bussiness created with MonthSelection

					applicantEmployee.createIncomeApplicant(applicantID, 11,
							livingAllowBusiness, livingAllowPosition,
							monthsInteger, true);
					Logger.debug("created with the Living Allowance");

					incometype=new Income();
					incometype.setTypeOfIncome("LivingAllowance");
					incometype.setBusiness(livingAllowBusiness);
					incometype.setPosition(livingAllowPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);

					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				} // End of Living allowance

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Commission")) {
					Logger.debug("inside Commission ");

					String commissionBusiness = dynamicForm
							.get("commissionBusiness1");
					String commissionStartMonth = dynamicForm
							.get("commissionMY1");
					String commissionPosition = dynamicForm
							.get("commissionPosition1");
					String commissionMonthTotal = dynamicForm
							.get("commissionTotal");
					Date commissionStartMonth1 = formatDateString
							.getFormattedDate(commissionStartMonth);

					income = new Incomes();
					income.Type = "Commission";
					income.Business = commissionBusiness;
					income.JobTitle = commissionPosition;
					income.Supplementary = true;
					income.Months = commissionMonthTotal;
					// put Logger here
					Logger.debug("Going to create Applicantbased Incomes for Commission ");

					int monthsInteger = Integer.parseInt(commissionMonthTotal);
					// Bussiness created with MonthSelection

					applicantEmployee.createIncomeApplicant(applicantID, 4,
							commissionBusiness, commissionPosition,
							monthsInteger, true);
					Logger.debug("created with the Commission");

					incometype=new Income();
					incometype.setTypeOfIncome("Commission");
					incometype.setBusiness(commissionBusiness);
					incometype.setPosition(commissionPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);

					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				} // End of Commission

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Bonus")) {
					Logger.debug("inside Bonus ");

					String bonusBusiness = dynamicForm.get("bonusBusiness1");
					String bonusStartMonth = dynamicForm.get("bonusMY1");
					String bonusPosition = dynamicForm.get("bonusPosition1");
					String bonusMonthTotal = dynamicForm.get("bonusTotal");

					Date bonusStartMonth1 = formatDateString
							.getFormattedDate(bonusStartMonth);
					income = new Incomes();
					income.Type = "Bonus";
					income.Business = bonusBusiness;
					income.JobTitle = bonusPosition;
					income.Supplementary = true;
					income.Months = bonusMonthTotal;

					// put Logger here

					Logger.debug("Going to create Applicantbased Incomes for Bonus ");

					int monthsInteger = Integer.parseInt(bonusMonthTotal);
					// Bussiness created with MonthSelection

					applicantEmployee.createIncomeApplicant(applicantID, 8,
							bonusBusiness, bonusPosition, monthsInteger, true);
					Logger.debug("created with the Bonus");


					incometype=new Income();
					incometype.setTypeOfIncome("Bonus");
					incometype.setBusiness(bonusBusiness);
					incometype.setPosition(bonusPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);

					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				} // End of Bonus allowance

				if (selectvalue != null
						&& selectvalue.equalsIgnoreCase("Other")) {
					Logger.debug("inside Other ");

					String otherBusiness = dynamicForm.get("otherBusiness1");
					String otherStartMonth = dynamicForm.get("otherMY1");
					String otherPosition = dynamicForm.get("otherPosition1");
					String otherMonthTotal = dynamicForm.get("otherTotal");
					Date otherStartMonth1 = formatDateString
							.getFormattedDate(otherStartMonth);

					income = new Incomes();
					income.Type = "Other";
					income.Business = otherBusiness;
					income.JobTitle = otherPosition;
					income.Supplementary = true;
					income.Months = otherMonthTotal;

					// put Logger here

					Logger.debug("Going to create Applicantbased Incomes for O");

					int monthsInteger = Integer.parseInt(otherMonthTotal);
					// Bussiness created with MonthSelection

					applicantEmployee.createIncomeApplicant(applicantID, 13,
							otherBusiness, otherPosition, monthsInteger, true);
					Logger.debug("created with the Other");

					incometype=new Income();
					incometype.setTypeOfIncome("Other");
					incometype.setBusiness(otherBusiness);
					incometype.setPosition(otherPosition);
					incometype.setMonth(monthsInteger);
					incometype.setHistorical(true);
					applicant.getIncomes().add(incometype);

					// Remaining fields Stay Blank
					incomeListForOdoo.add(income);
				} // End of Other

			}// end of for loop

			
			opportunity.setPogressStatus(75);
			Logger.debug("applicantID>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
					+ applicantID);
			
			
			storeData.storeDataInCouchBase(formType, opportunity);
			return ok(mortgagePage10Assets.render("", applicantName,
					coApplicantName, totalAssets));
		} catch (Exception e) {

			Logger.error("Error in mortgagePage9 ", e);
			return ok("some thing went wrong in mortgagePage9");
		}
	}

	public static Result mortgagePage10Assets() {
		Logger.info("inside mortgagePage10Assets");
		try {
			DynamicForm dynamicForm = form().bindFromRequest();
			CouchBaseOperation storeData = new CouchBaseOperation();

			String formType = "Mortgage Application";
			String subForm = "Mortgage Application 10 assets";
			Session session = Http.Context.current().session();
			String applicantID = "";
			String co_ApplicantID = "";

			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
					+ "");
			try {
				applicantID = (String) session.get("applicantID");

			
				co_ApplicantID = session.get("applicantID2");
			} catch (Exception e) {
				Logger.error("Error in getting values from session " + e);
			}

		
			String None = dynamicForm.get("None");
			Logger.debug("None= " + None);
			Logger.debug(None.equalsIgnoreCase("true") + "");
			if (None.equalsIgnoreCase("true")) {
				None = "";
				
				opportunity.setPogressStatus(85);
				storeData.storeDataInCouchBase(formType, opportunity);
			} else {
				
				Applicant applicant=opportunity.getApplicants().get(0);
				String Vehicle = dynamicForm.get("Vehicle");
				String BankAccount = dynamicForm.get("BankAccount");
				String RRSPTSFA = dynamicForm.get("RRSPTSFA");
				String Investments = dynamicForm.get("Investments");
				String Other = dynamicForm.get("Other");

				Logger.debug("Vehicle =" + Vehicle + "\n BankAccount ="
						+ BankAccount + "\n RRSPTSFA =" + RRSPTSFA
						+ "\n Investments =" + Investments + "\n Other ="
						+ Other);

				List<String> asstypeArray = new ArrayList<String>();
				if (Vehicle.equalsIgnoreCase("true") && Vehicle != null) {
					Vehicle = "Vehicle";
					asstypeArray.add(Vehicle);
				}
				if (BankAccount.equalsIgnoreCase("true") && BankAccount != null) {
					BankAccount = "BankAccount";
					asstypeArray.add(BankAccount);
				}
				if (RRSPTSFA.equalsIgnoreCase("true") && RRSPTSFA != null) {
					RRSPTSFA = "RRSPTSFA";
					asstypeArray.add(RRSPTSFA);
				}
				if (Investments.equalsIgnoreCase("true") && Investments != null) {
					Investments = "Investments";
					asstypeArray.add(Investments);
				}
				if (Other.equalsIgnoreCase("true") && Other != null) {
					Other = "Other";
					asstypeArray.add(Other);
				}
				if (None.equalsIgnoreCase("true") && None != null) {
					None = "None";
					asstypeArray.add(None);
				}
				// String[] asstypeArray = assetType.split("\n");
				Logger.debug("asserttype array i.e selected asstets lists : "
						+ asstypeArray);

				// int strasstypelen=asstypeArray.length;
				List<String> selectedValues = new ArrayList<String>();
				for (int i = 0; i <= asstypeArray.size() - 1; i++) {
					selectedValues.add(asstypeArray.get(i));
				}
				Logger.debug("after incomeoddlist = " + selectedValues.size());

				
				Assetes assets=null;
			
				CreateApplicant aseetsApplicant;
				for (String selectedValue : selectedValues) {

					Logger.debug("selected values inside loop : "
							+ selectedValue);
					Logger.debug(selectedValue.equalsIgnoreCase("Vehicle") + "");
					Logger.debug((selectedValue != null) + "");
					JSONObject assetDetails = null;
					JSONArray vehicleArray = new JSONArray();
					if (selectedValue.equalsIgnoreCase("Vehicle")
							&& (selectedValue != null)) {

						Logger.debug("inside Vehicle");
						// String vehicles=dynamicForm.get("vehicles");
						// //vehicles
						// Logger.debug(vehicles);
						
						

						final String[] asset = request().body()
								.asFormUrlEncoded().get("asset[]");
						final String[] description = request().body()
								.asFormUrlEncoded().get("description[]");
						final String[] values = request().body()
								.asFormUrlEncoded().get("value[]");
						final String[] ownerShip = request().body()
								.asFormUrlEncoded().get("designation[]");
						aseetsApplicant = new CreateApplicant();
						
						
				
						assets.setType("Vehicle");
						for (int i = 0; i <= asset.length - 1; i++) {
							Logger.debug("description[" + i + "] = "
									+ description[i]);
							Logger.debug("values[" + i + "] = " + values[i]);
							// CHANGE
							
							assets=new Assetes();
							assetDetails = new JSONObject();
							assetDetails.put("Applicant-AssetType" + i,
									selectedValue);
							if ((description[i] != null && !description[i]
									.equals(""))
									&& (values[i] != null && !values[i]
											.equals(""))) {

								Logger.debug("inside if Vehicle1");
								Logger.debug("ownership[" + i + "] = "
										+ ownerShip[i]);
								if (ownerShip[i].isEmpty()
										|| ownerShip[i] == null) {
									Logger.debug("all values are not null and empty");
									String discription1 = (String) description[i];
									String value = (String) values[i];
									// String
									// ownership=(String)job.get("Ownership");
									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "Vehicle",
											discription1, value);
									
									assets.setDescription(discription1);
									assets.setValue(value);
									assets.setOwnerShip(ownerShip[i]);
									Logger.debug("Applicant Assets created for Vehicle withOut Value and OwnerShip");
									// Logic for CouchBase
									assetDetails
											.put("Applicant-discription_Vehicle"
													+ i, discription1);
									assetDetails.put("Applicant-value_Vehicle"
											+ i, value);
									// dataStoreValue.put("ownership"+i,ownership);
								} else {
									// ownership is comming as null
									Logger.debug("all values are not null and empty except ownership");
									String discription = (String) description[i];
									String value = (String) values[i];
									String ownership = (String) ownerShip[i];
									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "Vehicle",
											discription, value);
									Logger.debug("Applicant Assets created for Vehicle withOut Value and OwnerShip");
									// Logic for CouchBase
									
									
									assets.setDescription(discription);
									assets.setValue(value);
									assets.setOwnerShip(ownerShip[i]);
									assetDetails
											.put("Applicant-discription_Vehicle"
													+ i, discription);
									assetDetails.put("Applicant-value_Vehicle"
											+ i, value);
									assetDetails.put(
											"Applicant-ownership_Vehicle" + i,
											ownership);
								}

							} else {
								Logger.debug("inside if Vehicle4");

								if (ownerShip[i].isEmpty()
										|| ownerShip[i] == null) {
									String discription = "Vehicle name not filled";
									String value = "0";
									// String
									// ownership=(String)job.get("Ownership");
									aseetsApplicant = new CreateApplicant();
									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "Vehicle",
											discription, value);
									
									
									assets.setDescription(discription);
									assets.setValue(value);
									assets.setOwnerShip(ownerShip[i]);
									// Loggeric for couchBase
								} else {
									String discription = "Vehicle not filled";
									String value = "0";
									String ownership = (String) ownerShip[i];
									aseetsApplicant = new CreateApplicant();
									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "Vehicle",
											discription, value);
									// Loggeric for couchBase
									assetDetails.put(
											"Applicant-ownership_vehicle_with",
											ownership);
									
									assets.setDescription(discription);
									assets.setValue(value);
									assets.setOwnerShip(ownerShip[i]);
								}
							}
							vehicleArray.put(assetDetails);
							applicant.getAssetList().add(assets);
						}

					}// End of Vehicle

					Logger.debug(selectedValue.equalsIgnoreCase("BankAccount")
							+ "ssss");
					Logger.debug((selectedValue != null) + "ssss");
					if (selectedValue.equalsIgnoreCase("BankAccount")
							&& (selectedValue != null)) {

						Logger.debug("inside Bank Account selected.");
						
						// String vehicles=dynamicForm.get("vehicles");
						// //vehicles
						// Logger.debug(vehicles);
						assets=new Assetes();
						assets.setType("BankAccount");

						final String[] asset1 = request().body()
								.asFormUrlEncoded().get("asset1[]");
						final String[] description1 = request().body()
								.asFormUrlEncoded().get("description1[]");
						final String[] values1 = request().body()
								.asFormUrlEncoded().get("value1[]");
						final String[] ownerShip1 = request().body()
								.asFormUrlEncoded().get("designation1[]");
						aseetsApplicant = new CreateApplicant();

						for (int i = 0; i <= asset1.length - 1; i++) {
							Logger.debug("description1[" + i + "] = "
									+ description1[i]);
							Logger.debug("values1[" + i + "] = " + values1[i]);
							assetDetails = new JSONObject();
							assetDetails.put("Applicant-AssetType" + i,
									selectedValue);
							if ((description1[i] != null && !description1[i]
									.equals(""))
									&& (values1[i] != null && !values1[i]
											.equals(""))) {
								Logger.debug("inside if Bank Account");
								Logger.debug("ownshipe[" + i + "] = "
										+ ownerShip1[i]);
								if (ownerShip1[i].isEmpty()
										|| ownerShip1[i] == null) {
									Logger.debug("all values are not null and empty");
									String discription1 = (String) description1[i];
									String value1 = (String) values1[i];
									// String
									// ownership=(String)job.get("Ownership");
									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "Non-RRSPs",
											discription1, value1);
									Logger.debug("Applicant Assets created for Bank Account withOut Value and OwnerShip");
									// Logic for CouchBase
									assetDetails.put(
											"Applicant-discription_Bank_Account"
													+ i, discription1);
									assetDetails.put(
											"Applicant-value_Bank_Account" + i,
											value1);
									assets.setDescription(discription1);
									assets.setValue(value1);
									assets.setOwnerShip(ownerShip1[i]);
									// dataStoreValue.put("ownership",ownership);
								} else {
									// ownership is comming as null
									Logger.debug("all values are not null and empty except ownership");
									String discription = (String) description1[i];
									String value1 = (String) values1[i];
									String ownership1 = (String) ownerShip1[i];
									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "Non-RRSPs",
											discription, value1);
									Logger.debug("Applicant Assets created for Bank Account withOut Value and OwnerShip");
									// Logic for CouchBase
									assetDetails.put(
											"Applicant-discription_Bank_Account"
													+ i, discription);
									assetDetails.put(
											"Applicant-value_Bank_Account" + i,
											value1);
									assetDetails.put(
											"Applicant-ownership_Bank_Account"
													+ i, ownership1);
									assets.setDescription(discription);
									assets.setValue(value1);
									assets.setOwnerShip(ownerShip1[i]);
								}

							} else {
								Logger.debug("inside if Bank_Account4");

								if (ownerShip1[i].isEmpty()
										|| ownerShip1[i] == null) {
									String discription1 = "Bank Account name not filled";
									String value1 = "0";
									// String
									// ownership=(String)job.get("Ownership");

									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "Non-RRSPs",
											discription1, value1);
									// Loggeric for couchBase
									assets.setDescription(discription1);
									assets.setValue(value1);
									assets.setOwnerShip(ownerShip1[i]);
								} else {
									String discription1 = "Bank Account not filled";
									String value1 = "0";
									String ownership1 = (String) ownerShip1[i];
									aseetsApplicant = new CreateApplicant();
									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "Non-RRSPs",
											discription1, value1);
									// Loggeric for couchBase
									assetDetails.put(
											"Applicant-ownership_Bank_Account_with"
													+ i, ownership1);
									assets.setDescription(discription1);
									assets.setValue(value1);
									assets.setOwnerShip(ownerShip1[i]);
								}
							}
							vehicleArray.put(assetDetails);
							applicant.getAssetList().add(assets);
						}
					}// End of Bank-Account

					Logger.debug(selectedValue.equalsIgnoreCase("RRSPTSFA")
							+ "RRsps test");
					Logger.debug((selectedValue != null) + "RRsps null test");
					if (selectedValue.equalsIgnoreCase("RRSPTSFA")
							&& (selectedValue != null)) {
						Logger.debug("RRSPTSFA");
						assets=new Assetes();
						
						assets.setType("RRSPTSFA");
						
			
						// String vehicles=dynamicForm.get("vehicles");
						// //vehicles
						// Logger.debug(vehicles);

						final String[] asset2 = request().body()
								.asFormUrlEncoded().get("asset2[]");
						final String[] description2 = request().body()
								.asFormUrlEncoded().get("description2[]");
						final String[] values2 = request().body()
								.asFormUrlEncoded().get("value2[]");
						final String[] ownerShip2 = request().body()
								.asFormUrlEncoded().get("designation2[]");
						aseetsApplicant = new CreateApplicant();

						for (int i = 0; i <= asset2.length - 1; i++) {
							Logger.debug("description1[" + i + "] = "
									+ description2[i] + "\n values1[" + i
									+ "] = " + values2[i]);
							assetDetails = new JSONObject();
							assetDetails.put("Applicant-AssetType" + i,
									selectedValue);
							if ((description2[i] != null && !description2[i]
									.equals(""))
									&& (values2[i] != null && !values2[i]
											.equals(""))) {
								Logger.debug("inside if RRSPs / TSFA");
								Logger.debug("ownshipe2[" + i + "] = "
										+ ownerShip2[i]);
								if (ownerShip2[i].isEmpty()
										|| ownerShip2[i] == null) {
									Logger.debug("all values are not null and empty");
									String discription2 = (String) description2[i];
									String value2 = (String) values2[i];
									// String
									// ownership=(String)job.get("Ownership");
									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "RRSPs", discription2,
											value2);
									Logger.debug("Applicant Assets created for RRSPs withOut Value and OwnerShip");
									// Logic for CouchBase
									assetDetails.put(
											"Applicant-discription_RRSPs" + i,
											discription2);
									assetDetails.put("Applicant-value_RRSPs"
											+ i, value2);
									assets.setDescription(discription2);
									assets.setValue(value2);
									assets.setOwnerShip(ownerShip2[i]);
									// dataStoreValue.put("ownership",ownership);
								} else {
									// ownership is comming as null
									Logger.debug("all values are not null and empty except ownership");
									String discription2 = (String) description2[i];
									String value2 = (String) values2[i];
									String ownership2 = (String) ownerShip2[i];
									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "RRSPs", discription2,
											value2);
									Logger.debug("Applicant Assets created for RRSPs withOut Value and OwnerShip");
									// Logic for CouchBase
									assetDetails.put(
											"Applicant-discription_RRSPs" + i,
											discription2);
									assetDetails.put("Applicant-value_RRSPs"
											+ i, value2);
									assetDetails.put(
											"Applicant-ownership_RRSPs" + i,
											ownership2);
									
									assets.setDescription(discription2);
									assets.setValue(value2);
									assets.setOwnerShip(ownerShip2[i]);
								}

							} else {
								Logger.debug("inside if RRSPs4");

								if (ownerShip2[i].isEmpty()
										|| ownerShip2[i] == null) {
									String discription2 = "RRSPs name not filled";
									String value2 = "0";
									// String
									// ownership=(String)job.get("Ownership");

									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "RRSPs", discription2,
											value2);
									assets.setDescription(discription2);
									assets.setValue(value2);
									assets.setOwnerShip(ownerShip2[i]);
									// Loggeric for couchBase
								} else {
									String discription2 = "RRSPs not filled";
									String value2 = "0";
									String ownership2 = (String) ownerShip2[i];

									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "RRSPs", discription2,
											value2);
									// Loggeric for couchBase
									assetDetails.put("Applicant-value_RRSPs4"
											+ i, value2);
									assetDetails.put(
											"Applicant-ownership_RRSPs4" + i,
											ownership2);
									assets.setDescription(discription2);
									assets.setValue(value2);
									assets.setOwnerShip(ownerShip2[i]);
								}
							}
							vehicleArray.put(assetDetails);
							applicant.getAssetList().add(assets);

						}
					}// End of RRSPs

					Logger.debug(selectedValue.equalsIgnoreCase("Investments")
							+ "<<<Investments test");
					Logger.debug((selectedValue != null) + "Selected null test");

					if ((selectedValue.equalsIgnoreCase("Investments"))
							&& (selectedValue != null)) {

						Logger.debug("Investment");

						assets=new Assetes();
						assets.setType("Investments");
						// String vehicles=dynamicForm.get("vehicles");
						// //vehicles
						// Logger.debug(vehicles);

						final String[] asset3 = request().body()
								.asFormUrlEncoded().get("asset3[]");
						final String[] description3 = request().body()
								.asFormUrlEncoded().get("description3[]");
						final String[] values3 = request().body()
								.asFormUrlEncoded().get("value3[]");
						final String[] ownerShip3 = request().body()
								.asFormUrlEncoded().get("designation3[]");
						aseetsApplicant = new CreateApplicant();

						for (int i = 0; i <= asset3.length - 1; i++) {
							Logger.debug("description1[" + i + "] = "
									+ description3[i]);
							Logger.debug("values1[" + i + "] = " + values3[i]);

							assetDetails = new JSONObject();
							assetDetails.put("Applicant-AssetType" + i,
									selectedValue);

							if ((description3[i] != null && !description3[i]
									.equals(""))
									&& (values3[i] != null && !values3[i]
											.equals(""))) {

								Logger.debug("inside if InvestmentsTSFA");
								Logger.debug("ownshipe3[" + i + "] = "
										+ ownerShip3[i]);
								if (ownerShip3[i].isEmpty()
										|| ownerShip3[i] == null) {
									Logger.debug("all values are not null and empty");
									String discription3 = (String) description3[i];
									String value3 = (String) values3[i];
									// String
									// ownership=(String)job.get("Ownership");
									Logger.debug("Applicant Assets to be created .........");
									// aseetsApplicant.createAssetApplicant(applicantId,
									// "Investments",discription3, value3);
									Logger.debug("Applicant Assets created for Investments withOut Value and OwnerShip");
									// Logic for CouchBase
									assetDetails.put(
											"Applicant-discription_Investments"
													+ i, discription3);
									assetDetails.put(
											"Applicant-value_Investments" + i,
											value3);
									assets.setDescription(discription3);
									assets.setValue(value3);
									assets.setOwnerShip(ownerShip3[i]);
									// dataStoreValue.put("ownership",ownership);
								} else {
									// ownership is comming as null
									Logger.debug("all values are not null and empty except ownership");
									String discription3 = (String) description3[i];
									String value3 = (String) values3[i];
									String ownership3 = (String) ownerShip3[i];
									// Logger.debug("Applicant Assets to be created .........");
									// aseetsApplicant.createAssetApplicant(applicantId,
									// "Investments",discription3, value3);
									// Logger.debug("Applicant Assets created for Investments withOut Value and OwnerShip");
									// Logic for CouchBase
									assetDetails.put(
											"Applicant-discription_Investments"
													+ i, discription3);
									assetDetails.put(
											"Applicant-value_Investments" + i,
											value3);
									assetDetails.put(
											"Applicant-ownership_Investments"
													+ i, ownership3);
									assets.setDescription(discription3);
									assets.setValue(value3);
									assets.setOwnerShip(ownerShip3[i]);
								}

							} else {
								Logger.debug("inside if Investments");

								if (ownerShip3[i].isEmpty()
										|| ownerShip3[i] == null) {
									String discription3 = "Investments name not filled";
									String value3 = "0";
									// String
									// ownership=(String)job.get("Ownership");

									// Logger.debug("Applicant Assets to be created .........");
									// aseetsApplicant.createAssetApplicant(applicantId,
									// "Investments",discription3, value3);
									// Loggeric for couchBase
									assets.setDescription(discription3);
									assets.setValue(value3);
									assets.setOwnerShip(ownerShip3[i]);
								} else {
									String discription3 = "Investments not filled";
									String value3 = "0";
									String ownership3 = (String) ownerShip3[i];

									Logger.debug("Applicant Assets to be created .........");
									// aseetsApplicant.createAssetApplicant(applicantId,
									// "RRSPs",discription3, value3);
									// Loggeric for couchBase
									assetDetails.put(
											"Applicant-value_Investments" + i,
											value3);
									assetDetails.put(
											"Applicant-ownership_Investments"
													+ i, ownership3);
									assets.setDescription(discription3);
									assets.setValue(value3);
									assets.setOwnerShip(ownerShip3[i]);
								}// End of inner else

							}// End of inner else
							vehicleArray.put(assetDetails);
							
							applicant.getAssetList().add(assets);
						}// End of Inner For Loop

					}// End of Investments

					Logger.debug(selectedValue.equalsIgnoreCase("Other")
							+ "<<<Other test");
					Logger.debug((selectedValue != null) + "Selected null test");

					if ((selectedValue.equalsIgnoreCase("Other"))
							&& (selectedValue != null)) {

						Logger.debug("Other");

						assets=new Assetes();
						assets.setType("Other");
						// String vehicles=dynamicForm.get("vehicles");
						// //vehicles
						// Logger.debug(vehicles);

						final String[] asset4 = request().body()
								.asFormUrlEncoded().get("asset4[]");
						final String[] description4 = request().body()
								.asFormUrlEncoded().get("description4[]");
						final String[] values4 = request().body()
								.asFormUrlEncoded().get("value4[]");
						final String[] ownerShip4 = request().body()
								.asFormUrlEncoded().get("designation4[]");
						aseetsApplicant = new CreateApplicant();

						for (int i = 0; i <= asset4.length - 1; i++) {
							Logger.debug("description4[" + i + "] = "
									+ description4[i]);
							Logger.debug("values4[" + i + "] = " + values4[i]);

							assetDetails = new JSONObject();
							assetDetails.put("Applicant-AssetType" + i,
									selectedValue);
							if ((description4[i] != null && !description4[i]
									.equals(""))
									&& (values4[i] != null && !values4[i]
											.equals(""))) {

								Logger.debug("inside if Other");
								Logger.debug("ownshipe4[" + i + "] = "
										+ ownerShip4[i]);
								if (ownerShip4[i].isEmpty()
										|| ownerShip4[i] == null) {
									Logger.debug("all values are not null and empty");
									String discription4 = (String) description4[i];
									String value4 = (String) values4[i];
									// String
									// ownership=(String)job.get("Ownership");
									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "other", discription4,
											value4);
									Logger.debug("Applicant Assets created for Other withOut Value and OwnerShip");
									// Logic for CouchBase
									assetDetails.put(
											"Applicant-discription_Other" + i,
											discription4);
									assetDetails.put("Applicant-value_Other"
											+ i, value4);
									
									assets.setDescription(discription4);
									assets.setValue(value4);
									assets.setOwnerShip(ownerShip4[i]);
									
									// dataStoreValue.put("ownership",ownership);
								} else {
									// ownership is comming as null
									Logger.debug("all values are not null and empty except ownership");
									String discription4 = (String) description4[i];
									String value4 = (String) values4[i];
									String ownership4 = (String) ownerShip4[i];
									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "other", discription4,
											value4);
									Logger.debug("Applicant Assets created for Other withOut Value and OwnerShip");
									// Logic for CouchBase
									assetDetails.put(
											"Applicant-discription_Other" + i,
											discription4);
									assetDetails.put("Applicant-value_Other"
											+ i, value4);
									assetDetails.put(
											"Applicant-ownership_Other" + i,
											ownership4);
									assets.setDescription(discription4);
									assets.setValue(value4);
									assets.setOwnerShip(ownerShip4[i]);
								}

							} else {
								Logger.debug("inside if Other");

								if (ownerShip4[i].isEmpty()
										|| ownerShip4[i] == null) {
									String discription4 = "Other name not filled";
									String value4 = "0";
									// String
									// ownership=(String)job.get("Ownership");

									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "other", discription4,
											value4);
									assets.setDescription(discription4);
									assets.setValue(value4);
									assets.setOwnerShip(ownerShip4[i]);
									// Loggeric for couchBase
								} else {
									String discription4 = "Other not filled";
									String value4 = "0";
									String ownership4 = (String) ownerShip4[i];

									Logger.debug("Applicant Assets to be created .........");
									aseetsApplicant.createAssetApplicant(
											applicantID, "other", discription4,
											value4);
									// Loggeric for couchBase
									assetDetails.put("Applicant-value_Other"
											+ i, value4);
									assetDetails.put(
											"Applicant-ownership_Other" + i,
											ownership4);
									assets.setDescription(discription4);
									assets.setValue(value4);
									assets.setOwnerShip(ownerShip4[i]);
								}// End of inner else

							}// End of inner else
							vehicleArray.put(assetDetails);

						applicant.getAssetList().add(assets);
						}// End of Inner For Loop

					}// End of Other

				}// End of For Loop

			}// End of else

			opportunity.setPogressStatus(85);
			storeData.storeDataInCouchBase(formType, opportunity);
			TotalAssets totalAssets = new TotalAssets();
			ArrayList<ApplicantProperties> listOfProperties = new ArrayList<ApplicantProperties>();

			String applicantName = session.get("applicantFirstName");
			String coApplicantName = session.get("co_applicantFirstName");

			return ok(mortgagePage11Properties.render("", applicantName,
					coApplicantName, listOfProperties));
		} catch (Exception e) {
			Logger.error("Error in mortgagePage10Assets", e);
			return ok("Some thing went wrong in mortgagePage10Assets");
		}
	}

	public static Result mortgagePage11Properties() {

		Logger.info("Inside mortgagePage11Properties");

		CouchBaseOperation storeData = new CouchBaseOperation();
		DynamicForm dynamicForm = form().bindFromRequest();
		List<ApplicantProperties> propertyList = new ArrayList<ApplicantProperties>();
		ApplicantProperties appProperties = null;
		PropertiesDBOperation propDBOperation = null;

		Session session = Http.Context.current().session();

		int crm_LeadId = 0;
		try {
			crm_LeadId = Integer.parseInt(session.get("crmLeadId")
					.toString());
		} catch (Exception e) {
			Logger.error("error in getting CRMLEADID from session id "
					+ crm_LeadId);
		}
		Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
				+ "");
		String applicantID = "";
		try {
			applicantID = session.get("applicantID");
		} catch (Exception e) {
			Logger.error("error in getting session value ");
		}
		try {
			String howManyProperties = dynamicForm.get("howManyProperties");
			if (howManyProperties != null
					&& howManyProperties.equalsIgnoreCase("one")) {
				Logger.info(" Inside  one  property selected.");
				appProperties = new ApplicantProperties();
				String address1 = dynamicForm.get("address0");
				String appxValue1 = dynamicForm.get("appxValue1");
				String mortgageValue1 = dynamicForm.get("mortgage1");
				String rentMo1 = dynamicForm.get("rentMo1");
				String condoFees1 = dynamicForm.get("condoFees1");
				String ownership1 = dynamicForm.get("ownership1");
				String selling1 = dynamicForm.get("agree1");
				Logger.debug("address1= " + address1 + "\n appxValue1= "
						+ appxValue1 + "\n mortgageValue1= " + mortgageValue1
						+ "\n rentMo1= " + rentMo1 + "\n condoFees1= "
						+ condoFees1 + "\n ownership1= " + ownership1
						+ "\n selling1= " + selling1);
				int monthlyRent = 0;
				if (rentMo1 != null) {
					try {
						monthlyRent = Integer.parseInt(rentMo1);
					} catch (Exception e) {
						Logger.debug("Error in parsing rentMo1");
					}
				}
				
				appProperties.setAddress(address1);
				appProperties.setAppxValue(appxValue1);
				appProperties.setMortgage(mortgageValue1);
				appProperties.setRentMo(monthlyRent);
				appProperties.setCondoFees(condoFees1);
				appProperties.setOwnership(ownership1);
				if (selling1 != null && selling1.equalsIgnoreCase("on")){
					appProperties.setSelling(true);

				}
				
				
			

				propertyList.add(appProperties);
				propDBOperation = new PropertiesDBOperation();
				propDBOperation.updateCBOpenErpProperties(propertyList,
						howManyProperties, applicantID,opportunity);
				
			
			} else if (howManyProperties != null
					&& howManyProperties.equalsIgnoreCase("two")) {
				Logger.debug("Property Two is selected");
				

				appProperties = new ApplicantProperties();
				String address21 = dynamicForm.get("currentAddress21");
				String appxValue21 = dynamicForm.get("appxValue21");
				String mortgageValue21 = dynamicForm.get("mortgage21");
				String rentMo21 = dynamicForm.get("rentMo21");
				String condoFees21 = dynamicForm.get("condoFees21");
				String ownership21 = dynamicForm.get("ownership21");
				String selling21 = dynamicForm.get("agreeTwo1");
				int monthlyRent21 = 0;
				if (rentMo21 != null) {
					monthlyRent21 = Integer.parseInt(rentMo21);
				}
				appProperties.setAddress(address21);
				appProperties.setAppxValue(appxValue21);
				appProperties.setMortgage(mortgageValue21);
				appProperties.setRentMo(monthlyRent21);
				appProperties.setCondoFees(condoFees21);
				appProperties.setOwnership(ownership21);
				if (selling21 != null && selling21.equalsIgnoreCase("on"))
					appProperties.setSelling(true);
				propertyList.add(appProperties);
				Logger.debug("address21= " + address21 + "\n appxValue21= "
						+ appxValue21 + "\n mortgageValue21= "
						+ mortgageValue21 + "\n rentMo21= " + rentMo21
						+ "\n condoFees21= " + condoFees21);

				ApplicantProperties appProperties2 = new ApplicantProperties();

				String address22 = dynamicForm.get("currentAddress22");
				String appxValue22 = dynamicForm.get("appxValue22");
				String mortgageValue22 = dynamicForm.get("mortgage22");
				String rentMo22 = dynamicForm.get("rentMo22");
				String condoFees22 = dynamicForm.get("condoFees22");
				String ownership22 = dynamicForm.get("ownership22");
				String selling22 = dynamicForm.get("agreeTwo2");

				int monthlyRent22 = 0;
				if (rentMo22 != null) {
					monthlyRent22 = Integer.parseInt(rentMo22);
				}
				appProperties2.setAddress(address22);
				appProperties2.setAppxValue(appxValue22);
				appProperties2.setMortgage(mortgageValue22);
				appProperties2.setRentMo(monthlyRent22);
				appProperties2.setCondoFees(condoFees22);
				appProperties2.setOwnership(ownership22);
				if (selling22 != null && selling22.equalsIgnoreCase("on"))
					appProperties2.setSelling(true);
				propertyList.add(appProperties2);
				Logger.debug("address22= " + address22 + "\n appxValue22= "
						+ appxValue22 + "\n mortgageValue21= "
						+ mortgageValue22 + "\n rentMo22= " + rentMo22
						+ "\n condoFees22= " + condoFees22 + "\n ownership22= "
						+ ownership22 + "\n selling22= " + selling22);
				propDBOperation = new PropertiesDBOperation();
				propDBOperation.updateCBOpenErpProperties(propertyList,
						howManyProperties, applicantID,opportunity);
				
				
				

			} else if (howManyProperties != null
					&& howManyProperties.equalsIgnoreCase("three")) {
				Logger.debug("Property Three is selected");
				appProperties = new ApplicantProperties();
				String address31 = dynamicForm.get("currentAddress31");
				String appxValue31 = dynamicForm.get("appxValue31");
				String mortgageValue31 = dynamicForm.get("mortgage31");
				String rentMo31 = dynamicForm.get("rentMo31");
				String condoFees31 = dynamicForm.get("condoFees31");
				String ownership31 = dynamicForm.get("ownership31");
				String selling31 = dynamicForm.get("agreeThree1");
				int monthlyRent31 = 0;
				if (rentMo31 != null) {
					monthlyRent31 = Integer.parseInt(rentMo31);
				}
				appProperties.setAddress(address31);
				appProperties.setAppxValue(appxValue31);
				appProperties.setMortgage(mortgageValue31);
				appProperties.setRentMo(monthlyRent31);
				appProperties.setCondoFees(condoFees31);
				appProperties.setOwnership(ownership31);
				if (selling31 != null && selling31.equalsIgnoreCase("on"))
					appProperties.setSelling(true);
				propertyList.add(appProperties);
				Logger.debug("address31= " + address31 + "\n appxValue31= "
						+ appxValue31 + "\n mortgageValue31= "
						+ mortgageValue31 + "\n rentMo31= " + rentMo31
						+ "\n condoFees31= " + condoFees31 + "\n ownership31 "
						+ ownership31 + "\n selling31 " + selling31);

				ApplicantProperties appProperties2 = new ApplicantProperties();
				String address32 = dynamicForm.get("currentAddress32");
				String appxValue32 = dynamicForm.get("appxValue32");
				String mortgageValue32 = dynamicForm.get("mortgage32");
				String rentMo32 = dynamicForm.get("rentMo32");
				String condoFees32 = dynamicForm.get("condoFees32");
				String ownership32 = dynamicForm.get("ownership32");
				String selling32 = dynamicForm.get("agreeThree2");
				int monthlyRent32 = 0;
				if (rentMo32 != null) {
					monthlyRent32 = Integer.parseInt(rentMo32);
				}
				appProperties2.setAddress(address32);
				appProperties2.setAppxValue(appxValue32);
				appProperties2.setMortgage(mortgageValue32);
				appProperties2.setRentMo(monthlyRent32);
				appProperties2.setCondoFees(condoFees32);
				appProperties2.setOwnership(ownership32);
				if (selling32 != null && selling32.equalsIgnoreCase("on"))
					appProperties2.setSelling(true);
				propertyList.add(appProperties2);
				Logger.debug("address32= " + address32 + "\n appxValue32= "
						+ appxValue32 + "\n mortgageValue32= "
						+ mortgageValue32 + "\n rentMo32= " + rentMo32
						+ "\n condoFees32= " + condoFees32 + "\n ownership32 "
						+ ownership32 + "\n selling32 " + selling32);

				ApplicantProperties appProperties3 = new ApplicantProperties();
				String address33 = dynamicForm.get("currentAddress33");
				String appxValue33 = dynamicForm.get("appxValue33");
				String mortgageValue33 = dynamicForm.get("mortgage33");
				String rentMo33 = dynamicForm.get("rentMo33");
				String condoFees33 = dynamicForm.get("condoFees33");
				String ownership33 = dynamicForm.get("ownership33");
				String selling33 = dynamicForm.get("agreeThree3");
				int monthlyRent33 = 0;
				if (rentMo33 != null) {
					monthlyRent33 = Integer.parseInt(rentMo33);
				}
				appProperties3.setAddress(address33);
				appProperties3.setAppxValue(appxValue33);
				appProperties3.setMortgage(mortgageValue33);
				appProperties3.setRentMo(monthlyRent33);
				appProperties3.setCondoFees(condoFees33);
				appProperties3.setOwnership(ownership33);
				if (selling33 != null && selling33.equalsIgnoreCase("on"))
					appProperties3.setSelling(true);
				propertyList.add(appProperties3);
				Logger.debug("address33= " + address33 + "\n appxValue33= "
						+ appxValue33 + "\n mortgageValue33= "
						+ mortgageValue33 + "\n rentMo33= " + rentMo33
						+ "\n condoFees33 " + condoFees33 + "\n ownership33 "
						+ ownership33 + "\n selling33 " + selling33);
				propDBOperation = new PropertiesDBOperation();
				propDBOperation.updateCBOpenErpProperties(propertyList,
						howManyProperties, applicantID,opportunity);
			} else if (howManyProperties != null
					&& howManyProperties.equalsIgnoreCase("four")) {
				Logger.debug("Property Four is selected");
				appProperties = new ApplicantProperties();
				String address41 = dynamicForm.get("currentAddress41");
				String appxValue41 = dynamicForm.get("appxValue41");
				String mortgageValue41 = dynamicForm.get("mortgage41");
				String rentMo41 = dynamicForm.get("rentMo41");
				String condoFees41 = dynamicForm.get("condoFees41");
				String ownership41 = dynamicForm.get("ownership41");
				String selling41 = dynamicForm.get("agreeFour1");
				int monthlyRent41 = 0;
				if (rentMo41 != null) {
					monthlyRent41 = Integer.parseInt(rentMo41);
				}
				appProperties.setAddress(address41);
				appProperties.setAppxValue(appxValue41);
				appProperties.setMortgage(mortgageValue41);
				appProperties.setRentMo(monthlyRent41);
				appProperties.setCondoFees(condoFees41);
				appProperties.setOwnership(ownership41);
				if (selling41 != null && selling41.equalsIgnoreCase("on"))
					appProperties.setSelling(true);
				propertyList.add(appProperties);
				Logger.debug("address41= " + address41 + "\n appxValue41= "
						+ appxValue41 + "\n mortgageValue41= "
						+ mortgageValue41 + "\n rentMo41= " + rentMo41
						+ "\n condoFees41= " + condoFees41 + "\n ownership41 "
						+ ownership41 + "\n selling41 " + selling41);

				ApplicantProperties appProperties2 = new ApplicantProperties();
				String address42 = dynamicForm.get("currentAddress42");
				String appxValue42 = dynamicForm.get("appxValue42");
				String mortgageValue42 = dynamicForm.get("mortgage42");
				String rentMo42 = dynamicForm.get("rentMo42");
				String condoFees42 = dynamicForm.get("condoFees42");
				String ownership42 = dynamicForm.get("ownership42");
				String selling42 = dynamicForm.get("agreeFour2");
				int monthlyRent42 = 0;
				if (rentMo42 != null) {
					monthlyRent42 = Integer.parseInt(rentMo42);
				}
				appProperties2.setAddress(address42);
				appProperties2.setAppxValue(appxValue42);
				appProperties2.setMortgage(mortgageValue42);
				appProperties2.setRentMo(monthlyRent42);
				appProperties2.setCondoFees(condoFees42);
				appProperties2.setOwnership(ownership42);
				if (selling42 != null && selling42.equalsIgnoreCase("on"))
					appProperties2.setSelling(true);
				propertyList.add(appProperties2);
				Logger.debug("address42= " + address42 + "\n appxValue42= "
						+ appxValue42 + "\n mortgageValue42= "
						+ mortgageValue42 + "\n rentMo42= " + rentMo42
						+ "\n condoFees42= " + condoFees42 + "\n ownership42 "
						+ ownership42 + "\n selling42 " + selling42);

				ApplicantProperties appProperties3 = new ApplicantProperties();
				String address43 = dynamicForm.get("currentAddress43");
				String appxValue43 = dynamicForm.get("appxValue43");
				String mortgageValue43 = dynamicForm.get("mortgage43");
				String rentMo43 = dynamicForm.get("rentMo43");
				String condoFees43 = dynamicForm.get("condoFees43");
				String ownership43 = dynamicForm.get("ownership43");
				String selling43 = dynamicForm.get("agreeFour3");
				int monthlyRent43 = 0;
				if (rentMo43 != null) {
					monthlyRent43 = Integer.parseInt(rentMo43);
				}
				appProperties3.setAddress(address43);
				appProperties3.setAppxValue(appxValue43);
				appProperties3.setMortgage(mortgageValue43);
				appProperties3.setRentMo(monthlyRent43);
				appProperties3.setCondoFees(condoFees43);
				appProperties3.setOwnership(ownership43);
				if (selling43 != null && selling43.equalsIgnoreCase("on"))
					appProperties3.setSelling(true);
				propertyList.add(appProperties3);
				Logger.debug("address43= " + address43 + "\n appxValue43= "
						+ appxValue43 + "\n mortgageValue43= "
						+ mortgageValue43 + "\n rentMo43= " + rentMo43
						+ "\n condoFees43 " + condoFees43 + "\n ownership43 "
						+ ownership43 + "\n selling43 " + selling43);

				ApplicantProperties appProperties4 = new ApplicantProperties();
				String address44 = dynamicForm.get("currentAddress44");
				String appxValue44 = dynamicForm.get("appxValue44");
				String mortgageValue44 = dynamicForm.get("mortgage44");
				String rentMo44 = dynamicForm.get("rentMo44");
				String condoFees44 = dynamicForm.get("condoFees44");
				String ownership44 = dynamicForm.get("ownership44");
				String selling44 = dynamicForm.get("agreeFour4");
				int monthlyRent44 = 0;
				if (rentMo44 != null) {
					monthlyRent44 = Integer.parseInt(rentMo44);
				}
				appProperties4.setAddress(address44);
				appProperties4.setAppxValue(appxValue44);
				appProperties4.setMortgage(mortgageValue44);
				appProperties4.setRentMo(monthlyRent44);
				appProperties4.setCondoFees(condoFees44);
				appProperties4.setOwnership(ownership44);
				if (selling44 != null && selling44.equalsIgnoreCase("on"))
					appProperties4.setSelling(true);
				propertyList.add(appProperties4);
				Logger.debug("address44= " + address44 + "\n appxValue44= "
						+ appxValue44 + "\n mortgageValue44= "
						+ mortgageValue44 + "\n rentMo44= " + rentMo44
						+ "\n condoFees44 " + condoFees44 + "\n ownership44 "
						+ ownership44 + "\n selling44 " + selling44);

				propDBOperation = new PropertiesDBOperation();
				propDBOperation.updateCBOpenErpProperties(propertyList,
						howManyProperties, applicantID,opportunity);

			} else if (howManyProperties != null
					&& howManyProperties.equalsIgnoreCase("more")) {

				Logger.debug("Property More is selected");
				appProperties = new ApplicantProperties();
				String address51 = dynamicForm.get("currentAddress51");
				String appxValue51 = dynamicForm.get("appxValue51");
				String mortgageValue51 = dynamicForm.get("mortgage51");
				String rentMo51 = dynamicForm.get("rentMo51");
				String condoFees51 = dynamicForm.get("condoFees51");
				String ownership51 = dynamicForm.get("ownership51");
				String selling51 = dynamicForm.get("agreeFive1");
				int monthlyRent51 = 0;
				if (rentMo51 != null && !rentMo51.equalsIgnoreCase("")) {
					monthlyRent51 = Integer.parseInt(rentMo51);
				}
				appProperties.setAddress(address51);
				appProperties.setAppxValue(appxValue51);
				appProperties.setMortgage(mortgageValue51);
				appProperties.setRentMo(monthlyRent51);
				appProperties.setCondoFees(condoFees51);
				appProperties.setOwnership(ownership51);
				if (selling51 != null && selling51.equalsIgnoreCase("on"))
					appProperties.setSelling(true);
				propertyList.add(appProperties);
				Logger.debug("address51= " + address51 + "\n appxValue51= "
						+ appxValue51 + "\n mortgageValue51= "
						+ mortgageValue51 + "\n rentMo51= " + rentMo51
						+ "\n condoFees51= " + condoFees51 + "\n ownership51 "
						+ ownership51 + "\n selling51 " + selling51);

				ApplicantProperties appProperties2 = new ApplicantProperties();
				String address52 = dynamicForm.get("currentAddress52");
				String appxValue52 = dynamicForm.get("appxValue52");
				String mortgageValue52 = dynamicForm.get("mortgage52");
				String rentMo52 = dynamicForm.get("rentMo52");
				String condoFees52 = dynamicForm.get("condoFees52");
				String ownership52 = dynamicForm.get("ownership52");
				String selling52 = dynamicForm.get("agreeFive2");
				int monthlyRent52 = 0;
				if (rentMo52 != null && !rentMo52.equalsIgnoreCase("")) {
					monthlyRent52 = Integer.parseInt(rentMo52);
				}
				appProperties2.setAddress(address52);
				appProperties2.setAppxValue(appxValue52);
				appProperties2.setMortgage(mortgageValue52);
				appProperties2.setRentMo(monthlyRent52);
				appProperties2.setCondoFees(condoFees52);
				appProperties2.setOwnership(ownership52);
				if (selling52 != null && selling52.equalsIgnoreCase("on"))
					appProperties2.setSelling(true);
				propertyList.add(appProperties2);
				Logger.debug("address52= " + address52 + "\n appxValue52= "
						+ appxValue52 + "\n mortgageValue52= "
						+ mortgageValue52 + "\n rentMo52= " + rentMo52
						+ "\n condoFees52= " + condoFees52 + "\n ownership52 "
						+ ownership52 + "\n selling52 " + selling52);

				ApplicantProperties appProperties3 = new ApplicantProperties();
				String address53 = dynamicForm.get("currentAddress53");
				String appxValue53 = dynamicForm.get("appxValue53");
				String mortgageValue53 = dynamicForm.get("mortgage53");
				String rentMo53 = dynamicForm.get("rentMo53");
				String condoFees53 = dynamicForm.get("condoFees53");
				String ownership53 = dynamicForm.get("ownership53");
				String selling53 = dynamicForm.get("agreeFive3");
				int monthlyRent53 = 0;
				if (rentMo53 != null && !rentMo53.equalsIgnoreCase("")) {
					monthlyRent53 = Integer.parseInt(rentMo53);
				}
				appProperties3.setAddress(address53);
				appProperties3.setAppxValue(appxValue53);
				appProperties3.setMortgage(mortgageValue53);
				appProperties3.setRentMo(monthlyRent53);
				appProperties3.setCondoFees(condoFees53);
				appProperties3.setOwnership(ownership53);
				if (selling53 != null && selling53.equalsIgnoreCase("on"))
					appProperties3.setSelling(true);
				propertyList.add(appProperties3);
				Logger.debug("address53= " + address53 + "\n appxValue53= "
						+ appxValue53 + "\n mortgageValue53= "
						+ mortgageValue53 + "\n rentMo53= " + rentMo53
						+ "\n condoFees53 " + condoFees53 + "\n ownership53 "
						+ ownership53 + "\n selling53 " + selling53);

				ApplicantProperties appProperties4 = new ApplicantProperties();
				String address54 = dynamicForm.get("currentAddress54");
				String appxValue54 = dynamicForm.get("appxValue54");
				String mortgageValue54 = dynamicForm.get("mortgage54");
				String rentMo54 = dynamicForm.get("rentMo54");
				String condoFees54 = dynamicForm.get("condoFees54");
				String ownership54 = dynamicForm.get("ownership54");
				String selling54 = dynamicForm.get("agreeFive4");
				int monthlyRent54 = 0;
				if (rentMo54 != null && !rentMo54.equalsIgnoreCase("")) {
					monthlyRent54 = Integer.parseInt(rentMo54);
				}
				appProperties4.setAddress(address54);
				appProperties4.setAppxValue(appxValue54);
				appProperties4.setMortgage(mortgageValue54);
				appProperties4.setRentMo(monthlyRent54);
				appProperties4.setCondoFees(condoFees54);
				appProperties4.setOwnership(ownership54);
				if (selling54 != null && selling54.equalsIgnoreCase("on"))
					appProperties4.setSelling(true);
				propertyList.add(appProperties4);
				Logger.debug("address54= " + address54 + "\n appxValue54= "
						+ appxValue54 + "\n mortgageValue54= "
						+ mortgageValue54 + "\n rentMo54= " + rentMo54
						+ "\n condoFees54 " + condoFees54 + "\n ownership54 "
						+ ownership54 + "\n selling54 " + selling54);

				ApplicantProperties appProperties5 = new ApplicantProperties();
				String address55 = dynamicForm.get("currentAddress55");
				String appxValue55 = dynamicForm.get("appxValue55");
				String mortgageValue55 = dynamicForm.get("mortgage55");
				String rentMo55 = dynamicForm.get("rentMo55");
				String condoFees55 = dynamicForm.get("condoFees55");
				String ownership55 = dynamicForm.get("ownership55");
				String selling55 = dynamicForm.get("agreeFive5");
				int monthlyRent55 = 0;
				if (rentMo55 != null && !rentMo55.equalsIgnoreCase("")) {
					monthlyRent55 = Integer.parseInt(rentMo55);
				}
				appProperties5.setAddress(address55);
				appProperties5.setAppxValue(appxValue55);
				appProperties5.setMortgage(mortgageValue55);
				appProperties5.setRentMo(monthlyRent55);
				appProperties5.setCondoFees(condoFees55);
				appProperties5.setOwnership(ownership55);
				if (selling55 != null && selling55.equalsIgnoreCase("on"))
					appProperties5.setSelling(true);
				propertyList.add(appProperties5);
				Logger.debug("address55= " + address55 + "\n appxValue55= "
						+ appxValue55 + "\n mortgageValue55= "
						+ mortgageValue55 + "\n rentMo55= " + rentMo55
						+ "\n condoFees55 " + condoFees55 + "\n ownership55 "
						+ ownership55 + "\n selling55 " + selling55);

				ApplicantProperties appProperties6 = new ApplicantProperties();
				String address56 = dynamicForm.get("currentAddress56");
				String appxValue56 = dynamicForm.get("appxValue56");
				String mortgageValue56 = dynamicForm.get("mortgage56");
				String rentMo56 = dynamicForm.get("rentMo56");
				String condoFees56 = dynamicForm.get("condoFees56");
				String ownership56 = dynamicForm.get("ownership56");
				String selling56 = dynamicForm.get("agreeFive6");
				int monthlyRent56 = 0;
				if (rentMo56 != null && !rentMo56.equalsIgnoreCase("")) {
					monthlyRent56 = Integer.parseInt(rentMo56);
				}
				appProperties6.setAddress(address56);
				appProperties6.setAppxValue(appxValue56);
				appProperties6.setMortgage(mortgageValue56);
				appProperties6.setRentMo(monthlyRent56);
				appProperties6.setCondoFees(condoFees56);
				appProperties6.setOwnership(ownership56);
				if (selling56 != null && selling56.equalsIgnoreCase("on"))
					appProperties6.setSelling(true);
				propertyList.add(appProperties6);
				Logger.debug("address56= " + address56 + "\n appxValue56= "
						+ appxValue56 + "\n mortgageValue56= "
						+ mortgageValue56 + "\n rentMo56= " + rentMo56
						+ "\n condoFees56 " + condoFees56 + "\n ownership56 "
						+ ownership56 + "\n selling56 " + selling56);

				ApplicantProperties appProperties7 = new ApplicantProperties();
				String address57 = dynamicForm.get("currentAddress57");
				String appxValue57 = dynamicForm.get("appxValue57");
				String mortgageValue57 = dynamicForm.get("mortgage57");
				String rentMo57 = dynamicForm.get("rentMo57");
				String condoFees57 = dynamicForm.get("condoFees57");
				String ownership57 = dynamicForm.get("ownership57");
				String selling57 = dynamicForm.get("agreeFive7");
				int monthlyRent57 = 0;
				if (rentMo57 != null && !rentMo57.equalsIgnoreCase("")) {
					monthlyRent57 = Integer.parseInt(rentMo57);
				}
				appProperties7.setAddress(address57);
				appProperties7.setAppxValue(appxValue57);
				appProperties7.setMortgage(mortgageValue57);
				appProperties7.setRentMo(monthlyRent57);
				appProperties7.setCondoFees(condoFees57);
				appProperties7.setOwnership(ownership57);
				if (selling57 != null && selling57.equalsIgnoreCase("on"))
					appProperties7.setSelling(true);
				propertyList.add(appProperties7);
				Logger.debug("address57= " + address57 + "\n appxValue57= "
						+ appxValue57 + "\n mortgageValue57= "
						+ mortgageValue57 + "\n rentMo57= " + rentMo57
						+ "\n condoFees57 " + condoFees57 + "\n ownership57 "
						+ ownership57 + "\n selling57 " + selling57);

				propDBOperation = new PropertiesDBOperation();
				propDBOperation.updateCBOpenErpProperties(propertyList,
						howManyProperties, applicantID,opportunity);
			}
			
			String additionalApplicant1 = "";
			try {
				
				additionalApplicant1 =opportunity.getIsAdditionalApplicantExist();

			} catch (JSONException json) {
				Logger.error("Error when reading leading goal from couchbase ",
						json);
			}
			String applicantName = "";
			String coApplicantName = "";
			try {

				applicantName = (String) session.get("applicantFirstName");
				coApplicantName = (String) session.get("co_applicantFirstName");
			} catch (NullPointerException nlp) {
				Logger.error(
						"Error when reading applicant and co applicant name from session.",
						nlp);
			}
			return ok(mortgagePage12Disclose.render("", additionalApplicant1,
					7, applicantName, coApplicantName));
		} catch (Exception e) {
			Logger.error("Error in mortgagePage11Properties", e);
			return ok("Something went wrong in mortgagePage11Properties...");
		}

	}

	public static Result mortgagePage6a() {
		Logger.info(" Inside mortgagePage6a ");
		DynamicForm dynamicForm = form().bindFromRequest();
		PersonalInfoDBOperation personalInfoDB = null;
		PersonalInfo personalInfo = new PersonalInfo();
		String applicantID = "";
		String additionalApplicants = "";
		CouchBaseOperation storeData=new CouchBaseOperation();

		Session session = Http.Context.current().session();
		int crm_LeadId = 0;
		try {
			crm_LeadId = Integer.parseInt(session.get("crmLeadId")
					.toString());
		} catch (Exception e) {
			Logger.error("error in getting CRMLEADID from session id "
					+ crm_LeadId);
		}
		Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
				+ "");
		
		try {
			applicantID = (String) session.get("applicantID");
			additionalApplicants = session.get("additionalApplicants");
		} catch (Exception e) {
			Logger.error("Error in getting values from session " + e);
		}
		try {
			String mobilePhone = dynamicForm.get("applMobPhone");
			String workPhone = dynamicForm.get("applWorkPhone");
			String homePhone = dynamicForm.get("applHomePhone");
			String inputBirthDay = dynamicForm.get("applBirthday");
			String insurance = dynamicForm.get("applInsurNum");
			String relationshipStatus = dynamicForm.get("appRelStatus");
			String dependant = dynamicForm.get("applDependants");
			String refareYouCanada = dynamicForm.get("areYouCanadianRess");
			String movedCanadas = dynamicForm.get("movedCanadas");

			personalInfo.setMobilePhone(mobilePhone);
			personalInfo.setWorkPhone(workPhone);
			personalInfo.setHomePhone(homePhone);
			personalInfo.setBirthDay(inputBirthDay);
			personalInfo.setSocialInsurance(insurance);
			personalInfo.setRelationStatus(relationshipStatus);
			personalInfo.setDependents(dependant);
			personalInfo.setAreUCanadianRes(refareYouCanada);
			personalInfo.setMovedCanada(movedCanadas);
			personalInfo.setAdditionalApplicant(additionalApplicants);

			String applicantName = (String) session.get("applicantName");
			String coApplicantName = (String) session.get("coApplicantName");
			personalInfo.setApplicantName(applicantName);
			personalInfo.setCoApplicantName(coApplicantName);
			// get ip of latest form sumitted
			personalInfoDB = new PersonalInfoDBOperation(applicantID);
			personalInfoDB.updatePersonalInfo(personalInfo,opportunity);

			ApplicantAddressParameter7 address = new ApplicantAddressParameter7();
			address.setApplicantName(applicantName);
			if (additionalApplicants != null
					&& additionalApplicants.equalsIgnoreCase("yes"))
				return ok(mortgagePage6b.render(personalInfo));
			else
				return ok(mortgagePage7a.render(address));
		} catch (Exception e) {
			Logger.error("Error in mortgagePage6a" + e);
			return ok("some thing went wrong in mortgagePage6a");
		}
	}

	public static Result mortgagePage6b() {
		Logger.info(" Inside mortgagePage6b ");
		DynamicForm dynamicForm = form().bindFromRequest();
		CreateApplicant updateApplicant = new CreateApplicant();
		PersonalInfo personalInfo = new PersonalInfo();
		PersonalInfoDBOperation personalInfoDB = null;
		CouchBaseOperation storeData=new CouchBaseOperation();
		Session session = Http.Context.current().session();

		int crm_LeadId = 0;
		try {
			crm_LeadId = Integer.parseInt(session.get("crmLeadId")
					.toString());
		} catch (Exception e) {
			Logger.error("error in getting CRMLEADID from session id "
					+ crm_LeadId);
		}
		Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
				+ "");
		try {
			String formType = "Mortgage Application";
			String subForm = "Mortgage Application 6";

			String CoMobilePhone = dynamicForm.get("coApplMobPhone");
			String CoAppWorkPhone = dynamicForm.get("coApplWorkPhone");
			String CoAppHomePhone = dynamicForm.get("coApplHomePhone");
			String inputCoAppBirthday = dynamicForm.get("coApplBirthday");
			String CoAppinsurance = dynamicForm.get("coApplInsurNum");
			String CoApprelationshipStatus = dynamicForm.get("coAppRelStatus");
			String CoAppdependant = dynamicForm.get("coAppDependants");
			String CoApprefareYouCanada = dynamicForm.get("coApplicantss");
			String CoAppmovedCanadas = dynamicForm.get("coAppMovedCanadae");

			personalInfo.setCoMobilePhone(CoMobilePhone);
			personalInfo.setCoWorkPhone(CoAppWorkPhone);
			personalInfo.setCoHomePhone(CoAppHomePhone);
			personalInfo.setCoBirthDay(inputCoAppBirthday);
			personalInfo.setCoSocialInsurance(CoAppinsurance);
			personalInfo.setCoRelationStatus(CoApprelationshipStatus);
			personalInfo.setCoDependents(CoAppdependant);
			personalInfo.setCoAreUCanadianRes(CoApprefareYouCanada);
			personalInfo.setCoMovedCanada(CoAppmovedCanadas);
			Logger.debug("CoAppBirthday " + inputCoAppBirthday
					+ "CoMobilePhone " + CoMobilePhone + "CoAppWorkPhone "
					+ CoAppWorkPhone + "CoAppHomePhone " + CoAppHomePhone
					+ "CoAppinsurance " + CoAppinsurance
					+ "CoApprelationshipStatus " + CoApprelationshipStatus
					+ "CoAppdependant " + CoAppdependant
					+ "CoApprefareYouCanada " + CoApprefareYouCanada
					+ "CoAppmovedCanadas " + CoAppmovedCanadas);
			// get ip of latest form sumitted
			String ip = request().remoteAddress();
			String applicantId2 = "";
			try {
				applicantId2 = (String) session.get("applicantID2");
			} catch (Exception e) {
				Logger.error("Error in gettin session applicant Value" + e);
			}
			personalInfoDB = new PersonalInfoDBOperation(applicantId2);
			personalInfoDB.updateCoApplicantPersonalInfo(personalInfo,
					applicantId2,opportunity);
			ApplicantAddressParameter7 address = new ApplicantAddressParameter7();
			String applicantName = (String) session.get("applicantName");
			String coApplicantName = (String) session.get("coApplicantName");
			address.setApplicantName(applicantName);
			return ok(mortgagePage7a.render(address));
		} catch (Exception e) {
			Logger.error("Error in mortgagePage6b " + e);
			return ok("Some thing went wrong in mortgagePage6b");
		}
	}

	public static Result mortgagePage7a() {
		Logger.info("Inside mortgagePage7a");
		DynamicForm dynamicForm = form().bindFromRequest();
		CreateApplicant cLead;
		try {
			Address addressSplit = new Address();
			AddressGroup currentaddressObj = null;
			AddressGroup prioraddress1Obj = null;
			AddressGroup prioraddress2Obj = null;

			String subForm = "Mortgage Application 7";
			String additionalApplicants = dynamicForm
					.get("additionalApplicants");
			Logger.info("additionalApplicants   " + additionalApplicants);
			CouchBaseOperation storeData=new CouchBaseOperation();
			Session session = Http.Context.current().session();

			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
					+ "");

			String applicantID = "";
			try {
				applicantID = (String) session.get("applicantID");
				Logger.debug("applicantID " + applicantID);
			} catch (Exception e) {
				Logger.error("Error in  reading data from session " + e);
			}

			Applicant  applicant=opportunity.getApplicants().get(0);
			
			controllers.Address applicantAddress=null;
			ArrayList<AddressGroup> listOfAddresses = new ArrayList<AddressGroup>();
			String currentAddress = dynamicForm.get("currentAddress1");
			String inputMovedIn1 = dynamicForm.get("movedIn1");
			String currentSumMonth = dynamicForm.get("currentaddressmonthsum");
			String totalcurrentMonths = dynamicForm.get("totalcurrentmonths");

			DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
			Date movedIn1 = null;
			try {
				movedIn1 = df2.parse(inputMovedIn1);
			} catch (ParseException e) {
				Logger.error("Error in parsing string to date");
			}
			Logger.debug("birthday>>>>>>>>>>>>>>>>>>" + movedIn1
					+ "<<<<<<<<<<<<<<<<<");

			int totalcurrentMonthsInt = 0;
			Logger.debug("*********inside  currentAddress************ "
					+ "\n currentAddress" + currentAddress
					+ "\n input MovedIn1 " + inputMovedIn1
					+ "After string-to-date movedIn1 " + movedIn1);

			Logger.debug("currentSumMonth" + currentSumMonth);
			Logger.debug("totalcurrentMonths" + totalcurrentMonths);
			int totalSumMonths = 0;
			try {
				totalSumMonths = Integer.parseInt(totalcurrentMonths);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (currentAddress != null) {
				currentaddressObj = new AddressGroup(currentAddress,
						currentSumMonth, totalcurrentMonths);
				listOfAddresses.add(currentaddressObj);
				HashMap currentAddressSplit = addressSplit
						.getProperAddress(currentAddress);

				String name = null;
				String city = null;
				String province = null;
				String postalcode = null;
				if (currentAddressSplit != null) {
					name = (String) currentAddressSplit.get("address1");
					city = (String) currentAddressSplit.get("city");
					province = (String) currentAddressSplit.get("Province");
					postalcode = (String) currentAddressSplit.get("postalcode");
					// creating Applicant
					cLead = new CreateApplicant();

					cLead.createApplicantAddress(applicantID, name, city,
							province, postalcode, movedIn1);
					Logger.debug("Applicant  created with currentAddressSplit ");
				}
				applicantAddress=new controllers.Address();
				
				applicantAddress.setName(name);
				applicantAddress.setCity(city);
				applicantAddress.setMovedIn(movedIn1);
				applicantAddress.setPostalCode(postalcode);
				applicantAddress.setProvience(province);
				applicantAddress.setMonths(Integer.valueOf(currentSumMonth));
				applicant.getListOfAddress().add(applicantAddress);
			}
			if ((totalcurrentMonths != null && totalcurrentMonths.length() != 0 && !totalcurrentMonths
					.equals("")) && totalcurrentMonthsInt <= 36) {

				Logger.debug("**********inside 1st priorAddress1 *******************");

				String priorAddress1 = dynamicForm.get("currentAddress2");
				String inputMovedIn2 = dynamicForm.get("movedIn2");

				String priorSumMonth1 = dynamicForm.get("priormonthsum1");
				String totalpriorcurrentmonths1 = dynamicForm
						.get("totalpriormonths1");
				int totalpriorcurrentmonths1Int = 0;
				try {
					totalpriorcurrentmonths1Int = Integer
							.parseInt(totalpriorcurrentmonths1);
				} catch (Exception e) {
					Logger.error("exception in converting totalpriorcurrentmonths to int : "
							+ e);
				}
				Date movedIn2 = null;
				try {
					movedIn2 = df2.parse(inputMovedIn2);
				} catch (ParseException e) {
					Logger.error("Error in parsing string to date");
				}

				Logger.debug("priorAddress1" + priorAddress1);
				Logger.debug("inputMovedIn1 " + inputMovedIn1);
				Logger.debug("after string-2-date movedIn2" + movedIn2);
				Logger.debug("movedIn2>>>>>>>>>>>>>>>>>>" + movedIn2
						+ "<<<<<<<<<<<<<<<<<");

				Logger.debug("priorSumMonth1" + priorSumMonth1);
				Logger.debug("totalpriorcurrentmonths1"
						+ totalpriorcurrentmonths1);

				// New Logic For prior Date 1
				Logger.debug("totalMonths:" + totalpriorcurrentmonths1);

				Logger.debug("TotalDate is:" + totalpriorcurrentmonths1);

				if (priorAddress1 != null) {
					// currentaddressObj = new
					// AddressGroup(coAppcurrentAddress,currentYear,currentMonths,currentSumMonth,totalcurrentMonths);
					prioraddress1Obj = new AddressGroup(priorAddress1,
							priorSumMonth1, totalpriorcurrentmonths1);
					// listOfAddresses.add(currentaddressObj);
					listOfAddresses.add(prioraddress1Obj);
					HashMap currentAddressSplit = addressSplit
							.getProperAddress(priorAddress1);

					String name = null;
					String city = null;
					String province = null;
					String postalcode = null;
					if (currentAddressSplit != null) {
						name = (String) currentAddressSplit.get("address1");
						city = (String) currentAddressSplit.get("city");
						province = (String) currentAddressSplit.get("Province");
						postalcode = (String) currentAddressSplit
								.get("postalcode");
						// Creating Applicant
						cLead = new CreateApplicant();

						// Loggeric for date
						cLead.createApplicantAddress(applicantID, name, city,
								province, postalcode, movedIn2);

						Logger.debug("Going to OpenERP Create Applicant address ");

						applicantAddress=new controllers.Address();
						
						applicantAddress.setName(name);
						applicantAddress.setCity(city);
						applicantAddress.setMovedIn(movedIn2);
						applicantAddress.setPostalCode(postalcode);
						applicantAddress.setProvience(province);
						applicantAddress.setMonths(Integer.valueOf(priorSumMonth1));
						applicant.getListOfAddress().add(applicantAddress);
					}// spliting address

				}// checking prior address is not null

				String totalpriorcurrentmonths2 = dynamicForm
						.get("totalpriormonths2");
				int totalpriorcurrentmonths2Int = 0;
				try {
					totalpriorcurrentmonths2Int = Integer
							.parseInt(totalpriorcurrentmonths2);
				} catch (Exception e) {
					Logger.error("Error when parsing totalpriorcurrentmonths from string to int"
							+ e);
				}
				if (totalpriorcurrentmonths2Int <= 36
						&& (totalpriorcurrentmonths2 != null && !totalpriorcurrentmonths2
								.equals(""))) {

					Logger.debug("taking 2nd prior address");
					String priorAddress2 = dynamicForm.get("currentAddress3");
					String inputMovedIn3 = dynamicForm.get("movedIn3");
					String priorSumMonth2 = dynamicForm.get("priormonthsum2");

					Date movedIn3 = null;
					try {
						movedIn3 = df2.parse(inputMovedIn3);
					} catch (ParseException e) {
						Logger.error("Error in parsing string to date");
					}
					Logger.debug("********** inside 1st priorAddress2 *******************");
					Logger.debug("priorAddress2" + priorAddress2);
					Logger.debug("inputMovedIn3 " + inputMovedIn3);
					Logger.debug("Affter string-2-date movedIn3 " + movedIn3);
					Logger.debug("priorSumMonth2" + priorSumMonth2);
					// New Loggeric For prior Date 2
					Logger.debug("totalpriorcurrentmonths2:"
							+ totalpriorcurrentmonths2);

					Logger.debug("TotalDate is:" + totalpriorcurrentmonths2Int);

					Calendar prical2 = Calendar.getInstance();
					prical2.add(Calendar.MONTH, -totalpriorcurrentmonths2Int);
					String currentDateTimenew2 = (df2.format(prical2.getTime()));

					Logger.debug("CurrentDate for Prior Address 2 is:"
							+ currentDateTimenew2);

					if (priorAddress2 != null) {

						prioraddress2Obj = new AddressGroup(priorAddress2,
								priorSumMonth2, totalpriorcurrentmonths2);
						listOfAddresses.add(prioraddress2Obj);

						HashMap currentAddressSplit = addressSplit
								.getProperAddress(priorAddress2);

						String name = null;
						String city = null;
						String province = null;
						String postalcode = null;
						if (currentAddressSplit != null) {
							name = (String) currentAddressSplit.get("address1");
							city = (String) currentAddressSplit.get("city");
							province = (String) currentAddressSplit
									.get("Province");
							postalcode = (String) currentAddressSplit
									.get("postalcode");
							cLead = new CreateApplicant();

							cLead.createApplicantAddress(applicantID, name,
									city, province, postalcode, movedIn3);
							Logger.debug("Applicant created with priorAddress2 ");

							Logger.debug("CouchBase data is appending...");
							applicantAddress=new controllers.Address();
							
							applicantAddress.setName(name);
							applicantAddress.setCity(city);
							applicantAddress.setMovedIn(movedIn3);
							applicantAddress.setPostalCode(postalcode);
							applicantAddress.setProvience(province);
							applicantAddress.setMonths(Integer.valueOf(priorSumMonth2));
							applicant.getListOfAddress().add(applicantAddress);
						}// spliting prior address 2
					}// checking prior address 2 is not null
				}// end of Loggeric of prior address 2

			}
		String formType="MortgageForm7a";
	storeData.storeDataInCouchBase(formType, opportunity);
			Logger.debug("Data created in coucbase for MortgageForm7");

			// req.setAttribute("uniid",uniid);
			// req.setAttribute("applicantId",applicantId);

			CoApplicantAddressParameter7 coApplicantAddress = new CoApplicantAddressParameter7();
			String additionalApplicant = "yes";
			String coApplicantName = (String) session
					.get("co_applicantFirstName");

			coApplicantAddress.setCoApplicantName(coApplicantName);
			String applicantName = (String) session.get("applicantFirstName");
			if (additionalApplicant != null
					&& additionalApplicant.equalsIgnoreCase("yes"))
				return ok(mortgagePage7b.render(coApplicantAddress));
			else
				return ok(mortgagePage8.render("",
						new EmployeIncomeTypeParam(),
						new SelfEmployeIncomeTypeParam(),
						new PensionIncomeTypeParam(),
						new InvestmentsIncomeTypeParam(),
						new MaternityIncomeTypeParam(),
						new VehicleAllowIncomeTypeParam(),
						new LivingAllowIncomeTypeParam(),
						new CommissionIncomeTypeParam(),
						new BonusIncomeTypeParam(), new OtherIncomeTypeParam(),
						applicantName));
		} catch (Exception e) {
			Logger.error("Error in mortgagePage7a " + e);
			return ok("some thing went wrong in mortgagePage7a");
		}
	}

	public static Result mortgagePage7b() {
		Logger.info("Inside mortgagePage7b");
		DynamicForm dynamicForm = form().bindFromRequest();
		CreateApplicant cLead;
		CouchBaseOperation storeData=new CouchBaseOperation();
		Session session = Http.Context.current().session();

		int crm_LeadId = 0;
		try {
			crm_LeadId = Integer.parseInt(session.get("crmLeadId")
					.toString());
		} catch (Exception e) {
			Logger.error("error in getting CRMLEADID from session id "
					+ crm_LeadId);
		}
		Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
				+ "");		try {

					
					Applicant applicant=opportunity.getApplicants().get(1);
					controllers.Address applicantAddress=null;
			AddressGroup currentaddress4Obj = null;
			AddressGroup prioraddress5Obj = null;
			AddressGroup prioraddress6Obj = null;
			Address addressSplit = new Address();
			ArrayList<AddressGroup> listOfAddresses = new ArrayList<AddressGroup>();

			String subForm = "Mortgage Application 7b";

			String applicantID = "";
			String coApplicantID = "";

			try {
				applicantID = (String) session.get("applicantID");
				coApplicantID = session.get("applicantID2");
			} catch (Exception e) {
				Logger.error("Error in getting values from session " + e);
			}

			String coAppcurrentAddress = dynamicForm.get("CoCurrentAddress1");
			String coAppInputMovedIn1 = dynamicForm.get("CoMovedIn1");

			String coAppCurrentSumMonth = dynamicForm
					.get("coAppcurrentaddressmonthsum");
			String coAppTotalcurrentMonths = dynamicForm
					.get("coAppTotalcurrentMonths");
			DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
			df2 = new SimpleDateFormat("MM/dd/yyyy");
			Date coMovedIn1 = null;
			try {
				coMovedIn1 = df2.parse(coAppInputMovedIn1);
			} catch (ParseException e) {
				Logger.error("Error in parsing string to date", e);
			}
			Logger.debug("birthday" + coMovedIn1);

			int coApptotalcurrentMonthsInt = 0;
			Logger.debug("*********inside  coAppcurrentAddress************ ");
			Logger.debug("coAppcurrentAddress" + coAppcurrentAddress);
			Logger.debug("input MovedIn1 " + coAppInputMovedIn1);
			Logger.debug("After string-to-date movedIn1 " + coMovedIn1);

			Logger.debug("coAppCurrentSumMonth" + coAppCurrentSumMonth);
			Logger.debug("coAppTotalcurrentMonths" + coAppTotalcurrentMonths);

			int coApptotalSumMonths = 0;
			try {
				coApptotalSumMonths = Integer.parseInt(coAppTotalcurrentMonths);
			} catch (Exception e) {
				Logger.error(
						"Error in parsing string to int coAppTotalcurrentMonths",
						e);
			}

			if (coAppcurrentAddress != null) {

				currentaddress4Obj = new AddressGroup(coAppcurrentAddress,
						coAppCurrentSumMonth, coAppTotalcurrentMonths);
				listOfAddresses.add(currentaddress4Obj);
				HashMap currentAddressSplit = addressSplit
						.getProperAddress(coAppcurrentAddress);

				String name = null;
				String city = null;
				String province = null;
				String postalcode = null;
				if (currentAddressSplit != null) {
					name = (String) currentAddressSplit.get("address1");
					city = (String) currentAddressSplit.get("city");
					province = (String) currentAddressSplit.get("Province");
					postalcode = (String) currentAddressSplit.get("postalcode");
					// creating Applicant
					cLead = new CreateApplicant();

					cLead.createApplicantAddress(coApplicantID, name, city,
							province, postalcode, coMovedIn1);
					Logger.debug("Applicant  created with currentAddressSplit ");
				}
				applicantAddress=new controllers.Address();
				
				applicantAddress.setName(name);
				applicantAddress.setCity(city);
				applicantAddress.setMovedIn(coMovedIn1);
				applicantAddress.setPostalCode(postalcode);
				applicantAddress.setProvience(province);
				applicantAddress.setMonths(Integer.valueOf(coApptotalSumMonths));
				applicant.getListOfAddress().add(applicantAddress);
			}

			if ((coAppTotalcurrentMonths != null
					&& coAppTotalcurrentMonths.length() != 0 && !coAppTotalcurrentMonths
						.equals("")) && coApptotalcurrentMonthsInt <= 36) {
				Logger.debug("**********inside 1st coAppPriorAddress1 *************");

				String coAppPriorAddress1 = dynamicForm
						.get("CoCurrentAddress2");
				String coAppInputMovedIn2 = dynamicForm.get("CoMovedIn2");

				String coAppPriorSumMonth1 = dynamicForm
						.get("coAppPriorSumMonth1");
				String coAppTotalpriorcurrentmonths1 = dynamicForm
						.get("coAppTotalpriorcurrentmonths1");
				int totalpriorcurrentmonths1Int = 0;
				try {
					totalpriorcurrentmonths1Int = Integer
							.parseInt(coAppTotalpriorcurrentmonths1);
				} catch (Exception e) {
					Logger.error("exception in converting totalpriorcurrentmonths to int : "
							+ e);
				}
				Date coMovedIn2 = null;
				try {
					coMovedIn2 = df2.parse(coAppInputMovedIn2);
				} catch (ParseException e) {
					Logger.error("Error in parsing string to date");
				}

				Logger.debug("coAppPriorAddress1" + coAppPriorAddress1);
				Logger.debug("coAppInputMovedIn1 " + coAppInputMovedIn2);
				Logger.debug("after string-2-date movedIn2" + coMovedIn2);
				Logger.debug("comovedIn2>>> " + coMovedIn2);

				Logger.debug("coAppPriorSumMonth1" + coAppPriorSumMonth1);
				Logger.debug("coAppTotalpriorcurrentmonths1"
						+ coAppTotalpriorcurrentmonths1);

				// New Logic For prior Date 1
				Logger.debug("coAppTotalpriorcurrentmonths1:"
						+ coAppTotalpriorcurrentmonths1);

				Logger.debug("TotalDate is:" + coAppTotalpriorcurrentmonths1);

				if (coAppPriorAddress1 != null) {
					// currentaddressObj = new
					// AddressGroup(coAppcurrentAddress,currentYear,currentMonths,coAppCurrentSumMonth,coAppTotalcurrentMonths);
					prioraddress5Obj = new AddressGroup(coAppPriorAddress1,
							coAppPriorSumMonth1, coAppTotalpriorcurrentmonths1);
					// listOfAddresses.add(currentaddressObj);
					listOfAddresses.add(prioraddress5Obj);
					HashMap currentAddressSplit = addressSplit
							.getProperAddress(coAppPriorAddress1);

					String name = null;
					String city = null;
					String province = null;
					String postalcode = null;
					if (currentAddressSplit != null) {
						name = (String) currentAddressSplit.get("address1");
						city = (String) currentAddressSplit.get("city");
						province = (String) currentAddressSplit.get("Province");
						postalcode = (String) currentAddressSplit
								.get("postalcode");
						// Creating Applicant
						cLead = new CreateApplicant();

						// Loggeric for date
						cLead.createApplicantAddress(coApplicantID, name, city,
								province, postalcode, coMovedIn2);

						Logger.debug("Going to OpenERP Create Applicant address ");

						applicantAddress=new controllers.Address();
						
						applicantAddress.setName(name);
						applicantAddress.setCity(city);
						applicantAddress.setMovedIn(coMovedIn2);
						applicantAddress.setPostalCode(postalcode);
						applicantAddress.setProvience(province);
						applicantAddress.setMonths(Integer.valueOf(totalpriorcurrentmonths1Int));
						applicant.getListOfAddress().add(applicantAddress);

					}// spliting address

				}// checking prior address is not null
				String coApptotalpriorcurrentmonths2 = dynamicForm
						.get("coApptotalpriorcurrentmonths2");
				int totalpriorcurrentmonths2Int = 0;
				try {
					totalpriorcurrentmonths2Int = Integer
							.parseInt(coApptotalpriorcurrentmonths2);
				} catch (Exception e) {
					Logger.error(
							"Error when parsing totalpriorcurrentmonths from string to int",
							e);
				}
				if (totalpriorcurrentmonths2Int <= 36
						&& (coApptotalpriorcurrentmonths2 != null && !coApptotalpriorcurrentmonths2
								.equals(""))) {

					Logger.debug("taking 2nd prior address");
					String coApppriorAddress2 = dynamicForm
							.get("CoCurrentAddress4");
					String coAppInputMovedIn3 = dynamicForm.get("CoMovedIn3");
					String coApppriorSumMonth2 = dynamicForm
							.get("coApppriorSumMonth2");

					Date coMovedIn3 = null;
					try {
						coMovedIn3 = df2.parse(coAppInputMovedIn3);
					} catch (ParseException e) {
						Logger.error("Error in parsing string to date");
					}
					Logger.debug("********** inside 1st co priorAddress2 *******************");
					Logger.debug("coApppriorAddress2" + coApppriorAddress2);
					Logger.debug("coAppInputMovedIn3 " + coAppInputMovedIn3);
					Logger.debug("Affter string-2-date coMovedIn3 "
							+ coMovedIn3);
					Logger.debug("coApppriorSumMonth2" + coApppriorSumMonth2);
					// New Loggeric For prior Date 2
					Logger.debug("coApptotalpriorcurrentmonths2:"
							+ coApptotalpriorcurrentmonths2);

					Logger.debug("TotalDate is:" + totalpriorcurrentmonths2Int);

					Calendar prical2 = Calendar.getInstance();
					prical2.add(Calendar.MONTH, -totalpriorcurrentmonths2Int);
					String currentDateTimenew2 = (df2.format(prical2.getTime()));

					Logger.debug("CurrentDate for co app Prior Address 2 is:"
							+ currentDateTimenew2);

					if (coApppriorAddress2 != null) {

						prioraddress6Obj = new AddressGroup(coApppriorAddress2,
								coApppriorSumMonth2,
								coApptotalpriorcurrentmonths2);
						listOfAddresses.add(prioraddress6Obj);

						HashMap currentAddressSplit = addressSplit
								.getProperAddress(coApppriorSumMonth2);

						String name = null;
						String city = null;
						String province = null;
						String postalcode = null;
						if (currentAddressSplit != null) {
							name = (String) currentAddressSplit.get("address1");
							city = (String) currentAddressSplit.get("city");
							province = (String) currentAddressSplit
									.get("Province");
							postalcode = (String) currentAddressSplit
									.get("postalcode");
							cLead = new CreateApplicant();

							cLead.createApplicantAddress(coApplicantID, name,
									city, province, postalcode, coMovedIn3);
							Logger.debug("Applicant created with priorAddress2 ");

							Logger.debug("CouchBase data is appending...");

							applicantAddress=new controllers.Address();
							
							applicantAddress.setName(name);
							applicantAddress.setCity(city);
							applicantAddress.setMovedIn(coMovedIn3);
							applicantAddress.setPostalCode(postalcode);
							applicantAddress.setProvience(province);
							applicant.getListOfAddress().add(applicantAddress);

						}// spliting prior address 2
					}// checking prior address 2 is not null
				}// end of Loggeric of prior address 2
			}

			String formType="Applicant-SubForm-7b";
			opportunity.setPogressStatus(65);
			storeData.storeDataInCouchBase(formType, opportunity);
			Logger.debug("Data created in coucbase for MortgageForm7");
			EmployeIncomeTypeParam employeIncome = new EmployeIncomeTypeParam();
			SelfEmployeIncomeTypeParam selfEmpIncome = new SelfEmployeIncomeTypeParam();
			PensionIncomeTypeParam pension = new PensionIncomeTypeParam();
			InvestmentsIncomeTypeParam investIncType = new InvestmentsIncomeTypeParam();
			MaternityIncomeTypeParam maternityIncType = new MaternityIncomeTypeParam();
			VehicleAllowIncomeTypeParam vehicleIncType = new VehicleAllowIncomeTypeParam();
			LivingAllowIncomeTypeParam livingIncType = new LivingAllowIncomeTypeParam();
			CommissionIncomeTypeParam commIncType = new CommissionIncomeTypeParam();
			BonusIncomeTypeParam bonusIncType = new BonusIncomeTypeParam();
			OtherIncomeTypeParam otherIncType = new OtherIncomeTypeParam();
			String applicantName = (String) session.get("applicantFirstName");
			return ok(mortgagePage8.render("", employeIncome, selfEmpIncome,
					pension, investIncType, maternityIncType, vehicleIncType,
					livingIncType, commIncType, bonusIncType, otherIncType,
					applicantName));
		} catch (Exception e) {
			Logger.debug("Error in mortgagePage7b ", e);
			return ok("Some thing went wrong in mortgagePage7");
		}
	}

	public static Result mortgagePage12() {

		HashMap dataStoreValue = new HashMap();
		DynamicForm dynamicForm = form().bindFromRequest();
		String filepath = null;
		String applicantName = "";
		String applicantLastName = "";
		String applicantOneEmail = "";
		String applicantTwoEmail = "";
		String applicantThreeEmail = "";
		int applicantId = 0;
		String coApplicantName = "";
		String referrerEmail = "";
		String applicantTwoFirstName = "";
		String applicantTwoLastName = "";
		int applicantId2 = 0;
		String referralName = "";
		try {
			Logger.debug("inside service method");
			// String uniid = dynamicForm.get("uniid");
			CouchBaseOperation storeData=new CouchBaseOperation();
			Session session = Http.Context.current().session();

			int crm_LeadId = 0;
			try {
				crm_LeadId = Integer.parseInt(session.get("crmLeadId")
						.toString());
			} catch (Exception e) {
				Logger.error("error in getting CRMLEADID from session id "
						+ crm_LeadId);
			}
			Opportunity opportunity = storeData.getOpporunityData(crm_LeadId
					+ "");	

			String coapplicant = dynamicForm.get("coapplicant");

			String co_applicantExsist = "";
			try {

				co_applicantExsist = (String) session
						.get("additionalApplicants");
				if (co_applicantExsist == null) {
					co_applicantExsist = "";
				}
			} catch (Exception e) {
				Logger.error(
						"Error when getting additionalApplicants from session ",
						e);
			}

			// session.setAttribute("crmLeadId", crmLeadId);
			try {

				referrerEmail = (String) session.get("referralEmail");
				referrerEmail = referrerEmail.trim();
				referralName = (String) session.get("referralName");
				applicantName = (String) session.get("applicantFirstName");
				applicantLastName = (String) session.get("applicantLasttName");
				applicantOneEmail = (String) session.get("applicantEmail");
				applicantId = Integer.parseInt((String) session
						.get("applicantID"));

				Logger.debug("applicantId " + applicantId);
			} catch (Exception e) {
				Logger.error("Error when reading data from session ", e);
			}
			try {

				applicantTwoFirstName = (String) session
						.get("co_applicantFirstName");

				if (applicantTwoFirstName == null) {
					applicantTwoFirstName = "";
				}

				applicantTwoLastName = (String) session
						.get("co_applicantLastName");
				applicantTwoEmail = (String) session.get("co_applicantEmail");

				if (applicantTwoEmail == null) {
					applicantTwoEmail = "";
				}
				applicantId2 = Integer.parseInt((String) session
						.get("applicantID2"));

			} catch (Exception e) {
				Logger.error(
						"Error when reading applicantFirstName,coApplicantFirstName,CoAppEmail ",
						e);
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			// Logger.debug("old unique id is  " + uniid);
			// get current date time with Calendar()
			Calendar cal = Calendar.getInstance();
			String currentDateTime = (dateFormat.format(cal.getTime()));

			// String form1UniqueId = (String)
			// ses.getAttribute("form1uniqueId");
			// if(uniid.equals(form1UniqueId)){
			// get ip of latest form sumitted
			String ip = request().remoteAddress();
			// if(uniid.equals(form1UniqueId)){

			BufferedImage image = null;
			BufferedImage image1 = null;

			String areUsingTouchScreenDevice = dynamicForm.get("touchScreen");
			if (areUsingTouchScreenDevice.equalsIgnoreCase("Yes")) {

				// BASE64Decoder decoder = new BASE64Decoder();
				String fileName = dynamicForm.get("sign1");

				String newString = fileName.substring(22);

				if (co_applicantExsist.equalsIgnoreCase("Yes")) {
					String filename2 = dynamicForm.get("sign2");
					String newString1 = filename2.substring(22);
					byte[] decodedBytes1 = Base64.decode(newString1);
					image1 = ImageIO.read(new ByteArrayInputStream(
							decodedBytes1));

				}

				byte[] decodedBytes = Base64.decode(newString);

				if (decodedBytes == null) {
					System.out.println("decodedBytes  is null");
				}

				image = ImageIO.read(new ByteArrayInputStream(decodedBytes));

				filepath = MortgageApplicationPdfGeneration
						.MortgageApplicationPdfGenerationMethod(applicantName,
								areUsingTouchScreenDevice, co_applicantExsist,
								image, image1, "", "");

				SendWithUsExample sendWithUsExample = new SendWithUsExample();

			
				sendWithUsExample.sendTOreferralCompletedApp(
						referralName.trim(), applicantName, referrerEmail,
						applicantTwoFirstName);
				sendWithUsExample.sendDisclosuresToclientCompletedApp(
						applicantName, applicantOneEmail,
						applicantTwoFirstName, applicantTwoEmail,
						applicantThreeEmail, filepath);
				if (image == null) {
					System.out.println("Buffered Image is null");
				}
			} else {
				String mytypedname2 = dynamicForm.get("typedName2");
				// System.out.println(mytypedname2);
				String mytypedname1 = dynamicForm.get("typedName1");
				dataStoreValue.put("Signature_mytypedname1", mytypedname1);
				dataStoreValue.put("Signature_mytypedname2", mytypedname2);
				filepath = MortgageApplicationPdfGeneration
						.MortgageApplicationPdfGenerationMethod(applicantName,
								areUsingTouchScreenDevice, "Yes", null, null,
								mytypedname1, mytypedname2);
				SendWithUsExample sendWithUsExample = new SendWithUsExample();

				
				sendWithUsExample.sendTOreferralCompletedApp(
						referralName.trim(), applicantName, referrerEmail,
						applicantTwoFirstName);
				sendWithUsExample.sendDisclosuresToclientCompletedApp(
						applicantName, applicantOneEmail,
						applicantTwoFirstName, applicantTwoEmail,
						applicantThreeEmail, filepath);
			}

			// Logic to store Signature in OpenERP
			System.out.println("file path ----------" + filepath);
			CreateApplicant signatureAppliant = new CreateApplicant();

			signatureAppliant.updateApplicantSignatureAndIp(applicantId,
					cal.getTime(), ip);

			if (applicantId2 != 0) {
				signatureAppliant.updateApplicantSignatureAndIp(applicantId2,
						cal.getTime(), ip);
			}
			Logger.debug("going to update signature");
			try {

				/*
				 * ThankyouMailTemplateforApplicant
				 * .ThankyouMailTemplateforApplicantMethod(applicantOneEmail,
				 * applicantName, applicantTwoEmail, applicantThreeEmail,
				 * applicantId, applicantId2, "applicant.record", filepath);
				 */

			} catch (Exception e) {
				Logger.error("Error ", e);
			}
			try {
				GenericHelperClass genericHelperClass = new GenericHelperClass();
				Logger.debug("getting oppeunity name");
				com.debortoliwines.openerp.api.Session openERPSession = genericHelperClass
						.getOdooConnection();
				ObjectAdapter applicantAd3 = openERPSession
						.getObjectAdapter("crm.lead");
				com.debortoliwines.openerp.api.FilterCollection filters11 = new com.debortoliwines.openerp.api.FilterCollection();
				filters11.add("id", "=", crm_LeadId);
				com.debortoliwines.openerp.api.RowCollection partners11 = applicantAd3
						.searchAndReadObject(filters11, new String[] { "name",
								"id", "stage_id" });

				Row row = partners11.get(0);
				row.put("stage_id", 11);

				applicantAd3.writeObject(row, true);

				Logger.debug("getting opprunity name:" + row.get("name"));

				Object val = null;
				try {
					val = row.get("referred_source");
					Logger.debug("referred_source " + val);
				} catch (Exception e) {
					Logger.error("Error when reading referred source. ", e);
				}

				String referalName = (String) session.get("referralName");

				if (val == null) {
					new SendWithUsExample().sentToSupportReferralMissing(row
							.get("name").toString(), "support@visdom.ca");
				}/*
				 * CompletedMortgageAppMailTemplate.
				 * CompletedMortgageAppMailTemplateMethod(leadId, "crm.lead",
				 * row.get("name").toString(), applicantName,
				 * applicantLastName);
				 */
			} catch (Exception e) {
				Logger.error(
						"Error in converting stage partial to completedd app ",
						e);
			}
			try {
				Path path = Paths.get(filepath);
				Logger.debug("======================================");
				byte[] data1 = Files.readAllBytes(path);
				Logger.debug(" data1 hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh " + data1);
				CouchBaseOperation storedata = new CouchBaseOperation();
				HashMap data11 = new HashMap();

				String encodeData = net.iharder.Base64.encodeBytes(data1);

				data11.put("attachement", encodeData);
				storedata.storeDataInCouchBase(
						"doc_Disclosures_" + crm_LeadId, "mortgageForm8",
						data11);
				opportunity.setPogressStatus(100);
			opportunity.setMortgage_Brokerage_Disclosures_Id("doc_Disclosures_" + crm_LeadId);
String formType="mortgageForm8";
			storeData.storeDataInCouchBase(formType, opportunity);
				JSONObject jsonObject = storedata.getData(CouchBaseOperation.OPPORUTUNITY_FORM_DATA_COUCHBASEKEY+crm_LeadId);
				if (jsonObject != null) {
					new GenericHelperClass().createNote(new Integer(
							crm_LeadId), jsonObject.toString(),
							"Applicant Json Data", "applicant.record", formType);
				}
			} catch (Exception e) {
				Logger.error("error in get pdf data", e);
			}
			try {

				File file2 = new File(filepath);
				file2.delete();
				Logger.debug("pdf file deleted   -----------");
			} catch (Exception e) {
				Logger.error("Error when removing pdf file ", e);
			}
			/*
			 * org.codehaus.jettison.json.JSONObject jsoObject = new
			 * org.codehaus.jettison.json.JSONObject(); jsoObject.put("id", id);
			 */
			/*
			 * new DocumentAnalyzerRestCall(jsoObject .toString()).start();
			 */

			// req.setAttribute("applicantId",applicantId);
			/*
			 * req.setAttribute( "message",
			 * "Thank you for completing the Visdom Mortgage Application.  We will be in touch with you very soon."
			 * );
			 * req.getRequestDispatcher("MortgageApplicationSucess.jsp").forward
			 * ( req, res);
			 */

			// res.sendRedirect("http://form.jotformpro.com/form/50496822883971?uniqueId"+form1UniqueId);
			/*
			 * }else{ req.setAttribute("message",
			 * "Both forms not filled by same Person Please fill the Form once agian"
			 * );
			 * req.getRequestDispatcher("MortgageApplicationSucess.jsp").forward
			 * (req, res); }
			 */
			/*
			 * }else{ req.setAttribute("message",
			 * " We are sorry, but it seems the security and reliability of your internet connection may have been weakened.  To protect your identity and the security of your information, can you please submit this application again"
			 * );
			 * req.getRequestDispatcher("MortgageApplicationSucess.jsp").forward
			 * (req, res);
			 * 
			 * }
			 */
		} catch (Exception e) {

			Logger.error("Error in mortgagePage12 ", e);
		}

		return ok(MortgageApplicationSucess
				.render("Thank you for completing the Visdom Mortgage Application.  We will be in touch with you very soon."));
	}

	// Back Button Operation methods...
	public static Result mortgageBackPage1() {

		Logger.info(" inside mortgageBackPage1 ");
		String applicantID = "";
		String coApplicantID = "";
		int leadId = 0;
		Session session = Http.Context.current().session();
		String mobilePhone = "";
		try {
			applicantID = (String) session.get("applicantID");
			Logger.debug("applicantID " + applicantID);
			leadId = Integer.parseInt((String) session.get("crmLeadId"));
			Logger.debug("leadId " + leadId);
			mobilePhone = session.get("isMobile");
			Logger.debug("mobilePhone " + mobilePhone);

			coApplicantID = session.get("applicantID2");
		} catch (Exception e) {
			Logger.error("Error in getting values from session " + e);
		}
		boolean isMobile = false;
		if (mobilePhone != null && !mobilePhone.isEmpty()) {
			if (mobilePhone.equalsIgnoreCase("isMobile")) {
				isMobile = true;
			}
		}

	

		ApplicantDBOperation appDBOperation = null;
		ApplicantBasicDetails appBasicDetails = null;
		CouchBaseOperation couchbaseObject = null;
		JSONObject jsonObject = null;
		DynamicForm dynamicForm = form().bindFromRequest();
		String additionalApplicant1 = "";
		Opportunity opportunity=null;
		String  lendingGoal=null;
		try {
			couchbaseObject = new CouchBaseOperation();

			opportunity=couchbaseObject.getOpporunityData(leadId+"");
			additionalApplicant1 =opportunity.getIsAdditionalApplicantExist();
			lendingGoal = opportunity.getWhat_is_your_lending_goal();
		} catch (JSONException json) {
			Logger.error("Error when reading data from couchbase " + json);
		}

		if (isMobile) {
			Logger.debug("Going to render Mobile page i.e mortgagePage1b ");
			if (additionalApplicant1 != ""
					&& additionalApplicant1.equalsIgnoreCase("yes")) {
				
				Applicant applicant=opportunity.getApplicants().get(1);
				return ok(mortgagePage1b.render(opportunity,applicant));
			} else {
				
				Applicant applicant=opportunity.getApplicants().get(0);
				return ok(mortgagePage1a.render(opportunity,applicant));
			}
		} else {
			try {

				// get applicantid and additionalApplicant from openerp by using
				// crmLeadId ,rendered by previous page
				// HARD CODE
				Logger.debug("Rendering System pages");

				couchbaseObject = new CouchBaseOperation();

			

				// Need to read from couchbase after inserted the data of Co
				// applicant
				Applicant applicant=opportunity.getApplicants().get(0);
				Applicant co_applicant=null;

				if (additionalApplicant1 != null
						&& !additionalApplicant1.equalsIgnoreCase("")
						&& additionalApplicant1.equalsIgnoreCase("yes")) {
					co_applicant=opportunity.getApplicants().get(1);
				}

				

				// int crmLeadId = jsonObject.getInt("opporunity_id");
				// Logger.debug("crmLeadId "+crmLeadId);

				return ok(mortgagePage1.render(opportunity,applicant,co_applicant));
			} catch (Exception e) {
				Logger.error("Error in linking back page 1 " + e);
				return ok("Some thing went wrong in linking back page 1 ");
			}
		}
	}

	public static Result mortgageBackPage2() {
		Logger.info("Inside mortgageBackPage2");
		CouchBaseOperation couchbaseObject = null;
		LendingTermDBOperation lendingDB = null;
		LendingTerm lendingTerm = null;
		try {
			String applicantID = "";
			String coApplicantID = "";
			int leadId = 0;
			Session session = Http.Context.current().session();
			String mobilePhone = "";
			try {
				applicantID = (String) session.get("applicantID");

				leadId = Integer.parseInt((String) session.get("crmLeadId"));
				mobilePhone = session.get("isMobile");

				coApplicantID = session.get("applicantID2");
			} catch (Exception e) {
				Logger.error("Error in getting values from session " + e);
			}

			boolean isMobile = false;
			if (mobilePhone != null && !mobilePhone.isEmpty()) {
				if (mobilePhone.equalsIgnoreCase("isMobile")) {
					isMobile = true;
				}
			}

			String applicantIDCB = "Applicant_" + applicantID;
			couchbaseObject = new CouchBaseOperation();
			JSONObject jsonObject = null;
			String Applicant_leadingGoal = "";

		Opportunity opportunity=null;
			try {
				opportunity=couchbaseObject.getOpporunityData(leadId+"");
		Applicant_leadingGoal=opportunity.getWhat_is_your_lending_goal();
			} catch (JSONException json) {
				Logger.error("Error when reading data from couchbase " + json);
			}

			if (Applicant_leadingGoal != null
					&& Applicant_leadingGoal.equalsIgnoreCase("Purchase")) {
				Logger.debug("Rendering mortgagePage Purchase");
				try {
					lendingDB = new LendingTermDBOperation();
					lendingTerm.setAddress(opportunity.getAddress());
					lendingTerm.setDownpayment(opportunity.getDown_payment_amount()+"");
					//lendingTerm.setBankAccount(opportunity.getBank_account()!);
					Logger.debug("LendingTermDetails of Purchase , Address "
							+ lendingTerm.getAddress() + "\n down payment "
							+ lendingTerm.getDownpayment()
							+ "\n Down Payment sources like, bankAccount"
							+ lendingTerm.isBankAccount() + "\n rrsps"
							+ lendingTerm.isRrsps() + "\n investments "
							+ lendingTerm.isInvestments() + "\n borrowed "
							+ lendingTerm.isBorrowed() + "\n saleOfProperty "
							+ lendingTerm.isSaleOfProperty() + "\n gift "
							+ lendingTerm.isGift() + "\n personalCash "
							+ lendingTerm.isPersonalCash()
							+ "\n existingEquity "
							+ lendingTerm.isExistingEquity()
							+ "\n sweetEquity " + lendingTerm.isSweetEquity()
							+ "\n who will live "
							+ lendingTerm.getWhoWillLiving() + "\n Rental "
							+ lendingTerm.getRentalAmount() + "\n MLS listed "
							+ lendingTerm.getMlsListed());
				} catch (Exception e) {
					Logger.error(
							"Error when reading Purchase data from couchbase inside MortgageBackPage2 of Mortgage ",
							e);
				}
				return ok(mortgagePage2Pur.render(lendingTerm, ""));
			} else {
				try {
					lendingDB = new LendingTermDBOperation();
					lendingTerm = lendingDB.getLendingTerms(applicantIDCB);
					Logger.debug("LendingTermDetails of Refinance , Address "
							+ lendingTerm.getAddress() + "\n market value "
							+ lendingTerm.getMarketValue()
							+ "\n additional Amount "
							+ lendingTerm.getAdditionalAmount()
							+ "\n who will live "
							+ lendingTerm.getWhoWillLiving() + "\n Rental "
							+ lendingTerm.getRentalAmount()
							+ "\n buy Property " + lendingTerm.isBuyProperty()
							+ "\n Pay of Debt " + lendingTerm.isPayOffDebt()
							+ "\n buyInvestment "
							+ lendingTerm.isBuyInvestments() + "\n BuyVehicle "
							+ "\n Renovate " + lendingTerm.isRenovate()
							+ "\n Refurnish " + lendingTerm.isRefurnish()
							+ "\n vacation " + lendingTerm.isVacation()
							+ "\n RecVehicle " + lendingTerm.isRecVehicle()
							+ "\n Other " + lendingTerm.isOther());
				} catch (Exception e) {
					Logger.error(
							"Error when reading Refinance data from couchbase inside MortgageBackPage2 of Mortgage ",
							e);
				}
				return ok(mortgagePage2Ref.render(lendingTerm, ""));
			}
		} catch (Exception e) {
			Logger.error("Error when rendering page2 " + e);
			return ok("Something went wrong when rendering page2 ");
		}
	}

	public static Result mortgageBackPage23() {

		Logger.info("Inside mortgageBackPage23");
		CouchBaseOperation couchbaseObject = null;
		/*
		 * PreApproval preApproval = new PreApproval(); PreApprovalDBOperation
		 * preApprovalDBOjbect = new PreApprovalDBOperation();
		 */
		LendingTermDBOperation lendingDB = null;
		LendingTerm lendingTerm = null;
		JSONObject jsonObject = null;
		try {
			String applicantID = "";
			String coApplicantID = "";
			int leadId = 0;
			Session session = Http.Context.current().session();
			String mobilePhone = "";
			try {
				applicantID = (String) session.get("applicantID");
				leadId = Integer.parseInt((String) session.get("crmLeadId"));
				mobilePhone = session.get("isMobile");
				coApplicantID = session.get("applicantID2");
			} catch (Exception e) {
				Logger.error("Error in getting values from session " + e);
			}
			boolean isMobile = false;
			if (mobilePhone != null && !mobilePhone.isEmpty()) {
				if (mobilePhone.equalsIgnoreCase("isMobile")) {
					isMobile = true;
				}
			}
			String applicantIDCB = "Applicant_" + applicantID;
			couchbaseObject = new CouchBaseOperation();
			String Applicant_leadingGoal = "";
			String Applicant_propertylisted1 = "";
			try {
				jsonObject = couchbaseObject.getCouchBaseData(applicantIDCB);
				Applicant_leadingGoal = jsonObject
						.getString("Applicant-leadingGoal");
				if (Applicant_leadingGoal != null
						&& Applicant_leadingGoal.equalsIgnoreCase("Purchase"))
					Applicant_propertylisted1 = jsonObject
							.getString("Applicant-propertylisted1");

			} catch (JSONException json) {
				Logger.error("Error when reading data from couchbase " + json);
			}
			Logger.debug("Applicant_leadingGoal " + Applicant_leadingGoal);
			if (Applicant_leadingGoal != null
					&& Applicant_leadingGoal.equalsIgnoreCase("PreApproval")) {

				Logger.debug("Rendering mortgagePage PreApproval");
				try {
					lendingDB = new LendingTermDBOperation();
					lendingTerm = lendingDB.getLendingTerms(applicantIDCB);

					Logger.debug("LendingTermDetails of Purchase , Province "
							+ lendingTerm.getProvince() + "\n purchase price "
							+ lendingTerm.getPurchasePrice()
							+ "\n down payment " + lendingTerm.getDownpayment()
							+ "\n Down Payment sources like, bankAccount"
							+ lendingTerm.isBankAccount() + "\n rrsps"
							+ lendingTerm.isRrsps() + "\n investments "
							+ lendingTerm.isInvestments() + "\n borrowed "
							+ lendingTerm.isBorrowed() + "\n saleOfProperty "
							+ lendingTerm.isSaleOfProperty() + "\n gift "
							+ lendingTerm.isGift() + "\n personalCash "
							+ lendingTerm.isPersonalCash()
							+ "\n existingEquity "
							+ lendingTerm.isExistingEquity()
							+ "\n sweetEquity " + lendingTerm.isSweetEquity()
							+ "\n who will live "
							+ lendingTerm.getWhoWillLiving() + "\n Rental "
							+ lendingTerm.getRentalAmount());
				} catch (Exception e) {
					Logger.error(
							"Error when reading Purchase data from couchbase inside MortgageBackPage2 of Mortgage ",
							e);
				}
				return ok(mortgagePage2Pre.render("", "", lendingTerm));
			} else if (Applicant_leadingGoal != null
					&& Applicant_leadingGoal.equalsIgnoreCase("Refinance")) {
				Logger.debug("Rendering mortgageBackPage3 ");
				return mortgageBackPage3();
			} else {
				if (Applicant_propertylisted1 != null
						&& !Applicant_propertylisted1.equalsIgnoreCase("")
						&& Applicant_propertylisted1
								.equalsIgnoreCase("MLSListed")
						|| Applicant_propertylisted1
								.equalsIgnoreCase("NewBuild")) {
					Logger.debug("Rendering method mortgagePage2() ");
					return mortgageBackPage2();
				} else {
					Logger.debug("Rendering method mortgagePage3()");
					return mortgageBackPage3();
				}
			}

		} catch (Exception e) {
			Logger.error("Error when rendering page2 " + e);

		}
		return ok("Something went wrong when rendering page2 ");
	}

	public static Result mortgageBackPage3() {

		Logger.debug("Inside mortgage Page3");
		CouchBaseOperation couchbaseObject = null;

		try {
			couchbaseObject = new CouchBaseOperation();
			String applicantID = "";
			String coApplicantID = "";
			int leadId = 0;
			Session session = Http.Context.current().session();
			String mobilePhone = "";
			try {
				applicantID = (String) session.get("applicantID");

				leadId = Integer.parseInt((String) session.get("crmLeadId"));
				mobilePhone = session.get("isMobile");
				coApplicantID = session.get("applicantID2");
			} catch (Exception e) {
				Logger.error("Error in getting values from session " + e);
			}
			boolean isMobile = false;
			if (mobilePhone != null && !mobilePhone.isEmpty()) {
				if (mobilePhone.equalsIgnoreCase("isMobile")) {
					isMobile = true;
				}
			}
			String applicantIDCB = "Applicant_" + applicantID;
			JSONObject jsonObject = null;
			String Applicant_typeofbuilding = "";
			String Applicant_propertystyle = "";
			String Applicant_sqaurefootage = "";
			String Applicant_propertyheated = "";
			String Applicant_getwater = "";
			String Applicant_propertydispose = "";
			String Applicant_garagetype = "";
			String Applicant_garageSize = "";

			try {

				jsonObject = couchbaseObject.getCouchBaseData(applicantIDCB);

				Applicant_typeofbuilding = jsonObject
						.getString("Applicant-typeofbuilding");
				Applicant_propertystyle = jsonObject
						.getString("Applicant-propertystyle");
				Applicant_sqaurefootage = jsonObject
						.getString("Applicant-sqaurefootage");
				Applicant_propertyheated = jsonObject
						.getString("Applicant-propertyheated");

				Applicant_getwater = jsonObject.getString("Applicant-getwater");
				Applicant_propertydispose = jsonObject
						.getString("Applicant-propertydispose");
				Applicant_garagetype = jsonObject
						.getString("Applicant-garagetype");
				Applicant_garageSize = jsonObject
						.getString("Applicant-garageSize");
			} catch (JSONException e) {
				Logger.error("Error in reading from couchbase " + e);
			}

			Logger.debug("Applicant_typeofbuilding " + Applicant_typeofbuilding
					+ "\n Applicant_propertystyle " + Applicant_propertystyle
					+ "\n Applicant_sqaurefootage " + Applicant_sqaurefootage
					+ "\n Applicant_propertyheated " + Applicant_propertyheated
					+ "\n Applicant_getwater " + Applicant_getwater
					+ "\n Applicant_propertydispose "
					+ Applicant_propertydispose + "\n Applicant_garagetype "
					+ Applicant_garagetype + "\n  Applicant_garageSize "
					+ Applicant_garageSize);

			return ok(mortgagePage3.render("", Applicant_typeofbuilding,
					Applicant_propertystyle, Applicant_sqaurefootage,
					Applicant_propertyheated, Applicant_getwater,
					Applicant_propertydispose, Applicant_garagetype,
					Applicant_garageSize));
		} catch (Exception e) {
			Logger.error("Error in when rendering page3 " + e);
			return ok(" Something went wrong when rendering page ");
		}
	}

	public static Result mortgageBackPage4() {
		Logger.info("Inside mortgageBackPage4 method of Mortgage Controller.");

		String applicantID = "";
		String mortgageType = "";
		String mortgageTerm = "";
		String lookingFor = "";
		String ammortizeYear = "";
		Session session = Http.Context.current().session();
		try {
			applicantID = (String) session.get("applicantID");
			Logger.debug("applicant id from session " + applicantID);
		} catch (Exception e) {
			Logger.error("Error in getting values from session " + e);
		}
		CouchbaseDAO cbDao = new CouchbaseDAO(applicantID);
		try {
			List<String> dataList = cbDao.getPage4Data(applicantID);
			mortgageType = dataList.get(0);
			mortgageTerm = dataList.get(1);
			lookingFor = dataList.get(2);
			if (lookingFor != null && lookingFor.equalsIgnoreCase("Other"))
				ammortizeYear = dataList.get(3);
		} catch (Exception e) {
			Logger.error("Error in mortgageBackPage4 method of Mortgage..", e);
		}
		return ok(mortgagePage4.render("", mortgageType, mortgageTerm,
				lookingFor, ammortizeYear));
	}

	public static Result mortgageBackPage5a() {
		Logger.info("Inside mortgageBackPage5a method of Mortgage Controller.");
		String applicantID = "";
		Session session = Http.Context.current().session();
		try {
			applicantID = (String) session.get("applicantID");
		} catch (Exception e) {
			Logger.error("Error in getting values from session " + e);
		}
		CouchbaseDAO cbDao = new CouchbaseDAO(applicantID);
		List<String> dataList = cbDao.getPage5aData(applicantID);
		String incomedown1 = dataList.get(0);
		String largerfamily1 = dataList.get(1);
		String buyingnewvechile1 = dataList.get(2);
		String Planninglifestyle1 = dataList.get(3);
		String financialrisk1 = dataList.get(4);
		return ok(mortgagePage5a.render("", incomedown1, largerfamily1,
				buyingnewvechile1, Planninglifestyle1, financialrisk1));
	}

	public static Result mortgageBackPage5b() {
		Logger.info("Inside mortgageBackPage5a method of Mortgage Controller.");
		String applicantID = "";
		Session session = Http.Context.current().session();
		try {
			applicantID = (String) session.get("applicantID");
		} catch (Exception e) {
			Logger.error("Error in getting values from session " + e);
		}
		CouchbaseDAO cbDao = new CouchbaseDAO(applicantID);
		List<String> dataList = cbDao.getPage5bData(applicantID);
		String thinkproperty1 = "";
		String imaginesamejob1 = "";
		String incomeraise1 = "";
		String rentalproperty1 = "";
		try {
			thinkproperty1 = dataList.get(0);
			imaginesamejob1 = dataList.get(1);
			incomeraise1 = dataList.get(2);
			rentalproperty1 = dataList.get(3);
		} catch (NullPointerException nlp) {
			Logger.error("Error Reading getPage5Data", nlp);
		}
		return ok(mortgagePage5b.render("", thinkproperty1, imaginesamejob1,
				incomeraise1, rentalproperty1));
	}

	public static Result mortgageBackPage6() {
		Logger.info("Inside mortgageBackPage6");

		CouchBaseOperation couchbaseObject = null;
		PersonalInfo personalInfo = null;
		try {
			couchbaseObject = new CouchBaseOperation();
			String applicantID = "";
			String coApplicantID = "";
			int leadId = 0;
			Session session = Http.Context.current().session();
			String mobilePhone = "";
			try {
				applicantID = (String) session.get("applicantID");

				leadId = Integer.parseInt((String) session.get("crmLeadId"));
				mobilePhone = session.get("isMobile");

				coApplicantID = session.get("applicantID2");
			} catch (Exception e) {
				Logger.error("Error in getting values from session " + e);
			}
			boolean isMobile = false;
			if (mobilePhone != null && !mobilePhone.isEmpty()) {
				if (mobilePhone.equalsIgnoreCase("isMobile")) {
					isMobile = true;
				}
			}
			String applicantIDCB = "Applicant_" + applicantID;
			JSONObject jsonObject = null;

			String applicantMobile = "";
			String applicantWorkPhone = "";
			String ApplicantHomePhone = "";
			String applicantBirthday = "";
			String applicantInsurance = "";
			String applicantRelationshipStatus = "";
			String applicantDependant = "";
			String applicantNonResident = "";
			String applicantMovedCanada = "";
			String additionalApplicant = null;
			// For coApplicant
			String coApplicantMobile = "";
			String coApplicantWorkPhone = "";
			String coApplicantHomePhone = "";
			String coApplicantBirthday = "";
			String coApplicantInsurance = "";
			String coApplicantRelationshipStatus = "";
			String coApplicantDependant = "";
			String coApplicantNonResident = "";
			String coApplicantMovedCanada = "";
			String additionalApplicantCB = "";
			try {
				jsonObject = couchbaseObject.getCouchBaseData(applicantIDCB);
				applicantMobile = jsonObject.getString("Applicant-mobile");
				applicantWorkPhone = jsonObject
						.getString("Applicant-workPhone");
				ApplicantHomePhone = jsonObject
						.getString("Applicant-homePhone");
				applicantBirthday = jsonObject.getString("Applicant-birthday");
				applicantInsurance = jsonObject
						.getString("Applicant-insurance");
				applicantRelationshipStatus = jsonObject
						.getString("Applicant-relationshipStatus");
				applicantDependant = jsonObject
						.getString("Applicant-dependant");
				applicantNonResident = jsonObject
						.getString("Applicant-non_Resident");
				applicantMovedCanada = jsonObject
						.getString("Applicant-moved_canada");
				additionalApplicantCB = jsonObject
						.getString("Applicant-additionalApplicant");
				Logger.debug("addi>>>>>>>>> " + additionalApplicantCB);
				if (additionalApplicantCB != null
						&& additionalApplicantCB.equalsIgnoreCase("yes")) {
					// Read Coapplicant
					coApplicantMobile = jsonObject
							.getString("Co_Applicant-mobile");
					coApplicantWorkPhone = jsonObject
							.getString("Co_Applicant-workPhone");
					coApplicantHomePhone = jsonObject
							.getString("Co_Applicant-homePhone");
					coApplicantBirthday = jsonObject
							.getString("Co_Applicant-birthday");
					coApplicantInsurance = jsonObject
							.getString("Co_Applicant-insurance");
					coApplicantRelationshipStatus = jsonObject
							.getString("Co_Applicant-relationshipStatus");
					coApplicantDependant = jsonObject
							.getString("Co_Applicant-dependant");
					coApplicantNonResident = jsonObject
							.getString("Co_Applicant-non_Resident");
					coApplicantMovedCanada = jsonObject
							.getString("Co_Applicant-moved_canada");
				}
			} catch (JSONException jsonException) {
				Logger.error("Error when reading from couchbase. "
						+ jsonException);
			}
			String applicantName = session.get("applicantFirstName");
			String coApplicantName = session.get("co_applicantFirstName");
			Logger.debug("addi>>>>>>>>> " + additionalApplicantCB);
			return ok(mortgagePage6.render(additionalApplicantCB,
					applicantName, coApplicantName, applicantMobile,
					applicantWorkPhone, ApplicantHomePhone, applicantBirthday,
					applicantInsurance, applicantRelationshipStatus,
					applicantDependant, applicantNonResident,
					applicantMovedCanada, coApplicantMobile,
					coApplicantWorkPhone, coApplicantHomePhone,
					coApplicantBirthday, coApplicantInsurance,
					coApplicantRelationshipStatus, coApplicantDependant,
					coApplicantNonResident, coApplicantMovedCanada));
		} catch (Exception e) {
			Logger.error("Error in mortgageBackPage6 " + e);
			return ok("Something went wrong when rendering mortgageBackPage6 ");
		}
	}

	public static Result mortgageBackPage7() {
		Logger.info("Inside mortgageBackPage7 Address");
		CouchBaseOperation couchbaseObject = null;
		ApplicantAddressParameter7 applAddressParam = null;
		CoApplicantAddressParameter7 coApplAddressParam = null;
		FormatDateString formatDateString = new FormatDateString();
		AddressDBOperation addressDB = null;
		couchbaseObject = new CouchBaseOperation();
		String applicantID = "";
		String coApplicantID = "";
		int leadId = 0;
		Session session = Http.Context.current().session();
		String mobilePhone = "";
		try {
			applicantID = (String) session.get("applicantID");
			leadId = Integer.parseInt((String) session.get("crmLeadId"));
			mobilePhone = session.get("isMobile");

			coApplicantID = session.get("applicantID2");
		} catch (Exception e) {
			Logger.error("Error in getting values from session " + e);
		}
		boolean isMobile = false;
		if (mobilePhone != null && !mobilePhone.isEmpty()) {
			if (mobilePhone.equalsIgnoreCase("isMobile")) {
				isMobile = true;
			}
		}
		String applicantIDCB = "Applicant_" + applicantID;

		JSONObject jsonObject = null;
		String additionalApplicant = "";
		if (isMobile) {
			Logger.debug("Going to render mobile page");
			try {
				jsonObject = couchbaseObject.getCouchBaseData(applicantIDCB);
				additionalApplicant = jsonObject
						.getString("Applicant-additionalApplicant");
				addressDB = new AddressDBOperation(applicantID);
				applAddressParam = new ApplicantAddressParameter7();
				coApplAddressParam = new CoApplicantAddressParameter7();
				if (additionalApplicant != null
						&& !additionalApplicant.equalsIgnoreCase("")
						&& additionalApplicant.equalsIgnoreCase("yes")) {
					coApplAddressParam = addressDB.getCoApplAddressDetails();
					return ok(mortgagePage7b.render(coApplAddressParam));
				} else {
					applAddressParam = addressDB.getApplAddressDetails();
					return ok(mortgagePage7a.render(applAddressParam));
				}
			} catch (JSONException | NullPointerException jsonExp) {
				Logger.error("Error when reading from couchbase ", jsonExp);
				return ok("Something went wrong when rendering mobile pages 7a or 7b");
			}

		} else {
			// uncomment and put in save
			try {
				try {
					jsonObject = couchbaseObject
							.getCouchBaseData(applicantIDCB);
					applAddressParam = new ApplicantAddressParameter7();
					applAddressParam.setAdditionalApplicant(jsonObject
							.getString("Applicant-additionalApplicant"));
					applAddressParam.setApplicantCurrentAddress(jsonObject
							.getString("Applicant-CurrentAddress"));
					String date1 = formatDateString
							.getFormattedDateString(jsonObject
									.getString("Applicant-movedIn1"));

					applAddressParam.setApplicantMovedIn1(date1);
					applAddressParam.setApplicantCurrentSumMonth(jsonObject
							.getString("Applicant-currentSumMonth"));
					applAddressParam.setApplicantTotalcurrentMonths(jsonObject
							.getString("Applicant-totalcurrentMonths"));

					Logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> Additional Applicant : "
							+ applAddressParam.getAdditionalApplicant()
							+ "\n applicant Current Address: "
							+ applAddressParam.getApplicantCurrentAddress()
							+ "\n App movedIn1 "
							+ applAddressParam.getApplicantMovedIn1()
							+ "\n CurrentSumMonth "
							+ applAddressParam.getApplicantCurrentSumMonth()
							+ "\n TotalcurrentMonths"
							+ applAddressParam.getApplicantTotalcurrentMonths());
					int totalMonth = Integer.parseInt(applAddressParam
							.getApplicantTotalcurrentMonths());

					Logger.debug("totalMonth " + totalMonth);
					if (totalMonth < 36) {
						applAddressParam.setApplicantPriorAddress1(jsonObject
								.getString("Applicant-priorAddress1"));
						String date2 = formatDateString
								.getFormattedDateString(jsonObject
										.getString("Applicant-movedIn2"));
						applAddressParam.setApplicantMovedIn2(date2);
						applAddressParam.setApplicantPriorSumMonth1(jsonObject
								.getString("Applicant-priorSumMonth1"));
						applAddressParam
								.setApplicantTotalpriorcurrentmonths1(jsonObject
										.getString("Applicant-totalpriorcurrentmonths1"));
						int totalPriorMonth = Integer.parseInt(applAddressParam
								.getApplicantTotalpriorcurrentmonths1());

						Logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>priorAddress1 "
								+ applAddressParam.getApplicantPriorAddress1()
								+ "\n MovedIn2 "
								+ applAddressParam.getApplicantMovedIn2()
								+ "\n PriorSumMonth1 "
								+ applAddressParam.getApplicantPriorSumMonth1()
								+ "\n Totalpriorcurrentmonths1 "
								+ applAddressParam
										.getApplicantTotalpriorcurrentmonths1()
								+ "\n ");

						Logger.debug("totalPriorMonth " + totalPriorMonth);
						if (totalPriorMonth < 36) {

							applAddressParam
									.setApplicantPriorAddress2(jsonObject
											.getString("Applicant-priorAddress2"));

							String date3 = formatDateString
									.getFormattedDateString(jsonObject
											.getString("Applicant-movedIn3"));
							applAddressParam.setApplicantMovedIn3(date3);
							applAddressParam
									.setApplicantPriorSumMonth2(jsonObject
											.getString("Applicant-priorSumMonth2"));
							applAddressParam
									.setApplicantTotalpriorcurrentmonths2(jsonObject
											.getString("Applicant-totalpriorcurrentmonths2"));
							int totalPriorMonth3 = Integer
									.parseInt(applAddressParam
											.getApplicantTotalpriorcurrentmonths2());
							Logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>> priorAddress2 "
									+ applAddressParam
											.getApplicantPriorAddress2()
									+ "\n MovedIn3 "
									+ applAddressParam.getApplicantMovedIn3()
									+ "\n PriorSumMonth2 "
									+ applAddressParam
											.getApplicantPriorSumMonth2()
									+ "\n "
									+ applAddressParam
											.getApplicantTotalpriorcurrentmonths2());
							Logger.debug("totalPriorMonth3 " + totalPriorMonth3);
						}
					}
					if (applAddressParam.getAdditionalApplicant() != null
							&& applAddressParam.getAdditionalApplicant()
									.equalsIgnoreCase("yes")) {
						coApplAddressParam = new CoApplicantAddressParameter7();
						coApplAddressParam.setCoAppcurrentAddress(jsonObject
								.getString("Applicant-coAppcurrentAddress"));
						String coDate1 = formatDateString
								.getFormattedDateString(jsonObject
										.getString("Applicant-coAppmovedIn1"));
						coApplAddressParam.setCoAppmovedIn1(coDate1);
						coApplAddressParam.setCoAppCurrentSumMonth(jsonObject
								.getString("Applicant-coAppCurrentSumMonth"));
						coApplAddressParam
								.setCoAppTotalcurrentMonths(jsonObject
										.getString("Applicant-coAppTotalcurrentMonths"));
						Logger.debug(">>>>>>>>>>>>>>  "
								+ coApplAddressParam.getCoAppcurrentAddress());

						int coTotalCurrentMonth = Integer
								.parseInt(coApplAddressParam
										.getCoAppTotalcurrentMonths());
						Logger.debug("coAppcurrentAddress "
								+ coApplAddressParam.getCoAppcurrentAddress()
								+ "\n"
								+ coApplAddressParam.getCoAppmovedIn1()
								+ "\n "
								+ coApplAddressParam.getCoAppCurrentSumMonth()
								+ "\n "
								+ coApplAddressParam
										.getCoAppTotalcurrentMonths()
								+ "\n coTotalCurrentMonth "
								+ coTotalCurrentMonth);

						Logger.debug("coTotalCurrentMonth "
								+ coTotalCurrentMonth);
						if (coTotalCurrentMonth < 36) {
							coApplAddressParam.setCoAppPriorAddress1(jsonObject
									.getString("Applicant-coAppPriorAddress1"));
							String coDate2 = formatDateString
									.getFormattedDateString(jsonObject
											.getString("Applicant-coMovedIn2"));
							coApplAddressParam.setCoMovedIn2(coDate2);
							coApplAddressParam.setCoPriorSumMonth1(jsonObject
									.getString("Applicant-coPriorSumMonth1"));
							coApplAddressParam
									.setCoTotalpriorcurrentmonths1(jsonObject
											.getString("Applicant-coTotalpriorcurrentmonths1"));
							int coTotalPriorMonth2 = Integer
									.parseInt(coApplAddressParam
											.getCoTotalpriorcurrentmonths1());
							Logger.debug(">>>>>>>>>>>>>>> coAppPriorAddress1 "
									+ coApplAddressParam
											.getCoAppPriorAddress1()
									+ "\n tCoMovedIn2 "
									+ coApplAddressParam.getCoMovedIn2()
									+ "\n CoPriorSumMonth1"
									+ coApplAddressParam.getCoPriorSumMonth1()
									+ "\n "
									+ coApplAddressParam
											.getCoTotalpriorcurrentmonths1()
									+ "\n " + coTotalPriorMonth2);
							if (coTotalPriorMonth2 < 36) {
								coApplAddressParam
										.setCoApppriorAddress2(jsonObject
												.getString("Applicant-coApppriorAddress2"));
								String coDate3 = formatDateString
										.getFormattedDateString(jsonObject
												.getString("Applicant-coMovedIn3"));
								coApplAddressParam.setCoMovedIn3(coDate3);
								coApplAddressParam
										.setCoApppriorSumMonth2(jsonObject
												.getString("Applicant-coApppriorSumMonth2"));
								coApplAddressParam
										.setCoApptotalpriorcurrentmonths2(jsonObject
												.getString("Applicant-coApptotalpriorcurrentmonths2"));
								int coTotalPriorMonth3 = Integer
										.parseInt(coApplAddressParam
												.getCoApptotalpriorcurrentmonths2());
								Logger.debug(">>>>>>>>>>>>>>>>>>> coApppriorAddress2 "
										+ coApplAddressParam
												.getCoApppriorAddress2()
										+ "\n CoMovedIn3 "
										+ coApplAddressParam.getCoMovedIn3()
										+ "\n CoApppriorSumMonth2 "
										+ coApplAddressParam
												.getCoApppriorSumMonth2()
										+ "\n CoApptotalpriorcurrentmonths2 "
										+ coApplAddressParam
												.getCoApptotalpriorcurrentmonths2()
										+ "\n coTotalPriorMonth3 "
										+ coTotalPriorMonth3);
							}
						}
					}
				} catch (JSONException | NullPointerException jsonExp) {
					Logger.error("Error when reading from couchbase ", jsonExp);
				}
				String applicantName = session.get("applicantFirstName");
				String coApplicantName = session.get("co_applicantFirstName");
				String additionalApplicant1 = session
						.get("additionalApplicants");
				return ok(mortgagePage7Address.render(additionalApplicant1,
						applicantName, coApplicantName, applAddressParam,
						coApplAddressParam));
				// return ok(mortgagePage7Address.render(""));
			} catch (Exception e) {
				Logger.error("Error in mortgagePage7Address ", e);
				return ok("Something went wrong when rendering page7 ");
			}
		}
	}

	public static Result mortgageBackPage89() {
		Logger.info("*** Inside mortgageBackPage89 ***");
		String additionalApplicant = "";
		JSONObject jsonObject = null;
		CouchBaseOperation couchbaseObject = null;
		String applicantID = "";
		Session session = Http.Context.current().session();
		String mobilePhone = "";

		try {
			applicantID = (String) session.get("applicantID");

		} catch (Exception e) {
			Logger.error("Error in getting values from session " + e);
		}
		String applicantIDCB = "Applicant_" + applicantID;
		try {
			couchbaseObject = new CouchBaseOperation();
			jsonObject = couchbaseObject.getCouchBaseData(applicantIDCB);
			additionalApplicant = jsonObject
					.getString("Applicant-additionalApplicant");
		} catch (JSONException jsonException) {
			Logger.error("Error when reading from couchbase", jsonException);
		}
		if (additionalApplicant != null
				&& additionalApplicant.equalsIgnoreCase("yes"))
			return mortgageBackPage9();
		else
			return mortgageBackPage8();
	}

	public static Result mortgageBackPage8() {
		Logger.debug("*** inside mortgageBackPage8 ***");
		CouchBaseOperation couchbaseObject = null;
		EmployeIncomeTypeParam employeeIncome = new EmployeIncomeTypeParam();
		SelfEmployeIncomeTypeParam selfEmployeeIncome = new SelfEmployeIncomeTypeParam();
		PensionIncomeTypeParam pensionIncType = new PensionIncomeTypeParam();
		InvestmentsIncomeTypeParam investmentIncType = new InvestmentsIncomeTypeParam();
		MaternityIncomeTypeParam maternityIncType = new MaternityIncomeTypeParam();
		VehicleAllowIncomeTypeParam vehicleIncType = new VehicleAllowIncomeTypeParam();
		LivingAllowIncomeTypeParam livingIncType = new LivingAllowIncomeTypeParam();
		CommissionIncomeTypeParam commissionIncType = new CommissionIncomeTypeParam();
		BonusIncomeTypeParam bonusIncType = new BonusIncomeTypeParam();
		OtherIncomeTypeParam otherIncType = new OtherIncomeTypeParam();
		FormatDateString formatDateString = new FormatDateString();
		try {
			couchbaseObject = new CouchBaseOperation();

			String applicantID = "";
			String coApplicantID = "";

			int leadId = 0;
			Session session = Http.Context.current().session();
			String mobilePhone = "";
			try {
				applicantID = (String) session.get("applicantID");

				leadId = Integer.parseInt((String) session.get("crmLeadId"));
				mobilePhone = session.get("isMobile");

				coApplicantID = session.get("applicantID2");
			} catch (Exception e) {
				Logger.error("Error in getting values from session " + e);
			}
			boolean isMobile = false;
			if (mobilePhone != null && !mobilePhone.isEmpty()) {
				if (mobilePhone.equalsIgnoreCase("isMobile")) {
					isMobile = true;
				}
			}

			String applicantIDCB = "Applicant_" + applicantID;
			JSONObject jsonObject = null;
			String additionalApplicant = "";
			String currentSelfEmployee = "";
			String currentEmployee = "";
			String pension = "";
			String investment = "";
			String maternity = "";
			String vehicleAllow = "";
			String livingAllow = "";
			String commission = "";
			String bonus = "";
			String other = "";
			// List<String> incomeTypeList = new ArrayList<String>();
			try {
				Logger.debug("Inside 2nd try");
				jsonObject = couchbaseObject.getCouchBaseData(applicantIDCB);
				additionalApplicant = jsonObject
						.getString("Applicant-additionalApplicant");
				try {
					currentEmployee = jsonObject
							.getString("Applicant-currentEmployee1");
					Logger.debug("currentEmployee>>" + currentEmployee + "<<<");
				} catch (JSONException | NullPointerException excp) {
					Logger.debug("Exception when reading Employed record from couchbase.");
				}

				if (currentEmployee != null
						&& currentEmployee.equalsIgnoreCase("Employed")) {
					Logger.debug("Inside Employed income type selected ,1st Record.");
					// incomeTypeList.add(currentEmployee);
					employeeIncome.setEmployed(true);
					employeeIncome.setBusiness1(jsonObject
							.getString("Applicant-business1"));
					String date1 = formatDateString
							.getFormattedDateString(jsonObject
									.getString("Applicant-startMonth1"));
					employeeIncome.setStartMonth1(date1);
					employeeIncome.setCurrentPosition1(jsonObject
							.getString("Applicant-currentPosition1"));
					employeeIncome.setMonthsWorked1(jsonObject
							.getString("Applicant-monthsWorked1"));

					Logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> " + "\n Business1 "
							+ employeeIncome.getBusiness1()
							+ "\n <<App startMonths "
							+ employeeIncome.getStartMonth1()
							+ "\n CurrentPosition1 "
							+ employeeIncome.getCurrentPosition1()
							+ "\n MonthsWorked1"
							+ employeeIncome.getMonthsWorked1());
					int totalMonth = Integer.parseInt(employeeIncome
							.getMonthsWorked1());

					if (totalMonth < 36) {
						Logger.debug("Inside Employed 2nd Record");
						employeeIncome.setBusiness11(jsonObject
								.getString("Applicant-business11"));
						String empStartMonth2 = jsonObject
								.getString("Applicant-startMonth11");
						Logger.debug("empStartMonth1 testtt" + empStartMonth2);
						String date2 = formatDateString
								.getFormattedDateString(empStartMonth2);
						employeeIncome.setStartMonth11(date2);
						employeeIncome.setPosition11(jsonObject
								.getString("Applicant-position11"));
						employeeIncome.setMonthTotal11(jsonObject
								.getString("Applicant-monthsTotal11"));

						Logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>Business11 "
								+ employeeIncome.getBusiness11()
								+ "\n StartMonth11 "
								+ employeeIncome.getStartMonth11()
								+ "\n Position11 "
								+ employeeIncome.getPosition11()
								+ "\n MonthTotal11 "
								+ employeeIncome.getMonthTotal11() + "\n ");
						int totalMonth2 = Integer.parseInt(employeeIncome
								.getMonthTotal11());
						Logger.debug("totalMonth2 " + totalMonth2);
						if (totalMonth2 < 36) {
							Logger.debug("Inside Employed 3rd Record");
							employeeIncome.setBusiness21(jsonObject
									.getString("Applicant-business21"));
							String date3 = formatDateString
									.getFormattedDateString(jsonObject
											.getString("Applicant-startMonth21"));
							employeeIncome.setStartMonth21(date3);
							employeeIncome.setPosition21(jsonObject
									.getString("Applicant-position21"));
							employeeIncome.setMonthsWorked21(jsonObject
									.getString("Applicant-monthsTotal21"));
							Logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>Business21 "
									+ employeeIncome.getBusiness21()
									+ "\n StartMonth21 "
									+ employeeIncome.getStartMonth21()
									+ "\n Position21 "
									+ employeeIncome.getPosition21()
									+ "\n MonthTotal21 "
									+ employeeIncome.getMonthsWorked21()
									+ "\n ");
							int totalMonth3 = Integer.parseInt(employeeIncome
									.getMonthTotal11());
							Logger.debug("totalMonth2 " + totalMonth2);
						}
					}
				}
				Logger.debug("Should come");
				try {
					currentSelfEmployee = jsonObject
							.getString("Applicant-priorSelfEmployee1_Self-Employ");
					Logger.debug("currentSelfEmployee " + currentSelfEmployee);
				} catch (JSONException | NullPointerException excp) {
					Logger.error(
							"Exception when reading Self Employed record from couchbase.",
							excp);
				}

				if (currentSelfEmployee != null
						&& currentSelfEmployee
								.equalsIgnoreCase("Self-Employed")) {
					Logger.debug("Inside SelfEmployed income type selected.");
					// incomeTypeList.add(currentSelfEmployee);
					selfEmployeeIncome.setSelfEmployed(true);
					selfEmployeeIncome.setBusiness21(jsonObject
							.getString("Applicant-business_Self-Employ"));
					String date1 = formatDateString
							.getFormattedDateString(jsonObject
									.getString("Applicant-startMonth1_Self-Employ"));
					selfEmployeeIncome.setStartMonth21(date1);
					selfEmployeeIncome.setCurrentPosition21(jsonObject
							.getString("Applicant-position1_Self-Employ"));
					selfEmployeeIncome.setMonthsWorked21(jsonObject
							.getString("Applicant-monthsTotal1_Self-Employ"));

					Logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> " + "\n Business1 "
							+ employeeIncome.getBusiness1()
							+ "\n App startMonths "
							+ employeeIncome.getStartMonth1()
							+ "\n CurrentPosition1 "
							+ employeeIncome.getCurrentPosition1()
							+ "\n MonthsWorked1"
							+ employeeIncome.getMonthsWorked1());
					int totalMonth = 0;
					try {
						totalMonth = Integer.parseInt(employeeIncome
								.getMonthsWorked1());
					} catch (Exception e) {
						Logger.error(
								"Error when parsing totalmonths of selfEmployee",
								e);
					}

					if (totalMonth < 36) {

						selfEmployeeIncome.setBusiness22(jsonObject
								.getString("Applicant-business2_Self-Employ"));
						String date2 = formatDateString
								.getFormattedDateString(jsonObject
										.getString("Applicant-startMonth2_Self-Employ"));
						selfEmployeeIncome.setStartMonth22(date2);
						selfEmployeeIncome
								.setPosition22(jsonObject
										.getString("Applicant-positionself2_Self-Employ"));
						selfEmployeeIncome
								.setMonthTotal22(jsonObject
										.getString("Applicant-selfemployemonthsumTotal2_Self-Employ"));

						Logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>Business11 "
								+ employeeIncome.getBusiness11()
								+ "\n StartMonth11 "
								+ employeeIncome.getStartMonth11()
								+ "\n Position11 "
								+ employeeIncome.getPosition11()
								+ "\n MonthTotal11 "
								+ employeeIncome.getMonthTotal11() + "\n ");
						int totalMonth2 = 0;
						try {
							totalMonth2 = Integer.parseInt(employeeIncome
									.getMonthTotal11());
						} catch (Exception e) {
							Logger.error(
									"Exception when parsing totalMonth2 of Employee",
									e);
						}
						Logger.debug("totalMonth2 " + totalMonth2);
						if (totalMonth2 < 36) {

							selfEmployeeIncome
									.setBusiness23(jsonObject
											.getString("Applicant-business3_Self-Employ"));
							String date3 = formatDateString
									.getFormattedDateString(jsonObject
											.getString("Applicant-startMonth3_Self-Employ"));
							selfEmployeeIncome.setStartMonth23(date3);
							selfEmployeeIncome
									.setPosition23(jsonObject
											.getString("Applicant-positionself3_Self-Employ"));
							selfEmployeeIncome
									.setMonthsWorked23(jsonObject
											.getString("Applicant-selfemployemonthsumTotal3_Self-Employ"));
							Logger.debug("3rd Record Self Employed>>>>>>>>>>>>>>>>>>>>>>>>Business23 "
									+ selfEmployeeIncome.getBusiness23()
									+ "\n StartMonth21 "
									+ selfEmployeeIncome.getStartMonth23()
									+ "\n Position23 "
									+ selfEmployeeIncome.getPosition23()
									+ "\n MonthTotal23 "
									+ selfEmployeeIncome.getMonthsWorked23()
									+ "\n ");
							int totalMonth3 = 0;
							try {
								totalMonth3 = Integer.parseInt(employeeIncome
										.getMonthTotal11());
							} catch (Exception e) {
								Logger.error(
										"Error when parsing total month3 of selfEmployee ",
										e);
							}
							Logger.debug("totalMonth2 " + totalMonth2);
						}
					}
				}
				Logger.debug("Before Pension");
				try {
					pension = jsonObject.getString("Applicant-type_Pension");
					Logger.debug("Pension " + pension);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading pension record from couchbase.",
							e);
				}

				if (pension != null && pension.equalsIgnoreCase("Pension")) {
					Logger.debug("Inside Pension Record");
					// incomeTypeList.add(pension);
					pensionIncType.setPension(true);
					pensionIncType.setBusiness(jsonObject
							.getString("Applicant-bussiness_Pension"));
					String ss = jsonObject
							.getString("Applicant-startMonth_Pension");
					Logger.debug("  <<>>> " + ss);
					String datePension = formatDateString
							.getFormattedDateString(jsonObject
									.getString("Applicant-startMonth_Pension"));
					pensionIncType.setStartMonth(datePension);
					pensionIncType.setCurrentPosition(jsonObject
							.getString("Applicant-jobTitle_Pension"));
					pensionIncType.setMonthsWorked(jsonObject
							.getString("Applicant-months_Pension"));
					Logger.debug("pension Record>>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business " + pensionIncType.getBusiness()
							+ "\n App startMonths "
							+ pensionIncType.getStartMonth() + "\n Position1 "
							+ pensionIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ pensionIncType.getMonthsWorked());
				}
				Logger.debug("Before Investments");
				try {
					investment = jsonObject
							.getString("Applicant-type_Investments");
					Logger.debug("Investments " + investment);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading Investment record from couchbase.",
							e);
				}
				if (investment != null
						&& investment.equalsIgnoreCase("Investments")) {
					Logger.debug("Inside Investment record.");
					investmentIncType.setInvestments(true);
					investmentIncType.setBusiness(jsonObject
							.getString("Applicant-bussiness_Investments"));
					String dateInvestment = formatDateString
							.getFormattedDateString(jsonObject
									.getString("Applicant-startMonth_Investments"));
					investmentIncType.setStartMonth(dateInvestment);
					investmentIncType.setCurrentPosition(jsonObject
							.getString("Applicant-Title_Investments"));
					investmentIncType.setMonthsWorked(jsonObject
							.getString("Applicant-months_Investments"));
					Logger.debug("Investment type >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + investmentIncType.getBusiness()
							+ "\n App startMonths "
							+ investmentIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ investmentIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ investmentIncType.getMonthsWorked());
				}

				Logger.debug("Before Maternity");
				try {
					maternity = jsonObject
							.getString("Applicant-type_Maternity");
					Logger.debug("maternity " + maternity);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading Maternity record from couchbase.",
							e);
				}
				if (maternity != null
						&& maternity.equalsIgnoreCase("Maternity")) {
					maternityIncType.setMaternity(true);
					maternityIncType.setBusiness(jsonObject
							.getString("Applicant-bussiness_Maternity"));
					String dateMaternity = formatDateString
							.getFormattedDateString(jsonObject
									.getString("Applicant-startMonth_Maternity"));
					maternityIncType.setStartMonth(dateMaternity);
					maternityIncType.setCurrentPosition(jsonObject
							.getString("Applicant-jobTitle_Maternity"));
					maternityIncType.setMonthsWorked(jsonObject
							.getString("Applicant-months_Maternity"));
					Logger.debug("Maternity >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + maternityIncType.getBusiness()
							+ "\n App startMonths "
							+ maternityIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ maternityIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ maternityIncType.getMonthsWorked());
				}

				Logger.debug("Before Vehicle Allow");
				try {
					vehicleAllow = jsonObject
							.getString("Applicant-type_Vehicle");
					Logger.debug("vehicleAllow " + vehicleAllow);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading vehicle Allow record from couchbase.",
							e);
				}
				if (vehicleAllow != null
						&& vehicleAllow.equalsIgnoreCase("Vehicle Allowance")) {

					vehicleIncType.setVehicleAllow(true);
					vehicleIncType.setBusiness(jsonObject
							.getString("Applicant-bussiness_Vehicle"));
					String dateInvestment = formatDateString
							.getFormattedDateString(jsonObject
									.getString("Applicant-startMonth_Vehicle"));
					vehicleIncType.setStartMonth(dateInvestment);
					vehicleIncType.setCurrentPosition(jsonObject
							.getString("Applicant-jobTitle_Vehicle"));
					vehicleIncType.setMonthsWorked(jsonObject
							.getString("Applicant-months_Vehicle"));
					Logger.debug("vehicle Allow >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + vehicleIncType.getBusiness()
							+ "\n App startMonths "
							+ vehicleIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ vehicleIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ vehicleIncType.getMonthsWorked());
				}

				Logger.debug("Before living Allow");
				try {
					livingAllow = jsonObject.getString("Applicant-type_Living");
					Logger.debug("vehicleAllow " + livingAllow);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading Living Allow record from couchbase.",
							e);
				}

				if (livingAllow != null
						&& livingAllow.equalsIgnoreCase("Living Allowance")) {

					livingIncType.setLivingAllow(true);
					livingIncType.setBusiness(jsonObject
							.getString("Applicant-bussiness_Living"));
					String dateLiving = formatDateString
							.getFormattedDateString(jsonObject
									.getString("Applicant-startMonth_Vehicle"));
					livingIncType.setStartMonth(dateLiving);
					livingIncType.setCurrentPosition(jsonObject
							.getString("Applicant-jobTitle_Vehicle"));
					livingIncType.setMonthsWorked(jsonObject
							.getString("Applicant-months_Vehicle"));
					Logger.debug("Living Allow >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + livingIncType.getBusiness()
							+ "\n App startMonths "
							+ livingIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ livingIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ livingIncType.getMonthsWorked());
				}

				Logger.debug("Before commission");
				try {
					commission = jsonObject
							.getString("Applicant-type_Commission");
					Logger.debug("commission " + commission);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading commission record from couchbase.",
							e);
				}
				if (commission != null
						&& commission.equalsIgnoreCase("Commission")) {

					commissionIncType.setCommission(true);
					commissionIncType.setBusiness(jsonObject
							.getString("Applicant-bussiness_Commission"));

					String dateCommission = formatDateString
							.getFormattedDateString(jsonObject
									.getString("Applicant-startMonth_Commission"));
					// String dateCommission =
					// jsonObject.getString("Applicant-startMonth_Commission");
					commissionIncType.setStartMonth(dateCommission);
					commissionIncType.setCurrentPosition(jsonObject
							.getString("Applicant-jobTitle_Commission"));
					commissionIncType.setMonthsWorked(jsonObject
							.getString("Applicant-months_Commission"));
					Logger.debug("Commission  >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + commissionIncType.getBusiness()
							+ "\n App startMonths "
							+ commissionIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ commissionIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ commissionIncType.getMonthsWorked());
				}
				Logger.debug("Before bonus");
				try {
					bonus = jsonObject.getString("Applicant-type_Bonus");
					Logger.debug("bonus " + bonus);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading bonus record from couchbase.",
							e);
				}
				if (bonus != null && bonus.equalsIgnoreCase("Bonus")) {

					bonusIncType.setBonus(true);
					bonusIncType.setBusiness(jsonObject
							.getString("Applicant-bussiness_Bonus"));
					String dateBonus = formatDateString
							.getFormattedDateString(jsonObject
									.getString("Applicant-startMonth_Bonus"));
					bonusIncType.setStartMonth(dateBonus);
					bonusIncType.setCurrentPosition(jsonObject
							.getString("Applicant-jobTitle_Bonus"));
					bonusIncType.setMonthsWorked(jsonObject
							.getString("Applicant-months_Bonus"));
					Logger.debug("Bonus >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + bonusIncType.getBusiness()
							+ "\n App startMonths "
							+ bonusIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ bonusIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ bonusIncType.getMonthsWorked());
				}
				Logger.debug("before other");
				try {
					other = jsonObject.getString("Applicant-type_Other");
					Logger.debug("other " + other);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading other record from couchbase.",
							e);
				}

				if (other != null && other.equalsIgnoreCase("Other")) {

					otherIncType.setOther(true);
					otherIncType.setBusiness(jsonObject
							.getString("Applicant-bussiness_Other"));
					String dateOther = formatDateString
							.getFormattedDateString(jsonObject
									.getString("Applicant-startMonth_Other"));
					otherIncType.setStartMonth(dateOther);
					otherIncType.setCurrentPosition(jsonObject
							.getString("Applicant-jobTitle_Other"));
					otherIncType.setMonthsWorked(jsonObject
							.getString("Applicant-months_Other"));
					Logger.debug("Bonus >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + otherIncType.getBusiness()
							+ "\n App startMonths "
							+ otherIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ otherIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ otherIncType.getMonthsWorked());
				}
			} catch (JSONException jsonException) {
				Logger.error("Error when reading from couchbase", jsonException);
			}
			String applicantName = (String) session.get("applicantFirstName");
			return ok(mortgagePage8.render(additionalApplicant, employeeIncome,
					selfEmployeeIncome, pensionIncType, investmentIncType,
					maternityIncType, vehicleIncType, livingIncType,
					commissionIncType, bonusIncType, otherIncType,
					applicantName));
		} catch (Exception e) {
			Logger.error("Error in mortgageBackPage8 ", e);
			return ok("Something went wrong when rendering page7 ");
		}
		// Return code shoud be here
	}

	public static Result mortgageBackPage9() {

		Logger.debug("*** inside mortgageBackPage9 ***");
		CouchBaseOperation couchbaseObject = null;
		EmployeIncomeTypeParam employeeIncome = new EmployeIncomeTypeParam();
		SelfEmployeIncomeTypeParam selfEmployeeIncome = new SelfEmployeIncomeTypeParam();
		PensionIncomeTypeParam pensionIncType = new PensionIncomeTypeParam();
		InvestmentsIncomeTypeParam investmentIncType = new InvestmentsIncomeTypeParam();
		MaternityIncomeTypeParam maternityIncType = new MaternityIncomeTypeParam();
		VehicleAllowIncomeTypeParam vehicleIncType = new VehicleAllowIncomeTypeParam();
		LivingAllowIncomeTypeParam livingIncType = new LivingAllowIncomeTypeParam();
		CommissionIncomeTypeParam commissionIncType = new CommissionIncomeTypeParam();
		BonusIncomeTypeParam bonusIncType = new BonusIncomeTypeParam();
		OtherIncomeTypeParam otherIncType = new OtherIncomeTypeParam();
		FormatDateString formatDateString = new FormatDateString();
		try {
			couchbaseObject = new CouchBaseOperation();

			String applicantID = "";
			String coApplicantID = "";
			int leadId = 0;
			Session session = Http.Context.current().session();
			String mobilePhone = "";
			try {
				applicantID = (String) session.get("applicantID");
				leadId = Integer.parseInt((String) session.get("crmLeadId"));
				mobilePhone = session.get("isMobile");
				coApplicantID = session.get("applicantID2");
			} catch (Exception e) {
				Logger.error("Error in getting values from session ", e);
			}
			boolean isMobile = false;
			if (mobilePhone != null && !mobilePhone.isEmpty()) {
				if (mobilePhone.equalsIgnoreCase("isMobile")) {
					isMobile = true;
				}
			}

			String applicantIDCB = "Applicant_" + applicantID;
			JSONObject jsonObject = null;
			String additionalApplicant = "";
			String currentSelfEmployee = "";
			String currentEmployee = "";
			String pension = "";
			String investment = "";
			String maternity = "";
			String vehicleAllow = "";
			String livingAllow = "";
			String commission = "";
			String bonus = "";
			String other = "";
			// List<String> incomeTypeList = new ArrayList<String>();
			try {
				Logger.debug("Inside 2nd try");
				jsonObject = couchbaseObject.getCouchBaseData(applicantIDCB);
				additionalApplicant = jsonObject
						.getString("Applicant-additionalApplicant");
				try {
					currentEmployee = jsonObject
							.getString("CoApplicant-Employedtype8");
					Logger.debug("currentEmployee>>" + currentEmployee + "<<<");
				} catch (JSONException | NullPointerException excp) {
					Logger.debug("Exception when reading Employed record from couchbase.");
				}

				if (currentEmployee != null
						&& currentEmployee.equalsIgnoreCase("Employed")) {
					Logger.debug("Inside Employed income type selected ,1st Record.");
					// incomeTypeList.add(currentEmployee);
					employeeIncome.setEmployed(true);
					employeeIncome.setBusiness1(jsonObject
							.getString("CoApplicant-Employedbussiness8"));
					String date1 = formatDateString
							.getFormattedDateString(jsonObject
									.getString("CoApplicant-startMonth1"));
					employeeIncome.setStartMonth1(date1);
					employeeIncome.setCurrentPosition1(jsonObject
							.getString("CoApplicant-EmployedjobTitle8"));
					employeeIncome
							.setMonthsWorked1(jsonObject
									.getString("CoApplicant-EmployedemployeeTotalMonths8"));

					Logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> " + "\n Business1 "
							+ employeeIncome.getBusiness1()
							+ "\n <<App startMonths "
							+ employeeIncome.getStartMonth1()
							+ "\n CurrentPosition1 "
							+ employeeIncome.getCurrentPosition1()
							+ "\n MonthsWorked1"
							+ employeeIncome.getMonthsWorked1());
					int totalMonth = Integer.parseInt(employeeIncome
							.getMonthsWorked1());

					if (totalMonth < 36) {
						Logger.debug("Inside Employed 2nd Record");
						employeeIncome.setBusiness11(jsonObject
								.getString("CoApplicant-Employedbussiness9"));
						String empStartMonth2 = jsonObject
								.getString("CoApplicant-startMonth9");
						Logger.debug("empStartMonth1 testtt" + empStartMonth2);
						String date2 = formatDateString
								.getFormattedDateString(empStartMonth2);
						employeeIncome.setStartMonth11(date2);
						employeeIncome.setPosition11(jsonObject
								.getString("CoApplicant-EmployedjobTitle9"));
						employeeIncome
								.setMonthTotal11(jsonObject
										.getString("CoApplicant-EmployedemployeeTotalMonths9"));

						Logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>Business11 "
								+ employeeIncome.getBusiness11()
								+ "\n StartMonth11 "
								+ employeeIncome.getStartMonth11()
								+ "\n Position11 "
								+ employeeIncome.getPosition11()
								+ "\n MonthTotal11 "
								+ employeeIncome.getMonthTotal11() + "\n ");
						int totalMonth2 = Integer.parseInt(employeeIncome
								.getMonthTotal11());
						Logger.debug("totalMonth2 " + totalMonth2);
						if (totalMonth2 < 36) {
							Logger.debug("Inside Employed 3rd Record");
							employeeIncome
									.setBusiness21(jsonObject
											.getString("CoApplicant-Employedbussiness10"));
							String date3 = formatDateString
									.getFormattedDateString(jsonObject
											.getString("CoApplicant-startMonth10"));
							employeeIncome.setStartMonth21(date3);
							employeeIncome
									.setPosition21(jsonObject
											.getString("CoApplicant-EmployedjobTitle10"));
							employeeIncome
									.setMonthsWorked21(jsonObject
											.getString("CoApplicant-EmployedemployeeTotalMonths10"));
							Logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>Business21 "
									+ employeeIncome.getBusiness21()
									+ "\n StartMonth21 "
									+ employeeIncome.getStartMonth21()
									+ "\n Position21 "
									+ employeeIncome.getPosition21()
									+ "\n MonthTotal21 "
									+ employeeIncome.getMonthsWorked21()
									+ "\n ");
							int totalMonth3 = Integer.parseInt(employeeIncome
									.getMonthTotal11());
							Logger.debug("totalMonth2 " + totalMonth2);
						}
					}
				}
				Logger.debug("Should come");
				try {
					currentSelfEmployee = jsonObject
							.getString("CoApplicant-Self-Employtype12");
					Logger.debug("currentSelfEmployee " + currentSelfEmployee);
				} catch (JSONException | NullPointerException excp) {
					Logger.error(
							"Exception when reading Self Employed record from couchbase.",
							excp);
				}

				if (currentSelfEmployee != null
						&& currentSelfEmployee
								.equalsIgnoreCase("Self-Employed")) {
					Logger.debug("Inside SelfEmployed income type selected.");
					// incomeTypeList.add(currentSelfEmployee);
					selfEmployeeIncome.setSelfEmployed(true);
					selfEmployeeIncome.setBusiness21(jsonObject
							.getString("CoApplicant-Self-Employbussiness12"));
					String date1 = formatDateString
							.getFormattedDateString(jsonObject
									.getString("CoApplicant-Self-EmployStartMonth1"));
					selfEmployeeIncome.setStartMonth21(date1);
					selfEmployeeIncome.setCurrentPosition21(jsonObject
							.getString("CoApplicant-Self-EmployjobTitle12"));
					selfEmployeeIncome
							.setMonthsWorked21(jsonObject
									.getString("CoApplicant-Self-EmployemployeeTotalMonths12"));

					Logger.debug(">>>>>>>>>>>>>>>>>>>>>>>> " + "\n Business1 "
							+ employeeIncome.getBusiness1()
							+ "\n App startMonths "
							+ employeeIncome.getStartMonth1()
							+ "\n CurrentPosition1 "
							+ employeeIncome.getCurrentPosition1()
							+ "\n MonthsWorked1"
							+ employeeIncome.getMonthsWorked1());
					int totalMonth = 0;
					try {
						totalMonth = Integer.parseInt(employeeIncome
								.getMonthsWorked1());
					} catch (Exception e) {
						Logger.error(
								"Error when parsing totalmonths of selfEmployee",
								e);
					}

					if (totalMonth < 36) {

						selfEmployeeIncome
								.setBusiness22(jsonObject
										.getString("CoApplicant-Self-Employbussiness13"));
						String date2 = formatDateString
								.getFormattedDateString(jsonObject
										.getString("CoApplicant-Self-Employtype13StartMonth"));
						selfEmployeeIncome.setStartMonth22(date2);
						selfEmployeeIncome
								.setPosition22(jsonObject
										.getString("CoApplicant-Self-EmployjobTitle13"));
						selfEmployeeIncome
								.setMonthTotal22(jsonObject
										.getString("CoApplicant-Self-EmployemployeeTotalMonths13"));

						Logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>Business11 "
								+ employeeIncome.getBusiness11()
								+ "\n StartMonth11 "
								+ employeeIncome.getStartMonth11()
								+ "\n Position11 "
								+ employeeIncome.getPosition11()
								+ "\n MonthTotal11 "
								+ employeeIncome.getMonthTotal11() + "\n ");
						int totalMonth2 = 0;
						try {
							totalMonth2 = Integer.parseInt(employeeIncome
									.getMonthTotal11());
						} catch (Exception e) {
							Logger.error(
									"Exception when parsing totalMonth2 of Employee",
									e);
						}
						Logger.debug("totalMonth2 " + totalMonth2);
						if (totalMonth2 < 36) {

							selfEmployeeIncome
									.setBusiness23(jsonObject
											.getString("CoApplicant-Self-Employbussiness2"));
							String date3 = formatDateString
									.getFormattedDateString(jsonObject
											.getString("CoApplicant-Self-EmployStartMonth2"));
							selfEmployeeIncome.setStartMonth23(date3);
							selfEmployeeIncome
									.setPosition23(jsonObject
											.getString("Applicant-positionself3_Self-Employ"));
							selfEmployeeIncome
									.setMonthsWorked23(jsonObject
											.getString("CoApplicant-Self-Employpositionself2"));
							Logger.debug("3rd Record Self Employed>>>>>>>>>>>>>>>>>>>>>>>>Business23 "
									+ selfEmployeeIncome.getBusiness23()
									+ "\n StartMonth21 "
									+ selfEmployeeIncome.getStartMonth23()
									+ "\n Position23 "
									+ selfEmployeeIncome.getPosition23()
									+ "\n MonthTotal23 "
									+ selfEmployeeIncome.getMonthsWorked23()
									+ "\n ");
							int totalMonth3 = 0;
							try {
								totalMonth3 = Integer.parseInt(employeeIncome
										.getMonthTotal11());
							} catch (Exception e) {
								Logger.error(
										"Error when parsing total month3 of selfEmployee ",
										e);
							}
							Logger.debug("totalMonth2 " + totalMonth2);
						}
					}
				}
				Logger.debug("Before Pension");
				try {
					pension = jsonObject.getString("CoApplicant-Pensiontype");
					Logger.debug("Pension " + pension);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading pension record from couchbase.",
							e);
				}

				if (pension != null && pension.equalsIgnoreCase("Pension")) {
					Logger.debug("Inside Pension Record");
					// incomeTypeList.add(pension);
					pensionIncType.setPension(true);
					pensionIncType.setBusiness(jsonObject
							.getString("CoApplicant-Pensionbussiness"));
					String ss = jsonObject.getString("CoApplicant-startMonth1");
					Logger.debug("  <<>>> " + ss);
					String datePension = formatDateString
							.getFormattedDateString(ss);
					pensionIncType.setStartMonth(datePension);
					pensionIncType.setCurrentPosition(jsonObject
							.getString("CoApplicant-PensionjobTitle"));
					pensionIncType.setMonthsWorked(jsonObject
							.getString("CoApplicant-pensionmonths"));
					Logger.debug("pension Record>>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business " + pensionIncType.getBusiness()
							+ "\n App startMonths "
							+ pensionIncType.getStartMonth() + "\n Position1 "
							+ pensionIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ pensionIncType.getMonthsWorked());
				}
				Logger.debug("Before Investments");
				try {
					investment = jsonObject
							.getString("CoApplicant-type_Investments");
					Logger.debug("Investments " + investment);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading Investment record from couchbase.",
							e);
				}
				if (investment != null
						&& investment.equalsIgnoreCase("Investments")) {
					Logger.debug("Inside Investment record.");
					investmentIncType.setInvestments(true);
					investmentIncType.setBusiness(jsonObject
							.getString("CoApplicant-bussiness_Investments"));
					String dateInvestment = formatDateString
							.getFormattedDateString(jsonObject
									.getString("CoApplicant-startMonth_Investments"));
					investmentIncType.setStartMonth(dateInvestment);
					investmentIncType.setCurrentPosition(jsonObject
							.getString("CoApplicant-Title_Investments"));
					investmentIncType.setMonthsWorked(jsonObject
							.getString("CoApplicant-months_Investments"));
					Logger.debug("Investment type >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + investmentIncType.getBusiness()
							+ "\n App startMonths "
							+ investmentIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ investmentIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ investmentIncType.getMonthsWorked());
				}

				Logger.debug("Before Maternity");
				try {
					maternity = jsonObject
							.getString("CoApplicant-type_Maternity");
					Logger.debug("maternity " + maternity);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading Maternity record from couchbase.",
							e);
				}
				if (maternity != null
						&& maternity.equalsIgnoreCase("Maternity")) {
					maternityIncType.setMaternity(true);
					maternityIncType.setBusiness(jsonObject
							.getString("CoApplicant-bussiness_Maternity"));
					String dateMaternity = formatDateString
							.getFormattedDateString(jsonObject
									.getString("CoApplicant-startMonth_Maternity"));
					maternityIncType.setStartMonth(dateMaternity);
					maternityIncType.setCurrentPosition(jsonObject
							.getString("CoApplicant-jobTitle_Maternity"));
					maternityIncType.setMonthsWorked(jsonObject
							.getString("CoApplicant-months_Maternity"));
					Logger.debug("Maternity >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + maternityIncType.getBusiness()
							+ "\n App startMonths "
							+ maternityIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ maternityIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ maternityIncType.getMonthsWorked());
				}

				Logger.debug("Before Vehicle Allow");
				try {
					vehicleAllow = jsonObject
							.getString("CoApplicant-VehicleAllowancetype2");
					Logger.debug("vehicleAllow " + vehicleAllow);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading vehicle Allow record from couchbase.",
							e);
				}
				if (vehicleAllow != null
						&& vehicleAllow.equalsIgnoreCase("Vehicle Allowance")) {

					vehicleIncType.setVehicleAllow(true);
					vehicleIncType
							.setBusiness(jsonObject
									.getString("CoApplicant-VehicleAllowancebussiness2"));
					String dateInvestment = formatDateString
							.getFormattedDateString(jsonObject
									.getString("CoApplicant-VehicleStartMonth"));
					vehicleIncType.setStartMonth(dateInvestment);
					vehicleIncType
							.setCurrentPosition(jsonObject
									.getString("CoApplicant-VehicleAllowancejobTitle2"));
					vehicleIncType.setMonthsWorked(jsonObject
							.getString("CoApplicant-VehicleAllowancemonths2"));
					Logger.debug("vehicle Allow >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + vehicleIncType.getBusiness()
							+ "\n App startMonths "
							+ vehicleIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ vehicleIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ vehicleIncType.getMonthsWorked());
				}

				Logger.debug("Before living Allow");
				try {
					livingAllow = jsonObject
							.getString("CoApplicant-type_Living");
					Logger.debug("vehicleAllow " + livingAllow);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading Living Allow record from couchbase.",
							e);
				}

				if (livingAllow != null
						&& livingAllow.equalsIgnoreCase("Living Allowance")) {

					livingIncType.setLivingAllow(true);
					livingIncType.setBusiness(jsonObject
							.getString("CoApplicant-bussiness_Living"));
					String dateLiving = formatDateString
							.getFormattedDateString(jsonObject
									.getString("CoApplicant-startMonth_Living"));
					livingIncType.setStartMonth(dateLiving);
					livingIncType.setCurrentPosition(jsonObject
							.getString("CoApplicant-jobTitle_Living"));
					livingIncType.setMonthsWorked(jsonObject
							.getString("CoApplicant-months_Living"));
					Logger.debug("Living Allow >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + livingIncType.getBusiness()
							+ "\n App startMonths "
							+ livingIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ livingIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ livingIncType.getMonthsWorked());
				}

				Logger.debug("Before commission");
				try {
					commission = jsonObject
							.getString("CoApplicant-type_Commission");
					Logger.debug("commission " + commission);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading commission record from couchbase.",
							e);
				}
				if (commission != null
						&& commission.equalsIgnoreCase("Commission")) {

					commissionIncType.setCommission(true);
					commissionIncType.setBusiness(jsonObject
							.getString("CoApplicant-bussiness_Commission"));

					String dateCommission = formatDateString
							.getFormattedDateString(jsonObject
									.getString("CoApplicant-startMonth_Commission"));
					// String dateCommission =
					// jsonObject.getString("Applicant-startMonth_Commission");
					commissionIncType.setStartMonth(dateCommission);
					commissionIncType.setCurrentPosition(jsonObject
							.getString("CoApplicant-jobTitle_Commission"));
					commissionIncType.setMonthsWorked(jsonObject
							.getString("CoApplicant-months_Commission"));
					Logger.debug("Commission  >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + commissionIncType.getBusiness()
							+ "\n App startMonths "
							+ commissionIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ commissionIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ commissionIncType.getMonthsWorked());
				}
				Logger.debug("Before bonus");
				try {
					bonus = jsonObject.getString("CoApplicant-type_Bonus");
					Logger.debug("bonus " + bonus);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading bonus record from couchbase.",
							e);
				}
				if (bonus != null && bonus.equalsIgnoreCase("Bonus")) {

					bonusIncType.setBonus(true);
					bonusIncType.setBusiness(jsonObject
							.getString("CoApplicant-bussiness_Bonus"));
					String dateBonus = formatDateString
							.getFormattedDateString(jsonObject
									.getString("CoApplicant-bussiness_Bonus"));
					bonusIncType.setStartMonth(dateBonus);
					bonusIncType.setCurrentPosition(jsonObject
							.getString("CoApplicant-jobTitle_Bonus"));
					bonusIncType.setMonthsWorked(jsonObject
							.getString("CoApplicant-months_Bonus"));
					Logger.debug("Bonus >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + bonusIncType.getBusiness()
							+ "\n App startMonths "
							+ bonusIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ bonusIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ bonusIncType.getMonthsWorked());
				}
				Logger.debug("before other");
				try {
					other = jsonObject.getString("CoApplicant-type_Other");
					Logger.debug("other " + other);
				} catch (JSONException e) {
					Logger.error(
							"Error when reading other record from couchbase.",
							e);
				}

				if (other != null && other.equalsIgnoreCase("Other")) {

					otherIncType.setOther(true);
					otherIncType.setBusiness(jsonObject
							.getString("CoApplicant-bussiness_Other"));
					String dateOther = formatDateString
							.getFormattedDateString(jsonObject
									.getString("CoApplicant-startMonth_Other"));
					otherIncType.setStartMonth(dateOther);
					otherIncType.setCurrentPosition(jsonObject
							.getString("CoApplicant-jobTitle_Other"));
					otherIncType.setMonthsWorked(jsonObject
							.getString("CoApplicant-months_Other"));
					Logger.debug("Bonus >>>>>>>>>>>>>>>>>>>>>>>> "
							+ "\n Business1 " + otherIncType.getBusiness()
							+ "\n App startMonths "
							+ otherIncType.getStartMonth()
							+ "\n CurrentPosition1 "
							+ otherIncType.getCurrentPosition()
							+ "\n MonthsWorked1"
							+ otherIncType.getMonthsWorked());
				}
			} catch (JSONException jsonException) {
				Logger.error("Error when reading from couchbase", jsonException);
			}
			String coApplicantName = (String) session
					.get("co_applicantFirstName");
			return ok(mortgagePage9.render(additionalApplicant, employeeIncome,
					selfEmployeeIncome, pensionIncType, investmentIncType,
					maternityIncType, vehicleIncType, livingIncType,
					commissionIncType, bonusIncType, otherIncType,
					coApplicantName));
		} catch (Exception e) {
			Logger.error("Error in mortgageBackPage9 ", e);
			return ok("Something went wrong when rendering mortgageBackPage9 ");
		}
		// Return code shoud be here

	}

	public static Result mortgageBackPage10() {
		Logger.debug("*** inside mortgageBackPage10 ***");
		CouchBaseOperation couchbaseObject = null;
		AssetsParam assetParam = null;
		List<AssetsParam> vehicleList = new ArrayList();
		List<AssetsParam> bankAccList = new ArrayList();
		List<AssetsParam> rrsptList = new ArrayList();
		List<AssetsParam> investmntList = new ArrayList();
		List<AssetsParam> otherList = new ArrayList();
		TotalAssets totalAssets = new TotalAssets();
		try {
			couchbaseObject = new CouchBaseOperation();
			String applicantID = "";
			String coApplicantID = "";

			int leadId = 0;
			Session session = Http.Context.current().session();
			String mobilePhone = "";
			try {
				applicantID = (String) session.get("applicantID");

				leadId = Integer.parseInt((String) session.get("crmLeadId"));
				mobilePhone = session.get("isMobile");

				coApplicantID = session.get("applicantID2");
			} catch (Exception e) {
				Logger.error("Error in getting values from session " + e);
			}
			boolean isMobile = false;
			if (mobilePhone != null && !mobilePhone.isEmpty()) {
				if (mobilePhone.equalsIgnoreCase("isMobile")) {
					isMobile = true;
				}
			}
			String applicantIDCB = "Applicant_" + applicantID;
			JSONArray vehicleType = null;
			JSONArray bankAcctArray = null;
			JSONArray rrspArray = null;
			JSONArray investmtArray = null;
			JSONArray otherArray = null;
			JSONObject jsonObject = null;

			try {
				Logger.debug(applicantIDCB);
				jsonObject = couchbaseObject.getCouchBaseData(applicantIDCB);
			} catch (JSONException | NullPointerException excp) {
				Logger.error("Error when reading json object from couchbase ",
						excp);
			}
			// Vehicle Record reading
			try {
				vehicleType = jsonObject.getJSONArray("Vehicle");
				Logger.debug("Vehicle items " + vehicleType.length());
				if (vehicleType != null) {
					for (int i = 0; i <= vehicleType.length() - 1; i++) {
						assetParam = new AssetsParam();
						JSONObject vehicleDetails = (JSONObject) vehicleType
								.get(i);
						assetParam.setAssetType(vehicleDetails
								.getString("Applicant-AssetType" + i));
						assetParam
								.setDescription(vehicleDetails
										.getString("Applicant-discription_Vehicle"
												+ i));
						assetParam.setValue(vehicleDetails
								.getString("Applicant-value_Vehicle" + i));
						try {
							assetParam.setOwnership(vehicleDetails
									.getString("Applicant-ownership_Vehicle"
											+ i));
						} catch (JSONException | NullPointerException excp) {
							Logger.error("Ownership is not there for vehicle ",
									excp);
						}
						vehicleList.add(assetParam);
					}
				}
			} catch (JSONException | NullPointerException excp) {
				Logger.debug(
						"Exception when reading Vehicle's all record from couchbase.",
						excp);
			}
			// Bank Account record reading
			try {
				bankAcctArray = jsonObject.getJSONArray("Bank Account");
				Logger.debug("Bank Account items " + bankAcctArray.length());
				if (bankAcctArray != null) {
					for (int i = 0; i <= bankAcctArray.length() - 1; i++) {
						assetParam = new AssetsParam();
						JSONObject vehicleDetails = (JSONObject) bankAcctArray
								.get(i);
						assetParam.setAssetType(vehicleDetails
								.getString("Applicant-AssetType" + i));
						assetParam.setDescription(vehicleDetails
								.getString("Applicant-discription_Bank_Account"
										+ i));
						assetParam.setValue(vehicleDetails
								.getString("Applicant-value_Bank_Account" + i));
						assetParam.setOwnership(vehicleDetails
								.getString("Applicant-ownership_Bank_Account"
										+ i));
						// Logger.debug(" Applicant-AssetType "+i+vehicleDetails.getString("Applicant-AssetType"+i));
						bankAccList.add(assetParam);
					}
				}
			} catch (JSONException | NullPointerException excp) {
				Logger.debug(
						"Exception when reading Vehicle's all record from couchbase.",
						excp);
			}
			// RRSP/TSFA record reading from couchbase.
			try {
				rrspArray = jsonObject.getJSONArray("RRSPTSFA");
				Logger.debug("RRSPTSFA items " + rrspArray.length());
				if (rrspArray != null) {
					for (int i = 0; i <= rrspArray.length() - 1; i++) {
						// Logger.debug(assetsType.get(i)+"=======");
						assetParam = new AssetsParam();
						JSONObject vehicleDetails = (JSONObject) rrspArray
								.get(i);
						// String
						// ss=vehicleDetails.getString("Applicant-AssetType"+i);
						assetParam.setAssetType(vehicleDetails
								.getString("Applicant-AssetType" + i));
						assetParam.setDescription(vehicleDetails
								.getString("Applicant-discription_RRSPs" + i));
						assetParam.setValue(vehicleDetails
								.getString("Applicant-value_RRSPs" + i));
						assetParam.setOwnership(vehicleDetails
								.getString("Applicant-ownership_RRSPs" + i));
						// Logger.debug(" Applicant-AssetType "+i+vehicleDetails.getString("Applicant-AssetType"+i));
						rrsptList.add(assetParam);
					}
				}
			} catch (JSONException | NullPointerException excp) {
				Logger.debug(
						"Exception when reading rrsp's all record from couchbase.",
						excp);
			}

			// Reading Investment record.
			try {
				investmtArray = jsonObject.getJSONArray("Investments");
				Logger.debug("investmnt items " + investmtArray.length());
				if (investmtArray != null) {
					for (int i = 0; i <= investmtArray.length() - 1; i++) {
						// Logger.debug(assetsType.get(i)+"=======");
						assetParam = new AssetsParam();
						JSONObject vehicleDetails = (JSONObject) investmtArray
								.get(i);
						// String
						// ss=vehicleDetails.getString("Applicant-AssetType"+i);
						assetParam.setAssetType(vehicleDetails
								.getString("Applicant-AssetType" + i));
						assetParam.setDescription(vehicleDetails
								.getString("Applicant-discription_Investments"
										+ i));
						assetParam.setValue(vehicleDetails
								.getString("Applicant-value_Investments" + i));
						assetParam.setOwnership(vehicleDetails
								.getString("Applicant-ownership_Investments"
										+ i));
						// Logger.debug(" Applicant-AssetType "+i+vehicleDetails.getString("Applicant-AssetType"+i));
						investmntList.add(assetParam);
					}
				}

			} catch (JSONException | NullPointerException excp) {
				Logger.debug(
						"Exception when reading investmt 's all record from couchbase.",
						excp);
			}
			// Reading Other record.
			try {
				otherArray = jsonObject.getJSONArray("Other");
				Logger.debug("Other items " + otherArray.length());
				if (otherArray != null) {
					for (int i = 0; i <= otherArray.length() - 1; i++) {
						// Logger.debug(assetsType.get(i)+"=======");
						assetParam = new AssetsParam();
						JSONObject vehicleDetails = (JSONObject) otherArray
								.get(i);
						// String
						// ss=vehicleDetails.getString("Applicant-AssetType"+i);
						assetParam.setAssetType(vehicleDetails
								.getString("Applicant-AssetType" + i));
						assetParam.setDescription(vehicleDetails
								.getString("Applicant-discription_Other" + i));
						assetParam.setValue(vehicleDetails
								.getString("Applicant-value_Other" + i));
						assetParam.setOwnership(vehicleDetails
								.getString("Applicant-ownership_Other" + i));
						// Logger.debug(" Applicant-AssetType "+i+vehicleDetails.getString("Applicant-AssetType"+i));
						otherList.add(assetParam);
					}
				}

			} catch (JSONException | NullPointerException excp) {
				Logger.debug(
						"Exception when reading investmt 's all record from couchbase.",
						excp);
			}
			totalAssets.setVehicle(vehicleList);
			totalAssets.setBankAccount(bankAccList);
			totalAssets.setRrsp(rrsptList);
			totalAssets.setInvestments(investmntList);
			totalAssets.setOthers(otherList);
			// Logger.debug(">>><<<>>><<>>"+totalAssets.getVehicle().get(0).getValue());

			String applicantName = session.get("applicantFirstName");
			String coApplicantName = session.get("co_applicantFirstName");
			return ok(mortgagePage10Assets.render("yes", applicantName,
					coApplicantName, totalAssets));
		} catch (Exception e) {
			Logger.error("Error when rendering mortgagePage10.", e);
			return ok("Something went wrong in mortgagePageBack10");
		}

	}

	public static Result mortgageBackPage11() {
		Logger.debug("*** inside mortgageBackPage11 ***");
		Session session = Http.Context.current().session();
		String applicantId = "";
		String applicantName = "";
		String coApplicantName = "";

		try {
			applicantId = (String) session.get("applicantID");
			applicantName = session.get("applicantFirstName");
			coApplicantName = session.get("co_applicantFirstName");
		} catch (Exception e) {
			Logger.error("Error when reading data from session ", e);
		}
		PropertiesDBOperation propDBObject = new PropertiesDBOperation();
		Map propertyMap = propDBObject.readPropertiesFromCB(applicantId);
		String howManyProperty = (String) propertyMap.get("howManyProperty");
		System.out.println("howManyProperty " + howManyProperty);
		List<ApplicantProperties> propertyList = (List) propertyMap
				.get("propertyList");
		ApplicantProperties propertyListTest = (ApplicantProperties) propertyList
				.get(0);
		Logger.debug("Address1 " + propertyListTest.getAddress()
				+ "\n Appx value " + propertyListTest.getAppxValue()
				+ "\n Mortgage " + propertyListTest.getMortgage()
				+ "\n Rent mo " + propertyListTest.getRentMo()
				+ "\n Condo Fees " + propertyListTest.getCondoFees()
				+ "\n Ownership " + propertyListTest.getOwnership()
				+ "\n Selling" + propertyListTest.isSelling());

		return ok(mortgagePage11Properties.render(howManyProperty,
				applicantName, coApplicantName, propertyList));
	}

	public static Result mortgageBackPage1a() {
		Logger.debug("*** inside mortgageBackPage1a ***");
		int leadId = 0;
		CouchBaseOperation couchBaseOperation=new CouchBaseOperation();
		Session session = Http.Context.current().session();
		String mobilePhone = "";
		Applicant appBasicDetails=null;
		Opportunity opportunity=null;
		try {
			
			leadId = Integer.parseInt((String) session.get("crmLeadId"));
			Logger.debug("leadId " + leadId);
		
			 opportunity=couchBaseOperation.getOpporunityData(leadId+"");
			 appBasicDetails=opportunity.getApplicants().get(0);

			
		} catch (Exception e) {
			Logger.error("Error in getting values from session " + e);
		}
		return ok(mortgagePage1a.render(opportunity,appBasicDetails));
	}

	public static Result mortgageBackPage6a() {
		Logger.debug("*** inside mortgageBackPage6a ***");
		String applicantID = "";

		Session session = Http.Context.current().session();
		String mobilePhone = "";
		try {
			applicantID = (String) session.get("applicantID");
			mobilePhone = session.get("isMobile");
		} catch (Exception e) {
			Logger.error("Error in getting values from session " + e);
		}
		PersonalInfoDBOperation personalInfoDB = new PersonalInfoDBOperation(
				applicantID);
		PersonalInfo personalInfo = personalInfoDB.getPersonalInfo();
		Logger.debug("Mobile " + personalInfo.getMobilePhone()
				+ "\n Work phone " + personalInfo.getWorkPhone()
				+ "\n home phone " + personalInfo.getHomePhone()
				+ "\n bityhday " + personalInfo.getBirthDay()
				+ "\n Social insurance: " + personalInfo.getSocialInsurance()
				+ "\n Relation " + personalInfo.getRelationStatus()
				+ "\n dependant " + personalInfo.getDependents()
				+ "\n are you " + personalInfo.getAreUCanadianRes()
				+ "\n moved " + personalInfo.getMovedCanada());
		return ok(mortgagePage6a.render(personalInfo));
	}

	public static Result mortgageBackPage6b() {
		Logger.debug("*** inside mortgageBackPage6b ***");
		String coApplicantID = "";

		int leadId = 0;
		Session session = Http.Context.current().session();
		String mobilePhone = "";
		try {
			mobilePhone = session.get("isMobile");

			coApplicantID = session.get("applicantID2");
		} catch (Exception e) {
			Logger.error("Error in getting values from session " + e);
		}
		PersonalInfoDBOperation personalInfoDB = new PersonalInfoDBOperation(
				coApplicantID);
		PersonalInfo personalInfo = personalInfoDB.getCoAppPersonalInfo();
		Logger.debug("Mobile " + personalInfo.getCoMobilePhone()
				+ "\n Work phone " + personalInfo.getCoWorkPhone()
				+ "\n home phone " + personalInfo.getCoHomePhone()
				+ "\n bityhday " + personalInfo.getCoBirthDay()
				+ "\n Social insurance: " + personalInfo.getCoSocialInsurance()
				+ "\n Relation " + personalInfo.getCoRelationStatus()
				+ "\n dependant " + personalInfo.getCoDependents()
				+ "\n are you " + personalInfo.getCoAreUCanadianRes()
				+ "\n moved " + personalInfo.getCoMovedCanada());
		return ok(mortgagePage6b.render(personalInfo));
	}

	public static Result mortgageBackPage7a() {
		Logger.debug("*** inside mortgageBackPage7a ***");

		String applicantID = "";

		Session session = Http.Context.current().session();
		String mobilePhone = "";
		try {
			applicantID = (String) session.get("applicantID");
			mobilePhone = session.get("isMobile");

		} catch (Exception e) {
			Logger.error("Error in getting values from session " + e);
		}
		AddressDBOperation addressDB = new AddressDBOperation(applicantID);
		ApplicantAddressParameter7 addresses = addressDB
				.getApplAddressDetails();
		Logger.debug("CurrentAddress1 "
				+ addresses.getApplicantCurrentAddress() + "\n moved in1 "
				+ addresses.getApplicantMovedIn1()
				+ "\n Total month and years1 "
				+ addresses.getApplicantTotalcurrentMonths() + "\n address2 "
				+ addresses.getApplicantPriorAddress1() + "\n momvedIn2: "
				+ addresses.getApplicantMovedIn2()
				+ "\n total month and year prior2  "
				+ addresses.getApplicantTotalpriorcurrentmonths1()
				+ "\n address3 " + addresses.getApplicantPriorAddress2()
				+ "\n momvedIn3: " + addresses.getApplicantMovedIn3()
				+ "\n total month and year prior3  "
				+ addresses.getApplicantTotalpriorcurrentmonths2());

		return ok(mortgagePage7a.render(addresses));
	}

	public static Result mortgageBackPage7b() {
		Logger.debug("*** inside mortgageBackPage7a ***");
		String coApplicantID = "";

		int leadId = 0;
		Session session = Http.Context.current().session();
		String mobilePhone = "";
		try {
			mobilePhone = session.get("isMobile");

			coApplicantID = session.get("applicantID2");
		} catch (Exception e) {
			Logger.error("Error in getting values from session " + e);
		}
		AddressDBOperation addressDB = new AddressDBOperation(coApplicantID);
		ApplicantAddressParameter7 addresses = new ApplicantAddressParameter7();
		// ApplicantAddressParameter7 addresses =
		// addressDB.getApplAddressDetails();
		// Logger.debug(
		// "CurrentAddress1 "+addresses.getApplicantCurrentAddress()+"\n moved in1 "+addresses.getApplicantMovedIn1()+"\n Total month and years1 "+addresses.getApplicantTotalcurrentMonths()+
		// "\n address2 "+addresses.getApplicantPriorAddress1()+"\n momvedIn2: "+addresses.getApplicantMovedIn2()+"\n total month and year prior2  "+addresses.getApplicantTotalpriorcurrentmonths1()+
		// "\n address3 "+addresses.getApplicantPriorAddress2()+"\n momvedIn3: "+addresses.getApplicantMovedIn3()+"\n total month and year prior3  "+addresses.getApplicantTotalpriorcurrentmonths2()
		// );

		return ok(mortgagePage7a.render(addresses));
	}

	public static Result privacyPolice() {
		return ok(privacypolicy.render(""));
	}

	public static Result mortgagePage33() {
		DynamicForm dynamicForm = form().bindFromRequest();
		String Employed = dynamicForm.get("employee");
		List<AssetsParam> vehicleList = new ArrayList();
		List<AssetsParam> bankAccList = new ArrayList();
		List<AssetsParam> rrsptList = new ArrayList();
		List<AssetsParam> investmntList = new ArrayList();
		List<AssetsParam> otherList = new ArrayList();
		TotalAssets totalAssets = new TotalAssets();
		totalAssets.setVehicle(vehicleList);
		totalAssets.setBankAccount(bankAccList);
		totalAssets.setRrsp(rrsptList);
		totalAssets.setInvestments(investmntList);
		totalAssets.setOthers(otherList);
		//
		// return ok(mortgagePage10Assets.render("","xxx","yyy",totalAssets));
		/*
		 * ApplicantBasicDetails appBasicDetails=new ApplicantBasicDetails();
		 * return ok(mortgagePage1b.render(appBasicDetails));
		 */
		// return ok(mortgagePage2Ref.render("",""));
		// ApplicantAddressParameter7 address=new ApplicantAddressParameter7();
		// return ok(mortgagePage7b.render(""));
		/*
		 * return ok(mortgagePage9.render("",new EmployeIncomeTypeParam(),new
		 * SelfEmployeIncomeTypeParam(),new PensionIncomeTypeParam(), new
		 * InvestmentsIncomeTypeParam(),new MaternityIncomeTypeParam(),new
		 * VehicleAllowIncomeTypeParam(), new LivingAllowIncomeTypeParam(),new
		 * CommissionIncomeTypeParam(),new BonusIncomeTypeParam(),new
		 * OtherIncomeTypeParam(),"CoApp's Name"));
		 */
		/*
		 * return ok(mortgagePage8.render("",new EmployeIncomeTypeParam(),new
		 * SelfEmployeIncomeTypeParam(),new PensionIncomeTypeParam(), new
		 * InvestmentsIncomeTypeParam(),new MaternityIncomeTypeParam(),new
		 * VehicleAllowIncomeTypeParam(), new LivingAllowIncomeTypeParam(),new
		 * CommissionIncomeTypeParam(),new BonusIncomeTypeParam(),new
		 * OtherIncomeTypeParam(),"yes"));
		 */
		// return ok(mortgagePage3.render("","","","","","","","",""));

		// return ok(mortgagePage11Properties.render("dummy","xxx" ,"yyy", new
		// ArrayList<ApplicantProperties>()));

		// return ok(mortgagePage4.render("","","","",""));
		// return ok(mortgagePage5b.render(""));
		// PersonalInfo personal=new PersonalInfo();
		// return ok(mortgagePage6a.render(personal));
		// return ok(mortgagePage2Pre.render("","","","",""));
		// return ok(mortgagePage12Disclose.render("","yes",new
		// Integer(0),"xxx","yyy"));

		// return
		// ok(MortgageApplicationSucess.render("Thank you for completing the Visdom Mortgage Application.  We will be in touch with you very soon."));
		// return ok(mortgagePage1.render("","","","","","","","","","",""));
		// return ok(mortgagePage1a.render(new ApplicantBasicDetails()));
		// return ok(mortgagePage1b.render(new ApplicantBasicDetails()));
		// return ok(mortgagePage6.render("yes", "xxx", "yyy", "", "", "", "",
		// "", "", "", "", "", "", "", "", "", "", "", "", "", ""));
		// return ok(mortgagePage7Address.render("yes", "xxx", "yyy",new
		// ApplicantAddressParameter7(),new CoApplicantAddressParameter7()));

		/*
		 * TotalAssets total = new TotalAssets(); total.setBankAccount(new
		 * ArrayList<AssetsParam>()); total.setVehicle(new
		 * ArrayList<AssetsParam>()); total.setInvestments(new
		 * ArrayList<AssetsParam>()); total.setRrsp(new
		 * ArrayList<AssetsParam>()); total.setOthers(new
		 * ArrayList<AssetsParam>()); return
		 * ok(mortgagePage10Assets.render("","xxx","yyy",total));
		 */

		// return ok(mortgagePage2Pre.render("","",new LendingTerm()));
		return ok(mortgagePage2Pur.render(new LendingTerm(), "yes"));
		// return ok(mortgagePage2Ref.render(new LendingTerm(),""));
		// return ok(mortgagePage222.render("","yes",new
		// Integer(12),"xxx","yyy"));
	}

}
