package in.co.retail.nxtbazaar.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.co.retail.applibrary.searchview.MaterialSearchView;
import in.co.retail.nxtbazaar.Adapter.ProductCommonAdapter;
import in.co.retail.nxtbazaar.Controller.ActionBarHelper;
import in.co.retail.nxtbazaar.DBAdapter.SQLiteAdapter;
import in.co.retail.nxtbazaar.Model.Apis;
import in.co.retail.nxtbazaar.Model.CartModel;
import in.co.retail.nxtbazaar.Model.LoggedUser;
import in.co.retail.nxtbazaar.Model.ProductCommonModel;
import in.co.retail.nxtbazaar.R;
import in.co.retail.nxtbazaar.Utility.AppConstants;
import in.co.retail.nxtbazaar.Utility.Loading;
import in.co.retail.nxtbazaar.Utility.Utils;
import in.co.retail.nxtbazaar.WebService.AsyncHttpRequest;

import static in.co.retail.nxtbazaar.Utility.AppConstants.ApplicationLayoutModeAction.NONE;
import static in.co.retail.nxtbazaar.Utility.AppConstants.ApplicationLayoutModeAction.SEARCH;

/**
 * Created by Niha on 1/8/2016.
 */
public class CartActivity extends AppCompatActivity implements View.OnClickListener, ProductCommonAdapter.Listener, AsyncHttpRequest.RequestListener
{
    private static String TAG = CartActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar mToolbar;
    private Button mBtnCart;
    private RecyclerView mListView;
    private Loading mLoading;
    private CartModel mCartModel;
    private ProductCommonAdapter mProductAdapter;
    private MaterialSearchView mSearchView;
    private AsyncHttpRequest mAsyncHttpRequest;
    private SQLiteAdapter db;

    private AppConstants.ApplicationLayoutModeAction MODE = NONE;

    private String mHeading = "";
	private Integer txtSize;

