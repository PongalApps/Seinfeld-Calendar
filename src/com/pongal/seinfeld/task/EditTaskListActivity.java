package com.pongal.seinfeld.task;

import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.pongal.seinfeld.R;
import com.pongal.seinfeld.ReminderTimeService;
import com.pongal.seinfeld.Util;
import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;
import com.pongal.seinfeld.homescreen.HomeScreenWidgetProvider;

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
	    @Override
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
	    @Override
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
