package com.pongal.paid.seinfeld.homescreen;

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

import com.pongal.paid.seinfeld.CalendarActivity;
import com.pongal.paid.seinfeld.R;
import com.pongal.paid.seinfeld.data.Constants;
import com.pongal.paid.seinfeld.data.Date;
import com.pongal.paid.seinfeld.data.TaskSnippet;
import com.pongal.paid.seinfeld.db.DBManager;
import com.pongal.paid.seinfeld.homescreen.WidgetConfiguration.TaskSharedConfigNames;

public class HomeScreenWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_REFRESH = "com.seinfeld.action.homeScreenRefresh";
    public static final String ACTION_SELECT = "com.seinfeld.action.homeScreenSelectDate";
    public static final String ACTION_DESELECT = "com.seinfeld.action.homeScreenDeselectDate";
    public static final String ACTION_NEXT_TASK = "com.seinfeld.action.homeScreenNextTask";
    public static final String ACTION_UPDATE_DATE = "com.seinfeld.action.homeScreenUpdateDate";
    public static final String ACTION_DELETE = "com.seinfeld.action.homeScreenTaskDelete";
    public static final String ACTION_DATE_CHANGED = "android.intent.action.DATE_CHANGED";

    // public static final String URI_SCHEME = "seinfeldcal";

    public static final String WIDGET_ID = "widget_id";
    public static final String TASK = "task";
    public static final String TASK_ID = "task_id";
    public static final String TASK_NAME = "task_name";
    public static final String TASK_MARKED = "task_marked";

    public static final String AppNameTag = "seinfeld";

    private DBManager dbManager;
    private AppWidgetManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {
	final String actionText = intent.getAction();
	manager = AppWidgetManager.getInstance(context);

	if (AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(actionText)) {
	    final Bundle bundle = intent.getExtras();
	    if (bundle == null) {
		Log.d(Constants.LogTag, "onReceive(ACTION_APPWIDGET_ENABLED).....No Extras !!!");
		return;
	    }

	    int wid = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_IDS, -1);
	    Log.d(Constants.LogTag,
		    String.format("onReceive(ACTION_APPWIDGET_ENABLED, 'EXTRA_APPWIDGET_IDS').....%d", wid));

	    wid = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
	    Log.d(Constants.LogTag,
		    String.format("onReceive(ACTION_APPWIDGET_ENABLED, EXTRA_APPWIDGET_ID).....%d", wid));
	} else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(actionText)) {
	    final int[] appWidgetIds = intent.getExtras().getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
	    for (int widgetId : appWidgetIds) {
		refreshWidget(context, widgetId);
	    }
	} else if (ACTION_REFRESH.equals(actionText) || ACTION_DATE_CHANGED.equals(actionText)) {
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
	    refreshWidget(context, appWidgetId);
	    dbManager.close();
	} else if (ACTION_UPDATE_DATE.equals(actionText)) {
	    final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_IDS);
	    // Clear done flag for the next day
	    updateSharedPrefs(context, appWidgetId, false);
	    refreshWidget(context, appWidgetId);
	} else if (ACTION_DELETE.equals(actionText)) {
	    SharedPreferences config = context.getSharedPreferences(WidgetConfiguration.PREFS_NAME, 0);
	    int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
		    new ComponentName(context, HomeScreenWidgetProvider.class));
	    Bundle bundle = intent.getExtras();
	    int taskId = bundle.getInt(TASK_ID);
	    for (int widgetId : appWidgetIds) {
		int widgetTaskId = config.getInt(String.format(WidgetConfiguration.TASK_ID_SHR, widgetId), -1);
		if (widgetTaskId == taskId) {
		    RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.home_widget);
		    view.setTextViewText(R.id.taskName, "Deleted!!!");
		    view.setViewVisibility(R.id.currentDate, View.GONE);
		    view.setViewVisibility(R.id.currentDateSelected, View.GONE);
		    manager.updateAppWidget(widgetId, view);
		}
	    }
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
	intent.setData(getUriData(appWidgetId));
	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	return pendingIntent;
    }

    private static PendingIntent getHeaderPendingIntent(Context context, int appWidetId, TaskSnippet taskSnip) {
	Intent intent = new Intent(context, CalendarActivity.class);
	intent.putExtra("taskId", taskSnip.taskId);
	PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidetId, intent,
		PendingIntent.FLAG_CANCEL_CURRENT);
	return pendingIntent;
    }

    private static Uri getUriData(int widgetId) {
	Uri baseUri = Uri.parse(Constants.URI_SCHEME + "://widget/id/");
	return Uri.withAppendedPath(baseUri, String.valueOf(widgetId));
    }

    private void updateSharedPrefs(Context context, int widgetId, int taskId, String taskName, boolean taskDone) {
	SharedPreferences config = context.getSharedPreferences(WidgetConfiguration.PREFS_NAME, 0);
	WidgetConfiguration.TaskSharedConfigNames cfgNames = new WidgetConfiguration.TaskSharedConfigNames(widgetId);

	final int widgetTaskId = config.getInt(cfgNames.Id, -1);
	// int widgetTaskId =
	// config.getInt(String.format(WidgetConfiguration.TASK_ID_SHR,
	// widgetId), -1);

	if (widgetTaskId == taskId) {
	    SharedPreferences.Editor edit = config.edit();
	    // edit.putInt(String.format(WidgetConfiguration.TASK_ID_SHR,
	    // widgetId), taskId);
	    // edit.putString(String.format(WidgetConfiguration.TASK_NAME_SHR,
	    // widgetId), taskName);
	    // edit.putBoolean(String.format(WidgetConfiguration.TASK_DONE_SHR,
	    // widgetId), taskDone);

	    edit.putInt(cfgNames.Id, taskId);
	    edit.putString(cfgNames.Name, taskName);
	    edit.putBoolean(cfgNames.Done, taskDone);

	    edit.commit();
	    refreshWidget(context, widgetId);
	}
    }

    private void updateSharedPrefs(Context context, int widgetId, boolean taskDone) {
	SharedPreferences config = context.getSharedPreferences(WidgetConfiguration.PREFS_NAME, 0);
	Editor editor = config.edit();
	// editor.putBoolean(String.format(WidgetConfiguration.TASK_DONE_SHR,
	// widgetId), taskDone);
	editor.putBoolean(new WidgetConfiguration.TaskSharedConfigNames(widgetId).Done, taskDone);
	editor.commit();
    }

    private void deleteWidgetPrefs(Context context, int widgetId) {
	WidgetConfiguration.TaskSharedConfigNames cfgNames = new WidgetConfiguration.TaskSharedConfigNames(widgetId);
	SharedPreferences config = context.getSharedPreferences(WidgetConfiguration.PREFS_NAME, 0);
	Editor prefsEditor = config.edit();
	prefsEditor.remove(cfgNames.Id);
	prefsEditor.remove(cfgNames.Name);
	prefsEditor.remove(cfgNames.Done);
	prefsEditor.commit();
    }

    public static void refreshWidget(Context context, int widgetId) {
	Log.d(Constants.LogTag, String.format("Refreshing widget (ID: %d).", widgetId));

	TaskSnippet taskInfo = getFromSharedPrefs(context, widgetId);
	AppWidgetManager manager = AppWidgetManager.getInstance(context);
	PendingIntent selectPendingIntent = getPendingIntent(context, widgetId, taskInfo);
	PendingIntent deselectPendingIntent = getPendingIntent(context, widgetId, taskInfo);
	PendingIntent headerIntent = getHeaderPendingIntent(context, widgetId, taskInfo);
	RemoteViews views = getRemoteView(context, taskInfo);
	views.setOnClickPendingIntent(R.id.currentDate, selectPendingIntent);
	views.setOnClickPendingIntent(R.id.currentDateSelected, deselectPendingIntent);
	views.setOnClickPendingIntent(R.id.taskName, headerIntent);
	manager.updateAppWidget(widgetId, views);
    }

    private static TaskSnippet getFromSharedPrefs(Context context, int widgetId) {
	SharedPreferences config = context.getSharedPreferences(WidgetConfiguration.PREFS_NAME, 0);

	TaskSharedConfigNames cfgNames = new TaskSharedConfigNames(widgetId);
	final int taskId = config.getInt(cfgNames.Id, -1);

	final String taskName = config.getString(cfgNames.Name, "");
	final boolean doneToday = config.getBoolean(cfgNames.Done, false);
	TaskSnippet taskInfo = new TaskSnippet(taskId, taskName, doneToday);
	Log.d(Constants.LogTag, "Task snippet: " + taskInfo);
	Log.d(Constants.LogTag, "Task (from shared prefs): " + taskInfo.toString());
	return taskInfo;
    }

    private static RemoteViews getRemoteView(Context context, TaskSnippet taskInfo) {
	Date today = new Date();
	RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.home_widget);
	view.setTextViewText(R.id.taskName, taskInfo.taskName);

	final String dateString = today.format("MMM").toUpperCase() + "  " + today.getDay();
	Log.d(Constants.LogTag, String.format("getRemoteView: Date is %s", dateString));

	final int selectedId = taskInfo.doneToday ? R.id.currentDateSelected : R.id.currentDate;
	view.setTextViewText(selectedId, dateString);

	view.setViewVisibility(R.id.currentDate, R.id.currentDate == selectedId ? View.VISIBLE : View.GONE);
	view.setViewVisibility(R.id.currentDateSelected, R.id.currentDateSelected == selectedId ? View.VISIBLE
		: View.GONE);
	return view;
    }

    private void initDBManager(Context context) {
	if (dbManager == null) {
	    dbManager = new DBManager(context);
	}
    }
}
