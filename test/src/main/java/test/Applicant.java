package controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)

public class Applicant {
	
	
	public int id;
	public String applicantId;
	public List<String> collections;
	public int payment;
	
	@Override
	public String toString() {
		return "Applicant [id=" + id + ", applicantId=" + applicantId
				+ ", collections=" + collections + ", payment=" + payment
				+ ", total_rental_income=" + total_rental_income
				+ ", crp_3_score=" + crp_3_score + ", total_asset="
				+ total_asset + ", bankruptcy_discharge_date="
				+ bankruptcy_discharge_date + ", applicant_name="
				+ applicant_name + ", monthly_support_payment="
				+ monthly_support_payment + ", immigration_date="
				+ immigration_date + ", orderly_debt_payment="
				+ orderly_debt_payment + ", primary1=" + primary1
				+ ", consentDateTime=" + consentDateTime + ", non_resident="
				+ non_resident + ",  signature=" + signature
				+ ", work=" + work + ", email_personal=" + email_personal
				+ ", ers2Score=" + ers2Score + ", cell=" + cell
				+ ", totalNetWorth=" + totalNetWorth + ", bankruptcy="
				+ bankruptcy + ", messageIsFollower=" + messageIsFollower
				+ ", totalIncome=" + totalIncome + ", totalInquires="
				+ totalInquires + ", home=" + home + ", applicant_last_name="
				+ applicant_last_name + ", creditReport=" + creditReport
				+ ", odpDischargeDate=" + odpDischargeDate
				+ ", includeInOpportunity=" + includeInOpportunity
				+ ", monthlychildsupport=" + monthlychildsupport
				+ ", beacon_9_score=" + beacon_9_score + ", beacon_5_score="
				+ beacon_5_score + ", money=" + money + ", identity_attached="
				+ identity_attached + ", contact_record=" + contact_record
				+ ", ninqidx=" + ninqidx + ", total_employed_income="
				+ total_employed_income + ", email_work=" + email_work
				+ ", sin=" + sin + ", dob=" + dob + ", signatureIp="
				+ signatureIp + ", passport=" + passport
				+ ", totalOtherIncome=" + totalOtherIncome + ", messageUnread="
				+ messageUnread + ", messageSummary=" + messageSummary
				+ ", totalSelfEmployed=" + totalSelfEmployed
				+ ", relationship_status=" + relationship_status
				+ ", best_number=" + best_number + ", mortgageIds="
				+ mortgageIds + ", incomeIds=" + incomeIds + ", incomes="
				+ incomes + ", propertyIds=" + propertyIds + ", properties="
				+ properties + ", assetIds=" + assetIds + ", collectionIds="
				+ collectionIds + ", collection=" + collection
				+ ", liabilityIds=" + liabilityIds + ", totalApplicantIncome="
				+ totalApplicantIncome + ", totalApplicantLiabilities="
				+ totalApplicantLiabilities
				+ ", totalApplicantLiabilityPayments="
				+ totalApplicantLiabilityPayments + ", totalApplicantAssets="
				+ totalApplicantAssets + ", totalApplicantMortgages="
				+ totalApplicantMortgages + ", totalApplicantMortgagePayments="
				+ totalApplicantMortgagePayments
				+ ", totalApplicantCollections=" + totalApplicantCollections
				+ ", totalApplicantPropertyTaxes="
				+ totalApplicantPropertyTaxes + ", totalApplicantCondoFees="
				+ totalApplicantCondoFees + "]";
	}
	public int getPayment() {
		return payment;
	}
	public void setPayment(int payment) {
		this.payment = payment;
	}
	public List<String> getCollections() {
		return collections;
	}
	public void setCollections(List<String> collections) {
		this.collections = collections;
	}
	public Boolean getOrderly_debt_payment() {
		return orderly_debt_payment;
	}
	public void setOrderly_debt_payment(Boolean orderly_debt_payment) {
		this.orderly_debt_payment = orderly_debt_payment;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTotal_rental_income() {
		return total_rental_income;
	}
	public void setTotal_rental_income(String total_rental_income) {
		this.total_rental_income = total_rental_income;
	}
	public Integer getCrp_3_score() {
		return crp_3_score;
	}
	public void setCrp_3_score(Integer crp_3_score) {
		this.crp_3_score = crp_3_score;
	}
	public Integer getTotal_asset() {
		return total_asset;
	}
	public void setTotal_asset(Integer total_asset) {
		this.total_asset = total_asset;
	}
	
	public String getApplicant_name() {
		return applicant_name;
	}
	public void setApplicant_name(String applicant_name) {
		this.applicant_name = applicant_name;
	}
	public Double getMonthly_support_payment() {
		return monthly_support_payment;
	}
	public void setMonthly_support_payment(Double monthly_support_payment) {
		this.monthly_support_payment = monthly_support_payment;
	}
	public Date getImmigration_date() {
		return immigration_date;
	}
	public void setImmigration_date(Date immigration_date) {
		this.immigration_date = immigration_date;
	}
	public Boolean getNon_resident() {
		return non_resident;
	}
	public void setNon_resident(Boolean non_resident) {
		this.non_resident = non_resident;
	}
	
	public String getEmail_personal() {
		return email_personal;
	}
	public void setEmail_personal(String email_personal) {
		this.email_personal = email_personal;
	}
	public String getApplicant_last_name() {
		return applicant_last_name;
	}
	public void setApplicant_last_name(String applicant_last_name) {
		this.applicant_last_name = applicant_last_name;
	}
	public Integer getBeacon_9_score() {
		return beacon_9_score;
	}
	public void setBeacon_9_score(Integer beacon_9_score) {
		this.beacon_9_score = beacon_9_score;
	}
	public Integer getBeacon_5_score() {
		return beacon_5_score;
	}
	public void setBeacon_5_score(Integer beacon_5_score) {
		this.beacon_5_score = beacon_5_score;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public Boolean getIdentity_attached() {
		return identity_attached;
	}
	public void setIdentity_attached(Boolean identity_attached) {
		this.identity_attached = identity_attached;
	}
	public String getContact_record() {
		return contact_record;
	}
	public void setContact_record(String contact_record) {
		this.contact_record = contact_record;
	}
	public Integer getNinqidx() {
		return ninqidx;
	}
	public void setNinqidx(Integer ninqidx) {
		this.ninqidx = ninqidx;
	}
	public String getTotal_employed_income() {
		return total_employed_income;
	}
	public void setTotal_employed_income(String total_employed_income) {
		this.total_employed_income = total_employed_income;
	}
	public String getEmail_work() {
		return email_work;
	}
	public void setEmail_work(String email_work) {
		this.email_work = email_work;
	}
	public String getRelationship_status() {
		return relationship_status;
	}
	public void setRelationship_status(String relationship_status) {
		this.relationship_status = relationship_status;
	}
	public String getBest_number() {
		return best_number;
	}
	public void setBest_number(String best_number) {
		this.best_number = best_number;
	}
	public String getApplicantId() {
		return applicantId;
	}
	public void setApplicantId(String applicantId) {
		this.applicantId = applicantId;
	}
		//total_rental_income
		//@ERP( name = "total_rental_income" )
		public String total_rental_income;

		//crp_3_score
		//@ERP( name = "crp_3_score" )
		public Integer crp_3_score;

		//total_asset
		//@ERP( name = "total_asset" )
		public Integer total_asset;

		//bankruptcy_discharge_date
		//@Temporal(TemporalType.TIMESTAMP)
		//@ERP( name = "bankruptcy_discharge_date" )
		public Date bankruptcy_discharge_date;

		//applicant_name
		//@ERP( name = "applicant_name" )
		public String applicant_name;

		//monthly_support_payment
		//@ERP( name = "monthly_support_payment" )
		public Double monthly_support_payment;

		//immigration_date
		//@ERP( name = "immigration_date" )
		public Date immigration_date;

		//orderly_debt_payment
		//@ERP( name = "orderly_debt_payment" )
		public Boolean orderly_debt_payment;

		//primary
		public Boolean primary1;

		//consent_dateTime
		//@ERP( name = "consent_dateTime" )
		public Date consentDateTime;

		//non_resident
		//@ERP( name = "non_resident" )
		public Boolean non_resident;

		//date_time_bureau_obtained
		//@ERP( name = "date_time_bureau_obtained" )

		//signature
		public String signature;

		//work
		public String work;

		//email_personal
		//@ERP( name = "email_personal" )
		public String email_personal;

		//ers_2_score
		//@ERP( name = "ers_2_score" )
		public Integer ers2Score;

		//cell
		public String cell;

		//total_net_worth
		//@ERP( name = "total_net_worth" )
		public String totalNetWorth;

		//bankruptcy
		public Boolean bankruptcy;

		//message_is_follower
		//@ERP( name = "message_is_follower" )
		public Boolean messageIsFollower;

		//total_income
		//@ERP( name = "total_income" )
		public Double totalIncome;

		//total_inquires
		//@ERP( name = "total_inquires" )
		public Integer totalInquires;

		//home
		public String home;

		//applicant_last_name
		//@ERP( name = "applicant_last_name" )
		public String applicant_last_name;

		//credit_report
		//@Column(length=9999)
		//@ERP( name = "credit_report" )
		public String creditReport;

		//odp_discharge_date
		//@Temporal(TemporalType.TIMESTAMP)
		//@ERP( name = "odp_discharge_date" )
		public Date odpDischargeDate;

		//include_in_opportunity
		//@ERP( name = "include_in_opportunity" )
		public Boolean includeInOpportunity;

		//monthlychildsupport
		public Double monthlychildsupport;

		//beacon_9_score
		//@ERP( name = "beacon_9_score" )
		public Integer beacon_9_score;

		//beacon_5_score
		//@ERP( name = "beacon_5_score" )
		public Integer beacon_5_score;

		//money
		public String money;

		//identity_attached
		//@ERP( name = "identity_attached" )
		public Boolean identity_attached;

		//contact_record
		//@ERP( name = "contact_record" )
		public String contact_record;

		//ninqidx
		public Integer ninqidx;

		//total_employed_income
		//@ERP( name = "total_employed_income" )
		public String total_employed_income;

		//email_work
		//@ERP( name = "email_work" )
		public String email_work;

		//sin
		public String sin;

		//dob
		//@Temporal(TemporalType.TIMESTAMP)
		public Date dob;

		//signature_ip
		//@ERP( name = "signature_ip" )
		public String signatureIp;

		//passport
		public String passport;

		//total_other_income
		//@ERP( name = "total_other_income" )
		public String totalOtherIncome;

		//message_unread
		//@ERP( name = "message_unread" )
		public Boolean messageUnread;

		//message_summary
		//@Column(length=9999)
		//@ERP( name = "message_summary" )
		public String messageSummary;

		//total_self_employed
		//@ERP( name = "total_self_employed" )
		public String totalSelfEmployed;

		/*********************************selection type******************************/
		//relationship_status
		//@ERP( type = OpenERPType.selection, name = "relationship_status" )
		public String relationship_status;

		//best_number
		//@ERP( type = OpenERPType.selection, name = "best_number" )
		public String best_number;

		/*********************************one2many type******************************/

		//many to one : mapping table : applicant_mortgage(com.syml.domain.Mortgage)-->applicant_id
		//@ERP(name="mortgage", type = OpenERPType.one2many )
		//@JsonIgnore
		//@Transient
		public List<Integer> mortgageIds = new ArrayList<Integer>();
		
		//@Transient
		//public List<Mortgage> mortgages = new ArrayList<Mortgage>();

		//many to one : mapping table : income_employer(com.syml.domain.Income)-->applicant_id
		//@ERP(name="incomes", type = OpenERPType.one2many )
		//@JsonIgnore
		//@Transient
		public List<Integer> incomeIds = new ArrayList<Integer>();
		
		//@Transient
		//@ERP(type = OpenERPType.ignore )
		public List<Income> incomes = new ArrayList<Income>();

		//many to one : mapping table : applicant_property(com.syml.domain.Property)-->applicant_id
		//@ERP(name="properties", type = OpenERPType.one2many )
		//@JsonIgnore
		//@Transient
		public List<Integer> propertyIds = new ArrayList<Integer>();
		
		//@Transient
		//@ERP(type = OpenERPType.ignore )
		public List<Property> properties = new ArrayList<Property>();

		//many to one : mapping table : crm_asset(com.syml.domain.Asset)-->opportunity_id
		//@ERP(name="asset_ids", type = OpenERPType.one2many )
		//@JsonIgnore
		//@Transient
		public List<Integer> assetIds = new ArrayList<Integer>();
		
		//@Transient
		//@ERP(type = OpenERPType.ignore )
	//	public List<Asset> assets = new ArrayList<Asset>();

		//many to one : mapping table : applicant_collection(com.syml.domain.Collection)-->applicant_id
		//@ERP(name="collection", type = OpenERPType.one2many )
		//@JsonIgnore
		//@Transient
		public List<Integer> collectionIds = new ArrayList<Integer>();
		
		
		//@Transient
		public List<Collection> collection = null;

		//many to one : mapping table : applicant_liabilities(com.syml.domain.Liability)-->applicant_id
		//@ERP(name="liabilities", type = OpenERPType.one2many )
		//@JsonIgnore
		//@Transient
		public List<Integer> liabilityIds = new ArrayList<Integer>();
		
		
		// There are a few variables which are needed to hold totals for the applicant.  Some of these values may be sent back to OpenERP	
		public double totalApplicantIncome;
		public double totalApplicantLiabilities;
		
	
		public Boolean getPrimary1() {
			return primary1;
		}
		public void setPrimary1(Boolean primary1) {
			this.primary1 = primary1;
		}
		public Date getConsentDateTime() {
			return consentDateTime;
		}
		public void setConsentDateTime(Date consentDateTime) {
			this.consentDateTime = consentDateTime;
		}
	
		public String getSignature() {
			return signature;
		}
		public void setSignature(String signature) {
			this.signature = signature;
		}
		public String getWork() {
			return work;
		}
		public void setWork(String work) {
			this.work = work;
		}
	
		public Integer getErs2Score() {
			return ers2Score;
		}
		public void setErs2Score(Integer ers2Score) {
			this.ers2Score = ers2Score;
		}
		public String getCell() {
			return cell;
		}
		public void setCell(String cell) {
			this.cell = cell;
		}
		public String getTotalNetWorth() {
			return totalNetWorth;
		}
		public void setTotalNetWorth(String totalNetWorth) {
			this.totalNetWorth = totalNetWorth;
		}
		public Boolean getBankruptcy() {
			return bankruptcy;
		}
		public void setBankruptcy(Boolean bankruptcy) {
			this.bankruptcy = bankruptcy;
		}
		public Boolean getMessageIsFollower() {
			return messageIsFollower;
		}
		public void setMessageIsFollower(Boolean messageIsFollower) {
			this.messageIsFollower = messageIsFollower;
		}
		public Double getTotalIncome() {
			return totalIncome;
		}
		public void setTotalIncome(Double totalIncome) {
			this.totalIncome = totalIncome;
		}
		public Integer getTotalInquires() {
			return totalInquires;
		}
		public void setTotalInquires(Integer totalInquires) {
			this.totalInquires = totalInquires;
		}
		public String getHome() {
			return home;
		}
		public void setHome(String home) {
			this.home = home;
		}
	
		public String getCreditReport() {
			return creditReport;
		}
		public void setCreditReport(String creditReport) {
			this.creditReport = creditReport;
		}
		public Date getOdpDischargeDate() {
			return odpDischargeDate;
		}
		public void setOdpDischargeDate(Date odpDischargeDate) {
			this.odpDischargeDate = odpDischargeDate;
		}
		public Boolean getIncludeInOpportunity() {
			return includeInOpportunity;
		}
		public void setIncludeInOpportunity(Boolean includeInOpportunity) {
			this.includeInOpportunity = includeInOpportunity;
		}
		public Double getMonthlychildsupport() {
			return monthlychildsupport;
		}
		public void setMonthlychildsupport(Double monthlychildsupport) {
			this.monthlychildsupport = monthlychildsupport;
		}
	
		public String getSin() {
			return sin;
		}
		public void setSin(String sin) {
			this.sin = sin;
		}
		public Date getDob() {
			return dob;
		}
		public void setDob(Date dob) {
			this.dob = dob;
		}
		public String getSignatureIp() {
			return signatureIp;
		}
		public void setSignatureIp(String signatureIp) {
			this.signatureIp = signatureIp;
		}
		public String getPassport() {
			return passport;
		}
		public void setPassport(String passport) {
			this.passport = passport;
		}
		public String getTotalOtherIncome() {
			return totalOtherIncome;
		}
		public void setTotalOtherIncome(String totalOtherIncome) {
			this.totalOtherIncome = totalOtherIncome;
		}
		public Boolean getMessageUnread() {
			return messageUnread;
		}
		public void setMessageUnread(Boolean messageUnread) {
			this.messageUnread = messageUnread;
		}
		public String getMessageSummary() {
			return messageSummary;
		}
		public void setMessageSummary(String messageSummary) {
			this.messageSummary = messageSummary;
		}
		public String getTotalSelfEmployed() {
			return totalSelfEmployed;
		}
		public void setTotalSelfEmployed(String totalSelfEmployed) {
			this.totalSelfEmployed = totalSelfEmployed;
		}
		
		public List<Integer> getMortgageIds() {
			return mortgageIds;
		}
		public void setMortgageIds(List<Integer> mortgageIds) {
			this.mortgageIds = mortgageIds;
		}
		
		public List<Integer> getIncomeIds() {
			return incomeIds;
		}
		public void setIncomeIds(List<Integer> incomeIds) {
			this.incomeIds = incomeIds;
		}
		public List<Income> getIncomes() {
			return incomes;
		}
		public void setIncomes(List<Income> incomes) {
			this.incomes = incomes;
		}
		public List<Integer> getPropertyIds() {
			return propertyIds;
		}
		public void setPropertyIds(List<Integer> propertyIds) {
			this.propertyIds = propertyIds;
		}
		public List<Property> getProperties() {
			return properties;
		}
		public void setProperties(List<Property> properties) {
			this.properties = properties;
		}
		public List<Integer> getAssetIds() {
			return assetIds;
		}
		public void setAssetIds(List<Integer> assetIds) {
			this.assetIds = assetIds;
		}
		
		public List<Integer> getCollectionIds() {
			return collectionIds;
		}
		public void setCollectionIds(List<Integer> collectionIds) {
			this.collectionIds = collectionIds;
		}
		public List<Collection> getCollection() {
			return collection;
		}
		public void setCollection(List<Collection> collection) {
			this.collection = collection;
		}
		public List<Integer> getLiabilityIds() {
			return liabilityIds;
		}
		public void setLiabilityIds(List<Integer> liabilityIds) {
			this.liabilityIds = liabilityIds;
		}
		public double getTotalApplicantIncome() {
			return totalApplicantIncome;
		}
		public void setTotalApplicantIncome(double totalApplicantIncome) {
			this.totalApplicantIncome = totalApplicantIncome;
		}
		public double getTotalApplicantLiabilities() {
			return totalApplicantLiabilities;
		}
		public void setTotalApplicantLiabilities(double totalApplicantLiabilities) {
			this.totalApplicantLiabilities = totalApplicantLiabilities;
		}
		public double getTotalApplicantLiabilityPayments() {
			return totalApplicantLiabilityPayments;
		}
		public void setTotalApplicantLiabilityPayments(
				double totalApplicantLiabilityPayments) {
			this.totalApplicantLiabilityPayments = totalApplicantLiabilityPayments;
		}
		public double getTotalApplicantAssets() {
			return totalApplicantAssets;
		}
		public void setTotalApplicantAssets(double totalApplicantAssets) {
			this.totalApplicantAssets = totalApplicantAssets;
		}
		public double getTotalApplicantMortgages() {
			return totalApplicantMortgages;
		}
		public void setTotalApplicantMortgages(double totalApplicantMortgages) {
			this.totalApplicantMortgages = totalApplicantMortgages;
		}
		public double getTotalApplicantMortgagePayments() {
			return totalApplicantMortgagePayments;
		}
		public void setTotalApplicantMortgagePayments(
				double totalApplicantMortgagePayments) {
			this.totalApplicantMortgagePayments = totalApplicantMortgagePayments;
		}
		public double getTotalApplicantCollections() {
			return totalApplicantCollections;
		}
		public void setTotalApplicantCollections(double totalApplicantCollections) {
			this.totalApplicantCollections = totalApplicantCollections;
		}
		public double getTotalApplicantPropertyTaxes() {
			return totalApplicantPropertyTaxes;
		}
		public void setTotalApplicantPropertyTaxes(double totalApplicantPropertyTaxes) {
			this.totalApplicantPropertyTaxes = totalApplicantPropertyTaxes;
		}
		public double getTotalApplicantCondoFees() {
			return totalApplicantCondoFees;
		}
		public void setTotalApplicantCondoFees(double totalApplicantCondoFees) {
			this.totalApplicantCondoFees = totalApplicantCondoFees;
		}
		public double totalApplicantLiabilityPayments;
		public double totalApplicantAssets;
		public double totalApplicantMortgages;
		public double totalApplicantMortgagePayments;
		public double totalApplicantCollections;
		public double totalApplicantPropertyTaxes;
		public double totalApplicantCondoFees;

}
