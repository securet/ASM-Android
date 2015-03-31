package com.project.asm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

import com.project.asm.RestClient.RequestMethod;

public class API {

	
	
	// ================================================
	// WEB SERVICE DETAILS AND METHODS
	// ================================================
	private static final String TAG = "ASM_API";
	// Server Deails
	private static String SOAP_NAMESPACE = "http://securet.in/";
	private static String SOAP_URL = "http://54.186.184.239/ASM_Web/WS/GetASMData.asmx";
	
	// For ValidateEmployee
	/*private static String SOAP_METHOD_LOGIN = "ValidateEmployee";
	private static String SOAP_ACTION_LOGIN = SOAP_NAMESPACE + SOAP_METHOD_LOGIN;*/
	
	// For GetSitesbyEmployeeID
	/*private static String SOAP_METHOD_GET_SITES = "GetSitesbyEmployeeID";
	private static String SOAP_ACTION_GET_SITES = SOAP_NAMESPACE + SOAP_METHOD_GET_SITES;*/
	
	// For GetAllCategory
	/*private static String SOAP_METHOD_GET_CATEGORY = "GetAllCategory";
	private static String SOAP_ACTION_GET_CATEGORY = SOAP_NAMESPACE + SOAP_METHOD_GET_CATEGORY;*/
	
	// For GetSubCategoryByCategoryID
	/*private static String SOAP_METHOD_GET_SUB_CATEGORY = "GetSubCategoriesbyCategoryID";
	private static String SOAP_ACTION_GET_SUB_CATEGORY = SOAP_NAMESPACE + SOAP_METHOD_GET_SUB_CATEGORY;*/
	
	
	// For GetSubCategoryByCategoryID
	/*private static String SOAP_METHOD_GET_SEVERITY = "GetAllSeverity";
	private static String SOAP_ACTION_GET_SEVERITY = SOAP_NAMESPACE + SOAP_METHOD_GET_SEVERITY;*/
	
	/*// For InsertComplaints
	private static String SOAP_METHOD_INSERT_COMPLAINTS = "InsertComplaints";
	private static String SOAP_ACTION_INSERT_COMPLAINTS = SOAP_NAMESPACE + SOAP_METHOD_INSERT_COMPLAINTS;*/

	// For InsertComplaints
	/*private static String SOAP_METHOD_VIEW_COMPLAINTS = "GetComplaintsforEmployee";
	private static String SOAP_ACTION_VIEW_COMPLAINTS = SOAP_NAMESPACE + SOAP_METHOD_VIEW_COMPLAINTS;*/
	
	// For GetComplaintByComplaintID
	private static String SOAP_METHOD_GET_COMPLAINT = "GetComplaintDetailsbyComplaintID";
	private static String SOAP_ACTION_GET_COMPLAINT = SOAP_NAMESPACE + SOAP_METHOD_GET_COMPLAINT;
	
	// For UpdateComplaint
	private static String SOAP_METHOD_UPDATE_COMPLAINT = "UpdateComplaints";
	private static String SOAP_ACTION_UPDATE_COMPLAINT = SOAP_NAMESPACE + SOAP_METHOD_UPDATE_COMPLAINT;
	
	// For GetAllComment
	private static String SOAP_METHOD_ALL_COMMENTS = "GetAllCommentsforComplaint";
	private static String SOAP_ACTION_ALL_COMMENTS = SOAP_NAMESPACE + SOAP_METHOD_ALL_COMMENTS;
	
	// For Logout
	/*private static String SOAP_METHOD_LOGOUT = "LogOut";
	private static String SOAP_ACTION_LOGOUT = SOAP_NAMESPACE + SOAP_METHOD_LOGOUT;*/
	
	// For debugs all logs...
	public static int LOGLEVEL = 1; // set -1 to stop debugging in the app
	public static boolean WARN = LOGLEVEL > 1; // set LOGLEVEL = 2
	public static boolean DEBUG = LOGLEVEL > 0; // set LOGLEVEL = 1
	
	public static String bugsenseAPI = "56253889";
	
	
	public static String HOST = "http://asm.securet.in/";
	private static String REST = "rest/";
	
	private static String VALIDATE_USER = HOST+REST+"validateUser"; // done
	private static String SERVICE_TYPES = HOST+REST+"serviceTypes"; // done
	private static String GET_ISSUE_TYPES_FOR_SERVICE = HOST+REST+"getIssueTypesForService"; // done
	private static String GET_USER_SITES = HOST+REST+"getSitesForUser"; // done
	private static String GET_ALL_SEVERITY = HOST+REST+"severityTypes"; // done
	
