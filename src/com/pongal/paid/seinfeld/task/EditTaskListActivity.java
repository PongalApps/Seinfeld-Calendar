package com.pongal.paid.seinfeld.task;

import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.pongal.paid.seinfeld.ReminderTimeService;
import com.pongal.paid.seinfeld.Util;
import com.pongal.paid.seinfeld.data.Task;
import com.pongal.paid.seinfeld.db.DBManager;
import com.pongal.paid.seinfeld.homescreen.HomeScreenWidgetProvider;
import com.pongal.seinfeld.R;

public class EditTaskListActivity extends Activity {

    private TaskListView taskView;
    private Task selectedTask;
    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	initDBManager();
	taskView = new TaskListView(getApplicationContext(), "Nothing to edit...");
	setContentView(taskView);
	refreshTaskList();
    }

    private void initDBManager() {
	if (dbManager == null)
	    dbManager = new DBManager(getApplicationContext());
    }

    private void refreshTaskList() {
	Set<Task> tasks = dbManager.getTasks();
	taskView.addTasks(tasks, R.layout.taskedit, getEditTaskClickHandler());
	sendBroadcast(new Intent(HomeScreenWidgetProvider.ACTION_REFRESH));
    }

    private OnClickListener getEditTaskClickHandler() {
	return new OnClickListener() {
	    public void onClick(View taskView) {
		selectedTask = (Task) taskView.getTag();
		showDialog(EditTaskView.EDIT_TASK);
	    }
	};
    }

    @Override
    protected Dialog onCreateDialog(int id) {
	switch (id) {
	case EditTaskView.EDIT_TASK:
	    return new EditTaskView(EditTaskListActivity.this);
	}
	return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
	switch (id) {
	case EditTaskView.EDIT_TASK:
	    ((EditTaskView) dialog).init(selectedTask, id, getSaveTaskHandler(id));
	    break;
	}
	super.onPrepareDialog(id, dialog);
    }
    
    @Override
    protected void onDestroy() {
	super.onDestroy();
	dbManager.close();
    }

    private TaskUpdatedHandler getSaveTaskHandler(final int dialogType) {
	return new TaskUpdatedHandler() {
	    public void onUpdate(Task task) {
		dbManager.updateTask(task);
		refreshTaskList();
		
		ReminderTimeService rtService = new ReminderTimeService(getApplicationContext());
		if (task.isReminderSet()) {
		    rtService.setReminder(task);
		} else {
		    rtService.cancelReminder(task);
		}
		
		dismissDialog(dialogType);
		sendBroadcast(Util.getBroadcast(task, HomeScreenWidgetProvider.ACTION_REFRESH));
	    }
	};
    }
}
