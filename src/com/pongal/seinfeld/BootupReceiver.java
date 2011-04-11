package com.pongal.seinfeld;

import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;

public class BootupReceiver extends BroadcastReceiver {
    Context context;
    DBManager dbMgr;
    
    @Override
    public void onReceive(Context context, Intent intent) {
	Log.d(null, "onReceive(Boot)");
	
	ReminderTimeService reminderTimeService = new ReminderTimeService(context);
	Set<Task> tasks = dbMgr.getTasks();
	for (Task t : tasks) {
	    reminderTimeService.setReminder(t);
	}
    }
}
