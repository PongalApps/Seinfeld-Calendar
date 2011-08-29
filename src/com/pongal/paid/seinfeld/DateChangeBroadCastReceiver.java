package com.pongal.paid.seinfeld;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import com.pongal.paid.seinfeld.data.Constants;
import com.pongal.paid.seinfeld.homescreen.HomeScreenWidgetProvider;
import com.pongal.paid.seinfeld.homescreen.WidgetConfiguration;

public class DateChangeBroadCastReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
	Log.d(Constants.LogTag, "DateChangeBroadCastReceiver::onReceive(" + intent.getAction() +")");
	
	final ComponentName componentName = new ComponentName(context, HomeScreenWidgetProvider.class);
	final int[] widgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
	// Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	Intent updateIntent = new Intent(HomeScreenWidgetProvider.ACTION_DATE_CHANGED);
	updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
	//initializeAlarm(context);
	context.sendBroadcast(updateIntent);
    }
    
    private void initializeAlarm(Context context)
    {
	final ComponentName provider = new ComponentName(context, WidgetConfiguration.class);
	final int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(provider);
		
	Intent intent = new Intent(context, HomeScreenWidgetProvider.class);
	intent.setAction(HomeScreenWidgetProvider.ACTION_UPDATE_DATE);
	intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
	PendingIntent datePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

	AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	alarms.cancel(datePendingIntent);
	alarms.setRepeating(AlarmManager.RTC, getTimeForMidnight(), AlarmManager.INTERVAL_DAY, datePendingIntent);
    }
    
    private long getTimeForMidnight()
    {
	Calendar midnight = Calendar.getInstance();
	midnight.add(Calendar.DATE, 1);
	midnight.set(Calendar.HOUR_OF_DAY, 0);
	midnight.set(Calendar.MINUTE, 0);
	midnight.set(Calendar.SECOND, 0);
	midnight.set(Calendar.MILLISECOND, 0);
	Log.d(Constants.LogTag, "Setting for midnight: " + DateFormat.format("dd-MM-yyyy hh:mm:ss a", midnight.getTimeInMillis()));
	return midnight.getTimeInMillis();
    }
}
