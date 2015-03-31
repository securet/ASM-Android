package com.project.asm;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import com.data.model.Complain;

@SuppressLint("NewApi")
public class TicketViewScreen extends Activity implements OnClickListener, OnItemClickListener{

	ProgressDialog pd;
	private SharedPreferences myPrefs;
	private SharedPreferences.Editor prefsEditor;
	
	private ArrayList<Complain> m_orders = null;
	private ArrayList<Complain> temp_orders = null;
    private ComplainOrder complainAdapter;
    private ListView complainLV;
    private ImageView backBtn, filterBtn;
    private Dialog filterDialog;
    private ImageView refreshBtn;
    private static final String[] GENRES = new String[] {
        "Open", "Work_In_Progress", "Resolved", "Closed"
    };
	
    final ArrayList<String> status = new ArrayList<String>();
    
    private TextView employeeName;
    ImageView cancelTV;
    Button okBtn;
    ListView listView;
    ArrayAdapter<String> adapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 BugSenseHandler.initAndStartSession(TicketViewScreen.this, API.bugsenseAPI);
		setContentView(R.layout.view_ticket_layout);
		
		myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
		
		// resources
		complainLV = (ListView) findViewById(R.id.ticketLV);
		backBtn = (ImageView) findViewById(R.id.backBtn);
		filterBtn = (ImageView) findViewById(R.id.filterBtn);
		refreshBtn = (ImageView) findViewById(R.id.refreshBtn);
		employeeName = (TextView)findViewById(R.id.employeeName);
		
		// listener
		backBtn.setOnClickListener(this);
		filterBtn.setOnClickListener(this);
		refreshBtn.setOnClickListener(this);
		
		complainLV.setOnItemClickListener(this);
		
		pd = new ProgressDialog(TicketViewScreen.this);
		pd.setMessage("Please wait...");
		pd.setCancelable(false);
		
		
		filterDialog = new Dialog(this,R.style.CustomDialogTheme);
		filterDialog.setContentView(R.layout.filter_dialog);
		filterDialog.setCancelable(true);
		filterDialog.setCanceledOnTouchOutside(true);
		LinearLayout listLayout = (LinearLayout) filterDialog.findViewById(R.id.listlayout);
		cancelTV = (ImageView) filterDialog.findViewById(R.id.cancelTv);
		okBtn = (Button) filterDialog.findViewById(R.id.okBtn);
					
		listView = new ListView(getApplicationContext());
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, GENRES);
		listView.setAdapter(adapter);
					
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listLayout.addView(listView);
        listView.setCacheColorHint(0x00000000);
		
        if(status.size()>0){
        	for(int i = 0; i<listView.getAdapter().getCount();i++){
        		for(int j=0; j<status.size();j++){
        			if(listView.getAdapter().getItem(i).toString().toLowerCase().equals(status.get(j).toLowerCase())){
        				listView.setItemChecked(i, true);
        				break;
        			}	
        		}
        	}
        }else{
        	//Toast.makeText(getApplicationContext(), ""+status.size(), Toast.LENGTH_SHORT).show();
        	for(int j=0; j<adapter.getCount();j++){
        		listView.setItemChecked(j, true);
    		}
        }
		
		
		employeeName.setText("Logged in as "+myPrefs.getString("EmployeeName", "Chikhalkar"));
		
		ViewComplainTask task = new ViewComplainTask();
   		task.execute(new String[]{myPrefs.getString("EmployeeCode", "0"),myPrefs.getString("EmployeeToken", "Token")});
				 
   		
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.refreshBtn:
			
			ViewComplainTask task = new ViewComplainTask();
	   		task.execute(new String[]{myPrefs.getString("EmployeeCode", "0"),myPrefs.getString("EmployeeToken", "Token")});
			break;
		
		case R.id.backBtn:
			finish();
			break;
		
		case R.id.filterBtn:
