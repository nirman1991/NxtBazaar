package in.co.retail.nxtbazaar.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

import static in.co.retail.nxtbazaar.Utility.AppConstants.ApplicationLayoutModeAction.*;

/**
 * Created by Niha on 1/5/2016.
 */
public class ProductCommonActivity extends AppCompatActivity implements AsyncHttpRequest.RequestListener, View.OnClickListener, ProductCommonAdapter.Listener {
    private static String TAG = ProductCommonActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar mToolbar;
    private Button mBtnPrev, mBtnNext, mBtnCart;
    private RecyclerView mListView;
    private Loading mLoading;
    private int mPosition,mPos;
    private boolean mStatus;
    private MaterialSearchView mSearchView;

    // parameter
    private String _subCatCode = "";
    private String _subCatName = "";
    // limit
    int _per_page = 10, _total_items = 0;
    int _lower_limit = 0, _upper_limit = 10;
    int txtSize = 0;

    // for suggestion list (false) or search page (true)
    boolean searchStatus = false;

    //
    private ProductCommonAdapter mProductAdapter;
    private AsyncHttpRequest mAsyncHttpRequest;

    private AppConstants.ApplicationLayoutModeAction MODE = NONE;
    private String _type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("TAG : " + TAG);
        initData(getIntent());
        switch (MODE)
        {
            case WISHLIST:
                setContentView(R.layout.activity_wishlist);
                break;
            default:
                setContentView(R.layout.activity_product_common);

                mBtnCart = (Button) findViewById(R.id.btnCart);

                mBtnCart.setText(" Cart >> ");
                mBtnCart.setTextSize(txtSize);
                mBtnCart.setOnClickListener(this);
                break;
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mContext = this;

        mBtnPrev = (Button) findViewById(R.id.btnPrev);
        mBtnNext = (Button) findViewById(R.id.btnNext);

        /* for materialized search view */
        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        initSearchData();

        ActionBarHelper.init(this, mToolbar, true, true, _subCatName, false);

        mListView = (RecyclerView) findViewById(R.id.mListItem);
        ((TextView) findViewById(R.id.txtHeading)).setText(_subCatName);

        mBtnPrev.setText(" << Prev ");
        mBtnPrev.setTextSize(txtSize);
        mBtnPrev.setOnClickListener(this);

        mBtnNext.setText(" Next >> ");
        mBtnNext.setTextSize(txtSize);
        mBtnNext.setOnClickListener(this);

        requestProduct();
    }

    private void initData(Intent intent) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int _dpi = metrics.densityDpi;

        switch (_dpi) {
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
        if (intent != null) {
            MODE = AppConstants.ApplicationLayoutModeAction.valueOf(intent.getExtras().getString("MODE", NONE.name()));
            switch (MODE) {
                case PRODUCT: {
                    _subCatCode = intent.getExtras().getString("SUB_CAT_CODE");
                    _subCatName = intent.getExtras().getString("SUB_CAT_NAME");
                }
                break;
                case NEW_PRODUCT: {
                    _subCatCode = "0";
                    _subCatName = "NEW 30 PRODUCTS";
                }
                break;
                case WISHLIST: {
                    _subCatCode = "0";
                    _subCatName = "WISHLIST";
                }
                break;
                case MONTHLY_BASKET: {
                    _subCatCode = "0";
                    _subCatName = "MONTHLY BASKET";
                }
                break;
                case SEARCH: {
                    _subCatCode = intent.getExtras().getString("KEY");
                    _subCatName = "SEARCH LIST";
                    _type = intent.getExtras().getString("TYPE");
                    searchStatus = intent.getExtras().getBoolean("STATUS");
                }
                break;
                case PROMOTION: {
                    _subCatCode = intent.getExtras().getString("PROMO_CODE");
                    _subCatName = intent.getExtras().getString("PROMO_CODE");
                }
                break;
                case SINGLE_PRODUCT:
                    mPos = intent.getExtras().getInt("pos");
                    mStatus = intent.getExtras().getBoolean("status");
                    break;
            }

            System.out.println("CODE : " + _subCatCode);
            System.out.println("NAME : " + _subCatName);
        }
    }

