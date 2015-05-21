package com.project.asm;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.data.model.ServiceData;
import com.data.model.SeverityData;
import com.data.model.SubCategoryData;
import com.function.imageUpload.Constants;
import com.project.asm.widget.adapter.SiteSuggestion;

public class SubmitTicketScreen extends Activity implements OnClickListener, LocationListener{

    private static final String TAG = SubmitTicketScreen.class.getName();
    ImageView backBtn;
	Spinner  categorySpnr, subCategorySpnr, severitySpnr;
    AutoCompleteTextView sites;
	String commentMessage = "";
	ProgressDialog pd;
	private SharedPreferences myPrefs;
	private SharedPreferences.Editor prefsEditor;
	private EditText commentET;
	private Button submitBtn;
	//private Uri mImageCaptureUri;
	
	private String finalSiteID = "";
	private String finalCategoryID = "";
    private String finalCategoryName = "";
	private String finalSubCategoryID = "";
	private String finalSeverityName = "";
	//private String finalFileName = "";
	private String finalLatitude = String.valueOf(OptionScreen.hardFix.getLatitude());
	private String finalLongtude = String.valueOf(OptionScreen.hardFix.getLongitude());

    //For Site
    private SiteSuggestion siteSuggestion;

	// For Category
	private ArrayList<String> serviceName = new ArrayList<String>();
	private ArrayList<ServiceData> servicedata = new ArrayList<ServiceData>();
	private ArrayAdapter<String> serviceAdapter;
	
	// For SubCategory
	private ArrayList<String> subCategoryName = new ArrayList<String>();
	private ArrayList<SubCategoryData> subCategorydata = new ArrayList<SubCategoryData>();
	private ArrayAdapter<String> subCategoryAdapter;


    //Vendor Details
    private RelativeLayout vendorDetails;
    private TextView vendorEmail;
    private TextView vendorMobile;

	// For Severity
	private ArrayList<String> severityName = new ArrayList<String>();
	private ArrayList<SeverityData> severitydata = new ArrayList<SeverityData>();
	private ArrayAdapter<String> severityAdapter;
	
	LocationManager locationMgr;
	
	ProgressDialog	dialog ;
	String FILE_NAME = "";
	
	
	/*private AmazonS3Client s3Client = new AmazonS3Client(
			new BasicAWSCredentials(Constants.ACCESS_KEY_ID,
					Constants.SECRET_KEY));*/

	private EditText selectPhoto = null;
	private Button showInBrowser = null;
	
	private final int REQUEST_CAMERA = 0;
	private final int SELECT_FILE = 1;
	
