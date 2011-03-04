package com.pongal.seinfeld;

import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;

public class TaskActivity extends Activity {

    DBManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	TaskView taskView = new TaskView(getApplicationContext());
	initDBManager();
	Set<Task> tasks = manager.getTasks();
	Log.d(null, "Count: " + tasks.size());
	taskView.addTasks(tasks, getTaskClickHandler());
	setContentView(taskView);
    }

    private OnClickListener getTaskClickHandler() {
	return new OnClickListener() {
	    @Override
	    public void onClick(View view) {
		Task task = (Task) view.getTag();
		Intent intent = new Intent(TaskActivity.this, CalendarActivity.class);
		intent.putExtra("taskId", task.getId());
		startActivity(intent);
	    }
	};
    }
    
    private void initDBManager() {
	if (manager == null)
	    manager = new DBManager(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	manager.close();
    }

}
