package com.pongal.seinfeld.homescreen;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.pongal.seinfeld.CalendarActivity;
import com.pongal.seinfeld.R;
import com.pongal.seinfeld.data.Date;
import com.pongal.seinfeld.data.TaskSnippet;
import com.pongal.seinfeld.db.DBManager;

public class HomeScreenWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_REFRESH = "com.seinfeld.action.homeScreenRefresh";
    public static final String ACTION_SELECT = "com.seinfeld.action.homeScreenSelectDate";
    public static final String ACTION_DESELECT = "com.seinfeld.action.homeScreenDeselectDate";
    public static final String ACTION_NEXT_TASK = "com.seinfeld.action.homeScreenNextTask";
    public static final String URI_SCHEME = "seinfeldcal";

    public static final String WIDGET_ID = "widget_id";
    public static final String TASK = "task";
    public static final String TASK_ID = "task_id";
    public static final String TASK_NAME = "task_name";
    public static final String TASK_MARKED = "task_marked";

    public static final String AppNameTag = "seinfeld";

    private DBManager dbManager;

    @Override
    public void onReceive(Context context, Intent intent) {
	final String actionText = intent.getAction();
	Log.d("seinfeld", "onReceive: " + actionText);

	if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(actionText)) {
	    final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_IDS);
	    refreshWidget(context, appWidgetId);
	}
	else if (ACTION_REFRESH.equals(actionText)) {
	    int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
		    new ComponentName(context, HomeScreenWidgetProvider.class));
	    Bundle bundle = intent.getExtras();
	    int taskId = bundle.getInt(TASK_ID);
	    String taskName = bundle.getString(TASK_NAME);
	    boolean taskDone = bundle.getBoolean(TASK_MARKED);
	    for (int appWidgetId : appWidgetIds) {
		updateSharedPrefs(context, appWidgetId, taskId, taskName, taskDone);
	    }
	} else if (ACTION_SELECT.equals(actionText) || ACTION_DESELECT.equals(actionText)) {
	    final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_IDS);
	    final Bundle bundle = intent.getExtras();
	    final int taskId = bundle.getInt(TASK_ID);
	    final boolean marked = ACTION_SELECT.equals(actionText);

	    initDBManager(context);
	    dbManager.updateTaskCalendar(taskId, new Date(), marked);
	    updateSharedPrefs(context, appWidgetId, marked);
	    // TODO: Iterate all widgets and update those with task id 'taskId'
	    refreshWidget(context, appWidgetId);
	    dbManager.close();
	} else {
	    super.onReceive(context, intent);
	}
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {        
        super.onDeleted(context, appWidgetIds);
        for (int widgetId : appWidgetIds) {            
            deleteWidgetPrefs(context, widgetId);
        }
    }

    private static PendingIntent getPendingIntent(Context context, int appWidgetId, TaskSnippet taskSnip) {
	Intent intent = new Intent();
	intent.setAction(!taskSnip.doneToday ? ACTION_SELECT : ACTION_DESELECT);
	intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
	intent.putExtra(TASK_ID, taskSnip.taskId);
	intent.putExtra(TASK_NAME, taskSnip.taskName);
	intent.putExtra(TASK_MARKED, taskSnip.doneToday);
	Uri data = Uri.withAppendedPath(Uri.parse(URI_SCHEME + "://widget/id/"), String.valueOf(appWidgetId));
	intent.setData(data);
	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	return pendingIntent;
    }

    private static PendingIntent getHeaderPendingIntent(Context context, int appWidetId, TaskSnippet taskSnip) {
	Intent intent = new Intent(context, CalendarActivity.class);
	intent.putExtra("taskId", taskSnip.taskId);
	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	return pendingIntent;
    }

    private void updateSharedPrefs(Context context, int widgetId, int taskId, String taskName, boolean taskDone) {
	SharedPreferences config = context.getSharedPreferences(WidgetConfiguration.PREFS_NAME, 0);
	int widgetTaskId = config.getInt(String.format(WidgetConfiguration.TASK_ID_SHR, widgetId), -1);
	if (widgetTaskId == taskId) {
	    SharedPreferences.Editor edit = config.edit();
	    edit.putInt(String.format(WidgetConfiguration.TASK_ID_SHR, widgetId), taskId);
	    edit.putString(String.format(WidgetConfiguration.TASK_NAME_SHR, widgetId), taskName);
	    edit.putBoolean(String.format(WidgetConfiguration.TASK_DONE_SHR, widgetId), taskDone);
	    edit.commit();
	    refreshWidget(context, widgetId);
	}
    }

    private void updateSharedPrefs(Context context, int widgetId, boolean taskDone) {
	SharedPreferences config = context.getSharedPreferences(WidgetConfiguration.PREFS_NAME, 0);
	Editor editor = config.edit();
	editor.putBoolean(String.format(WidgetConfiguration.TASK_DONE_SHR, widgetId), taskDone);
	editor.commit();
    }
    
    private void deleteWidgetPrefs(Context context, int widgetId) {
	final String taskIdPrefName = String.format(WidgetConfiguration.TASK_ID_SHR, widgetId);
	final String taskNamePrefName = String.format(WidgetConfiguration.TASK_NAME_SHR, widgetId);
	final String taskDonePrefName = String.format(WidgetConfiguration.TASK_DONE_SHR, widgetId);
	SharedPreferences config = context.getSharedPreferences(WidgetConfiguration.PREFS_NAME, 0);
	Editor prefsEditor = config.edit();
	prefsEditor.remove(taskIdPrefName);
	prefsEditor.remove(taskNamePrefName);
	prefsEditor.remove(taskDonePrefName);
	prefsEditor.commit();
    }

    public static void refreshWidget(Context context, int widgetId) {
	TaskSnippet taskSnip = getFromSharedPrefs(context, widgetId);
	AppWidgetManager manager = AppWidgetManager.getInstance(context);
	PendingIntent selectPendingIntent = getPendingIntent(context, widgetId, taskSnip);
	PendingIntent deselectPendingIntent = getPendingIntent(context, widgetId, taskSnip);
	PendingIntent headerIntent = getHeaderPendingIntent(context, widgetId, taskSnip);
	RemoteViews views = getRemoteView(context, taskSnip);
	views.setOnClickPendingIntent(R.id.currentDate, selectPendingIntent);
	views.setOnClickPendingIntent(R.id.currentDateSelected, deselectPendingIntent);
	views.setOnClickPendingIntent(R.id.taskName, headerIntent);
	manager.updateAppWidget(widgetId, views);
    }

    private static TaskSnippet getFromSharedPrefs(Context context, int widgetId) {
	SharedPreferences config = context.getSharedPreferences(WidgetConfiguration.PREFS_NAME, 0);
	TaskSnippet taskSnip = new TaskSnippet();
	taskSnip.taskId = config.getInt(String.format(WidgetConfiguration.TASK_ID_SHR, widgetId), -1);
	taskSnip.taskName = config.getString(String.format(WidgetConfiguration.TASK_NAME_SHR, widgetId), "");
	taskSnip.doneToday = config.getBoolean(String.format(WidgetConfiguration.TASK_DONE_SHR, widgetId), false);
	Log.d("seinfeld", "task snip: " + taskSnip);
	return taskSnip;
    }

    private static RemoteViews getRemoteView(Context context, TaskSnippet taskSnip) {
	Date today = new Date();
	RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.home_widget);
	view.setTextViewText(R.id.taskName, taskSnip.taskName);
	// view.setTextViewText(R.id.currentMonth, today.format("MMM"));	
	final int selectedId = taskSnip.doneToday ? R.id.currentDateSelected : R.id.currentDate;	
	view.setTextViewText(selectedId, today.format("MMM").toUpperCase() + "  " + today.getDay());	
	
	Log.d("seinfeld", "CDate: " + (R.id.currentDate == selectedId));
	Log.d("seinfeld", "CDateSelected: " + (R.id.currentDateSelected == selectedId));
	
	view.setViewVisibility(R.id.currentDate, R.id.currentDate == selectedId ? View.VISIBLE : View.GONE);
	view.setViewVisibility(R.id.currentDateSelected, R.id.currentDateSelected == selectedId ? View.VISIBLE : View.GONE);
	return view;
    }

    private void initDBManager(Context context) {
	if (dbManager == null) {
	    dbManager = new DBManager(context);
	}
    }
}
