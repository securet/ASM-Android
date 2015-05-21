package com.project.asm;

import java.io.File;
import java.net.URL;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.function.imageUpload.Constants;

public class UpdateTicketScreen extends Activity implements OnClickListener, LocationListener{

    private static final String TAG = UpdateTicketScreen.class.getName();
    ImageView backBtn;
	TextView sitesSpnr, categorySpnr, subCategorySpnr, severitySpnr, vendorEmail,vendorMobile;
	String commentMessage = "";
	ProgressDialog pd;
	private SharedPreferences myPrefs;
	private SharedPreferences.Editor prefsEditor;
	private EditText commentET;
	private Button submitBtn;
	private Button closeStatusBtn;

	private String finalSiteID = "";
	private String finalCategoryID = "";
	private String finalSubCategoryID = "";
	private String finalSeverityName = "";
	private String finalFileName = "";
	
	LocationManager locationMgr;
	private TextView ticketTitle, complaintStatusTV, dateTV;
	private ImageView statusIcon;
    private RelativeLayout vendorDetails=null;
	
	private String ComplaintID;
	private String ComplaintCode;
	private String ComplaintDescription;
	private String ComplaintSeverity;
	private String ComplaintSiteID;
	private String ComplaintSiteName;
	private String ComplaintCategoryID;
	private String ComplaintCategoryName;
    private String ComplaintVendorEmail;
    private String ComplaintVendorMobile;
	private String ComplaintSubCategoryID;
	private String ComplaintSubCategoryName;
	private String ComplaintStatusID;
	private String ComplaintStatusName;
	private String Filename;
	
	private TextView employeeName;
	private EditText filenameET;
	private TextView previousComment;
	private TextView poRequests;

	private final int REQUEST_CAMERA = 0;
	private final int SELECT_FILE = 1;
	
	/*private AmazonS3Client s3Client = new AmazonS3Client(
			new BasicAWSCredentials(Constants.ACCESS_KEY_ID,
					Constants.SECRET_KEY));*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 BugSenseHandler.initAndStartSession(UpdateTicketScreen.this, API.bugsenseAPI);
		setContentView(R.layout.update_ticket_layout);
		
		myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
		
		// To stop Popping Keyboard
		UpdateTicketScreen.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		//resources
		backBtn  = (ImageView) findViewById(R.id.backBtn);
		sitesSpnr = (TextView) findViewById(R.id.sitesSpnr);

        vendorDetails = (RelativeLayout) findViewById(R.id.vendorDetails);
		vendorEmail = (TextView) findViewById(R.id.vendorEmail);
        vendorMobile = (TextView) findViewById(R.id.vendorMobile);

        commentET = (EditText) findViewById(R.id.commentET);
		categorySpnr = (TextView) findViewById(R.id.categorySpinner);
		subCategorySpnr = (TextView) findViewById(R.id.subCategorySpnr);
		severitySpnr = (TextView) findViewById(R.id.severitySpnr);
		submitBtn = (Button) findViewById(R.id.submitBtn);
		ticketTitle = (TextView) findViewById(R.id.ticketTitle);
		closeStatusBtn = (Button) findViewById(R.id.closeStatusBtn);
		
		complaintStatusTV = (TextView) findViewById(R.id.complainStatusTV);
		statusIcon = (ImageView) findViewById(R.id.statusImg);
		dateTV = (TextView) findViewById(R.id.dateTV);
		employeeName = (TextView)findViewById(R.id.employeeName);
		filenameET = (EditText)findViewById(R.id.filename);
		previousComment = (TextView) findViewById(R.id.previousComment);
		poRequests = (TextView) findViewById(R.id.poRequests);

				
		// lictener
		backBtn.setOnClickListener(this);
		submitBtn.setOnClickListener(this);
		closeStatusBtn.setOnClickListener(this);
		filenameET.setOnClickListener(this);
		previousComment.setOnClickListener(this);
		poRequests.setOnClickListener(this);

		// on Touch Listenre
		/*sitesSpnr.setOnTouchListener(sitesSpinnerOnTouch);
		categorySpnr.setOnTouchListener(categorySpinnerOnTouch);
		subCategorySpnr.setOnTouchListener(subCategorySpinnerOnTouch);
		severitySpnr.setOnTouchListener(severitySpinnerOnTouch);*/
		
