package com.pongal.seinfeld;

import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;

public class BootupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
	ReminderTimeService reminderTimeService = new ReminderTimeService(context);
	Set<Task> tasks = getTasks(context);
	for (Task t : tasks) {
	    Log.d(null, "BootupReceiver: Registering '" + t.getText() + "' for reminder [" + t.getReminderTime().toString() + "]");
	    reminderTimeService.setReminder(t);
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
