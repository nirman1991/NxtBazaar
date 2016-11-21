package in.co.retail.nxtbazaar.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import in.co.retail.nxtbazaar.DBAdapter.SQLiteAdapter;
import in.co.retail.nxtbazaar.Model.LoggedUser;
import in.co.retail.nxtbazaar.R;

public class SplashScreen extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new IntentLauncher().start();
    }

    private class IntentLauncher extends Thread {
        @Override
        /**
         *
         *
         * Sleep for some time and than start new activity.
         */
        public void run()
        {
            try
            {
                // Sleeping
                Thread.sleep(3 * 1000);
                checkUser();
                if (LoggedUser.customer == null)
                {
                    Intent intentAuthorize = new Intent(SplashScreen.this, AuthorizeActivity.class);
                    startActivity(intentAuthorize);
                    finish();
                }
                else
                {
                    Intent intentHome = new Intent(SplashScreen.this, HomeActivity.class);
                    startActivity(intentHome);
                    finish();
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception : " + e.getMessage());
            }
        }
    }

    private void checkUser()
    {
        try
        {
            SQLiteAdapter obj = new SQLiteAdapter(this);
            obj.open();
            LoggedUser.customer = obj.user();
            obj.close();
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }
}
