package in.co.retail.nxtbazaar.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.retail.nxtbazaar.Adapter.SavingAdapter;
import in.co.retail.nxtbazaar.Controller.ActionBarHelper;
import in.co.retail.nxtbazaar.Model.Apis;
import in.co.retail.nxtbazaar.Model.LoggedUser;
import in.co.retail.nxtbazaar.R;
import in.co.retail.nxtbazaar.Utility.Utils;
import in.co.retail.nxtbazaar.WebService.AsyncHttpRequest;

/**
 * Created by Niha on 1/18/16.
 */
public class SavingActivity extends AppCompatActivity implements AsyncHttpRequest.RequestListener
{
    //attributes
    private static String TAG = SavingActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private Context mContext;
    private RecyclerView mListView;
    private SavingAdapter mSavingAdapter;

    private AsyncHttpRequest mAsyncHttpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving);
        System.out.println("TAG : " + TAG);
        this.mContext = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarHelper.init(this, mToolbar, true, true, "Savings", true);

        mListView = (RecyclerView) findViewById(R.id.listItem);
        initSetData();
    }

    private void initSetData() {
        JSONArray jArr = new JSONArray();
        try
        {
            JSONObject jObj = new JSONObject();
            jObj.put("TYPE", "FULL");
            jObj.put("CUSTID", LoggedUser.customer.getCustId());
            jArr.put(jObj);
            callApi(Apis.POPULATE_SAVING_URL, jArr, Apis.POPULATE_SAVING_CODE);
        }
        catch (JSONException e) {
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
    public void onRequestCompleted(String response, int requestCode) {
        try {
            if (!response.equals("[]") && requestCode != 0) {
                JSONArray jArr = new JSONArray(response.trim());
                switch (requestCode) {
                    case Apis.POPULATE_SAVING_CODE: {
                        if (!jArr.getJSONObject(0).has("ERROR")) {
                            TextView _txtMsg = (TextView) findViewById(R.id.txtMsg);
                            if (jArr.getJSONObject(0).getBoolean("STATUS")) {
                                _txtMsg.setVisibility(View.GONE);
                                mListView.setVisibility(View.VISIBLE);
                                System.out.println("RES " + jArr.getJSONObject(0).getJSONArray("LIST"));
                                mSavingAdapter = new SavingAdapter(mContext, jArr.getJSONObject(0).getJSONArray("LIST"));
                                mListView.setAdapter(mSavingAdapter);
                                mListView.setLayoutManager(new LinearLayoutManager(this));
                            }
                            else
                            {
                                _txtMsg.setText("There are no Transactions To Display.");
                                _txtMsg.setVisibility(View.VISIBLE);
                                mListView.setVisibility(View.GONE);
                                Snackbar.make(mListView, "There are no Transactions To Display !!!", Snackbar.LENGTH_SHORT).show();
                            }

                        } else {
                            Snackbar.make(mListView, jArr.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(Exception e, int requestCode) {
        Utils.showToast(this, "Please check your Internet Connection");
    }

    @Override
    public void onRequestStarted(int requestCode) {

    }

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }
}
