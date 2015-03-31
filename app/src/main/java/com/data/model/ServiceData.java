package com.data.model;

public class ServiceData {

	private String serviceID = "";
	private String serviceName= "";
	
	
	public ServiceData(String serviceID, String serviceName){
		this.serviceID = serviceID;
		this.serviceName = serviceName;
	}
	
	public String getServiceID(){return serviceID;}
	public String getServiceName(){return serviceName;}
}
