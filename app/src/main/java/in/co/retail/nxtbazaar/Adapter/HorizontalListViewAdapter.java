package in.co.retail.nxtbazaar.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.co.retail.applibrary.LazyLoading.ImageLoader;
import in.co.retail.nxtbazaar.Model.Apis;
import in.co.retail.nxtbazaar.Model.ProductCommonModel;
import in.co.retail.nxtbazaar.R;
import in.co.retail.nxtbazaar.Utility.Constants;

/**
 * Created by Niha on 11/25/2015.
 */
public class HorizontalListViewAdapter extends ArrayAdapter<ProductCommonModel>
{
    private LayoutInflater mLayoutInflater;
    private ImageLoader imageLoader;
    private Context mContext;
    private int mResId;
    private Listener mListener;

    public HorizontalListViewAdapter(Context _context, int _resourceId, List<ProductCommonModel> _listModel) {
        super(_context, _resourceId, _listModel);
        // TODO Auto-generated constructor stub
        this.mContext = _context;
        this.mResId = _resourceId;
        this.mListener = (Listener) mContext;
        this.imageLoader = new ImageLoader(getContext(), R.drawable.app_product_blank);
        this.mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /* (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent)
    {
        // TODO Auto-generated method stub
        final ProductCommonModel _hlvm = getItem(_position);
        System.out.print("\nPOSITION : " + _position + " ");
        System.out.print("P_NAME : " + _hlvm.getProductName() + " ");
        System.out.print("P_CODE : " + _hlvm.getProductCode() + "\n");
        if (mLayoutInflater != null && _hlvm != null)
        {
            System.out.print("VIEW TO BE RETURN \n");

            // Inflate the view since it does not exist
            _convertView = mLayoutInflater.inflate(mResId, _parent, false);

            TextView lblText = (TextView) _convertView.findViewById(R.id.lblText);
            TextView lblMrp = (TextView) _convertView.findViewById(R.id.lblMrp);
            TextView lblDfSalesRate = (TextView) _convertView.findViewById(R.id.lblDfSalesRate);
            ImageView horizonImgCommon = (ImageView) _convertView.findViewById(R.id.horizonImgCommon);

            // set the controls image
            imageLoader.DisplayImage(Apis.URL + "assets/products/" + _hlvm.getProductCode() + ".jpg", horizonImgCommon);
            System.out.print("DATA "+Apis.URL + "assets/products/" + _hlvm.getProductCode() + ".jpg");

            lblText.setText(_hlvm.getProductName());

            lblDfSalesRate.setText(mContext.getResources().getString(R.string.rupees) + " " + _hlvm.getProductDFRate());
            lblDfSalesRate.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_light));

            if (Float.valueOf(_hlvm.getProductDFRate()) < Float.valueOf(_hlvm.getProductMRP())) {
                lblMrp.setText(String.valueOf(mContext.getResources().getString(R.string.rupees) + " " + _hlvm.getProductMRP()));
                lblMrp.setPaintFlags(lblMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else
                lblMrp.setVisibility(View.GONE);

            //for image onClick event
            horizonImgCommon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onImageClick(_hlvm.getProductCode());
                }
            });

        }
        else
        {
            System.out.print("NULL NOTHING TO RETURN");
        }
        return _convertView;
    }

    public interface Listener
    {
        void onImageClick(String productCode);
    }
}