		complaintStatusTV.setText(""+getIntent().getExtras().getString("complain_status"));
		
		
		if(getIntent().getExtras().getString("complain_status").toLowerCase().equals("open")){
			statusIcon.setImageResource(R.drawable.open_icon);
            closeStatusBtn.setText("Close");
			//closeStatusBtn.setVisibility(View.GONE);
    	}else if(getIntent().getExtras().getString("complain_status").toLowerCase().equals("work_in_progress")){
    		statusIcon.setImageResource(R.drawable.inprogress_icon);
    		closeStatusBtn.setVisibility(View.GONE);
    	}else if(getIntent().getExtras().getString("complain_status").toLowerCase().equals("resolved")){
    		statusIcon.setImageResource(R.drawable.resolved_icon);
    		closeStatusBtn.setVisibility(View.VISIBLE);
    		submitBtn.setText("Reject and Open");
    	}else if(getIntent().getExtras().getString("complain_status").toLowerCase().equals("closed")){
    		//Toast.makeText(getApplicationContext(), ""+complaintStatusTV.getText().toString(), Toast.LENGTH_SHORT).show();
    		statusIcon.setImageResource(R.drawable.closed_icon);
    		closeStatusBtn.setVisibility(View.GONE);
    		submitBtn.setVisibility(View.GONE);
    	}
		
		ticketTitle.setText(""+getIntent().getExtras().getString("complain_code"));
		employeeName.setText("Logged in as "+myPrefs.getString("EmployeeName", "Chikhalkar"));
		
		pd = new ProgressDialog(UpdateTicketScreen.this);
		pd.setMessage("Please wait...");
		pd.setCancelable(false);
		
		sitesSpnr.requestFocus();		
		
		GetComplainByComplaintID task = new GetComplainByComplaintID();
   		task.execute(new String[]{getIntent().getExtras().getString("complain_id")});
		   		
