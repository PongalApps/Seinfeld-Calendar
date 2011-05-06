package com.pongal.seinfeld;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.TypedValue;

import com.pongal.seinfeld.data.Constants;
import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.homescreen.HomeScreenWidgetProvider;

public class Util {

    public static int getInDIP(int pixels, Context context) {
	return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		(float) pixels,
		context.getResources().getDisplayMetrics());
    }

    public static Intent getBroadcast(Task t, String action) {
	Intent intent = new Intent(action);
	intent.putExtra(HomeScreenWidgetProvider.TASK_ID, t.getId());
	intent.putExtra(HomeScreenWidgetProvider.TASK_NAME, t.getText());
	intent.putExtra(HomeScreenWidgetProvider.TASK_MARKED, t.isTodayAccomplished());
	Uri data = Uri.withAppendedPath(Uri.parse(Constants.URI_SCHEME + "://widget/id/"), "");
	intent.setData(data);
	return intent;
    }

    public static long convertToMilliseconds(int hour24, int mins) {
	final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour24);
        cal.set(Calendar.MINUTE, mins);
        cal.set(Calendar.SECOND, 0);
        final long msTime = cal.getTimeInMillis();

	return msTime;
    }
}
