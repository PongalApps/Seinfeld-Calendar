package com.pongal.seinfeld;

import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.homescreen.HomeScreenWidgetProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.TypedValue;

public class Util {

    public static int getInDIP(int pixels, Context context) {
	return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) pixels, context.getResources()
		.getDisplayMetrics());
    }

    public static Intent getBroadcastRefresh(Task t) {
	Intent intent = new Intent(HomeScreenWidgetProvider.ACTION_REFRESH);
	intent.setAction(HomeScreenWidgetProvider.ACTION_REFRESH);
	intent.putExtra(HomeScreenWidgetProvider.TASK_ID, t.getId());
	intent.putExtra(HomeScreenWidgetProvider.TASK_NAME, t.getText());
	intent.putExtra(HomeScreenWidgetProvider.TASK_MARKED, t.isTodayAccomplished());
	Uri data = Uri.withAppendedPath(Uri.parse(HomeScreenWidgetProvider.URI_SCHEME + "://widget/id/"), "");
	intent.setData(data);
	return intent;
    }

}
