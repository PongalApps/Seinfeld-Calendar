package com.pongal.seinfeld.task;

import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.pongal.seinfeld.CalendarActivity;
import com.pongal.seinfeld.R;
import com.pongal.seinfeld.ReminderTimeService;
import com.pongal.seinfeld.SplashScreenActivity;
import com.pongal.seinfeld.data.Constants;
import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;

public class TaskActivity extends Activity {

    DBManager dbManager;
    TaskListView taskView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	taskView = new TaskListView(getApplicationContext(), "Click the menu button to add tasks...");
	taskView.addQuestionClickListener(getQuestionIconListener());
	setContentView(taskView);
	initDBManager();
	refreshTaskList();
	
	// NOTE: To be used only when something screwed up with shared preferences or widgets
	/*deleteSharedPreferences(WidgetConfiguration.PREFS_NAME);*/
    }
    
    // NOTE: To be used only when something screwed up with shared preferences or widgets
    private void deleteSharedPreferences(String sharedPrefsName) {
	Log.d(Constants.LogTag, String.format("!Deleting shared preferences '%s'", sharedPrefsName));
	SharedPreferences sharedCfg = getApplicationContext().getSharedPreferences(sharedPrefsName, 0);
	Editor editor = sharedCfg.edit();
	editor.clear();
	editor.commit();
    }

    @Override
    protected void onResume() {
	super.onResume();
	refreshTaskList();
    }

    private void refreshTaskList() {
	Set<Task> tasks = dbManager.getTasks();
	taskView.addTasks(tasks, R.layout.task, getTaskClickHandler());
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

    public OnClickListener getQuestionIconListener() {
	return new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		Intent intent = new Intent(TaskActivity.this, SplashScreenActivity.class);
		startActivity(intent);
	    }
	};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.taskmenu, menu);	
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.addTask:
	    showDialog(EditTaskView.ADD_TASK);
	    break;
	case R.id.deleteTask:
	    startActivity(new Intent(TaskActivity.this, DeleteTaskListActivity.class));
	    break;
	case R.id.editTask:
	    startActivity(new Intent(TaskActivity.this, EditTaskListActivity.class));
	    break;
	/*case R.id.reminderTime:
	    showDialog(ReminderTimeDialog.REMINDER_TIME_DIALOG);
	    break;*/
	}
	return super.onOptionsItemSelected(item);
    }   
    
    private TaskUpdatedHandler getSaveTaskHandler(final int dialogType) {
	return new TaskUpdatedHandler() {
	    @Override
	    public void onUpdate(Task task) {
		final int taskId = dbManager.updateTask(task);
		refreshTaskList();

		final Task newTask = new Task(taskId, task.getText());
		newTask.setReminderTime(task.getReminderTime());

		ReminderTimeService rtService = new ReminderTimeService(getApplicationContext());
		if (newTask.isReminderSet()) {
		    rtService.setReminder(newTask);
		} else {
		    rtService.cancelReminder(newTask);
		}

		dismissDialog(dialogType);
	    }
	};
    }

    @Override
    protected Dialog onCreateDialog(int id) {
	switch (id) {
	case EditTaskView.ADD_TASK:
	    return new EditTaskView(TaskActivity.this);	    
	/*case ReminderTimeDialog.REMINDER_TIME_DIALOG:
	    return new ReminderTimeDialog(TaskActivity.this);*/
	}
	return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
	switch (id) {
	case EditTaskView.ADD_TASK:
	    ((EditTaskView) dialog).init(new Task(), id, getSaveTaskHandler(id));
	    break;
	}
	super.onPrepareDialog(id, dialog);
    }
    
    @Override
    protected void onDestroy() {
	super.onDestroy();
	dbManager.close();
    }

    private void initDBManager() {
	if (dbManager == null) {
	    dbManager = new DBManager(getApplicationContext());
	}
    }
}
