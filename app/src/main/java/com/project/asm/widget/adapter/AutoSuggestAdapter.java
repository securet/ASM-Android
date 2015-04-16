package com.project.asm.widget.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Sharad on 4/1/2015.
 */
public abstract class AutoSuggestAdapter extends ArrayAdapter<Object> implements Filterable {
    private static final String TAG = AutoSuggestAdapter.class.getName();
    private List<Object> resultList;

    private String fetchSize="10";//default to 10

    public AutoSuggestAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }


    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Object getItem(int index) {
        return resultList.get(index);
    }

    public String getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(String fetchSize) {
        this.fetchSize = fetchSize;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = fetchSuggestionResults(constraint.toString(), fetchSize);
                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    if(resultList!=null) {
                        filterResults.count = resultList.size();
                    }
                }
                return filterResults;
            }


            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return displayTextForSelection(resultValue);
            }
        };
        return filter;
    }

    protected abstract List<Object> fetchSuggestionResults(String s, String fetchSize);

    protected abstract CharSequence displayTextForSelection(Object resultValue);

}

