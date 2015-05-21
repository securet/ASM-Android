package com.project.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.project.asm.listener.EndlessScrollListener;

@SuppressLint("NewApi")
public class TicketViewScreen extends Activity implements OnClickListener, OnItemClickListener{

    private static final String TAG = TicketViewScreen.class.getName();
    private static final int ticketsToDisplay = 10;
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
    private static final String[] TICKET_STATUS = new String[] {
        "Open", "Work_In_Progress", "Resolved", "Closed"
    };
	
    final ArrayList<String> status = new ArrayList<String>();
    
    private TextView employeeName;
    ImageView cancelTV;
    Button okBtn;
    ListView listView;
    ArrayAdapter<String> statusFilterAdapter;

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
		statusFilterAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, TICKET_STATUS);
		listView.setAdapter(statusFilterAdapter);
					
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
        	for(int j=0; j< statusFilterAdapter.getCount();j++){
        		listView.setItemChecked(j, true);
    		}
        }

        complainLV.setOnScrollListener(new EndlessScrollListener(ticketsToDisplay) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                ViewComplainTask task = new ViewComplainTask();
                task.setStart(totalItemsCount);
                task.setLength(ticketsToDisplay);
                setTicketStatusFilter(task);
                task.execute();
            }
        });
		
		employeeName.setText("Logged in as "+myPrefs.getString("EmployeeName", "Chikhalkar"));

        startLoadingTickets();
	}

    private void startLoadingTickets() {
        ViewComplainTask task = new ViewComplainTask();
        //always reset the adapter on start...
        if(m_orders!=null){
            m_orders.clear();
            //complainAdapter.notifyDataSetChanged();
        }
        setTicketStatusFilter(task);
        task.execute();
    }

    private void setTicketStatusFilter(ViewComplainTask task) {
        populateFilterStatus();
        if(status!=null && status.size()>0){
            List<String> statusFilter = new ArrayList<String>();
            for(String selectedStatus : status){
                statusFilter.add(selectedStatus);
            }
            task.setStatusFilter(statusFilter);
        }
    }


    @Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.refreshBtn:

            startLoadingTickets();
			break;
		
		case R.id.backBtn:
			finish();
			break;
		
		case R.id.filterBtn:

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
					SparseBooleanArray checked = listView.getCheckedItemPositions();
					if(checked.size()>0){
                        startLoadingTickets();
					}
				}
			});

			filterDialog.show();
			break;
		}
	}
	
	
	
	private class ViewComplainTask extends AsyncTask<String, Void, String> {
        private List<String> statusFilter;
        private int start=0;
        private int length=10;

        public List<String> getStatusFilter() {
            return statusFilter;
        }

        public void setStatusFilter(List<String> statusFilter) {
            this.statusFilter = statusFilter;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        @Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.show();
		};

		@Override
		protected String doInBackground(String... params) {
			String response = "";

			try {
				/*Log.d(TAG,"EmployeeCode:"+params[0]);
				Log.d(TAG,"EmployeeToken:"+params[1]);*/
							
				//response = API.GetSites(params[0], params[1]);

                Map<String,Object> requestParams = new HashMap<String, Object>();
                requestParams.put("j_username", myPrefs.getString("UserName", ""));
                requestParams.put("j_password", myPrefs.getString("Password", ""));
                if(statusFilter!=null && !statusFilter.isEmpty()){
                    requestParams.put("statusFilter", statusFilter);
                }
                if(start>0){
                    requestParams.put("start", String.valueOf(start));
                }
                if(length>0){
                    requestParams.put("length", String.valueOf(length));
                }
				response = API.fetchTickets(requestParams);

			} catch (Exception e) {
				e.printStackTrace();
				response = "No Internet";
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			Log.d(TAG,"The Message Is: " + result);
			
			if (!(result.equals("No Internet")) || !(result.equals(""))) {

				try {
					
					if(result.toString().contains("status") && (new JSONObject(result).getString("status").toString().equals("success"))){
						JSONObject obj = new JSONObject(result);
						JSONObject dataObject = new JSONObject(obj.getString("data"));
						JSONArray array = new JSONArray(dataObject.getString("data"));
						Log.d(TAG,"ARRAY:"+array.toString(2));
                        if(complainLV.getAdapter()==null) {
                            m_orders = new ArrayList<Complain>();
                            complainAdapter = new ComplainOrder(TicketViewScreen.this, R.layout.complain_row, m_orders);
                            complainLV.setAdapter(complainAdapter);
                        }
						for(int i=0; i<array.length();i++){
							JSONObject o = array.getJSONObject(i);
							Complain s = new Complain(
									o.getString("ticketId"), 
									o.getString("ticketId"), 
									o.getString("statusId"),
									o.getJSONObject("site").getString("name"),
                                    o.getJSONObject("serviceType").getString("name"));
							s.setSource(o.getString("source"));
							m_orders.add(s);
                            complainAdapter.notifyDataSetChanged();
						}
						

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
			v.setBackgroundColor(Color.WHITE);
            Complain o = items.get(position);
            if (o != null) {
            	TextView complainCode = (TextView) v.findViewById(R.id.complainCodeTV);
                TextView status = (TextView)v.findViewById(R.id.complainStatusTV);
                ImageView statusImg = (ImageView)v.findViewById(R.id.statusImg);
                TextView siteName = (TextView) v.findViewById(R.id.siteName);
                TextView complaintCategoryName = (TextView) v.findViewById(R.id.complaintCategoryNameTV);

				if(o.getSource()!=null && o.getSource().equals("HP_TOOL")){
					v.setBackgroundColor(Color.parseColor("#324DA1"));
					complainCode.setTextColor(Color.WHITE);
					status.setTextColor(Color.WHITE);
					siteName.setTextColor(Color.WHITE);
					complaintCategoryName.setTextColor(Color.WHITE);
				}else {
					v.getBackground().clearColorFilter();
					complainCode.setTextColor(Color.BLACK);
					status.setTextColor(Color.BLACK);
					siteName.setTextColor(Color.BLACK);
					complaintCategoryName.setTextColor(Color.BLACK);
				}
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

    public void populateFilterStatus(){
        SparseBooleanArray checked = listView.getCheckedItemPositions();

        //Log.d(TAG,"CheckedItem:::"+listView.getCheckedItemCount());

        if(checked.size()>0) {

            status.clear();
            for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                if (checked.get(i)) {
                    Log.d(TAG, "ITEM: " + listView.getAdapter().getItem(i));
                    status.add("" + listView.getAdapter().getItem(i).toString().toLowerCase());
                }
            }
        }
   }

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		
		Complain c = (Complain)arg0.getItemAtPosition(position);
		
		Log.d(TAG,"Name: "+c.getComplaintCategoryName());
		Log.d(TAG,"Name: "+c.getComplaintID());
		Log.d(TAG,"Name: "+c.getComplaintStatus());
		
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
