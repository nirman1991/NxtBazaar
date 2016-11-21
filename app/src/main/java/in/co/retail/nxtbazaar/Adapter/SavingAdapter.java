package in.co.retail.nxtbazaar.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import in.co.retail.applibrary.horizontallistview.HorizontalListView;
import in.co.retail.nxtbazaar.R;

/**
 * Created by Niha on 1/18/16.
 */
public class SavingAdapter extends RecyclerView.Adapter<SavingAdapter.SavingViewHolder>
{
    private LayoutInflater inflater;
    private Context mContext;
    private JSONArray jDetail;
    private int flag = 0;

    public SavingAdapter(Context context, JSONArray jArr)
    {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.jDetail = jArr;
    }

    @Override
    public SavingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.saving_list_column, parent, false);
        SavingViewHolder holder = new SavingViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SavingViewHolder holder, final int position)
    {
        TableRow tbl_row;
        final TextView ord_id = new TextView(mContext);
        TextView ord_total;
        TextView ord_discount;

        try
        {
            holder.txtMonth.setText(jDetail.getJSONObject(position).getString("MONTH"));
            holder.txtMonth.setGravity(Gravity.CENTER_VERTICAL);
            holder.txtSaving.setText("SAVINGS : " + mContext.getResources().getString(R.string.rupees) + jDetail.getJSONObject(position).getString("TOTAL_DISCOUNT"));
            holder.txtSaving.setGravity(Gravity.CENTER_VERTICAL);

            JSONObject jObj = jDetail.getJSONObject(position).getJSONObject("ORDER_DETAILS");
            for(int i=0; i < jObj.length(); i++)
            {
                //JSONObject jObj = jDetail.getJSONObject(position).getJSONArray("ORDER_DETAILS").getJSONObject(i);
                tbl_row = new TableRow(mContext);
                tbl_row.setTag(i);
                tbl_row.setGravity(Gravity.CENTER_HORIZONTAL);
                tbl_row.setBackgroundColor(mContext.getResources().getColor(R.color.lightColorLogoGreen));
               // tbl_row.setLayoutParams(tlp);

                ord_id.setText(jObj.getString("ORDER_ID"));
                ord_id.setTypeface(null, Typeface.BOLD);
                //ord_id.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                //ord_id.setPaintFlags(ord_id.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                ord_id.setGravity(Gravity.CENTER_HORIZONTAL);

                ord_total = new TextView(mContext);
                ord_total.setText(mContext.getResources().getString(R.string.rupees) + jObj.getString("ORDER_TOTAL"));
                ord_total.setTypeface(null, Typeface.BOLD);
                ord_total.setGravity(Gravity.CENTER_HORIZONTAL);

                ord_discount = new TextView(mContext);
                ord_discount.setText(mContext.getResources().getString(R.string.rupees) + jObj.getString("DISCOUNT"));
                ord_discount.setTypeface(null, Typeface.BOLD);
                ord_discount.setGravity(Gravity.CENTER_HORIZONTAL);

                // set layout params for table row
                TableLayout.LayoutParams tlp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                tlp.setMargins(8, 0, 8, 5);

                // add view to table
                holder.odrDetails.addView(tbl_row, tlp);

                //add view
                tbl_row.addView(ord_id);
                tbl_row.addView(ord_total);
                tbl_row.addView(ord_discount);
            }

            if(jDetail.getJSONObject(position).getJSONArray("ORDER_DETAILS").length() > 0)
                holder.btnOrdDetails.setEnabled(true);
            else
                holder.btnOrdDetails.setEnabled(false);

            ord_id.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    TableRow tbl_row_1;
                    TableRow tabl_row = new TableRow(mContext);
                    TextView p_name, p_name_txt;
                    TextView qty, qty_txt;
                    TextView df_rate, df_rate_txt;

                    if (flag == 0)
                    {
                        System.out.println("IF");
                        // set layout params for table row
                        TableLayout.LayoutParams tlp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                        tlp.setMargins(8, 5, 8, 5);


                        tabl_row.setId(R.id.tbl_row);
                        tabl_row.setBackgroundColor(mContext.getResources().getColor(R.color.colorLogoGreen));
                        tabl_row.setLayoutParams(tlp);

                        p_name = new TextView(mContext);
                        p_name.setText("NAME");
                        p_name.setTypeface(null, Typeface.BOLD);

                        qty = new TextView(mContext);
                        qty.setText("QUANTITY");
                        qty.setTypeface(null, Typeface.BOLD);
                        qty.setGravity(Gravity.CENTER_HORIZONTAL);

                        df_rate = new TextView(mContext);
                        df_rate.setText("PRICE/QTY");
                        df_rate.setTypeface(null, Typeface.BOLD);
                        df_rate.setGravity(Gravity.CENTER_HORIZONTAL);

                        //add view
                        tabl_row.addView(p_name);
                        tabl_row.addView(df_rate);
                        tabl_row.addView(qty);
                        tabl_row.setPadding(20, 0, 0, 0);

                        // add view to table
                        holder.odrDetails.addView(tabl_row, tlp);

                        try
                        {
                            for (int i = 0; i < jDetail.getJSONObject(position).getJSONArray("PRODUCT_DETAILS").length(); i++)
                            {
                                JSONObject jObj = jDetail.getJSONObject(position).getJSONArray("PRODUCT_DETAILS").getJSONObject(i);
                                //new dynamic row
                                tbl_row_1 = new TableRow(mContext);
                                tbl_row_1.setId(R.id.tbl_row_1);
                                tbl_row_1.setBackgroundColor(mContext.getResources().getColor(R.color.lightColorLogoGreen));

                                p_name_txt = new TextView(mContext);
                                p_name_txt.setText(jObj.getString("P_NAME"));
                                p_name_txt.setTypeface(null, Typeface.BOLD);

                                qty_txt = new TextView(mContext);
                                qty_txt.setText(jObj.getString("QTY"));
                                qty_txt.setTypeface(null, Typeface.BOLD);
                                qty_txt.setGravity(Gravity.CENTER_HORIZONTAL);

                                df_rate_txt = new TextView(mContext);
                                df_rate_txt.setText(mContext.getResources().getString(R.string.rupees) + jObj.getString("PROD_RATE"));
                                df_rate_txt.setTypeface(null, Typeface.BOLD);
                                df_rate_txt.setGravity(Gravity.CENTER_HORIZONTAL);

                                //add view
                                tbl_row_1.addView(p_name_txt);
                                tbl_row_1.addView(df_rate_txt);
                                tbl_row_1.addView(qty_txt);
                                tbl_row_1.setPadding(20, 0, 0, 0);

                                // set layout params for table row
                                tbl_row_1.setLayoutParams(tlp);

                                // add view to table
                                holder.odrDetails.addView(tbl_row_1, tlp);
                                flag = 1;
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if(flag == 1) {
                        System.out.println("ELSE IF");
                        // holder.odrDetails.removeAllViews();
                        tabl_row.removeAllViews();
                        flag = 0;
                    }
                    else {System.out.print("ELSE");}
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        holder.btnOrdDetails.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(holder.linLayout.getVisibility() == View.GONE) {
                    holder.btnOrdDetails.setText("HIDE");
                    holder.linLayout.setVisibility(View.VISIBLE);
                }
                else {
                    holder.btnOrdDetails.setText("DETAILS");
                    holder.linLayout.setVisibility(View.GONE);
                }

            }
        });
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return jDetail.length();
    }

    class SavingViewHolder extends RecyclerView.ViewHolder
    {
        TextView txtMonth, txtSaving;
        Button btnOrdDetails;
        TableLayout odrDetails;
        LinearLayout linLayout;

        public SavingViewHolder(View itemView)
        {
            super(itemView);
            txtMonth = (TextView) itemView.findViewById(R.id.txtMonth);
            txtSaving = (TextView) itemView.findViewById(R.id.txtSaving);
            btnOrdDetails = (Button) itemView.findViewById(R.id.btnOrdDetails);
            odrDetails = (TableLayout) itemView.findViewById(R.id.orderDetails);
            linLayout = (LinearLayout) itemView.findViewById(R.id.linLayout);
        }
    }
}
