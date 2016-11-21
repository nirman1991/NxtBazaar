package in.co.retail.nxtbazaar.DBAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import in.co.retail.nxtbazaar.Model.CartModel;
import in.co.retail.nxtbazaar.Model.CustomerDetailsModel;
import in.co.retail.nxtbazaar.Model.MonthlyBasketModel;
import in.co.retail.nxtbazaar.Model.ProductCommonModel;

/**
 * Created by Niha on 12/5/15.
 */
public class SQLiteAdapter implements Serializable
{
    private static final int DATABASE_VERSION = 1;
    private static Context mContext;
    private DatabaseHelper dbHelper = null;

    private static SQLiteDatabase db = null;

    private static final String DB_NAME = "NxtBazzarDB";
    private static final String DB_TABLE_USER = "TbUser";
    private static final String DB_TABLE_CART = "TbCart";
    //private static final String DB_TABLE_MONTHLY_BASKET = "TbMonthlyBasket";

    private DecimalFormat deciForm = new DecimalFormat("#.##");

    private static final String CREATE_TABLE_USER = "CREATE TABLE "
            + DB_TABLE_USER
            + "(CustId TEXT, CustTitle TEXT, EmailId TEXT, AltId TEXT, "
            + "CustName1 TEXT, Address1 TEXT, Area1 TEXT, City1 TEXT, PinCode1 TEXT, LandMark1 TEXT, MobNo1 TEXT, "
            + "CustName2 TEXT, Address2 TEXT, Area2 TEXT, City2 TEXT, PinCode2 TEXT, LandMark2 TEXT, MobNo2 TEXT)";
            /*+ "Address3 TEXT, Area3 TEXT, City3 TEXT, PinCode3 TEXT, LandMark3 TEXT, MobNo3 TEXT)";/**/

    private static final String CREATE_TABLE_CART = "CREATE TABLE "
            + DB_TABLE_CART
            + "(BrCode TEXT, ProdCode TEXT, ProdName TEXT, DfSaleRate TEXT, MRP TEXT, BalQty TEXT, SelQty TEXT)";

    /*private static final String CREATE_TABLE_MONTHLY_BASKET = "CREATE TABLE "
            + DB_TABLE_MONTHLY_BASKET
            + "(MBId TEXT, MBName TEXT)";/**/

