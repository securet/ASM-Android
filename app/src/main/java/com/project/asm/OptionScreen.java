package com.project.asm;

import org.json.JSONObject;

import com.bugsense.trace.BugSenseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OptionScreen extends Activity implements OnClickListener, LocationListener{
	
	ImageView logoutImage;
	TextView logoutText;
	LinearLayout submitLayout,viewTicketLayout;
	private SharedPreferences myPrefs;
	private SharedPreferences.Editor prefsEditor;
	private TextView employeeName;
	LocationManager locationMgr;
	public static final Location hardFix = new Location("ATL");
	
	private ProgressDialog pd;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 BugSenseHandler.initAndStartSession(OptionScreen.this, API.bugsenseAPI);
		setContentView(R.layout.option_layout);
		
		myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
		
		{
	    	hardFix.setLatitude(Double.parseDouble(myPrefs.getString("latitude", "23.5389327")));
	        hardFix.setLongitude(Double.parseDouble(myPrefs.getString("longitude", "73.5938466")));
	        
	    }
		
		
		// resources
		logoutImage = (ImageView) findViewById(R.id.logoutImage);
		logoutText = (TextView) findViewById(R.id.logoutText);
		submitLayout = (LinearLayout) findViewById(R.id.submitLayout);
		viewTicketLayout = (LinearLayout) findViewById(R.id.viewTicketLayout);
		employeeName = (TextView) findViewById(R.id.employeeName);
		
		// listener
		logoutImage.setOnClickListener(this);
		logoutText.setOnClickListener(this);
		submitLayout.setOnClickListener(this);
		viewTicketLayout.setOnClickListener(this);
		
		employeeName.setText("Logged in as "+myPrefs.getString("EmployeeName", "Chikhalkar"));
		
		pd = new ProgressDialog(OptionScreen.this);
		pd.setMessage("Please wait...");
		pd.setCancelable(false);
		 
		findLocation();
		
	}
	
	@Override
	protected void onResume() {
		CheckEnableGPS();
		super.onResume();
	}
	
	 private void CheckEnableGPS(){
		 
		 LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		 locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L,1.0f,OptionScreen.this );
		 boolean isGPS = locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
		 
		 if(!isGPS){
			 AlertDialog.Builder builder = new AlertDialog.Builder(OptionScreen.this);
			 builder.setTitle("GPS Settings");
			 builder.setMessage("GPS is disable. Find best location, please enable it from settings.");
			 builder.setNegativeButton("Settings",new DialogInterface.OnClickListener() {
				 @Override
				 public void onClick(DialogInterface dialog, int which) {
					 //dialog.dismiss();
					 startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
					 //finish();
			     }
			});
			builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			});
			    
			AlertDialog  dialog = builder.create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);
			dialog.show();
		 }
		 
		   

		   }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		case R.id.viewTicketLayout:
			startActivity(new Intent(OptionScreen.this, TicketViewScreen.class));
			break;
		case R.id.submitLayout:
			startActivity(new Intent(OptionScreen.this, SubmitTicketScreen.class));
			break;
		case R.id.logoutImage:
			Logout();
			break;
		case R.id.logoutText:
			Logout();
			break;
		}
	}
	
	public void Logout(){
		
		
		/*LogoutTask task = new LogoutTask();
		task.execute(new String[]{myPrefs.getString("EmployeeID", "0"),myPrefs.getString("EmployeeToken", "Token")});*/
		
		prefsEditor = myPrefs.edit();
		prefsEditor.putBoolean("login", false); // value to store
		prefsEditor.commit();
		
		finish();
		startActivity(new Intent(OptionScreen.this, LoginScreen.class));
		
		
		
	}
	
	
	/*private class LogoutTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.show();
		};

		@Override
		protected String doInBackground(String... params) {
			String response = "";

			try {
				System.out.println("EmployeeID:"+params[0]);
				System.out.println("EmployeeToken: "+params[1]);
				
				//response = API.GetSites(params[0], params[1]);
				response = API.getLogout(params[0],params[1]);

			} catch (Exception e) {
				e.printStackTrace();
				response = "No Internet";
				BugSenseHandler.sendException(e);
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			System.out.println("The Message Is: " + result);
			
			if (!(result.equals("No Internet")) || !(result.equals(""))) {

				try {
					JSONObject obj = new JSONObject(result);
					
					Toast.makeText(getApplicationContext(), ""+obj.getString("Messages"), Toast.LENGTH_SHORT).show();
					
					prefsEditor = myPrefs.edit();
					prefsEditor.putBoolean("login", false); // value to store
					prefsEditor.commit();
					
					finish();
					startActivity(new Intent(OptionScreen.this, LoginScreen.class));
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
					BugSenseHandler.sendException(e);
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Error in response. Please try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}*/
	
	
	private void findLocation() {
		locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, this);

        try {

            try {
                Location gps = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location network = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                 
                // ==================================== 
                // Un-comment and use THIS TO GET CURRECT LAT-LNG - SHREYASH
                // ====================================
                if (gps != null) 
                 onLocationChanged(gps);
                else if (network != null) 
                 onLocationChanged(network);
                else 
                 onLocationChanged(OptionScreen.hardFix);
            } catch (Exception ex2) {
             ex2.printStackTrace();
                onLocationChanged(OptionScreen.hardFix);
            }  
		
	   } catch(Exception ex){}
	
	}

	@Override
	public void onLocationChanged(Location location) {
		
		String lat = String.valueOf(location.getLatitude());
		String lon = String.valueOf(location.getLongitude());
		
		prefsEditor = myPrefs.edit();
		prefsEditor.putString("latitude", lat); // value to store
		prefsEditor.commit();
		
		prefsEditor = myPrefs.edit();
		prefsEditor.putString("longitude", lon); // value to store
		prefsEditor.commit();
		
	}


	@Override
	public void onProviderDisabled(String provider) {}


	@Override
	public void onProviderEnabled(String provider) {}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	
	

}
