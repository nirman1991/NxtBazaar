package in.co.retail.nxtbazaar.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.retail.applibrary.LazyLoading.ImageLoader;
import in.co.retail.nxtbazaar.DBAdapter.SQLiteAdapter;
import in.co.retail.nxtbazaar.Model.Apis;
import in.co.retail.nxtbazaar.Model.LoggedUser;
import in.co.retail.nxtbazaar.Model.ProductCommonModel;
import in.co.retail.nxtbazaar.R;
import in.co.retail.nxtbazaar.Utility.AppConstants;
import in.co.retail.nxtbazaar.Utility.Utils;
import in.co.retail.nxtbazaar.WebService.AsyncHttpRequest;

import static in.co.retail.nxtbazaar.Utility.AppConstants.ApplicationLayoutModeAction.MONTHLY_BASKET;
import static in.co.retail.nxtbazaar.Utility.AppConstants.ApplicationLayoutModeAction.NONE;
import static in.co.retail.nxtbazaar.Utility.AppConstants.ApplicationLayoutModeAction.WISHLIST;

/**
 * Created by Niha on 1/22/2016.
 */
public class SingleProductActivity extends AppCompatActivity implements AsyncHttpRequest.RequestListener, View.OnClickListener
{
    private static String TAG = SingleProductActivity.class.getSimpleName();
    private Context mContext;
    private ImageView mProdImg,mCancel, mBtnWishlist, mBtnMB, mBtnMinus, mBtnPlus, mBtnAddToCart, mBtnRemove, mBtnCart;
    private TextView mMrp,mProdName,mDfRate,mDescription;
    private EditText mSelQty;
    private ImageLoader mImageLoader;
    private AsyncHttpRequest mAsyncHttpRequest;

    private AppConstants.ApplicationLayoutModeAction MODE = NONE;
    private AppConstants.ApplicationLayoutModeAction mPrefListMode = NONE;

