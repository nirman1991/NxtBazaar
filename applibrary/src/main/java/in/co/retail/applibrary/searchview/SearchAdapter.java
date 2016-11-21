package in.co.retail.applibrary.searchview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.co.retail.applibrary.LazyLoading.ImageLoader;
import in.co.retail.applibrary.R;

/**
 * Suggestions Adapter.
 *
 * @author Miguel Catalan Bañuls
 */
public class SearchAdapter extends BaseAdapter implements Filterable {

    private JSONArray jArray;
    private ArrayList<String> data;
    private Drawable suggestionIcon;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;

    //private String[] typeAheadData;

    public SearchAdapter(Context context, JSONArray jArr) {
        inflater = LayoutInflater.from(context);
        jArray = jArr;
        this.data = new ArrayList<>();
        this.imageLoader = new ImageLoader(context, R.styleable.MaterialSearchView_searchSuggestionIcon);
    }

    public SearchAdapter(Context context, JSONArray jArr, Drawable suggestionIcon) {
        inflater = LayoutInflater.from(context);
        jArray = jArr;
        this.data = new ArrayList<>();
        this.suggestionIcon = suggestionIcon;
        this.imageLoader = new ImageLoader(context, suggestionIcon);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                System.out.println("SEARCH CONSTRAINT : " + constraint);
                FilterResults filterResults = new FilterResults();
                if (!TextUtils.isEmpty(constraint)) {

                    // Retrieve the autocomplete results.
                    List<String> searchData = new ArrayList<>();

                    for (int i = 0; i < jArray.length(); i++)
                    {
                        try {
                            searchData.add(jArray.getJSONObject(i).getString("P_NAME"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // Assign the data to the FilterResults
                    filterResults.values = searchData;
                    filterResults.count = searchData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    data = (ArrayList<String>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }

    @Override
    public int getCount() {
        return jArray.length();
    }

    @Override
    public Object getItem(int position) {
        JSONObject jObj = new JSONObject();
        try {
            jObj = jArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jObj;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SuggestionsViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.suggest_item, parent, false);
            viewHolder = new SuggestionsViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SuggestionsViewHolder) convertView.getTag();
        }

        //String currentListData = (String) getItem(position);
        //viewHolder.textView.setText(currentListData);

        String _prodName = "", _url = ""; // _brCode = "", _prodCode = "0"
        try {
            //_brCode = jArray.getJSONObject(position).getString("BR_CODE");
            //_prodCode = jArray.getJSONObject(position).getString("P_CODE");
            System.out.println("SEARCH POSITION : " + position);
            _prodName = jArray.getJSONObject(position).getString("P_NAME");
            _url = jArray.getJSONObject(position).getString("URL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        viewHolder.textView.setText(_prodName);

        // set the controls image
        if(!_url.equals(""))
            imageLoader.DisplayImage(_url, viewHolder.imageView);

        return convertView;
    }

    private class SuggestionsViewHolder {

        TextView textView;
        ImageView imageView;

        public SuggestionsViewHolder(View convertView) {
            textView = (TextView) convertView.findViewById(R.id.suggestion_text);
            imageView = (ImageView) convertView.findViewById(R.id.suggestion_icon);
            if (suggestionIcon != null) {
                imageView.setImageDrawable(suggestionIcon);
            }
        }
    }
}