	private static String INSERT_COMPLAINT = HOST+REST+"ticket/create"; // done
	
	private static String GET_COMPLAINT_DETAILS_BY_ID = HOST+REST+"/ticket/forId"; // done
	
	private static String GET_COMPLAINT_FOR_EMPLOYEE = HOST+REST+"ticket/forUser"; // done
	
	private static String UPDATE_COMPLAINT = HOST+REST+"ticket/update"; // 
	
	private static String GET_COMMENTS_FOR_COMPLAINT = HOST+REST+"ticket/history"; 
	 
	
	
	
	// --------------------------------------------------------------------------------
	// WEb SERVICE METHODS START FROM HERE
	// --------------------------------------------------------------------------------
	
	//===========================================================================
	//                     All WebService with SOAP Then REST
	//===========================================================================
	//===========================================================================
	
	/*// Get ValidateUser SOAP
	public static String LoginSOAP(String emailAddress, String password) {
		String responce = null;
		SoapObject request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_LOGIN);
		
		request.addProperty("emailID", emailAddress);
		request.addProperty("password", password);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
			androidHttpTransport.call(SOAP_ACTION_LOGIN, envelope);
			
			SoapObject result = (SoapObject) envelope.bodyIn;
			 
			
			if (result != null) {
				responce = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				responce = "";
			}
			
			System.out.println("The Responce is: "+responce.toString());
		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
		}
		return responce;
	}*/
	
