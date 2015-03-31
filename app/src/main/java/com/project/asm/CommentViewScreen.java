package com.project.asm;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.data.model.Comments;
import com.data.model.Complain;

public class CommentViewScreen extends Activity implements OnClickListener, OnItemClickListener{

	ProgressDialog pd;
	private SharedPreferences myPrefs;
	private SharedPreferences.Editor prefsEditor;
	
	private ArrayList<Comments> m_orders = null;
	private ArrayList<Comments> temp_orders = null;
    private ComplainOrder complainAdapter;
    private ListView complainLV;
    private ImageView backBtn;
    private Dialog filterDialog;
    
    private static final String[] GENRES = new String[] {
        "Open", "Work In Progress", "Resolved", "Closed"
    };
	
    final ArrayList<String> status = new ArrayList<String>();
    
    private TextView employeeName;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 BugSenseHandler.initAndStartSession(CommentViewScreen.this, API.bugsenseAPI);
		setContentView(R.layout.comments_layout);
		
		myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
		
		// resources
		complainLV = (ListView) findViewById(R.id.commentLV);
		backBtn = (ImageView) findViewById(R.id.backBtn);
		
		
		employeeName = (TextView)findViewById(R.id.employeeName);
		
		// listener
		backBtn.setOnClickListener(this);
		
		
		
		//complainLV.setOnItemClickListener(this);
		complainLV.setClickable(false);
		
		pd = new ProgressDialog(CommentViewScreen.this);
		pd.setMessage("Please wait...");
		pd.setCancelable(false);
		
		employeeName.setText("Logged in as "+myPrefs.getString("EmployeeName", "Chikhalkar"));
		
		ViewCommentsTask task = new ViewCommentsTask();
		task.execute(new String[]{getIntent().getExtras().getString("comment_id"),myPrefs.getString("UserName", "Token"), myPrefs.getString("Password", "")});
		
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		
		case R.id.backBtn:
			finish();
			break;
		
		
		}
	}
	
	
	
	private class ViewCommentsTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.show();
		};

		@Override
		protected String doInBackground(String... params) {
			String response = "";

			try {
				System.out.println("ComplaintID:"+params[0]);
				System.out.println("EmployeeToken:"+params[1]);
							
				//response = API.GetSites(params[0], params[1]);
				response = API.GetAllCommentsByComplaintIDRest(params[0],params[1], params[2]);

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
						JSONArray array = obj.getJSONArray("data");
						
						System.out.println("ARRAY:"+array.toString(2));
						m_orders = new ArrayList<Comments>();
						m_orders.clear();
						for(int i=0; i<array.length();i++){
							JSONObject o = array.getJSONObject(i);
							Comments s = new Comments(o.getString("modifiedByUser"), o.getString("description"), o.getString("lastUpdatedTimestamp"));
							m_orders.add(s);
						}
						
						//m_orders = new ArrayList<Complain>();
						complainAdapter = new ComplainOrder(CommentViewScreen.this, R.layout.comments_row, m_orders);
					    complainLV.setAdapter(complainAdapter);
					}else{
						Toast.makeText(getApplicationContext(), ""+new JSONObject(result).getString("messages").toString(), Toast.LENGTH_SHORT).show();
					}
					
					
				        
				     //getOrders();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Error in response. Please try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class ComplainOrder extends ArrayAdapter<Comments> {
        private ArrayList<Comments> items;
        public ComplainOrder(Context context, int textViewResourceId, ArrayList<Comments> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }
        
        @Override
        public Comments getItem(int position) {
        	return this.items.get(position);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	View v = convertView;
            if (v == null) {
            	LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.comments_row, null);
            }
            Comments o = items.get(position);
            if (o != null) {
            	TextView commentUser = (TextView) v.findViewById(R.id.userNameTV);
                TextView date = (TextView)v.findViewById(R.id.dateTV);
                TextView comment = (TextView)v.findViewById(R.id.comment);
                
                
                if (commentUser != null) {
                	commentUser.setText(o.getName());  
                }
                
                if (date != null) {
                	date.setText(o.getDate());  
                }
                
                if(comment != null){
                	comment.setText(o.getComments());
                }
                
                                   	
            }
            return v;
        }
    }
    
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		
		
	}
	
	
	
	
	public void ShowAlertDialog(){
		 AlertDialog.Builder builder = new AlertDialog.Builder(CommentViewScreen.this);
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
	
	
	// 

}