/*			filterDialog = new Dialog(this,R.style.CustomDialogTheme);
			filterDialog.setContentView(R.layout.filter_dialog);
			filterDialog.setCancelable(true);
			filterDialog.setCanceledOnTouchOutside(true);
			LinearLayout listLayout = (LinearLayout) filterDialog.findViewById(R.id.listlayout);
			ImageView cancelTV = (ImageView) filterDialog.findViewById(R.id.cancelTv);
			Button okBtn = (Button) filterDialog.findViewById(R.id.okBtn);
						
			final ListView listView = new ListView(getApplicationContext());
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, GENRES);
			listView.setAdapter(adapter);
						
	        listView.setItemsCanFocus(false);
	        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        listLayout.addView(listView);
	        listView.setCacheColorHint(0x00000000);*/
	        
	        listView.setOnItemClickListener(new OnItemClickListener() {

				@SuppressLint("NewApi")
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					
					
					System.out.println("ITEMS: "+listView.getCheckedItemPositions().toString());
					SparseBooleanArray checked = listView.getCheckedItemPositions();
					//long[] i = listView.getCheckItemIds();
					
					//Toast.makeText(getApplicationContext(), "Hey.. "+checked.size()+"  "+i.toString(), Toast.LENGTH_SHORT).show();
					
				}
			});
	        
	        if(status.size()>0){
	        	for(int i = 0; i<listView.getAdapter().getCount();i++){
	        		for(int j=0; j<status.size();j++){
	        			if(listView.getAdapter().getItem(i).toString().equalsIgnoreCase(status.get(j).toLowerCase())){
	        				listView.setItemChecked(i, true);
	        				break;
	        			}	
	        		}
	        	}
	        }
	        
	        
	        cancelTV.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					filterDialog.dismiss();
				}
			});
	        
	        okBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					filterDialog.dismiss();
					
					System.out.println("ITEMS: "+listView.getCheckedItemPositions().toString());
					SparseBooleanArray checked = listView.getCheckedItemPositions();
					
					System.out.println("CheckedItem:::"+listView.getCheckedItemCount());
					
					if(checked.size()>0){
						
						status.clear();
						for(int i = 0; i < listView.getAdapter().getCount(); i++) {
						    if (checked.get(i)) {
						        System.out.println("ITEM: "+listView.getAdapter().getItem(i));
						        status.add(""+listView.getAdapter().getItem(i).toString().toLowerCase());
						     }
						}
						
						temp_orders = new ArrayList<Complain>();
						temp_orders.clear();
						for(int i = 0; i< m_orders.size();i++){
							for(int j=0; j<status.size();j++){
								if(m_orders.get(i).getComplaintStatus().toLowerCase().equals(status.get(j).toLowerCase())){
									Complain s = new Complain(m_orders.get(i).getComplaintID(), m_orders.get(i).getComplaintCode(),m_orders.get(i).getComplaintStatus(),m_orders.get(i).getSiteCode(),m_orders.get(i).getComplaintCategoryName());
									temp_orders.add(s);
									break;
								}
							}
						}
						
						if(temp_orders.size()>0){
							complainLV.setVisibility(View.VISIBLE);
							complainAdapter = new ComplainOrder(TicketViewScreen.this, R.layout.complain_row, temp_orders);
						    complainLV.setAdapter(complainAdapter);
						    complainAdapter.notifyDataSetChanged();
						}else{
							complainLV.setVisibility(View.INVISIBLE);
							// THIS IS REMAINING TO IMPLEMENT
							String msg = "";
							/*if(status.size()>0){
								 
					        	for(int j=0; j<status.size();j++){
					        		msg = msg + ", "+status.get(j); 	
					        	}
					        	
					        }else{
					        	msg = "";
					        }*/
							Toast.makeText(getApplicationContext(), "No tickets available to display!", Toast.LENGTH_SHORT).show();
						}
						
						
					}else{
						complainAdapter = new ComplainOrder(TicketViewScreen.this, R.layout.complain_row, m_orders);
					    complainLV.setAdapter(complainAdapter);
					    
					}
					
					
					
					/*if(listView.getCheckedItemPosition() == -1){
						Toast.makeText(getApplicationContext(), "Please select option to filter", Toast.LENGTH_SHORT).show();
					}
					//Toast.makeText(getApplicationContext(), ""+listView.getCheckedItemPosition(), Toast.LENGTH_SHORT).show();
					
					if(listView.getCheckedItemPosition()==0){ // for OPEN
						System.out.println("OPEN");
						temp_orders = new ArrayList<Complain>();
						temp_orders.clear();
						for(int i=0; i<m_orders.size();i++){
							if(m_orders.get(i).getComplaintStatus().equals("Open")){
								System.out.println("STATUS:: "+m_orders.get(i).getComplaintStatus());
								Complain s = new Complain(m_orders.get(i).getComplaintID(), m_orders.get(i).getComplaintCode(),m_orders.get(i).getComplaintStatus());
								temp_orders.add(s);
							}
						}
						if(temp_orders.size()>0){
							
							complainAdapter = new ComplainOrder(TicketViewScreen.this, R.layout.complain_row, temp_orders);
						    complainLV.setAdapter(complainAdapter);
						    complainAdapter.notifyDataSetChanged();
						}else{
							Toast.makeText(getApplicationContext(), "Complain not available", Toast.LENGTH_SHORT).show();
						}
						filterDialog.dismiss();
					}else if(listView.getCheckedItemPosition()==1){ // for Work in progress
						System.out.println("Work in Progress");
						temp_orders = new ArrayList<Complain>();
						temp_orders.clear();
						for(int i=0; i<m_orders.size();i++){
							if(m_orders.get(i).getComplaintStatus().equals("Work In Progress")){
								Complain s = new Complain(m_orders.get(i).getComplaintID(), m_orders.get(i).getComplaintCode(),m_orders.get(i).getComplaintStatus());
								temp_orders.add(s);
							}
						}
						if(temp_orders.size()>0){
							complainAdapter = new ComplainOrder(TicketViewScreen.this, R.layout.complain_row, temp_orders);
						    complainLV.setAdapter(complainAdapter);
						    complainAdapter.notifyDataSetChanged();
						}else{
							Toast.makeText(getApplicationContext(), "Complain not available", Toast.LENGTH_SHORT).show();
						}
						filterDialog.dismiss();
					}else if(listView.getCheckedItemPosition()==2){ // for Resolved
						System.out.println("Resolved");
						temp_orders = new ArrayList<Complain>();
						temp_orders.clear();
						for(int i=0; i<m_orders.size();i++){
							if(m_orders.get(i).getComplaintStatus().equals("Resolved")){
								Complain s = new Complain(m_orders.get(i).getComplaintID(), m_orders.get(i).getComplaintCode(),m_orders.get(i).getComplaintStatus());
								temp_orders.add(s);
							}
						}
						if(temp_orders.size()>0){
							complainAdapter = new ComplainOrder(TicketViewScreen.this, R.layout.complain_row, temp_orders);
						    complainLV.setAdapter(complainAdapter);
						    complainAdapter.notifyDataSetChanged();
						}else{
							Toast.makeText(getApplicationContext(), "Complain not available", Toast.LENGTH_SHORT).show();
						}
						filterDialog.dismiss();
					}else if(listView.getCheckedItemPosition()==3){ // for Closed
						System.out.println("Closed");
						temp_orders = new ArrayList<Complain>();
						temp_orders.clear();
						for(int i=0; i<m_orders.size();i++){
							if(m_orders.get(i).getComplaintStatus().equals("Closed")){
								Complain s = new Complain(m_orders.get(i).getComplaintID(), m_orders.get(i).getComplaintCode(),m_orders.get(i).getComplaintStatus());
								temp_orders.add(s);
							}
						}
						if(temp_orders.size()>0){
							complainAdapter = new ComplainOrder(TicketViewScreen.this, R.layout.complain_row, temp_orders);
						    complainLV.setAdapter(complainAdapter);
						    complainAdapter.notifyDataSetChanged();
						}else{
							Toast.makeText(getApplicationContext(), "Complain not available", Toast.LENGTH_SHORT).show();
						}
						
						
						
					}*/
				}
			});
	             
			
			filterDialog.show();
			
			break;
		}
	}
	
	
	
	private class ViewComplainTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.show();
		};

		@Override
		protected String doInBackground(String... params) {
			String response = "";

			try {
				/*System.out.println("EmployeeCode:"+params[0]);
				System.out.println("EmployeeToken:"+params[1]);*/
							
				//response = API.GetSites(params[0], params[1]);
				response = API.ViewComplaintsRest(myPrefs.getString("UserName", ""),myPrefs.getString("Password", ""));

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
						JSONObject dataObject = new JSONObject(obj.getString("data"));
						JSONArray array = new JSONArray(dataObject.getString("data"));
						System.out.println("ARRAY:"+array.toString(2));
						m_orders = new ArrayList<Complain>();
						m_orders.clear();
						for(int i=0; i<array.length();i++){
							JSONObject o = array.getJSONObject(i);
							Complain s = new Complain(
									o.getString("ticketId"), 
									o.getString("ticketId"), 
									o.getString("statusId"),
									o.getString("siteName"),
									o.getString("serviceTypeName"));
							m_orders.add(s);
						}
						
						//m_orders = new ArrayList<Complain>();
						complainAdapter = new ComplainOrder(TicketViewScreen.this, R.layout.complain_row, m_orders);
					    complainLV.setAdapter(complainAdapter);
					
					}else{
						Toast.makeText(getApplicationContext(), ""+new JSONObject(result).getString("messages").toString(), Toast.LENGTH_SHORT).show();
					}
					        
				     //getOrders();
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
	
	private class ComplainOrder extends ArrayAdapter<Complain> {
        private ArrayList<Complain> items;
        public ComplainOrder(Context context, int textViewResourceId, ArrayList<Complain> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }
        
        @Override
        public Complain getItem(int position) {
        	return this.items.get(position);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	View v = convertView;
            if (v == null) {
            	LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.complain_row, null);
            }
            Complain o = items.get(position);
            if (o != null) {
            	TextView complainCode = (TextView) v.findViewById(R.id.complainCodeTV);
                TextView status = (TextView)v.findViewById(R.id.complainStatusTV);
                ImageView statusImg = (ImageView)v.findViewById(R.id.statusImg);
                TextView siteName = (TextView) v.findViewById(R.id.siteName);
                TextView complaintCategoryName = (TextView) v.findViewById(R.id.complaintCategoryNameTV);
                
                if (complainCode != null) {
                	complainCode.setText(o.getComplaintCode());  
                }
                
                if (status != null) {
                	status.setText(o.getComplaintStatus());  
                }
                
                if(siteName != null){
                	siteName.setText(o.getSiteCode());
                }
                
                if(complaintCategoryName != null){
                	complaintCategoryName.setText(o.getComplaintCategoryName());
                }
                
                if(statusImg != null){
                	if(o.getComplaintStatus().toLowerCase().equals("open")){
                		statusImg.setImageResource(R.drawable.open_icon);
                	}else if(o.getComplaintStatus().toLowerCase().equals("work_in_progress")){
                		statusImg.setImageResource(R.drawable.inprogress_icon);
                	}else if(o.getComplaintStatus().toLowerCase().equals("resolved")){
                		statusImg.setImageResource(R.drawable.resolved_icon);
                	}else{
                		statusImg.setImageResource(R.drawable.closed_icon);
                	}
                }                   	
            }
            return v;
        }
    }
    
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		
		Complain c = (Complain)arg0.getItemAtPosition(position);
		
		System.out.println("Name: "+c.getComplaintCategoryName());
		System.out.println("Name: "+c.getComplaintID());
		System.out.println("Name: "+c.getComplaintStatus());
		
		Intent in = new Intent(this, UpdateTicketScreen.class);
		in.putExtra("complain_id",c.getComplaintID() ); 
		in.putExtra("complain_status",c.getComplaintStatus() ); 
		in.putExtra("complain_code",c.getComplaintCode() );
		startActivity(in);
	}
	
	
	
	
	public void ShowAlertDialog(){
		 AlertDialog.Builder builder = new AlertDialog.Builder(TicketViewScreen.this);
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
