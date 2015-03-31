package com.project.asm;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	//private static String SOAP_NAMESPACE = "http://tempuri.org/";
	private static String SOAP_NAMESPACE = "http://securet.in/";
	//private static String SOAP_URL = "http://14.200.162.57/ipadservice/ipadservice.asmx";
	private static String SOAP_URL = "http://54.186.184.239/ASM_Web/WS/GetASMData.asmx";
	// insertUserInfo
	/*private static String SOAP_METHOD_insertUserDetail = "GetAllCategoryinJson";
	private static String SOAP_ACTION_insertUserInfo = SOAP_NAMESPACE + SOAP_METHOD_insertUserDetail;*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//insertUserDetails();
			}
		}).start();
		
		
	}
	
	/*private String insertUserDetails(){
		String res = null;
		
		SoapObject request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_insertUserDetail);
		// Use this to add parameters
		
		// Declare the version of the SOAP request
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		try {
			System.out.println("1");
			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
			System.out.println("2");
			// this is the actual part that will call the webservice
			androidHttpTransport.call(SOAP_ACTION_insertUserInfo, envelope);
			System.out.println("3");
			// Get the SoapResult from the envelope body.
			SoapObject result = (SoapObject) envelope.bodyIn;
			System.out.println("4");
			if (result != null) {
				// Get the first property and change the label text
				//txtCel.setText(result.getProperty(0).toString());
				res = result.getProperty(0).toString();
			} else {
				//Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
				res = "";
			}
			System.out.println("5");
			System.out.println("The Responce is: "+res.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}*/
	

}
