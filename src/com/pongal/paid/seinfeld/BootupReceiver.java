package com.pongal.paid.seinfeld;

import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pongal.paid.seinfeld.data.Constants;
import com.pongal.paid.seinfeld.data.Task;
import com.pongal.paid.seinfeld.db.DBManager;

public class BootupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
	ReminderTimeService reminderTimeService = new ReminderTimeService(context);
	Set<Task> tasks = getTasks(context);
	for (Task t : tasks) {
	    reminderTimeService.setReminder(t);
	    
	    final String reminderInfo = t.isReminderSet() ? " for reminder [" +  t.getReminderTime().toString() + "]" : "";
	    Log.d(Constants.LogTag, "BootupReceiver: Registering '" + t.getText() + "'" + reminderInfo);
	}

	// TODO: Have to broadcast HomeScreenWidgetProvider.ACTION_UPDATE_DATE?
	// or HomeScreenWidgetProvider.ACTION_REFRESH? so that the home widgets
	// refresh themselves	
    }

    private Set<Task> getTasks(Context context) {
	DBManager dbMgr = new DBManager(context);
	final Set<Task>tasks = dbMgr.getTasks();
	dbMgr.close();

	return tasks;
    }
}
