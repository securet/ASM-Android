package com.data.model;

public class Comments {

	private String Name = "";
	private String Comments= "";
	private String Date= "";
	
	
	public Comments(String Name, String Comments, String Date){
		this.Name = Name;
		this.Comments = Comments;
		this.Date = Date;
		
	}
	
	public String getName(){return Name;}
	
	public String getComments(){return Comments;}
	
	public String getDate(){return Date;}
		
}
