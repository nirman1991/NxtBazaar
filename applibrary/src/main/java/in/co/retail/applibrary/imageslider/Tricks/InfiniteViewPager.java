package in.co.retail.applibrary.imageslider.Tricks;

/**
 * Created by Niha on 11/24/2015.
 */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * A {@link ViewPager} that allows pseudo-infinite paging with a wrap-around effect. Should be used with an {@link
 * in.co.retail.applibrary.imageslider.Adapters.InfinitePagerAdapter}.
 */

public class InfiniteViewPager extends ViewPagerEx
{
    public InfiniteViewPager(Context context) {
        super(context);
    }

    public InfiniteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
    }
}
