package com.pongal.seinfeld;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;

public class ReminderTimeService {
    Context context;

    public ReminderTimeService(Context context) {
	this.context = context;
    }

    public void setReminder(Task task) {
	Intent intent = new Intent(context, AlarmReceiver.class);
	intent.putExtra("taskId", task.getId());
	intent.putExtra("taskName", task.getText());
	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

	AlarmManager alarmManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
	
	/*Calendar cal = Calendar.getInstance();
	final int hour = cal.get(Calendar.HOUR_OF_DAY);
	final int mins = cal.get(Calendar.MINUTE);
	final long msTime = cal.getTimeInMillis() + 5000;
	alarmManager.setRepeating(AlarmManager.RTC, msTime + 3000, 60000, pendingIntent);*/

	if (!task.isReminderSet())
	{
	    Log.d("seinfeld", "(Internal Error!) Task does not have a reminder time to set!");
	    return;
	}
	
	final java.util.Date reminderTime = task.getReminderTime();
	final int hour = reminderTime.getHours();
	final int mins = reminderTime.getMinutes();
	final long msTime = Util.convertToMilliseconds(hour, mins);
	
	// cancelReminder(task);
	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, msTime, AlarmManager.INTERVAL_DAY, pendingIntent);
	
	Log.d("seinfeld", "Reminder set. (" + task.getId() + "). " + task.getText() + ". " + hour + ":" + mins);
	Toast.makeText(context, "Reminder set for " + hour + ":" + mins, Toast.LENGTH_LONG).show();
    }

    public void cancelReminder(Task task) {
	Intent intent = new Intent(context, AlarmReceiver.class);
	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

	AlarmManager alarmManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
	alarmManager.cancel(pendingIntent);
    }
}