    private void requestProduct() {
        JSONArray jArr = new JSONArray();
        switch (MODE) {
            case PRODUCT: {
                try {
                    JSONObject jObj = new JSONObject();
                    jObj.put("CUSTID", LoggedUser.customer.getCustId());
                    jObj.put("SUB_CAT_CODE", _subCatCode);
                    jObj.put("UPPER_LIMIT", _upper_limit);
                    jObj.put("LOWER_LIMIT", _lower_limit);
                    jArr.put(jObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callApi(Apis.PRODUCT_LIST_URL, jArr, Apis.PRODUCT_LIST_CODE);
            }
            break;
            case NEW_PRODUCT: {
                try {
                    JSONObject jObj = new JSONObject();
                    jObj.put("TYPE", "FULL");
                    jObj.put("CUSTID", LoggedUser.customer.getCustId());
                    jObj.put("UPPER_LIMIT", _upper_limit);
                    jObj.put("LOWER_LIMIT", _lower_limit);
                    jArr.put(jObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callApi(Apis.NEW_PRODUCT_URL, jArr, Apis.NEW_PRODUCT_CODE);
            }
            break;
            case WISHLIST:
            case MONTHLY_BASKET: {
                int pref_list_id = -1;
                switch (MODE) {
                    case MONTHLY_BASKET:
                        pref_list_id = 1;
                        break;
                    case WISHLIST:
                        pref_list_id = 0;
                        break;
                }
                try {
                    JSONObject jObj = new JSONObject();
                    jObj.put("CUSTID", LoggedUser.customer.getCustId());
                    jObj.put("PREF_LIST_ID", pref_list_id);
                    jObj.put("UPPER_LIMIT", _upper_limit);
                    jObj.put("LOWER_LIMIT", _lower_limit);
                    jArr.put(jObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callApi(Apis.GET_PREF_LIST_URL, jArr, Apis.GET_PREF_LIST_CODE);
            }
            break;
            case SEARCH: {
                JSONObject jObj = new JSONObject();
                try {
                    jObj.put("KEY", _subCatCode);
                    jObj.put("TYPE", _type);
                    jObj.put("LOWER_LIMIT", _lower_limit);
                    jObj.put("UPPER_LIMIT", _upper_limit);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jArr.put(jObj);
                callApi(Apis.SEARCH_LIST_URL, jArr, Apis.SEARCH_LIST_CODE);
            }
            break;
            case PROMOTION: {
                JSONObject jObj = new JSONObject();
                try {
                    jObj.put("TYPE", "FULL");
                    jObj.put("ID", _subCatCode);
                    jObj.put("LOWER_LIMIT", _lower_limit);
                    jObj.put("UPPER_LIMIT", _upper_limit);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jArr.put(jObj);
                callApi(Apis.GET_PROMOTION_URL, jArr, Apis.GET_PROMOTION_CODE);
            }
        }
    }

    private void initSearchData() {
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
                Intent intentSearch = new Intent(ProductCommonActivity.this, ProductCommonActivity.class).putExtras(bn);
                //startActivity(intentSearch);
                startActivityForResult(intentSearch, 2);
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
                Intent intentSearch = new Intent(ProductCommonActivity.this, SingleProductActivity.class).putExtras(bn);
                startActivityForResult(intentSearch, 2);
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
    public void onBackPressed() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);

        menu.findItem(R.id.action_overflow).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
            {
                switch (MODE)
                {
                    case MONTHLY_BASKET:
                        Intent intentHome = new Intent(ProductCommonActivity.this,HomeActivity.class);
                        startActivity(intentHome);
                        break;
                    default:
                        finish();
                }
            }
            break;
            case R.id.action_cart: {
                SQLiteAdapter db = new SQLiteAdapter(mContext);
                db.open();
                int cart_count = db.getCartSize();
                db.close();
                if (cart_count > 0) {
                    Bundle bn = new Bundle();
                    bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.CART.name());
                    Intent intentCart = new Intent(ProductCommonActivity.this, CartActivity.class).putExtras(bn);
                    startActivity(intentCart);
                } else {
                    Snackbar.make(mListView, "No Item in app_cart.", Snackbar.LENGTH_LONG).show();
                }
            }
            break;
            default:
                System.out.println("Default block clicked on option menu click");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPrev: {
                if (_lower_limit > 0) {
                    _lower_limit = _lower_limit - _per_page;
                    _upper_limit = _upper_limit - _per_page;
                    requestProduct();
                } else {
                    Snackbar.make(mBtnPrev, "You are in first", Snackbar.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.btnNext: {
                if (_upper_limit <= _total_items) {
                    _lower_limit = _lower_limit == 0 ? _per_page : (_lower_limit + _per_page);
                    _upper_limit = _upper_limit + _per_page;
                    requestProduct();
                } else {
                    Snackbar.make(mBtnNext, "You are in last", Snackbar.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.btnCart: {
                SQLiteAdapter db = new SQLiteAdapter(mContext);
                db.open();
                CartModel cartModel = db.getCartItem();
                db.close();
                if (cartModel != null) {
                    Bundle bn = new Bundle();
                    bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.CART.name());
                    Intent intentCart = new Intent(ProductCommonActivity.this, CartActivity.class).putExtras(bn);
                    startActivityForResult(intentCart, 1);
                } else {
                    Snackbar.make(mBtnCart, "No Item in app_cart.", Snackbar.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    private void dispButton() {
        System.out.println("BUTTON " + _total_items + " - " + _upper_limit + " - " + _lower_limit);
        if (_lower_limit < _upper_limit && _upper_limit < _total_items) {
            //System.out.println("BUTTON DISP SECOND " + _total_items + " - " + _upper_limit + " - " + _lower_limit);
            mBtnNext.setVisibility(View.VISIBLE);
            mBtnPrev.setVisibility(View.VISIBLE);
        }

        if (_lower_limit == 0 && _upper_limit < _total_items) {
            //System.out.println("BUTTON DISP FIRST " + _total_items + " - " + _upper_limit + " - " + _lower_limit);
            mBtnNext.setVisibility(View.VISIBLE);
            mBtnPrev.setVisibility(View.INVISIBLE);
        }

        if (_lower_limit < _upper_limit && _upper_limit >= _total_items) {
            //System.out.println("BUTTON DISP THIRD " + _total_items + " - " + _upper_limit + " - " + _lower_limit);
            mBtnNext.setVisibility(View.INVISIBLE);
            mBtnPrev.setVisibility(View.VISIBLE);
        }

        if (_lower_limit == 0 && _total_items <= _upper_limit) {
            //System.out.println("BUTTON DISP FOURTH " + _total_items + " - " + _upper_limit + " - " + _lower_limit);
            mBtnNext.setVisibility(View.INVISIBLE);
            mBtnPrev.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onRequestCompleted(String response, int requestCode) {
        //System.out.println(response);
        try {
            if (!response.equals("[]") && requestCode != 0) {
                JSONArray jArray = new JSONArray(response.trim());
                switch (requestCode) {
                    case Apis.SEARCH_LIST_CODE: {
                        JSONArray jTemp = new JSONArray();
                        System.out.println("SEARCH STATUS : " + searchStatus);
                        if (MODE.equals(SEARCH) && searchStatus) {
                            if (!jArray.getJSONObject(0).has("ERROR")) {
                                if (jArray.getJSONObject(0).getBoolean("STATUS")) {
                                    _total_items = Integer.valueOf(jArray.getJSONObject(0).getString("COUNT"));
                                    List<ProductCommonModel> _prodList = getProductList(jArray.getJSONObject(0).getJSONArray("LIST"));
                                    System.out.println("SIZE : " + _prodList.size());

                                    mProductAdapter = new ProductCommonAdapter(mContext, this, _prodList, SEARCH); // or change to NEW_PRODUCTS and similarly write in adapter
                                    mListView.setAdapter(mProductAdapter);
                                    mListView.setLayoutManager(new LinearLayoutManager(this));

                                    // set total value if any
                                    SQLiteAdapter db = new SQLiteAdapter(mContext);
                                    db.open();
                                    JSONArray jArr = db.getTotal();
                                    db.close();
                                    onItemChangeInCart(jArr);
                                } else
                                    Snackbar.make(mListView, "NO PRODUCTS FETCHED. TRY AGAIN !!!", Snackbar.LENGTH_SHORT).show();
                            } else
                                Snackbar.make(mListView, jArray.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
                        } else {
                            if (!jArray.getJSONObject(0).has("ERROR")) {
                                if (jArray.getJSONObject(0).has("COUNT")) {
                                    JSONArray jArr = jArray.getJSONObject(0).getJSONArray("LIST");
                                    for (int i = 0; i < jArr.length(); i++) {
                                        String url = Apis.URL + "assets/products/" + jArr.getJSONObject(i).getString("P_CODE") + ".jpg";
                                        JSONObject jObj = new JSONObject();
                                        jObj.put("P_NAME", jArr.getJSONObject(i).getString("P_NAME"));
                                        jObj.put("P_CODE", jArr.getJSONObject(i).getString("P_CODE"));
                                        jObj.put("BR_CODE", jArr.getJSONObject(i).getString("BR_CODE"));
                                        jObj.put("URL", url);
                                        jTemp.put(jObj);
                                    }
                                }
                            } else {
                                JSONObject jObj = new JSONObject();
                                jObj.put("P_NAME", jArray.getJSONObject(0).getString("ERROR"));
                                jObj.put("P_CODE", "0");
                                jObj.put("BR_CODE", "0");
                                jObj.put("URL", "");
                                jTemp.put(jObj);
                            }
                            this.mSearchView.setSuggestions(jTemp);
                        }
                        mLoading.dismiss();
                    }
                    break;
                    case Apis.NEW_PRODUCT_CODE:
                    case Apis.PRODUCT_LIST_CODE: {
                        if (!jArray.getJSONObject(0).has("ERROR")) {
                            if (jArray.getJSONObject(0).getBoolean("STATUS")) {
                                _total_items = Integer.valueOf(jArray.getJSONObject(0).getJSONArray("LIST").getJSONObject(0).getString("TOTAL_COUNT"));
                                System.out.println(jArray.getJSONObject(0).getJSONArray("LIST"));
                                System.out.println("TOTAL COUNT : " + _total_items);
                                List<ProductCommonModel> _prodList = getProductList(jArray.getJSONObject(0).getJSONArray("LIST"));
                                System.out.println("SIZE : " + _prodList.size());

                                mProductAdapter = new ProductCommonAdapter(mContext, this, _prodList, PRODUCT); // or change to NEW_PRODUCTS and similarly write in adapter
                                mListView.setAdapter(mProductAdapter);
                                mListView.setLayoutManager(new LinearLayoutManager(this));

                                // set total value if any
                                SQLiteAdapter db = new SQLiteAdapter(mContext);
                                db.open();
                                JSONArray jArr = db.getTotal();
                                db.close();
                                onItemChangeInCart(jArr);
                            } else
                                Snackbar.make(mListView, "NO PRODUCTS FETCHED. TRY AGAIN !!!", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(mListView, jArray.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
                        }
                        mLoading.dismiss();
                    }
                    break;
                    case Apis.ADD_REMOVE_PREF_LIST_CODE: {
                        if (!jArray.getJSONObject(0).has("ERROR")) {
                            if (jArray.getJSONObject(0).getBoolean("STATUS")) {
                                mProductAdapter.removeItem(mPosition);
                                Snackbar.make(mListView, "PRODUCT SUCCESSFULLY REMOVED", Snackbar.LENGTH_SHORT).show();
                            } else
                                Snackbar.make(mListView, "NO PRODUCTS FETCHED. TRY AGAIN !!!", Snackbar.LENGTH_SHORT).show();
                        } else
                            Snackbar.make(mListView, jArray.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
                        mLoading.dismiss();
                    }
                    break;
                    case Apis.GET_PREF_LIST_CODE: {
                        if (!jArray.getJSONObject(0).has("ERROR")) {
                            if (jArray.getJSONObject(0).getBoolean("STATUS")) {
                                _total_items = Integer.valueOf(jArray.getJSONObject(0).getJSONArray("LIST").getJSONObject(0).getString("TOTAL_COUNT"));
                                List<ProductCommonModel> _prodList = getProductList(jArray.getJSONObject(0).getJSONArray("LIST"));
                                mProductAdapter = new ProductCommonAdapter(mContext, this, _prodList, MODE);
                                mListView.setAdapter(mProductAdapter);
                                mListView.setLayoutManager(new LinearLayoutManager(this));

                                // set total value if any
                                if (MODE == MONTHLY_BASKET) {
                                    SQLiteAdapter db = new SQLiteAdapter(mContext);
                                    db.open();
                                    JSONArray jArr = db.getTotal();
                                    db.close();
                                    onItemChangeInCart(jArr);
                                }
                            } else
                                Snackbar.make(mListView, "NO PRODUCTS FETCHED. TRY AGAIN !!!", Snackbar.LENGTH_SHORT).show();
                        } else
                            Snackbar.make(mListView, jArray.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
                        mLoading.dismiss();
                    }
                    break;
                    case Apis.GET_PROMOTION_CODE: {
                        if (!jArray.getJSONObject(0).has("ERROR")) {
                            if (jArray.getJSONObject(0).getBoolean("STATUS")) {
                                _total_items = Integer.valueOf(jArray.getJSONObject(0).getJSONArray("LIST").getJSONObject(0).getString("TOTAL_COUNT"));
                                List<ProductCommonModel> _prodList = getProductList(jArray.getJSONObject(0).getJSONArray("LIST"));
                                mProductAdapter = new ProductCommonAdapter(mContext, this, _prodList, MODE);
                                mListView.setAdapter(mProductAdapter);
                                mListView.setLayoutManager(new LinearLayoutManager(this));
                            } else
                                Snackbar.make(mListView, "NO PRODUCTS FETCHED. TRY AGAIN !!!", Snackbar.LENGTH_SHORT).show();
                        } else
                            Snackbar.make(mListView, jArray.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
                        mLoading.dismiss();
                    }
                    break;
                }
            }
            dispButton();
        } catch (JSONException e) {
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
        System.out.println("DATA : " + data);
        switch (requestCode) {
            case 0: { // image click option
                int pos = mPos;
                boolean status = true;
                // refresh the list items
                mProductAdapter.refreshPosition(pos);

                if (!status)
                {
                    mProductAdapter.removeItem(pos);
                }
            }
            break;
            case 1: { // for cart activity
                // refresh the list items
                mProductAdapter.refreshAll();
            }
            break;
            case 2: { // from search option
                requestProduct();
            }
            break;
        }

        //changes in footer
        switch (MODE) {
            // No action for WishList
            case PRODUCT:
            case CART:
            case MONTHLY_BASKET:
            case REVIEW_ORDER:
            case SEARCH: {
                SQLiteAdapter db = new SQLiteAdapter(mContext);
                db.open();
                JSONArray jArr = db.getTotal();
                db.close();
                onItemChangeInCart(jArr);
            }
            break;
        }
    }

    private List<ProductCommonModel> getProductList(JSONArray jArray) {
        List<ProductCommonModel> _prodList = new ArrayList();
        if (jArray.length() > 0) {
            ProductCommonModel _prodData;
            for (int i = 0; i < jArray.length(); i++) {
                _prodData = new ProductCommonModel();
                try {
                    _prodData.setProductBrCode(jArray.getJSONObject(i).getString("BR_CODE"));
                    _prodData.setProductCode(jArray.getJSONObject(i).getString("P_CODE"));
                    _prodData.setProductName(jArray.getJSONObject(i).getString("P_NAME"));
                    _prodData.setProductMRP(jArray.getJSONObject(i).getString("MRP"));
                    _prodData.setProductDFRate(jArray.getJSONObject(i).getString("DF_SALE_RATE"));
                    _prodData.setProductBalQty(jArray.getJSONObject(i).getString("BAL_QTY"));
                    _prodList.add(_prodData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return _prodList;
    }

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode) {
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }

    @Override
    public void onItemChangeInCart(JSONArray jArr) {
        if (jArr != null) {
            try {
                ((TextView) findViewById(R.id.totalValue)).setText(mContext.getResources().getString(R.string.totalValue) + " " + jArr.getJSONObject(0).getString("TOTAL_AMOUNT"));
                ((TextView) findViewById(R.id.totalSaving)).setText(mContext.getResources().getString(R.string.totalSavings) + " " + jArr.getJSONObject(0).getString("TOTAL_SAVING"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ((TextView) findViewById(R.id.totalValue)).setText(mContext.getResources().getString(R.string.totalValue) + " 0");
            ((TextView) findViewById(R.id.totalSaving)).setText(mContext.getResources().getString(R.string.totalSavings) + " 0");
        }
    }

    @Override
    public void onItemChangeInPrefList(JSONArray jArr, int position) {
        if (jArr != null) {
            mPosition = position;
            callApi(Apis.ADD_REMOVE_PREF_LIST_URL, jArr, Apis.ADD_REMOVE_PREF_LIST_CODE);
        }
    }

    @Override
    public void onImageClick(String productCode, int position) {
        Bundle bn = new Bundle();
        bn.putString("P_CODE", productCode);
        bn.putString("MODE", MODE.name());
        bn.putInt("POS", position);
        Intent intentSingleProd = new Intent(ProductCommonActivity.this, SingleProductActivity.class).putExtras(bn);
        startActivityForResult(intentSingleProd, 0);
    }
}
