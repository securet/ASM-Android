package com.project.asm;

import org.json.JSONObject;

import com.bugsense.trace.BugSenseHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginScreen extends Activity implements OnClickListener {

	Button loginBtn;
	EditText emailET, passwordET;
	ProgressDialog pd;
	private SharedPreferences myPrefs;
	private SharedPreferences.Editor prefsEditor;
	private boolean login = false;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(LoginScreen.this, API.bugsenseAPI);
        setContentView(R.layout.login_layout);
		myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
		login = myPrefs.getBoolean("login", false);

        Utils.setAppVersion(this);

		if (login) {
			Intent i = new Intent(LoginScreen.this, OptionScreen.class);
			startActivity(i);
			finish();
		}

		// resources
		loginBtn = (Button) findViewById(R.id.loginBtn);
		emailET = (EditText) findViewById(R.id.emailET);
		passwordET = (EditText) findViewById(R.id.passwordET);

		// lictener
		loginBtn.setOnClickListener(this);

		pd = new ProgressDialog(LoginScreen.this);
		pd.setMessage("Please wait...");
		pd.setCancelable(false);

	}

    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			if ((emailET.getText().toString().trim().equals(null))
					|| (emailET.getText().toString().trim().equals(""))
					|| (passwordET.getText().toString().trim().equals(""))
					|| (passwordET.getText().toString().trim().equals(null))) {
				Toast.makeText(getApplicationContext(),
						"Please enter valid username and password.",
						Toast.LENGTH_SHORT).show();
			} else {
				LoginTask task = new LoginTask();
				task.execute(new String[] {
						emailET.getText().toString().trim(),
						passwordET.getText().toString().trim() });

			}

			break;
		}
	}

	private class LoginTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.show();
		};

		@Override
		protected String doInBackground(String... params) {
			String response = "";

			try {
				response = API.LoginRest(params[0], params[1]);
			} catch (Exception e) {
				e.printStackTrace();
				response = "";
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			if(API.DEBUG)
				System.out.println("The Message Is: " + result);
			if (!(result.equals("No Internet")) || !(result.equals(""))) {
				try {
					
					if(result.toString().contains("status") && (new JSONObject(result).getString("status").toString().equals("success"))){
						JSONObject data = new JSONObject(result);
						JSONObject obj = data.getJSONObject("data");
						JSONObject userLogin = obj.getJSONObject("userLogin");
						if(API.DEBUG){
							System.out.println("====" + obj.getString("userId"));
							System.out.println("====" + obj.getString("userId"));
							System.out.println("====" + obj.getString("fullName"));
							System.out.println("====" + userLogin.getString("enabled"));
							//System.out.println("====" + obj.getString("EmployeeToken"));
						}
						if (userLogin.getString("enabled").equals("false")) {
							Toast.makeText(getApplicationContext(),
									"Invalid Username/Password",
									Toast.LENGTH_SHORT).show();
						} else {
							/*Toast.makeText(getApplicationContext(),
									"You are successfully login.",
									Toast.LENGTH_SHORT).show();*/
							// Storing data to Device

							// Save data to Shared Preferences
							saveAllDataToSharedPreferences(
									obj.getString("userId").toString(), 
									obj.getString("userId").toString(),
									obj.getString("fullName").toString(), 
									userLogin.getString("enabled").toString(),
									//obj.getString("EmployeeToken")
									emailET.getText().toString().trim(),
									passwordET.getText().toString().trim()
									);

							startActivity(new Intent(LoginScreen.this,
									OptionScreen.class));
							finish();
						}
					}else{
						Toast.makeText(getApplicationContext(), ""+new JSONObject(result).getString("messages").toString(), Toast.LENGTH_SHORT).show();
					}
					
					

				} catch (Exception e) {
					if(API.DEBUG){
						e.printStackTrace();	
					}
					
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

	private void saveAllDataToSharedPreferences(String EmployeeID,
			String EmployeeCode, String EmployeeName, String EmployeeValid, String UserName, String Password) {
		prefsEditor = myPrefs.edit();
		prefsEditor.putBoolean("login", true); // value to store
		prefsEditor.commit();

		prefsEditor = myPrefs.edit();
		prefsEditor.putString("EmployeeID", EmployeeID); // value to store
		prefsEditor.commit();

		prefsEditor = myPrefs.edit();
		prefsEditor.putString("EmployeeCode", EmployeeCode); // value to store
		prefsEditor.commit();

		prefsEditor = myPrefs.edit();
		prefsEditor.putString("EmployeeName", EmployeeName); // value to store
		prefsEditor.commit();

		prefsEditor = myPrefs.edit();
		prefsEditor.putString("EmployeeValid", EmployeeValid); // value to store
		prefsEditor.commit();
		
		/*prefsEditor = myPrefs.edit();
		prefsEditor.putString("EmployeeToken", EmployeeToken); // value to store
		prefsEditor.commit();*/
		
		prefsEditor = myPrefs.edit();
		prefsEditor.putString("UserName", UserName); // value to store
		prefsEditor.commit();
		
		prefsEditor = myPrefs.edit();
		prefsEditor.putString("Password", Password); // value to store
		prefsEditor.commit();
		
	}

}
