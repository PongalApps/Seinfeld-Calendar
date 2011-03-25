package com.pongal.seinfeld.homescreen;

import java.util.ArrayList;
import java.util.List;

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

import com.pongal.seinfeld.R;
import com.pongal.seinfeld.data.Date;
import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;

public class HomeScreenWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_REFRESH = "com.seinfeld.action.homeScreenRefresh";
    public static final String ACTION_SELECT = "com.seinfeld.action.homeScreenSelectDate";
    public static final String ACTION_DESELECT = "com.seinfeld.action.homeScreenDeselectDate";
    public static final String ACTION_NEXT_TASK = "com.seinfeld.action.homeScreenNextTask";
    public static final String URI_SCHEME = "seinfeldcal";
    public static final String ACTION_CREATE = "com.seinfeld.action.homeScreenCreate";
    
    public static final String WIDGET_ID = "widget_id";
    public static final String PREFS_NAME = "seinfeld_prefs";
    public static final String TASK = "task";
    public static final String TASK_ID = "task_id";
    public static final String TASK_NAME = "task_name";
    public static final String TASK_MARKED = "task_marked";
    
    public static final String AppNameTag = "seinfeld";

    private DBManager dbManager;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	/*initTaskList(context);
	for (int widgetId : appWidgetIds) {
	    int taskId = tasks.get(currentTaskIndex).getId();
	    updateWidget(context, appWidgetManager, widgetId, taskId);
	}*/

	Log.d("seinfeld", "onUpdate: ");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
	super.onReceive(context, intent);
	final String actionText = intent.getAction();
	Log.d("seinfeld", "onReceive: " + actionText);

	if (ACTION_CREATE.equals(actionText)) {
	    int taskId;
	    String taskName;
	    Task task = (Task) intent.getExtras().get(TASK);
	    SharedPreferences config = context.getSharedPreferences(PREFS_NAME, 0);
	    Editor editor = config.edit();
	    editor.putInt(TASK_NAME, task.getId());
	    taskId = config.getInt(TASK_ID, -1);
	    taskName = config.getString(TASK_NAME, "");
	} else if (ACTION_REFRESH.equals(actionText)) {
	    AppWidgetManager awm = AppWidgetManager.getInstance(context);
	    ComponentName componentName = new ComponentName(context, HomeScreenWidgetProvider.class);
	    onUpdate(context, awm, awm.getAppWidgetIds(componentName));
	} else if (ACTION_SELECT.equals(actionText) || ACTION_DESELECT.equals(actionText)) {
	    final int appWidgetIds = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_IDS);
	    Log.d(AppNameTag, "Id: " + appWidgetIds);
	    
	    initDBManager(context);    
	    
	    final Bundle bundle = intent.getExtras();
	    final int taskId = bundle.getInt(TASK_ID);
	    final String taskName = bundle.getString(TASK_NAME);
	    final boolean marked = ACTION_SELECT.equals(actionText);
	    
	    Log.d(AppNameTag, taskId + " : " + taskName + " : " + marked);
	    
	    dbManager.updateTaskCalendar(taskId, new Date(), marked);
	    // TODO: Iterate all widgets and update those with task id 'taskId'
	    updateWidget(context, AppWidgetManager.getInstance(context), appWidgetIds, taskId, taskName, marked);
	    dbManager.close();	    
	}
    }   

    private static PendingIntent getPendingIntent(Context context, int appWidgetId, int taskId, String taskName, boolean marked) {
	Intent intent = new Intent();
	intent.setAction(!marked ? ACTION_SELECT : ACTION_DESELECT);
	intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
	intent.putExtra(TASK_ID, taskId);
	intent.putExtra(TASK_NAME, taskName);
	intent.putExtra(TASK_MARKED, marked);
	Uri data = Uri.withAppendedPath(Uri.parse(URI_SCHEME + "://widget/id/"), String.valueOf(appWidgetId));
	intent.setData(data);
	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	return pendingIntent;
    }
    
    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, int taskId, String taskName, boolean marked) {
	PendingIntent selectPendingIntent = getPendingIntent(context, widgetId, taskId, taskName, marked);
	PendingIntent deselectPendingIntent = getPendingIntent(context, widgetId, taskId, taskName, marked);
	RemoteViews views = getRemoteView(context, taskId, taskName, marked);
	views.setOnClickPendingIntent(R.id.currentDate, selectPendingIntent);
	views.setOnClickPendingIntent(R.id.currentDateSelected, deselectPendingIntent);
	appWidgetManager.updateAppWidget(widgetId, views);
    }

    private static RemoteViews getRemoteView(Context context, int taskId, String taskName, boolean marked) {
	Date today = new Date();
	RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.home_widget);
	view.setTextViewText(R.id.taskName, taskName);
	int selectedId = marked ? R.id.currentDateSelected : R.id.currentDate;
	view.setTextViewText(selectedId, today.format("MMM") + "\n" + today.getDay());
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
