package com.pongal.seinfeld;

import android.content.Context;
import android.util.TypedValue;

public class Util {

    public static int getInDIP(int pixels, Context context) {
	return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) pixels, context.getResources()
		.getDisplayMetrics());
    }

}
