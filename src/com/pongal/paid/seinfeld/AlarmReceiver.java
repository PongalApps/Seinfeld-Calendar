package com.pongal.paid.seinfeld;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.pongal.paid.seinfeld.data.Constants;
import com.pongal.paid.seinfeld.data.Task;
import com.pongal.paid.seinfeld.db.DBManager;

public class AlarmReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
	Log.d(Constants.LogTag, "AlarmReceiver.onReceive(" + intent.getAction() + ")"); 
	
	final Bundle bundle = intent.getExtras();
	final int taskId = bundle.getInt("taskId");
	final String taskName = bundle.getString("taskName");
	Log.d(Constants.LogTag, "[AlarmReceiver.onReceive] TaskInfo: " + taskId + "..." + taskName);

	Intent notifyingIntent = new Intent(context, CalendarActivity.class);	
	notifyingIntent.setData(getUri(taskId));
	notifyingIntent.putExtra("taskId", taskId);
	PendingIntent myIntent = PendingIntent.getActivity(context, 0, notifyingIntent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);

	Notification notification = new Notification(R.drawable.icon, taskName, System.currentTimeMillis() + 1000);
	notification.setLatestEventInfo(context, taskName, "Click to open the application", myIntent);
	notification.flags |= Notification.FLAG_AUTO_CANCEL;
	// notification.defaults |= Notification.DEFAULT_VIBRATE;

	NotificationManager notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
	notificationManager.notify(taskId, notification);
	Task task = new DBManager(context).getTaskDetails(taskId);
	if(task.isSendReminderSet()){
	    sendSMS(context, task);	    
	}

	Log.i(Constants.LogTag, "Sucessfully Changed Time");
    }
    
    private static Uri getUri(int taskId) {
	// Uri data = Uri.withAppendedPath(Uri.parse(Constants.URI_SCHEME + "://notification/"), String.valueOf(taskId));
	final Uri baseUri = Uri.parse(Constants.URI_SCHEME + "://notification/");
	return Uri.withAppendedPath(baseUri, String.valueOf(taskId));
    }
    
    private void sendSMS(Context context, Task task) {
        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, AlarmReceiver.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(task.getPhoneNumber(), null, task.getReminderText(), pi, null); 
    }

}