    public SQLiteAdapter(Context ctx)
    {
        SQLiteAdapter.mContext = ctx;
        dbHelper = new DatabaseHelper(ctx);
        open();
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {

        public DatabaseHelper(Context context)
        {
            super(context, DB_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try
            {
                System.out.println(CREATE_TABLE_USER);
                System.out.println(CREATE_TABLE_CART);
                //System.out.println(CREATE_TABLE_MONTHLY_BASKET);
                db.execSQL(CREATE_TABLE_USER);
                db.execSQL(CREATE_TABLE_CART);
                //db.execSQL(CREATE_TABLE_MONTHLY_BASKET);
                System.out.println("Created Tables");
            }
            catch (Exception e)
            {
                Toast.makeText(mContext, "Error creating table", Toast.LENGTH_LONG).show();
                e.getStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            //db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CARTS);
            db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CART);
            //db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_MONTHLY_BASKET);
            onCreate(db);
        }
    }

    // ---opens the database---
    public SQLiteAdapter open() throws SQLException
    {
        if (db == null || !db.isOpen())
        {
            db = dbHelper.getWritableDatabase();
        }
        return this;
    }

    // ---closes the database---
    public void close()
    {
        if (db == null && db.isOpen())
        {
            dbHelper.close();
        }
    }


    // -- for user details -- //
    public boolean saveUser(CustomerDetailsModel model)
    {
        try
        {
            ContentValues insertValues = new ContentValues();
            insertValues.put("CustId", model.getCustId());
            insertValues.put("CustTitle", model.getCustTitle());
            insertValues.put("EmailId", model.getEmailId());
            insertValues.put("AltId", model.getAltId());
            insertValues.put("CustName1", model.getCustName1());
            insertValues.put("Address1", model.getAddress1());
            insertValues.put("Area1", model.getArea1());
            insertValues.put("City1", model.getCity1());
            insertValues.put("PinCode1", model.getPinCode1());
            insertValues.put("LandMark1", model.getLandMark1());
            insertValues.put("MobNo1", model.getMobileNo1());
            insertValues.put("CustName2", model.getCustName2());
            insertValues.put("Address2", model.getAddress2());
            insertValues.put("Area2", model.getArea2());
            insertValues.put("City2", model.getCity2());
            insertValues.put("PinCode2", model.getPinCode2());
            insertValues.put("LandMark2", model.getLandMark2());
            insertValues.put("MobNo2", model.getMobileNo2());
            db.insert(DB_TABLE_USER, null, insertValues);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public CustomerDetailsModel user()
    {
        CustomerDetailsModel model = null;
        try
        {
            Cursor cursor = db.query(DB_TABLE_USER, new String[] {}, null, null, null, null, null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                int count = cursor.getCount();
                if (count > 0)
                {
                    model = new CustomerDetailsModel();
                    model.setCustId(cursor.getString(cursor.getColumnIndex("CustId")));
                    model.setCustTitle(cursor.getString(cursor.getColumnIndex("CustTitle")));
                    model.setEmailId(cursor.getString(cursor.getColumnIndex("EmailId")));
                    model.setAltId(cursor.getString(cursor.getColumnIndex("AltId")));
                    model.setCustName1(cursor.getString(cursor.getColumnIndex("CustName1")));
                    model.setAddress1(cursor.getString(cursor.getColumnIndex("Address1")));
                    model.setArea1(cursor.getString(cursor.getColumnIndex("Area1")));
                    model.setCity1(cursor.getString(cursor.getColumnIndex("City1")));
                    model.setPinCode1(cursor.getString(cursor.getColumnIndex("PinCode1")));
                    model.setLandMark1(cursor.getString(cursor.getColumnIndex("LandMark1")));
                    model.setMobileNo1(cursor.getString(cursor.getColumnIndex("MobNo1")));
                    model.setCustName2(cursor.getString(cursor.getColumnIndex("CustName2")));
                    model.setAddress2(cursor.getString(cursor.getColumnIndex("Address2")));
                    model.setArea2(cursor.getString(cursor.getColumnIndex("Area2")));
                    model.setCity2(cursor.getString(cursor.getColumnIndex("City2")));
                    model.setPinCode2(cursor.getString(cursor.getColumnIndex("PinCode2")));
                    model.setLandMark2(cursor.getString(cursor.getColumnIndex("LandMark2")));
                    model.setMobileNo2(cursor.getString(cursor.getColumnIndex("MobNo2")));
                }
            }
            cursor.close();
        }

        catch (Exception e)
        {
            // TODO: handle exception
        }
        return model;
    }

    public boolean updatePrimaryAdd(JSONArray jArr)
    {
        String where = null;
        String[] whereArgs = null;
        ContentValues updateValues = new ContentValues();
        try {
            updateValues.put("CustName1", jArr.getJSONObject(0).getString("NAME"));
            updateValues.put("EmailId", jArr.getJSONObject(0).getString("EMAIL"));
            updateValues.put("Address1", jArr.getJSONObject(0).getString("ADDRESS"));
            updateValues.put("Area1", jArr.getJSONObject(0).getString("AREA"));
            updateValues.put("City1", jArr.getJSONObject(0).getString("CITY"));
            updateValues.put("PinCode1", jArr.getJSONObject(0).getString("PINCODE"));
            updateValues.put("LandMark1", jArr.getJSONObject(0).getString("LANDMARK"));
            where = "CustId=?";
            whereArgs = new String[] {jArr.getJSONObject(0).getString("CUSTID")};
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try
        {
            return db.update(DB_TABLE_USER, updateValues, where, whereArgs) > 0 ? true : false;
        }
        catch (SQLException se)
        {
            se.printStackTrace();
            return false;
        }
    }

    public boolean updateSecondaryAdd(JSONArray jArr, String alt_id)
    {
        String where = null;
        String[] whereArgs = null;
        ContentValues updateValues = new ContentValues();
        try {
            updateValues.put("AltId", alt_id);
            updateValues.put("CustName2", jArr.getJSONObject(0).getString("NAME"));
            updateValues.put("Address2", jArr.getJSONObject(0).getString("ADDRESS"));
            updateValues.put("Area2", jArr.getJSONObject(0).getString("AREA"));
            updateValues.put("City2", jArr.getJSONObject(0).getString("CITY"));
            updateValues.put("PinCode2", jArr.getJSONObject(0).getString("PINCODE"));
            updateValues.put("LandMark2", jArr.getJSONObject(0).getString("LANDMARK"));
            updateValues.put("MobNo2", jArr.getJSONObject(0).getString("MOB_NO"));
            where = "CustId=?";
            whereArgs = new String[] {jArr.getJSONObject(0).getString("CUSTID")};
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try
        {
            return db.update(DB_TABLE_USER, updateValues, where, whereArgs) > 0 ? true : false;
        }
        catch (SQLException se)
        {
            se.printStackTrace();
            return false;
        }
    }
    // -- end -- //

    // -- for app_cart details -- //
    public boolean saveCart(ProductCommonModel model)
    {
        try
        {
            ContentValues insertValues = new ContentValues();
            insertValues.put("BrCode", model.getProductBrCode());
            insertValues.put("ProdCode", model.getProductCode());
            insertValues.put("ProdName", model.getProductName());
            insertValues.put("DfSaleRate", model.getProductDFRate());
            insertValues.put("MRP", model.getProductMRP());
            insertValues.put("BalQty", model.getProductBalQty());
            insertValues.put("SelQty", model.getSelectedQty());
            db.insert(DB_TABLE_CART, null, insertValues);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public boolean updateIndex(ProductCommonModel data) {
        try {
            String WHERE = String.format("%s='%s'", "ProdCode", data.getProductCode());
            ContentValues insertValues = new ContentValues();
            insertValues.put("BrCode", data.getProductBrCode());
            insertValues.put("DfSaleRate", data.getProductDFRate());
            insertValues.put("SelQty", data.getSelectedQty());
            insertValues.put("BalQty", data.getProductBalQty());
            insertValues.put("MRP", data.getProductMRP());
            return db.update(DB_TABLE_CART, insertValues, WHERE, null) > 0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int deleteCartRow(String p_code)
    {
        return db.delete(DB_TABLE_CART, "ProdCode=?", new String[] { "" + p_code });
    }

    public CartModel getCartItem()
    {
        List<ProductCommonModel> productItemsList = new ArrayList<>();
        CartModel cart = null;
        float totalAmount = 0;
        float totalSavings = 0;
        float df_sale_rate;
        try
        {
            Cursor cursor = db.query(DB_TABLE_CART, new String[] {}, null, null, null, null, null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                int count = cursor.getCount();
                if(count>0)
                {
                    cart = new CartModel();
                    for (int i = 0; i < count; i++)
                    {
                        //(BrCode TEXT, ProdCode TEXT, ProdName TEXT, DfSaleRate TEXT, MRP TEXT, BallQty TEXT, SelQty TEXT)
                        ProductCommonModel model = new ProductCommonModel();
                        model.setProductBrCode(cursor.getString(cursor.getColumnIndex("BrCode")));
                        model.setProductCode(cursor.getString(cursor.getColumnIndex("ProdCode")));
                        model.setProductName(cursor.getString(cursor.getColumnIndex("ProdName")));
                        model.setProductDFRate(cursor.getString(cursor.getColumnIndex("DfSaleRate")));
                        model.setProductMRP(cursor.getString(cursor.getColumnIndex("MRP")));
                        model.setProductBalQty(cursor.getString(cursor.getColumnIndex("BalQty")));
                        model.setSelectedQty(cursor.getString(cursor.getColumnIndex("SelQty")));
                        productItemsList.add(model);

                        df_sale_rate = Float.valueOf(cursor.getString(cursor.getColumnIndex("SelQty"))) * Float.valueOf(cursor.getString(cursor.getColumnIndex("DfSaleRate")));
                        totalSavings += (Float.valueOf(cursor.getString(cursor.getColumnIndex("SelQty"))) * Float.valueOf(cursor.getString(cursor.getColumnIndex("MRP")))) - df_sale_rate;

                        totalAmount += df_sale_rate;

                        cursor.moveToNext();
                    }
                    cursor.close();
                    cart.setProductItemList(productItemsList);
                    cart.setmTotal(deciForm.format(totalAmount));
                    cart.setmDiscount(deciForm.format(totalSavings));
                }
                else
                    System.out.println("SQLLITE : NO DATA");
            }

        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        return cart;
    }/**/

    public JSONArray getTotal()
    {
        float totalAmount = 0;
        float totalSavings = 0;
        float df_sale_rate;
        JSONArray jArr = null;

        Cursor cursor = db.query(DB_TABLE_CART, new String[] {}, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int count = cursor.getCount();
            if(count>0)
            {
                for (int i = 0; i < count; i++)
                {
                    df_sale_rate = Float.valueOf(cursor.getString(cursor.getColumnIndex("SelQty"))) * Float.valueOf(cursor.getString(cursor.getColumnIndex("DfSaleRate")));
                    totalSavings += (Float.valueOf(cursor.getString(cursor.getColumnIndex("SelQty"))) * Float.valueOf(cursor.getString(cursor.getColumnIndex("MRP")))) - df_sale_rate;

                    totalAmount += df_sale_rate;

                    cursor.moveToNext();
                }
            }
            cursor.close();
            jArr = new JSONArray();
            JSONObject jObj = new JSONObject();
            try {
                jObj.put("TOTAL_AMOUNT", deciForm.format(totalAmount));
                jObj.put("TOTAL_SAVING", deciForm.format(totalSavings));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jArr.put(jObj);
        }
        return jArr;
    }

    public int emptyCart()
    {
        return db.delete(DB_TABLE_CART, "1", null);
    }

    public int getCartSize()
    {
        Cursor cursor = db.query(DB_TABLE_CART, new String[] {}, null,	null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public String isItemExist(int productCode)
    {
        String selQty = "0";
        boolean flag = false;
        String query = "select SelQty from "+ DB_TABLE_CART +" where ProdCode=" + productCode + "";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            selQty = cursor.getString(cursor.getColumnIndex("SelQty"));
        }
        cursor.close();
        return selQty;
    }
    // -- end -- //

    /*// -- Monthly Basket -- //
    public boolean saveMB(JSONArray jArr)
    {
        try
        {
            boolean status = false;
            for(int i = 0; i < jArr.length(); i++)
            {
                ContentValues insertValues = new ContentValues();
                insertValues.put("MBId", jArr.getJSONObject(i).getString("PREF_LIST_ID"));
                insertValues.put("MBName", jArr.getJSONObject(i).getString("PREF_LIST_NAME"));
                db.insert(DB_TABLE_MONTHLY_BASKET, null, insertValues);
                status = true;
            }
            return status;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /*public boolean updateMB(ProductCommonModel data) {
        try {
            String WHERE = String.format("%s='%s'", "ProdCode", data.getProductCode());
            ContentValues insertValues = new ContentValues();
            insertValues.put("DfSaleRate", data.getProductDFRate());
            insertValues.put("SelQty", data.getSelectedQty());
            insertValues.put("BalQty", data.getProductBalQty());
            insertValues.put("MRP", data.getProductMRP());
            return db.update(DB_TABLE_CART, insertValues, WHERE, null) > 0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }/**/

    /*public JSONArray getMB()
    {
        JSONArray jArr = new JSONArray();
        JSONObject jObj = null;
        try
        {
            Cursor cursor = db.query(DB_TABLE_MONTHLY_BASKET, new String[] {}, null, null, null, null, null);
            if (cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                int count = cursor.getCount();
                if(count>0)
                {
                    for (int i = 0; i < count; i++)
                    {
                        jObj = new JSONObject();
                        jObj.put("PREF_LIST_ID", cursor.getString(cursor.getColumnIndex("MBId")));
                        jObj.put("PREF_LIST_NAME", cursor.getString(cursor.getColumnIndex("MBName")));
                        jArr.put(jObj);

                        cursor.moveToNext();
                    }
                    cursor.close();

                }
                else
                    System.out.println("SQLLITE : NO DATA");
            }

        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        return jArr;
    }

    public int deleteMBRow(String mb_id)
    {
        return db.delete(DB_TABLE_MONTHLY_BASKET, "MBId=?", new String[] { "" + mb_id });
    }

    public int emptyMB()
    {
        return db.delete(DB_TABLE_MONTHLY_BASKET, "1", null);
    }
    // -- end -- ///**/

}
