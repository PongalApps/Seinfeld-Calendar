package com.pongal.seinfeld.homescreen;

import java.util.ArrayList;
import java.util.List;

import com.pongal.seinfeld.R;
import com.pongal.seinfeld.data.Date;
import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class HomeScreenWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_REFRESH = "com.seinfeld.action.homeScreenRefresh";
    public static final String ACTION_SELECT = "com.seinfeld.action.homeScreenSelectDate";
    public static final String ACTION_DESELECT = "com.seinfeld.action.homeScreenDeselectDate";
    public static final String ACTION_NEXT_TASK = "com.seinfeld.action.homeScreenNextTask";
    public static final String URI_SCHEME = "seinfeldcal";

    DBManager dbManager;
    static List<Task> tasks;
    static int currentTaskIndex = 0;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	super.onUpdate(context, appWidgetManager, appWidgetIds);
	initTaskList(context);
	for (int widgetId : appWidgetIds) {
	    int taskId = tasks.get(currentTaskIndex).getId();
	    updateWidget(context, appWidgetManager, widgetId, taskId);
	}
    }

    @Override
    public void onReceive(Context context, Intent intent) {
	super.onReceive(context, intent);
	final String actionText = intent.getAction();
	Log.d("seinfeld", "onReceive: " + actionText);
	if (ACTION_REFRESH.equals(actionText)) {
	    AppWidgetManager awm = AppWidgetManager.getInstance(context);
	    ComponentName componentName = new ComponentName(context, HomeScreenWidgetProvider.class);
	    onUpdate(context, awm, awm.getAppWidgetIds(componentName));
	} else if (ACTION_SELECT.equals(actionText) || ACTION_DESELECT.equals(actionText)) {
	    final int appWidgetIds = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            Log.d("seinfeld", "Id: " + appWidgetIds);
	    initDBManager(context);
	    final int taskId = intent.getExtras().getInt("taskId");
	    dbManager.updateTaskCalendar(taskId, new Date(), ACTION_SELECT.equals(actionText));
	    getRemoteView(context);
	    dbManager.close();
	}
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, int taskId) {
	PendingIntent selectPendingIntent = getPendingIntent(context, widgetId, taskId, true);
	PendingIntent deselectPendingIntent = getPendingIntent(context, widgetId, taskId, false);
	RemoteViews views = getRemoteView(context);
	views.setOnClickPendingIntent(R.id.currentDate, selectPendingIntent);
	views.setOnClickPendingIntent(R.id.currentDateSelected, deselectPendingIntent);
	// PendingIntent nextTaskPendingIntent =
	// getNextTaskPendingIntent(context, widgetId);
	// views.setOnClickPendingIntent(R.id.nextTask, nextTaskPendingIntent);
	appWidgetManager.updateAppWidget(widgetId, views);
    }

    private PendingIntent getPendingIntent(Context context, int appWidgetId, int taskId, boolean select) {
	Intent intent = new Intent();
	intent.setAction(select ? ACTION_SELECT : ACTION_DESELECT);
	intent.putExtra("taskId", taskId);
	intent.putExtra("marked", select);
	intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId );
	Uri data = Uri.withAppendedPath(Uri.parse(URI_SCHEME + "://widget/id/"), String.valueOf(appWidgetId));
	intent.setData(data);
	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	return pendingIntent;
    }

    private RemoteViews getRemoteView(Context context) {
	Date today = new Date();
	RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.home_widget);
	view.setTextViewText(R.id.taskName, tasks.get(currentTaskIndex).getText());

	int selectedId = tasks.get(currentTaskIndex).isAccomplishedDate(today) ? R.id.currentDateSelected
		: R.id.currentDate;
	view.setTextViewText(selectedId, today.format("MMM") + "\n" + today.getDay());
	view.setViewVisibility(R.id.currentDate, R.id.currentDate == selectedId ? View.VISIBLE : View.GONE);
	view.setViewVisibility(R.id.currentDateSelected, R.id.currentDateSelected == selectedId ? View.VISIBLE
		: View.GONE);
	return view;
    }

    private void initTaskList(Context context) {
	initDBManager(context);
	tasks = new ArrayList<Task>(dbManager.getTasks());
	dbManager.close();
    }

    private void initDBManager(Context context) {
	if (dbManager == null) {
	    dbManager = new DBManager(context);
	}
    }

}
