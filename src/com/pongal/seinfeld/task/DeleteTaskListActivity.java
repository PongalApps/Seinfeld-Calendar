package com.pongal.seinfeld.task;

import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.pongal.seinfeld.R;
import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;

public class DeleteTaskListActivity extends Activity {

    private TaskListView taskView;
    DBManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	initDBManager();
	taskView = new TaskListView(getApplicationContext());
	setContentView(taskView);
	refreshTaskList();
    }

    private OnClickListener getDeleteTaskClickHandler() {
	return new OnClickListener() {
	    @Override
	    public void onClick(View taskView) {
		Task task = (Task) taskView.getTag();
		manager.deleteTask(task);
		refreshTaskList();
	    }
	};
    }

    private void refreshTaskList() {
	taskView.clear();
	Set<Task> tasks = manager.getTasks();
	taskView.addTasks(tasks, R.layout.taskdelete, getDeleteTaskClickHandler());
    }

    private void initDBManager() {
	if (manager == null)
	    manager = new DBManager(getApplicationContext());
    }

}