   		//findLocation();
   		
	}
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		case R.id.previousComment:
			Intent in = new Intent(this, CommentViewScreen.class);
			in.putExtra("comment_id",getIntent().getExtras().getString("complain_id")); 
			startActivity(in);
			break;

		case R.id.poRequests:
			Intent poIntent = new Intent(this, PORequestViewScreen.class);
			poIntent.putExtra("ticketId",getIntent().getExtras().getString("complain_id"));
			startActivity(poIntent);
			break;

		case R.id.filename:
			if(Filename.toString().trim().equals("") || Filename.toString().trim().equals(null)){
				//selectImage();
			}else{
				String uri= Filename.toString().trim(); 
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri)); 
				startActivity(myIntent);
			}
			break;
		
		case R.id.closeStatusBtn:
			
			if(commentET.getText().toString().trim().equals("")
					|| commentET.getText().toString().trim().equals(null)){
				
				Toast.makeText(getApplicationContext(), "Please enter information.", Toast.LENGTH_SHORT).show();
				
			}else{
				String ticketStatus = getIntent().getExtras().getString("complain_status");
				if(ticketStatus.equalsIgnoreCase("resolved") || ticketStatus.equalsIgnoreCase("open")){
					ComplaintStatusID = "CLOSED";
				}
				UpdateComplainTask task = new UpdateComplainTask();
				task.execute(new String[]{
						ComplaintID,
						commentET.getText().toString().trim(),
						ComplaintStatusID,
						myPrefs.getString("UserName", ""),
						myPrefs.getString("Password", "")});
				
			}
			
			
			
			break;
		
		case R.id.submitBtn:
			/*if(ComplaintID.equals("")||ComplaintID.equals(null)
					|| ComplaintCode.equals("") || ComplaintCode.equals(null)
					|| finalSubCategoryID.equals("") || finalSubCategoryID.equals(null)
					|| finalSeverityName.equals("") || finalSeverityName.equals(null)
					|| commentET.getText().toString().trim().equals("")
					|| commentET.getText().toString().trim().equals(null)){
				Toast.makeText(getApplicationContext(), "Please select information", Toast.LENGTH_SHORT).show();
			}else{*/
				//Toast.makeText(getApplicationContext(), "Please select information", Toast.LENGTH_SHORT).show();
			
			if(commentET.getText().toString().trim().equals("")
					|| commentET.getText().toString().trim().equals(null)){
				
				Toast.makeText(getApplicationContext(), "Please enter information.", Toast.LENGTH_SHORT).show();
				
			}else{
				
				if(getIntent().getExtras().getString("complain_status").toLowerCase().equals("resolved")){
					ComplaintStatusID = "WORK_IN_PROGRESS"; // Rohit tell me to do like this
					//ComplaintStatusID = "216"; // updated as Kalyan said on excel sheet
				}
					
					UpdateComplainTask task2 = new UpdateComplainTask();
					task2.execute(new String[]{
							ComplaintID,
							commentET.getText().toString().trim(),
							ComplaintStatusID,
							myPrefs.getString("UserName", ""),
							myPrefs.getString("Password", "")});
			}
			
			//}
			
			break;
		
		case R.id.backBtn:
			//ShowAlertDialog();
			finish();
			break;
		}
	}
	
	private class UpdateComplainTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.show();
		};

		@Override
		protected String doInBackground(String... params) {
			String response = "";

			try {
				System.out.println("EmployeeCode:"+params[0]);
				System.out.println("ComplaintID:"+params[1]);
				System.out.println("ComplaintSiteID:"+params[2]);
				System.out.println("Description: "+params[3]);
				System.out.println("ComplaintStatusID: "+params[4]);
				//System.out.println("token: "+params[5]);
				
				
				//response = API.GetSites(params[0], params[1]);
				response = API.UpdateComplaintRest(params[0], 
						params[1], params[2], params[3], params[4]);

			} catch (Exception e) {
				e.printStackTrace();
				response = "No Internet";
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			System.out.println("The Message Is: " + result);
			
			if (!(result.equals("No Internet")) || !(result.equals(""))) {
				try {
					if(result.toString().contains("status") && (new JSONObject(result).getString("status").toString().equals("success"))){
						JSONObject obj = new JSONObject(result);
						JSONArray array = new JSONArray(obj.getString("messages"));
						String message = array.getJSONObject(0).getString("defaultMessage");
						
						Toast.makeText(getApplicationContext(), ""+message, Toast.LENGTH_SHORT).show();
						finish();	
					}else{
						JSONObject obj = new JSONObject(result);
						JSONArray array = new JSONArray(obj.getString("messages"));
						String message = array.getJSONObject(0).getString("defaultMessage");
						
						Toast.makeText(getApplicationContext(), ""+message, Toast.LENGTH_SHORT).show();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							"Error in response. Please try again.",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Error in response. Please try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
	private class GetComplainByComplaintID extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.show();
		};

		@Override
		protected String doInBackground(String... params) {
			String response = "";

			try {
				response = API.GetComplaintByComplaintIDRest(params[0],myPrefs.getString("UserName",""), myPrefs.getString("Password", ""));

			} catch (Exception e) {
				e.printStackTrace();
				response = "No Internet";

			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			System.out.println("The Message Is: " + result);
			
			if (!(result.equals("No Internet")) || !(result.equals(""))) {
				try {
					
					if(result.toString().contains("status") && (new JSONObject(result).getString("status").toString().equals("success"))){
						JSONObject temp = new JSONObject(result);
						JSONObject obj = temp.getJSONObject("data");
						
						ComplaintID = obj.getString("ticketId");
						ComplaintCode= obj.getString("ticketId");
						ComplaintDescription = obj.getString("description");
						
						if(obj.isNull(("severity"))){
							ComplaintSeverity = "NOT AVAILABLE";
						}else{
							ComplaintSeverity = (obj.getJSONObject("severity")).getString("enumerationId");
						}


                        JSONObject site = obj.getJSONObject("site");
                        ComplaintSiteID = site.getString("siteId");
						ComplaintSiteName = site.getString("name")+"\n"+(site.isNull("area")?"":site.getString("area"));
						
						//ComplaintCategoryName = obj.getJSONObject("serviceType").getString("name");
						if(obj.isNull(("serviceType"))){
							ComplaintCategoryID = "NOT AVAILABLE";
							ComplaintCategoryName = "NOT AVAILABLE";
							
						}else{
							ComplaintCategoryID = obj.getJSONObject("serviceType").getString("serviceTypeId");
							ComplaintCategoryName = obj.getJSONObject("serviceType").getString("name");
						}

                        if(!obj.isNull("resolver")){
                            vendorDetails.setVisibility(View.VISIBLE);
                            JSONObject resolver = obj.getJSONObject("resolver");
                            ComplaintVendorEmail = resolver.getString("emailId");
                            ComplaintVendorMobile = resolver.getString("mobile");
                        }else{
                            vendorDetails.setVisibility(View.GONE);
                        }
						
						if(obj.isNull("issueType")){
							ComplaintSubCategoryID = "NOT AVAILABLE";
						}else{
							ComplaintSubCategoryID = obj.getJSONObject("issueType").getString("issueTypeId");	
						}
						
						ComplaintSubCategoryName = obj.getJSONObject("issueType").getString("name");
						ComplaintStatusID = obj.getJSONObject("status").getString("enumerationId");
						ComplaintStatusName = obj.getJSONObject("status").getString("enumDescription");
						
						try{
							JSONArray array = new JSONArray(obj.getString("attachments"));
							
							Filename = API.HOST + array.getJSONObject(0).getString("attachmentPath");
						}catch(Exception e){
                            Log.d(TAG,"No Image path found",e);
                            Filename ="No Image Available";
						}
						
						String date = obj.getString("createdTimestamp");
						
						dateTV.setText(date);
						sitesSpnr.setText(ComplaintSiteName);
						categorySpnr.setText(ComplaintCategoryName);
                        vendorEmail.setText(ComplaintVendorEmail);
                        vendorMobile.setText(ComplaintVendorMobile);
						subCategorySpnr.setText(ComplaintSubCategoryName);
						severitySpnr.setText(ComplaintSeverity);
						
						if(Filename.equals("") || Filename.equals(null) || Filename.equals("No Image Available")){
							filenameET.setText("No Image Available");
						}else{
							filenameET.setText("View Image");
						}
					}else{
						Toast.makeText(getApplicationContext(), ""+new JSONObject(result).getString("messages").toString(), Toast.LENGTH_SHORT).show();
					}
					
					
										
					
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							"Error in response. Please try again.",
							Toast.LENGTH_SHORT).show();
				}
				
				
			} else {
				Toast.makeText(getApplicationContext(),
						"Error in response. Please try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
	
	
	
	
	
	
	/*private class InsertComplainTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.show();
		};

		@Override
		protected String doInBackground(String... params) {
			String response = "";

			try {
				//response = API.InsertComplaints(EmployeeCode, SiteID, SubCategoryID, Description, SeverityName, Filename, GPSLong, GPSLat, tokenString)

			} catch (Exception e) {
				e.printStackTrace();
				response = "No Internet";

			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			System.out.println("The Message Is: " + result);
			
			if (!(result.equals("No Internet")) || !(result.equals(""))) {

				try {
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Error in response. Please try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}*/
	
	public void ShowAlertDialog(){
		 AlertDialog.Builder builder = new AlertDialog.Builder(UpdateTicketScreen.this);
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
	
	private void selectImage() {
		final CharSequence[] items = { "From Camera", "From Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(UpdateTicketScreen.this);
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
					final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(UpdateTicketScreen.this)) );  
					
					startActivityForResult(intent, REQUEST_CAMERA);
					
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
	private File getTempFile(Context context){   
		//it will return /sdcard/image.tmp   
		//final File path = new File( Environment.getExternalStorageDirectory(), context.getPackageName() );
		final File path = new File( Environment.getExternalStorageDirectory(), context.getPackageName());
		
		if(!path.exists()){     
			path.mkdir();   
		}   
		return new File(path, "image.tmp"); 
	} 
	
	// This method is automatically called by the image picker when an image is
	// selected.
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_FILE:
			if (resultCode == RESULT_OK) {

				Uri selectedImage = imageReturnedIntent.getData();
				//new S3PutObjectTask().execute(selectedImage);
			}
			break;
		case REQUEST_CAMERA:
			if (resultCode == RESULT_OK) {
				
				final File file = getTempFile(this);   
				 Uri uri = getImageContentUri(getApplicationContext(), file);
				System.out.println("URI:: "+uri);
				
				//new S3PutObjectTask().execute(uri);
			}
			break;
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
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
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
	
	/*private class S3PutObjectTask extends AsyncTask<Uri, Void, S3TaskResult> {

		ProgressDialog dialog;

		protected void onPreExecute() {
			dialog = new ProgressDialog(UpdateTicketScreen.this);
			dialog.setMessage(UpdateTicketScreen.this.getString(R.string.uploading));
			dialog.setCancelable(false);
			dialog.show();
		}

		protected S3TaskResult doInBackground(Uri... uris) {

			if (uris == null || uris.length != 1) {
				return null;
			}

			// The file location of the image selected.
			Uri selectedImage = uris[0];


            ContentResolver resolver = getContentResolver();
            String fileSizeColumn[] = {OpenableColumns.SIZE}; 
            
			Cursor cursor = resolver.query(selectedImage,
                    fileSizeColumn, null, null, null);
			
            cursor.moveToFirst();

            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            // If the size is unknown, the value stored is null.  But since an int can't be
            // null in java, the behavior is implementation-specific, which is just a fancy
            // term for "unpredictable".  So as a rule, check if it's null before assigning
            // to an int.  This will happen often:  The storage API allows for remote
            // files, whose size might not be locally known.
            String size = null;
            if (!cursor.isNull(sizeIndex)) {
                // Technically the column stores an int, but cursor.getString will do the
                // conversion automatically.
                size = cursor.getString(sizeIndex);
            } 
            
			cursor.close();

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(resolver.getType(selectedImage));
			if(size != null){
			    metadata.setContentLength(Long.parseLong(size));
			}
			
			S3TaskResult result = new S3TaskResult();

			// Put the image data into S3.
			try {
				//s3Client.createBucket(Constants.getPictureBucket());

				PutObjectRequest por = new PutObjectRequest(
						Constants.getPictureBucket(), Constants.PICTURE_NAME,
						resolver.openInputStream(selectedImage),metadata);
				s3Client.putObject(por);
				
				
			} catch (Exception exception) {

				result.setErrorMessage(exception.getMessage());
			}

			return result;
		}

		protected void onPostExecute(S3TaskResult result) {

			dialog.dismiss();

			if (result.getErrorMessage() != null) {

				displayErrorAlert(
						UpdateTicketScreen.this.getString(R.string.upload_failure_title),
						result.getErrorMessage());
			}else{
				new S3GeneratePresignedUrlTask().execute();
			}
		}
	}*/
	
	/*private class S3GeneratePresignedUrlTask extends AsyncTask<Void, Void, S3TaskResult> {

		protected S3TaskResult doInBackground(Void... voids) {

			S3TaskResult result = new S3TaskResult();
			
			try {
		// 	Ensure that the image will be treated as such.
				ResponseHeaderOverrides override = new ResponseHeaderOverrides();
				override.setContentType("image/jpeg");
				
		// 	Generate the presigned URL.
				
		// 	Added an hour's worth of milliseconds to the current time.
				Date expirationDate = new Date(
						System.currentTimeMillis() + 3600000);
				GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(
						Constants.getPictureBucket(), Constants.PICTURE_NAME);
				urlRequest.setExpiration(expirationDate);
				urlRequest.setResponseHeaders(override);
				
				URL url = s3Client.generatePresignedUrl(urlRequest);
				System.out.println("~~~~~~~~~~ URL::"+url);
				result.setUri(Uri.parse(url.toURI().toString()));
				
			} catch (Exception exception) {
				
				result.setErrorMessage(exception.getMessage());
			}
			
			return result;
		}
		
		protected void onPostExecute(S3TaskResult result) {
			
			if (result.getErrorMessage() != null) {
				
				displayErrorAlert(
						UpdateTicketScreen.this
						.getString(R.string.browser_failure_title),
						result.getErrorMessage());
			} else if (result.getUri() != null) {
				
		// 	Display in Browser.
		//	startActivity(new Intent(Intent.ACTION_VIEW, result.getUri()));
				System.out.println("URL:"+result.getUri());
				//filenameET.setText(""+result.getUri().toString());
				finalFileName = result.getUri().toString();
				//Toast.makeText(getApplicationContext(), ""+result.getUri(), Toast.LENGTH_SHORT).show();
			}
		}
	}*/
	
	protected void displayErrorAlert(String title, String message) {

		AlertDialog.Builder confirm = new AlertDialog.Builder(this);
		confirm.setTitle(title);
		confirm.setMessage(message);

		confirm.setNegativeButton(
				UpdateTicketScreen.this.getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						//S3UploaderActivity.this.finish();
					}
				});

		confirm.show().show();
	}
	
	private class S3TaskResult {
		String errorMessage = null;
		Uri uri = null;

		public String getErrorMessage() {
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		public Uri getUri() {
			return uri;
		}

		public void setUri(Uri uri) {
			this.uri = uri;
		}
	}
	
	// 

}