	private TextView employeeName;
	public static Uri selectedImg;
	public static String realPath = "";
	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	private File path = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES);
	File original= null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		BugSenseHandler.initAndStartSession(SubmitTicketScreen.this, API.bugsenseAPI);
		setContentView(R.layout.submit_ticket_layout);
		
		myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
		
		// To stop Popping Keyboard
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		/*s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));
		ClientConfiguration conf = new ClientConfiguration();
		conf.setSocketTimeout(7200000);
		conf.setConnectionTimeout(7200000);
		
		s3Client.setConfiguration(conf);*/
		
		//resources
		backBtn  = (ImageView) findViewById(R.id.backBtn);
        sites = (AutoCompleteTextView) findViewById(R.id.sites);
		commentET = (EditText) findViewById(R.id.commentET);
		categorySpnr = (Spinner) findViewById(R.id.categorySpinner);
		subCategorySpnr = (Spinner) findViewById(R.id.subCategorySpnr);

        vendorDetails = (RelativeLayout)findViewById(R.id.vendorDetails);
        vendorEmail = (TextView) findViewById(R.id.vendorEmail);
        vendorMobile = (TextView) findViewById(R.id.vendorMobile);

		severitySpnr = (Spinner) findViewById(R.id.severitySpnr);
		submitBtn = (Button) findViewById(R.id.submitBtn);
		selectPhoto = (EditText)findViewById(R.id.fileUploadET);
		employeeName = (TextView)findViewById(R.id.employeeName);
       this.backBtn.getRootView();
		// listeners
		backBtn.setOnClickListener(this);
		submitBtn.setOnClickListener(this);
		selectPhoto.setOnClickListener(this);


        //assign the auto suggest adapter
        siteSuggestion = new SiteSuggestion(this, R.layout.list_item);
        siteSuggestion.setUserName(myPrefs.getString("UserName", ""));
        siteSuggestion.setPassword(myPrefs.getString("Password", ""));
        sites.setAdapter(siteSuggestion);

        //also bind site events
        sites.setOnClickListener(siteSuggestion);
        sites.setOnItemClickListener(siteSuggestion);

		// on Touch Listener
		//sitesSpnr.setOnTouchListener(sitesSpinnerOnTouch);
		categorySpnr.setOnTouchListener(categorySpinnerOnTouch);
		subCategorySpnr.setOnTouchListener(subCategorySpinnerOnTouch);
		severitySpnr.setOnTouchListener(severitySpinnerOnTouch);
		
		pd = new ProgressDialog(SubmitTicketScreen.this);
		pd.setMessage("Please wait...");
		pd.setCancelable(false);
		
		dialog = new ProgressDialog(SubmitTicketScreen.this);
		dialog.setMessage(SubmitTicketScreen.this.getString(R.string.uploading));
		dialog.setCancelable(false);
		
		employeeName.setText("Logged in as "+myPrefs.getString("EmployeeName", "Chikhalkar"));
		
		// setting adapter for category
		serviceAdapter = new ArrayAdapter<String>(SubmitTicketScreen.this, android.R.layout.simple_spinner_item,  serviceName);
		serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
		// setting adapter for sub category
		subCategoryAdapter = new ArrayAdapter<String>(SubmitTicketScreen.this, android.R.layout.simple_spinner_item,  subCategoryName);
		subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// setting adapter for Severity
		severityAdapter = new ArrayAdapter<String>(SubmitTicketScreen.this, android.R.layout.simple_spinner_item,  severityName);
		severityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//SitesTask task = new SitesTask();
   		//task.execute(new String[]{myPrefs.getString("UserName", ""),myPrefs.getString("Password", "")});
   		findLocation();

        //check for any updates from server
        AppUpdateNotifier.initAppUpdateNotifier(this);
	}
	
	
	private void selectImage() {
		final CharSequence[] items = { "From Camera", "From Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(SubmitTicketScreen.this);
		builder.setTitle("Attach Photo");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("From Camera")) {
					
					/*String fileName = "temp.jpg";
					ContentValues values = new ContentValues();
					values.put(MediaStore.Images.Media.TITLE, fileName);
					mImageCaptureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					//intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);*/

					// Working code but create exception
					//================================================================
					final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(SubmitTicketScreen.this)) );
					startActivityForResult(intent, REQUEST_CAMERA);
					//=================================================================
					
					/*Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					if (hasImageCaptureBug()) {
					    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("/sdcard/tmp")));
					} else {
					    i.putExtra(MediaStore.EXTRA_OUTPUT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					}
					startActivityForResult(i, REQUEST_CAMERA);*/
					//startCamera();
					
				} else if (items[item].equals("From Gallery")) {
					Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(Intent.createChooser(intent, "Select File"),	SELECT_FILE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}
	
	public boolean hasImageCaptureBug() {

	    // list of known devices that have the bug
	    ArrayList<String> devices = new ArrayList<String>();
	    devices.add("android-devphone1/dream_devphone/dream");
	    devices.add("generic/sdk/generic");
	    devices.add("vodafone/vfpioneer/sapphire");
	    devices.add("tmobile/kila/dream");
	    devices.add("verizon/voles/sholes");
	    devices.add("google_ion/google_ion/sapphire");

	    return devices.contains(android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
	            + android.os.Build.DEVICE);

	}
	
	private File getTempFile(Context context){   
		
		final File path = new File( Environment.getExternalStorageDirectory(), "ASM");
		
		if(!path.exists()){     
			path.mkdir();   
		}   
		return new File(path, "image.jpg");
		
	}


	private String getRealPathFromURI(Uri contentURI) {
	    String result;
	    Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
	    if (cursor == null) { // Source is Dropbox or other similar local file path
	        result = contentURI.getPath();
	    } else { 
	        cursor.moveToFirst(); 
	        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	        result = cursor.getString(idx);
	        cursor.close();
	    }
	    return result;
	}
	
	// This method is automatically called by the image picker when an image is
	// selected.
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_FILE:
			if (resultCode == RESULT_OK) {

				selectedImg = imageReturnedIntent.getData();
				realPath = getRealPathFromURI(selectedImg);
				//Toast.makeText(getApplicationContext(), "Path is: "+realPath, Toast.LENGTH_SHORT).show();
				selectPhoto.setText("Image attached");
				//new S3PutObjectTask().execute(selectedImage);
			}
			break;
		case REQUEST_CAMERA:
			if (resultCode == RESULT_OK) {
				try{
                    // TODO Auto-generated method stub
                    final File file = getTempFile(SubmitTicketScreen.this);
                    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                    final File f = getTempFile(SubmitTicketScreen.this);
                    selectedImg = Uri.fromFile(f);
                    mediaScanIntent.setData(selectedImg);
                    this.sendBroadcast(mediaScanIntent);

                    selectedImg = getImageContentUri(SubmitTicketScreen.this, file);
                    //selectedImg = Uri.fromFile(file);

                    realPath = getRealPathFromURI(selectedImg);
                    //Toast.makeText(getApplicationContext(), "Path is: "+realPath, Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "URI:: " + selectedImg);
                    selectPhoto.setText("Image attached");
				}catch(Exception e){
					e.printStackTrace();
					BugSenseHandler.sendException(e);
				}
				
			}
			break;
		}
	}
	
	public void startCamera() {

	    File photo = null;
	    //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    
	   /* if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
	        photo = new File(android.os.Environment.getExternalStorageDirectory(), FILE_NAME);
	    } else {*/
	    
	        photo = new File(getCacheDir(), "dummmy.jpg");
	   // }    
	    if (photo != null) {
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
	        selectedImg = Uri.fromFile(photo);
	        startActivityForResult(intent, REQUEST_CAMERA);
	    }
	}
	
	public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        
        
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


	protected void displayErrorAlert(String title, String message) {

		AlertDialog.Builder confirm = new AlertDialog.Builder(this);
		confirm.setTitle(title);
		confirm.setMessage(message);

		confirm.setNegativeButton(
				SubmitTicketScreen.this.getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						//S3UploaderActivity.this.finish();
					}
				});

		confirm.show().show();
	}

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
            	BugSenseHandler.sendException(ex2);
             ex2.printStackTrace();
                onLocationChanged(OptionScreen.hardFix);
            }  
		
	   } catch(Exception ex){
		   BugSenseHandler.sendException(ex);
	   }
	
	}


	private View.OnTouchListener categorySpinnerOnTouch = new View.OnTouchListener() {
	    public boolean onTouch(View v, MotionEvent event) {
	        if (event.getAction() == MotionEvent.ACTION_UP) {
	          // Toast.makeText(getApplicationContext(), "Touched", Toast.LENGTH_SHORT).show();
                resetVendorDetails();
	           	if(servicedata.size()>0){
	           	}else{
	           		CategoryTask task = new CategoryTask();
	           		task.execute(new String[]{myPrefs.getString("EmployeeToken", "Token")});	        
	           	}
	        }
	        return false;
	    }
	};
	
	private View.OnTouchListener subCategorySpinnerOnTouch = new View.OnTouchListener() {
	    public boolean onTouch(View v, MotionEvent event) {
	        if (event.getAction() == MotionEvent.ACTION_UP) {
	          // Toast.makeText(getApplicationContext(), "Touched", Toast.LENGTH_SHORT).show();
	        	if(TextUtils.isEmpty(finalCategoryID)){
           			Toast.makeText(getApplicationContext(), "Please select category", Toast.LENGTH_SHORT).show();
           		}else{
           			if(subCategorydata.size()==0){
                        executeSubCategoryTask();
    	           	}
           		}
	           	
	        }
	        return false;
	    }
	};

    private void executeSubCategoryTask() {
        SubCategoryTask task = new SubCategoryTask();
        //finalSiteID =
        if(isSiteSelectedAndAssigned()) {
            task.execute(new String[]{finalSiteID, finalCategoryID, myPrefs.getString("UserName", ""), myPrefs.getString("Password", "")});

        }
    }

    private View.OnTouchListener severitySpinnerOnTouch = new View.OnTouchListener() {
	    public boolean onTouch(View v, MotionEvent event) {
	        if (event.getAction() == MotionEvent.ACTION_UP) {
	          // Toast.makeText(getApplicationContext(), "Touched", Toast.LENGTH_SHORT).show();
	        	if(severitydata.size()>0){
    	           	}else{
    	           		SeverityTask task = new SeverityTask();
    		        	task.execute(new String[]{myPrefs.getString("UserName", ""),myPrefs.getString("Password", "")});
    	           	}
	        }
	        return false;
	    }
	};
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		case R.id.fileUploadET:
			
			selectImage();
			
			
			break;
		
		case R.id.submitBtn:
			try{
				
				FILE_NAME = Constants.PICTURE_NAME + SystemClock.currentThreadTimeMillis();
                //validate is site is selected
                boolean siteSelected = isSiteSelectedAndAssigned();
               if(siteSelected && (TextUtils.isEmpty(finalCategoryID)
						//|| TextUtils.isEmpty(finalSiteID)
						|| TextUtils.isEmpty(finalSubCategoryID)
						|| TextUtils.isEmpty(finalSeverityName)
						|| commentET.getText().toString().trim().equals(""))){
					Toast.makeText(getApplicationContext(), "Please select information", Toast.LENGTH_SHORT).show();
				}else{
                    pd.show();
                    //assign the site selected
                    finalSiteID = siteSuggestion.getSelectedSite().getString("siteId");
                    InsertComplainTask task = new InsertComplainTask();
                    task.execute(new String[]{
                            //myPrefs.getString("UserName", ""),
                            finalSiteID,
                            finalCategoryID,
                            commentET.getText().toString().trim(),
                            finalSeverityName,
                            //URLEncoder.encode(finalFileName, "UTF-8"),
                            realPath,
                            myPrefs.getString("longitude", "73.5938466"), //finalLongtude
                            myPrefs.getString("latitude", "23.5389327"), //finalLatitude,
                            finalSubCategoryID, //issue type ID
                            myPrefs.getString("UserName", ""),
                            myPrefs.getString("Password", ""),});
				}
			}catch(Exception e){
				Log.e(TAG,"Could not post the ticket",e);
			}
						
			break;
		
		case R.id.backBtn:
			ShowAlertDialog();
			break;
		}
	}

    private boolean isSiteSelectedAndAssigned(){
        boolean valid = true;
        if(siteSuggestion.getSelectedSite()==null) {
            valid=false;
            Toast.makeText(getApplicationContext(), "Please select site", Toast.LENGTH_SHORT).show();
        }else{
            //assign the site
            try {
                finalSiteID = siteSuggestion.getSelectedSite().getString("siteId");
            } catch (JSONException e) {
                Log.e(TAG,"Could not find siteId",e);
            }
        }
        return valid;
    }

	private class InsertComplainTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.show();
		};

		@Override
		protected String doInBackground(String... params) {
			String response = "";

			try {
				Log.d(TAG,"EmployeeCode:"+params[0]);
				Log.d(TAG,"SiteID:"+params[1]);
				Log.d(TAG,"SubCategoryID:"+params[2]);
				Log.d(TAG,"Description: "+params[3]);
				Log.d(TAG,"SeverityName: "+params[4]);
				Log.d(TAG,"finalFileName: "+params[5]);
				Log.d(TAG,"GPSLong: "+params[6]);
				Log.d(TAG,"GPSLat: "+params[7]);
				Log.d(TAG,"EmployeeToken: "+params[8]);
				Log.d(TAG,"EmployeeToken: "+params[9]);
				
				
				//response = API.InsertComplaintsRest(params[0],params[1],params[2],params[3],params[4],params[5],params[6],params[7],params[8]);
				response = API.InsertComplaintsRest(
						params[0], 
						params[1], 
						params[2], 
						params[3], 
						params[4], 
						params[5], 
						params[6], 
						params[7], 
						params[8], 
						params[9]);

			} catch (Exception e) {
				e.printStackTrace();
				response = "No Internet";
				BugSenseHandler.sendException(e);
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG,"The Message Is: " + result);
			
			if (!(result.equals("No Internet")) || !(result.equals(""))) {

				try {
					
					if(result.toString().contains("status") && (new JSONObject(result).getString("status").toString().equals("success"))){
						JSONObject obj = new JSONObject(result);
						JSONArray array = new JSONArray(obj.getString("messages"));
						String message = array.getJSONObject(0).getString("defaultMessage");
						
						Toast.makeText(getApplicationContext(), ""+message, Toast.LENGTH_SHORT).show();
						
						/*try{
							deleteDirectory(path);
							
						}catch(Exception e){
							e.printStackTrace();
						}*/
						if(pd.isShowing()){
                            pd.dismiss();
                        }
						startActivity(new Intent(SubmitTicketScreen.this,TicketViewScreen.class));
						finish();	
					}else{
                        pd.dismiss();
						JSONObject obj = new JSONObject(result);
						JSONArray array = new JSONArray(obj.getString("messages"));
						String message = array.getJSONObject(0).getString("defaultMessage");
						
						Toast.makeText(getApplicationContext(), ""+message, Toast.LENGTH_SHORT).show();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					BugSenseHandler.sendExceptionMessage("result",result,e);
					pd.dismiss();
				}
			} else {
				Toast.makeText(getApplicationContext(), "Error in response. Please try again.",	Toast.LENGTH_SHORT).show();
				pd.dismiss();
			}
		}
	}
	public static boolean deleteDirectory(File path) {
				
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      if (files == null) {
	          return true;
	      }
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }
	
	private class CategoryTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//pd.show();
		};

		@Override
		protected String doInBackground(String... params) {
			String response = "";

			try {
				response = API.getAllCategoryRest(myPrefs.getString("UserName", ""), myPrefs.getString("Password", ""));

			} catch (Exception e) {
				e.printStackTrace();
				response = "No Internet";
				BugSenseHandler.sendException(e);

			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			//pd.dismiss();
			Log.d(TAG,"The Message Is: " + result);
			
			if (!(result.equals("No Internet")) || !(result.equals(""))) {

				try {
					
					if(result.toString().contains("status") && (new JSONObject(result).getString("status").toString().equals("success"))){
						JSONObject obj = new JSONObject(result);
						JSONArray array = new JSONArray(obj.getString("data"));
						if(API.DEBUG) {
                            Log.d(TAG, "ARRAY:" + array.toString(2));
                        }
						serviceAdapter.clear();
						servicedata.clear();
						serviceName.clear();
						for(int i=0; i<array.length();i++){
							JSONObject o = array.getJSONObject(i);
							ServiceData s = new ServiceData(o.getString("serviceTypeId"), o.getString("name"));
							servicedata.add(s);
							serviceName.add(o.getString("name"));
						}
						
						serviceAdapter = new ArrayAdapter<String>(SubmitTicketScreen.this, android.R.layout.simple_spinner_item,  serviceName);
						categorySpnr.setAdapter(serviceAdapter);
						serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						categorySpnr.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> arg0,	View arg1, int arg2, long arg3) {
								//Toast.makeText(getApplicationContext(), "Hello : "+servicedata.get(arg2).getCategoryID(), Toast.LENGTH_SHORT).show();
								finalCategoryID = servicedata.get(arg2).getServiceID();
                                finalCategoryName = servicedata.get(arg2).getServiceName();
								subCategorydata.clear();
								subCategoryAdapter.clear();
								subCategoryName.clear();

                                executeSubCategoryTask();
								
							}
							@Override
							public void onNothingSelected(AdapterView<?> arg0) {}
						});
						
						
						SeverityTask task = new SeverityTask();
						task.execute(new String[]{myPrefs.getString("UserName", ""),myPrefs.getString("Password", "")});	
					}else{
						pd.dismiss();
						Toast.makeText(getApplicationContext(), ""+new JSONObject(result).getString("messages").toString(), Toast.LENGTH_SHORT).show();
					}
					
				} catch (Exception e) {
					if(API.DEBUG){
						e.printStackTrace();	
					}
					pd.dismiss();
					Toast.makeText(getApplicationContext(),
							"Error in response. Please try again.",
							Toast.LENGTH_SHORT).show();
					BugSenseHandler.sendException(e);
				}

			} else {
				pd.dismiss();
				Toast.makeText(getApplicationContext(),
						"Error in response. Please try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class SubCategoryTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.show();
		};

		@Override
		protected String doInBackground(String... params) {
			String response = "";

			try {
                //fetch them using - getVendorAndIssueTypes
				response = API.getVendorAndIssueTypes(params[0], params[1],params[2],params[3]);
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
			if(API.DEBUG)
				Log.d(TAG,"The Message Is: " + result);
			
			if (!(result.equals("No Internet")) || !(result.equals(""))) {

				try {
					JSONObject obj = new JSONObject(result);
                    JSONObject data = obj.getJSONObject("data");
                    if(!TextUtils.isEmpty(finalCategoryID) && !finalCategoryName.equalsIgnoreCase("ALL OK") && data.isNull("vendors")){
                       Toast vendorErrorToast = Toast.makeText(getApplicationContext(), "No Vendor Mapping Found",  Toast.LENGTH_LONG);
                       vendorErrorToast.show();
                       return;//no further processing.. when no vendor found
                    }
                    JSONArray issueTypes = data.getJSONArray("issueTypes");
                    if (API.DEBUG) {
                        Log.d(TAG, "ARRAY:" + issueTypes.toString(2));
                    }
                    if(finalCategoryName.equalsIgnoreCase("ALL OK")){
                        resetVendorDetails();
                    }else if(!data.isNull("vendors")){
                        JSONObject vendor = data.getJSONObject("vendors");
                        setVendorDetails(vendor);
                    }

					subCategoryAdapter.clear();
					subCategorydata.clear();
					subCategoryName.clear();
					for(int i=0; i<issueTypes.length();i++){
						JSONObject o = issueTypes.getJSONObject(i);
						SubCategoryData s = new SubCategoryData(o.getString("issueTypeId"), o.getString("name"));
						subCategorydata.add(s);
						subCategoryName.add(o.getString("name"));
					}

					subCategoryAdapter = new ArrayAdapter<String>(SubmitTicketScreen.this, android.R.layout.simple_spinner_item,  subCategoryName);
					subCategorySpnr.setAdapter(subCategoryAdapter);
					subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					subCategorySpnr.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> arg0,	View arg1, int arg2, long arg3) {
							//Toast.makeText(getApplicationContext(), "Hello : "+subCategorydata.get(arg2).getSubCategoryID(), Toast.LENGTH_SHORT).show();
							finalSubCategoryID = subCategorydata.get(arg2).getSubCategoryID();
						}
						@Override
						public void onNothingSelected(AdapterView<?> arg0) {}
					});

				} catch (Exception e) {
					pd.dismiss();
					e.printStackTrace();
					BugSenseHandler.sendException(e);
				}
			} else {
				pd.dismiss();
				Toast.makeText(getApplicationContext(),
						"Error in response. Please try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

    private void setVendorDetails(JSONObject vendor) throws JSONException {
        vendorEmail.setText("Vendor Email: " + vendor.getString("emailId"));
        vendorMobile.setText("Vendor Mobile: " + vendor.getString("mobile"));
        vendorDetails.setVisibility(View.VISIBLE);
        //vendorEmail.setVisibility(View.VISIBLE);
        //vendorMobile.setVisibility(View.VISIBLE);
    }

    private void resetVendorDetails(){
        vendorDetails.setVisibility(View.GONE);
        vendorEmail.setText("");
        vendorMobile.setText("");
        //vendorEmail.setVisibility(View.GONE);
        //vendorMobile.setVisibility(View.GONE);
    }

    private class SeverityTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//pd.show();
		};

		@Override
		protected String doInBackground(String... params) {
			String response = "";

			try {
				response = API.GetAllSeverityRest(params[0],params[1]);

			} catch (Exception e) {
				if(API.DEBUG)
					e.printStackTrace();
				response = "No Internet";
				BugSenseHandler.sendException(e);
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			if(API.DEBUG)
				Log.d(TAG,"The Message Is: " + result);
			
			if (!(result.equals("No Internet")) || !(result.equals(""))) {

				try {
					
					if(result.toString().contains("status") && (new JSONObject(result).getString("status").toString().equals("success"))){
						JSONObject obj = new JSONObject(result);
						JSONArray array = new JSONArray(obj.getString("data"));
						if(API.DEBUG) {
                            Log.d(TAG, "ARRAY:" + array.toString(2));
                        }
						severityAdapter.clear();
						severitydata.clear();
						severityName.clear();
						for(int i=0; i<array.length();i++){
							JSONObject o = array.getJSONObject(i);
							SeverityData s = new SeverityData(o.getString("enumerationId"));
							severitydata.add(s);
							severityName.add(o.getString("enumerationId"));
						}
						
						severityAdapter = new ArrayAdapter<String>(SubmitTicketScreen.this, android.R.layout.simple_spinner_item,  severityName);
						severitySpnr.setAdapter(severityAdapter);
						severityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						severitySpnr.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> arg0,	View arg1, int arg2, long arg3) {
								//Toast.makeText(getApplicationContext(), "Hello : "+severitydata.get(arg2).getSeverityName(), Toast.LENGTH_SHORT).show();
								finalSeverityName = severitydata.get(arg2).getSeverityName();
							}
							@Override
							public void onNothingSelected(AdapterView<?> arg0) {}
						});
					}else{
						pd.dismiss();
						Toast.makeText(getApplicationContext(), ""+new JSONObject(result).getString("messages").toString(), Toast.LENGTH_SHORT).show();
					}
					

				} catch (Exception e) {
					if(API.DEBUG) {
                        Log.e(TAG,"Error on severity selection",e);
                    }
					pd.dismiss();
					Toast.makeText(getApplicationContext(),
							"Error in response. Please try again.",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				pd.dismiss();
				Toast.makeText(getApplicationContext(),
						"Error in response. Please try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	

	public void ShowAlertDialog(){
		 AlertDialog.Builder builder = new AlertDialog.Builder(SubmitTicketScreen.this);
		 builder.setTitle("Unsaved Changes");
		 builder.setMessage("The data entered will be discarded");
		 builder.setNegativeButton("Ok",new DialogInterface.OnClickListener() {
			 @Override
			 public void onClick(DialogInterface dialog, int which) {
				 dialog.dismiss();
				 finish();
		     }
		});
		builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		    
		AlertDialog  dialog = builder.create();
		dialog.show();
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