    boolean searchStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_common);
        System.out.println("TAG : " + TAG);
        mListView = (RecyclerView) findViewById(R.id.mListItem);
        mBtnCart = (Button) findViewById(R.id.btnCart);

        initData(getIntent());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mContext = this;

        /* for materialized search view */
        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        initSearchData();

        mBtnCart.setText(" CHECKOUT >> ");
        mBtnCart.setTextSize(txtSize);
        mBtnCart.setOnClickListener(this);

        ActionBarHelper.init(this, mToolbar, true, true, mHeading, false);

        setData();
    }

    private void initData(Intent intent) {
        if (intent != null)
        {
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int _dpi = metrics.densityDpi;

			switch(_dpi)
			{
				case DisplayMetrics.DENSITY_MEDIUM:
					txtSize = 20;
					break;
				case DisplayMetrics.DENSITY_HIGH:
					txtSize = 12;
					break;
				case DisplayMetrics.DENSITY_XHIGH:
					txtSize = 15;					
					break;
				case DisplayMetrics.DENSITY_XXHIGH:
					txtSize = 15;
					break;
				case DisplayMetrics.DENSITY_XXXHIGH:
					txtSize = 15;
					break;
			}

            MODE = AppConstants.ApplicationLayoutModeAction.valueOf(intent.getExtras().getString("MODE", NONE.name()));
            switch(MODE)
            {
                case CART:
                {
                    System.out.println("CART OVERVIEW");
                    mHeading = "CART OVERVIEW";
                }
                break;
                case REVIEW_ORDER:
                {
                    System.out.println("REVIEW ORDER");
                    mHeading = "REVIEW ORDER";

                    ((TextView) findViewById(R.id.totalSaving)).setTypeface(null, Typeface.BOLD);
                    ((TextView) findViewById(R.id.totalValue)).setTypeface(null, Typeface.BOLD);
                    mBtnCart.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    private void setData() {
        ((TextView)findViewById(R.id.txtHeading)).setText(mHeading);
        db = new SQLiteAdapter(mContext);
        db.open();
        mCartModel = db.getCartItem();
        db.close();
        //
        if (mCartModel != null) {
            ((TextView) findViewById(R.id.totalValue)).setText(mContext.getResources().getString(R.string.totalValue) + " " + mCartModel.getmTotal());
            ((TextView) findViewById(R.id.totalSaving)).setText(mContext.getResources().getString(R.string.totalSavings) + " " + mCartModel.getmDiscount());
            List<ProductCommonModel> _prodModelList = mCartModel.getProductItemList();
            mProductAdapter = new ProductCommonAdapter(mContext, this, _prodModelList, MODE);
            mListView.setAdapter(mProductAdapter);
            mListView.setLayoutManager(new LinearLayoutManager(this));
        }
        else {
            finish();
            ((TextView) findViewById(R.id.totalValue)).setText(mContext.getResources().getString(R.string.totalValue) + " 0");
            ((TextView) findViewById(R.id.totalSaving)).setText(mContext.getResources().getString(R.string.totalSavings) + " 0");
        }
    }

    private void initSearchData()
    {
        mSearchView.setVoiceSearch(true);
        mSearchView.setCursorDrawable(R.drawable.color_cursor_white);

        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchStatus = true;
                Bundle bn = new Bundle();
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.SEARCH.name());
                bn.putString("KEY", query);
                bn.putString("TYPE", "OR");
                bn.putBoolean("STATUS", true);
                if (MODE.equals(SEARCH))
                    finish();
                Intent intentSearch = new Intent(CartActivity.this, ProductCommonActivity.class).putExtras(bn);
                startActivityForResult(intentSearch, 1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("TEXT CHANGE : " + newText + "\nLENGTH : " + newText.length());
                searchStatus = false;
                JSONArray jArr = new JSONArray();
                JSONObject jObj = new JSONObject();
                try {
                    jObj.put("P_NAME", "ENTER UPTO 3 CHARACTERS TO SEARCH");
                    jObj.put("P_CODE", "0");
                    jObj.put("BR_CODE", "0");
                    jObj.put("URL", "");
                    jArr.put(jObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSearchView.setSuggestions(jArr);
                if (newText.length() >= 3) {
                    jArr = new JSONArray();
                    jObj = new JSONObject();
                    try {
                        jObj.put("TYPE", "OR");
                        jObj.put("KEY", newText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jArr.put(jObj);
                    callApi(Apis.SEARCH_LIST_URL, jArr, Apis.SEARCH_LIST_CODE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextClick(String text) {
                //System.out.println("TEXT CHANGE : " + text);
                String productCode = "";
                try {
                    JSONObject jObj = new JSONObject(text);
                    productCode = jObj.getString("P_CODE");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Bundle bn = new Bundle();
                bn.putString("P_CODE", productCode);
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.NONE.name());
                bn.putInt("POS", -1);
                Intent intentSearch = new Intent(CartActivity.this, SingleProductActivity.class).putExtras(bn);
                startActivityForResult(intentSearch, 1);
                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                System.out.println("onSearchViewShown.");
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                System.out.println("onSearchViewClosed.");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                Intent intent=new Intent();
                setResult(0,intent);
                finish();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);

        menu.findItem(R.id.action_overflow).setVisible(false);
        menu.findItem(R.id.action_cart).setVisible(false);

        return true;
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.btnCart) // checkout button
        {
            if(LoggedUser.customer.getCustName1().equals(""))
            {
                Bundle bn = new Bundle();
                bn.putInt("TYPE", 2);
                bn.putBoolean("EXIT", false);
                Intent intent = new Intent(CartActivity.this, UserInfoActivity.class).putExtras(bn);
                startActivity(intent);
            }
            else
            {
                Bundle bn = new Bundle();
                bn.putString("ADD_TYPE", "PRIMARY");
                Intent intent = new Intent(CartActivity.this, DeliverySpecsActivity.class).putExtras(bn);
                startActivity(intent);
            }

        }
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setData();
    }

    @Override
    public void onRequestCompleted(String response, int requestCode) {
        try
        {
            if(!response.equals("[]") && requestCode != 0) {
                JSONArray jArray = new JSONArray(response.trim());
                switch(requestCode) {
                    case Apis.SEARCH_LIST_CODE: {
                        JSONArray jTemp = new JSONArray();
                        if (!jArray.getJSONObject(0).has("ERROR")) {
                            if (jArray.getJSONObject(0).has("COUNT")) {
                                JSONArray jArr = jArray.getJSONObject(0).getJSONArray("LIST");
                                for(int i = 0; i < jArr.length(); i++)
                                {
                                    String url = Apis.URL + "assets/products/" + jArr.getJSONObject(i).getString("P_CODE") + ".jpg";
                                    JSONObject jObj = new JSONObject();
                                    jObj.put("P_NAME", jArr.getJSONObject(i).getString("P_NAME"));
                                    jObj.put("P_CODE", jArr.getJSONObject(i).getString("P_CODE"));
                                    jObj.put("BR_CODE", jArr.getJSONObject(i).getString("BR_CODE"));
                                    jObj.put("URL", url);
                                    jTemp.put(jObj);
                                }
                            }
                        }
                        else
                        {
                            JSONObject jObj = new JSONObject();
                            jObj.put("P_NAME", jArray.getJSONObject(0).getString("ERROR"));
                            jObj.put("P_CODE", "0");
                            jObj.put("BR_CODE", "0");
                            jObj.put("URL", "");
                            jTemp.put(jObj);
                        }
                        this.mSearchView.setSuggestions(jTemp);
                        mLoading.dismiss();
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
    public void onRequestError(Exception e, int requestCode) {
        Utils.showToast(this, "Please check your Internet Connection");
        mLoading.hide();
    }

    @Override
    public void onRequestStarted(int requestCode) {
        mLoading = new Loading(this);
        mLoading.show();
    }

    @Override
    public void onItemChangeInCart(JSONArray jArr) {
        if(jArr != null)
        {
            try
            {
                ((TextView) findViewById(R.id.totalValue)).setText(mContext.getResources().getString(R.string.totalValue) + " " + jArr.getJSONObject(0).getString("TOTAL_AMOUNT"));
                ((TextView) findViewById(R.id.totalSaving)).setText(mContext.getResources().getString(R.string.totalSavings) + " " + jArr.getJSONObject(0).getString("TOTAL_SAVING"));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
        else
        {
            ((TextView) findViewById(R.id.totalValue)).setText(mContext.getResources().getString(R.string.totalValue) + " 0");
            ((TextView) findViewById(R.id.totalSaving)).setText(mContext.getResources().getString(R.string.totalSavings) + " 0");/**/
        }
    }

    @Override
    public void onItemChangeInPrefList(JSONArray jArr, int position) {

    }

    @Override
    public void onImageClick(String productCode, int position)
    {

    }

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }
}
