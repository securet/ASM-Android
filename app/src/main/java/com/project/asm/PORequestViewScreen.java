package com.project.asm;

import com.bugsense.trace.BugSenseHandler;
import com.data.model.PartOrderRequest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PORequestViewScreen  extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = PORequestViewScreen.class.getName();
    private SharedPreferences myPrefs;

    private ListView poRequestsLV;
    private ImageView backBtn;
    private TextView loggedInUser;

    private PORequestAdapter poRequestAdapter;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, API.bugsenseAPI);
        setContentView(R.layout.list_po_request_layout);

        myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        loggedInUser = (TextView)findViewById(R.id.employeeName);

        backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        pd = new ProgressDialog(PORequestViewScreen.this);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);

        poRequestsLV = (ListView) this.findViewById(R.id.poRequestsLV);
        poRequestsLV.setOnItemClickListener(this);

        loggedInUser.setText("Logged in as " + myPrefs.getString("EmployeeName", "Chikhalkar"));

        String ticketId = getIntent().getExtras().getString("ticketId");
        TextView poTitle = (TextView) this.findViewById(R.id.poTitle);
        //set the title
        poTitle.setText("PO Requests for "+ticketId);

        loadPORequests();

    }

    private void loadPORequests() {
        String ticketId = getIntent().getExtras().getString("ticketId");
        ViewPoRequestTask viewPoRequestTask = new ViewPoRequestTask();
        viewPoRequestTask.execute(new String[]{ticketId, myPrefs.getString("UserName", "Token"), myPrefs.getString("Password", "")});
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            PartOrderRequest partOrderRequest = (PartOrderRequest)adapterView.getItemAtPosition(position);

            Log.d(TAG,"Name: "+partOrderRequest.getTicketId());
            Log.d(TAG, "PartOrder: " + partOrderRequest.getPartOrderRequestId());

            Intent in = new Intent(this, UpdatePORequestScreen.class);
            in.putExtra("ticketId",partOrderRequest.getTicketId());
            in.putExtra("partOrderRequestId",partOrderRequest.getPartOrderRequestId());
            startActivity(in);
    }

    private class ViewPoRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        };

        @Override
        protected String doInBackground(String... params) {
            String response = "";

            try {
                Log.d(TAG, "ticketId:" + params[0]);


                Map<String,Object> requestParams = new HashMap<String,Object>();
                requestParams.put("ticketId",params[0]);
                requestParams.put("j_username",params[1]);
                requestParams.put("j_password",params[2]);
                response = API.getAllPORequestByTicketId(requestParams);

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

                        JSONObject resultObject = new JSONObject(result);
                        JSONArray array = resultObject.getJSONArray("data");
                        List<PartOrderRequest> partOrderRequestList = new ArrayList<PartOrderRequest>();
                        Log.d(TAG, "ARRAY:" + array.toString(2));
                        for(int i=0; i<array.length();i++){
                            JSONObject o = array.getJSONObject(i);
                            PartOrderRequest partOrderRequest = new PartOrderRequest();

                            partOrderRequest.setTicketId(o.getString("ticketId"));
                            partOrderRequest.setPartOrderRequestId(o.getInt("partOrderRequestId"));
                            partOrderRequest.setPartName(o.getJSONObject("serviceSparePart").getString("partName"));
                            BigDecimal cost = new BigDecimal(o.getDouble("cost"));
                            partOrderRequest.setCost(cost);
                            if(o.getJSONObject("initiatedBy")!=null){
                                partOrderRequest.setInitiatedBy(o.getJSONObject("initiatedBy").getString("userId"));
                            }
                            if(o.getJSONObject("respondedBy")!=null){
                                partOrderRequest.setRespondedBy(o.getJSONObject("respondedBy").getString("userId"));
                            }
                            partOrderRequest.setCreatedTimestamp(o.getString("createdTimestamp"));
                            partOrderRequest.setLastUpdatedTimestamp(o.getString("lastUpdatedTimestamp"));
                            partOrderRequest.setStatus(o.getString("statusId"));

                            partOrderRequestList.add(partOrderRequest);
                        }

                        //m_orders = new ArrayList<Complain>();
                        poRequestAdapter = new PORequestAdapter(PORequestViewScreen.this, R.layout.po_request_row, partOrderRequestList);
                        poRequestsLV.setAdapter(poRequestAdapter);
                    }else{
                        Toast.makeText(getApplicationContext(), "" + new JSONObject(result).getString("messages").toString(), Toast.LENGTH_SHORT).show();
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

    private class PORequestAdapter extends ArrayAdapter<PartOrderRequest> {
        private List<PartOrderRequest> items;

        public PORequestAdapter(Context context, int textViewResourceId, List<PartOrderRequest> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public PartOrderRequest getItem(int position) {
            return this.items.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.po_request_row, null);
            }
            PartOrderRequest o = items.get(position);
            if (o != null) {
                //TextView ticketIdView = (TextView) v.findViewById(R.id.ticketId);
                TextView partName = (TextView)v.findViewById(R.id.partName);
                TextView partDetail = (TextView)v.findViewById(R.id.partDetail);
                TextView partStatus = (TextView)v.findViewById(R.id.partStatus);
                ImageView statusImg = (ImageView)v.findViewById(R.id.statusImg);



                if (partName != null) {
                    partName.setText(o.getPartName()+"- Rs." + o.getCost());
                }


                if (partDetail != null) {
                    partDetail.setText("Initiated On: "+o.getCreatedTimestamp());
                }


                if(partStatus != null){
                    partStatus.setText(o.getStatus());
                }
                String status = o.getStatus();
                Utils.setPORequestStatusImageIcon(status, statusImg);


            }
            return v;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadPORequests();
    }
}
