package in.co.retail.applibrary.imageslider.Transformers;

/**
 * Created by Niha on 11/5/2015.
 */

import android.view.View;

public class DefaultTransformer extends BaseTransformer {

	@Override
	protected void onTransform(View view, float position) {
	}

	@Override
	public boolean isPagingEnabled() {
		return true;
	}

}
