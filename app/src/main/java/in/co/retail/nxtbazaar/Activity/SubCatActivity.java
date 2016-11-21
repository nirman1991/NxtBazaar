package in.co.retail.nxtbazaar.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.co.retail.nxtbazaar.Adapter.CommonListAdapter;
import in.co.retail.nxtbazaar.Controller.ActionBarHelper;
import in.co.retail.nxtbazaar.Model.Apis;
import in.co.retail.nxtbazaar.Model.SubCatItem;
import in.co.retail.nxtbazaar.R;
import in.co.retail.nxtbazaar.Utility.AppConstants;
import in.co.retail.nxtbazaar.Utility.Utils;
import in.co.retail.nxtbazaar.WebService.AsyncHttpRequest;

/**
 * Created by Niha on 12/4/2015.
 */

public class SubCatActivity extends AppCompatActivity implements AsyncHttpRequest.RequestListener
{
    private static String TAG = SubCatActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar mToolbar;

    private AsyncHttpRequest mAsyncHttpRequest;

    private RecyclerView mListItem;
    private CommonListAdapter mSubCatAdapter;

    private JSONArray jArr;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_cat);
        System.out.println("TAG : " + TAG);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        ActionBarHelper.init(this, mToolbar, true, true, getIntent().getExtras().getString("CAT_NAME"), false);

        this.mContext = this;
        mListItem = (RecyclerView) findViewById(R.id.listItem);
        System.out.println("CAT_NAME : " + getIntent().getExtras().getString("CAT_NAME"));
        JSONArray jArr = new JSONArray();
        try
        {
            JSONObject jObj = new JSONObject();
            jObj.put("CAT_CODE", getIntent().getExtras().getInt("CAT_CODE"));
            jArr.put(jObj);
            callApi(Apis.SUB_CAT_LIST_URL, jArr, Apis.SUB_CAT_LIST_CODE);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestCompleted(String response, int requestCode)
    {
        try
        {
            if(!response.equals("[]") && requestCode != 0)
            {
                JSONArray jArray = new JSONArray(response.trim());
                switch(requestCode)
                {
                    case Apis.SUB_CAT_LIST_CODE:
                    {
                        if (!jArray.getJSONObject(0).has("ERROR"))
                        {
                            if (jArray.getJSONObject(0).getBoolean("STATUS"))
                            {
                                jArr = jArray.getJSONObject(0).getJSONArray("LIST");
                                mSubCatAdapter = new CommonListAdapter(this, jArray.getJSONObject(0).getJSONArray("LIST"), AppConstants.ApplicationLayoutModeAction.SUB_CATEGORY);
                                mListItem.setAdapter(mSubCatAdapter);
                                mListItem.setLayoutManager(new LinearLayoutManager(this));
                                mListItem.addOnItemTouchListener(new RecyclerTouchListener(mContext, mListItem, new ClickListener() {
                                    @Override
                                    public void onClick(View view, int position) {

                                        Bundle bundle = new Bundle();
                                        bundle.putString("MODE", AppConstants.ApplicationLayoutModeAction.PRODUCT.name());
                                        try {
                                            bundle.putString("SUB_CAT_CODE", jArr.getJSONObject(position).getString("SUB_CAT_CODE"));
                                            bundle.putString("SUB_CAT_NAME", jArr.getJSONObject(position).getString("SUB_CAT_NAME"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        Intent intentProduct = new Intent(SubCatActivity.this, ProductCommonActivity.class).putExtras(bundle);
                                        startActivity(intentProduct);
                                    }

                                    @Override
                                    public void onLongClick(View view, int position) {
                                        System.out.println("POSITION : " + position);
                                        try {
                                            System.out.println("SUB CAT CODE : " + jArr.getJSONObject(position).getString("SUB_CAT_CODE"));
                                            System.out.println("SUB CAT NAME : " + jArr.getJSONObject(position).getString("SUB_CAT_NAME"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }));
                            }
                            else
                                Snackbar.make(mListItem, "NO SUB CATEGORY FETCHED. TRY AGAIN !!!", Snackbar.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Snackbar.make(mListItem, jArray.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(Exception e, int requestCode)
    {
        Utils.showToast(this, "Please check your Internet Connection");
    }

    @Override
    public void onRequestStarted(int requestCode)
    {

    }

    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener
    {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context _context, final RecyclerView _recyclerView, final ClickListener _clickListener)
        {
            this.clickListener = _clickListener;
            gestureDetector = new GestureDetector(_context, new GestureDetector.SimpleOnGestureListener()
            {
                @Override
                public boolean onSingleTapUp(MotionEvent e)
                {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e)
                {
                    View child = _recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null)
                    {
                        clickListener.onLongClick(child, _recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
        {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e))
            {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e)
        {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
        {

        }
    }

    public interface ClickListener
    {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        //Utils.LogBook(Utils.LogType.INFO, TAG, "1_ " + _requestUrl);
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }
}
