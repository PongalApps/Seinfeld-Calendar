package com.pongal.seinfeld;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static Intent getBroadcast(Task t, String action) {
	Intent intent = new Intent(action);
	intent.putExtra(HomeScreenWidgetProvider.TASK_ID, t.getId());
	intent.putExtra(HomeScreenWidgetProvider.TASK_NAME, t.getText());
	intent.putExtra(HomeScreenWidgetProvider.TASK_MARKED, t.isTodayAccomplished());
	Uri data = Uri.withAppendedPath(Uri.parse(HomeScreenWidgetProvider.URI_SCHEME + "://widget/id/"), "");
	intent.setData(data);
	return intent;
    }

    public static long convertToMilliseconds(int hour24, int mins) {
	long msTime = 0;

	try {
	    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
	    Date date = dateFormat.parse(hour24 +":" + mins);
	    msTime = date.getTime();
	} catch(ParseException pex) {	    
	}

	return msTime;
    }
}
