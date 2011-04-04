package com.pongal.seinfeld.homescreen;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pongal.seinfeld.R;
import com.pongal.seinfeld.Util;
import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;

public class WidgetConfiguration extends Activity {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public static final String PREFS_NAME = "Seinfeld_Widget_Prefs";
    public static final String TASK_ID_SHR = "TaskId-%d";
    public static final String TASK_NAME_SHR = "TaskName-%d";
    public static final String TASK_DONE_SHR = "TaskDone-%d";

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
	dbManager.close();
    }

    private void populateTaskList() {
	Set<Task> tasks = dbManager.getTasks();
	LinearLayout taskLayout = (LinearLayout) findViewById(R.id.taskList);
	taskLayout.removeAllViews();
	for (Task task : tasks) {
	    if (isWidgetAlreadyAdded(task)) {
		continue;
	    }
	    TextView text = new TextView(getApplicationContext());
	    text.setText(task.getText());
	    text.setTag(task);
	    text.setOnClickListener(getTaskClickListener());
	    text.setBackgroundResource(R.layout.taskbg);
	    text.setTextColor(getResources().getColor(R.color.defaultTextColor));
	    text.setMinHeight(Util.getInDIP(50, getApplicationContext()));
	    int padding = Util.getInDIP(10, getApplicationContext());
	    int leftPadding = Util.getInDIP(20, getApplicationContext());
	    text.setPadding(leftPadding, padding, padding, padding);
	    taskLayout.addView(text);
	}
	if (taskLayout.getChildCount() == 0) {
	    TextView instr = (TextView) findViewById(R.id.widgetInstr);
	    instr.setText("No tasks available");
	}
    }

    private void updateSharedPrefs(int widgetId, Task task) {
	SharedPreferences config = getSharedPreferences(WidgetConfiguration.PREFS_NAME, 0);
	SharedPreferences.Editor edit = config.edit();
	edit.putInt(String.format(TASK_ID_SHR, widgetId), task.getId());
	edit.putString(String.format(TASK_NAME_SHR, widgetId), task.getText());
	edit.putBoolean(String.format(TASK_DONE_SHR, widgetId), task.isTodayAccomplished());
	edit.commit();
    }

    private boolean isWidgetAlreadyAdded(Task task) {
	SharedPreferences config = getSharedPreferences(WidgetConfiguration.PREFS_NAME, 0);
	Map<String, ?> sharedPrefs = config.getAll();
	for (String key : sharedPrefs.keySet()) {
	    if (key.startsWith(TASK_ID_SHR.replace("%d", ""))) {
		int taskId = config.getInt(key, -1);
		if (task.getId() == taskId)
		    return true;
	    }
	}
	return false;
    }

    private OnClickListener getTaskClickListener() {
	return new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		final Context context = getApplicationContext();
		final Task task = (Task) v.getTag();
		Log.d(HomeScreenWidgetProvider.AppNameTag, "Updating shared prefs for widget id: " + appWidgetId);
		updateSharedPrefs(appWidgetId, task);
		HomeScreenWidgetProvider.refreshWidget(context, appWidgetId);
		setDateChangeIntent();
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

    private void setDateChangeIntent() {
	Intent intent = new Intent(HomeScreenWidgetProvider.ACTION_UPDATE_DATE);
	intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
	Uri data = Uri.withAppendedPath(Uri.parse(HomeScreenWidgetProvider.URI_SCHEME + "://widget/id/"),
		String.valueOf(appWidgetId));
	intent.setData(data);
	PendingIntent datePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,
		PendingIntent.FLAG_UPDATE_CURRENT);
	
	AlarmManager alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
	alarms.cancel(datePendingIntent);
	alarms.setRepeating(AlarmManager.RTC, getTimeForMidnight(), AlarmManager.INTERVAL_DAY, datePendingIntent);
//	alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),  5000, datePendingIntent);
    }

    private long getTimeForMidnight() {
	Calendar midnight = Calendar.getInstance();
	midnight.add(Calendar.DATE, 1);
	midnight.set(Calendar.HOUR_OF_DAY, 0);
	midnight.set(Calendar.MINUTE, 0);
	midnight.set(Calendar.SECOND, 0);
	midnight.set(Calendar.MILLISECOND, 0);
	Log.d("seinfeld","Date: " + SimpleDateFormat.getInstance().format(midnight.getTime()));
	return midnight.getTimeInMillis();
    }

    private void initDBManager(Context context) {
	if (dbManager == null) {
	    dbManager = new DBManager(context);
	}
    }
}
