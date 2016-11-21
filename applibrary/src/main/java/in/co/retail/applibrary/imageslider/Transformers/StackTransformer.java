package in.co.retail.applibrary.imageslider.Transformers;

/**
 * Created by Niha on 11/5/2015.
 */

import android.view.View;

import com.nineoldandroids.view.ViewHelper;

public class StackTransformer extends BaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
		ViewHelper.setTranslationX(view,position < 0 ? 0f : -view.getWidth() * position);
	}

}
