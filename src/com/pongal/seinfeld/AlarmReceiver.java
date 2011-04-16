package com.pongal.seinfeld;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    // private int SIMPLE_NOTFICATION_ID = 1;
    public static final String URI_SCHEME = "seinfeldcal";

    @Override
    public void onReceive(Context context, Intent intent) {
	Log.d(null, "Received alarm intent");
	
	final Bundle bundle = intent.getExtras();
	final int taskId = bundle.getInt("taskId");
	final String taskName = bundle.getString("taskName");
	Log.d("seinfeld", "[AlarmReceiver.onReceive] TaskInfo: " + taskId + "..." + taskName);

	Intent notifyingIntent = new Intent(context, CalendarActivity.class);
	Uri data = Uri.withAppendedPath(Uri.parse(URI_SCHEME + "://notification/"), String.valueOf(taskId));
	notifyingIntent.setData(data);
	notifyingIntent.putExtra("taskId", taskId);
	PendingIntent myIntent = PendingIntent.getActivity(context, 0, notifyingIntent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);

	Notification notification = new Notification(R.drawable.icon, taskName, System.currentTimeMillis() + 1000);
	notification.setLatestEventInfo(context, taskName, "Click to open the application", myIntent);
	notification.flags |= Notification.FLAG_AUTO_CANCEL;
	// notification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

	NotificationManager notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
	notificationManager.notify(taskId, notification);

	Log.i(getClass().getSimpleName(), "Sucessfully Changed Time");
    }
}
