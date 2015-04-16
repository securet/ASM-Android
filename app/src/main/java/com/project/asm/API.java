package com.project.asm;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

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

    // For debugs all logs...
	public static int LOGLEVEL = 1; // set -1 to stop debugging in the app
	public static boolean WARN = LOGLEVEL > 1; // set LOGLEVEL = 2
	public static boolean DEBUG = LOGLEVEL > 0; // set LOGLEVEL = 1
	
	public static String bugsenseAPI = "2dd5922c";
	
	
//	public static String HOST = "http://192.168.0.61:8080/asm/";
//    public static String HOST = "http://test.asm.securet.in/";
    public static String HOST = "http://asm.securet.in/";
    private static String REST = "rest/v1/";

	private static String VALIDATE_USER = HOST+REST+"validateUser"; // done
	private static String SERVICE_TYPES = HOST+REST+"serviceTypes"; // done
	private static String GET_ISSUE_TYPES_FOR_SERVICE = HOST+REST+"getIssueTypesForService"; // done
    private static final String GET_VENDORS_AND_ISSUE_TYPES_FOR_SERVICE = HOST+REST+"getVendorAndIssueTypes"; // done;
	private static String GET_USER_SITES = HOST+REST+"getSitesForUser"; // done
	private static String GET_ALL_SEVERITY = HOST+REST+"severityTypes"; // done
	
	private static String INSERT_COMPLAINT = HOST+REST+"ticket/create"; // done
	
	private static String GET_COMPLAINT_DETAILS_BY_ID = HOST+REST+"ticket/forId"; // done
	
	private static String GET_COMPLAINT_FOR_EMPLOYEE = HOST+REST+"ticket/forUser"; // done
	
	private static String UPDATE_COMPLAINT = HOST+REST+"ticket/update"; // 
	
	private static String GET_COMMENTS_FOR_COMPLAINT = HOST+REST+"ticket/history";

    private static final String SEARCH_SITES_BY_KEYWORD = HOST+REST+"searchUserSites";;
    private static final String CHECK_APP_NOTIFICATIONS = HOST+REST+"appNotifications";;

	
	
	// --------------------------------------------------------------------------------
	// Restful API invocation METHODS START FROM HERE
	// --------------------------------------------------------------------------------
	
	//===========================================================================
	//                     All  REST services
	//===========================================================================
	//===========================================================================
	

    // Make a rest, add any parameters REST
    public static String makeRequest(String URI, Map<String,Object> params) {
        String response = null;
        RestClient client = new RestClient(URI);

        for(Map.Entry<String,Object> param: params.entrySet()){
            if(param.getValue() instanceof  String){
               client.AddParam(param.getKey(), String.valueOf(param.getValue()));
            }else if(param.getValue() instanceof List){
                for(Object paramValue : (List)param.getValue()){
                    client.AddParam(param.getKey(), String.valueOf(paramValue));
                }
            }
        }
        try {
            client.Execute(RequestMethod.GET);
        } catch (Exception e) {
            Log.e(TAG,"Could not load request:"+URI,e);
            response = "";
        }


        if(client.getResponseCode()== 200){
            response = client.getResponse();
        }else{
            response = "";
        }

        if (DEBUG) {
            Log.d(TAG, "Response: " + response);
        }
        return response;
    }

    // Validate User REST
	public static String LoginRest(String userNameValue, String passwordValue) {
		String response = null;
		RestClient client = new RestClient(VALIDATE_USER);
		
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);
			

		} catch (Exception e) {
			e.printStackTrace();
			response = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			response = client.getResponse();	
		}else{
			response = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + response);
		return response;
	}
	
	
		
	// GET Sites
	/*public static String GetSitesSOAP(String employeeId, String tokenString) {
		String response = null;
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
				response = result.getProperty(0).toString();
				
				
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				response = "";
			}
			
			System.out.println("The response is: "+response.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response = "";
		}
		return response;
	}*/
		
	// get User sites REST
	public static String GetSitesRest( String userNameValue, String passwordValue) {
		String response = null;
		
		RestClient client = new RestClient(GET_USER_SITES);
		
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);

		} catch (Exception e) {
			e.printStackTrace();
			response = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			response = client.getResponse();	
		}else{
			response = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + response);
		return response;
	}
	
	/*// Get All Category
	public static String GetAllCategorySOAP(String tokenString) {
		String response = null;
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
				response = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				response = "";
			}
			
			System.out.println("The response is: "+response.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response = "";
		}
		return response;
	}*/
	
	
	// Validate GetAllCategory REST
	public static String getAllCategoryRest(String userNameValue, String passwordValue) {
		String response = null;
		RestClient client = new RestClient(SERVICE_TYPES);
		
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);

		} catch (Exception e) {
			e.printStackTrace();
			response = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			response = client.getResponse();	
		}else{
			response = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + response);
		return response;
	}
	
	// Get Sub Category
	/*public static String GetSubCategorySOAP(String categoryID, String tokenString ) {
		String response = null;
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
				response = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				response = "";
			}
			
			System.out.println("The response is: "+response.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response = "";
		}
		return response;
	}*/

    // Validate GetSUBCategory REST
    public static String getVendorAndIssueTypes(String siteId,String serviceIDValue, String userNameValue, String passwordValue) {
        String response = null;
        RestClient client = new RestClient(GET_VENDORS_AND_ISSUE_TYPES_FOR_SERVICE);

        client.AddParam("siteId", siteId);
        client.AddParam("serviceTypeId", serviceIDValue);
        client.AddParam("j_username", userNameValue);
        client.AddParam("j_password", passwordValue);

        // client.AddParam("output", "json");
        try {
            client.Execute(RequestMethod.GET);

        } catch (Exception e) {
            e.printStackTrace();
            response = "";
            if (DEBUG)
                Log.d(TAG, "");
        }


        if(client.getResponseCode()== 200){
            response = client.getResponse();
        }else{
            response = "";
        }

        if (DEBUG)
            Log.d(TAG, "Response: " + response);
        return response;
    }

	// Validate GetSUBCategory REST
	public static String getSubCategoryRest(String serviceIDValue, String userNameValue, String passwordValue) {
		String response = null;
		RestClient client = new RestClient(GET_ISSUE_TYPES_FOR_SERVICE);
		
		client.AddParam("serviceTypeId", serviceIDValue);
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);

		} catch (Exception e) {
			e.printStackTrace();
			response = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			response = client.getResponse();	
		}else{
			response = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + response);
		return response;
	}
	
	
	// Get All Severity
	/*public static String GetAllSeverity(String tokenString ) {
		String response = null;
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
				response = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				response = "";
			}
			
			System.out.println("The response is: "+response.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response = "";
		}
		return response;
	}*/
	
	
	// Get All Severity
	public static String GetAllSeverityRest(String userNameValue,String passwordValue ) {
		String response = null;
		RestClient client = new RestClient(GET_ALL_SEVERITY);
		
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);
	
		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);
	
		} catch (Exception e) {
			e.printStackTrace();
			response = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			response = client.getResponse();	
		}else{
			response = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + response);
		return response;
	}
	
	// Insert COmplain
	/*public static String InsertComplaints(String EmployeeCode, String SiteID, String SubCategoryID, String Description, String SeverityName, String Filename, String GPSLong, String GPSLat, String tokenString ) {
		String response = null;
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
				response = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				response = "";
			}
			
			System.out.println("The response is: "+response.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response = "";
		}
		return response;
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
		String responseStr = "";
		
		
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
			HttpResponse httpResponse = httpclient.execute(httppost);
			try {
				//System.out.println("----------------------------------------");
				//System.out.println(response.getStatusLine());
				System.out.println("========================================== Result Code: " + httpResponse.getStatusLine().getStatusCode());
				HttpEntity resEntity = httpResponse.getEntity();
				//System.out.println("Response content: " + resEntity.getContent().toString());
				
				
				
				if(httpResponse.getStatusLine().getStatusCode()==200){
					
					InputStream instream = resEntity.getContent();
					//System.out.println("Response content: " + convertStreamToString(instream));
					responseStr = convertStreamToString(instream);
	                // Closing the input stream will trigger connection release
	                //instream.close();*/
	                instream.close();
					
				}else{
                    responseStr="";
				}
				
				//EntityUtils.consume(resEntity);
			}catch(Exception e){
                responseStr  = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseStr  = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + responseStr);
		
		
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
			response = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			response = client.getResponse();	
		}else{
			response = "";
		}*/
		
		if (DEBUG)
			Log.d(TAG, "Response: " + responseStr);
		return responseStr;
	}	
	// Get All COmplain
	/*public static String ViewComplaints(String EmployeeCode, String tokenString ) {
		String response = null;
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
				response = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				response = "";
			}
			
			System.out.println("The response is: "+response.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response = "";
		}
		return response;
	}*/

    // Get All tickets for user with filters and offset
    public static String fetchTickets(Map<String,Object> params) {
        return makeRequest(GET_COMPLAINT_FOR_EMPLOYEE,params);
    }

	// Get All COmplain REST
	public static String ViewComplaintsRest(String userNameValue, String passwordValue ) {
		String response = null;
		RestClient client = new RestClient(GET_COMPLAINT_FOR_EMPLOYEE);
		
		//client.AddParam("ticketId", ticketIDValue);
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);

		} catch (Exception e) {
			e.printStackTrace();
			response = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			response = client.getResponse();	
		}else{
			response = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + response);
		return response;
	}
	
	
	// Get All Complaint by ID
	/*public static String GetComplaintByComplaintID(String ComplaintID, String tokenString ) {
		String response = null;
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
				response = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				response = "";
			}
			
			System.out.println("The response is: "+response.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response = "";
		}
		return response;
	}*/
	
	// Get All Complaint by ID REST
	public static String GetComplaintByComplaintIDRest(String ticketIDValue, String userNameValue, String passwordValue ) {
		String response = null;
		RestClient client = new RestClient(GET_COMPLAINT_DETAILS_BY_ID);
		
		client.AddParam("ticketId", ticketIDValue);
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);

		} catch (Exception e) {
			e.printStackTrace();
			response = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			response = client.getResponse();	
		}else{
			response = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + response);
		return response;
	}
	
	// Update Complaint
	/*public static String UpdateComplaint(String EmployeeCode, String ComplaintID, String SiteID, String Description, String StatusID,String tokenString ) {
		String response = null;
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
				response = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				response = "";
			}
			
			System.out.println("The response is: "+response.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response = "";
		}
		return response;
	}*/
	
	public static String UpdateComplaintRest(String ComplaintID,String Description, String StatusID,String userNameValue, String passwordValue ) {
		String responseStr = null;
		
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
			HttpResponse httpResponse = httpclient.execute(httppost);
			try {
				//System.out.println("----------------------------------------");
				//System.out.println(response.getStatusLine());
				System.out.println("========================================== Result Code: "+httpResponse.getStatusLine().getStatusCode());
				HttpEntity resEntity = httpResponse.getEntity();
				//System.out.println("Response content: " + resEntity.getContent().toString());
				
				
				
				if(httpResponse.getStatusLine().getStatusCode()==200){
					
					InputStream instream = resEntity.getContent();
					//System.out.println("Response content: " + convertStreamToString(instream));
                    responseStr = convertStreamToString(instream);
	                // Closing the input stream will trigger connection release
	                //instream.close();*/
	                instream.close();
					
				}else{
                    responseStr="";
				}
				
				//EntityUtils.consume(resEntity);
			}catch(Exception e){
                responseStr  = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseStr  = "";
		}

		if (DEBUG)
			Log.d(TAG, "Response: " + responseStr);
		return responseStr;
	}
	
	
	
	// Get All Comments by ID REST
	public static String GetAllCommentsByComplaintIDRest(String ticketIDValue, String userNameValue, String passwordValue ) {
		String response = null;
		RestClient client = new RestClient(GET_COMMENTS_FOR_COMPLAINT);
		
		client.AddParam("ticketId", ticketIDValue);
		client.AddParam("j_username", userNameValue);
		client.AddParam("j_password", passwordValue);

		// client.AddParam("output", "json");
		try {
			client.Execute(RequestMethod.GET);

		} catch (Exception e) {
			e.printStackTrace();
			response = "";
			if (DEBUG)
				Log.d(TAG, "");
		}
		
		
		if(client.getResponseCode()== 200){
			response = client.getResponse();	
		}else{
			response = "";
		}
		
		if (DEBUG)
			Log.d(TAG, "Response: " + response);
		return response;
	}

    // Get sites search by key word..
    public static String searchSites(String searchString,String resultSize,String userNameValue, String passwordValue ) {
        String response = null;
        RestClient client = new RestClient(SEARCH_SITES_BY_KEYWORD);

        client.AddParam("searchString", searchString);
        client.AddParam("resultSize", resultSize);
        client.AddParam("j_username", userNameValue);
        client.AddParam("j_password", passwordValue);

        // client.AddParam("output", "json");
        try {
            client.Execute(RequestMethod.GET);
        } catch (Exception e) {
            response = "";
            if (DEBUG) {
                //Log.e(TAG, "Error searching sites",e);
            }
        }


        if(client.getResponseCode()== 200){
            response = client.getResponse();
        }else{
            response = "";
        }

        if (DEBUG) {
            Log.d(TAG, "Response: " + response);
        }
        return response;
    }

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

    public static String checkAppNotifications(Map<String,Object> params) {
       return makeRequest(CHECK_APP_NOTIFICATIONS,params);
    }
}
