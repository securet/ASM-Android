package com.project.asm.widget.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.project.asm.API;
import com.project.asm.R;
import com.project.asm.SubmitTicketScreen;
import com.project.asm.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sharad on 4/1/2015.
 */
public class SiteSuggestion extends AutoSuggestAdapter implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = SiteSuggestion.class.getName();

    private Toast toast = Toast.makeText(this.getContext().getApplicationContext(), "Could not find sites", Toast.LENGTH_SHORT);
    private String userName;
    private String password;

    private String searchString;
    private JSONObject selectedSite=null;

    public SiteSuggestion(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public JSONObject getSelectedSite() {
        return selectedSite;
    }

    public void setSelectedSite(JSONObject selectedSite) {
        this.selectedSite = selectedSite;
    }

    @Override
    public List<Object> fetchSuggestionResults(String searchString, String resultSize) {
       selectedSite = null;//reset selection to null.
       this.searchString=searchString;
       String responseStr = API.searchSites(searchString,resultSize,userName,password);
        try {
            JSONObject result = new JSONObject(responseStr);
            if(result.getString("status").toString().equals("success")) {
                JSONArray sites = new JSONArray(result.getString("data"));
                List<Object> results = new ArrayList<Object>();
                try {
                    Log.d(TAG, "Response: " + responseStr);
                    for (int i = 0; i < sites.length(); i++) {
                        JSONObject site = sites.getJSONObject(i);
                        results.add(site);
                    }
                } catch (JSONException e) {
                    if(!toast.getView().isShown()) {
                        toast.show();
                    }
                    Log.e(TAG, "could not find sites", e);
                }
                return results;
            }
        } catch (JSONException e) {
            if(!toast.getView().isShown()) {
                toast.show();
            }
            Log.e(TAG, "could not find sites", e);
        }
        return null;
    }


    @Override
    public View  getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            //assign the list text view created..
//            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            View layout = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent,false);
            view = (TextView)layout.findViewById(R.id.list_item_text);
        }
        JSONObject site = (JSONObject)getItem(position);
        try {
            ((TextView)view).setText(site.getString("name") + "\n(" + (site.isNull("area") ? "" : site.getString("area")) + ")");
        } catch (JSONException e) {
            Log.e(TAG,"Could not retrieve site:"+site,e);
        }
        return view;
    }

    @Override
    protected CharSequence displayTextForSelection(Object resultValue) {
        String displayText = null;
        if(resultValue!=null) {
            JSONObject site = (JSONObject) resultValue;
            try {
                displayText=site.getString("name") + "\n(" + (site.isNull("area") ? "" : site.getString("area")) + ")";
            } catch (JSONException e) {
                Log.e(TAG,"Error on display of site after selection..:"+site,e);
            }
        }
        return displayText;
    }

    @Override
    public void  onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        JSONObject site = (JSONObject) adapterView.getItemAtPosition(position);
        selectedSite = site;
        Activity activity = (Activity)adapterView.getContext();
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)activity.getCurrentFocus();
        //set the font size on selection .. make this configurable
        autoCompleteTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
    }

    @Override
    public void onClick(View view) {
        AutoCompleteTextView autoCompleteTextView = ((AutoCompleteTextView)view);
        //set the font size on user typing .. make this configurable
        autoCompleteTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        autoCompleteTextView.setText(searchString);
        if(!TextUtils.isEmpty(searchString)) {
            autoCompleteTextView.setSelection(searchString.length());
        }
    }
}