	// Validate User REST
	public static String LoginRest(String userNameValue, String passwordValue) {
		String responce = null;
		RestClient client = new RestClient(VALIDATE_USER);
		
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);
			

		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			responce = client.getResponse();	
		}else{
			responce = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + responce);
		return responce;
	}
	
	
		
	// GET Sites
	/*public static String GetSitesSOAP(String employeeId, String tokenString) {
		String responce = null;
		SoapObject request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_GET_SITES);
		
		request.addProperty("EmployeeID", employeeId);
		request.addProperty("EmployeeToken", tokenString);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
			androidHttpTransport.call(SOAP_ACTION_GET_SITES, envelope);
			
			SoapObject result = (SoapObject) envelope.bodyIn;
			
			if (result != null) {
				responce = result.getProperty(0).toString();
				
				
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				responce = "";
			}
			
			System.out.println("The Responce is: "+responce.toString());
		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
		}
		return responce;
	}*/
		
	// get User sites REST
	public static String GetSitesRest( String userNameValue, String passwordValue) {
		String responce = null;
		
		RestClient client = new RestClient(GET_USER_SITES);
		
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);

		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			responce = client.getResponse();	
		}else{
			responce = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + responce);
		return responce;
	}
	
	/*// Get All Category
	public static String GetAllCategorySOAP(String tokenString) {
		String responce = null;
		SoapObject request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_GET_CATEGORY);
		
		request.addProperty("EmployeeToken", tokenString);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
			androidHttpTransport.call(SOAP_ACTION_GET_CATEGORY, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;
			if (result != null) {
				responce = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				responce = "";
			}
			
			System.out.println("The Responce is: "+responce.toString());
		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
		}
		return responce;
	}*/
	
	
	// Validate GetAllCategory REST
	public static String getAllCategoryRest(String userNameValue, String passwordValue) {
		String responce = null;
		RestClient client = new RestClient(SERVICE_TYPES);
		
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);

		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			responce = client.getResponse();	
		}else{
			responce = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + responce);
		return responce;
	}
	
	// Get Sub Category
	/*public static String GetSubCategorySOAP(String categoryID, String tokenString ) {
		String responce = null;
		SoapObject request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_GET_SUB_CATEGORY);
		
		request.addProperty("CategoryID", categoryID);
		request.addProperty("EmployeeToken", tokenString);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
			androidHttpTransport.call(SOAP_ACTION_GET_SUB_CATEGORY, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;
			if (result != null) {
				responce = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				responce = "";
			}
			
			System.out.println("The Responce is: "+responce.toString());
		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
		}
		return responce;
	}*/
	
	
	// Validate GetSUBCategory REST
	public static String getSubCategoryRest(String serviceIDValue, String userNameValue, String passwordValue) {
		String responce = null;
		RestClient client = new RestClient(GET_ISSUE_TYPES_FOR_SERVICE);
		
		client.AddParam("serviceTypeId", serviceIDValue);
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);

		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			responce = client.getResponse();	
		}else{
			responce = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + responce);
		return responce;
	}
	
	
	// Get All Severity
	/*public static String GetAllSeverity(String tokenString ) {
		String responce = null;
		SoapObject request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_GET_SEVERITY);
		
		request.addProperty("EmployeeToken", tokenString);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
			androidHttpTransport.call(SOAP_ACTION_GET_SEVERITY, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;
			if (result != null) {
				responce = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				responce = "";
			}
			
			System.out.println("The Responce is: "+responce.toString());
		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
		}
		return responce;
	}*/
	
	
	// Get All Severity
	public static String GetAllSeverityRest(String userNameValue,String passwordValue ) {
		String responce = null;
		RestClient client = new RestClient(GET_ALL_SEVERITY);
		
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);
	
		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);
	
		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			responce = client.getResponse();	
		}else{
			responce = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + responce);
		return responce;
	}
	
	// Insert COmplain
	/*public static String InsertComplaints(String EmployeeCode, String SiteID, String SubCategoryID, String Description, String SeverityName, String Filename, String GPSLong, String GPSLat, String tokenString ) {
		String responce = null;
		SoapObject request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_INSERT_COMPLAINTS);
		
		request.addProperty("EmployeeCode", EmployeeCode);
		request.addProperty("SiteID", SiteID);
		request.addProperty("SubCategoryID", SubCategoryID);
		request.addProperty("Description", Description);
		request.addProperty("SeverityName", SeverityName);
		request.addProperty("Filename", Filename);
		request.addProperty("GPSLong", GPSLong);
		request.addProperty("GPSLat", GPSLat);
		request.addProperty("EmployeeToken", tokenString);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
			androidHttpTransport.call(SOAP_ACTION_INSERT_COMPLAINTS, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;
			if (result != null) {
				responce = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				responce = "";
			}
			
			System.out.println("The Responce is: "+responce.toString());
		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
		}
		return responce;
	}*/
	
	
	
	// Insert COmplain Rest
	@SuppressWarnings("deprecation")
	public static String InsertComplaintsRest(
				String siteIDValue, 
				String serviceTypeIDValue, 
				String descriptionValue, 
				String enumerationIDValue,
				String FilenameValue, 
				String LongValue, 
				String LatValue, 
				String issueTypeIDValue,
				String userNameValue, 
				String passwordValue) {
		String responce = "";
		
		
		/*try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //bm.compress(CompressFormat.JPEG, 75, bos);
            byte[] data = bos.toByteArray();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(
                    "http://10.0.2.2/cfc/iphoneWebservice.cfc?returnformat=json&amp;method=testUpload");
            ByteArrayBody bab = new ByteArrayBody(data, "forest.jpg");
            // File file= new File("/mnt/sdcard/forest.png");
            // FileBody bin = new FileBody(file);
            
            
            MultipartEntity reqEntity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("uploaded", bab);
           // reqEntity.addPart("photoCaption", new StringEntity("sfsdfsdf"));
            postRequest.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();
 
            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            System.out.println("Response: " + s);
        } catch (Exception e) {
            // handle exception here
            Log.e(e.getClass().getName(), e.getMessage());
        }*/
		
		
		//CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httppost = new HttpPost(INSERT_COMPLAINT+"?j_username="+userNameValue+"&j_password="+passwordValue);
			
			//httppost.addHeader("Content-Type","multipart/form-data");
			//httppost.addHeader("Content-Disposition", "form-data; name = args");
						
			HttpEntity reqEntity;
			//StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);
			if(FilenameValue.toString().equals("")||FilenameValue.toString().equals(null)){
				
				reqEntity = MultipartEntityBuilder.create()
				//.addPart("ticketAttachments", bin)
				.addPart("site.siteId", new StringBody(siteIDValue))
				.addPart("serviceType.serviceTypeId", new StringBody(serviceTypeIDValue))
				.addPart("issueType.issueTypeId", new StringBody(issueTypeIDValue))
				.addPart("severity.enumerationId", new StringBody(enumerationIDValue))
				.addPart("description", new StringBody(descriptionValue))
				.addPart("latitude", new StringBody(LatValue))
				.addPart("longitude", new StringBody(LongValue))
				.addPart("source", new StringBody("MOBILE_APP"))
				/*.addPart("j_username", new StringBody(userNameValue))
				.addPart("j_password", new StringBody(passwordValue))*/
				.build();
			}else{
				FileBody bin = new FileBody(new File(FilenameValue));
				reqEntity = MultipartEntityBuilder.create()
				.addPart("ticketAttachments", bin)
				.addPart("site.siteId", new StringBody(siteIDValue))
				.addPart("serviceType.serviceTypeId", new StringBody(serviceTypeIDValue))
				.addPart("issueType.issueTypeId", new StringBody(issueTypeIDValue))
				.addPart("severity.enumerationId", new StringBody(enumerationIDValue))
				.addPart("description", new StringBody(descriptionValue))
				.addPart("latitude", new StringBody(LatValue))
				.addPart("longitude", new StringBody(LongValue))
				.addPart("source", new StringBody("MOBILE_APP"))
				/*.addPart("j_username", new StringBody(userNameValue))
				.addPart("j_password", new StringBody(passwordValue))*/
				.build();
			}
			/*HttpEntity reqEntity = MultipartEntityBuilder.create()
				.addPart("ticketAttachments", bin)
				.addPart("site.siteId", new StringBody(siteIDValue))
				.addPart("serviceType.serviceTypeId", new StringBody(serviceTypeIDValue))
				.addPart("issueType.issueTypeId", new StringBody(issueTypeIDValue))
				.addPart("severity.enumerationId", new StringBody(enumerationIDValue))
				.addPart("description", new StringBody(descriptionValue))
				.addPart("latitude", new StringBody(LatValue))
				.addPart("longitude", new StringBody(LongValue))
				.addPart("source", new StringBody("MOBILE_APP"))
				.addPart("j_username", new StringBody(userNameValue))
				.addPart("j_password", new StringBody(passwordValue))
				
				.build();*/

			httppost.setEntity(reqEntity);

			System.out.println("executing request " + httppost.getRequestLine());
			HttpResponse response = httpclient.execute(httppost);
			try {
				//System.out.println("----------------------------------------");
				//System.out.println(response.getStatusLine());
				System.out.println("========================================== Result Code: "+response.getStatusLine().getStatusCode());
				HttpEntity resEntity = response.getEntity();
				//System.out.println("Response content: " + resEntity.getContent().toString());
				
				
				
				if(response.getStatusLine().getStatusCode()==200){
					
					InputStream instream = resEntity.getContent();
					//System.out.println("Response content: " + convertStreamToString(instream));
					responce = convertStreamToString(instream);
	                // Closing the input stream will trigger connection release
	                //instream.close();*/
	                instream.close();
					
				}else{
					responce="";
				}
				
				//EntityUtils.consume(resEntity);
			}catch(Exception e){ 
				responce  = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			responce  = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + responce);
		
		
		/*RestClient client = new RestClient(INSERT_COMPLAINT);
		
		client.AddParam("siteId", siteIDValue);
		client.AddParam("serviceTypeId", serviceTypeIDValue);
		client.AddParam("description", descriptionValue);
		client.AddParam("enumerationId", enumerationIDValue);
		client.AddParam("ticketAttachments", FilenameValue);
		client.AddParam("longitude", LongValue);
		client.AddParam("latitude", LatValue);
		client.AddParam("source", sourceValue);
		client.AddParam("issueTypeId", issueTypeIDValue);
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);
		

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.POST);

		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			responce = client.getResponse();	
		}else{
			responce = "";
		}*/
		
		if (DEBUG)
			Log.d(TAG, "Response: " + responce);
		return responce;
	}	
	// Get All COmplain
	/*public static String ViewComplaints(String EmployeeCode, String tokenString ) {
		String responce = null;
		SoapObject request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_VIEW_COMPLAINTS);
		
		request.addProperty("EmployeeCode", EmployeeCode);
		request.addProperty("EmployeeToken", tokenString);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
			androidHttpTransport.call(SOAP_ACTION_VIEW_COMPLAINTS, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;
			if (result != null) {
				responce = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				responce = "";
			}
			
			System.out.println("The Responce is: "+responce.toString());
		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
		}
		return responce;
	}*/
	
	
	// Get All COmplain REST
	public static String ViewComplaintsRest(String userNameValue, String passwordValue ) {
		String responce = null;
		RestClient client = new RestClient(GET_COMPLAINT_FOR_EMPLOYEE);
		
		//client.AddParam("ticketId", ticketIDValue);
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);

		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			responce = client.getResponse();	
		}else{
			responce = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + responce);
		return responce;
	}
	
	
	// Get All Complaint by ID
	/*public static String GetComplaintByComplaintID(String ComplaintID, String tokenString ) {
		String responce = null;
		SoapObject request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_GET_COMPLAINT);
		
		request.addProperty("ComplaintID", ComplaintID);
		request.addProperty("EmployeeToken", tokenString);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
			androidHttpTransport.call(SOAP_ACTION_GET_COMPLAINT, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;
			if (result != null) {
				responce = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				responce = "";
			}
			
			System.out.println("The Responce is: "+responce.toString());
		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
		}
		return responce;
	}*/
	
	// Get All Complaint by ID REST
	public static String GetComplaintByComplaintIDRest(String ticketIDValue, String userNameValue, String passwordValue ) {
		String responce = null;
		RestClient client = new RestClient(GET_COMPLAINT_DETAILS_BY_ID);
		
		client.AddParam("ticketId", ticketIDValue);
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);

		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			responce = client.getResponse();	
		}else{
			responce = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + responce);
		return responce;
	}
	
	// Update Complaint
	/*public static String UpdateComplaint(String EmployeeCode, String ComplaintID, String SiteID, String Description, String StatusID,String tokenString ) {
		String responce = null;
		SoapObject request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_UPDATE_COMPLAINT);
		
		request.addProperty("EmployeeCode", EmployeeCode);
		request.addProperty("ComplaintID", ComplaintID);
		request.addProperty("SiteID", SiteID);
		request.addProperty("Description", Description);
		request.addProperty("StatusID", StatusID);
		request.addProperty("EmployeeToken", tokenString);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
			androidHttpTransport.call(SOAP_ACTION_UPDATE_COMPLAINT, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;
			if (result != null) {
				responce = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				responce = "";
			}
			
			System.out.println("The Responce is: "+responce.toString());
		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
		}
		return responce;
	}*/
	
	public static String UpdateComplaintRest(String ComplaintID,String Description, String StatusID,String userNameValue, String passwordValue ) {
		String responce = null;
		
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httppost = new HttpPost(UPDATE_COMPLAINT+"?j_username="+userNameValue+"&j_password="+passwordValue);
			
			//httppost.addHeader("Content-Type","multipart/form-data");
			//httppost.addHeader("Content-Disposition", "form-data; name = args");
						
			HttpEntity reqEntity;
			//StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);
				
			reqEntity = MultipartEntityBuilder.create()
			.addPart("ticketId", new StringBody(ComplaintID))
			.addPart("status.enumerationId", new StringBody(StatusID))
			.addPart("description", new StringBody(Description))
			/*.addPart("j_username", new StringBody(userNameValue))
			.addPart("j_password", new StringBody(passwordValue))*/
			.build();
			
			httppost.setEntity(reqEntity);

			System.out.println("executing request " + httppost.getRequestLine());
			HttpResponse response = httpclient.execute(httppost);
			try {
				//System.out.println("----------------------------------------");
				//System.out.println(response.getStatusLine());
				System.out.println("========================================== Result Code: "+response.getStatusLine().getStatusCode());
				HttpEntity resEntity = response.getEntity();
				//System.out.println("Response content: " + resEntity.getContent().toString());
				
				
				
				if(response.getStatusLine().getStatusCode()==200){
					
					InputStream instream = resEntity.getContent();
					//System.out.println("Response content: " + convertStreamToString(instream));
					responce = convertStreamToString(instream);
	                // Closing the input stream will trigger connection release
	                //instream.close();*/
	                instream.close();
					
				}else{
					responce="";
				}
				
				//EntityUtils.consume(resEntity);
			}catch(Exception e){ 
				responce  = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			responce  = "";
		}

		if (DEBUG)
			Log.d(TAG, "Response: " + responce);
		return responce;
	}
	
	
	
	// Get All Comments by ID REST
	public static String GetAllCommentsByComplaintIDRest(String ticketIDValue, String userNameValue, String passwordValue ) {
		String responce = null;
		RestClient client = new RestClient(GET_COMMENTS_FOR_COMPLAINT);
		
		client.AddParam("ticketId", ticketIDValue);
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);

		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			responce = client.getResponse();	
		}else{
			responce = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + responce);
		return responce;
	}
	
	// Get All Comments by ID
	/*public static String GetAllCommentsByComplaintID(String ComplaintID, String tokenString ) {
		String responce = null;
		SoapObject request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_ALL_COMMENTS);
		
		request.addProperty("ComplaintID", ComplaintID);
		request.addProperty("EmployeeToken", tokenString);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
			androidHttpTransport.call(SOAP_ACTION_ALL_COMMENTS, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;
			if (result != null) {
				responce = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				responce = "";
			}
			
			System.out.println("The Responce is: "+responce.toString());
		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
		}
		return responce;
	}*/
	
	
	
	
	/*// Get LOGOUT
	public static String getLogout(String EmployeeID, String tokenString ) {
		String responce = null;
		SoapObject request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_LOGOUT);
		
		request.addProperty("EmployeeID", EmployeeID);
		request.addProperty("Token", tokenString);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
			androidHttpTransport.call(SOAP_ACTION_LOGOUT, envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;
			if (result != null) {
				responce = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				responce = "";
			}
			
			System.out.println("The Responce is: "+responce.toString());
		} catch (Exception e) {
			e.printStackTrace();
			responce = "";
		}
		return responce;
	}*/
	private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
