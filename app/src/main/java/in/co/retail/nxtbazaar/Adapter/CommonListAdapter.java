package in.co.retail.nxtbazaar.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.co.retail.nxtbazaar.Model.MonthlyBasketModel;
import in.co.retail.nxtbazaar.Model.SubCatItem;
import in.co.retail.nxtbazaar.R;
import in.co.retail.nxtbazaar.Utility.AppConstants;

import static in.co.retail.nxtbazaar.Utility.AppConstants.ApplicationLayoutModeAction.*;

/**
 * Created by Niha on 12/4/2015.
 */
public class CommonListAdapter extends RecyclerView.Adapter<CommonListAdapter.CommonViewHolder>
{
    JSONArray jList;
    private MonthlyBasketListener mMBListener;
    //private List<SubCatItem> mSubCatData;
    //private List<MonthlyBasketModel> mMBData;
    private LayoutInflater inflater;
    private Context mContext;
    private AppConstants.ApplicationLayoutModeAction MODE = NONE;

    public CommonListAdapter(Context _context, JSONArray _data, AppConstants.ApplicationLayoutModeAction _mode)//, MonthlyBasketListener _mbListener)
    {
        this.mContext = _context;//_activity.getApplicationContext();
        inflater = LayoutInflater.from(mContext);
        this.jList = _data;
        this.MODE = _mode;

        switch (MODE)
        {
            case MONTHLY_BASKET:
            {
                this.mMBListener = (MonthlyBasketListener) mContext;
            }
            break;
        }
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.common_list_row, parent, false);
        CommonViewHolder holder = new CommonViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CommonViewHolder holder, final int position)
    {
        try {
            final JSONObject jObj = jList.getJSONObject(position);
            switch (MODE) {
                case SUB_CATEGORY: {
                    holder.titleName.setText(jObj.getString("SUB_CAT_NAME"));
                    holder.titleCode.setText(jObj.getString("SUB_CAT_CODE"));
                }
                break;
                case MONTHLY_BASKET: {
                    holder.titleName.setText(jObj.getString("PREF_LIST_NAME"));
                    holder.titleCode.setText(jObj.getString("PREF_LIST_ID"));
                    holder.imgLayout.setVisibility(View.VISIBLE);

                    holder.imgRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("REMOVE");
                            String mbId = null;
                            try {
                                mbId = jObj.getString("PREF_LIST_ID");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mMBListener.onChangeInMonthlyBasket(v, mbId, position);
                        }
                    });
                }
                break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount()
    {
        return jList.length();
    }

    public boolean addItem(JSONObject jObjModel)
    {
        if(jObjModel != null) {
            //addimg dataList
            System.out.println("BEFORE ADDING : " + jList.length());
            //mMBData.add(getItemCount(), mbModel);
            jList.put(jObjModel);
            System.out.println("AFTER ADDING : " + jList.length());
            //adding
            notifyItemInserted(getItemCount());
            return true;
        }
        else
            return false;
    }

    @SuppressLint("NewApi")
    public boolean removeItem(int position) {
        if (jList.length() >= position + 1) {
            //removing datalist
            System.out.println("BEFORE REMOVING : " + jList.length());
            //mMBData.remove(position); will not work
            jList.remove(position);
            System.out.println("AFTER REMOVING : " + jList.length());

            //removing views
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());

            return true;
        }
        return false;
    }

    class CommonViewHolder extends RecyclerView.ViewHolder
    {
        TextView titleName, titleCode;
        LinearLayout imgLayout;
        ImageView imgRemove;

        public CommonViewHolder(View itemView)
        {
            super(itemView);
            titleName = (TextView) itemView.findViewById(R.id.titleName);
            titleCode = (TextView) itemView.findViewById(R.id.titleCode);
        }
    }

    /*private List<SubCatItem> getSubCatList(JSONArray jArray)
    {
        List<SubCatItem> _subCatData = new ArrayList();
        if (jArray.length() > 0)
        {
            SubCatItem _subCatItem;
            for (int i = 0; i < jArray.length(); i++)
            {
                _subCatItem = new SubCatItem();
                try
                {
                    _subCatItem.setSubCatName(jArray.getJSONObject(i).getString("SUB_CAT_NAME"));
                    _subCatItem.setSubCatCode(jArray.getJSONObject(i).getString("SUB_CAT_CODE"));
                    _subCatData.add(_subCatItem);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return _subCatData;
    }/**/

    /*private List<MonthlyBasketModel> getMBList(JSONArray jArray)
    {
        List<MonthlyBasketModel> _mbData = new ArrayList();
        if (jArray.length() > 0)
        {
            MonthlyBasketModel _mbItem;
            for (int i = 0; i < jArray.length(); i++)
            {
                _mbItem = new MonthlyBasketModel();
                try
                {
                    _mbItem.setMonthlyBasketId(jArray.getJSONObject(i).getString("PREF_LIST_ID"));
                    _mbItem.setMonthlyBasketName(jArray.getJSONObject(i).getString("PREF_LIST_NAME"));
                    _mbData.add(_mbItem);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return _mbData;
    }/**/

    public interface MonthlyBasketListener
    {
        void onChangeInMonthlyBasket(View view, String mb_id, int position);
    }
}
