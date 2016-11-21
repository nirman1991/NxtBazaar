package in.co.retail.nxtbazaar.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.co.retail.nxtbazaar.R;

/**
 * Created by Niha on 1/9/2016.
 */
public class ThankYouActivity extends AppCompatActivity implements View.OnClickListener
{
    private static String TAG = ThankYouActivity.class.getSimpleName();

    private Context mContext;
    private ImageView mImgBack;
    private TextView mTxtOrdId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        System.out.println("TAG : " + TAG);
        mContext = this;

        mTxtOrdId = (TextView) findViewById(R.id.txtOrdId);
        mImgBack = (ImageView) findViewById(R.id.imgBack);
        mImgBack.setOnClickListener(this);

        initData(getIntent());
    }

    private void initData(Intent intent) {
        if (intent != null)
        {
            mTxtOrdId.setText("ORDER ID : " + intent.getExtras().getString("OrderId"));
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.imgBack)
        {
            Intent intent = new Intent(ThankYouActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Snackbar.make(mTxtOrdId, "The Order is processed. You cannot go back.", Snackbar.LENGTH_SHORT).show();
            //showPrompt(mContext, R.drawable.green_logout, "ALERT", "Do you want to exit ? ");
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
