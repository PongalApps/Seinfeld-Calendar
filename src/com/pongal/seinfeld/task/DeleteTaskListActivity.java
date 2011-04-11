package com.pongal.seinfeld.task;

import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.pongal.seinfeld.R;
import com.pongal.seinfeld.Util;
import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;
import com.pongal.seinfeld.homescreen.HomeScreenWidgetProvider;

public class DeleteTaskListActivity extends Activity {

    private TaskListView taskView;
    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	initDBManager();
	taskView = new TaskListView(getApplicationContext(), "Nothing to delete...");
	setContentView(taskView);
	refreshTaskList();
    }
    
    @Override
    protected void onDestroy() {
	super.onDestroy();
	dbManager.close();
    }

    private OnClickListener getDeleteTaskClickHandler() {
	return new OnClickListener() {
	    @Override
	    public void onClick(View taskView) {
		Task task = (Task) taskView.getTag();
		dbManager.deleteTask(task);
		refreshTaskList();
		sendBroadcast(Util.getBroadcast(task, HomeScreenWidgetProvider.ACTION_DELETE));
	    }
	};
    }

    private void refreshTaskList() {
	Set<Task> tasks = dbManager.getTasks();
	taskView.addTasks(tasks, R.layout.taskdelete, getDeleteTaskClickHandler());
    }

    private void initDBManager() {
	if (dbManager == null)
	    dbManager = new DBManager(getApplicationContext());
    }

}