    private String mProdCode;
    private int mPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single_product);
        System.out.println("TAG : " + TAG);

        this.mContext = this;

        mProdImg = (ImageView)findViewById(R.id.productImage);
        mCancel = (ImageView)findViewById(R.id.productCancel);

        mBtnWishlist = (ImageView)findViewById(R.id.btnWishlist);
        mBtnMB = (ImageView)findViewById(R.id.btnMonthlyBasket);

        mBtnMinus = (ImageView) findViewById(R.id.btnMinus);
        mBtnPlus = (ImageView) findViewById(R.id.btnPlus);
        mBtnAddToCart = (ImageView) findViewById(R.id.btnAddToCart);
        mBtnRemove = (ImageView) findViewById(R.id.btnRemove);
        mBtnCart = (ImageView) findViewById(R.id.fab);

        mSelQty = (EditText) findViewById(R.id.selQty);

        mMrp= (TextView)findViewById(R.id.mrp);
        mProdName = (TextView)findViewById(R.id.productName);
        mDfRate = (TextView)findViewById(R.id.dfRate);
        mDescription = (TextView)findViewById(R.id.productDescription);

        mBtnWishlist.setOnClickListener(this);
        mBtnMB.setOnClickListener(this);
        mBtnMinus.setOnClickListener(this);
        mBtnPlus.setOnClickListener(this);
        mBtnAddToCart.setOnClickListener(this);
        mBtnRemove.setOnClickListener(this);
        mBtnCart.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        this.mImageLoader = new ImageLoader(mContext, R.drawable.try_product_blank);

        initData(getIntent());
    }

    private void initData(Intent _intent)
    {
        if(_intent != null) {
            MODE = AppConstants.ApplicationLayoutModeAction.valueOf(getIntent().getExtras().getString("MODE"));
            mProdCode = getIntent().getExtras().getString("P_CODE");
            mPos = getIntent().getExtras().getInt("POS");
        }
        else {
            MODE = NONE;
            mProdCode = "0";
            mPos = -1;
        }

        try
        {
            JSONArray jArr = new JSONArray();
            JSONObject jObj = new JSONObject();

            jObj.put("CUSTID", LoggedUser.customer.getCustId());
            jObj.put("P_CODE", mProdCode);
            jArr.put(jObj);
            callApi(Apis.SINGLE_PRODUCT_URL, jArr, Apis.SINGLE_PRODUCT_CODE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        System.out.println(_jArr.toString());
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
            {
                boolean status = true;
                mPos=1;
                Bundle bn = new Bundle();
                bn.putBoolean("status", status);
                bn.putInt("pos", mPos);
                Intent intent = new Intent(SingleProductActivity.this,ProductCommonActivity.class).putExtras(bn);
                startActivity(intent);
                //setResult(0,intent);

                finish();//finishing activity
            }
            break;
            default:
                System.out.println("Default block clicked on option menu click");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestCompleted(String response, int requestCode)
    {
        try {
            if (!response.equals("[]") && requestCode != 0)
            {
                JSONArray jArray = new JSONArray(response.trim());
                switch (requestCode)
                {
                    case Apis.SINGLE_PRODUCT_CODE:
                    {
                        if (jArray.getJSONObject(0).getBoolean("STATUS"))
                        {
                            JSONArray jArr = jArray.getJSONObject(0).getJSONArray("DETAILS");
                            mImageLoader.DisplayImage(Apis.URL + "assets/products/" + mProdCode + ".jpg", mProdImg);
                            mProdImg.setTag(jArr.getJSONObject(0).getString("BR_CODE"));
                            mProdName.setText(jArr.getJSONObject(0).getString("P_NAME"));
                            mDescription.setText(jArr.getJSONObject(0).getString("WEB_KEYWORD"));

                            mDfRate.setText(mContext.getResources().getString(R.string.rupees) + " " + jArr.getJSONObject(0).getString("DF_SALE_RATE"));
                            mDfRate.setTag(jArr.getJSONObject(0).getString("DF_SALE_RATE"));
                            mDfRate.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_light));

                            mMrp.setTag(jArr.getJSONObject(0).getString("MRP"));
                            if (Float.valueOf(jArr.getJSONObject(0).getString("DF_SALE_RATE")) < Float.valueOf(jArr.getJSONObject(0).getString("MRP"))) {
                                mMrp.setText(String.valueOf(mContext.getResources().getString(R.string.rupees) + " " + jArr.getJSONObject(0).getString("MRP")));
                                mMrp.setPaintFlags(mMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            }
                            else
                                mMrp.setVisibility(View.GONE);

                            //for wishlist
                            if (jArr.getJSONObject(0).getString("WISHLIST").equals("1")) {
                                mBtnWishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_filled_wishlist));//(mContext.getResources().getDrawable(R.drawable.ic_filled_wishlist));
                                mBtnWishlist.setTag(true);
                            }
                            else {
                                mBtnWishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_wishlist));
                                mBtnWishlist.setTag(false);
                            }

                            //for MonthlyBasket
                            if (jArr.getJSONObject(0).getString("MB").equals("1"))
                            {
                                mBtnMB.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_filled_shopping_basket));//(mContext.getResources().getDrawable(R.drawable.ic_filled_wishlist));
                                mBtnMB.setTag(true);
                            }
                            else {
                                mBtnMB.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_shopping_basket));
                                mBtnMB.setTag(false);
                            }

                            // get selected qty of a p_code from sqlite database
                            SQLiteAdapter db = new SQLiteAdapter(mContext);
                            db.open();
                            String _selQty = db.isItemExist(Integer.valueOf(jArr.getJSONObject(0).getString("P_CODE")));
                            db.close();

                            if(Float.valueOf(jArr.getJSONObject(0).getString("BAL_QTY")) > 0) {
                                ((LinearLayout) findViewById(R.id.inStockProd)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.outOfStockProd)).setVisibility(View.GONE);
                                System.out.println("SELECTED QTY : " + _selQty);
                                if(Integer.valueOf(_selQty) != 0) {
                                    mSelQty.setText(_selQty);
                                    mSelQty.setTag(jArr.getJSONObject(0).getString("BAL_QTY"));
                                    withCartVisi();
                                }
                                else
                                {
                                    mSelQty.setTag(jArr.getJSONObject(0).getString("BAL_QTY"));
                                    withOutCatVisi();
                                }
                            }
                            else
                            {
                                ((LinearLayout) findViewById(R.id.outOfStockProd)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.inStockProd)).setVisibility(View.GONE);
                            }
                        }
                        else
                            Snackbar.make(mSelQty, "PRODUCT DETAILS NOT FETCHED. TRY AGAIN !!!", Snackbar.LENGTH_SHORT).show();

                    }
                    break;
                    case Apis.ADD_REMOVE_PREF_LIST_CODE:
                    {
                        if (!jArray.getJSONObject(0).has("ERROR")) {
                            if (jArray.getJSONObject(0).getBoolean("STATUS")) {
                                System.out.println("SELECTED PREF LIST MODE : " + mPrefListMode.name());

                                switch(mPrefListMode)
                                {
                                    case WISHLIST: {
                                        if (!Boolean.valueOf(mBtnWishlist.getTag().toString())) {
                                            //mStatus = true;
                                            mBtnWishlist.setTag(true);
                                            mBtnWishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_filled_wishlist));
                                            Snackbar.make(mBtnWishlist, "PRODUCT SUCCESSFULLY ADDED TO WISHLIST", Snackbar.LENGTH_SHORT).show();
                                        } else {
                                            //mStatus = false;
                                            mBtnWishlist.setTag(false);
                                            mBtnWishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_wishlist));
                                            Snackbar.make(mBtnWishlist, "PRODUCT SUCCESSFULLY REMOVED", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                    break;
                                    case MONTHLY_BASKET:{
                                        if (!Boolean.valueOf(mBtnMB.getTag().toString())) {
                                            //mStatus = true;
                                            mBtnMB.setTag(true);
                                            mBtnMB.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_filled_shopping_basket));
                                            Snackbar.make(mBtnMB, "PRODUCT SUCCESSFULLY ADDED TO MONTHLY BASKET", Snackbar.LENGTH_SHORT).show();
                                        } else {
                                            //mStatus = false;
                                            mBtnMB.setTag(false);
                                            mBtnMB.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_shopping_basket));
                                            Snackbar.make(mBtnMB, "PRODUCT SUCCESSFULLY REMOVED", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                            else
                                Snackbar.make(mBtnMB, "NO PRODUCTS FETCHED. TRY AGAIN !!!", Snackbar.LENGTH_SHORT).show();
                        }
                        else
                            Snackbar.make(mBtnMB, jArray.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(Exception e, int requestCode)
    {
        Utils.showToast(this,"Please check your Internet Connection");
    }

    @Override
    public void onRequestStarted(int requestCode)
    {

    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.productCancel:
            {
                boolean status = true;
                switch (MODE)
                {
                    case HOME:
                        Intent intentHome = new Intent(SingleProductActivity.this, HomeActivity.class);
                        startActivity(intentHome);
                }


                if (MODE == WISHLIST)
                    status = Boolean.valueOf(mBtnWishlist.getTag().toString());
                else if (MODE == MONTHLY_BASKET)
                    status = Boolean.valueOf(mBtnMB.getTag().toString());

                Bundle bn = new Bundle();
                bn.putBoolean("status", status);
                bn.putInt("pos", mPos);
                Intent intent = new Intent().putExtras(bn);
                setResult(0,intent);
                finish();//finishing activity
            }
            break;
            case R.id.btnWishlist:
            {
                mPrefListMode = AppConstants.ApplicationLayoutModeAction.WISHLIST;
                JSONArray jArr = new JSONArray();
                JSONObject jObj = new JSONObject();
                try
                {
                    if (Boolean.valueOf(mBtnWishlist.getTag().toString()))
                        jObj.put("TYPE", "REMOVE");
                    else
                        jObj.put("TYPE", "ADD");
                    jObj.put("CUSTID", LoggedUser.customer.getCustId());
                    jObj.put("PREF_LIST_ID", 0);
                    jObj.put("P_CODE", mProdCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jArr.put(jObj);
                callApi(Apis.ADD_REMOVE_PREF_LIST_URL, jArr, Apis.ADD_REMOVE_PREF_LIST_CODE);
            }
            break;
            case R.id.btnMonthlyBasket:
            {
                mPrefListMode = AppConstants.ApplicationLayoutModeAction.MONTHLY_BASKET;
                JSONArray jArr = new JSONArray();
                JSONObject jObj = new JSONObject();
                try
                {
                    if (Boolean.valueOf(mBtnMB.getTag().toString()))
                        jObj.put("TYPE", "REMOVE");
                    else
                        jObj.put("TYPE", "ADD");
                    jObj.put("CUSTID", LoggedUser.customer.getCustId());
                    jObj.put("PREF_LIST_ID", 1);
                    jObj.put("P_CODE", mProdCode);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                jArr.put(jObj);
                callApi(Apis.ADD_REMOVE_PREF_LIST_URL, jArr, Apis.ADD_REMOVE_PREF_LIST_CODE);
            }
            break;
            case R.id.btnMinus:
            {
                int selQty = Integer.valueOf(mSelQty.getText().toString());
                if (selQty > 1)
                {
                    selQty -= 1;
                    mSelQty.setText(String.valueOf(selQty));
                    itemChangeInCart();
                }
                else
                    Snackbar.make(mSelQty, "Can't reduce more", Snackbar.LENGTH_LONG).show();
            }
            break;
            case R.id.btnPlus:
            {
                int selQty = Integer.valueOf(mSelQty.getText().toString());
                if (selQty < Integer.valueOf(mSelQty.getTag().toString())) {
                    selQty += 1;
                    mSelQty.setText(String.valueOf(selQty));
                    itemChangeInCart();
                } else
                    Snackbar.make(mSelQty, "Can't exceed Maximun Balance Qty. (" + mSelQty.getTag().toString() + ")", Snackbar.LENGTH_LONG).show();
            }
            break;
            case R.id.btnAddToCart:
            {
                if(mSelQty.getText().toString().equals(""))
                    Snackbar.make(mSelQty, "Empty Quantity can't be added to Cart. ", Snackbar.LENGTH_LONG).show();
                else if(Integer.valueOf(mSelQty.getText().toString()) == 0)
                    Snackbar.make(mSelQty, "Zero Quantity can't be added to Cart. ", Snackbar.LENGTH_LONG).show();
                else if (Integer.valueOf(mSelQty.getText().toString()) > Integer.valueOf(mSelQty.getTag().toString())) {
                    mSelQty.setText("1");
                    Snackbar.make(mSelQty, "Can't exceed Maximun Balance Qty. " + mSelQty.getTag().toString(), Snackbar.LENGTH_LONG).show();
                } else {
                    itemChangeInCart();
                    withCartVisi();
                }
            }
            break;
            case R.id.fab:
            {
                Bundle bn = new Bundle();
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.CART.name());
                Intent intentCart = new Intent(SingleProductActivity.this,CartActivity.class).putExtras(bn);
                startActivity(intentCart);
            }
            break;
            case R.id.btnRemove:
            {
                deleteItemFromCart();
            }
            break;
        }
    }

    private void itemChangeInCart() {
        ProductCommonModel _prodModel = new ProductCommonModel();
        _prodModel.setProductBrCode(mProdImg.getTag().toString());
        _prodModel.setProductCode(mProdCode);
        _prodModel.setProductName(mProdName.getText().toString());
        _prodModel.setProductMRP(mMrp.getTag().toString());
        _prodModel.setProductDFRate(mDfRate.getTag().toString());
        _prodModel.setProductBalQty(mSelQty.getTag().toString());
        _prodModel.setSelectedQty(String.valueOf(mSelQty.getText().toString()));

        // save to database
        SQLiteAdapter db = new SQLiteAdapter(mContext);
        if (Integer.valueOf(db.isItemExist(Integer.valueOf(mProdCode))) > 0) {
            if (db.updateIndex(_prodModel)) {
                Snackbar.make(mSelQty, "PRODUCT SUCCESSFULLY UPDATED IN CART. ", Snackbar.LENGTH_LONG).show();
            } else
                Snackbar.make(mSelQty, "PRODUCT FAILED TO BE UPDATED IN CART. ", Snackbar.LENGTH_LONG).show();
        } else {
            if (db.saveCart(_prodModel)) {
                Snackbar.make(mSelQty, "PRODUCT ADDED TO CART SUCCESSFULLY. ", Snackbar.LENGTH_LONG).show();
            } else
                Snackbar.make(mSelQty, "PRODUCT FAILED TO BE ADDED IN CART. ", Snackbar.LENGTH_LONG).show();
        }
        db.close();
    }

    private void deleteItemFromCart()
    {
        // remove from database
        SQLiteAdapter db = new SQLiteAdapter(mContext);
        db.open();
        if (db.deleteCartRow(mProdCode) > 0) {
            db.close();
            Snackbar.make(mSelQty, "PRODUCT REMOVED FROM CART SUCCESSFULLY. ", Snackbar.LENGTH_LONG).show();
            withOutCatVisi();
        }
        else
            Snackbar.make(mSelQty, "FAILED TO REMOVE THE PRODUCT FROM CART. ", Snackbar.LENGTH_LONG).show();
    }

    private void withCartVisi()
    {
        mSelQty.setEnabled(false);
        mBtnMinus.setVisibility(View.VISIBLE);
        mBtnPlus.setVisibility(View.VISIBLE);
        mBtnAddToCart.setVisibility(View.GONE);
        mBtnRemove.setVisibility(View.VISIBLE);
    }

    private void withOutCatVisi()
    {
        mSelQty.setText("1");
        mSelQty.setEnabled(true);
        mBtnMinus.setVisibility(View.GONE);
        mBtnPlus.setVisibility(View.GONE);
        mBtnAddToCart.setVisibility(View.VISIBLE);
        mBtnRemove.setVisibility(View.GONE);
    }
}
