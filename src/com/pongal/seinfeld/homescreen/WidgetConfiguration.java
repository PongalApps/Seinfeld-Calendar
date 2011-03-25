package com.pongal.seinfeld.homescreen;

import java.util.Set;

import com.pongal.seinfeld.R;
import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

public class WidgetConfiguration extends Activity {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Intent launchIntent = getIntent();
	Bundle extras = launchIntent.getExtras();
	if (extras == null) {
	    finish();
	} else {
	    appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	    setCancelIntent();
	}

	setContentView(R.layout.widget_config);
	initDBManager(getApplicationContext());
	populateTaskList();
    }

    private void populateTaskList() {
	Set<Task> tasks = dbManager.getTasks();
	LinearLayout taskLayout = (LinearLayout) findViewById(R.id.taskList);
	taskLayout.removeAllViews();
	for (Task task : tasks) {
	    TextView text = new TextView(getApplicationContext());
	    text.setText(task.getText());
	    text.setTag(task);
	    text.setOnClickListener(getTaskClickListener());
	    text.setBackgroundResource(R.layout.taskbg);
	    taskLayout.addView(text);
	}
    }

    private OnClickListener getTaskClickListener() {
	return new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		final Context context = getApplicationContext();
		final Task task = (Task) v.getTag();
				
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);		
		HomeScreenWidgetProvider.updateWidget(context, appWidgetManager, appWidgetId, task.getId(), task.getText(), task.isTodayAccomplished());
		
		Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);                
		finish();
	    }
	};
    }

    private void setCancelIntent() {
	Intent cancelResultValue = new Intent();
	cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	setResult(RESULT_CANCELED, cancelResultValue);
    }

    private void initDBManager(Context context) {
	if (dbManager == null) {
	    dbManager = new DBManager(context);
	}
    }
}
