package com.data.model;

public class Complain {

	private String complaintID = "";
	private String complaintCode= "";
	private String complaintStatus= "";
	private String siteCode = "";
	private String complaintCategoryName = "";
	
	
	public Complain(String complaintID, String complaintCode, String complaintStatus, String siteCode, String complaintCategoryName){
		this.complaintID = complaintID;
		this.complaintCode = complaintCode;
		this.complaintStatus = complaintStatus;
		this.siteCode = siteCode;
		this.complaintCategoryName = complaintCategoryName;
	}
	
	public String getComplaintID(){return complaintID;}
	
	public String getComplaintCode(){return complaintCode;}
	
	public String getComplaintStatus(){return complaintStatus;}
	
	public String getSiteCode(){return siteCode;}
	
	public String getComplaintCategoryName(){return complaintCategoryName;}
	
}
