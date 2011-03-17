package com.pongal.seinfeld.task;

import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.pongal.seinfeld.CalendarActivity;
import com.pongal.seinfeld.R;
import com.pongal.seinfeld.SplashScreenActivity;
import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;

public class TaskActivity extends Activity {

    DBManager manager;
    TaskListView taskView;
    Button deleteTask;
    Button addTask;
    Button editTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	taskView = new TaskListView(getApplicationContext());
	taskView.addSelectionChangedHandler(getTaskSelectionChangedHandler());
	taskView.addQuestionClickListener(getQuestionIconListener());
	setContentView(taskView);

	addTask = (Button) findViewById(R.id.addTask);
	addTask.setOnClickListener(getAddTaskClickHandler(EditTaskView.ADD_TASK));

	deleteTask = (Button) findViewById(R.id.deleteTask);
	deleteTask.setOnClickListener(getDeleteTaskClickHandler());

	editTask = (Button) findViewById(R.id.editTask);
	editTask.setOnClickListener(getAddTaskClickHandler(EditTaskView.EDIT_TASK));

	initDBManager();
	refreshTaskList();

    }

    private void refreshTaskList() {
	taskView.clear();
	Set<Task> tasks = manager.getTasks();
	taskView.addTasks(tasks, getTaskClickHandler());
	deleteTask.setEnabled(false);
	editTask.setEnabled(false);
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

    private OnClickListener getAddTaskClickHandler(final int taskType) {
	return new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		showDialog(taskType);
	    }
	};
    }

    private OnClickListener getDeleteTaskClickHandler() {
	return new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		List<Task> selections = taskView.getSelections();
		for (Task task : selections) {
		    manager.deleteTask(task);
		}
		refreshTaskList();
	    }
	};
    }

    private TaskUpdatedHandler getSaveTaskHandler(final int dialogType) {
	return new TaskUpdatedHandler() {
	    @Override
	    public void onUpdate(Task task) {
		manager.updateTask(task);
		refreshTaskList();
		dismissDialog(dialogType);
	    }
	};
    }

    private TaskSelectionChangedHandler getTaskSelectionChangedHandler() {
	return new TaskSelectionChangedHandler() {
	    public void onSelectionChange(List<Task> tasks) {
		deleteTask.setEnabled(tasks.size() > 0);
		editTask.setEnabled(tasks.size() == 1);
	    }
	};
    }

    @Override
    protected Dialog onCreateDialog(int id) {
	switch (id) {
	case EditTaskView.ADD_TASK:
	case EditTaskView.EDIT_TASK:
	    return new EditTaskView(TaskActivity.this);
	}
	return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
	switch (id) {
	case EditTaskView.ADD_TASK:
	    ((EditTaskView) dialog).init(new Task(), id, getSaveTaskHandler(id));
	    break;
	case EditTaskView.EDIT_TASK:
	    Task task = taskView.getSelections().get(0);
	    ((EditTaskView) dialog).init(task, id, getSaveTaskHandler(id));
	    break;
	}
	super.onPrepareDialog(id, dialog);
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
