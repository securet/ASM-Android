package com.project.asm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.data.model.PartOrderRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sharad on 5/8/2015.
 */
public class UpdatePORequestScreen  extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = UpdatePORequestScreen.class.getName();
    private SharedPreferences myPrefs;
    ProgressDialog pd;

    private ImageView backBtn;
    private ImageView statusImg;
    private TextView loggedInUser;

    private TextView poRequestTitle;
    private TextView status;
    private TextView initiatedDate;
    private TextView partOrderRequestIdView;
    private TextView partName;
    private TextView partDescription;
    private TextView cost;
    private TextView initiatedBy;
    private TextView respondedBy;
    private TextView updatedOn;
    private Button authorizeBTN,rejectBTN;

    private LinearLayout poOptions;

    String ticketId;
    Integer partOrderRequestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, API.bugsenseAPI);
        setContentView(R.layout.update_po_request_layout);

        myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);

        loggedInUser = (TextView)findViewById(R.id.employeeName);
        loggedInUser.setText("Logged in as " + myPrefs.getString("EmployeeName", ""));

        backBtn = (ImageView) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        ticketId = getIntent().getExtras().getString("ticketId");
        partOrderRequestId = getIntent().getExtras().getInt("partOrderRequestId");

        poRequestTitle = (TextView)findViewById(R.id.poRequestTitle);
        poRequestTitle.setText("PO Request Status : " + ticketId+" - "+partOrderRequestId);

        authorizeBTN = (Button)findViewById(R.id.authorizeBTN);
        authorizeBTN.setOnClickListener(this);

        rejectBTN = (Button)findViewById(R.id.rejectBTN);
        rejectBTN.setOnClickListener(this);

        pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);

        poOptions = (LinearLayout)findViewById(R.id.poOptions);

        status = (TextView)findViewById(R.id.status);
        statusImg = (ImageView)findViewById(R.id.statusImg);
        initiatedDate = (TextView)findViewById(R.id.initiatedDate);

        partOrderRequestIdView = (TextView)findViewById(R.id.partOrderRequestId);
        partName = (TextView)findViewById(R.id.partName);
        partDescription = (TextView)findViewById(R.id.partDescription);
        cost = (TextView)findViewById(R.id.cost);
        initiatedBy = (TextView)findViewById(R.id.initiatedBy);
        respondedBy = (TextView)findViewById(R.id.respondedBy);
        updatedOn = (TextView)findViewById(R.id.updatedOn);

        FetchPORequestTask poRequestTask = new FetchPORequestTask();
        poRequestTask.execute(new String[]{ticketId,String.valueOf(partOrderRequestId)});

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.authorizeBTN:
                UpdatePORequestStatusTask updatePORequestStatusTask = new UpdatePORequestStatusTask();
                updatePORequestStatusTask.execute(String.valueOf(partOrderRequestId),"Authorize");
                break;
            case R.id.rejectBTN:
                UpdatePORequestStatusTask rejectPORequestStatusTask = new UpdatePORequestStatusTask();
                rejectPORequestStatusTask.execute(String.valueOf(partOrderRequestId),"Reject");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private class FetchPORequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        };

        @Override
        protected String doInBackground(String... params) {
            String response = "";

            try {
                Map<String,Object> requestParams = new HashMap<String, Object>();
                requestParams.put("partOrderRequestId",params[1]);
                requestParams.put("j_username",myPrefs.getString("UserName",""));
                requestParams.put("j_password",myPrefs.getString("Password",""));
                response = API.fetchPartOrderRequestForRequestId(requestParams);

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
                        JSONObject resultObj = new JSONObject(result);
                        JSONObject o = resultObj.getJSONObject("data");

                        PartOrderRequest partOrderRequest = new PartOrderRequest();

                        partOrderRequest.setTicketId(o.getString("ticketId"));
                        partOrderRequest.setPartOrderRequestId(o.getInt("partOrderRequestId"));
                        partOrderRequest.setPartName(o.getJSONObject("serviceSparePart").getString("partName"));
                        partOrderRequest.setPartDescription(o.getJSONObject("serviceSparePart").getString("partDescription"));
                        BigDecimal poRequestCost = new BigDecimal(o.getDouble("cost"));
                        partOrderRequest.setCost(poRequestCost);
                        if(o.getJSONObject("initiatedBy")!=null){
                            partOrderRequest.setInitiatedBy(o.getJSONObject("initiatedBy").getString("userId"));
                        }
                        if(!o.isNull("respondedBy")){
                            partOrderRequest.setRespondedBy(o.getJSONObject("respondedBy").getString("userId"));
                        }
                        partOrderRequest.setCreatedTimestamp(o.getString("createdTimestamp"));
                        partOrderRequest.setLastUpdatedTimestamp(o.getString("lastUpdatedTimestamp"));
                        partOrderRequest.setStatus(o.getString("statusId"));

                        status.setText(partOrderRequest.getStatus());
                        Utils.setPORequestStatusImageIcon(partOrderRequest.getStatus(), statusImg);
                        initiatedDate.setText(partOrderRequest.getCreatedTimestamp());

                        partOrderRequestIdView.setText(String.valueOf(partOrderRequest.getPartOrderRequestId()));
                        partName.setText(partOrderRequest.getPartName());
                        partDescription.setText(partOrderRequest.getPartDescription());
                        cost.setText(partOrderRequest.getCost().toPlainString());
                        if(!TextUtils.isEmpty(partOrderRequest.getInitiatedBy())) {
                            initiatedBy.setText(partOrderRequest.getInitiatedBy());
                        }
                        if(!TextUtils.isEmpty(partOrderRequest.getRespondedBy()) && !partOrderRequest.getRespondedBy().equals("null")) {
                            respondedBy.setText(partOrderRequest.getRespondedBy());
                            respondedBy.setVisibility(View.VISIBLE);
                        }else{
                            respondedBy.setVisibility(View.GONE);
                        }

                        updatedOn.setText(partOrderRequest.getLastUpdatedTimestamp());

                        if(partOrderRequest.getStatus().equals("Initiated")){
                            poOptions.setVisibility(View.VISIBLE);
                        }else{
                            poOptions.setVisibility(View.GONE);
                        }

                    }else{
                        JSONObject message = new JSONObject(result).getJSONObject("messages");
                        if(!message.isNull("defaultMessage")){
                            Toast.makeText(getApplicationContext(), "" + message.getString("defaultMessage"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG,"Error while fetching part order:",e);
                    Toast.makeText(getApplicationContext(), "Error in response. Please try again.",  Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        "Error in response. Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class UpdatePORequestStatusTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        };

        @Override
        protected String doInBackground(String... params) {
            String response = "";

            try {

                Map<String,Object> requestParams = new HashMap<String, Object>();
                requestParams.put("partOrderRequestId",params[0]);
                requestParams.put("status.enumDescription",params[1]);
                requestParams.put("j_username",myPrefs.getString("UserName",""));
                requestParams.put("j_password",myPrefs.getString("Password",""));
                response = API.updatePORequestStatus(requestParams);

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
                        //startActivity(getIntent());
                    }else{
                        JSONObject obj = new JSONObject(result);
                        JSONArray array = new JSONArray(obj.getString("messages"));
                        String message = array.getJSONObject(0).getString("defaultMessage");

                        Toast.makeText(getApplicationContext(), ""+message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e(TAG,"Could not update PO Request status",e);
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

}
