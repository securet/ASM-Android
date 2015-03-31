package com.data.model;

public class SitesData {

	private String siteID = "";
	private String siteName= "";
	
	
	public SitesData(String siteID, String siteName){
		this.siteID = siteID;
		this.siteName = siteName;
	}
	
	public String getSiteID(){return siteID;}
	
	public String getSiteName(){return siteName;}
}
