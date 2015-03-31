package com.data.model;

public class SubCategoryData {

	private String subCategoryID = "";
	private String subCategoryName= "";
	
	
	public SubCategoryData(String subCategoryID, String subCategoryName){
		this.subCategoryID = subCategoryID;
		this.subCategoryName=subCategoryName;
	}
	
	public String getSubCategoryID(){return subCategoryID;}
	public String getSubCategoryName(){return subCategoryName;}
}
